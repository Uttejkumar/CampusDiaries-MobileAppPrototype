package com.vasavidiaries.campusdiariesbeta;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vasavidiaries.campusdiariesbeta.Communications.NetworkUtils;
import com.vasavidiaries.campusdiariesbeta.Models.Posts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static android.support.v7.widget.RecyclerView.*;

public class ModeratePosts extends AppCompatActivity {

    ListView tobemoderatedposts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderate_posts);

        tobemoderatedposts = (ListView) findViewById(R.id.moderatePosts);

        new GetModeratePosts().execute("Execute");
    }

    public class GetModeratePosts extends AsyncTask<String, Void, List<Posts>> {


        @Override
        protected List<Posts> doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http://campusdiaries.pythonanywhere.com/retrievetobemoderatedposts");
            } catch (Exception e) {
                Log.e("Exception", "To be Moderated URL", e);
            }
            String returnresults = null;
            try {
                returnresults = getJsonModerateData(url);
            } catch (Exception e) {
                Log.e("Exception", "Getting JSON Moderate", e);
            }
            String jsonstring = returnresults;
            List<Posts> tobemoderatedarray = new ArrayList<>();
            try {
                JSONObject parentobject = new JSONObject(jsonstring);
                JSONArray parentArray = parentobject.getJSONArray("AllPosts");

                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    Posts post = new Posts();

                    post.setPostid(finalObject.getInt("postid"));
                    post.setTitle(finalObject.getString("title"));
                    post.setClub(finalObject.getString("club"));
                    post.setContact(finalObject.getInt("contact"));
                    post.setEnddate(finalObject.getString("enddate"));
                    post.setPostedby(finalObject.getInt("postedby"));
                    post.setPostpic(finalObject.getString("postpic"));
                    post.setShortdesc(finalObject.getString("shortdesc"));
                    post.setStartdate(finalObject.getString("startdate"));

                    tobemoderatedarray.add(post);
                }
            } catch (Exception e) {
                Log.e("JSON", "Exception parsing", e);
            }

            return tobemoderatedarray;
        }

        @Override
        protected void onPostExecute(List<Posts> tobemoderatedarray) {
            super.onPostExecute(tobemoderatedarray);

            ModerateAdapter adapter = new ModerateAdapter(getApplicationContext(), R.layout.postmoderation, tobemoderatedarray);
            tobemoderatedposts.setAdapter(adapter);
        }
    }

    public static String getJsonModerateData(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d("JSON", "Getting JSON data");
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
        } catch (Exception e) {
            Log.e("HTTPError", "Exception", e);
        } finally {
            Log.d("JSON DATA", "Disconnecting");
            urlConnection.disconnect();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            new GetModeratePosts().execute("Execute");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ModerateAdapter extends ArrayAdapter {

        private List<Posts> moderatelist;
        private int resource;
        private LayoutInflater inflater;
        ViewHolder holder = null;

        public ModerateAdapter(Context context, int resource, List<Posts> objects) {
            super(context, resource, objects);
            moderatelist = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Log.d("Moderateposts", "Inside getView");

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.postimage = (ImageView) convertView.findViewById(R.id.dPostimage);
                holder.title = (TextView) convertView.findViewById(R.id.dTitle);
                holder.location = (TextView) convertView.findViewById(R.id.dLocation);
                //holder.postedby = (TextView)convertView.findViewById(R.id.postedby);
                holder.Club = (TextView) convertView.findViewById(R.id.dClub);
                holder.startdate = (TextView) convertView.findViewById(R.id.dStartdate);
                holder.starttime = (TextView) convertView.findViewById(R.id.dStarttime);
                holder.contact = (TextView) convertView.findViewById(R.id.dContact);
                holder.Description = (TextView) convertView.findViewById(R.id.dDescription);

                holder.approve = (Button) convertView.findViewById(R.id.dApproved);
                holder.reject = (Button) convertView.findViewById(R.id.dRejected);

                holder.linearLayout = (LinearLayout)convertView.findViewById(R.id.linearlayoutid);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Log.d("Moderating posts", "Assigning values");

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarModerate);

            ImageLoader.getInstance().displayImage(moderatelist.get(position).getPostpic(), holder.postimage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            Log.d("Moderate Adapter", "Setting values to views");

            holder.title.setText("Title: " + moderatelist.get(position).getTitle());
            //holder.location.setText("Location: " + moderatelist.get(position).getLocation());
            holder.startdate.setText("Start Date: " + moderatelist.get(position).getStartdate());
            //holder.starttime.setText("Start Time: " + moderatelist.get(position).getStarttime());
            holder.contact.setText("Contact: " + String.valueOf(moderatelist.get(position).getContact()));
            holder.Description.setText("Description\n" + moderatelist.get(position).getShortdesc());

            holder.approve.setTag(moderatelist.get(position).getPostid());
            holder.reject.setTag(moderatelist.get(position).getPostid());

            holder.linearLayout = (LinearLayout)convertView.findViewById(R.id.linearlayoutid);

            holder.approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
                    String moderator = prefs.getString("username", "");
                    String moderatorauth = prefs.getString("password", "");

                    Uri builtUri = Uri.parse("http://campusdiaries.pythonanywhere.com").buildUpon()
                            .appendPath("approvepost")
                            .appendQueryParameter("moderator", moderator)
                            .appendQueryParameter("modpass", moderatorauth)
                            .appendQueryParameter("postid", holder.approve.getTag().toString())
                            .build();

                    URL url = null;
                    try {
                        url = new URL(builtUri.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    Log.d("Passing URL:", url.toString());

                    new getModerationConfirmation().execute(url);

                    //v.setVisibility(GONE);
                    holder.linearLayout.setVisibility(View.GONE);


                    Log.d("Pressed approved", holder.approve.getTag().toString());
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences prefs = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
                    String moderator = prefs.getString("username", "");
                    String moderatorauth = prefs.getString("password", "");

                    Uri builtUri = Uri.parse("http://campusdiaries.pythonanywhere.com").buildUpon()
                            .appendPath("rejectpost")
                            .appendQueryParameter("moderator", moderator)
                            .appendQueryParameter("modpass", moderatorauth)
                            .appendQueryParameter("postid", holder.approve.getTag().toString())
                            .build();

                    URL url = null;
                    try {
                        url = new URL(builtUri.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    new getModerationConfirmation().execute(url);

                    //v.setVisibility(GONE);
                    holder.linearLayout.setVisibility(View.GONE);

                    Log.d("Pressed rejected", holder.reject.getTag().toString());
                }
            });


            return convertView;
        }

        class ViewHolder {
            private ImageView postimage;
            private TextView title;
            private TextView location;
            private TextView Club;
            private TextView startdate;
            private TextView starttime;
            private TextView contact;
            private TextView Description;
            private Button approve;
            private Button reject;
            LinearLayout linearLayout;
        }
    }

    public class getModerationConfirmation extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String response = null;
            Log.d("Inside getmod", searchUrl.toString());
            try {
                Log.d("URL: ", searchUrl.toString());
                response = getResponseFromHttpUrl(searchUrl);
            } catch (Exception e) {
                Log.e("MYAPP", "Exception", e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("What it returned", response.toString());
            if (response != null && !response.equals("")) {
                if (response.equals("Approved")) {
                    Toast.makeText(getApplicationContext(), "Post Approved",
                            Toast.LENGTH_SHORT).show();
                }
                if (response.equals("Rejected")) {
                    Toast.makeText(getApplicationContext(), "Post Rejected",
                            Toast.LENGTH_SHORT).show();
                }
                if(response.equals("Failed")){
                    Toast.makeText(getApplicationContext(), "Moderation failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d("LoginURL","Inside Call");
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
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
