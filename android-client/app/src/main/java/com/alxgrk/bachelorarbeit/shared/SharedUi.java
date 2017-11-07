package com.alxgrk.bachelorarbeit.shared;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

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
import lombok.Value;

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;

public class SharedUi {

    @Getter
    private final AbstractFragment fragment;

    @Getter
    private final List<Link> links;

    @Getter
    private List<Button> buttons = Lists.newArrayList();

    public SharedUi(AbstractFragment fragment, List<Link> links) {
        this.fragment = fragment;
        this.links = links;
    }

    /**
     * A shorthand function of {@link #createButtons(List, Function)} when you only want a
     * reload button to be generated from the {@link PossibleRelation#SELF} relation link.
     */
    public void createButtons() {
        List<String> expectedRels = Lists.newArrayList(SELF.toString());

        Function<SharedUi.LinkButton, Button> mappingFunctionSelf = lb -> {
            PossibleRelation relation = PossibleRelation.getBy(lb.getDisplayText());

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

        createButtons(expectedRels, mappingFunctionSelf);
    }

    public void createButtons(List<String> expectedRels, Function<LinkButton, Button> mappingFunction) {
        Collection<SharedUi.LinkButton> linkButtons = Collections2.transform(getLinks(),
                l -> new SharedUi.LinkButton(l.getRel(), l.getHref()));

        Collection<LinkButton> filtered = Collections2.filter(linkButtons,
                rb -> expectedRels.contains(rb.getDisplayText()));

        buttons = Lists.newArrayList(Collections2.transform(filtered, mappingFunction));
    }

    public Button createFollowButton(LinkButton lb, View.OnClickListener onClick) {
        return createButtonWith(lb.getDisplayText(), onClick);
    }

    public Button createButtonWith(String displayText, View.OnClickListener onClick) {
        Button button = new Button(fragment.getContext());
        button.setText(displayText);
        button.setOnClickListener(onClick);
        return button;
    }

    @Value
    public static class LinkButton {
        @NonNull
        final String displayText;

        @NonNull
        final String href;
    }

}
