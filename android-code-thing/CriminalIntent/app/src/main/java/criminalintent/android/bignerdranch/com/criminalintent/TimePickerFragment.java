package criminalintent.android.bignerdranch.com.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by 我 on 2016/11/23.
 */
public class TimePickerFragment extends DialogFragment {

    private static final String CRIME_TIME="crime_time";
    public static final String EXTRA_TIME="com.bignerdranch.android.criminalintent.time";
    private TimePicker mTimePicker;

    private void sendResult(Date date){
        Intent intent=new Intent();
        intent.putExtra(EXTRA_TIME,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK,intent);
    }
    public static TimePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(CRIME_TIME,date);
        TimePickerFragment timePickerFragment=new TimePickerFragment();
        timePickerFragment.setArguments(args);
        return timePickerFragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View v=inflater.inflate(R.layout.dialog_time,null);

        final Date date=(Date) getArguments().getSerializable(CRIME_TIME);
        mTimePicker=(TimePicker)v.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setIs24HourView(true);
        final Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        if(Build.VERSION.SDK_INT<23){
            mTimePicker.setCurrentHour(Integer.valueOf(hour));
            mTimePicker.setCurrentMinute(Integer.valueOf(minute));
        }else {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int currentHour;
                        int currentMinute;
                        if(Build.VERSION.SDK_INT<23){
                            currentHour=mTimePicker.getCurrentHour();
                            currentMinute=mTimePicker.getCurrentMinute();
                        }else{
                            currentHour=mTimePicker.getHour();
                            currentMinute=mTimePicker.getMinute();
                        }
                        calendar.set(Calendar.HOUR_OF_DAY,currentHour);
                        calendar.set(Calendar.MINUTE,currentMinute);
                        Date currentDate=calendar.getTime();
                        sendResult(currentDate);
                    }
                })
                .create();
    }
}
