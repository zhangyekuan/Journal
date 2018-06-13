package com.kk.android.dayonejournal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class BigPictureFragment extends Fragment {
    private static final String ARE_GALLERY_URL = "gallery_id";
    private ImageView mPictureImageView;
    private FloatingActionButton mFloatingActionButton;
    public static BigPictureFragment newInstance(String url){
        Bundle args = new Bundle();
        args.putSerializable(ARE_GALLERY_URL,url);
        BigPictureFragment fragment = new BigPictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_fragment,container,false);
        mPictureImageView = (ImageView)view.findViewById(R.id.picture_image_view);
        mFloatingActionButton = (FloatingActionButton) view
                .findViewById(R.id.download_image_button);
        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
        final String url =  (String) getArguments().getSerializable(ARE_GALLERY_URL);
        Log.i("TAG",url);
        Glide.with(getActivity())
                .load(url)
                .into(mPictureImageView);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String path =  getImagePath(url);
                        File dirFile = new File(Environment.getExternalStorageDirectory().getPath());
                        if(!dirFile.exists()){
                            dirFile.mkdir();
                        }
                        String fileName;
                        fileName = UUID.randomUUID().toString()+".jpg";
                        File jia = new File(Environment
                                .getExternalStorageDirectory().getPath() +"/GalleryPhotos");
                        if(!jia.exists()){   //判断文件夹是否存在，不存在则创建
                            jia.mkdirs();
                        }
                        File myCaptureFile = new File(jia +"/"+ fileName);
                        copyFile(path,myCaptureFile);
                        //通过广播通知照片库更新
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(myCaptureFile);
                        intent.setData(uri);
                        getActivity().sendBroadcast(intent);
                    }
                }).start();
            }
        });
        return view;
    }
    private String getImagePath(String imgUrl) {
        String path = null;
        FutureTarget<File> future = Glide.with(this)
                .load(imgUrl)
                .downloadOnly(500,500);
        try {
            File cacheFile = future.get();
            path = cacheFile.getAbsolutePath();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return path;
    }
    public void copyFile(String oldPath, File newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                //int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
