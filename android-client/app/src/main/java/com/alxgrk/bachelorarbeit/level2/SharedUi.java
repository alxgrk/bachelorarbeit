package com.alxgrk.bachelorarbeit.level2;

import android.view.View;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;
import static com.alxgrk.bachelorarbeit.shared.AbstractFragment.reload;

public class SharedUi {

    @Getter
    private final AbstractFragment fragment;

    @Getter
    private List<Button> buttons = Lists.newArrayList();

    public SharedUi(AbstractFragment fragment) {
        this.fragment = fragment;
    }

    public void createButtons() {
        buttons =  Lists.newArrayList(createButtonWith("Reload", view -> reload(fragment)));
    }

    public Button createButtonWith(String displayText, View.OnClickListener onClick) {
        Button button = new Button(fragment.getContext());
        button.setText(displayText);
        button.setOnClickListener(onClick);
        return button;
    }

}
