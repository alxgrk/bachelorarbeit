package com.alxgrk.bachelorarbeit.organizations.collection;

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
            Log.d(TAG, OrganizationsAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(OrganizationCollection orgs) {
            OrganizationsUi organizationsUi = new OrganizationsUi(OrganizationsFragment.this, orgs.getLinks(),
                    orgs.getMembers());

            for (ConstraintLayout orgEntry : organizationsUi.getUiOrgEntries()) {
                container.addView(orgEntry);
            }
            for (Button button : organizationsUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(OrganizationsFragment.this, orgs.getLinks(), true);
        }
    }
}
