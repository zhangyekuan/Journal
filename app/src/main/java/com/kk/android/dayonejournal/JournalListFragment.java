package com.kk.android.dayonejournal;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class JournalListFragment extends Fragment {
    private RecyclerView mJournalRecyclerView;
    private JournalAdapter mAdapter;
    private ImageButton mJournalAddImageButton;
    private ImageButton mCameraImageButton;
    private static UUID sUUID;
    private File mFile;
    private static final int REQUEST_PHOTO = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_list,
                container,false);
        mJournalAddImageButton = (ImageButton) view.findViewById(R.id.add_imageButton);
        mJournalAddImageButton.setOnClickListener(new View.OnClickListener() {
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
        mCameraImageButton = (ImageButton)view.findViewById(R.id.camera_ImageButton);
        mCameraImageButton.setOnClickListener(new View.OnClickListener() {
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
        mJournalRecyclerView = (RecyclerView) view.findViewById(R.id.journal_recycler_view);
        mJournalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    public class JournalHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private Journal mJournal;
        private TextView mTitleTextView;
        private TextView mTextTextView;
        private TextView mYMTextView;
        private TextView mDayTextView;
        public ImageView mPhotoImageView;
        private ImageButton mDeleteImageButton;
        private ImageButton mShareImageButton;
        private TextView mLocationTextView;
        public JournalHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_journal,parent,false));
            mTitleTextView = (TextView) itemView.findViewById(R.id.Journal_title);
            mTextTextView = (TextView) itemView.findViewById(R.id.journal_text);
            mYMTextView = (TextView) itemView.findViewById(R.id.year_month_TextView);
            mDayTextView = (TextView) itemView.findViewById(R.id.day_TextView);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.photo_ImageView);
            mDeleteImageButton = (ImageButton) itemView.findViewById(R.id.delete);
            mShareImageButton = (ImageButton)itemView.findViewById(R.id.share);
            mLocationTextView = (TextView)itemView.findViewById(R.id.location_textView);
            mTextTextView.setOnClickListener(this);
            mTitleTextView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Intent intent = JournalPagerActivity.newIntent(getActivity(),mJournal.getId());
            startActivity(intent);
        }

        public void bind(Journal journal){
            Calendar calendar = Calendar.getInstance();
            mJournal = journal;
            mTitleTextView.setText(mJournal.getTitle());
            mTextTextView.setText(mJournal.getContent());
            if(mJournal.getDay() == 0){
            mJournal.setDay(calendar.get(Calendar.DAY_OF_MONTH));
            mJournal.setYear(calendar.get(Calendar.YEAR));
            mJournal.setMonth(calendar.get(Calendar.MONTH)+1);
            }
            mDayTextView.setText(mJournal.getDay()+" ");
            mYMTextView.setText(mJournal.getYear()+"年"+mJournal.getMonth()+"月");
        }
    }
    public class JournalAdapter extends RecyclerView.Adapter<JournalHolder> {

        private List<Journal> mJournals;
        public JournalAdapter(List<Journal> journals){
            mJournals = journals;
        }
        @Override
        public JournalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new JournalHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(JournalHolder holder, final int position) {
            final Journal journal = mJournals.get(position);
            holder.bind(journal);
            holder.mDeleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JournalLab.get(getActivity()).deleteJournal(journal);
                    mJournals.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,mJournals.size());
                    //不加这句话的话容易数组越界，应用直接崩
                }
            });
            holder.mShareImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT,journal.getContent());
                    startActivity(intent);
                }
            });
            if(journal.getLocation() != null){
                Log.d("cylog","666"+journal.getLocation());
                holder.mLocationTextView.setText(journal.getLocation());
            }
        }

        @Override
        public int getItemCount() {
            return mJournals.size();
        }

        public void setJournal(List<Journal> journals){
            mJournals = journals;
        }

    }
    private void updateUI(){
        JournalLab journalLab = JournalLab.get(getActivity());
        List<Journal> journals = journalLab.getJournals();
        if(mAdapter == null) {
            mAdapter = new JournalAdapter(journals);
            mJournalRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.setJournal(journals);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }

    private void updatePhotoView(){
        Intent intent = JournalPagerActivity.newIntent(getActivity(),sUUID);
        startActivity(intent);
    }
}