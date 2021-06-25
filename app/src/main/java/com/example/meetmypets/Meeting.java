package com.example.meetmypets;

import android.graphics.Bitmap;

import java.util.List;

public class Meeting {
    String meetingId;
    String meetingName;
    String meetingDescription;
    List<String> subscribedUserIds;
    List<String> usersNoNotifications;
    Bitmap meetingImage;
    int distance;
    String adminId;
    Boolean enablePassword;
    String meetingPassword;

    public Meeting(String meetingName, List<String> subscribedUserIds, int distance) {
        this.meetingName = meetingName;
        this.subscribedUserIds = subscribedUserIds;
        this.distance = distance;
    }

//    public Meeting(String meetingId, String meetingName, String meetingDescription, List<String> subscribedUserIds,
//                   List<String> usersNoNotifications, String adminId, Boolean enablePassword, String meetingPassword) {
//        this.meetingId = meetingId;
//        this.meetingName = meetingName;
//        this.meetingDescription = meetingDescription;
//        this.subscribedUserIds = subscribedUserIds;
//        this.usersNoNotifications = usersNoNotifications;
//        this.adminId = adminId;
//        this.enablePassword = enablePassword;
//        this.meetingPassword = meetingPassword;
//    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public String getMeetingDescription() {
        return meetingDescription;
    }

    public List<String> getSubscribedUserIds() {
        return subscribedUserIds;
    }

    public List<String> getUsersNoNotifications() {
        return usersNoNotifications;
    }

    public String getAdminId() {
        return adminId;
    }

    public Boolean getEnablePassword() {
        return enablePassword;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public void setMeetingDescription(String meetingDescription) {
        this.meetingDescription = meetingDescription;
    }

    public void setSubscribedUserIds(List<String> subscribedUserIds) {
        this.subscribedUserIds = subscribedUserIds;
    }

    public void setUsersNoNotifications(List<String> usersNoNotifications) {
        this.usersNoNotifications = usersNoNotifications;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setEnablePassword(Boolean enablePassword) {
        this.enablePassword = enablePassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }
}
