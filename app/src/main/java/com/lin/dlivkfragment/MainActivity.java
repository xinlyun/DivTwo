package com.lin.dlivkfragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.lin.dlivkfragment.fragment.MusicPlayer;
import com.lin.dlivkfragment.fragment.NaviStart3Activity;
import com.lin.dlivkfragment.interfaces.IWindowControl;
import com.lin.dlivkfragment.view.MyRelaLayout;


public class MainActivity extends Activity implements View.OnClickListener{
    private MyRelaLayout rll1,rll2;
    private ImageView img0,img1;
    private FragmentManager fragmentManager;
    private int height;
    private float posi,dis ;
    private VelocityTracker vTracker = null;
    private boolean Flag = false;
    ViewGroup.LayoutParams params1,params2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        height = display.getHeight();

        initView();
        Toast.makeText(this,"height:"+height,Toast.LENGTH_SHORT).show();
    }

    private void initView(){

        FragmentTransaction transaction =getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment1,new NaviStart3Activity());
        transaction.add(R.id.fragment2,new MusicPlayer());
        transaction.commit();

        rll1  = (MyRelaLayout) findViewById(R.id.rll1);
        rll2  = (MyRelaLayout) findViewById(R.id.rll2);
        rll2.setStyle(1);
        rll1.inputActivity(this);
        rll2.inputActivity(this);
        params1 = rll1.getLayoutParams();
        params2 = rll2.getLayoutParams();

        ViewTreeObserver observer = findViewById(R.id.main).getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                findViewById(R.id.main).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int h = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
                height = h;
                params1.height = rll1.getHeight();
                params2.height = rll2.getHeight();
            }
        });


        img0 = (ImageView) findViewById(R.id.img0);
        img1 = (ImageView) findViewById(R.id.img1);
        img0.setOnClickListener(this);
        img1.setOnClickListener(this);
    }
    long time = 0;
    float speed = 0;
    public void moving(MotionEvent event){
        rll1.setVisibility(View.VISIBLE);
        rll2.setVisibility(View.VISIBLE);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                posi = event.getRawY();
                if(vTracker == null){
                    vTracker = VelocityTracker.obtain();
                }else{
                    vTracker.clear();
                }
                vTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                vTracker.addMovement(event);
                vTracker.computeCurrentVelocity(5);

                dis = event.getRawY() - posi;
                speed = dis/(System.currentTimeMillis()-time);
                time = System.currentTimeMillis();

                if(dis!=0&&iWindowControl!=null)
                    iWindowControl.dismiss();
//                    ((NaviStart3Activity)getFragmentManager().findFragmentById(R.id.mfragment1)).dismiss();
                posi = event.getRawY();
                params1.height = (int) ((float) params1.height + dis );
                params2.height = (int) ((float) params2.height - dis);
//                        if (params1.height == -1 || params1.height == -2) {
//                            params1.height = 3;
//                            params2.height = -3;
//                        } else if (params2.height == -1 || params2.height == -2) {
//                            params1.height = -3;
//                            params2.height = 3;
//                        }
                rll1.setLayoutParams(params1);
                rll2.setLayoutParams(params2);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("MainActivity","speed1:"+vTracker.getYVelocity()+"   speed2"+speed*5);
                changePosi(vTracker.getYVelocity());

                break;
        }

    }
    public void moving(int style,float x,float y){
        switch (style){
            case MotionEvent.ACTION_DOWN:
                posi = y;
                break;
            case MotionEvent.ACTION_MOVE:
                dis =y - posi;

                posi = y;
                params1.height = (int) ((float) params1.height + dis );
                params2.height = (int) ((float) params2.height - dis);
//                        if (params1.height == -1 || params1.height == -2) {
//                            params1.height = 3;
//                            params2.height = -3;
//                        } else if (params2.height == -1 || params2.height == -2) {
//                            params1.height = -3;
//                            params2.height = 3;
//                        }
                rll1.setLayoutParams(params1);
                rll2.setLayoutParams(params2);
                break;
            case MotionEvent.ACTION_UP:
                if(dis>=0)
                    changePosi((int)dis);
                else changePosi((int)-dis);
                break;
        }
    }

    private void changePosi(float speed){


        if(Math.abs(speed)<2.50){
            if(rll1.getHeight()>rll2.getHeight()*2){
                new MyThread2(rll1.getHeight(),height).start();
            }else if (rll2.getHeight()>rll1.getHeight()*2){
                new MyThread2(rll1.getHeight(),0).start();
            }else {
                Log.d("MainActivity", "rl1_H:" + rll1.getHeight() + "  rl2_H:" + rll2.getHeight() + "  height:" + height / 2);
                new MyThread2(rll1.getHeight(),height/2).start();
            }
        }else if(Math.abs(speed)<10.0){
            if(rll1.getHeight()>rll2.getHeight()*2){
                MyThread2 myThread2 = new MyThread2(rll1.getHeight(),height);
                myThread2.setspeed(speed);
                myThread2.start();
            }else if (rll2.getHeight()>rll1.getHeight()*2){
                MyThread2 myThread2 = new MyThread2(rll1.getHeight(),0);
                myThread2.setspeed(speed);
                myThread2.start();
            }else {
                Log.d("MainActivity", "rl1_H:" + rll1.getHeight() + "  rl2_H:" + rll2.getHeight() + "  height:" +height/2);
                MyThread2 myThread2 = new MyThread2(rll1.getHeight(),height/2);
                myThread2.setspeed(speed);
                myThread2.start();
            }
        }else {
            if(speed<0){
                //上滑
                if(rll1.getHeight()>rll2.getHeight()){
                    MyThread2 myThread2 = new MyThread2(rll1.getHeight(),height/2);
                    myThread2.setspeed(Math.abs(speed));
                    myThread2.start();
                }else {
                    MyThread2 myThread2 = new MyThread2(rll1.getHeight(),0);
                    myThread2.setspeed(Math.abs(speed));
                    myThread2.start();
                }
            }else {
                //下滑
                if(rll1.getHeight()>rll2.getHeight()){
                    MyThread2 myThread2 = new MyThread2(rll1.getHeight(),height);
                    myThread2.setspeed(Math.abs(speed));
                    myThread2.start();
                }else {
                    MyThread2 myThread2 = new MyThread2(rll1.getHeight(),height/2);
                    myThread2.setspeed(Math.abs(speed));
                    myThread2.start();
                }
            }
        }

    }
    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(iWindowControl!=null)
                if (msg.arg1==0)
                    iWindowControl.reshow();
//                ((NaviStart3Activity)getFragmentManager().findFragmentById(R.id.mfragment1)).reshow();
                else
                    iWindowControl.dismiss();
//                ((NaviStart3Activity)getFragmentManager().findFragmentById(R.id.mfragment1)).dismiss();
            int rl1_height = msg.what;
            params1.height = rl1_height;
            params2.height = height-rl1_height;
            rll1.setLayoutParams(params1);
            rll2.setLayoutParams(params2);
            if(params1.height==0){
                rll1.setVisibility(View.GONE);
                img0.setVisibility(View.VISIBLE);
            }
            if (params2.height==0){rll2.setVisibility(View.GONE);img1.setVisibility(View.VISIBLE);}
            if(params1.height == params2.height){
                rll1.setVisibility(View.VISIBLE);
                rll2.setVisibility(View.VISIBLE);
                img0.setVisibility(View.GONE);
                img1.setVisibility(View.GONE);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img0:
                rll1.setVisibility(View.VISIBLE);
                img0.setVisibility(View.GONE);
                new MyThread(rll1.getHeight(),height/2).start();
                break;
            case R.id.img1:
                rll2.setVisibility(View.VISIBLE);
                img1.setVisibility(View.GONE);
                new MyThread(rll1.getHeight(),height/2).start();
                break;

        }
    }
//    public void change(boolean b){
////        final ViewGroup.LayoutParams params1,params2;
////        params1 = rll1.getLayoutParams();
////        params2 = rll2.getLayoutParams();
////
////
////
////        handler = new Handler(){
////            @Override
////            public void handleMessage(Message msg) {
////                params1.height--;
////                params2.height++;
////                rll1.setLayoutParams(params1);
////                rll2.setLayoutParams(params2);
////
////
////                super.handleMessage(msg);
////            }
////        };
//
////        new MyThread().start();
//
//
//    }

    class MyThread extends Thread{
        int currX,toX;
        MyThread(int currx,int posix){
            this.currX = currx;
            this.toX = posix;
        }
        @Override
        public void run() {
            super.run();
            float x ;
            Log.d("MainActivity","MyThread:"+currX+"  "+toX);
            if(currX<toX){
                x = (float)(toX-currX)/40;
                while (currX<toX){
                    Log.d("MainActivity","MyThreadxxxx:"+currX);
                    currX+=x;
                    Message message = handler.obtainMessage();
                    message.what = currX;
                    message.arg1 = 1;
                    handler.sendMessage(message);
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message1 = handler.obtainMessage();
                message1.what = toX;
                message1.arg1 = 1;
                handler.sendMessage(message1);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = handler.obtainMessage();
                message.what = toX;
                message.arg1 = 0;
                handler.sendMessage(message);
            }
            else if(currX>toX){

                x = (float)(currX-toX)/40;
                while (currX>toX){
                    Log.d("MainActivity","MyThreadyyyy:"+currX);
                    currX-=x;
                    Message message = handler.obtainMessage();
                    message.what = currX;
                    message.arg1 = 1;
                    handler.sendMessage(message);
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message1 = handler.obtainMessage();
                message1.what = toX;
                message1.arg1 = 1;
                handler.sendMessage(message1);
                try {


                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = handler.obtainMessage();
                message.what = toX;
                message.arg1 = 0;
                handler.sendMessage(message);
            }
        }
    }
    class MyThread2 extends Thread {
        int currX, toX;
        boolean flag = false;
        float speed = 0f;

        MyThread2(int currx, int posix) {
            if (posix == height / 2) {
                img1.setVisibility(View.GONE);
                img0.setVisibility(View.GONE);
            } else if (posix == 0) {
                img0.setVisibility(View.VISIBLE);
                img1.setVisibility(View.GONE);
            } else {
                img1.setVisibility(View.VISIBLE);
                img0.setVisibility(View.GONE);
            }
            this.currX = currx;
            this.toX = posix;
        }

        void setspeed(float speed) {
            this.speed = speed;
            if(speed>2.50f)
                flag = true;
        }

        @Override
        public void run() {
            super.run();
            double x;
            if(!flag) {
                if (currX < toX) {
                    x = (double) (toX - currX) / 2;
                    for (int i = 0; i <= 40; i++) {
                        int curr;
                        if (i < 20) {
                            curr = (int) (currX + x * (-Math.pow(-(i / 20f - 1f), 1 / 3.0) + 1));
                        } else {
                            curr = (int) (currX + x * (Math.pow((i / 20f - 1f), 1 / 3.0) + 1));
                        }
                        if (curr != 0) {
                            Message message = handler.obtainMessage();
                            message.what = curr;
                            message.arg1 = 1;
                            handler.sendMessage(message);
                        }

                        try {
                            sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = handler.obtainMessage();
                    message.what = toX;
                    message.arg1 = 0;
                    handler.sendMessage(message);
                } else if (currX > toX) {
                    x = (double) (currX - toX) / 2;
                    for (int i = 0; i <= 40; i++) {
                        int curr;
                        if (i < 20) {
                            curr = (int) (currX - x * (-Math.pow(-(i / 20f - 1f), 1 / 3.0) + 1));
                        } else {
                            curr = (int) (currX - x * (Math.pow((i / 20f - 1f), 1 / 3.0) + 1));
                        }
                        Log.d("MainActivity", "MyThreadyyy222: " + curr);
                        if (curr != 0) {
                            Message message = handler.obtainMessage();
                            message.what = curr;
                            message.arg1 = 1;
                            handler.sendMessage(message);
//                        handler.sendEmptyMessage(curr);
                        }
                        try {
                            sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = handler.obtainMessage();
                    message.what = toX;
                    if (toX > 0) message.arg1 = 0;
                    else message.arg1 = 1;
                    handler.sendMessage(message);
//                handler.sendEmptyMessage(toX);
                }
            }
            else
                {
//
//
                    if (currX < toX) {
                        while (currX<=toX){
                            Message message = handler.obtainMessage();
                            message.arg1 = 1;
                            currX = currX+(int)speed;
                            if(currX>toX)break;
                            message.what = currX;
                            handler.sendMessage(message);
                            try {
                                sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Message message = handler.obtainMessage();
                        message.arg1 = 0;
                        message.what = toX;
                        handler.sendMessage(message);
                    }else {
                        while (currX>=toX){
                            Message message = handler.obtainMessage();
                            message.arg1 = 1;
                            currX = currX-(int)speed;
                            if(currX<toX)break;
                            message.what = currX;
                            handler.sendMessage(message);
                            try {
                                sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
//                        Message message = handler.obtainMessage();
//                        message.arg1 = 0;
//                        message.what = toX;
//                        handler.sendMessage(message);
                        Message message = handler.obtainMessage();
                        message.what = toX;
                        if (toX > 0) message.arg1 = 0;
                        else message.arg1 = 1;
                        handler.sendMessage(message);
                    }

                }

        }
    }

    IWindowControl iWindowControl;

    public void setWindowControl(IWindowControl iWindowControl) {
        this.iWindowControl = iWindowControl;
    }

}
