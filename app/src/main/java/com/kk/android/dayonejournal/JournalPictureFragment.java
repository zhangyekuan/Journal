package com.kk.android.dayonejournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JournalPictureFragment extends Fragment {
    private static List<GalleryItem> sGalleryItems;
    private RecyclerView mWallpaperRecyclerView;
    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<GalleryItem> mItems = new ArrayList<>();
    private static final String TAG = "Fetchr";
    private PhotoAdapter mAdapter;
    //private static final String API_KEY = "789a3ff6f6b1f97c9b4e326278acc962";
    public static JournalPictureFragment newInstance(){
        return new JournalPictureFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.i(TAG,"Background thread started");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallpaper_gallery,container,
                false);
        mWallpaperRecyclerView = (RecyclerView) view.findViewById(R.id.wall_recycler_view);
        mWallpaperRecyclerView.setLayoutManager(
                new GridLayoutManager(getActivity(),2));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestPicture();
                setupAdapter();
            }
        });
        RequestPicture();
        setupAdapter();
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Background thread destroy");
    }

    private void setupAdapter() {
        if (isAdded()) {
            mAdapter = new PhotoAdapter(mItems);
            mWallpaperRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private GalleryItem mGalleryItem;
        private ImageView mItemImageView;
        public PhotoHolder(View view){
            super(view);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
            itemView.setOnClickListener(this);
        }
        public void bindGalleryItem(GalleryItem galleryItem){
            mGalleryItem = galleryItem;
            Glide.with(getActivity())
                    .load(galleryItem.getUrl())
                    .into(mItemImageView);
        }

        @Override
        public void onClick(View view) {
            Intent intent = BigPagerPictureActivity
                    .newIntent(getActivity(),mGalleryItem.getUrl());
            startActivity(intent);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery,parent,false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));
            if (!photoJsonObject.has("url_s")) {
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
    public void RequestPicture(){
        String url = "https://api.flickr.com/services/rest/" +
                "?method=flickr.photos.getRecent&api_key=" +
                "be3f07aecec54a5004a9983c5068219f&format=json&nojsoncallback=1&extras=url_s";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<GalleryItem> items = new ArrayList<>();
                        String json = response.toString();
                        try {
                            JSONObject jsonBody = new JSONObject(json);
                            parseItems(items, jsonBody);
                            mItems = items;
                            sGalleryItems = items;
                            setupAdapter();
                        } catch (JSONException je) {
                            Log.e(TAG, "Failed to parse JSON", je);
                        } catch (IOException ioe) {
                            Log.e(TAG, "Failed to IO", ioe);
                        }
                        setupAdapter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "6666667666" + error.getMessage(), error);
            }
        });
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(jsonObjectRequest);
    }
    public static List<GalleryItem> getGalleryItems(){
        return sGalleryItems;
    }


}
