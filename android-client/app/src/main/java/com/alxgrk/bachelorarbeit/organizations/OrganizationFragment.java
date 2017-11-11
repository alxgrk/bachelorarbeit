package com.alxgrk.bachelorarbeit.organizations;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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

public class OrganizationFragment extends AbstractFragment {

    private static final String TAG = OrganizationFragment.class.getSimpleName();

    public OrganizationFragment() {
        super();
    }

    public static OrganizationFragment newInstance(String nextHref) {
        return AbstractFragment.newInstance(new OrganizationFragment(), nextHref);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, new OrganizationAsyncTask());
    }

    class OrganizationAsyncTask extends AbstractAsyncTask<Organization> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.ORGANIZATION_TYPE;
        }

        @Override
        protected ResponseEntity<Organization> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<Organization> response = template.exchange(nextHref, HttpMethod.GET,
                    requestEntity, Organization.class);
            Log.d(TAG, OrganizationAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(Organization org) {
            OrganizationUi organizationUi = new OrganizationUi(OrganizationFragment.this, org);

            container.addView(organizationUi.getUiOrg());

            for (Button button : organizationUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(OrganizationFragment.this, org.getLinks(), true);
        }
    }
}
