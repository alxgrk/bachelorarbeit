package com.alxgrk.bachelorarbeit.level1.resources.collection;

import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.level1.SharedUi;
import com.alxgrk.bachelorarbeit.level1.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.level1.resources.ResourceFragment;
import com.alxgrk.bachelorarbeit.resources.Resource;
import com.alxgrk.bachelorarbeit.view.BookingView;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

import static android.content.Context.MODE_PRIVATE;

class ResourcesUi {

    @Getter
    private final String entryUrl;

    @Getter
    private SharedUi sharedUi;

    private final Collection<Resource> resources;

    @Getter
    private List<ConstraintLayout> uiResEntries = Lists.newArrayList();

    ResourcesUi(ResourcesFragment fragment, Collection<Resource> resources) {
        SharedPreferences prefs = fragment.getContext()
                .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY_1, SettingsActivity.ENTRY_URL_DEFAULT_1);

        this.resources = resources;

        sharedUi = new SharedUi(fragment);
        sharedUi.createButtons();

        createResEntries();
    }

    private void createResEntries() {
        Function<Resource, ConstraintLayout> resEntryCreationFuntion = res -> {
            ConstraintLayout result = (ConstraintLayout) getSharedUi().getFragment().getLayoutInflater()
                    .inflate(R.layout.entry_res, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(res.getName());

            if (res.getAvailableTimeslots().size() > 0 || res.getBookedTimeslots().size() > 0) {
                BookingView bookingView = result.findViewById(R.id.bv_res);
                bookingView.setVisibility(View.VISIBLE);
                bookingView.setAvailables(res.getAvailableTimeslots());
                bookingView.setBooked(res.getBookedTimeslots());
            }

            processSelfLink(res, result);

            processAdminsLink(res, result);

            return result;
        };

        uiResEntries = Lists.newArrayList(Collections2.transform(resources, resEntryCreationFuntion));
    }

    private void processSelfLink(Resource res, ConstraintLayout result) {
        ConstraintLayout entryLayout = result.findViewById(R.id.entry_res);
        entryLayout.setOnClickListener(v -> {
            sharedUi.getFragment().switchTo(ResourceFragment.newInstance(entryUrl + "resources/get-one?resId=" + res.getId()));
        });
    }

    private void processAdminsLink(Resource res, ConstraintLayout result) {
        Button btnRes = result.findViewById(R.id.btn_admins);
        btnRes.setVisibility(View.VISIBLE);
        btnRes.setText(PossibleRelation.ADMINISTRATORS.toString());
        btnRes.setOnClickListener(v -> {
            getSharedUi().getFragment().switchTo(AccountsFragment.newInstance(entryUrl + "resources/get-administrators-of-resource?resId=" + res.getId()));
        });
    }

}
