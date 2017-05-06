package criminalintent.android.bignerdranch.com.criminalintent;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by 我 on 2016/11/21.
 */
public class CrimePagerActivity extends AppCompatActivity
implements CrimeFragment.Callbacks{

    private static final String EXTRA_CRIME_ID="com.bignerdranch.android.crimnalintent.crime_id";
    private static final String EXTRA_SUBTITLE_VISIBLE="com.bignerdranch.android.crimnalintent.subtitleVisible";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;


    @Override
    public void onCrimeUpdated(Crime crime) {

    }

    public static Intent newIntent(Context packageContext, UUID crimeId, boolean subtitleVisible){
        Intent intent=new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        intent.putExtra(EXTRA_SUBTITLE_VISIBLE,subtitleVisible);
        return intent;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId=(UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);


        mViewPager=(ViewPager)findViewById(R.id.activity_crime_pager_view_pager);

        mCrimes=CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime=mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        int index=CrimeLab.get(this).getIndex(crimeId);
        mViewPager.setCurrentItem(index);
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent=CrimeListFragment.newIntent(this,getIntent().getBooleanExtra(EXTRA_SUBTITLE_VISIBLE,false));
        return intent;
    }

}
