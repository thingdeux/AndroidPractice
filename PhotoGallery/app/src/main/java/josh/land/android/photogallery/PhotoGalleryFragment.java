package josh.land.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by josh on 9/19/15.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    private static final String TEST_URL = "https://api.flickr.com/services/rest/?\nmethod=flickr.photos.getRecent&api_key=26070ad740fd501c085b5a572284fafc&format=json&nojsoncallback=1";
    private static final int GRID_SIZE = 3;
    private int current_page = 1;
    private boolean isMakingPaginationCall = false;
    private RecyclerView mPhotoRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems(current_page);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            Log.d(TAG, "AsyncTask sent Items: " + items.size());
            // TODO : Prune items from the head of the collection when items are > 500? 600?
            mItems.addAll(items);
            setupAdapter();
            isMakingPaginationCall = false;
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTextOverlay;
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView
                    .findViewById(R.id.fragment_photo_gallery_image_view);
            mTextOverlay = (TextView) itemView
                    .findViewById(R.id.fragment_photo_gallery_info_overlay);
        }

        public void bindGalleryItem(GalleryItem item) {
            mTextOverlay.setText(item.getId().toString());
            Picasso.with(getActivity())
                    .load(item.getUrl())
                    .placeholder(R.drawable.flickr)
                    .into(mItemImageView);
        }

//        public void bindInfoOverlay(GalleryItem item) {
//            mTextOverlay.setText(item.getId().toString());
//        }
//
//        public void bindDrawable(Drawable drawable) {
//            mItemImageView.setImageDrawable(drawable);
//        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
//            Drawable placeholder = getResources().getDrawable(R.drawable.flickr);
//            photoHolder.bindDrawable(placeholder);
//            photoHolder.bindInfoOverlay(galleryItem);
//            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
//            photoHolder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

//        Handler responseHandler = new Handler();
//        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
//        mThumbnailDownloader.setThumbNailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
//                  @Override
//                  public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
//                      Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                      photoHolder.bindDrawable(drawable);
//                  }
//              }
//        );
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
//        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mGridLayoutManager = new GridLayoutManager(getActivity(), GRID_SIZE);
        mPhotoRecyclerView.setLayoutManager(mGridLayoutManager);
        mPhotoRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                int i = (mGridLayoutManager.getItemCount() / GRID_SIZE) - GRID_SIZE*2;
//                Log.v(TAG, "" + mGridLayoutManager.findFirstVisibleItemPosition() + " -> " + i);
                if (mGridLayoutManager.findFirstVisibleItemPosition() >= (mGridLayoutManager.getItemCount() / GRID_SIZE) - GRID_SIZE * 2) {
                    // When the recycler view is 2 rows away from the bottom of its current set of items, make a new paged call
                    if (!isMakingPaginationCall) {
                        Log.v(TAG, "Making Pagination call");
                        current_page++;
                        isMakingPaginationCall = true;
                        new FetchItemsTask().execute();
                    }
                }
            }
        });

        setupAdapter();

        return v;
    }

    private void setupAdapter() {
        // Makes sure the fragment still exists and hasn't been GC'd
        if (isAdded()) {
            if (mPhotoRecyclerView.getAdapter() == null) {
                mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
            } else {
                mPhotoRecyclerView.getAdapter().notifyDataSetChanged();
            }


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
}
