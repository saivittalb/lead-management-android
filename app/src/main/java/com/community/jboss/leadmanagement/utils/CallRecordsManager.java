package com.community.jboss.leadmanagement.utils;

import android.os.Environment;

import com.community.jboss.leadmanagement.data.models.CallRecords;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CallRecordsManager {
    private String path = "";
    private final String AUDIO_RECORDER_FOLDER = "Lead Management Recordings";
    private Boolean flag = false;

    public Boolean writetofile(String contents) {
        Boolean status = false;
        try (FileWriter fw = new FileWriter(path);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(contents);
            out.flush();
        } catch (IOException e) {


        }
        return status;
    }

    public String readfile() {
        String jsonstring = null;
        try {
            BufferedReader br = null;

            try {
                br = new BufferedReader(new FileReader(path));
                jsonstring = br.readLine();
            } catch (FileNotFoundException ae) {
                ae.printStackTrace();

            } finally {
                if ((br != null))
                    br.close();
            }
        } catch (IOException ae) {
            ae.printStackTrace();
        }
        return jsonstring;
    }

    public ArrayList<CallRecords> getList(String jsonstring) {

        TypeToken<ArrayList<CallRecords>> token = new TypeToken<ArrayList<CallRecords>>() {
        };
        ArrayList<CallRecords> data = new Gson().fromJson(jsonstring, token.getType());
        return data;

    }

    public String getJson(ArrayList<CallRecords> data) {
        Gson gs = new Gson();
        TypeToken<ArrayList<CallRecords>> token = new TypeToken<ArrayList<CallRecords>>() {
        };
        String ar = gs.toJson(data, token.getType());
        return ar;
    }

    public void addCall(String callpath, String number, String time) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        String rootpath = file.getAbsolutePath();
        File listfile = new File(rootpath, "Call Records.json");
        if (!listfile.exists()) {
            try {
                listfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        path = listfile.getAbsolutePath();
        String inputdata = readfile();
        if (inputdata == null || inputdata.length() <= 17 || flag) {
            flag = false;
            ArrayList<CallRecords> tmp = new ArrayList<>();
            tmp.add(new CallRecords(number, callpath, time));
            writetofile(getJson(tmp));

        } else {
            ArrayList<CallRecords> tmp = getCallsList();
            CallRecords newcall = new CallRecords(number, callpath, time);
            tmp.add(newcall);
            writetofile(getJson(tmp));

        }

    }

    public ArrayList<CallRecords> getCallsList() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        String rootpath = file.getAbsolutePath();
        File listfile = new File(rootpath, "Call Records.json");
        if (!listfile.exists()) {
            try {
                listfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        path = listfile.getAbsolutePath();
        String inputdata = readfile();
        if (inputdata == null) {
            return new ArrayList<CallRecords>();
        } else {
            return getList(readfile());
        }

    }

    public void addbackupdetails(int position, String driveid) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        String rootpath = file.getAbsolutePath();
        File listfile = new File(rootpath, "Call Records.json");
        if (!listfile.exists()) {
            try {
                listfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        path = listfile.getAbsolutePath();
        ArrayList<CallRecords> data = getCallsList();
        CallRecords tmp = new CallRecords();
        tmp.setDriveid(driveid);
        tmp.setTime(data.get(position).getTime());
        tmp.setLocalpath(data.get(position).getLocalpath());
        tmp.setNumber(data.get(position).getNumber());
        data.set(position, tmp);
        String newdata = getJson(data);
        writetofile(newdata);
    }
}
