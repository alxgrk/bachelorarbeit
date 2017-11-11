package com.alxgrk.bachelorarbeit.root;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Lists;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RootFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RootFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RootFragment extends AbstractFragment {

    private static final String TAG = RootFragment.class.getSimpleName();

    public RootFragment() {
        super();
    }

    public static RootFragment newInstance() {
        return new RootFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getContext()
                .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        String entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY, SettingsActivity.ENTRY_URL_DEFAULT);

        new RootAsyncTask().execute(entryUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewContainer,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, viewContainer, (AbstractAsyncTask) null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (null != mListener) {
            mListener.onFragmentInteraction(this, Lists.newArrayList(), !hidden);
        }
    }

    class RootAsyncTask extends AbstractAsyncTask<Root> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.ROOT_TYPE;
        }

        @Override
        protected ResponseEntity<Root> doRequest(String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<Root> response = template.exchange(nextHref, HttpMethod.GET, requestEntity, Root.class);
            Log.d(TAG, "rootAsyncTask: received response " + response);
            return response;
        }

        @Override
        protected void doAfter(Root root) {
            RootUi rootUi = new RootUi(RootFragment.this, root.getLinks());

            for (Button button : rootUi.getUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(RootFragment.this, root.getLinks(), true);
        }
    }
}
