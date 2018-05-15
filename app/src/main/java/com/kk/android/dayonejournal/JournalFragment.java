package com.kk.android.dayonejournal;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JournalFragment extends Fragment {
    private static final String ARG_JOURNAL_ID = "journal_id";
    private File mPhotoFile;
    private Journal mJournal;
    private EditText mTitleField;
    private EditText mTextField;
    private Calendar mCalendar;
    private TextView mTimeTextView;
    private ImageView mPictureImageView;
    private ImageView mAlumImageView;
    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButton mAlbumButton;
    private static final int REQUEST_PHOTO = 2;
    public static final int SELECT_PHOTO = 1;
    public static JournalFragment newInstance(UUID journalId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_JOURNAL_ID,journalId);
        JournalFragment fragment = new JournalFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID JournalId = (UUID) getArguments().getSerializable(ARG_JOURNAL_ID);
        mJournal = JournalLab.get(getActivity()).getJournal(JournalId);
        //Log.i("TAGG","6666"+mJournal.getPath()+mJournal.getId()+"7777");
        if(mJournal.getPath()!= null){
            Bitmap bitmap = BitmapFactory.decodeFile(mJournal.getPath());
            mAlumImageView.setImageBitmap(bitmap);
        }
        mPhotoFile = JournalLab.get(getActivity()).getPhotoFile(mJournal);
    }
    @Override
    public void onPause() {
        super.onPause();
        JournalLab.get(getActivity())
                .updateJournal(mJournal);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_journal,container,false);
        if(mJournal.getPath()!= null){
            Bitmap bitmap = BitmapFactory.decodeFile(mJournal.getPath());
            mAlumImageView.setImageBitmap(bitmap);
        }
        //Log.i("TAG",mJournal.getContent());
        mTimeTextView = (TextView) v.findViewById(R.id.time_TextView);
        setDate();
        mTextField = (EditText) v.findViewById(R.id.journal_text);
        mTextField.setText(mJournal.getContent());
        mTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mJournal.setContent(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mTitleField = (EditText) v.findViewById(R.id.journal_title);
        mTitleField.setText(mJournal.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mJournal.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPictureImageView = (ImageView)v.findViewById(R.id.tupian_ImageView);
        updatePhotoView(mPhotoFile);
        mAlumImageView = (ImageView) v.findViewById(R.id.picture_Image_View);
        mFloatingActionButton = (FloatingActionButton)v.findViewById(R.id.fab_camera);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.kk.android.dayonejournal.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        mAlbumButton = (FloatingActionButton)v.findViewById(R.id.fab_photo);
        mAlbumButton.setOnClickListener(new View.OnClickListener() {
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
        return v;
    }
    private void setDate(){
        if(mJournal.getDay() == 0){
        mCalendar = Calendar.getInstance();
        int year = mCalendar.get(mCalendar.YEAR);
        int month = mCalendar.get(mCalendar.MONTH)+1;
        int day = mCalendar.get(mCalendar.DAY_OF_MONTH);
        mTimeTextView.setText(year+"年"+month+"月"+day+"日");
        }else{
        mTimeTextView.setText
                (mJournal.getYear()+"年"+mJournal.getMonth()+"月"+mJournal.getDay()+"日");}
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO) {
            /*Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/
            updatePhotoView(mPhotoFile);
        }
        if(requestCode == SELECT_PHOTO){
            setPicture(data);
        }
    }
    public void updatePhotoView(File file) {
        if (file == null || !file.exists()) {
            mPictureImageView.setImageDrawable(null);
        } else {
           Bitmap bitmap = PictureUtils.getScaleBitmap(
                    file.getPath(),getActivity());
            mPictureImageView.setImageBitmap(bitmap);
        }
    }
    private void openAlbum(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,SELECT_PHOTO);
    }
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
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void setPicture(Intent data){
        UUID JournalId = (UUID) getArguments().getSerializable(ARG_JOURNAL_ID);
        Journal journal = JournalLab.get(getActivity()).getJournal(JournalId);
        journal.setPath(handleImageOnKitKat(data));
        Log.i("TAG","8888"+journal.getPath()+journal.getId()+"7777");
        Bitmap bitmap = BitmapFactory.decodeFile(journal.getPath());
        mAlumImageView.setImageBitmap(bitmap);
    }
}
