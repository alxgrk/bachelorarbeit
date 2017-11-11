package com.alxgrk.bachelorarbeit.shared;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.Lists;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;

public abstract class AbstractAsyncTask<T> extends AsyncTask<Object, Void, T> {

    private static final String TAG = AbstractAsyncTask.class.getSimpleName();

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected T doInBackground(Object... nextHrefs) {
        try {
            List<MediaType> supportedMediaTypes = Lists.newArrayList(getSupportedMediaType());

            // Add the accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(supportedMediaTypes);
            HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setSupportedMediaTypes(supportedMediaTypes);
            restTemplate.getMessageConverters().add(converter);

            restTemplate.setErrorHandler(new GenericErrorHandler());

            ResponseEntity<T> responseEntity = doRequest(nextHrefs[0].toString(), restTemplate, requestEntity);

            return responseEntity.getBody();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(T t) {
        doAfter(t);
    }

    /**
     * Override this to provide the MediaType used for interaction with the REST API.
     *
     * @return the MediaType used for requests
     */
    protected abstract MediaType getSupportedMediaType();

    /**
     * Use this method to call whatever method on a pre-configure RestTemplate.
     *
     * @param template      the RestTemplate configured with a specific MessageConverter
     * @param requestEntity a HttpEntity configure with the SupportedMediaType accept header
     * @return the response
     */
    protected abstract ResponseEntity<T> doRequest(String nextHref, RestTemplate template,
                                                   HttpEntity<?> requestEntity);

    /**
     * Will be executed after the task finished. Parent thread is UI-Thread!
     *
     * @param result the body of the response
     */
    protected abstract void doAfter(T result);

    protected static class GenericErrorHandler implements ResponseErrorHandler {

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
