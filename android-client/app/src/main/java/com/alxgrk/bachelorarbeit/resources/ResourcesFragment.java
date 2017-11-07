package com.alxgrk.bachelorarbeit.resources;

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
 * {@link ResourcesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResourcesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResourcesFragment extends AbstractFragment {

    private static final String TAG = ResourcesFragment.class.getSimpleName();

    public ResourcesFragment() {
        super();
    }

    public static ResourcesFragment newInstance(String nextHref) {
        return AbstractFragment.newInstance(new ResourcesFragment(), nextHref);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, new ResourcesAsyncTask());
    }

    class ResourcesAsyncTask extends AbstractAsyncTask<ResourceCollection> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.RESOURCE_TYPE;
        }

        @Override
        protected ResponseEntity<ResourceCollection> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<ResourceCollection> response = template.exchange(nextHref, HttpMethod.GET,
                    requestEntity, ResourceCollection.class);
            Log.d(TAG, "resourcesAsyncTask: received response " + response);
            return response;
        }

        @Override
        protected void doAfter(ResourceCollection resources) {
            Collection<ResourceUi.ResourceButton> resourcesButtons = Collections2.transform(resources.getLinks(),
                    l -> new ResourceUi.ResourceButton(l.getRel(), l.getHref()));

            ResourceUi resourceUi = ResourceUi.builder(ResourcesFragment.this)
                    .resources(resources.getMembers())
                    .buttonSpecs(resourcesButtons)
                    .build();

            for (ConstraintLayout resEntry : resourceUi.getUiResEntries()) {
                container.addView(resEntry);
            }
            for (Button button : resourceUi.getUiButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(ResourcesFragment.this, resources.getLinks(), true);
        }
    }
}
