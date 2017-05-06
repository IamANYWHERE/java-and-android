package criminalintent.android.bignerdranch.com.criminalintent;

import android.annotation.TargetApi;
import android.content.Context;

import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

/**
 * Created by 我 on 2017/4/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DetailTransition extends TransitionSet {
    public DetailTransition(){
        init();
    }
    public DetailTransition(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }
    private void init(){
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform());
    }
}
