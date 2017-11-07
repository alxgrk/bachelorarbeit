package com.alxgrk.bachelorarbeit.root;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.MainActivity;
import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.organizations.collection.OrganizationsFragment;
import com.alxgrk.bachelorarbeit.resources.collection.ResourcesFragment;
import com.alxgrk.bachelorarbeit.shared.SharedUi;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

import lombok.Getter;

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ACCOUNTS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ORGANIZATIONS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.RESOURCES;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;

class RootUi {

    @Getter
    private SharedUi ui;

    RootUi(RootFragment fragment, List<Link> links) {
        ui = new SharedUi(fragment, links);
        ui.createButtons(expectedRels, mappingFunction);
    }

    private List<String> expectedRels = Lists.newArrayList(SELF.toString(),
            ACCOUNTS.toString(),
            ORGANIZATIONS.toString(),
            RESOURCES.toString());

    private Function<SharedUi.LinkButton, Button> mappingFunction = lb -> {
        PossibleRelation relation = PossibleRelation.getBy(lb.getDisplayText());

        switch (relation) {
            case SELF:
                return ui.createButtonWith("Reload", view -> {
                    FragmentManager fragmentManager = ui.getFragment().getFragmentManager();
                    fragmentManager.beginTransaction()
                            .remove(ui.getFragment())
                            .commitNowAllowingStateLoss();
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment_layout, ui.getFragment())
                            .commit();
                });
            case ACCOUNTS:
                return ui.createFollowButton(lb, view -> {
                    Log.d(MainActivity.TAG, "clicked on accounts button");
                    ui.getFragment().switchTo(AccountsFragment.newInstance(lb.getHref()));
                });
            case ORGANIZATIONS:
                return ui.createFollowButton(lb, view -> {
                    Log.d(MainActivity.TAG, "clicked on orgs button");
                    ui.getFragment().switchTo(OrganizationsFragment.newInstance(lb.getHref()));
                });
            case RESOURCES:
                return ui.createFollowButton(lb, view -> {
                    Log.d(MainActivity.TAG, "clicked on resources button");
                    ui.getFragment().switchTo(ResourcesFragment.newInstance(lb.getHref()));
                });
            default:
                // will never be passed due to filtering beforehand
                return null;
        }
    };

}
