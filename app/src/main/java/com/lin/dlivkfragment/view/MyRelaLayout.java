package com.lin.dlivkfragment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.lin.dlivkfragment.MainActivity;

/**
 * Created by xinlyun on 15-11-10.
 */
public class MyRelaLayout extends RelativeLayout {

    private boolean flag=false,flag2= false;
    private int style = 0;
    public MyRelaLayout(Context context) {
        super(context);
    }

    public MyRelaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setStyle(int flag){
        this.style = flag;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(style==0){
            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(ev.getY()>=(getHeight()-80)&&ev.getX()>getWidth()/3f&&ev.getX()<(getWidth()*2/3)){
                        flag = true;
                        flag2= true;
                        return true;
                    }
                    break;
                default:
                    flag2 = false;
                    return false;
            }
        }else if(style==1){
            Log.d("MyRelaLayout","yyy:"+ev.getY());
            if(ev.getY()<110&&ev.getY()>0&&ev.getX()>getWidth()/3f&&ev.getX()<(getWidth()*2/3)) {
                flag = true;
                flag2= true;
                return true;
            }
            flag2 = false;
            return false;
        }
        flag2 = false;
        return false;
//        return super.onInterceptTouchEvent(ev);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mainActivity!=null&&flag2){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:
                    flag = false;
                    break;
            }
            mainActivity.moving(event);
        }

        return true;
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(mainActivity!=null){
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    if(style==0)
//                        if(event.getY()>=(getHeight()-80)) {
//                            flag = true;
//                            mainActivity.moving(event);
//                        }else {
//                            if(event.getY()<80&&event.getY()>0) {
//                                flag = true;
//                                mainActivity.moving(event);
//                            }
//                        }
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    if(flag)
//                        mainActivity.moving(event);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    if(flag) {
//                        mainActivity.moving(event);
//                        return true;
//                    }
//
//                    flag = false;
//                    break;
//            }
//
//        }
//
//        return flag;
//    }

    private MainActivity mainActivity;
    public void inputActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
}
