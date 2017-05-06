package criminalintent.android.bignerdranch.com.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by 我 on 2017/3/11.
 */
public class PictureFragment extends DialogFragment {

    private static final String ARG_FILE="file";
    ImageView mPicture;
    File mPictureFile;

    public static PictureFragment newInstance(File file){
        Bundle args=new Bundle();
        args.putSerializable(ARG_FILE,file);

        PictureFragment fragment=new PictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_picture,null);

        mPictureFile=(File) getArguments().getSerializable(ARG_FILE);
        mPicture=(ImageView) v.findViewById(R.id.dialog_crime_photo);
        if(mPictureFile==null||!mPictureFile.exists()){
            mPicture.setImageDrawable(null);
        }else{
            Bitmap bitmap=PictureUtils.getScaleBitmap(mPictureFile.getPath(),getActivity());
            mPicture.setImageBitmap(bitmap);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
}
