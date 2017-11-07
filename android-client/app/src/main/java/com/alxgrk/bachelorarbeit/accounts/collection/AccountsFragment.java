package com.alxgrk.bachelorarbeit.accounts.collection;

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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
            Log.d(TAG, AccountsAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(AccountCollection accounts) {
            AccountsUi accountsUi = new AccountsUi(AccountsFragment.this, accounts.getLinks(), accounts.getMembers());

            for (ConstraintLayout accountEntry : accountsUi.getUiAccountEntries()) {
                container.addView(accountEntry);
            }
            for (Button button : accountsUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(AccountsFragment.this, accounts.getLinks(), true);
        }
    }
}
