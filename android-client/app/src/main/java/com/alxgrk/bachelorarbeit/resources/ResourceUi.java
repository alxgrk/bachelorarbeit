package com.alxgrk.bachelorarbeit.resources;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.AccountsFragment;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.view.BookingView;
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

class ResourceUi {

    private final ResourcesFragment fragment;
    private final List<ResourceButton> buttonSpecs;
    private final List<Resource> resources;
    @Getter
    private List<Button> uiButtons = Lists.newArrayList();
    @Getter
    private List<ConstraintLayout> uiResEntries = Lists.newArrayList();

    @lombok.Builder(builderClassName = "InternalBuilder", builderMethodName = "internalBuilder")
    private ResourceUi(ResourcesFragment fragment, @Singular List<ResourceButton> buttonSpecs,
                       @Singular List<Resource> resources) {
        this.fragment = fragment;
        this.buttonSpecs = buttonSpecs;
        this.resources = resources;
    }

    static Builder builder(ResourcesFragment fragment) {
        return new Builder(fragment);
    }

    private void createResEntries() {
        Function<Resource, ConstraintLayout> resEntryCreationFuntion = res -> {
            ConstraintLayout result = (ConstraintLayout) fragment.getLayoutInflater()
                    .inflate(R.layout.entry_res, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(res.getName());

            if (res.getAvailableTimeslots().size() > 0 || res.getBookedTimeslots().size() > 0) {
                BookingView bookingView = result.findViewById(R.id.bv_res);
                bookingView.setVisibility(View.VISIBLE);
                bookingView.setAvailables(res.getAvailableTimeslots());
                bookingView.setBooked(res.getBookedTimeslots());
            }

            List<Link> membersLink = Lists.newArrayList(Collections2.filter(res.getLinks(),
                    l -> PossibleRelation.ADMINISTRATORS.toString().equalsIgnoreCase(l.getRel())));
            if (1 == membersLink.size()) {
                Button btnRes = result.findViewById(R.id.btn_admins);
                btnRes.setVisibility(View.VISIBLE);
                btnRes.setText(PossibleRelation.ADMINISTRATORS.toString());
                btnRes.setOnClickListener(v -> {
                    fragment.switchTo(AccountsFragment.newInstance(membersLink.get(0).getHref()));
                });
            }

            return result;
        };

        uiResEntries = Lists.newArrayList(Collections2.transform(resources, resEntryCreationFuntion));
    }

    private void createButtons() {
        List<String> expectedRels = Lists.newArrayList(SELF.toString());

        Collection<ResourceButton> filtered = Collections2.filter(buttonSpecs,
                rb -> expectedRels.contains(rb.getDisplayText()));

        Function<ResourceButton, Button> buttonCreationFunction = rb -> {
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

    private Button createFollowButton(ResourceButton rb, View.OnClickListener onClick) {
        return createButtonWith(rb.getDisplayText(), onClick);
    }

    private Button createButtonWith(String displayText, View.OnClickListener onClick) {
        Button button = new Button(fragment.getContext());
        button.setText(displayText);
        button.setOnClickListener(onClick);
        return button;
    }

    static class Builder extends InternalBuilder {
        Builder(ResourcesFragment fragment) {
            super();
            fragment(fragment);
        }

        @Override
        public ResourceUi build() {
            ResourceUi resourceUi = super.build();
            resourceUi.createButtons();
            resourceUi.createResEntries();
            return resourceUi;
        }
    }

    @Value
    static class ResourceButton {
        @NonNull
        final String displayText;

        @NonNull
        final String href;
    }
}
