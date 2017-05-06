package criminalintent.android.bignerdranch.com.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by 我 on 2016/11/22.
 */
public class DatePickerFragment extends DialogFragment {

    private static final String TAG="DateFragment";
    private static final String ARG_DATE="date";
    public static final String EXTRA_DATE="com.bignerdranch.android.criminalintent.date";
    private DatePicker mDatePicker;
    private Button mButton;
    public static DatePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_DATE,date);
        DatePickerFragment fragment=new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private void sendResult(int resultCode,Date date){
        Intent intent=new Intent();
        intent.putExtra(EXTRA_DATE,date);
        if(getTargetFragment()==null) {
            getActivity().setResult(resultCode, intent);
        }else
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getTargetFragment()==null) {
            View v = inflater.inflate(R.layout.dialog_date, null);

            mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
            final Date date = (Date) getArguments().getSerializable(ARG_DATE);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            mDatePicker.init(year, month, day, null);

            mButton = (Button) v.findViewById(R.id.dialog_date_ok);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentYear = mDatePicker.getYear();
                    int currentMonth = mDatePicker.getMonth();
                    int currentDay = mDatePicker.getDayOfMonth();
                    calendar.set(currentYear, currentMonth, currentDay);
                    Date currentDate = calendar.getTime();
                    sendResult(Activity.RESULT_OK, currentDate);
                    getActivity().finish();
                }
            });
            return v;
        }else{
            return super.onCreateView(inflater,container,savedInstanceState);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getTargetFragment() != null) {
            View v = LayoutInflater.from(getActivity())
                    .inflate(R.layout.dialog_date, null);

            mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
            final Date date = (Date) getArguments().getSerializable(ARG_DATE);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            mDatePicker.init(year, month, day, null);

            return new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle(R.string.date_picker_title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int year = mDatePicker.getYear();
                            int month = mDatePicker.getMonth();
                            int day = mDatePicker.getDayOfMonth();
                            Log.d(TAG, "onClick: " + year + month + day);
                            calendar.set(year, month, day);
                            Date currentDate = calendar.getTime();
                            sendResult(Activity.RESULT_OK, currentDate);
                        }
                    })
                    .create();
        }else{
            return super.onCreateDialog(savedInstanceState);
        }
    }

}
