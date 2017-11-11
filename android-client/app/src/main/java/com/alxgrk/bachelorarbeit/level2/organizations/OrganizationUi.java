package com.alxgrk.bachelorarbeit.level2.organizations;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.level2.SharedUi;
import com.alxgrk.bachelorarbeit.level2.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.organizations.Organization;
import com.alxgrk.bachelorarbeit.shared.DeletionAsyncTask;
import com.google.common.base.Function;

import lombok.Getter;

import static android.content.Context.MODE_PRIVATE;

class OrganizationUi {

    @Getter
    private final String entryUrl;

    @Getter
    private final SharedUi sharedUi;

    private Organization organization;

    @Getter
    private ConstraintLayout uiOrg;

    OrganizationUi(OrganizationFragment fragment, Organization organization) {
        SharedPreferences prefs = fragment.getContext()
                .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY_2, SettingsActivity.ENTRY_URL_DEFAULT_2);

        this.organization = organization;

        sharedUi = new SharedUi(fragment);
        sharedUi.createButtons();

        createOrgEntry();
    }

    private void createOrgEntry() {
        Function<Organization, ConstraintLayout> orgEntryCreationFuntion = org -> {
            ConstraintLayout result = (ConstraintLayout) sharedUi.getFragment().getLayoutInflater()
                    .inflate(R.layout.entry_org, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(org.getName());

            processDeleteLink(org, result);

            processMembersLink(org, result);

            return result;
        };

        uiOrg = orgEntryCreationFuntion.apply(organization);
    }

    private void processDeleteLink(Organization org, ConstraintLayout result) {
        ImageView ivDelete = result.findViewById(R.id.iv_delete);
        ivDelete.setVisibility(View.VISIBLE);
        ivDelete.setOnClickListener(v -> {
            Activity fragmentActivity = sharedUi.getFragment().getActivity();
            new DeletionAsyncTask(fragmentActivity, entryUrl + "orgs/" + org.getId()).execute();
            fragmentActivity.onBackPressed();
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
