package com.example.myyoutube.classes;

public class Comment {
    private String commentContent;
    private String commentPic;

    public Comment(String commentContent, String commentPic) {
        this.commentContent = commentContent;
        this.commentPic = commentPic;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentPic() {
        return commentPic;
    }

    public void setCommentPic(String commentPic) {
        this.commentPic = commentPic;
    }
}
