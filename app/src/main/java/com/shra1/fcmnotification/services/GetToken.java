package com.shra1.fcmnotification.services;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

public class GetToken extends AsyncTask<Void, Void, String> {

    public static final String TAG = "DeViCe";

    GetTokenCallback c;

    public GetToken(GetTokenCallback c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        c.onStart();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String token = null;
        do try {
            token = FirebaseInstanceId.getInstance().getToken();
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } while (token == null);
        return token;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        c.onEnd();
        c.onSuccessfull(s);
        Log.d(TAG, "myToken for " + android.os.Build.MANUFACTURER + ": " + s);
    }

    public interface GetTokenCallback {
        public void onSuccessfull(String token);

        public void onStart();

        public void onEnd();
    }
}