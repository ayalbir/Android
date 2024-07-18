package com.example.myyoutube.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myyoutube.screens.AddEditVideoActivity;
import com.example.myyoutube.screens.MainActivity;
import com.example.myyoutube.R;
import com.example.myyoutube.screens.UserVideosActivity;
import com.example.myyoutube.screens.VideoPlayerActivity;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.viewmodels.UserManager;
import com.example.myyoutube.viewmodels.VideosViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private User currentUser = MainActivity.getCurrentUser();
    private VideosViewModel videosViewModel;
    private UserManager userManager = UserManager.getInstance();


    static class VideoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvViews, tvTitle, tvChannel, tvTimeAgo;
        private final ImageView thumbnail, ivChannelPhoto;
        private final ImageButton overflowMenu;
        private final View channelLayout;

        private VideoViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvViews = view.findViewById(R.id.tvViews);
            thumbnail = view.findViewById(R.id.ivPic);
            tvChannel = view.findViewById(R.id.tvChannel);
            overflowMenu = view.findViewById(R.id.overflowMenu);
            ivChannelPhoto = view.findViewById(R.id.ivChannelPhoto);
            channelLayout= view.findViewById(R.id.channelLayout);
            tvTimeAgo= view.findViewById(R.id.tvTimeAgo);
        }
    }

    private final LayoutInflater mInflater;
    private final Context mContext;
    private List<Video> videos;
    private List<Video> videosFull = new ArrayList<>();

    public VideoListAdapter(Context context, VideosViewModel videosViewModel) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.videosViewModel = videosViewModel;
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
            User user = userManager.getUserByEmail(video.getEmail());
            holder.tvChannel.setText(Objects.requireNonNull(user.getFirstName()));
            holder.tvTimeAgo.setText(video.getTimeAgo());

            // Check if the pic is in the drawable resources
            int imageResId = mContext.getResources().getIdentifier(video.getPic(), "drawable", mContext.getPackageName());
            if (imageResId != 0) {
                // Image is in the drawable resources
                holder.thumbnail.setImageResource(imageResId);
            } else {
                // Image is from the gallery
                byte[] decodedString = Base64.decode(video.getPic(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.thumbnail.setImageBitmap(decodedByte);
            }
            String profileImageBase64 = user.getProfileImage();
            if (profileImageBase64 != null) {
                byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.ivChannelPhoto.setImageBitmap(decodedByte);
            }

            holder.channelLayout.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, UserVideosActivity.class);
                intent.putExtra("userEmail", video.getEmail());
                mContext.startActivity(intent);
            });
            holder.itemView.setOnClickListener(view -> {
                video.incrementViews();
                holder.tvViews.setText(String.valueOf(video.getViews()));

                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                intent.putExtra("videoId", video.getId());
                ((Activity) mContext).startActivityForResult(intent, 1);
            });

            holder.overflowMenu.setOnClickListener(view -> {
                if(currentUser != null){
                    if(currentUser.getEmail().equals(video.getEmail())){
                        showEditDeleteDialog(video, position);
                    }
                    else {
                        Toast.makeText(holder.itemView.getContext(), "You can only edit or delete your own videos", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(mContext, "User not connected", Toast.LENGTH_SHORT).show();
            });
        }
        else {
            videos = new ArrayList<>();
        }
    }

    private void showEditDeleteDialog(Video video, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Select Action");
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
            switch (which) {
                case 0: // Edit
                    if (currentUser != null) {
                        Intent intent = new Intent(mContext, AddEditVideoActivity.class);
                        intent.putExtra("videoId", video.getId());
                        ((Activity) mContext).startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_VIDEO);
                    } else {
                        Toast.makeText(mContext, "User not connected", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1: // Delete
                    if (currentUser != null) {
                        videosViewModel.delete(video);
                        removeItem(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "User not connected", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
        builder.show();
    }
    public void setVideos(List<Video> v) {
        videos = v;
        videosFull = new ArrayList<>(v);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, videos.size());
    }

    public void addItem(Video video) {
        if (!videos.contains(video)) {
            videos.add(video);
            videosFull.add(video);
            notifyItemInserted(videos.size() - 1);
        }
    }

    public void updateItem(Video updatedVideo) {
        for (int i = 0; i < videos.size(); i++) {
            if (Objects.equals(videos.get(i).getId(), updatedVideo.getId())) {
                videos.set(i, updatedVideo);
                videosFull.set(i, updatedVideo);
                notifyItemChanged(i);
                break;
            }
        }
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
                if (video.getTitle().toLowerCase().contains(text) || video.getEmail().toLowerCase().contains(text)) {
                    videos.add(video);
                }
            }
        }
        notifyDataSetChanged();
    }
}
