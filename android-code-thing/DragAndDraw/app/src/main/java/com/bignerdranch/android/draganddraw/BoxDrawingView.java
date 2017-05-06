package com.bignerdranch.android.draganddraw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by 我 on 2017/4/11.
 */
public class BoxDrawingView extends View {
    private static final String TAG="BoxDrawingView";
    private static final String SUPER_PARCEL="parcelable";
    private static final String BOXEN="boxen";
    private Box mCurrentBox;
    private List<Box> mBoxen=new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;
    private int[] mPointerId=new int[2];
    private PointF mPrePoint1;
    private PointF mPrePoint2;


    public BoxDrawingView(Context context){
        this(context,null);
    }
    public BoxDrawingView(Context context, AttributeSet attrs){
        super(context,attrs);
        mPointerId[0]=-1;
        mPointerId[1]=-1;
        mPrePoint1=null;
        mPrePoint2=null;
        mBoxPaint=new Paint();
        mBoxPaint.setColor(0x22ff0000);

        mBackgroundPaint=new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        for (Box box:mBoxen){
            float left=Math.min(box.getOrigin().x,box.getCurrent().x);
            float right=Math.max(box.getOrigin().x,box.getCurrent().x);
            float top=Math.min(box.getOrigin().y,box.getCurrent().y);
            float bottom=Math.max(box.getOrigin().y,box.getCurrent().y);

            float centerL=left+(right-left)/2;
            float centerH=bottom+(top-bottom)/2;
            canvas.rotate(box.getDegrees(),centerL,centerH);
            canvas.drawRect(left,top,right,bottom,mBoxPaint);
            canvas.rotate(-box.getDegrees(),centerL,centerH);
            Log.i(TAG,"boxPoint= "+box.getOrigin()+" "+box.getCurrent());
        }

    }

    private double getDegreeIncrement(PointF prePoint1,PointF prePoint2,PointF curPoint1,PointF curPoint2){
        double ox=prePoint1.x-prePoint2.x;
        double oy=prePoint1.y-prePoint2.y;
        double newx=curPoint1.x-curPoint2.x;
        double newy=curPoint1.y-curPoint2.y;
        double a=Math.sqrt(Math.pow(ox,2)+ Math.pow(oy,2));
        double b=Math.sqrt(Math.pow(newx,2)+Math.pow(newy,2));
        double ab=Math.abs(ox*newx+oy*newy);
        double degree=20*Math.acos(ab/(a*b));
        double symbol=(curPoint1.y-curPoint2.y)/(curPoint1.x-curPoint2.x)-
                (prePoint1.y-prePoint2.y)/(prePoint1.x-prePoint2.x);
        Log.i(TAG," "+ox+" "+oy+" "+newx+" "+newy);
        Log.i(TAG,"degree="+degree+" a="+a+" b="+b+" ab="+ab+" symbol="+symbol);
        if (degree>0) {
            if (symbol < 0) {
                return -degree;
            } else {
                return degree;
            }
        }else
            return 0;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current=new PointF(event.getX(),event.getY());
        PointF curPoint1=null;
        PointF curPoint2=null;
        if(mPointerId[1]>=0){
            curPoint1=new PointF(event.getX(event.findPointerIndex(mPointerId[0]))
                    ,event.getY(event.findPointerIndex(mPointerId[0])));
            curPoint2=new PointF(event.getX(event.findPointerIndex(mPointerId[1]))
                    ,event.getY(event.findPointerIndex(mPointerId[1])));
        }
        String action="";
        int actionIndex;
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                action="ACTION_DOWN";
                actionIndex=event.getActionIndex();
                action=action+" "+actionIndex;
                mCurrentBox=new Box(current);
                mPointerId[0]=event.getPointerId(actionIndex);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action="ACTION_MOVE";
                if(mCurrentBox!=null){
                    if(mPointerId[1]<0) {
                        mCurrentBox.setCurrent(current);
                    }else {
                        mCurrentBox.changeDegrees((float) getDegreeIncrement(mPrePoint1,mPrePoint2,curPoint1,curPoint2));
                        mPrePoint1=curPoint1;
                        mPrePoint2=curPoint2;
                        Log.i(TAG,"degree is "+getDegreeIncrement(mPrePoint1,mPrePoint2,curPoint1,curPoint2));
                    }
                    invalidate();
                }else {
                }
                action=action+event.getActionIndex();

                break;
            case MotionEvent.ACTION_UP:
                action="ACTION_UP";
                action=action+event.getActionIndex();
                if (mCurrentBox!=null&&mCurrentBox.getOrigin().equals(mCurrentBox.getCurrent())){
                    mBoxen.remove(mCurrentBox);
                }
                mCurrentBox=null;
                mPointerId[0]=-1;
                break;
            case MotionEvent.ACTION_CANCEL:
                action="ACTION_CANCEL";
                mCurrentBox=null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if(mCurrentBox!=null){
                    mBoxen.remove(mCurrentBox);
                    mCurrentBox=null;
                }
                if (!mBoxen.isEmpty()) {
                    mCurrentBox = mBoxen.get(mBoxen.size() - 1);
                }
                actionIndex=event.getActionIndex();
                action="ACTION_POINTER_DOWN";
                action=action+" "+actionIndex;
                mPointerId[1]=event.getPointerId(actionIndex);
                mPrePoint1=new PointF(event.getX(event.findPointerIndex(mPointerId[0]))
                        ,event.getY(event.findPointerIndex(mPointerId[0])));
                mPrePoint2=new PointF(event.getX(event.findPointerIndex(mPointerId[1]))
                        ,event.getY(event.findPointerIndex(mPointerId[1])));
                Log.i(TAG,"PID1="+mPointerId[0]+" PID2="+mPointerId[1]);
            break;
            case MotionEvent.ACTION_POINTER_UP:
                mCurrentBox=null;
                mPointerId[1]=-1;
                action="ACTION_POINTER_UP";
                action=action+event.getActionIndex();
                break;
        }
        Log.i(TAG,action+" at x="+current.x+",y="+current.y);
        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(SUPER_PARCEL,super.onSaveInstanceState());
        float[] boxen=new float[mBoxen.size()*4];
        int i=0;
        for(Box box:mBoxen){
            boxen[i]=box.getOrigin().x;
            boxen[i+1]=box.getOrigin().y;
            boxen[i+2]=box.getCurrent().x;
            boxen[i+3]=box.getCurrent().y;
            i+=4;
        }
        bundle.putFloatArray(BOXEN,boxen);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle=(Bundle)state;
        float[] boxen=bundle.getFloatArray(BOXEN);
        Box box;
        PointF pointFo;
        PointF pointFc;
        for (int i=0;i<boxen.length;i++){
            pointFo=new PointF(boxen[i],boxen[++i]);
            pointFc=new PointF(boxen[++i],boxen[++i]);
            box=new Box();
            box.setOrigin(pointFo);
            box.setCurrent(pointFc);
            mBoxen.add(box);
        }
        box=null;
        pointFc=null;
        pointFo=null;

        super.onRestoreInstanceState(bundle.getParcelable(SUPER_PARCEL));

    }
}
