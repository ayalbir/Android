package com.example.myyoutube.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myyoutube.AddEditVideoActivity;
import com.example.myyoutube.MainActivity;
import com.example.myyoutube.R;
import com.example.myyoutube.VideoPlayerActivity;
import com.example.myyoutube.classes.Video;
import com.example.myyoutube.classes.VideoManager;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvViews, tvTitle, tvChannel;
        private final ImageView thumbnail;
        private final ImageButton btnEditVideo, btnDeleteVideo;

        private VideoViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvViews = view.findViewById(R.id.tvViews);
            thumbnail = view.findViewById(R.id.ivPic);
            tvChannel = view.findViewById(R.id.tvChannel);
            btnEditVideo = view.findViewById(R.id.IBEdit);
            btnDeleteVideo = view.findViewById(R.id.IBDelete);
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
        if (videos != null) {
            Video video = videos.get(position);
            holder.tvTitle.setText(video.getTitle());
            holder.tvViews.setText(String.valueOf(video.getViews()));
            holder.tvChannel.setText(video.getChannel());
            int imageResId = mContext.getResources().getIdentifier(video.getThumbnail(), "drawable", mContext.getPackageName());
            holder.thumbnail.setImageResource(imageResId);

            holder.itemView.setOnClickListener(view -> {
                video.incrementViews();
                holder.tvViews.setText(String.valueOf(video.getViews()));

                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                intent.putExtra("videoId", video.getId());
                ((Activity) mContext).startActivityForResult(intent, 1);
            });

            holder.btnEditVideo.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, AddEditVideoActivity.class);
                intent.putExtra("videoId", video.getId());
                ((Activity) mContext).startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_VIDEO);
            });

            holder.btnDeleteVideo.setOnClickListener(view -> {
                VideoManager.getVideoManager().removeVideo(video);
                removeItem(position);
            });
        }
    }

    public void setVideos(List<Video> v) {
        videos = v;
        videosFull = new ArrayList<>(v);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        //videos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, videos.size());
    }


    public void addItem(Video video) {
        videos.add(video);
        videosFull.add(video);
        notifyItemInserted(videos.size() - 1);
    }

    public void updateItem(int position, Video video) {
        videos.set(position, video);
        videosFull.set(position, video);
        notifyItemChanged(position);
    }
    @Override
    public int getItemCount() {
        return videos != null ? videos.size() : 0;
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
}