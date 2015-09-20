package josh.land.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
            mItems.addAll(items);
            setupAdapter();
            isMakingPaginationCall = false;
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
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
                if (mGridLayoutManager.findFirstVisibleItemPosition() >= (mGridLayoutManager.getItemCount() / GRID_SIZE) - GRID_SIZE*2) {
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
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }
}
