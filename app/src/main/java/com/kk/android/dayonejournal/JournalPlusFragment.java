package com.kk.android.dayonejournal;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.UUID;

public class JournalPlusFragment extends BottomSheetDialogFragment {
    private TextView mTextTextView;
    private TextView mCameraTextView;
    private TextView mAlbumTextView;
    private static final int REQUEST_PHOTO = 2;
    public static final int SELECT_PHOTO = 1;
    private File mFile;
    private static UUID sUUID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sheet,null);
        mTextTextView = (TextView)view.findViewById(R.id.text_TextView);
        mTextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Journal journal = new Journal();
                CalendarDate calendarDate = new CalendarDate();
                calendarDate.setDate(journal);
                JournalLab.get(getActivity()).addJournal(journal);
                Intent intent = JournalPagerActivity.newIntent(getActivity(),journal.getId());
                startActivity(intent);
            }
        });

        mCameraTextView = (TextView) view.findViewById(R.id.camera_TextView);
        mCameraTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Journal journal = new Journal();
                CalendarDate calendarDate = new CalendarDate();
                calendarDate.setDate(journal);
                sUUID = journal.getId();
                mFile = JournalLab.get(getActivity()).getPhotoFile(journal);
                JournalLab.get(getActivity()).addJournal(journal);
                Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.kk.android.dayonejournal.fileprovider",mFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        mAlbumTextView = (TextView)view.findViewById(R.id.Album_TextView);
        mAlbumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                        .PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else { openAlbum();}
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
        if(requestCode == SELECT_PHOTO){
            Journal journal = new Journal();
            JournalLab.get(getActivity()).addJournal(journal);
            journal.setPath(handleImageOnKitKat(data).toString());
            Intent intent = JournalPagerActivity.newIntent(getActivity(),journal.getId());
            Log.i("TAGG",journal.getPath().toString()+journal.getId().toString());
            startActivity(intent);
        }
    }
    private void updatePhotoView(){
        Intent intent = JournalPagerActivity.newIntent(getActivity(),sUUID);
        startActivity(intent);
    }
    /*private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
    }*/
    private String handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(getContext(),uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" +id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri
                        .parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
       return imagePath;
    }
    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContext().getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore
                        .Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void openAlbum(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,SELECT_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
            if(grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED){
                openAlbum();
            }else{
                Toast.makeText(getActivity(), "sfsdfsdf", Toast.LENGTH_SHORT).show();
            }
            break;
            default:
        }
    }
}