package com.alxgrk.bachelorarbeit.root;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.MainActivity;
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

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ACCOUNTS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.ORGANIZATIONS;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.RESOURCES;
import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;

public class RootUi {

    @Getter
    private List<Button> uiButtons = Lists.newArrayList();

    private final RootFragment fragment;

    private final List<RootButton> buttonSpecs;

    @lombok.Builder(builderClassName = "InternalBuilder", builderMethodName = "internalBuilder")
    private RootUi(RootFragment fragment, @Singular List<RootButton> buttonSpecs) {
        this.fragment = fragment;
        this.buttonSpecs = buttonSpecs;
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
                    return createButtonWith("Reload", view -> { // TODO
                    });
                case ACCOUNTS:
                    return createFollowButton(rb, view -> {
                        Log.d(MainActivity.TAG, "clicked on accounts button");
                        fragment.switchToAccountsFragment(rb.getHref());
                    });
                case ORGANIZATIONS:
                    return createFollowButton(rb, view -> {
                        Log.d(MainActivity.TAG, "clicked on orgs button");
                    });
                case RESOURCES:
                    return createFollowButton(rb, view -> {
                        Log.d(MainActivity.TAG, "clicked on resources button");
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

    public static Builder builder(RootFragment fragment) {
        return new Builder(fragment);
    }

    public static class Builder extends InternalBuilder {
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
    public static class RootButton {
        @NonNull
        final String displayText;

        @NonNull
        final String href;
    }
}
