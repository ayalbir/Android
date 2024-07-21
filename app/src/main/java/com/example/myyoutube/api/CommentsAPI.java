package com.example.myyoutube.api;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.repositories.CommentRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentsAPI {
    Retrofit retrofit;
    CommentsAPIService commentsAPIService;
    private CommentRepository.CommentListData commentListData;

    public CommentsAPI(CommentRepository.CommentListData commentListData) {

        this.commentListData = commentListData;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Add the JWT token to the request headers
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + TokenService.getInstance().getToken())
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build()).build();

        commentsAPIService = retrofit.create(CommentsAPIService.class);
    }

    public void getCommentsForVideo(String videoId) {
        Call<ArrayList<JsonObject>> call = commentsAPIService.getCommentsByVideoId(videoId);
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
                            Comment comment = new Comment(videoId,"", text, profilePicture, email);
                            comment.set_id(id);
                            comments.add(comment);
                        }
                        commentListData.postValue(comments);
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

    public void addComment(Comment comment, String token) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("text", comment.getText());
            requestBodyJson.put("profilePicture", comment.getProfilePicture());
            requestBodyJson.put("email", comment.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = commentsAPIService.addComment(comment.getVideoId(), (JsonObject) JsonParser.parseString(requestBodyJson.toString()), "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        JsonObject json = response.body();
                        if (json != null) {
                            comment.set_id(json.get("_id").getAsString());
                            commentListData.addComment(comment);
                        }
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CommentsAPI", t.getLocalizedMessage());
            }
        });
    }

    public void updateComment(Comment comment, String token) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("text", comment.getText());
            requestBodyJson.put("profilePicture", comment.getProfilePicture());
            requestBodyJson.put("email", comment.getEmail());
            requestBodyJson.put("videoId", comment.getVideoId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Object jsonParser = JsonParser.parseString(requestBodyJson.toString());
        Call<JsonObject> call = commentsAPIService.updateComment(comment.get_id(), (JsonObject) jsonParser, "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        commentListData.updateComment(comment);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CommentsAPI", t.getLocalizedMessage());
            }
        });
    }

    public void deleteComment(Comment comment, String token) {
        Call<JsonObject> call = commentsAPIService.deleteComment(comment.getVideoId(),comment.get_id(), "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        commentListData.removeComment(comment.get_id());
                    }).start();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CommentsAPI", t.getLocalizedMessage());
            }
        });
    }
}

