package com.community.jboss.leadmanagement.main.callrecord;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.community.jboss.leadmanagement.R;
import com.community.jboss.leadmanagement.data.models.CallRecords;
import com.community.jboss.leadmanagement.main.MainFragment;
import com.community.jboss.leadmanagement.utils.CallRecordsManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;
import static com.community.jboss.leadmanagement.SettingsFragment.PREF_DARK_THEME;


public class CallRecordFragment extends MainFragment {
    private String tmppath;
    private GoogleSignInClient mGoogleSignInClient;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBarProgress;
    private int mediaFileLengthInMilliseconds;
    private DriveResourceClient mDriveResourceClient;
    private final Handler handler = new Handler();
    private int RC_SIGN_IN = 5845;
    private int RC_SIGN_IN_DOWNLOAD = 4548;
    private Boolean flag = false;
    private int pos;
    private RecyclerView.Adapter mAdapter;
    private String driveid = null;
    private File filename = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean  useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        View rootView = inflater.inflate(R.layout.activity_call_record, container, false);
        RecyclerView rv = rootView.findViewById(R.id.list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ArrayList<CallRecords> list = new CallRecordsManager().getCallsList();
        mAdapter = new CallRecordsAdapter(list, new CallRecordsAdapter.MyAdapterListener() {
            @Override
            public void playbuttonOnClick(View v, int position) {
                Toast.makeText(getContext(), "Starting media Playback", Toast.LENGTH_SHORT).show();
                audioPlayer(list.get(position).getLocalpath());

            }

            @Override
            public void backupstatusOnClick(View v, int position) {
                if (list.get(position).getDriveid() == null) {
                    Toast.makeText(getContext(), "Uploading data to cloud backup", Toast.LENGTH_SHORT).show();
                    pos = position;

                    backuptodrive(list.get(position).getLocalpath());

                } else {
                    if (!new File(list.get(position).getLocalpath()).exists()) {

                        Toast.makeText(getContext(), "Refreshing local copy", Toast.LENGTH_SHORT).show();
                        driveid = list.get(position).getDriveid();
                        filename = new File((list.get(position).getLocalpath()));
                        downloadinit();
                    }
                }

            }

        },useDarkTheme);


        rv.setLayoutManager(layoutManager);
        rv.setAdapter(mAdapter);

        rv.scrollToPosition(list.size() - 1);
        return rootView;
    }

    @Override
    public int getTitle() {
        return R.string.title_callrecordings;
    }

    public void audioPlayer(String filepath) {
        mediaPlayer = new MediaPlayer();
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_play);
        mediaPlayer = new MediaPlayer();
        ImageView buttonPlayPause = (ImageView) dialog.findViewById(R.id.playpause);
        seekBarProgress = (SeekBar) dialog.findViewById(R.id.seekBar);
        try {
            mediaPlayer.setDataSource(filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        seekBarProgress.setProgress(0);
        seekBarProgress.setMax(100);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mediaPlayer.release();
                flag = false;
            }
        });
        try {
            if (mediaPlayer.isPlaying()) {
                buttonPlayPause.setImageResource(R.drawable.baseline_play_arrow_black_48);
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            buttonPlayPause.setImageResource(R.drawable.baseline_pause_black_48);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mp.release();
                    flag = false;
                    dialog.dismiss();
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
                    dialog.show();
                    mediaPlayer.start();
                    flag = true;
                    primarySeekBarProgressUpdater();
                }
            });
            seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { //Required
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        flag = true;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * seekBarProgress.getProgress();
                        mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { //Required
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    flag = true;
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        buttonPlayPause.setImageResource(R.drawable.baseline_play_arrow_black_48);
                    } else {
                        buttonPlayPause.setImageResource(R.drawable.baseline_pause_black_48);
                        mediaPlayer.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void primarySeekBarProgressUpdater() {
        if (flag) {
            seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
            {
                if (mediaPlayer.isPlaying()) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            primarySeekBarProgressUpdater();
                        }
                    };
                    handler.postDelayed(notification, 1000);
                }
            }

        }
    }


    public void backuptodrive(String path) {

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                .requestIdToken("YOUR_REQUEST_ID_TOKEN")
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), signInOptions);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        tmppath = path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            task.addOnSuccessListener(
                    new OnSuccessListener<GoogleSignInAccount>() {
                        @Override
                        public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                            Log.d("Lead Management Android", "Sign in success");
                            // Build a drive client.
                            Log.d("Lead Management Android", googleSignInAccount.getDisplayName());
                            // Build a drive resource client.
                            mDriveResourceClient = Drive.getDriveResourceClient(getContext(), googleSignInAccount);
                            Runnable code = new Runnable() {
                                @Override
                                public void run() {
                                    createFile(tmppath);
                                }
                            };
                            code.run();

                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Sign in failed", e);
                                }
                            });
        } else if (requestCode == RC_SIGN_IN_DOWNLOAD) {
            downloadfile();
        }
    }

    private void createFile(String path) {

        final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getAppFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = rootFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();
                        File file = new File(path);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        FileInputStream fis = new FileInputStream(file);
                        for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                            baos.write(buf, 0, readNum);
                        }
                        OutputStream outputStream = contents.getOutputStream();
                        outputStream.write(baos.toByteArray());

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(file.getName())
                                .setMimeType("audio/mpeg")
                                .build();

                        return mDriveResourceClient.createFile(parent, changeSet, contents);
                    }
                }).addOnSuccessListener(new OnSuccessListener<DriveFile>() {
            @Override
            public void onSuccess(DriveFile driveFile) {
                String driveid = driveFile.getDriveId().encodeToString();
                new CallRecordsManager().addbackupdetails(pos, driveid);

                mAdapter.notifyItemChanged(pos);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR", "Unable to upload ");
                    }
                });

    }

    public void downloadinit() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                .requestIdToken("YOUR_REQUEST_ID_TOKEN")
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), signInOptions);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_DOWNLOAD);

    }

    public void downloadfile() {


        DriveId driveId = DriveId.decodeFromString(driveid);
        if (!filename.exists()) {   //This Codeblock creates the file if it does not exists
            try {
                filename.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        final DriveFile file = driveId.asDriveFile();
        Task<DriveContents> openFileTask =
                Drive.getDriveResourceClient(getContext(), GoogleSignIn.getLastSignedInAccount(getContext())).openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    InputStream inputstream = contents.getInputStream();

                    try {
                        FileOutputStream fileOutput = new FileOutputStream(filename);

                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputstream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                        }
                        fileOutput.close();
                        inputstream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mAdapter.notifyItemChanged(pos);
                    Task<Void> discardTask = Drive.getDriveResourceClient(getContext(), GoogleSignIn.getLastSignedInAccount(getContext())).discardContents(contents);
                    return discardTask;
                })
                .addOnFailureListener(e -> {

                });


    }

}
