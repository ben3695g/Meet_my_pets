package com.example.meetmypets.model;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Meeting {
    String meetingId;
    String meetingName;
    String meetingDescription;
    List<String> subscribedUserIds;
    List<String> usersNoNotifications;
    Bitmap meetingImage;
    LatLng meetingLocation;
    double distance;
    String adminId;
    Boolean enablePassword;
    String meetingPassword;
    List<String> subscribedUsers;

    public Meeting(String meetingId, String meetingName, String meetingDescription, List<String> subscribedUserIds, List<String> subscribedUsers, double distance, LatLng meetingLocation) {
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.subscribedUserIds = subscribedUserIds;
        this.meetingLocation = meetingLocation;
        this.distance = distance;
        this.meetingDescription=meetingDescription;
        this.subscribedUsers = subscribedUsers;
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

    public LatLng getMeetingLocation() {
        return meetingLocation;
    }

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

    public  List<String> getSubscribedUsers() {
        return subscribedUsers;
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

    public double getDistance() {
        return distance;
    }

    public String getFormattedDistance(){
        return String.format("%.2f", getDistance()) + " km";
    }

    public void setDistance(double distance) {
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
