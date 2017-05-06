package criminalintent.android.bignerdranch.com.criminalintent;

import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by 我 on 2016/11/10.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mPhoneNumber;

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public Crime(UUID id){
        //Generate unique identifier
        mDate=new Date();
        mId=id;
    }
    public Crime(){
        this(UUID.randomUUID());
    }
    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {return mDate;}

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
    public String getPhotoFilename(){
        return "IMG_"+getId().toString()+".jpg";
    }
}
