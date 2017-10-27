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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alxgrk.bachelorarbeit.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.AccountsFragment;
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
 * {@link OrganizationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrganizationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizationsFragment extends Fragment {

    private static final String TAG = OrganizationsFragment.class.getSimpleName();

    private static final String NEXT_HREF_ARG = "NEXT_HREF";

    private OnFragmentInteractionListener mListener;

    private LinearLayout orgsContainer;

    private ProgressBar progressBar;

    public OrganizationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nextHref
     * @return A new instance of fragment FragmentOne.
     */
    public static OrganizationsFragment newInstance(String nextHref) {
        OrganizationsFragment fragment = new OrganizationsFragment();
        Bundle args = new Bundle();
        args.putString(NEXT_HREF_ARG, nextHref);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        orgsContainer = (LinearLayout) inflater.inflate(R.layout.fragment_root, container, false);

        progressBar = container.findViewById(R.id.transition_progress);

        if (getArguments() != null) {
            progressBar.setVisibility(View.VISIBLE);

            String nextHref = getArguments().getString(NEXT_HREF_ARG);
            new OrganizationsAsyncTask(nextHref).execute();
        }

        return orgsContainer;
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
        void onFragmentInteraction(OrganizationsFragment rootFragment);
    }

    class OrganizationsAsyncTask extends AbstractAsyncTask<OrganizationCollection> {

        private final String nextHref;

        OrganizationsAsyncTask(String nextHref) {
            super(HateoasMediaType.ORGANIZATION_TYPE);

            this.nextHref = nextHref;
        }

        @Override
        protected ResponseEntity<OrganizationCollection> doRequest(
                RestTemplate template, HttpEntity<?> requestEntity) {
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
                orgsContainer.addView(orgEntry);
            }
            for (Button button : orgUi.getUiButtons()) {
                orgsContainer.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(OrganizationsFragment.this);
        }
    }
}
