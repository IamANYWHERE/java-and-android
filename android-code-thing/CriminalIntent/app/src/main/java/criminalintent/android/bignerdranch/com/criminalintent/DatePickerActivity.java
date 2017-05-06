package criminalintent.android.bignerdranch.com.criminalintent;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;


import java.util.Date;

/**
 * Created by 我 on 2016/11/24.
 */
public class DatePickerActivity extends SingleFragmentActivity {


    private static final String EXTRA_DATE="date";
    public static Intent newIntent(Context packageContext, Date date){
        Intent intent=new Intent(packageContext,DatePickerActivity.class);
        intent.putExtra(EXTRA_DATE,date);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        Date date = (Date) intent.getSerializableExtra(EXTRA_DATE);

        return DatePickerFragment.newInstance(date);
    }
}
