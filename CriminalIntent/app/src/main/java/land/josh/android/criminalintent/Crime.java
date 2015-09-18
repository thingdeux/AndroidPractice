package land.josh.android.criminalintent;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by josh on 8/30/15.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;


    public Crime() {
        // Generate unique identifier
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public void setTime(int hour, int minute) {
//        mDate.setTime();
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
}
