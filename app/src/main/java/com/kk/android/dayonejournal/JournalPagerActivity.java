package com.kk.android.dayonejournal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JournalPagerActivity extends AppCompatActivity {
    private static final String EXTRA_JOURNAL_ID =
            "com.kk.android.dayonejournal.journal_id";
    private ViewPager mViewPager;
    private List<Journal> mJournals;
    public static Intent newIntent(Context packageContext, UUID journalId){
        Intent intent = new Intent(packageContext,JournalPagerActivity.class);
        intent.putExtra(EXTRA_JOURNAL_ID,journalId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_pager);

        UUID journalId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_JOURNAL_ID);
        mViewPager = (ViewPager) findViewById(R.id.activity_journal_pager_view_pager);
        mJournals = JournalLab.get(this).getJournals();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Journal journal = mJournals.get(position);
                return JournalFragment.newInstance(journal.getId());
            }
            @Override
            public int getCount() {
                return mJournals.size();
            }
        });
        for(int i = 0;i<mJournals.size();i++){
            if(mJournals.get(i).getId().equals(journalId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
