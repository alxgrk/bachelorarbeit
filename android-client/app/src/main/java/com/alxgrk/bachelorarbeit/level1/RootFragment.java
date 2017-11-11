package com.alxgrk.bachelorarbeit.level1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alxgrk.bachelorarbeit.SettingsActivity;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.level1.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.level1.organizations.collection.OrganizationsFragment;
import com.alxgrk.bachelorarbeit.level1.resources.collection.ResourcesFragment;
import com.alxgrk.bachelorarbeit.shared.AbstractAsyncTask;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.google.common.collect.Lists;

import static android.content.Context.MODE_PRIVATE;


public class RootFragment extends AbstractFragment {

    private static final String TAG = RootFragment.class.getSimpleName();

    public RootFragment() {
        super();
    }

    public static RootFragment newInstance() {
        return new RootFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewContainer,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, viewContainer, (AbstractAsyncTask) null);

        SharedPreferences prefs = getContext()
                .getSharedPreferences(SettingsActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        String entryUrl = prefs.getString(SettingsActivity.ENTRY_URL_KEY_1, SettingsActivity.ENTRY_URL_DEFAULT_1);

        progressBar.setVisibility(View.GONE);

        Button accountsButton = new Button(getContext());
        accountsButton.setText(PossibleRelation.ACCOUNTS.toString());
        accountsButton.setOnClickListener(v -> {
            switchTo(AccountsFragment.newInstance(entryUrl + "accounts/get-all"));
        });
        container.addView(accountsButton);

        Button orgsButton = new Button(getContext());
        orgsButton.setText(PossibleRelation.ORGANIZATIONS.toString());
        orgsButton.setOnClickListener(v -> {
            switchTo(OrganizationsFragment.newInstance(entryUrl + "orgs/get-all"));
        });
        container.addView(orgsButton);

        Button resButton = new Button(getContext());
        resButton.setText(PossibleRelation.RESOURCES.toString());
        resButton.setOnClickListener(v -> {
            switchTo(ResourcesFragment.newInstance(entryUrl + "resources/get-all"));
        });
        container.addView(resButton);

        if (mListener != null)
            mListener.onFragmentInteraction(this, Lists.newArrayList(), true);

        return container;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (null != mListener) {
            mListener.onFragmentInteraction(this, Lists.newArrayList(), !hidden);
        }
    }

}
