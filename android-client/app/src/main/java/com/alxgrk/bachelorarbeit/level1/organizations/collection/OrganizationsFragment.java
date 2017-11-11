package com.alxgrk.bachelorarbeit.level1.organizations.collection;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.organizations.Organization;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Lists;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class OrganizationsFragment extends AbstractFragment {

    private static final String TAG = OrganizationsFragment.class.getSimpleName();

    public OrganizationsFragment() {
        super();
    }

    public static OrganizationsFragment newInstance(String nextHref) {
        return AbstractFragment.newInstance(new OrganizationsFragment(), nextHref);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, new OrganizationsAsyncTask());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (null != mListener) {
            mListener.onFragmentInteraction(this, getSavedLinks(), !hidden);
        }
    }

    class OrganizationsAsyncTask extends AbstractAsyncTask<Organization[]> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.ORGANIZATION_TYPE;
        }

        @Override
        protected ResponseEntity<Organization[]> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<Organization[]> response = template.exchange(nextHref, HttpMethod.POST,
                    requestEntity, Organization[].class);
            Log.d(TAG, OrganizationsAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(Organization[] orgs) {
            OrganizationsUi organizationsUi = new OrganizationsUi(OrganizationsFragment.this, Lists.newArrayList(orgs));

            for (ConstraintLayout orgEntry : organizationsUi.getUiOrgEntries()) {
                container.addView(orgEntry);
            }
            for (Button button : organizationsUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null) {
                // create a new link as a hack to be able to use the listener callback
                Link createLink = new Link().setHref(organizationsUi.getEntryUrl() + "orgs/create")
                        .setMethod(HttpMethod.POST.toString())
                        .setRel(PossibleRelation.CREATE.toString());
                mListener.onFragmentInteraction(OrganizationsFragment.this, Lists.newArrayList(createLink), true);
            }
        }
    }
}
