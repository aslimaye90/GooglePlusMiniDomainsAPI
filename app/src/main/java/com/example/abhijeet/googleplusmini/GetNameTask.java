package com.example.abhijeet.googleplusmini;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Display personalized greeting. This class contains boilerplate code to consume the token but
 * isn't integral to getting the tokens.
 */
public class GetNameTask extends AsyncTask<Void, Void, Void>{
    private static final String TAG = "TokenInfoTask";
    private static final String NAME_KEY = "given_name";

    protected HelloActivity mActivity;
    protected String mScope;
    protected String mEmail;

    GetNameTask(HelloActivity activity, String email, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = email;
    }

    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getTokenWithNotification(  //or GoogleAuthUtil.getToken()
                    mActivity, mEmail, mScope, null, makeCallback(mEmail));
        } catch (UserRecoverableNotifiedException userRecoverableException) {
            // Unable to authenticate, but the user can fix this.
            // Because we've used getTokenWithNotification(), a Notification is
            // created automatically so the user can recover from the error
            onError("Could not fetch token.", null);
        } catch (GoogleAuthException fatalException) {
            onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
        }
        return null;
    }

    private Intent makeCallback(String accountName) {
        Intent intent = new Intent();
        intent.setAction("com.example.abhijeet.googleplusmini.Callback");
        intent.putExtra(HelloActivity.EXTRA_ACCOUNTNAME, accountName);
        intent.putExtra(HelloActivity.TYPE_KEY, "Background");
        return intent;
    }

    public static class CallbackReceiver extends BroadcastReceiver {
        public static final String TAG = "CallbackReceiver";

        @Override
        public void onReceive(Context context, Intent callback) {
            Bundle extras = callback.getExtras();
            Intent intent = new Intent(context, HelloActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(extras);
            Log.i(TAG, "Received broadcast. Resurrecting activity");
            context.startActivity(intent);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            fetchNameFromProfileServer();
        } catch (IOException ex) {
            onError("Following Error occured, please try again. " + ex.getMessage(), ex);
        } catch (JSONException e) {
            onError("Bad response: " + e.getMessage(), e);
        }
        return null;
    }

    protected void onError(String msg, Exception e) {
        if (e != null) {
            Log.e(TAG, "Exception: ", e);
        }
        mActivity.show(msg);  // will be run in UI thread
    }


    private void fetchNameFromProfileServer() throws IOException, JSONException {
        String token = fetchToken();
        if (token == null) {
            // error has already been handled in fetchToken()
            return;
        }

        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int sc = con.getResponseCode();
        if (sc == 200) {
            InputStream is = con.getInputStream();
            String name = getFirstName(readResponse(is));
            mActivity.show("Hello " + name + "!");
            is.close();
            return;
        } else if (sc == 401) {
            GoogleAuthUtil.invalidateToken(mActivity, token);
            onError("Server auth error, please try again.", null);
            Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
            return;
        } else {
            onError("Server returned the following error code: " + sc, null);
            return;
        }
    }

    /**
     * Reads the response from the input stream and returns it as a string.
     */
    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }

    /**
     * Parses the response and returns the first name of the user.
     * @throws JSONException if the response is not JSON or if first name does not exist in response
     */
    private String getFirstName(String jsonResponse) throws JSONException {
        JSONObject profile = new JSONObject(jsonResponse);
        return profile.getString(NAME_KEY);
    }
}