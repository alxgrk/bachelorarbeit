package com.alxgrk.bachelorarbeit.organizations.collection;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.AccountFragment;
import com.alxgrk.bachelorarbeit.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.organizations.Organization;
import com.alxgrk.bachelorarbeit.organizations.OrganizationFragment;
import com.alxgrk.bachelorarbeit.shared.SharedUi;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Singular;

class OrganizationsUi {

    @Getter
    private SharedUi sharedUi;

    private Collection<Organization> organizations;

    @Getter
    private List<ConstraintLayout> uiOrgEntries = Lists.newArrayList();

    OrganizationsUi(OrganizationsFragment fragment, @Singular List<Link> links,
                    @Singular Collection<Organization> organizations) {
        this.organizations = organizations;

        sharedUi = new SharedUi(fragment, links);
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
        List<Link> selfLink = Lists.newArrayList(Collections2.filter(org.getLinks(),
                l -> PossibleRelation.SELF.toString().equalsIgnoreCase(l.getRel())));
        if (1 == selfLink.size()) {
            ConstraintLayout entryLayout = result.findViewById(R.id.entry_org);
            entryLayout.setOnClickListener(v -> {
                sharedUi.getFragment().switchTo(OrganizationFragment.newInstance(selfLink.get(0).getHref()));
            });
        }
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
