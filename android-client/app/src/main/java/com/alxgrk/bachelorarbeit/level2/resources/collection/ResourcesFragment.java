package com.alxgrk.bachelorarbeit.level2.resources.collection;

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
import com.alxgrk.bachelorarbeit.resources.Resource;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Lists;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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

    class ResourcesAsyncTask extends AbstractAsyncTask<Resource[]> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.RESOURCE_TYPE;
        }

        @Override
        protected ResponseEntity<Resource[]> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<Resource[]> response = template.exchange(nextHref, HttpMethod.GET,
                    requestEntity, Resource[].class);
            Log.d(TAG, ResourcesAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(Resource[] resources) {
            ResourcesUi resourcesUi = new ResourcesUi(ResourcesFragment.this, Lists.newArrayList(resources));

            for (ConstraintLayout resEntry : resourcesUi.getUiResEntries()) {
                container.addView(resEntry);
            }
            for (Button button : resourcesUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null) {
                // create a new link as a hack to be able to use the listener callback
                Link createLink = new Link().setHref(resourcesUi.getEntryUrl() + "resources")
                        .setMethod(HttpMethod.POST.toString())
                        .setRel(PossibleRelation.CREATE.toString());
                mListener.onFragmentInteraction(ResourcesFragment.this, Lists.newArrayList(createLink), true);
            }
        }
    }
}
