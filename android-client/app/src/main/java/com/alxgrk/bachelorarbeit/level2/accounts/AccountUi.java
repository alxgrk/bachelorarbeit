package com.alxgrk.bachelorarbeit.level2.accounts;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.accounts.Account;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.level2.SharedUi;
import com.alxgrk.bachelorarbeit.level2.resources.collection.ResourcesFragment;
import com.alxgrk.bachelorarbeit.shared.DeletionAsyncTask;
import com.google.common.base.Function;

import lombok.Getter;

import static android.content.Context.MODE_PRIVATE;

class AccountUi {

    @Getter
    private final String entryUrl;

    @Getter
    private SharedUi sharedUi;

    private Account account;

    @Getter
    private ConstraintLayout uiAccount;

    AccountUi(AccountFragment fragment, Account account) {
        SharedPreferences prefs = fragment.getContext()
                .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY_2, SettingsActivity.ENTRY_URL_DEFAULT_2);

        this.account = account;

        sharedUi = new SharedUi(fragment);
        sharedUi.createButtons();

        createAccountEntries();
    }

    private void createAccountEntries() {
        Function<Account, ConstraintLayout> accountEntryCreationFuntion = acc -> {
            ConstraintLayout result = (ConstraintLayout) sharedUi.getFragment().getLayoutInflater()
                    .inflate(R.layout.entry_account, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(acc.getName());

            TextView tvSurname = result.findViewById(R.id.tv_surname);
            tvSurname.setText(acc.getSurname());

            TextView tvUsername = result.findViewById(R.id.tv_username);
            tvUsername.setText(acc.getUsername());

            processDeleteLink(acc, result);

            processResLink(acc, result);

            return result;
        };

        uiAccount = accountEntryCreationFuntion.apply(account);
    }

    private void processDeleteLink(Account acc, ConstraintLayout result) {
        ImageView ivDelete = result.findViewById(R.id.iv_delete);
        ivDelete.setVisibility(View.VISIBLE);
        ivDelete.setOnClickListener(v -> {
            Activity fragmentActivity = sharedUi.getFragment().getActivity();
            new DeletionAsyncTask(fragmentActivity, entryUrl + "accounts/" + acc.getId()).execute();
            fragmentActivity.onBackPressed();
        });
    }

    private void processResLink(Account acc, ConstraintLayout result) {
        Button btnRes = result.findViewById(R.id.btn_resources);
        btnRes.setVisibility(View.VISIBLE);
        btnRes.setText(PossibleRelation.RESOURCES.toString());
        btnRes.setOnClickListener(v -> {
            sharedUi.getFragment().switchTo(ResourcesFragment.newInstance(entryUrl + "accounts/" + acc.getId() + "/resources"));
        });
    }

}
