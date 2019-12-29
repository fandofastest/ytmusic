package com.example.ytmusic;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private List<SongModel> listTracks;
    private Context thisContext;
    private AdapterView.OnItemClickListener thisOnItemClickListener;


    public SongAdapter(Context context, List<SongModel> tracks) {
        thisContext = context;
        listTracks = tracks;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        thisOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView judul;
        private TextView duration;
        private ImageView imageView;
        private TextView subjudul;


        ViewHolder(View v) {
            super(v);
            judul = v.findViewById(R.id.judul);
            duration = v.findViewById(R.id.duration);
            imageView = v.findViewById(R.id.image);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (thisOnItemClickListener != null) {
                thisOnItemClickListener.onItemClick(null, v, getLayoutPosition(), 0);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemsong, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SongModel tracks = listTracks.get(position);
        holder.judul.setText(tracks.getSongtitle());
        Glide.with(thisContext).load(tracks.getSongimage()).error(R.drawable.ic_launcher_background).into(holder.imageView);

//        String dur = new String(tracks.getduration());
//        long time = Long.parseLong(dur);
//        String data = new SimpleDateFormat("mm:ss").format(new Date(time));
//        holder.duration.setText(data);


    }

    @Override
    public int getItemCount() {
        return listTracks.size();
    }
}