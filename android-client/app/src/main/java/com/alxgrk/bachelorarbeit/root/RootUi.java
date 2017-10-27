package com.alxgrk.bachelorarbeit.root;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.MainActivity;
import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.AccountsFragment;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.organizations.OrganizationsFragment;
import com.alxgrk.bachelorarbeit.resources.ResourcesFragment;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ACCOUNTS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ORGANIZATIONS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.RESOURCES;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;

public class RootUi {

    private final RootFragment fragment;
    private final List<RootButton> buttonSpecs;
    @Getter
    private List<Button> uiButtons = Lists.newArrayList();

    @lombok.Builder(builderClassName = "InternalBuilder", builderMethodName = "internalBuilder")
    private RootUi(RootFragment fragment, @Singular List<RootButton> buttonSpecs) {
        this.fragment = fragment;
        this.buttonSpecs = buttonSpecs;
    }

    static Builder builder(RootFragment fragment) {
        return new Builder(fragment);
    }

    private void createButtons() {
        List<String> expectedRels = Lists.newArrayList(SELF.toString(),
                ACCOUNTS.toString(),
                ORGANIZATIONS.toString(),
                RESOURCES.toString());

        Collection<RootButton> filtered = Collections2.filter(buttonSpecs,
                rb -> expectedRels.contains(rb.getDisplayText()));

        Function<RootButton, Button> buttonCreationFunction = rb -> {
            PossibleRelation relation = PossibleRelation.getBy(rb.getDisplayText());

            switch (relation) {
                case SELF:
                    return createButtonWith("Reload", view -> {
                        FragmentManager fragmentManager = fragment.getFragmentManager();
                        fragmentManager.beginTransaction()
                                .remove(fragment)
                                .commitNowAllowingStateLoss();
                        fragmentManager.beginTransaction()
                                .add(R.id.main_fragment_layout, fragment)
                                .commit();
                    });
                case ACCOUNTS:
                    return createFollowButton(rb, view -> {
                        Log.d(MainActivity.TAG, "clicked on accounts button");
                        fragment.switchTo(AccountsFragment.newInstance(rb.getHref()));
                    });
                case ORGANIZATIONS:
                    return createFollowButton(rb, view -> {
                        Log.d(MainActivity.TAG, "clicked on orgs button");
                        fragment.switchTo(OrganizationsFragment.newInstance(rb.getHref()));
                    });
                case RESOURCES:
                    return createFollowButton(rb, view -> {
                        Log.d(MainActivity.TAG, "clicked on resources button");
                        fragment.switchTo(ResourcesFragment.newInstance(rb.getHref()));
                    });
                default:
                    // will never be passed due to filtering beforehand
                    return null;
            }
        };
        uiButtons = Lists.newArrayList(Collections2.transform(filtered, buttonCreationFunction));
    }

    private Button createFollowButton(RootButton rb, View.OnClickListener onClick) {
        return createButtonWith(rb.getDisplayText(), onClick);
    }

    private Button createButtonWith(String displayText, View.OnClickListener onClick) {
        Button button = new Button(fragment.getContext());
        button.setText(displayText);
        button.setOnClickListener(onClick);
        return button;
    }

    static class Builder extends InternalBuilder {
        Builder(RootFragment fragment) {
            super();
            fragment(fragment);
        }

        @Override
        public RootUi build() {
            RootUi rootUi = super.build();
            rootUi.createButtons();
            return rootUi;
        }
    }

    @Value
    static class RootButton {
        @NonNull
        final String displayText;

        @NonNull
        final String href;
    }
}
