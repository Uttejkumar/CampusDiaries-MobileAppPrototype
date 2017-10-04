package com.vasavidiaries.campusdiariesbeta;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.MenuItemHoverListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.vasavidiaries.campusdiariesbeta.JSONParser;
import com.vasavidiaries.campusdiariesbeta.Models.Posts;

public class PostsActivity extends AppCompatActivity {


    StringBuffer buffer = new StringBuffer();
    ListView lvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        Log.d("PLACE","IN POSTS ACTIVITY");

        lvPosts = (ListView)findViewById(R.id.lvPosts);

        new GetPosts().execute("Execute");
    }

    public class GetPosts extends AsyncTask<String, Void, List<Posts>>{

        @Override
        protected List<Posts> doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("http://10.0.2.2:8080/retrieveposts");
            } catch (Exception e) {
                Log.e("EXception", "URLS", e);
            }
            String searchResults = null;
            try {
                searchResults = getJsonData(url);
            } catch (Exception e) {
                Log.e("MYAPP", "Exception", e);
            }

            String jsonstring = searchResults;
            List<Posts> postsList = new ArrayList<>();

            try {
                JSONObject parentObject = new JSONObject(jsonstring);
                JSONArray parentArray = parentObject.getJSONArray("AllPosts");

                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    Posts post = new Posts();

                    post.setTitle(finalObject.getString("title"));
                    post.setClub(finalObject.getString("club"));
                    post.setContact(finalObject.getInt("contact"));
                    post.setEnddate(finalObject.getString("enddate"));
                    post.setPostedby(finalObject.getInt("postedby"));
                    post.setPostpic(finalObject.getString("postpic"));
                    post.setShortdesc(finalObject.getString("shortdesc"));
                    post.setStartdate(finalObject.getString("startdate"));

                    postsList.add(post);
                }
            }catch(Exception e)
            {
                Log.e("JSON","Exception",e);
            }

            return postsList;
        }

        @Override
        protected void onPostExecute(List<Posts> postsList) {
            super.onPostExecute(postsList);

            PostsAdapter adapter = new PostsAdapter(getApplicationContext(), R.layout.post, postsList);
            lvPosts.setAdapter(adapter);
        }
    }

    public static String getJsonData(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d("JSON","Getting JSON data");
        urlConnection.setUseCaches(false);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_refresh){
            new GetPosts().execute("Execute");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PostsAdapter extends ArrayAdapter{

        private List<Posts> postsList;
        private int resource;
        private LayoutInflater inflater;

        public PostsAdapter(Context context, int resource, List<Posts> objects) {

            super(context, resource, objects);
            postsList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            Log.d("PostsAdapter","inside get view");

            if (convertView == null){
                convertView = inflater.inflate(resource, null);
            }

            //ImageView ivPost;
            TextView title;
            TextView postedby;
            TextView Club;
            TextView startdate;
            TextView enddate;
            TextView contact;
            TextView Description;

            Log.d("POstsAdapter","Assigning views");

            //ivPost = (ImageView)convertView.findViewById(R.id.posticon);
            title = (TextView)convertView.findViewById(R.id.title);
            postedby = (TextView)convertView.findViewById(R.id.postedby);
            Club = (TextView)convertView.findViewById(R.id.Club);
            startdate = (TextView)convertView.findViewById(R.id.startdate);
            enddate = (TextView)convertView.findViewById(R.id.enddate);
            contact = (TextView)convertView.findViewById(R.id.contact);
            Description = (TextView)convertView.findViewById(R.id.Description);

            Log.d("PostsAdapter","Setting values to views");

            title.setText(postsList.get(position).getTitle());
            postedby.setText("Posted By: " + String.valueOf(postsList.get(position).getPostedby()));
            Club.setText("Club: " + postsList.get(position).getClub());
            startdate.setText("Start Date: " + postsList.get(position).getStartdate());
            enddate.setText("End date: " + postsList.get(position).getEnddate());
            contact.setText("Contact" + String.valueOf(postsList.get(position).getContact()));
            Description.setText(postsList.get(position).getShortdesc());

            Log.d("POstsAdapter","Returning convertview");

            return convertView;
        }
    }
}
