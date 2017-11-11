package com.alxgrk.bachelorarbeit.accounts;

import android.os.Bundle;
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

public class AccountFragment extends AbstractFragment {

    private static final String TAG = AccountFragment.class.getSimpleName();

    public AccountFragment() {
        super();
    }

    public static AccountFragment newInstance(String nextHref) {
        return AbstractFragment.newInstance(new AccountFragment(), nextHref);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, new AccountAsyncTask());
    }

    class AccountAsyncTask extends AbstractAsyncTask<Account> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.ACCOUNT_TYPE;
        }

        @Override
        protected ResponseEntity<Account> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<Account> response = template.exchange(nextHref, HttpMethod.GET,
                    requestEntity, Account.class);
            Log.d(TAG, AccountAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(Account account) {
            AccountUi accountUi = new AccountUi(AccountFragment.this, account);

            container.addView(accountUi.getUiAccount());

            for (Button button : accountUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null)
                mListener.onFragmentInteraction(AccountFragment.this, account.getLinks(), true);
        }
    }
}
