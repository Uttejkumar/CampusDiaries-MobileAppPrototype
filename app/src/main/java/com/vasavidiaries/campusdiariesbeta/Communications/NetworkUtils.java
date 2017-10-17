package com.vasavidiaries.campusdiariesbeta.Communications;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    final static String SERVER_BASE_URL =
            "http://campusdiaries.pythonanywhere.com/";

    public static URL buildUrl(String rollno_query, String pwd_query) {

        Uri builtUri = Uri.parse(SERVER_BASE_URL).buildUpon()
                .appendPath("login")
                .appendPath(rollno_query)
                .appendPath(pwd_query)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrl(String username, String rollno, String password, String passwordre) {

        Uri buildUri = Uri.parse(SERVER_BASE_URL).buildUpon()
                .appendPath("register")
                .appendPath(username)
                .appendPath(rollno)
                .appendPath(password)
                .appendPath(passwordre)
                .build();

        URL url = null;
        try{
            url = new URL(buildUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrlLogin(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d("LoginURL","Inside Call");
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        //urlConnection.setRequestMethod("");
        //urlConnection.setConnectTimeout(15001);
        //urlConnection.setReadTimeout(15001);

        try {
            Log.d("Response Code", Integer.toString(urlConnection.getResponseCode()));
            InputStream in = null;
            in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch(Exception e) {
            Log.e("HTTPError", "Exception", e);
        }
        finally {
            urlConnection.disconnect();
        }
        return null;
    }
}
