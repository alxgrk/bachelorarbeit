package com.alxgrk.bachelorarbeit.organizations;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.organizations.collection.OrganizationsFragment;
import com.alxgrk.bachelorarbeit.shared.SharedUi;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.List;

import lombok.Getter;

class OrganizationUi {

    @Getter
    private SharedUi sharedUi;

    private Organization organization;

    @Getter
    private ConstraintLayout uiOrg;

    OrganizationUi(OrganizationFragment fragment, Organization organization) {
        this.organization = organization;

        sharedUi = new SharedUi(fragment, organization.getLinks());
        sharedUi.createButtons();

        createOrgEntry();
    }

    private void createOrgEntry() {
        Function<Organization, ConstraintLayout> orgEntryCreationFuntion = org -> {
            ConstraintLayout result = (ConstraintLayout) sharedUi.getFragment().getLayoutInflater()
                    .inflate(R.layout.entry_org, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(org.getName());

            processMembersLink(org, result);

            return result;
        };

        uiOrg = orgEntryCreationFuntion.apply(organization);
    }

    private void processMembersLink(Organization org, ConstraintLayout result) {
        List<Link> membersLink = Lists.newArrayList(Collections2.filter(org.getLinks(),
                l -> PossibleRelation.MEMBERS.toString().equalsIgnoreCase(l.getRel())));
        if (1 == membersLink.size()) {
            Button btnOrg = result.findViewById(R.id.btn_members);
            btnOrg.setVisibility(View.VISIBLE);
            btnOrg.setText(PossibleRelation.MEMBERS.toString());
            btnOrg.setOnClickListener(v -> {
                sharedUi.getFragment().switchTo(AccountsFragment.newInstance(membersLink.get(0).getHref()));
            });
        }
    }

}
