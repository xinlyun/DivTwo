package com.lin.myfloatactionbtn;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
/**
 * Created by xinlyun on 15-9-22.
 */
public class MyFloatActionButton extends ImageView {

    protected Bitmap cirMap;
    private int width=400;
    private boolean flagUporDown=true,flag = true,visLock=false;
    static final int VIS=0,INVIS=1,Opening=2,Closing=3;
    int STYLE=INVIS;
    float px,py,widthX,heightX;
    int i;
    float x1,y1;
    private Matrix m;

    public MyFloatActionButton(Context context) {
        super(context);
    }

    public MyFloatActionButton(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(context, attrs);
        attr1 = attrs;
    }
    public void lock(){
        visLock = true;
    }
    public MyFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
        attr1 = attrs;
    }
    AttributeSet attr1;
    Bitmap b1;
    private void init(Context context,AttributeSet attributeSet){
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.MyFloatActionButton, 0, 0);
        int icon = attr.getResourceId(R.styleable.MyFloatActionButton_cir_icon, R.drawable.ic_fab_star);
        b1 = BitmapFactory.decodeResource(getResources(), icon);
        Matrix matrix = new Matrix();
        matrix.setScale((float) width / (float) b1.getWidth(), (float) width / (float) b1.getWidth());
        Bitmap b2 = Bitmap.createBitmap(b1,0,0,b1.getWidth(),b1.getHeight(),matrix,false);
        cirMap = createCircleImage(b2, width);
        m=new Matrix();
    }
    private void init(){
        Matrix matrix = new Matrix();
        matrix.setScale((float) width / (float) b1.getWidth(), (float) width / (float) b1.getWidth());
        Bitmap b2 = Bitmap.createBitmap(b1,0,0,b1.getWidth(),b1.getHeight(),matrix,false);
        cirMap = createCircleImage(b2, width);
        m = new Matrix();
    }

    public void init(int id,int width1){
        Log.d("Float","init "+width1);

        this.width = width1-8;
        b1 = BitmapFactory.decodeResource(getResources(), id);
        Matrix matrix = new Matrix();
        matrix.setScale((float) width / (float) b1.getWidth(), (float) width / (float) b1.getWidth());
        Bitmap b2 = Bitmap.createBitmap(b1,0,0,b1.getWidth(),b1.getHeight(),matrix,false);
        cirMap = createCircleImage(b2, width);
        flag = false;
        m = new Matrix();
    }
    public void setWidth(int width){
        if(flag) {
            Log.d("Float","setWidth "+width);
            this.width = width - 8;
            init();
            invalidate();
        }
    }
    protected void setRoute(float route){
        m.setRotate(route,cirMap.getWidth()/2,cirMap.getHeight()/2);
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(visLock||STYLE!=INVIS) {
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setAlpha(20);
            for (int i = 1; i <= 8; i++)
                canvas.drawCircle(width / 2 + i / 3, width / 2 + i, width / 2, p);
//            canvas.drawBitmap(cirMap, 0, 0, null);
                canvas.drawBitmap(cirMap,m,null);
            if (!flagUporDown) {
                p.setAlpha(50);
                canvas.drawCircle(width / 2, width / 2, width / 2, p);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width,width);
    }

    public void miss(){
        STYLE=INVIS;
        invalidate();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case 1:
                flagUporDown = true;
                if(l!=null)l.onClick(this);
//                if(clickListener!=null)clickListener.Click(this);
                invalidate();
                break;
            case 0:
                flagUporDown = false;
                invalidate();
                break;
        }
        return true;

    }
    float x,y;
    public void setXY(MyFloatActionMenu.Posi posi){
        this.x = posi.l;
        this.y = posi.t;
    }
    public void UPandDOWN(boolean b){
        flagUporDown = b;
        invalidate();
    }
    OnClickListener l ;
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        this.l = l;
    }

    public boolean isAnimationing(){
        if (STYLE==Opening||STYLE==Closing){
            return true;
        }else return false;
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int min)
    {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }


    public void reshow(int time){
//        setVisibility(View.VISIBLE);
        STYLE =VIS;
        invalidate();
        i=0;
        px=getX()+15;py = getY()+15;
        setX(px);
        setY(py);
        widthX = x-getX();
        heightX = y - getY();
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what<=30){
                    float xx=px+widthX*(float)(i)/30;
                    float yy=py + heightX * (float)(i) / 30;
                    setX(xx);
                    setY(yy);
                    i++;
                }
                super.handleMessage(msg);
            }
        };
        new Timer(handler,time).start();
    }
    public void cloesit(int time){

        i=0;
//        px=getX();py = getY();
        widthX = px-15-getX();
        heightX = py-15 - getY();
        x1=getX();
        y1=getY();
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what<=30){
                    if(i==30){
                        STYLE = INVIS;
                        invalidate();
                    }
                    setX(x1+widthX*i/30);
                    setY(y1 + heightX * i / 30);
                    i++;
                }
                super.handleMessage(msg);
            }
        };
        new Timer(handler,time).start();
    }


    protected class Timer extends Thread{
        Handler handler ;
        int time;
        Timer(Handler handler,int time){
            this.handler = handler;
            this.time = time/30;
        }

        @Override
        public void run() {
            try {
                int t=0;
                while(t<=30) {
                    sleep(time);
                    Message m = handler.obtainMessage();
                    m.what = t;
                    handler.sendMessage(m);
                    t++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
