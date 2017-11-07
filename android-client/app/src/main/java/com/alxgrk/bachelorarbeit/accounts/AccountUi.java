package com.alxgrk.bachelorarbeit.accounts;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.organizations.OrganizationFragment;
import com.alxgrk.bachelorarbeit.resources.collection.ResourcesFragment;
import com.alxgrk.bachelorarbeit.shared.SharedUi;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.List;

import lombok.Getter;

class AccountUi {

    @Getter
    private SharedUi sharedUi;

    private Account account;

    @Getter
    private ConstraintLayout uiAccount;

    AccountUi(AccountFragment fragment, Account account) {
        this.account = account;

        sharedUi = new SharedUi(fragment, account.getLinks());
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

            processOrgLink(acc, result);

            processResLink(acc, result);

            return result;
        };

        uiAccount = accountEntryCreationFuntion.apply(account);
    }

    private void processOrgLink(Account acc, ConstraintLayout result) {
        List<Link> orgLink = Lists.newArrayList(Collections2.filter(acc.getLinks(),
                l -> PossibleRelation.ORGANIZATION.toString().equalsIgnoreCase(l.getRel())));
        if (1 == orgLink.size()) {
            Button btnOrg = result.findViewById(R.id.btn_org);
            btnOrg.setVisibility(View.VISIBLE);
            btnOrg.setText(PossibleRelation.ORGANIZATION.toString());
            btnOrg.setOnClickListener(v -> {
                sharedUi.getFragment().switchTo(OrganizationFragment.newInstance(orgLink.get(0).getHref()));
            });
        }
    }

    private void processResLink(Account acc, ConstraintLayout result) {
        List<Link> resourcesLink = Lists.newArrayList(Collections2.filter(acc.getLinks(),
                l -> PossibleRelation.RESOURCES.toString().equalsIgnoreCase(l.getRel())));
        if (1 == resourcesLink.size()) {
            Button btnRes = result.findViewById(R.id.btn_resources);
            btnRes.setVisibility(View.VISIBLE);
            btnRes.setText(PossibleRelation.RESOURCES.toString());
            btnRes.setOnClickListener(v -> {
                sharedUi.getFragment().switchTo(ResourcesFragment.newInstance(resourcesLink.get(0).getHref()));
            });
        }
    }

}
