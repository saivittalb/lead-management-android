package com.community.jboss.leadmanagement.data.models;

public class CallRecords {
    public String number, localpath, driveid, time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    public String getDriveid() {
        return driveid;
    }

    public void setDriveid(String driveid) {
        this.driveid = driveid;
    }

    public CallRecords() {
    }

    public CallRecords(String number, String localpath, String time) {
        this.number = number;
        this.localpath = localpath;
        this.time = time;
    }

    public CallRecords(String number, String localpath, String driveid, String time) {
        this.number = number;
        this.localpath = localpath;
        this.driveid = driveid;
        this.time = time;
    }

    public CallRecords(String number, String localpath) {
        this.number = number;
        this.localpath = localpath;
    }
}