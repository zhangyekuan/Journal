package com.kk.android.dayonejournal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class JournalPictureFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_journal,container,false);
        return view;
    }
    private class PictureHolder extends RecyclerView.ViewHolder{
        private ImageView mPictureImageView;
        public PictureHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.picture_image,parent,false));
            mPictureImageView = (ImageView)itemView.findViewById(R.id.picture_view);
        }
    }
    private class PictureAdapter extends RecyclerView.Adapter<PictureHolder>{
        private List<Journal> mJournals;
        public PictureAdapter(List<Journal> journals){
            mJournals = journals;
        }
        @Override
        public PictureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PictureHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(PictureHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mJournals.size();
        }
    }
}
