package com.vasavidiaries.campusdiariesbeta.Communications;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ImageUpload {

    private static ImageUpload mInstance;
    private RequestQueue requestQueue;
    private static Context mctx;

    private ImageUpload(Context context)
    {
        mctx = context;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue(){
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized ImageUpload getmInstance(Context context){
        if(mInstance == null){
            mInstance = new ImageUpload(context);
        }
        return mInstance;
    }

    public<T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }


}
