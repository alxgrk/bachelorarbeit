package com.alxgrk.bachelorarbeit.resources.collection;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.AccountFragment;
import com.alxgrk.bachelorarbeit.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.resources.Resource;
import com.alxgrk.bachelorarbeit.resources.ResourceFragment;
import com.alxgrk.bachelorarbeit.shared.SharedUi;
import com.alxgrk.bachelorarbeit.view.BookingView;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Singular;

class ResourcesUi {

    @Getter
    private SharedUi sharedUi;

    private final Collection<Resource> resources;

    @Getter
    private List<ConstraintLayout> uiResEntries = Lists.newArrayList();

    ResourcesUi(ResourcesFragment fragment, List<Link> links, Collection<Resource> resources) {
        this.resources = resources;

        sharedUi = new SharedUi(fragment, links);
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

            List<Link> selfLink = Lists.newArrayList(Collections2.filter(res.getLinks(),
                    l -> PossibleRelation.SELF.toString().equalsIgnoreCase(l.getRel())));
            if (1 == selfLink.size()) {
                ConstraintLayout entryLayout = result.findViewById(R.id.entry_res);
                entryLayout.setOnClickListener(v -> {
                    sharedUi.getFragment().switchTo(ResourceFragment.newInstance(selfLink.get(0).getHref()));
                });
            }

            List<Link> membersLink = Lists.newArrayList(Collections2.filter(res.getLinks(),
                    l -> PossibleRelation.ADMINISTRATORS.toString().equalsIgnoreCase(l.getRel())));
            if (1 == membersLink.size()) {
                Button btnRes = result.findViewById(R.id.btn_admins);
                btnRes.setVisibility(View.VISIBLE);
                btnRes.setText(PossibleRelation.ADMINISTRATORS.toString());
                btnRes.setOnClickListener(v -> {
                    getSharedUi().getFragment().switchTo(AccountsFragment.newInstance(membersLink.get(0).getHref()));
                });
            }

            return result;
        };

        uiResEntries = Lists.newArrayList(Collections2.transform(resources, resEntryCreationFuntion));
    }

}
