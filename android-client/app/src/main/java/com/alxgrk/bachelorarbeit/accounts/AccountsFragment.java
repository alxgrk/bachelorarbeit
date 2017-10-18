package com.alxgrk.bachelorarbeit.accounts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
 * {@link AccountsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountsFragment extends Fragment {

    private static final String TAG = AccountsFragment.class.getSimpleName();

    private static final String NEXT_HREF_ARG = "NEXT_HREF";

    private OnFragmentInteractionListener mListener;

    private LinearLayout accountsContainer;

    public AccountsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentOne.
     * @param nextHref
     */
    public static AccountsFragment newInstance(String nextHref) {
        AccountsFragment fragment = new AccountsFragment();
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
            new AccountsAsyncTask(nextHref).execute();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView parent = (ScrollView) inflater.inflate(R.layout.fragment_root, container, false);
        accountsContainer = parent.findViewById(R.id.root_container);
        return accountsContainer;
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
        void onFragmentInteraction(AccountsFragment rootFragment);
    }

    class AccountsAsyncTask extends AbstractAsyncTask<AccountCollection> {

        private final String nextHref;

        AccountsAsyncTask(String nextHref) {
            super(HateoasMediaType.ACCOUNT_TYPE);

            this.nextHref = nextHref;
        }

        @Override
        protected ResponseEntity<AccountCollection> doRequest(
                RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<AccountCollection> response = template.exchange(nextHref, HttpMethod.GET,
                    requestEntity, AccountCollection.class);
            Log.d(TAG, "accountsAsyncTask: received response " + response);
            return response;
        }

        @Override
        protected void doAfter(AccountCollection accounts) {
            Collection<AccountUi.AccountButton> rootButtons = Collections2.transform(accounts.getLinks(),
                    l -> new AccountUi.AccountButton(l.getRel(), l.getHref()));

            AccountUi rootUi = AccountUi.builder(AccountsFragment.this)
                    .accounts(accounts.getMembers())
                    .buttonSpecs(rootButtons)
                    .build();

            for (ConstraintLayout accountEntry : rootUi.getUiAccountEntries()) {
                accountsContainer.addView(accountEntry);
            }
            for (Button button : rootUi.getUiButtons()) {
                accountsContainer.addView(button);
            }

            if (mListener != null)
                mListener.onFragmentInteraction(AccountsFragment.this);
        }
    }
}
