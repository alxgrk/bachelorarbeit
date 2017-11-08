package com.alxgrk.bachelorarbeit.shared;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.google.common.collect.Lists;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class AbstractFragment extends Fragment {

    public static final String NEXT_HREF_ARG = "NEXT_HREF";

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private List<Link> savedLinks = Lists.newArrayList();

    protected OnFragmentInteractionListener mListener;

    protected LinearLayout container;

    protected ProgressBar progressBar;

    public AbstractFragment() {
        // Required empty-args constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nextHref
     * @return A new instance of fragment FragmentOne.
     */
    public static <F extends AbstractFragment> F newInstance(F fragment, String nextHref) {
        Bundle args = new Bundle();
        args.putString(NEXT_HREF_ARG, nextHref);
        fragment.setArguments(args);
        return fragment;
    }

    protected <T extends AbstractAsyncTask> View onCreateView(LayoutInflater inflater,
                                                              @Nullable ViewGroup viewContainer,
                                                              T asyncTask) {
        container = (LinearLayout) inflater.inflate(R.layout.fragment_root, viewContainer, false);

        if (null != viewContainer)
            progressBar = viewContainer.findViewById(R.id.transition_progress);

        if (null != getArguments() && null != getArguments().getString(NEXT_HREF_ARG)) {
            progressBar.setVisibility(View.VISIBLE);

            String nextHref = getArguments().getString(NEXT_HREF_ARG);
            asyncTask.execute(nextHref);
        }

        return container;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " + OnFragmentInteractionListener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public <T extends Fragment> void switchTo(T fragment) {
        progressBar.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_fragment_layout, fragment)
                .addToBackStack(null)
                .hide(this)
                .commit();
    }

    public static void reload(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        fragmentManager.beginTransaction()
                .remove(fragment)
                .commitNowAllowingStateLoss();
        fragmentManager.beginTransaction()
                .add(R.id.main_fragment_layout, fragment)
                .commit();
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
        <F extends AbstractFragment> void onFragmentInteraction(F fragment, List<Link> links, boolean visible);
    }
}
