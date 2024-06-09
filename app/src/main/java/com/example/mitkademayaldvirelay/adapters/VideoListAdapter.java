package com.example.mitkademayaldvirelay.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitkademayaldvirelay.R;
import com.example.mitkademayaldvirelay.VideoPlayerActivity;
import com.example.mitkademayaldvirelay.classes.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvViews, tvTitle, tvChannel;
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
    private final Context mContext;
    private List<Video> videos = new ArrayList<>();
    private List<Video> videosFull = new ArrayList<>();

    public VideoListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.video_layout, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        if (videos != null && position < videos.size()) {
            Video video = videos.get(position);
            holder.tvTitle.setText(video.getTitle());
            holder.tvViews.setText(String.valueOf(video.getViews()));
            holder.tvChannel.setText(video.getChannel());
            int imageResId = mContext.getResources().getIdentifier(video.getThumbnail(), "drawable", mContext.getPackageName());
            holder.thumbnail.setImageResource(imageResId);
            holder.itemView.setOnClickListener(view -> {
                video.incrementViews();
                holder.tvViews.setText(String.valueOf(video.getViews()));

                // Start the VideoPlayerActivity with the video ID
                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                intent.putExtra("videoId", video.getId());
                if (mContext instanceof Activity) {
                    ((Activity) mContext).startActivityForResult(intent, 1);
                } else {
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void setVideos(List<Video> v) {
        this.videos = new ArrayList<>(v);
        this.videosFull = new ArrayList<>(v);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (videos != null)
            return videos.size();
        return 0;
    }

    public void filter(String text) {
        videos.clear();
        if (text.isEmpty()) {
            videos.addAll(videosFull);
        } else {
            text = text.toLowerCase();
            for (Video video : videosFull) {
                if (video.getTitle().toLowerCase().contains(text) || video.getChannel().toLowerCase().contains(text)) {
                    videos.add(video);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Video> getVideos() {
        return videos;
    }
}
