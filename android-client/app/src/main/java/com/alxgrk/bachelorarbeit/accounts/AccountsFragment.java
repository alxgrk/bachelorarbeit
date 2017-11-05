package com.alxgrk.bachelorarbeit.accounts;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;


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

    private ProgressBar progressBar;

    public AccountsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nextHref
     * @return A new instance of fragment FragmentOne.
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        accountsContainer = (LinearLayout) inflater.inflate(R.layout.fragment_root, container, false);

        progressBar = container.findViewById(R.id.transition_progress);

        if (getArguments() != null) {
            progressBar.setVisibility(View.VISIBLE);

            String nextHref = getArguments().getString(NEXT_HREF_ARG);
            new AccountsAsyncTask(nextHref).execute();
        }

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
        mListener.onFragmentInteraction(this, Lists.newArrayList());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(AccountsFragment accountsFragment, List<Link> links);
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
            Collection<AccountUi.AccountButton> accountButtons = Collections2.transform(accounts.getLinks(),
                    l -> new AccountUi.AccountButton(l.getRel(), l.getHref()));

            AccountUi accountUi = AccountUi.builder(AccountsFragment.this)
                    .accounts(accounts.getMembers())
                    .buttonSpecs(accountButtons)
                    .build();

            for (ConstraintLayout accountEntry : accountUi.getUiAccountEntries()) {
                accountsContainer.addView(accountEntry);
            }
            for (Button button : accountUi.getUiButtons()) {
                accountsContainer.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(AccountsFragment.this, accounts.getLinks());
        }
    }
}
