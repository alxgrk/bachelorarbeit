package com.alxgrk.bachelorarbeit.shared;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

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

import lombok.RequiredArgsConstructor;

public class CreationFragment extends Fragment {

    private static final String TAG = CreationFragment.class.getSimpleName();

    private static final String LINK_HREF_ARG = "LINK_HREF";

    private static final String LAYOUR_RES_ARG = "LAYOUT_RES";

    private static final String MEDIA_TYPE_ARG = "MEDIA_TYPE";

    private static final String INPUT_CLASS_ARG = "INPUT_CLASS";

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
                // TODO
            } else if (inputClass == Resource.class) {
                // TODO
            }

            if (null != creationLayout.getParent())
                ((ViewGroup) creationLayout.getParent()).removeView(creationLayout);
            creationContainer.addView(creationLayout);
            return creationContainer;
        }

        return container;
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
            new CreationAsyncTask(this, linkForCreation, mediaType, inputClass)
                    .execute(input);

            goBack();
        });
    }

    private void setupResourceCreation(ConstraintLayout creationLayout, Class<?> inputClass,
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
            new CreationAsyncTask(this, linkForCreation, mediaType, inputClass)
                    .execute(input);

            goBack();
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

            if (response.getStatusCode().value() < 400)
                Toast.makeText(fragment.getContext(), "Successfully created.", Toast.LENGTH_SHORT)
                        .show();
            else
                Toast.makeText(fragment.getContext(), "Could not be created.", Toast.LENGTH_SHORT)
                        .show();

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
