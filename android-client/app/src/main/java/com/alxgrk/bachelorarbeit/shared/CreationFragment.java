package com.alxgrk.bachelorarbeit.shared;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.accounts.Account;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.organizations.Organization;
import com.alxgrk.bachelorarbeit.resources.Resource;
import com.alxgrk.bachelorarbeit.resources.Timeslot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.common.collect.Lists;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Calendar;

import lombok.RequiredArgsConstructor;

public class CreationFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = CreationFragment.class.getSimpleName();

    private static final String LINK_HREF_ARG = "LINK_HREF";

    private static final String LAYOUR_RES_ARG = "LAYOUT_RES";

    private static final String MEDIA_TYPE_ARG = "MEDIA_TYPE";

    private static final String INPUT_CLASS_ARG = "INPUT_CLASS";

    private static final String DATEPICKER = "datepicker";

    private static final String TIMEPICKER = "timepicker";

    private OnFragmentInteractionListener mListener;

    private Pair<Calendar, Calendar> availableFromTo = new Pair<>(null, null);

    public CreationFragment() {
        // Required empty public constructor
    }

    public static CreationFragment newInstance(Link linkForCreation, MediaType mediaType,
                                               Class<?> inputClass, int layoutRes) {
        CreationFragment fragment = new CreationFragment();

        Bundle args = new Bundle();
        args.putString(LINK_HREF_ARG, linkForCreation.getHref());
        args.putString(MEDIA_TYPE_ARG, mediaType.toString());
        args.putSerializable(INPUT_CLASS_ARG, inputClass);
        args.putInt(LAYOUR_RES_ARG, layoutRes);
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
        if (getArguments() != null) {
            mListener.onFragmentInteraction(this, true);

            LinearLayout creationContainer = (LinearLayout) inflater.inflate(
                    R.layout.fragment_root, container, false);
            ConstraintLayout creationLayout = (ConstraintLayout) inflater.inflate(
                    getArguments().getInt(LAYOUR_RES_ARG), null, true);
            Class<?> inputClass = (Class<?>) getArguments().getSerializable(INPUT_CLASS_ARG);
            String linkForCreation = getArguments().getString(LINK_HREF_ARG);
            String mediaType = getArguments().getString(MEDIA_TYPE_ARG);

            if (inputClass == Account.class) {
                setupAccountCreation(creationLayout, inputClass, linkForCreation, mediaType);
            } else if (inputClass == Organization.class) {
                setupOrganizationCreation(creationLayout, inputClass, linkForCreation, mediaType);
            } else if (inputClass == Resource.class) {
                setupResourceCreation(creationLayout, inputClass, linkForCreation, mediaType, savedInstanceState);
            }

            if (null != creationLayout.getParent())
                ((ViewGroup) creationLayout.getParent()).removeView(creationLayout);
            creationContainer.addView(creationLayout);
            return creationContainer;
        }

        return container;
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
        mListener.onFragmentInteraction(this, false);
        mListener = null;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(year, month, day);
        if (null == availableFromTo.first)
            availableFromTo = new Pair<>(calendar, null);
        else
            availableFromTo = new Pair<>(availableFromTo.first, calendar);

        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true, false);

        timePickerDialog.setCloseOnSingleTapMinute(true);
        timePickerDialog.show(getFragmentManager(), TIMEPICKER);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (null == availableFromTo.second) {
            availableFromTo.first.set(Calendar.HOUR_OF_DAY, hourOfDay);
            availableFromTo.first.set(Calendar.MINUTE, minute);
        } else {
            availableFromTo.second.set(Calendar.HOUR_OF_DAY, hourOfDay);
            availableFromTo.second.set(Calendar.MINUTE, minute);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(CreationFragment creationFragment, boolean visible);
    }


    private void setupAccountCreation(ViewGroup creationLayout, Class<?> inputClass,
                                      String linkForCreation, String mediaType) {
        EditText etSurname = creationLayout.findViewById(R.id.et_account_surname);
        EditText etName = creationLayout.findViewById(R.id.et_account_name);
        EditText etUsername = creationLayout.findViewById(R.id.et_account_username);
        EditText etPassword = creationLayout.findViewById(R.id.et_account_password);

        Button btnCreate = creationLayout.findViewById(R.id.btn_account_create);
        btnCreate.setOnClickListener(v -> {
            Account input = new Account();
            input.setName(etName.getText().toString());
            input.setSurname(etSurname.getText().toString());
            input.setUsername(etUsername.getText().toString());
            input.setPassword(etPassword.getText().toString());

            if (null != input.getUsername() && !input.getUsername().isEmpty()
                    && null != input.getPassword() && !input.getPassword().isEmpty())
                new CreationAsyncTask(this, linkForCreation, mediaType, inputClass)
                        .execute(input);
            else
                Toast.makeText(this.getContext(), "Unable to create new article: either no username" +
                        " or password provided.", Toast.LENGTH_LONG).show();
        });
    }

    private void setupOrganizationCreation(ConstraintLayout creationLayout, Class<?> inputClass,
                                           String linkForCreation, String mediaType) {
        EditText etName = creationLayout.findViewById(R.id.et_org_name);

        Button btnCreate = creationLayout.findViewById(R.id.btn_org_create);
        btnCreate.setOnClickListener(v -> {
            Organization input = new Organization();
            input.setName(etName.getText().toString());
            new CreationAsyncTask(this, linkForCreation, mediaType, inputClass)
                    .execute(input);
        });
    }

    private void setupResourceCreation(ConstraintLayout creationLayout, Class<?> inputClass,
                                       String linkForCreation, String mediaType, Bundle savedInstanceState) {
        EditText etName = creationLayout.findViewById(R.id.et_res_name);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

        View.OnClickListener onClick = v -> {
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(true);
            datePickerDialog.show(getFragmentManager(), DATEPICKER);
        };
        creationLayout.findViewById(R.id.tv_res_avail_from).setOnClickListener(onClick);
        creationLayout.findViewById(R.id.tv_res_avail_to).setOnClickListener(onClick);

        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(DATEPICKER);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }

            TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag(TIMEPICKER);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }

        Button btnCreate = creationLayout.findViewById(R.id.btn_res_create);
        btnCreate.setOnClickListener(v -> {
            Timeslot available = new Timeslot();
            available.setBeginning(availableFromTo.first);
            available.setEnding(availableFromTo.second);

            Resource input = new Resource();
            input.setName(etName.getText().toString());
            input.setAvailableTimeslots(Lists.newArrayList(available));
            input.setBookedTimeslots(Lists.newArrayList());
            new CreationAsyncTask(this, linkForCreation, mediaType, inputClass)
                    .execute(input);
        });
    }

    <T extends Fragment> void goBack() {
        getActivity().onBackPressed();
    }

    @RequiredArgsConstructor
    class CreationAsyncTask extends AsyncTask<Object, Void, ResponseEntity<?>> {

        private final CreationFragment fragment;
        private final String linkForCreation;
        private final String mediaType;
        private final Class<?> inputClass;

        @Override
        protected ResponseEntity<?> doInBackground(Object... body) {
            try {
                MediaType contentType = MediaType.parseMediaType(this.mediaType);

                // Add the content type header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(contentType);
                HttpEntity<?> requestEntity = new HttpEntity<>(body[0], requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                converter.setSupportedMediaTypes(Lists.newArrayList(contentType));
                restTemplate.getMessageConverters().add(converter);
                restTemplate.setErrorHandler(new CreationErrorHandler());

                String result = new ObjectMapper().writeValueAsString(body[0]);
                Log.d(TAG, "Trying to post entity " + result);

                ResponseEntity<?> response = restTemplate.exchange(linkForCreation, HttpMethod.POST,
                        requestEntity, inputClass);

                Log.d(TAG, "Posting an object of type " + body.getClass()
                        + " returned HTTP response " + response.getStatusCode());

                return response;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResponseEntity<?> response) {
            if (null == response) {
                Log.d(TAG, "No response - error while sending request.");
                return;
            }

            Context context = fragment.getContext();
            if (response.getStatusCode().value() < 400) {
                String msg = "Successfully created.";
                Log.d(TAG, msg);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            } else {
                String msg = "Could not be created.";
                Log.e(TAG, msg + " Error code = " + response.getStatusCode());
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }

            goBack();
        }
    }

    static class CreationErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().value() >= 400;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            Log.e(TAG, "An error occured: " + response.getHeaders() + response.getBody());
        }
    }
}
