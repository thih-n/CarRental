package com.carrental.entity;

import java.sql.Timestamp;

public class Review {
    private int reviewID;
    private int contractID;
    private int userID;
    private String targetType;
    private int targetID;
    private Integer rating;
    private String comment;
    private String staffReply;
    private Integer repliedBy;
    private Timestamp replyDate;
    private Timestamp createdAt;

    public Review() {
    }

    public Review(int reviewID, int contractID, int userID, String targetType, int targetID, Integer rating,
            String comment, String staffReply, Integer repliedBy, Timestamp replyDate, Timestamp createdAt) {
        this.reviewID = reviewID;
        this.contractID = contractID;
        this.userID = userID;
        this.targetType = targetType;
        this.targetID = targetID;
        this.rating = rating;
        this.comment = comment;
        this.staffReply = staffReply;
        this.repliedBy = repliedBy;
        this.replyDate = replyDate;
        this.createdAt = createdAt;
    }

    public int getReviewID() { return reviewID; }
    public void setReviewID(int reviewID) { this.reviewID = reviewID; }

    public int getContractID() { return contractID; }
    public void setContractID(int contractID) { this.contractID = contractID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public int getTargetID() { return targetID; }
    public void setTargetID(int targetID) { this.targetID = targetID; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStaffReply() { return staffReply; }
    public void setStaffReply(String staffReply) { this.staffReply = staffReply; }

    public Integer getRepliedBy() { return repliedBy; }
    public void setRepliedBy(Integer repliedBy) { this.repliedBy = repliedBy; }

    public Timestamp getReplyDate() { return replyDate; }
    public void setReplyDate(Timestamp replyDate) { this.replyDate = replyDate; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
