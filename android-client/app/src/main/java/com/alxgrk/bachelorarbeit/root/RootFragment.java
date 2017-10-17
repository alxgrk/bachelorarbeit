package com.alxgrk.bachelorarbeit.root;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alxgrk.bachelorarbeit.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.MainActivity;
import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.stream.Collectors;

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

    public RootFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentOne.
     */
    public static RootFragment newInstance() {
        RootFragment fragment = new RootFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
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
        void onFragmentInteraction();
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
            Collection<RootUi.RootButton> rootButtons = root.getLinks()
                    .stream()
                    .map(l -> new RootUi.RootButton(l.getRel(), l.getHref()))
                    .collect(Collectors.toList());

            RootUi rootUi = RootUi.builder(RootFragment.this).buttonSpecs(rootButtons).build();

            for (Button button : rootUi.getUiButtons()) {
                rootContainer.addView(button);
            }

            if (mListener != null)
                mListener.onFragmentInteraction();
        }
    }
}
