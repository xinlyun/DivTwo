package com.lin.myfloatactionbtn;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;


/**
 * Created by xinlyun on 15-9-24.
 */
public class AddMyFloatActionButton extends MyFloatActionButton {
    public AddMyFloatActionButton(Context context) {
        super(context);
    }

    public AddMyFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddMyFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void clockwise(int time){
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                    if(msg.what==30)
                        setRoute(180);
//                        invalidate();
                    setRoute(6*msg.what);
                super.handleMessage(msg);
            }
        };
        new Timer(handler,time).start();
    }
    public void anticlockwise(int time){
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==30)
                    setRoute(180);
//                        invalidate();
                setRoute(180-6*msg.what);
                super.handleMessage(msg);
            }
        };
        new Timer(handler,time).start();
    }

}
