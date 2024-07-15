package com.example.myyoutube.api;

import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.repositories.CommentRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentsAPI {
    Retrofit retrofit;
    CommentsAPIService commentsAPIService;

    public CommentsAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        commentsAPIService = retrofit.create(CommentsAPIService.class);
    }

    public void fetchCommentsByVideoId(int videoId, MutableLiveData<List<Comment>> commentsLiveData) {
        Call<ArrayList<JsonObject>> call = commentsAPIService.getCommentsByVideoId(videoId);
        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                if (response.isSuccessful()) {
                    ArrayList<JsonObject> jsonCommentsList = response.body();
                    if (jsonCommentsList != null) {
                        List<Comment> comments = new ArrayList<>();
                        for (JsonObject jsonComment : jsonCommentsList) {
                            String commentId = (jsonComment.get("_id").getAsString());
                            String content = jsonComment.get("commentContent").getAsString();
                            String pic = jsonComment.get("commentPic").getAsString();
                            String publisher = jsonComment.get("commentPublisher").getAsString();
                            Comment comment = new Comment(commentId, content, pic, publisher);
                            comments.add(comment);
                        }
                        commentsLiveData.postValue(comments);
                    }
                } else {
                    Toast.makeText(Helper.context, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.e("CommentsAPI", t.getLocalizedMessage());
                Toast.makeText(Helper.context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addComment(Comment comment, MutableLiveData<String> messageLiveData) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("commentContent", comment.getText());
            requestBodyJson.put("commentPic", comment.getProfilePicture());
            requestBodyJson.put("commentPublisher", comment.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = commentsAPIService.addComment((JsonObject) JsonParser.parseString(requestBodyJson.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("insertedId")) {
                        messageLiveData.postValue("Comment added successfully");
                    } else {
                        messageLiveData.postValue("Failed to add comment");
                    }
                } else {
                    messageLiveData.postValue("Failed to add comment");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CommentsAPI", t.getLocalizedMessage());
                messageLiveData.postValue("Network error");
            }
        });
    }

    public void updateComment(Comment comment, MutableLiveData<String> messageLiveData) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("commentContent", comment.getText());
            requestBodyJson.put("commentPic", comment.getProfilePicture());
            requestBodyJson.put("commentPublisher", comment.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = commentsAPIService.updateComment(Integer.parseInt(comment.getVideoId()), (JsonObject) JsonParser.parseString(requestBodyJson.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("modifiedCount")) {
                        messageLiveData.postValue("Comment updated successfully");
                    } else {
                        messageLiveData.postValue("Failed to update comment");
                    }
                } else {
                    messageLiveData.postValue("Failed to update comment");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CommentsAPI", t.getLocalizedMessage());
                messageLiveData.postValue("Network error");
            }
        });
    }

    public void deleteComment(int commentId, MutableLiveData<String> messageLiveData) {
        Call<JsonObject> call = commentsAPIService.deleteComment(commentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("deletedCount")) {
                        messageLiveData.postValue("Comment deleted successfully");
                    } else {
                        messageLiveData.postValue("Failed to delete comment");
                    }
                } else {
                    messageLiveData.postValue("Failed to delete comment");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CommentsAPI", t.getLocalizedMessage());
                messageLiveData.postValue("Network error");
            }
        });
    }

    public void getCommentsByVideoId(int videoId, MutableLiveData<List<Comment>> commentsLiveData) {
        Call<ArrayList<JsonObject>> call = commentsAPIService.getCommentsByVideoId(videoId);
        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                if (response.isSuccessful()) {
                    ArrayList<JsonObject> jsonCommentsList = response.body();
                    if (jsonCommentsList != null) {
                        List<Comment> comments = new ArrayList<>();
                        for (JsonObject jsonComment : jsonCommentsList) {
                            String id = jsonComment.get("id").getAsString();
                            String content = jsonComment.get("content").getAsString();
                            String publisher = jsonComment.get("publisher").getAsString();
                            String profileImage = jsonComment.get("profileImage").getAsString();
                            Comment comment = new Comment(id,content, profileImage, publisher);
                            comments.add(comment);
                        }
                        commentsLiveData.postValue(comments);
                    }
                } else {
                    Toast.makeText(Helper.context, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.e("VideoAPI", t.getLocalizedMessage());
                Toast.makeText(Helper.context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
