package com.alxgrk.bachelorarbeit.root;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ACCOUNTS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ORGANIZATION;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.RESOURCES;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;

public class RootUi {

    @Getter
    private List<Button> uiButtons = Lists.newArrayList();

    private final Fragment fragment;

    private final List<RootButton> buttonSpecs;

    @lombok.Builder(builderClassName = "InternalBuilder", builderMethodName = "internalBuilder")
    private RootUi(Fragment fragment, @Singular List<RootButton> buttonSpecs) {
        this.fragment = fragment;
        this.buttonSpecs = buttonSpecs;
    }

    public static Builder builder(Fragment fragment) {
        return new Builder(fragment);
    }

    public static class Builder extends InternalBuilder {
        Builder(Fragment fragment) {
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
    public static class RootButton {
        @NonNull
        final String displayText;

        @NonNull
        final String href;
    }

    private void createButtons() {
        List<String> expectedRels = Lists.newArrayList(SELF.toString(),
                ACCOUNTS.toString(),
                ORGANIZATION.toString(),
                RESOURCES.toString());

        uiButtons = buttonSpecs.stream()
                .filter(rb -> expectedRels.contains(rb.getDisplayText()))
                .map(rb -> {
                    PossibleRelation relation = PossibleRelation.valueOf(rb.getDisplayText());

                    switch (relation) {
                        case SELF:
                            return createButtonWith("Reload", view -> { // TODO
                                });
                        case ACCOUNTS:
                            return createFollowButton(rb, view -> {});
                        case ORGANIZATIONS:
                            return createFollowButton(rb, view -> {});
                        case RESOURCES:
                            return createFollowButton(rb, view -> {});
                        default:
                            // will never be passed due to filtering beforehand
                            return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private <T extends AbstractAsyncTask> Button createFollowButton(RootButton rb, View.OnClickListener onClick) {
        return createButtonWith(rb.getDisplayText(), onClick);
    }

    @android.support.annotation.NonNull
    private <T extends AbstractAsyncTask> Button createButtonWith(String displayText, View.OnClickListener onClick) {
        Button button = new Button(fragment.getContext());
        button.setText(displayText);
        button.setOnClickListener(onClick);
        return button;
    }

}
