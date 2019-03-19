package com.community.jboss.leadmanagement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import com.community.jboss.leadmanagement.utils.CallRecordsManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CallRecorderService extends Service {

    private final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private final String AUDIO_RECORDER_FOLDER = "Lead Management Recordings";

    private MediaRecorder recorder = null;

    AudioManager audioManager;



    @Nullable
    @Override
    public Binder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if(audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true);
        }

        recorder = new MediaRecorder();
        String outputfilepath = getFilename();
        recorder.setOutputFile(outputfilepath);

        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            recorder.start();
            Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            if(e.getLocalizedMessage().contains("setAudioSource")){
                Toast.makeText(this, "Please grant permissions to use this feature", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Unknown error occurred, please try again later...", Toast.LENGTH_SHORT).show();
            }
        }
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String number = sharedPreferences.getString("number", "UNKNOWN-NUMBER");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = df.format(Calendar.getInstance().getTime());
        new CallRecordsManager().addCall(outputfilepath, number, strDate);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (null != recorder) {
                recorder.stop();
                recorder.reset();
                recorder.release();

                recorder = null;
            }
        }catch (IllegalStateException e){
            Toast.makeText(this, "Error occurred, please update application or try again later.", Toast.LENGTH_LONG).show();
        }
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_MP4);
    }
}
