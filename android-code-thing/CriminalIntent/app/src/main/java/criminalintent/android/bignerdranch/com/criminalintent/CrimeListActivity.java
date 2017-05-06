package criminalintent.android.bignerdranch.com.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by 我 on 2016/11/17.
 */
public class   CrimeListActivity extends SingleFragmentActivity
implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks{

    public static final String TAG="CrimeListActivity";
    @Override
    public void onCrimeSelected(Crime crime,boolean subtitleVisible) {
        if(findViewById(R.id.detail_fragment_container)==null){
            Log.d(TAG, "onCrimeSelected: detail=null");
            Intent intent=CrimePagerActivity.newIntent(this,crime.getId(),subtitleVisible);
            startActivity(intent);
        }else{
            Log.d(TAG, "onCrimeSelected: detail!=null");
            Fragment newDetail=CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment=(CrimeListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateIndex(crime);
        listFragment.updateUI();
    }

    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
