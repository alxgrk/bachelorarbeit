package com.alxgrk.bachelorarbeit;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.Lists;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractAsyncTask<T> extends AsyncTask<Void, Void, T> {

    private static final String TAG = AbstractAsyncTask.class.getSimpleName();

    private final MediaType supportedMediaType;

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected T doInBackground(Void... params) {
        try {
            List<MediaType> supportedMediaTypes = Lists.newArrayList(supportedMediaType);

            // Add the accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(supportedMediaTypes);
            HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setSupportedMediaTypes(supportedMediaTypes);
            restTemplate.getMessageConverters().add(converter);

            ResponseEntity<T> responseEntity = doRequest(restTemplate, requestEntity);

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
     * Use this method to call whatever method on a pre-configure RestTemplate.
     *
     * @param template      the RestTemplate configured with a specific MessageConverter
     * @param requestEntity a HttpEntity configure with the SupportedMediaType accept header
     * @return the response
     */
    protected abstract ResponseEntity<T> doRequest(RestTemplate template, HttpEntity<?> requestEntity);

    /**
     * Will be executed after the task finished. Parent thread is UI-Thread!
     *
     * @param result the body of the response
     */
    protected abstract void doAfter(T result);
}
