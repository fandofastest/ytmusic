package com.reborn.music.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.reborn.music.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG ="mytags" ;
    Button startbutton;
    ProgressBar progressBar;
    LinearLayout layutprogressbar;


    InterstitialAd fanInterstitialAd;
    private com.google.android.gms.ads.InterstitialAd mInterstitialAd;

    public static String keywords,statususer,newpaket,startappid,statusapp,adMobBannerId,adMobInterstitialId,adMobPublisherId,fanadStatus,fanBannerid,fanInterid,startappstatus,termsurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__splash);
        layutprogressbar=findViewById(R.id.llProgressBar);
        startbutton=findViewById(R.id.startbut);
        progressBar=findViewById(R.id.progressbar1);
//        startbutton.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {
            }
        } else {
        }

        getAdDetails(getString(R.string.jsonaja));
//        startbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
//                startActivity(intent);
//            }
//        });


    }


    private void getAdDetails(String url){



        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



                try {
                    JSONObject jsonObject=response.getJSONObject("jsondata");
                    statusapp=jsonObject.getString("statusapp");
                    statususer=jsonObject.getString("statususer");
                    startappid=jsonObject.getString("startappid");
                    fanBannerid = jsonObject.getString("fan_banner_ads_id");
                    fanInterid = jsonObject.getString("fan_interstitial_ads_id");
                    newpaket = jsonObject.getString("pack_name");
                    adMobBannerId = jsonObject.getString("admob_banner_ads_id");
                    adMobInterstitialId = jsonObject.getString("admob_interstitial_ads_id");
                    adMobPublisherId = jsonObject.getString("app_id");
                    keywords= jsonObject.getString("keywords");

//                    Toast.makeText(getContext(),ApiResources.fanadStatus+ApiResources.fanBannerid+ApiResources.fanInterid , Toast.LENGTH_LONG).show();
//
//
                    if (!statusapp.equals("hidup")){
                        startbutton.setText("UPDATE APP");
                        startbutton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        startbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+newpaket));
                                startActivity(intent);
                            }
                        });
                    }



                    if (!fanInterid.equals("")){
                        startbutton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        startbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                layutprogressbar.setVisibility(View.VISIBLE);
                                startbutton.setVisibility(View.GONE);

                                loadinter(fanInterid);
                            }
                        });
                    }


//                    new GDPRChecker()
//                            .withContext(getApplicationContext())
//                            .withPrivacyUrl(termsurl) // your privacy url
//                            .withPublisherIds(adMobPublisherId) // your admob account Publisher id
//                            .check();









                } catch (JSONException e) {
                    Log.e("json", "ERROR");
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", String.valueOf(error));


            }
        });

        Volley.newRequestQueue(SplashActivity.this).add(jsonObjectRequest);





    }


    public void loadinteradmob(String inter){

        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAd.setAdUnitId(inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                layutprogressbar.setVisibility(View.GONE);

                mInterstitialAd.show();

                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                StartAppAd startAppAd = new StartAppAd(getApplicationContext());
                startAppAd.showAd(new AdDisplayListener() {
                    @Override
                    public void adHidden(com.startapp.android.publish.adsCommon.Ad ad) {
                        layutprogressbar.setVisibility(View.GONE);

                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);

                        startActivity(intent);

                    }

                    @Override
                    public void adDisplayed(com.startapp.android.publish.adsCommon.Ad ad) {

                    }

                    @Override
                    public void adClicked(com.startapp.android.publish.adsCommon.Ad ad) {
                        layutprogressbar.setVisibility(View.GONE);

                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);

                        startActivity(intent);

                    }

                    @Override
                    public void adNotDisplayed(com.startapp.android.publish.adsCommon.Ad ad) {

                        layutprogressbar.setVisibility(View.GONE);

                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);

                        startActivity(intent);

                    }
                })   ;


//                final Handler   handler = new Handler();
//
//                final Runnable r = new Runnable() {
//                    public void run() {
//                        StartAppAd.showAd(getContext());
//
//                        layutprogressbar.setVisibility(View.GONE);
//
//                        Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);
//
//                        startActivity(intent);
//                    }
//                };
//
//                handler.postDelayed(r, 3000);



                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                layutprogressbar.setVisibility(View.GONE);

                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                layutprogressbar.setVisibility(View.GONE);


                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                layutprogressbar.setVisibility(View.GONE);

                Intent intent = new Intent(SplashActivity.this,MainActivity.class);

                startActivity(intent);

                // Code to be executed when the interstitial ad is closed.
            }
        });
    }


    public void loadinter (String inter){

        fanInterstitialAd = new InterstitialAd(this,inter);

        fanInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

                layutprogressbar.setVisibility(View.GONE);
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                layutprogressbar.setVisibility(View.GONE);

                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");

                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(Ad ad, AdError adError) {

                loadinteradmob(adMobInterstitialId);


                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                layutprogressbar.setVisibility(View.GONE);

                fanInterstitialAd.show();



                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad

            }

            @Override
            public void onAdClicked(Ad ad) {
                layutprogressbar.setVisibility(View.GONE);

                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        fanInterstitialAd.loadAd();
    }




}
