package com.mp3music.newapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mp3music.newapp.R;
import com.mp3music.newapp.Adapter.SongAdapter;
import com.mp3music.newapp.Model.SongModel;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.mp3music.newapp.Activity.SplashActivity.keywords;

public class MainActivity extends AppCompatActivity implements RatingDialogListener {
    public static  String DOWNLOAD_DIRECTORY = "/Downloads" ;
    String q;

    RecyclerView rvsong;
    LinearLayout linearLayout;
    SongAdapter songAdapter;
    SearchView searchView;
    private Context ctx;


    public static String serverurl="https://mp3.fando.id/mp3/get.php?id=";

    public static String KEY="AIzaSyCTOUQFBUuFRMzCmI41Z-EfXkDFwRI7RgE";
     public static List<SongModel> listlagu=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView=findViewById(R.id.searchview);

        linearLayout=findViewById(R.id.layouprogress);
        q=keywords;

        ctx=MainActivity.this;


        rvsong=findViewById(R.id.rvsong);
        rvsong.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rvsong.setHasFixedSize(true);
        rvsong.setNestedScrollingEnabled(false);
        songAdapter = new SongAdapter(MainActivity.this,listlagu);
        rvsong.setAdapter(songAdapter);
        songAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final SongModel songModel = listlagu.get(position);
                String idv =songModel.getSongid();

                if (SplashActivity.statususer.equals("aman")){
                    aman(position);

                }else{
                    tidakaman(idv);

                }



            }
        });


        search_query("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&type=video&q="+q+"&key="+KEY);




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                linearLayout.setVisibility(View.VISIBLE);
                listlagu.clear();

                search_query("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&type=video&q="+query+"&key="+KEY);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });




    }


    private long downloadFile(Uri uri, String fileStorageDestinationUri, String fileName) {

        long downloadReference = 0;

        DownloadManager downloadManager = (DownloadManager)ctx.getSystemService(DOWNLOAD_SERVICE);
        try {
            DownloadManager.Request request = new DownloadManager.Request(uri);

            //Setting title of request
            request.setTitle(fileName);

            //Setting description of request
            request.setDescription("Your file is downloading");

            //set notification when download completed
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(fileStorageDestinationUri, fileName);

            request.allowScanningByMediaScanner();

            //Enqueue download and save the referenceId
            downloadReference = downloadManager.enqueue(request);
        } catch (IllegalArgumentException e) {
            Toast.makeText(ctx,"Download link is broken or not availale for download",Toast.LENGTH_LONG).show();

            Log.e(TAG, "Line no: 455,Method: downloadFile: Download link is broken");

        }
        return downloadReference;
    }


    private void search_query(String url){


        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                linearLayout.setVisibility(View.GONE);



                try {
                    JSONArray jsonArray=response.getJSONArray("items");



                    for (int i = 0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        JSONObject jsonObjectid = jsonObject.getJSONObject("id");

                        JSONObject jsonObjecttitle=jsonObject.getJSONObject("snippet");

                        JSONObject jsonObjectthumbnail=jsonObjecttitle.getJSONObject("thumbnails");
                        JSONObject jsonObjectimage=jsonObjectthumbnail.getJSONObject("default");

                        String imageurl= jsonObjectimage.getString("url");

                        String vid= jsonObjectid.getString("videoId");
                        String title = jsonObjecttitle.getString("title");

//                        Toast.makeText(getApplicationContext(),vid+title+imageurl,Toast.LENGTH_LONG).show();

                        SongModel songModel = new SongModel();
                        songModel.setSongtitle(title);
                        songModel.setSongid(vid);
                        songModel.setSongimage(imageurl);


                        listlagu.add(songModel);
                        songAdapter.notifyDataSetChanged();





                        }





                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


    }


    private void tidakaman(String id){

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+id)));

    }


    private void aman(final int position){

        final SongModel songModel = listlagu.get(position);



        final CharSequence[] items = {"Play", "Download"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Download")) {

                    long downloadFileRef = downloadFile(Uri.parse(serverurl+songModel.getSongid()), DOWNLOAD_DIRECTORY, songModel.getSongtitle()+".mp3");
                    if (downloadFileRef != 0) {
                        Toast.makeText(ctx,"Starting download",Toast.LENGTH_LONG).show();

                    }else {
                        Toast.makeText(ctx,"File is not available for download",Toast.LENGTH_LONG).show();

                    }




                    return;



                } else if (items[item].equals("Play")) {



                    Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                    intent.putExtra("id",songModel.getSongid());
                    intent.putExtra("title",songModel.getSongtitle());
                    intent.putExtra("foto",songModel.getSongimage());
                    intent.putExtra("duration",songModel.getSongdura());
                    intent.putExtra("position",position);

                    startActivity(intent);






                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {
        finish();
        finishAffinity();
        System.exit(0);



    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {


        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }

    }

    @Override
    public void onBackPressed() {



            new AppRatingDialog.Builder()
                    .setPositiveButtonText("Submit")
                    .setNegativeButtonText("Cancel")
                    .setNeutralButtonText("Exit")
                    .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                    .setDefaultRating(2)
                    .setTitle("Rate this application")
                    .setDescription("Please select some stars and give your feedback")
                    .setCommentInputEnabled(true)
                    .setDefaultComment("This app is pretty cool !")
                    .setStarColor(R.color.orange_400)
                    .setNoteDescriptionTextColor(R.color.blue_grey_300)
                    .setTitleTextColor(R.color.colorPrimary)
                    .setDescriptionTextColor(R.color.white)
                    .setHint("Please write your comment here ...")
                    .setHintTextColor(R.color.blue_grey_300)
                    .setCommentTextColor(R.color.white)
                    .setCommentBackgroundColor(R.color.colorPrimaryDark)
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .create(MainActivity.this)
                    .show();



        }



}
