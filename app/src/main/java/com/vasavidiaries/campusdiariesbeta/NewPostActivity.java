package com.vasavidiaries.campusdiariesbeta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vasavidiaries.campusdiariesbeta.Communications.ImageUpload;
import com.vasavidiaries.campusdiariesbeta.Communications.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mPosttitle, mPostdetailsshort, mPostdetailslong, mContact;
    EditText mStartdate, mEnddate;
    Spinner mClubs;
    Button mUploadimage, mPost;

    EditText mImgname;
    ImageView mImgpreview;

    private final int IMG_REQUEST = 1;

    Bitmap bitmap;

    String commonTitle = null;

    final static String SERVER_BASE_URL =
            "http://10.0.2.2:8080/";

//    private String uploadurl = "http://10.0.2.2:8080/imageupload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mapGUI();
    }

    public void mapGUI()
    {
        mPosttitle = (EditText)findViewById(R.id.dPosttitle);
        mPostdetailsshort = (EditText)findViewById(R.id.dPostdetailsshort);
        mPostdetailslong = (EditText)findViewById(R.id.dPostdetailslong);
        mContact = (EditText)findViewById(R.id.dContact);

        mStartdate = (EditText)findViewById(R.id.dStartdate);
        mEnddate = (EditText)findViewById(R.id.dEnddate);

        mUploadimage = (Button)findViewById(R.id.dUploadimage);
        mPost = (Button)findViewById(R.id.dPost);

        mImgname = (EditText)findViewById(R.id.dimgname);
        mImgpreview = (ImageView)findViewById(R.id.dimgpreview);

        mUploadimage.setOnClickListener(this);
        mPost.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        switch(view.getId()){
            case R.id.dUploadimage:{
                selectImage();
                Toast.makeText(getApplicationContext(),"This feature will be implemented soon. Meanwhile you can upload a pic from the web app.", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.dPost:{
                uploadContent();
                uploadImage();
                break;
            }
        }
    }

    private void selectImage(){

        Log.d("Newpostactivity","Entered selectimage()");

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);

        Log.d("Newpostactivity","Left selectimage()");

    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data){

        super.onActivityResult(requestcode, resultcode, data);

        Log.d("Newpostactivity","Entered onactivityresult()");

        if(requestcode==IMG_REQUEST && resultcode == RESULT_OK && data!=null){
            Uri path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                mImgpreview.setImageBitmap(bitmap);
                mImgpreview.setVisibility(View.VISIBLE);
                mImgname.setVisibility(View.VISIBLE);
                mPosttitle.setVisibility(View.GONE);
                mPostdetailsshort.setVisibility(View.GONE);
                mPostdetailslong.setVisibility(View.GONE);
                mContact.setVisibility(View.GONE);
                mStartdate.setVisibility(View.GONE);
                mEnddate.setVisibility(View.GONE);
                mUploadimage.setVisibility(View.GONE);

            } catch (IOException e) {
                Log.e("Exception","BITMAP",e);
            }
        }

        Log.d("Newpostactivity","Left onactivityresult()");
    }

    private void uploadImage(){

        Log.d("Newpostactivity","Entered uploadImage()");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, build_url(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        mImgpreview.setImageResource(0);
                        mImgpreview.setVisibility(View.GONE);
                        mImgname.setText("");
                        mImgname.setVisibility(View.GONE);

                        if (response.equals("success")){
                            Intent gobacktoposts = new Intent(NewPostActivity.this, PostsActivity.class);
                            startActivity(gobacktoposts);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        ImageUpload.getmInstance(NewPostActivity.this).addToRequestQueue(stringRequest);

        Log.d("Newpostactivity","Left uploadImage()");
    }

    public String build_url(){

        Log.d("Newpostactivity","in buildurl()");

        SharedPreferences extractuser = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
        String postinguser = extractuser.getString("username","");

        Uri builtUri = Uri.parse(SERVER_BASE_URL).buildUpon()
                .appendPath("imageupload")
                .appendQueryParameter("postinguser",postinguser)
                .appendQueryParameter("title", commonTitle)
                .appendQueryParameter("imagename",mImgname.getText().toString().trim())
                .appendQueryParameter("imagebits",imageToString(bitmap))
                .build();

        Log.d("Newpostactivity","out buildurl()");
        return builtUri.toString();

    }

    private String imageToString(Bitmap bitmap){

        Log.d("Newpostactivity","in imagetostring()");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();

        Log.d("Newpostactivity","out imagetostring()");
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    public void uploadContent(){


        commonTitle = mPosttitle.getText().toString();
        String shortdesc = mPostdetailsshort.getText().toString();
        String longdesc = mPostdetailslong.getText().toString();
        String contact = mContact.getText().toString();
        String startdate = mStartdate.getText().toString();
        String enddate = mEnddate.getText().toString();
         //TODO: Spinner activity

        SharedPreferences extractuser = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
        String postinguser = extractuser.getString("username","");

        Uri uploadposturi = Uri.parse(SERVER_BASE_URL).buildUpon()
                .appendPath("newpost")
                .appendQueryParameter("postinguser", postinguser)
                .appendQueryParameter("title", commonTitle)
                .appendQueryParameter("shortdesc", shortdesc)
                .appendQueryParameter("longdesc", longdesc)
                .appendQueryParameter("contact", contact)
                .appendQueryParameter("startdate", startdate)
                .appendQueryParameter("enddate", enddate)
                .build();

        URL uploadposturl = null;
        try {
            uploadposturl = new URL(uploadposturi.toString());
        } catch (MalformedURLException e) {
            Log.e("NewPostActivity","Building upload content url",e);
        }

        new uploadingContent().execute(uploadposturl);

    }

    public class uploadingContent extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... params){
            URL posturl = params[0];
            String postresult = null;
            try{
                postresult = NetworkUtils.getResponseFromHttpUrlLogin(posturl);
            }catch (Exception e){
                Log.e("NewPostActivity", "Getting response", e);
            }
            return postresult;
        }

        @Override
        protected void onPostExecute(String postresults){
            Log.d("NewPostActivity","Reached onpostexecute of contentupload");
            if(postresults != null && !postresults.equals("")){
                if(postresults.equals("Successful upload")){
                    Toast.makeText(getApplicationContext(),"New post uploading...", Toast.LENGTH_LONG).show();
                }
                else if(postresults.equals("Failed")){
                    Toast.makeText(getApplicationContext(),"There seems to be an error in the post data. Try again.", Toast.LENGTH_SHORT).show();
                }
                else {
                        Toast.makeText(getApplicationContext(),"Oops...something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                    }
            }
            Log.d("NewPostActivity","Reached onpostexecute of contentupload");
        }
    }
}
