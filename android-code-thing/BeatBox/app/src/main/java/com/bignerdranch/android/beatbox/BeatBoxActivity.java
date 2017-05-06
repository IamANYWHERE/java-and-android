package com.bignerdranch.android.beatbox;

import android.support.v4.app.Fragment;

public class BeatBoxActivity extends SingleFragmentActivity2 {

    @Override
    protected Fragment createFragment() {
        return BeatBoxFragment.newInstance();
    }
}
