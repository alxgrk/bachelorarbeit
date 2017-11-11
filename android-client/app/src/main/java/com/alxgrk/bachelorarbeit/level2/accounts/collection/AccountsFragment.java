package com.alxgrk.bachelorarbeit.level2.accounts.collection;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.accounts.Account;
import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Lists;

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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (null != mListener) {
            mListener.onFragmentInteraction(this, getSavedLinks(), !hidden);
        }
    }

    class AccountsAsyncTask extends AbstractAsyncTask<Account[]> {

        @Override
        protected MediaType getSupportedMediaType() {
            return HateoasMediaType.ACCOUNT_TYPE;
        }

        @Override
        protected ResponseEntity<Account[]> doRequest(
                String nextHref, RestTemplate template, HttpEntity<?> requestEntity) {
            ResponseEntity<Account[]> response = template.exchange(nextHref, HttpMethod.GET,
                    requestEntity, Account[].class);
            Log.d(TAG, AccountsAsyncTask.class.getSimpleName() + ": received response " + response);
            return response;
        }

        @Override
        protected void doAfter(Account[] accounts) {
            AccountsUi accountsUi = new AccountsUi(AccountsFragment.this, Lists.newArrayList(accounts));

            for (ConstraintLayout accountEntry : accountsUi.getUiAccountEntries()) {
                container.addView(accountEntry);
            }
            for (Button button : accountsUi.getSharedUi().getButtons()) {
                container.addView(button);
            }

            progressBar.setVisibility(View.GONE);

            if (mListener != null) {
                // create a new link as a hack to be able to use the listener callback
                Link createLink = new Link().setHref(accountsUi.getEntryUrl() + "accounts")
                        .setMethod(HttpMethod.POST.toString())
                        .setRel(PossibleRelation.CREATE.toString());
                mListener.onFragmentInteraction(AccountsFragment.this, Lists.newArrayList(createLink), true);
            }
        }
    }
}
