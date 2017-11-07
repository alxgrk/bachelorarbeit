package com.alxgrk.bachelorarbeit.resources;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.shared.SharedUi;
import com.alxgrk.bachelorarbeit.view.BookingView;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

class ResourceUi {

    @Getter
    private SharedUi sharedUi;

    private final Resource resource;

    @Getter
    private ConstraintLayout uiResource;

    ResourceUi(ResourceFragment fragment, Resource resource) {
        this.resource = resource;

        sharedUi = new SharedUi(fragment, resource.getLinks());
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

            processAdminsLink(res, result);

            return result;
        };

        uiResource = resEntryCreationFuntion.apply(resource);
    }

    private void processAdminsLink(Resource res, ConstraintLayout result) {
        List<Link> administratorsLink = Lists.newArrayList(Collections2.filter(res.getLinks(),
                l -> PossibleRelation.ADMINISTRATORS.toString().equalsIgnoreCase(l.getRel())));
        if (1 == administratorsLink.size()) {
            Button btnRes = result.findViewById(R.id.btn_admins);
            btnRes.setVisibility(View.VISIBLE);
            btnRes.setText(PossibleRelation.ADMINISTRATORS.toString());
            btnRes.setOnClickListener(v -> {
                getSharedUi().getFragment().switchTo(AccountsFragment.newInstance(administratorsLink.get(0).getHref()));
            });
        }
    }

}
