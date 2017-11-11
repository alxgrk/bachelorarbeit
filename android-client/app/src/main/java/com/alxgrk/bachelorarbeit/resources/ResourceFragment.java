package com.alxgrk.bachelorarbeit.resources;

import android.os.Bundle;
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

public class ResourceFragment extends AbstractFragment {

    private static final String TAG = ResourceFragment.class.getSimpleName();

    public ResourceFragment() {
        super();
    }

    public static ResourceFragment newInstance(String nextHref) {
        return AbstractFragment.newInstance(new ResourceFragment(), nextHref);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, new ResourceAsyncTask());
    }

    class ResourceAsyncTask extends AbstractAsyncTask<Resource> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.RESOURCE_TYPE;
        }

        @Override
        protected ResponseEntity<Resource> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<Resource> response = template.exchange(nextHref, HttpMethod.GET,
                    requestEntity, Resource.class);
            Log.d(TAG, ResourceAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(Resource resource) {
            ResourceUi resourceUi = new ResourceUi(ResourceFragment.this, resource);

            container.addView(resourceUi.getUiResource());

            for (Button button : resourceUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(ResourceFragment.this, resource.getLinks(), true);
        }
    }
}
