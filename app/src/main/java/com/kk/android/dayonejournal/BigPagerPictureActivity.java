package com.kk.android.dayonejournal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class BigPagerPictureActivity extends AppCompatActivity{
    public static final String EXTRA_GALLERY_URL =
            "com.kk.android.wallpapergallery.gallery_id";
    private ViewPager mViewPager;
    private List<GalleryItem> mGalleryItems;
    public static Intent newIntent(Context packageContext, String url){
        Intent intent = new Intent(packageContext,BigPagerPictureActivity.class);
        intent.putExtra(EXTRA_GALLERY_URL,url);
        return intent;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_pager);
        final String url = (String) getIntent()
                .getSerializableExtra(EXTRA_GALLERY_URL);
        mViewPager = (ViewPager) findViewById(R.id.activity_picture_pager_view_pager);
        mGalleryItems = JournalPictureFragment.getGalleryItems();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                GalleryItem galleryItem = mGalleryItems.get(position);
                return BigPictureFragment.newInstance(galleryItem.getUrl());
            }

            @Override
            public int getCount() {
                return 20;
            }
        });
        for(int i = 0;i<mGalleryItems.size();i++){
            if(mGalleryItems.get(i).getUrl().equals(url)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
