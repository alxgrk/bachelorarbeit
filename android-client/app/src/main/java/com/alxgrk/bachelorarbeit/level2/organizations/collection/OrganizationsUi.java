package com.alxgrk.bachelorarbeit.level2.organizations.collection;

import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.level2.SharedUi;
import com.alxgrk.bachelorarbeit.level2.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.level2.organizations.OrganizationFragment;
import com.alxgrk.bachelorarbeit.organizations.Organization;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

import static android.content.Context.MODE_PRIVATE;

class OrganizationsUi {

    @Getter
    private final String entryUrl;

    @Getter
    private SharedUi sharedUi;

    private Collection<Organization> organizations;

    @Getter
    private List<ConstraintLayout> uiOrgEntries = Lists.newArrayList();

    OrganizationsUi(OrganizationsFragment fragment, Collection<Organization> organizations) {
        SharedPreferences prefs = fragment.getContext()
                .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY_2, SettingsActivity.ENTRY_URL_DEFAULT_2);

        this.organizations = organizations;

        sharedUi = new SharedUi(fragment);
        sharedUi.createButtons();

        createOrgEntries();
    }

    private void createOrgEntries() {
        Function<Organization, ConstraintLayout> orgEntryCreationFuntion = org -> {
            ConstraintLayout result = (ConstraintLayout) sharedUi.getFragment().getLayoutInflater()
                    .inflate(R.layout.entry_org, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(org.getName());

            processSelfLink(org, result);

            processMembersLink(org, result);

            return result;
        };

        uiOrgEntries = Lists.newArrayList(Collections2.transform(organizations, orgEntryCreationFuntion));
    }

    private void processSelfLink(Organization org, ConstraintLayout result) {
        ConstraintLayout entryLayout = result.findViewById(R.id.entry_org);
        entryLayout.setOnClickListener(v -> {
            sharedUi.getFragment().switchTo(OrganizationFragment.newInstance(entryUrl + "orgs/" + org.getId()));
        });
    }

    private void processMembersLink(Organization org, ConstraintLayout result) {
        Button btnOrg = result.findViewById(R.id.btn_members);
        btnOrg.setVisibility(View.VISIBLE);
        btnOrg.setText(PossibleRelation.MEMBERS.toString());
        btnOrg.setOnClickListener(v -> {
            sharedUi.getFragment().switchTo(AccountsFragment.newInstance(entryUrl + "orgs/" + org.getId() + "/accounts"));
        });
    }

}
