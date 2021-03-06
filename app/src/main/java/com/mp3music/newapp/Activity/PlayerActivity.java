package com.mp3music.newapp.Activity;



import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;


import com.bumptech.glide.Glide;
import com.mp3music.newapp.R;
import com.mp3music.newapp.Model.SongModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static com.mp3music.newapp.Activity.MainActivity.listlagu;
import static com.mp3music.newapp.Activity.MainActivity.serverurl;


public class PlayerActivity extends AppCompatActivity {

    private View parent_view;
    private AppCompatSeekBar seek_song_progressbar;
    private FloatingActionButton bt_play;
    private TextView tv_song_current_duration, tv_song_total_duration,judul;
    private String title,id,imgurl,durasi;
    private ImageView imageView;
    // Media PlayerActivity
    private MediaPlayer mp;
    private ProgressBar progressBar;
    private ImageButton next,prev,rand,repeat;

    private int position,currnetposition;

    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initComponent();
    }



    private void initComponent() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        parent_view = findViewById(R.id.parent_view);
        seek_song_progressbar = (AppCompatSeekBar) findViewById(R.id.seek_song_progressbar);
        bt_play = (FloatingActionButton) findViewById(R.id.bt_play);
        next=findViewById(R.id.bt_next);
        prev=findViewById(R.id.bt_prev);

        // set Progress bar values
        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

        tv_song_current_duration = (TextView) findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = (TextView) findViewById(R.id.tv_song_total_duration);
        imageView=findViewById(R.id.imagefoto);
        judul=findViewById(R.id.judul);
        progressBar=findViewById(R.id.progressBar);



        position=getIntent().getIntExtra("position",0);

        currnetposition=position;
        SongModel songModel = listlagu.get(position);

        title = songModel.getSongtitle();
        id = songModel.getSongid();
        imgurl = songModel.getSongimage();
        durasi= songModel.getSongdura();


        tv_song_total_duration.setText(durasi);
        judul.setText("Please Wait Preparing Your Music");
        Glide.with(getApplicationContext()).load(imgurl).error(R.drawable.ic_launcher_background).into(imageView);




        // Media PlayerActivity
        mp = new MediaPlayer();






        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Changing button image to play button
                bt_play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
            }
        });

        try {

            Uri myUri = Uri.parse(serverurl+id);


            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepareAsync(); //don't use prepareAsync for mp3 playback


        } catch (Exception e) {
            Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onPrepared(MediaPlayer mplayer) {

                progressBar.setVisibility(View.GONE);
                bt_play.setVisibility(View.VISIBLE);

                if (mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button
                    bt_play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                } else {
                    // Resume song
                    mp.start();
                    // Changing button image to pause button
                    bt_play.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    // Updating progress bar
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });

        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                judul.setText(title);

            }
        });




       final MusicUtils utils = new MusicUtils();
        // Listeners
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mp.seekTo(currentPosition);

                // update timer progress again
                mHandler.post(mUpdateTimeTask);
            }
        });
        buttonPlayerAction();
        updateTimerAndSeekbar();


    }

    @SuppressLint("RestrictedApi")
    private void playsong(int position){



        SongModel songModel = listlagu.get(position);

        title = songModel.getSongtitle();
        id = songModel.getSongid();
        imgurl = songModel.getSongimage();
        durasi= songModel.getSongdura();


        mp.stop();
        mp.reset();
        mp.release();

        judul.setText("Please Wait Preparing Your Music");
        Glide.with(getApplicationContext()).load(imgurl).error(R.drawable.ic_launcher_background).into(imageView);


        progressBar.setVisibility(View.VISIBLE);
        bt_play.setVisibility(View.GONE);





        try {

            Uri myUri = Uri.parse(serverurl+id);


            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepareAsync(); //don't use prepareAsync for mp3 playback


        } catch (Exception e) {
            Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onPrepared(MediaPlayer mplayer) {

                progressBar.setVisibility(View.GONE);
                bt_play.setVisibility(View.VISIBLE);

                if (mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button
                    bt_play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                } else {
                    // Resume song
                    mp.start();
                    // Changing button image to pause button
                    bt_play.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    next.setColorFilter(getResources().getColor(R.color.grey_90), PorterDuff.Mode.SRC_ATOP);
                    prev.setColorFilter(getResources().getColor(R.color.grey_90), PorterDuff.Mode.SRC_ATOP);
                    // Updating progress bar
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });

        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                judul.setText(title);

            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Changing button image to play button
                bt_play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
//                currnetposition=currnetposition+1;
//
////               Toast.makeText(getApplicationContext(),String.valueOf(currnetposition),Toast.LENGTH_LONG).show();
//
//                playsong(currnetposition);

            }
        });





    }



    /**
     * Play button click event plays a song and changes button to pause image
     * pauses a song and changes button to play image
     */
    private void buttonPlayerAction() {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button
                    bt_play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                } else {
                    // Resume song
                    mp.start();
                    // Changing button image to pause button
                    bt_play.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    // Updating progress bar
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_repeat: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Repeat", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_shuffle: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Shuffle", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_prev: {
                toggleButtonColor((ImageButton) v);
                currnetposition=currnetposition-1;

//                Toast.makeText(getApplicationContext(),String.valueOf(currnetposition),Toast.LENGTH_LONG).show();

                playsong(currnetposition);

                break;
            }
            case R.id.bt_next: {
                toggleButtonColor((ImageButton) v);
                currnetposition=currnetposition+1;

//               Toast.makeText(getApplicationContext(),String.valueOf(currnetposition),Toast.LENGTH_LONG).show();

                playsong(currnetposition);

//                toggleButtonColor((ImageButton) v);

                break;
            }
        }
    }


    private boolean toggleButtonColor(ImageButton bt) {
        String selected = (String) bt.getTag(bt.getId());
        if (selected != null) { // selected
            bt.setColorFilter(getResources().getColor(R.color.grey_90), PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(), null);
            return false;
        } else {
            bt.setTag(bt.getId(), "selected");
            bt.setColorFilter(getResources().getColor(R.color.red_500), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        // Displaying Total Duration time
        MusicUtils utils = new MusicUtils();
        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        tv_song_current_duration.setText(utils.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }

    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Snackbar.make(parent_view, item.getTitle(), Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }



    public class MusicUtils {

        public static final int MAX_PROGRESS = 10000;

        /**
         * Function to convert milliseconds time to
         * Timer Format
         * Hours:Minutes:Seconds
         */
        public String milliSecondsToTimer(long milliseconds) {
            String finalTimerString = "";
            String secondsString = "";

            // Convert total duration into time
            int hours = (int) (milliseconds / (1000 * 60 * 60));
            int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            // return timer string
            return finalTimerString;
        }

        /**
         * Function to get Progress percentage
         *
         * @param currentDuration
         * @param totalDuration
         */
        public int getProgressSeekBar(long currentDuration, long totalDuration) {
            Double progress = (double) 0;
            // calculating percentage
            progress = (((double) currentDuration) / totalDuration) * MAX_PROGRESS;

            // return percentage
            return progress.intValue();
        }

        /**
         * Function to change progress to timer
         *
         * @param progress - totalDuration
         *                 returns current duration in milliseconds
         */
        public int progressToTimer(int progress, int totalDuration) {
            int currentDuration = 0;
            totalDuration = (int) (totalDuration / 1000);
            currentDuration = (int) ((((double) progress) / MAX_PROGRESS) * totalDuration);

            // return current duration in milliseconds
            return currentDuration * 1000;
        }

    }

}
