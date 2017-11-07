package com.alxgrk.bachelorarbeit.resources.collection;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Lists;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (null != mListener) {
            mListener.onFragmentInteraction(this, getSavedLinks(), !hidden);
        }
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
            Log.d(TAG, ResourcesAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(ResourceCollection resources) {
            setSavedLinks(resources.getLinks());
            ResourcesUi resourcesUi = new ResourcesUi(ResourcesFragment.this, getSavedLinks(),
                    resources.getMembers());

            for (ConstraintLayout resEntry : resourcesUi.getUiResEntries()) {
                container.addView(resEntry);
            }
            for (Button button : resourcesUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(ResourcesFragment.this, getSavedLinks(), true);
        }
    }
}
