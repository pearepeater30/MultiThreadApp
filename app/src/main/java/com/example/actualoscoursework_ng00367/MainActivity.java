package com.example.actualoscoursework_ng00367;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainActivity extends AppCompatActivity {

    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public final int EXTERNAL_REQUEST = 138;
    public final File sourceFolder =  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "sourceFolder");
    public final File destinationFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "destinationFolder");
    private int filesCopied = 0;
    File[] filesBeingCopied = sourceFolder.listFiles();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askForPermission(getApplicationContext());
    }

    //class which initializes a thread which copies files and increments a counter and updates it to UI
    public synchronized void startProgress(final int beginning, final int end){
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        final Handler mHandler = new Handler();
        if (!memoryInfo.lowMemory) {
            new Thread(new Runnable() {
                public void run() {
                    for(int i = beginning; i < end; i++){
                        try{
                            Files.copy(filesBeingCopied[i].toPath(), new File(destinationFolder.getAbsolutePath() + File.separator +filesBeingCopied[i].getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                filesCopied++;
                                ((TextView) findViewById(R.id.fileCounter)).setText(String.valueOf(filesCopied));

                            }
                        });
                    }

                }

            }).start();
        }

    }

    //class that is run when button is clicked to begin copying files
    public void buttonClick(final View view){
        if (sourceFolder.isDirectory()){
            if (filesBeingCopied.length > 0) {
                startProgress(0, filesBeingCopied.length/2);
                startProgress(filesBeingCopied.length/2, filesBeingCopied.length);
            }
        }
    }

    //class which asks for permission to read and write files
    public boolean askForPermission (Context context){
        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!accessSD(context)) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }
        return isPermissionOn;
    }

    //class which gets memory info
    private ActivityManager.MemoryInfo getAvailableMemory(){
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }


    public boolean accessSD (Context context){
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, context));
    }

    private boolean hasPermission (String permmission, Context context){
        return (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permmission));
    }
}
