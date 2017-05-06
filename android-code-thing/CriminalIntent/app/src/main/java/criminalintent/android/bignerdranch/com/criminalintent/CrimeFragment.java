package criminalintent.android.bignerdranch.com.criminalintent;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.transition.Fade;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowId;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


/**
 * Created by 我 on 2016/11/14.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID="crime_id";
    private static final String DIALOG_DATE="dialog_date";
    private static final String DIALOG_TIME="dialog_time";
    private static final String TAG="CrimeFragment";
    private static final int REQUEST_PERMISSION=5;
    private static final int REQUEST_PHOTO=4;
    private static final int REQUEST_CONTACT=3;
    private static final int REQUEST_DATE=1;
    private static final int REQUEST_TIME=2;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private String mPhoneNumber;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callbacks mCallbacks;
    private Fragment mFragment;

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks=(Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }

    public void returnResult(){
        UUID crimeId=(UUID)getArguments().getSerializable(ARG_CRIME_ID);
        int index=CrimeLab.get(getActivity()).getIndex(crimeId);
        Intent intent=CrimeListFragment.newIntent(index);
        getActivity().setResult(Activity.RESULT_OK,intent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeId=(UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime=CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile=CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        mFragment=this;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_crime:
                CrimeLab crimeLab=CrimeLab.get(getActivity());
                crimeLab.deleteCrime(mCrime.getId());
                finishActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
private void finishActivity(){
    Intent intent=new Intent(getActivity(),CrimeListActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    getActivity().finish();
}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_crime,container,false);

        mTitleField=(EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                if(getActivity().findViewById(R.id.detail_fragment_container)==null) {
                    returnResult();
                }else{
                    updateCrime();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton=(Button)v.findViewById(R.id.crime_date);

        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getActivity().findViewById(R.id.detail_fragment_container)==null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dialog.show(fragmentManager, DIALOG_DATE);
                }else {
                    Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_DATE);
                }
            }
        });
        mTimeButton=(Button)v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getFragmentManager();
                TimePickerFragment timeDialog=TimePickerFragment.newInstance(mCrime.getDate());
                timeDialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                timeDialog.show(fragmentManager,DIALOG_TIME);
            }
        });

        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                if(getActivity().findViewById(R.id.detail_fragment_container)==null) {
                    returnResult();
                }else{
                    updateCrime();
                }
            }
        });

        mReportButton=(Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain")
                        .setChooserTitle(R.string.send_report)
                        .getIntent()
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                        .putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                startActivity(i);
            }
        });
        final Intent pickContact=new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton=(Button)v.findViewById(R.id.crime_suspect);
        PackageManager packageManager=getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY)==null){
            mSuspectButton.setEnabled(false);
        }else{
            mSuspectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNumberPermission(getActivity(),mFragment);
                    startActivityForResult(pickContact,REQUEST_CONTACT);
                }
            });
        }
        mPhoneNumber=mCrime.getPhoneNumber();
        mCallButton=(Button)v.findViewById(R.id.crime_call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPhoneNumber!=null) {
                    Uri number = Uri.parse("tel:" + mPhoneNumber);
                    Log.d(TAG, "onClick: number=" + number);
                    Intent intent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), mCrime.getPhoneNumber(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(mCrime.getSuspect()!=null){
            String suspect=mCrime.getSuspect();
            mSuspectButton.setText(suspect);
            mCallButton.setText(getString(R.string.call_somebody,suspect));
        }
        mPhotoButton=(ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto=(mPhotoFile!=null&&
                captureImage.resolveActivity(packageManager)!=null);
        if(canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            mPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(captureImage, REQUEST_PHOTO);
                }
            });
        }else{
            Toast.makeText(getActivity(),"no camera",Toast.LENGTH_SHORT);
        }
        mPhotoView=(ImageView)v.findViewById(R.id.crime_photo);

        final ViewTreeObserver observer=mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Point size=new Point();
                size.set(mPhotoView.getWidth(),mPhotoView.getHeight());
                updatePhotoView(size);
            }
        });

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureFragment pictureDialog=PictureFragment.newInstance(mPhotoFile);
                FragmentManager fragmentManager=getFragmentManager();
                pictureDialog.show(fragmentManager,null);
            }
        });
        return v;
    }


    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
    private void updatePhotoView(Point point){
        if(mPhotoFile==null||!mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap=PictureUtils
                    .getScaledBitmap(mPhotoFile.getPath(),point.x,point.y);
            mPhotoView.setImageBitmap(bitmap);
        }
    }
    private void updateDate() {
        String string;
        string= DateFormat.format("yyyy年MM月dd日",mCrime.getDate()).toString();
        mDateButton.setText(string);
    }

    private void updateTime(){
        String string;
        string=DateFormat.format("HH:mm",mCrime.getDate()).toString();
        mTimeButton.setText(string);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=Activity.RESULT_OK){
            return ;
        }
        if(requestCode==REQUEST_DATE){
            Date date=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            Log.d(TAG, "onActivityResult: date is "+date);
            updateCrime();
            updateDate();
        }else if(requestCode==REQUEST_TIME){
            Date timeDate=(Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(timeDate);
            updateCrime();
            updateTime();
        }else if(requestCode==REQUEST_CONTACT&&data!=null){
            Uri contactUri=data.getData();
            String[] queryFields=new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID,
            };
            String suspectId,suspect;
            ContentResolver resolver=getActivity().getContentResolver();
            Cursor c=resolver
                    .query(contactUri,queryFields,null,null,null);
            try{
                if(c.getCount()==0){
                    return;
                }
                c.moveToFirst();
                suspect=c.getString(0);
                suspectId=c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }finally{
                c.close();
            }
            Cursor cursor=resolver.query(CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{CommonDataKinds.Phone.NUMBER},
                    CommonDataKinds.Phone.CONTACT_ID+"=?"
                    ,new String[]{suspectId},
                    null);
            try{
                if(cursor.getCount()==0){
                    return;
                }
                cursor.moveToFirst();
                mPhoneNumber=cursor.getString(0);
                mCrime.setPhoneNumber(mPhoneNumber);
                mCallButton.setText(getString(R.string.call_somebody,suspect));
                updateCrime();
            }finally {
                cursor.close();
            }
        }else if(requestCode==REQUEST_PHOTO){
            ViewTreeObserver observer=mPhotoView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Point size=new Point();
                    size.x =mPhotoView.getWidth();
                    size.y=mPhotoView.getHeight();
                    updateCrime();
                    updatePhotoView(size);
                }
            });
        }
    }
    private void getNumberPermission(Activity activity,Fragment fragment){
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)!=
                PackageManager.PERMISSION_GRANTED){
            fragment.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_PERMISSION){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

            }else{

            }
        }
    }

    private String getCrimeReport(){
        String solvedString=null;
        if(mCrime.isSolved()){
            solvedString=getString(R.string.crime_report_solved);
        }else{
            solvedString=getString(R.string.crime_report_unsolved);
        }
        String dateFormat="yyyy年MM月dd日 HH：mm";
        String dateString= (String)DateFormat.format(dateFormat,mCrime.getDate());

        String suspect=mCrime.getSuspect();
        if(suspect==null){
            suspect=getString((R.string.crime_report_no_suspect));
        }else{
            suspect=getString(R.string.crime_report_suspect,suspect);
        }
        String report=getString(R.string.crime_report,
                mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }
}
