package com.kk.android.dayonejournal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class JournalListActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton mHomeImageButton;
    private ImageButton mPictureImageButton;
    private ImageButton mPlusImageButton;
    private ImageButton mLocationImageButton;
    private ImageButton mCalendarImageButton;
    private Fragment mCurrentFragment = new Fragment();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mHomeImageButton = (ImageButton) findViewById(R.id.home_ImageButton);
        mPictureImageButton = (ImageButton) findViewById(R.id.picture_ImageButton);
        mPlusImageButton = (ImageButton) findViewById(R.id.plus_ImageButton);
        mLocationImageButton = (ImageButton) findViewById(R.id.location_ImageButton);
        mCalendarImageButton = (ImageButton) findViewById(R.id.calender_ImageButton);
        mHomeImageButton.setOnClickListener(this);
        mPictureImageButton.setOnClickListener(this);
        mPlusImageButton.setOnClickListener(this);
        mLocationImageButton.setOnClickListener(this);
        mCalendarImageButton.setOnClickListener(this);
        SwitchFragment(new JournalListFragment());
    }
    private void SwitchFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(!fragment.isAdded()){
            if(mCurrentFragment != null){
                transaction.hide(mCurrentFragment);
            }
            transaction.add(R.id.fragment_container,fragment);
        }else{
            transaction
                    .hide(mCurrentFragment)
                    .show(fragment);
        }
           mCurrentFragment = fragment;
            transaction.commit();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_ImageButton:
                SwitchFragment(new JournalListFragment());
                change(view);
                break;
            case R.id.picture_ImageButton:
                SwitchFragment(new JournalPictureFragment());
                change(view);
                break;
            case R.id.plus_ImageButton:
                BottomSheetDialogFragment fragment = new JournalPlusFragment();
                fragment.show(getSupportFragmentManager(),"dialog");
                change(view);
                break;
            case R.id.location_ImageButton:
                SwitchFragment(new JournalLocationFragment());
                change(view);
                break;
            case R.id.calender_ImageButton:
                SwitchFragment(new JournalCalendarFragment());
                change(view);
                break;
                default:
                    break;
        }
    }
    private void change(View view){
        view.setFocusable(true);
        view.requestFocus();
        view.requestFocusFromTouch();
    }
}
