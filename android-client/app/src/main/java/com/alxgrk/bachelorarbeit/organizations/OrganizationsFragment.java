package com.alxgrk.bachelorarbeit.organizations;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Collections2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrganizationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrganizationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    class OrganizationsAsyncTask extends AbstractAsyncTask<OrganizationCollection> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.ORGANIZATION_TYPE;
        }

        @Override
        protected ResponseEntity<OrganizationCollection> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<OrganizationCollection> response = template.exchange(nextHref, HttpMethod.GET,
                    requestEntity, OrganizationCollection.class);
            Log.d(TAG, "orgsAsyncTask: received response " + response);
            return response;
        }

        @Override
        protected void doAfter(OrganizationCollection orgs) {
            Collection<OrganizationUi.OrganizationButton> orgButtons = Collections2.transform(orgs.getLinks(),
                    l -> new OrganizationUi.OrganizationButton(l.getRel(), l.getHref()));

            OrganizationUi orgUi = OrganizationUi.builder(OrganizationsFragment.this)
                    .organizations(orgs.getMembers())
                    .buttonSpecs(orgButtons)
                    .build();

            for (ConstraintLayout orgEntry : orgUi.getUiOrgEntries()) {
                container.addView(orgEntry);
            }
            for (Button button : orgUi.getUiButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(OrganizationsFragment.this, orgs.getLinks(), true);
        }
    }
}
