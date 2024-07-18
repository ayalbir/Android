package com.example.myyoutube.api;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.dao.CommentDao;
import com.example.myyoutube.entities.Comment;
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
    private CommentDao commentDao;

    public CommentsAPI(CommentDao commentDao) {
        this.commentDao = commentDao;
        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        commentsAPIService = retrofit.create(CommentsAPIService.class);
    }

    public void fetchCommentsByVideoId(String videoId, MutableLiveData<List<Comment>> commentsLiveData) {
        Call<ArrayList<JsonObject>> call = commentsAPIService.getCommentsByVideoId(Integer.parseInt(videoId));
        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                if (response.isSuccessful()) {
                    ArrayList<JsonObject> jsonCommentsList = response.body();
                    if (jsonCommentsList != null) {
                        List<Comment> comments = new ArrayList<>();
                        for (JsonObject jsonComment : jsonCommentsList) {
                            String id = jsonComment.get("_id").getAsString();
                            String text = jsonComment.get("text").getAsString();
                            String profilePicture = jsonComment.get("profilePicture").getAsString();
                            String email = jsonComment.get("email").getAsString();
                            String videoId = jsonComment.get("videoId").getAsString();
                            Comment comment = new Comment(videoId,id, text, profilePicture, email);
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
            requestBodyJson.put("text", comment.getText());
            requestBodyJson.put("profilePicture", comment.getProfilePicture());
            requestBodyJson.put("email", comment.getEmail());
            requestBodyJson.put("videoId", comment.getVideoId());
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
                        new Thread(() -> commentDao.insert(comment)).start();
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
            requestBodyJson.put("text", comment.getText());
            requestBodyJson.put("profilePicture", comment.getProfilePicture());
            requestBodyJson.put("email", comment.getEmail());
            requestBodyJson.put("videoId", comment.getVideoId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = commentsAPIService.updateComment(comment.getVideoId(), (JsonObject) JsonParser.parseString(requestBodyJson.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("modifiedCount")) {
                        new Thread(() -> commentDao.update(comment)).start();
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

    public void deleteComment(String commentId, MutableLiveData<String> messageLiveData) {
        Call<JsonObject> call = commentsAPIService.deleteComment(commentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("deletedCount")) {
                        new Thread(() -> commentDao.deleteById(commentId)).start();
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
}
