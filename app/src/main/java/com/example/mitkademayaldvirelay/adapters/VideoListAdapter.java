package com.example.mitkademayaldvirelay.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mitkademayaldvirelay.R;
import com.example.mitkademayaldvirelay.classes.Video;

import java.util.List;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    class VideoViewHolder extends RecyclerView.ViewHolder{
        private TextView tvViews, tvTitle, tvChannel;
        private final ImageView thumbnail;
        private VideoViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvViews = view.findViewById(R.id.tvViews);
            thumbnail = view.findViewById(R.id.ivPic);
            tvChannel = view.findViewById(R.id.tvChannel);
        }
    }

    private final LayoutInflater mInflater;
    private List<Video> videos;

    public VideoListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.video_layout,parent,false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        if (videos != null) {
            Video video = videos.get(position);
            holder.tvTitle.setText(video.getTitle());
            holder.tvViews.setText(String.valueOf(video.getViews()));
            holder.tvChannel.setText(video.getChannel());
            holder.thumbnail.setImageResource(video.getThumbnail());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: intent to the video itself
                }
            });
        }

    }

    public void setVideos(List<Video> v) {
        videos = v;
        notifyDataSetChanged();;
    }

    @Override
    public int getItemCount() {
        if (videos != null)
            return videos.size();
        return 0;
    }


    public List<Video> getVideos() {
        return videos;
    }
}
