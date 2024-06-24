package com.example.myyoutube.classes;

public class Comment {
    private String commentContent;
    private String commentPic;
    private String commentPublisher;

    public Comment(String commentContent, String commentPic, String commentPublisher) {
        this.commentContent = commentContent;
        this.commentPic = commentPic;
        this.commentPublisher = commentPublisher;
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

    public String getCommentPublisher() {
        return commentPublisher;
    }

    public void setCommentPublisher(String commentPublisher) {
        this.commentPublisher = commentPublisher;
    }
}
