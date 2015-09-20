package josh.land.android.photogallery;

/**
 * Created by josh on 9/19/15.
 */
public class GalleryItem {
    private static final String FLICKR_PROFILE_URL = "http://flickr.com/photo.gne?id=";
    private String mCaption;
    private String mId;
    private String mUrl;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUserUrl() {
        String[] splitUrl = getUrl().split("/");
        String filename = splitUrl[splitUrl.length - 1];
        String userId = filename.split("_")[0];
        return FLICKR_PROFILE_URL + userId;
    }
}
