package com.alxgrk.bachelorarbeit.accounts;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Collections2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountsFragment extends AbstractFragment {

    private static final String TAG = AccountsFragment.class.getSimpleName();

    public AccountsFragment() {
        super();
    }

    public static AccountsFragment newInstance(String nextHref) {
        return AbstractFragment.newInstance(new AccountsFragment(), nextHref);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, new AccountsAsyncTask());
    }

    class AccountsAsyncTask extends AbstractAsyncTask<AccountCollection> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.ACCOUNT_TYPE;
        }

        @Override
        protected ResponseEntity<AccountCollection> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
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
                container.addView(accountEntry);
            }
            for (Button button : accountUi.getUiButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(AccountsFragment.this, accounts.getLinks(), true);
        }
    }
}
