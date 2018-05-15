package com.kk.android.dayonejournal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.UUID;

public class TakePhoto extends AppCompatActivity {
    public  void takephoto(UUID uuid, File file, Activity activity,int request){
        Journal journal = new Journal();
        uuid = journal.getId();
        file = JournalLab.get(activity).getPhotoFile(journal);
        JournalLab.get(activity).addJournal(journal);
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(activity,
                "com.kk.android.dayonejournal.fileprovider",file);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(captureImage,request);
    }
}
