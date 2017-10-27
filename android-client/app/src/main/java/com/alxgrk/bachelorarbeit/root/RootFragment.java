package com.alxgrk.bachelorarbeit.root;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alxgrk.bachelorarbeit.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.google.common.collect.Collections2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RootFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RootFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RootFragment extends Fragment {

    private static final String TAG = RootFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private LinearLayout rootContainer;

    private ProgressBar progressBar;

    public RootFragment() {
        // Required empty public constructor
    }

    public static RootFragment newInstance() {
        return new RootFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RootAsyncTask(this).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootContainer = (LinearLayout) inflater.inflate(R.layout.fragment_root, container, false);

        progressBar = container.findViewById(R.id.transition_progress);

        return rootContainer;
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

    <T extends Fragment> void switchTo(T fragment) {
        progressBar.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_fragment_layout, fragment)
                .addToBackStack(null)
                .hide(this)
                .commit();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(RootFragment rootFragment);
    }

    class RootAsyncTask extends AbstractAsyncTask<Root> {

        private final String entryUrl;

        RootAsyncTask(Fragment fragment) {
            super(HateoasMediaType.ROOT_TYPE);

            SharedPreferences prefs = fragment.getContext()
                    .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
            entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY, SettingsActivity.ENTRY_URL_DEFAULT);
        }

        @Override
        protected ResponseEntity<Root> doRequest(RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<Root> response = template.exchange(entryUrl, HttpMethod.GET, requestEntity, Root.class);
            Log.d(TAG, "rootAsyncTask: received response " + response);
            return response;
        }

        @Override
        protected void doAfter(Root root) {
            Collection<RootUi.RootButton> rootButtons = Collections2.transform(root.getLinks(),
                    l -> new RootUi.RootButton(l.getRel(), l.getHref()));

            RootUi rootUi = RootUi.builder(RootFragment.this).buttonSpecs(rootButtons).build();

            for (Button button : rootUi.getUiButtons()) {
                rootContainer.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(RootFragment.this);
        }
    }
}
