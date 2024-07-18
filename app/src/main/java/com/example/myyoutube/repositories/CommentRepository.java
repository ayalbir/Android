package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myyoutube.api.CommentsAPI;
import com.example.myyoutube.entities.Comment;

import java.util.LinkedList;
import java.util.List;

public class CommentRepository {
    private final CommentsAPI commentsAPI;
    private final CommentListData commentsLiveData;
    private final String videoId;

    public CommentRepository(String videoId) {
        commentsLiveData = new CommentListData();
        this.videoId = videoId;
        commentsAPI = new CommentsAPI(commentsLiveData);
    }

    public LiveData<List<Comment>> get() {
        return commentsLiveData;
    }

    public void getCommentsForVideo() {
        commentsAPI.getCommentsForVideo(videoId);
    }

    public void addComment(Comment comment, String token) {
        commentsAPI.addComment(comment, token);
    }

    public void deleteComment(Comment comment, String token) {
        commentsAPI.deleteComment(comment, token);
    }

    public void updateComment(Comment comment , String token) {
        commentsAPI.updateComment(comment, token);
    }

    public class CommentListData extends MutableLiveData<List<Comment>> {


        public CommentListData() {
            super();
            setValue(new LinkedList<>());

        }



        @Override
        protected void onActive() {
            super.onActive();

        }


        public void addComment(Comment newComment) {
            List<Comment> comments1 = getValue();
            if (comments1 == null)
                return;
            comments1.add(0, newComment);
            commentsLiveData.postValue(comments1);
        }


        public void removeComment(String id) {
            List<Comment> comments1 = getValue();
            if (comments1 == null)
                return;
            for (Comment comment : comments1) {
                if (comment.get_id().equals(id)) {
                    comments1.remove(comment);
                    commentsLiveData.postValue(comments1);
                    break;
                }
            }
        }


        public void updateComment(Comment comment) {
            List<Comment> comments1 = getValue();
            if (comments1 == null)
                return;
            String commentId = comment.get_id();
            for (Comment curr : comments1) {
                if (curr.get_id().equals(commentId)) {
                    curr.setText(comment.getText());
                    commentsLiveData.postValue(comments1);
                    break;
                }
            }
        }
    }
}