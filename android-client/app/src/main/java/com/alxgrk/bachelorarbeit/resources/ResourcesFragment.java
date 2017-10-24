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
import android.widget.LinearLayout;

import com.alxgrk.bachelorarbeit.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.google.common.collect.Collections2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
public class ResourcesFragment extends Fragment {

    private static final String TAG = ResourcesFragment.class.getSimpleName();

    private static final String NEXT_HREF_ARG = "NEXT_HREF";

    private OnFragmentInteractionListener mListener;

    private LinearLayout resourcesContainer;

    public ResourcesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentOne.
     * @param nextHref
     */
    public static ResourcesFragment newInstance(String nextHref) {
        ResourcesFragment fragment = new ResourcesFragment();
        Bundle args = new Bundle();
        args.putString(NEXT_HREF_ARG, nextHref);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String nextHref = getArguments().getString(NEXT_HREF_ARG);
            new ResourcesAsyncTask(nextHref).execute();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        resourcesContainer = (LinearLayout) inflater.inflate(R.layout.fragment_root, container, false);
        return resourcesContainer;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ResourcesFragment resourcesFragment);
    }

    class ResourcesAsyncTask extends AbstractAsyncTask<ResourceCollection> {

        private final String nextHref;

        ResourcesAsyncTask(String nextHref) {
            super(HateoasMediaType.RESOURCE_TYPE);

            this.nextHref = nextHref;
        }

        @Override
        protected ResponseEntity<ResourceCollection> doRequest(
                RestTemplate template, HttpEntity<?> requestEntity) {
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
                resourcesContainer.addView(resEntry);
            }
            for (Button button : resourceUi.getUiButtons()) {
                resourcesContainer.addView(button);
            }

            if (mListener != null)
                mListener.onFragmentInteraction(ResourcesFragment.this);
        }
    }
}
