package com.alxgrk.bachelorarbeit.level1.accounts.collection;

import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.accounts.Account;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.level1.SharedUi;
import com.alxgrk.bachelorarbeit.level1.accounts.AccountFragment;
import com.alxgrk.bachelorarbeit.level1.resources.collection.ResourcesFragment;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

import static android.content.Context.MODE_PRIVATE;

class AccountsUi {

    @Getter
    private final String entryUrl;

    @Getter
    private SharedUi sharedUi;

    private Collection<Account> accounts;

    @Getter
    private List<ConstraintLayout> uiAccountEntries = Lists.newArrayList();

    AccountsUi(AccountsFragment fragment, Collection<Account> accounts) {
        SharedPreferences prefs = fragment.getContext()
                .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY_1, SettingsActivity.ENTRY_URL_DEFAULT_1);

        this.accounts = accounts;

        sharedUi = new SharedUi(fragment);
        sharedUi.createButtons();

        createAccountEntries();
    }

    private void createAccountEntries() {
        Function<Account, ConstraintLayout> accountEntryCreationFunction = acc -> {
            ConstraintLayout result = (ConstraintLayout) sharedUi.getFragment().getLayoutInflater()
                    .inflate(R.layout.entry_account, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(acc.getName());

            TextView tvSurname = result.findViewById(R.id.tv_surname);
            tvSurname.setText(acc.getSurname());

            TextView tvUsername = result.findViewById(R.id.tv_username);
            tvUsername.setText(acc.getUsername());

            processSelfLink(acc, result);

            processResLink(acc, result);

            return result;
        };

        uiAccountEntries = Lists.newArrayList(Collections2.transform(accounts, accountEntryCreationFunction));
    }

    private void processSelfLink(Account acc, ConstraintLayout result) {
        ConstraintLayout entryLayout = result.findViewById(R.id.entry_account);
        entryLayout.setOnClickListener(v -> {
            sharedUi.getFragment().switchTo(AccountFragment.newInstance(entryUrl + "accounts/get-one?accountId=" + acc.getId()));
        });
    }

    private void processResLink(Account acc, ConstraintLayout result) {
        Button btnRes = result.findViewById(R.id.btn_resources);
        btnRes.setVisibility(View.VISIBLE);
        btnRes.setText(PossibleRelation.RESOURCES.toString());
        btnRes.setOnClickListener(v -> {
            sharedUi.getFragment().switchTo(ResourcesFragment.newInstance(entryUrl + "accounts/get-resources-of-account?accountId=" + acc.getId()));
        });
    }

}
