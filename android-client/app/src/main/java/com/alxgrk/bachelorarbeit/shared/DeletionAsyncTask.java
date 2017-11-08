package com.alxgrk.bachelorarbeit.shared;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeletionAsyncTask extends AsyncTask<Void, Void, ResponseEntity<?>> {

    private static final String TAG = DeletionAsyncTask.class.getSimpleName();

    private final Activity activity;

    private final String linkForDeletion;

    @Override
    protected ResponseEntity<?> doInBackground(Void... avoid) {
        try {
            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.setErrorHandler(new AbstractAsyncTask.GenericErrorHandler());

            ResponseEntity<?> response = restTemplate.exchange(linkForDeletion, HttpMethod.DELETE,
                    null, null);

            Log.d(TAG, "Delete on " + linkForDeletion
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

        if (response.getStatusCode().value() < 400) {
            String msg = "Successfully deleted.";
            Log.d(TAG, msg);
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        } else {
            String msg = "Could not be deleted.";
            Log.e(TAG, msg + " Error code = " + response.getStatusCode());
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        }
    }

}
