package com.alxgrk.bachelorarbeit.organizations;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;

class OrganizationUi {

    private final OrganizationsFragment fragment;
    private final List<OrganizationButton> buttonSpecs;
    private final List<Organization> organizations;
    @Getter
    private List<Button> uiButtons = Lists.newArrayList();
    @Getter
    private List<ConstraintLayout> uiOrgEntries = Lists.newArrayList();

    @lombok.Builder(builderClassName = "InternalBuilder", builderMethodName = "internalBuilder")
    private OrganizationUi(OrganizationsFragment fragment, @Singular List<OrganizationButton> buttonSpecs,
                           @Singular List<Organization> organizations) {
        this.fragment = fragment;
        this.buttonSpecs = buttonSpecs;
        this.organizations = organizations;
    }

    static Builder builder(OrganizationsFragment fragment) {
        return new Builder(fragment);
    }

    private void createOrgEntries() {
        Function<Organization, ConstraintLayout> orgEntryCreationFuntion = org -> {
            ConstraintLayout result = (ConstraintLayout) fragment.getLayoutInflater()
                    .inflate(R.layout.entry_org, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(org.getName());

            Collection<Link> membersLink = Collections2.filter(org.getLinks(),
                    l -> PossibleRelation.MEMBERS.toString().equalsIgnoreCase(l.getRel()));
            if (1 == membersLink.size()) {
                Button btnOrg = result.findViewById(R.id.btn_members);
                btnOrg.setVisibility(View.VISIBLE);
                btnOrg.setText(PossibleRelation.MEMBERS.toString());
            }

            return result;
        };

        uiOrgEntries = Lists.newArrayList(Collections2.transform(organizations, orgEntryCreationFuntion));
    }

    private void createButtons() {
        List<String> expectedRels = Lists.newArrayList(SELF.toString());

        Collection<OrganizationButton> filtered = Collections2.filter(buttonSpecs,
                rb -> expectedRels.contains(rb.getDisplayText()));

        Function<OrganizationButton, Button> buttonCreationFunction = rb -> {
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
                default:
                    // will never be passed due to filtering beforehand
                    return null;
            }
        };
        uiButtons = Lists.newArrayList(Collections2.transform(filtered, buttonCreationFunction));
    }

    private Button createFollowButton(OrganizationButton rb, View.OnClickListener onClick) {
        return createButtonWith(rb.getDisplayText(), onClick);
    }

    private Button createButtonWith(String displayText, View.OnClickListener onClick) {
        Button button = new Button(fragment.getContext());
        button.setText(displayText);
        button.setOnClickListener(onClick);
        return button;
    }

    static class Builder extends InternalBuilder {
        Builder(OrganizationsFragment fragment) {
            super();
            fragment(fragment);
        }

        @Override
        public OrganizationUi build() {
            OrganizationUi organizationUi = super.build();
            organizationUi.createButtons();
            organizationUi.createOrgEntries();
            return organizationUi;
        }
    }

    @Value
    static class OrganizationButton {
        @NonNull
        final String displayText;

        @NonNull
        final String href;
    }
}
