package com.vasavidiaries.campusdiariesbeta;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vasavidiaries.campusdiariesbeta.Communications.NetworkUtils;

import java.net.URL;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mUname, mRollno, mPassword, mPasswordre;
    Button mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mapGUI();
    }

    public void mapGUI()
    {
        mUname = (EditText) findViewById(R.id.dUsername);
        mRollno = (EditText) findViewById(R.id.dRollnumber);
        mPassword = (EditText) findViewById(R.id.dPassword);
        mPasswordre = (EditText) findViewById(R.id.dPasswordre);

        mSignup = (Button) findViewById(R.id.dSignup);

        mSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        try{
            if (view.getId() == R.id.dSignup){
                String username = mUname.getText().toString();
                String rollnumber = mRollno.getText().toString();
                String password = mPassword.getText().toString();
                String passwordre = mPasswordre.getText().toString();

                if(rollnumber.length() == 12 && password.equals(passwordre))
                    signUpEvent(username, rollnumber, password, passwordre);
                else
                    Toast.makeText(getApplicationContext(),"Incorrect SignUp Credentials", Toast.LENGTH_LONG);
            }
        }catch(Exception e) {
            Log.e("Signup","Exception",e);
        }
    }

    public void signUpEvent(String username, String rollnumber, String password, String passwordre)
    {
        URL newUser = NetworkUtils.buildUrl(username, rollnumber, password, passwordre);
        Log.d("URL", newUser.toString());
        new QueryTask().execute(newUser);
    }

    public class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrlLogin(searchUrl);
            } catch (Exception e) {
                Log.e("MYAPP", "Exception", e);
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            Log.d("Searchresults","Hello" + searchResults);
            if (searchResults != null && !searchResults.equals("")) {
                if (searchResults.equals("Exists")) {
                    Toast.makeText(getApplicationContext(), "Rollnumber already registered.",
                            Toast.LENGTH_LONG).show();
                    //TODO: Go to FirstTimeActivity
                }
                else if(searchResults.equals("Unmatched")){
                    Toast.makeText(getApplicationContext(), "Registration Error. Retry.",
                            Toast.LENGTH_LONG).show();
                    //TODO: Go to FirstTimeActivity
                }
                else if(searchResults.equals("Success")){
                    Toast.makeText(getApplicationContext(), "Registration successful.", Toast.LENGTH_LONG).show();
                    Thread splashscreen = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                Intent goToFirstTime = new Intent(SignUpActivity.this, FirstTimeActivity.class);
                                startActivity(goToFirstTime);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    splashscreen.start();
                }
            }
        }
    }
}
