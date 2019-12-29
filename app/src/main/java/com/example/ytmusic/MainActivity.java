package com.example.ytmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String q;

    String judul,idsong,imgurl;
    RecyclerView rvsong;

    SongAdapter songAdapter;
    SearchView searchView;

    public static String KEY="AIzaSyCTOUQFBUuFRMzCmI41Z-EfXkDFwRI7RgE";
     List<SongModel> listlagu=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView=findViewById(R.id.searchview);


        q="Via Vallen";




        rvsong=findViewById(R.id.rvsong);
        rvsong.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rvsong.setHasFixedSize(true);
        rvsong.setNestedScrollingEnabled(false);
        songAdapter = new SongAdapter(MainActivity.this,listlagu);
        rvsong.setAdapter(songAdapter);
        songAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SongModel songModel = listlagu.get(position);

                        Intent intent = new Intent(MainActivity.this,Player.class);
                        intent.putExtra("id",songModel.getSongid());
                        intent.putExtra("title",songModel.getSongtitle());
                        intent.putExtra("foto",songModel.getSongimage());
                        intent.putExtra("duration",songModel.getSongdura());

                        startActivity(intent);



            }
        });


        search_query("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&type=video&q="+q+"&key="+KEY);




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


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


    private void search_query(String url){


        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



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

}
