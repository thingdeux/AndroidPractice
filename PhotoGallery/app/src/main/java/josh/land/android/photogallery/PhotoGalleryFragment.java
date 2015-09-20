package josh.land.android.photogallery;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
    private String usersQuery = "";

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            String query = usersQuery;
            if (query.isEmpty()) {
                return new FlickrFetchr().fetchRecentPhotos(current_page);
            } else {
                return new FlickrFetchr().searchPhotos(query, current_page);
            }
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

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextOverlay;
        private ImageView mItemImageView;
        private GalleryItem mItem;

        public PhotoHolder(View itemView) {
            super(itemView);

            FrameLayout container = (FrameLayout) itemView.findViewById(R.id.fragment_photo_gallery_container);
            container.setOnClickListener(this);


            mItemImageView = (ImageView) itemView
                    .findViewById(R.id.fragment_photo_gallery_image_view);
            mTextOverlay = (TextView) itemView
                    .findViewById(R.id.fragment_photo_gallery_info_overlay);

            mTextOverlay.setOnClickListener(this);
        }

        public void bindGalleryItem(GalleryItem item) {
            mItem = item;
            mTextOverlay.setText(item.getId());
            Picasso.with(getActivity())
                    .load(item.getUrl())
                    .placeholder(R.drawable.flickr)
                    .into(mItemImageView);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mItem.getUserUrl()));
            startActivity(intent);
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
        setHasOptionsMenu(true);
        updateItems();

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO : Sent user back to position 1 in the recycler view
                Log.d(TAG, "QueryTextSubmit: " + query);
                usersQuery = query;
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });
    }

    private void updateItems() {
        current_page = 1;
        isMakingPaginationCall = false;
        mItems.clear();
        new FetchItemsTask().execute();
    }
}
