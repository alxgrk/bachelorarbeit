package com.alxgrk.bachelorarbeit.level1;

import android.view.View;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Lists;

import java.util.List;

import lombok.Getter;

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
