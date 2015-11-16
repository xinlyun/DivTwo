package com.lin.myfloatactionbtn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xinlyun on 15-9-30.
 */
public class VoiceView extends View {
    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    private int bacgroundColor;
    private boolean flag=true,lock = false,lock2 = false;
    private float radus,length;
    private Paint p,p2,p3;

    RectF rect,rect2;
    private int strV=0,strV2=0;
    public static final int CLOSE=0,OPEN=1;
    private static final int  CLOSING=2,OPENING=3;
    private int Style = OPEN;
    private float voice=0;
    public VoiceView(Context context) {
        super(context);

    }

    public VoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        initview();
        this.attrs = attrs;
    }

    public VoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
//        initview();
    }
    //    private int getWidth() ,getHeight();
    private AttributeSet attrs;


    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d("VoiceView",""+getWidth()+" "+getHeight());

        if(flag) {
            radus = getWidth() > getHeight() ? getHeight() / 2-10 : getWidth() / 2-10;
            length = getWidth() > getHeight() ? getWidth()/4-10:getHeight()/4-10;

            p = new Paint();
            p.setAntiAlias(true);
            p.setStrokeWidth(8f);
            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.parseColor("#1E88E5"));


            p2 = new Paint();
            p2.setAntiAlias(true);
            p2.setColor(Color.parseColor("#1E88E5"));
            p2.setStyle(Paint.Style.FILL);
            p2.setStrokeWidth(8f);

            p3 = new Paint();
            p3.setAntiAlias(true);
            p3.setColor(Color.parseColor("#1E88E5"));
            p3.setStyle(Paint.Style.STROKE);
            p3.setStrokeWidth(0f);
            p3.setAlpha(70);

            rect = new RectF(getWidth() / 2 - radus , getHeight() / 2 - radus , getWidth() / 2 + radus , getHeight() / 2 + radus );
            rect2 = new RectF(0, 0, getWidth(), getHeight());
            flag = false;


        }
        switch (Style){
            case CLOSE:
                strV2=30;
                canvas.drawCircle(getWidth()/2,getHeight()/2,radus+4,p2);
                break;
            case OPEN:
                strV2=0;
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, radus - 30 + strV2, p2);
                canvas.drawArc(rect, 0 + strV, 75 + strV2 * 3, false, p);
                canvas.drawArc(rect,180+strV,75+strV2*3,false,p);
                strV = (strV+8)%360;

                if(voice>0) {
                    p3.setStrokeWidth(length * 1.5f * voice);
                    voice-=0.05f;
                }else {
                    p3.setStrokeWidth(0);
                    voice = 0 ;
                }

                canvas.drawCircle(getWidth()/2,getHeight()/2,length,p3);


                invalidate();
                break;
            case CLOSING:
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, radus - 30 + strV2, p2);
                canvas.drawArc(rect, 0 + strV, 75 + strV2 * 3, false, p);
                canvas.drawArc(rect,180+strV,75+strV2*3,false,p);
                strV = (strV+8)%360;
                if(strV2==30)Style = CLOSE;
                strV2+=2;
                invalidate();
                break;
            case OPENING:
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, radus - 30 + strV2, p2);
                canvas.drawArc(rect, 0 + strV, 75 + strV2 * 3, false, p);
                canvas.drawArc(rect,180+strV,75+strV2*3,false,p);
                strV = (strV+8)%360;
                if(strV2==0)Style = OPEN;
                strV2-=2;
                invalidate();
                break;
        }


    }


    public void setVoice(float f){
        if(f!=0){
            voice = f;
//            p3.setStrokeWidth(length*voice);
        }


        p3.setStrokeWidth(length*1.5f*voice);
    }


    public boolean isLock(){
        if(Style==CLOSING||Style==CLOSE)return true;
        else return false;
    }
    public void Lock(){
        Style = CLOSING;
        invalidate();
    }
    public void unLock(){
//        lock = false;
        Style = OPENING;
        invalidate();
    }
}
