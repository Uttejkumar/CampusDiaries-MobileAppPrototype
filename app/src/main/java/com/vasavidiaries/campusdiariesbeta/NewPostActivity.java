package com.vasavidiaries.campusdiariesbeta;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
                uploadImage();

                break;
            }
        }
    }

    private void selectImage(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data){

        super.onActivityResult(requestcode, resultcode, data);

        if(requestcode==IMG_REQUEST && resultcode == RESULT_OK && data!=null){
            Uri path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                mImgpreview.setImageBitmap(bitmap);
                mImgpreview.setVisibility(View.VISIBLE);
                mImgname.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.e("Exception","BITMAP",e);
            }
        }
    }

    private void uploadImage(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, build_url(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        mImgpreview.setImageResource(0);
                        mImgpreview.setVisibility(View.GONE);
                        mImgname.setText("");
                        mImgname.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        ImageUpload.getmInstance(NewPostActivity.this).addToRequestQueue(stringRequest);
    }

    public String build_url(){

        Uri builtUri = Uri.parse(SERVER_BASE_URL).buildUpon()
                .appendPath("imageupload")
                .appendQueryParameter("imagename",mImgname.getText().toString().trim())
                .appendQueryParameter("imagebits",imageToString(bitmap))
                .build();
        return builtUri.toString();

    }

    private String imageToString(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

}
