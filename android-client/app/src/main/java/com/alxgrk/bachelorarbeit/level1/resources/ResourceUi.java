package com.alxgrk.bachelorarbeit.level1.resources;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.level1.SharedUi;
import com.alxgrk.bachelorarbeit.level1.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.resources.Resource;
import com.alxgrk.bachelorarbeit.shared.DeletionAsyncTask;
import com.alxgrk.bachelorarbeit.view.BookingView;
import com.google.common.base.Function;

import org.springframework.http.HttpMethod;

import lombok.Getter;

import static android.content.Context.MODE_PRIVATE;

class ResourceUi {

    @Getter
    private final String entryUrl;

    @Getter
    private SharedUi sharedUi;

    private final Resource resource;

    @Getter
    private ConstraintLayout uiResource;

    ResourceUi(ResourceFragment fragment, Resource resource) {
        SharedPreferences prefs = fragment.getContext()
                .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY_1, SettingsActivity.ENTRY_URL_DEFAULT_1);

        this.resource = resource;

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

            processDeleteLink(res, result);

            processAdminsLink(res, result);

            return result;
        };

        uiResource = resEntryCreationFuntion.apply(resource);
    }

    private void processDeleteLink(Resource res, ConstraintLayout result) {
        ImageView ivDelete = result.findViewById(R.id.iv_delete);
        ivDelete.setVisibility(View.VISIBLE);
        ivDelete.setOnClickListener(v -> {
            Activity fragmentActivity = sharedUi.getFragment().getActivity();
            new DeletionAsyncTask(fragmentActivity, entryUrl + "resources/delete?resId=" + res.getId(),
                    HttpMethod.POST).execute();
            fragmentActivity.onBackPressed();
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
