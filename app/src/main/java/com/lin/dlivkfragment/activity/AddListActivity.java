package com.lin.dlivkfragment.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.util.ActivityManager;
import com.lin.dlivkfragment.util.MusicUtils;


/**
 * 添加播放列表界面
 * Created by zzq on 15-8-15.
 */
public class AddListActivity extends Activity implements View.OnClickListener{

    private EditText etListName;
    private ImageButton ibOk;
    private ImageButton ibCancel;
    private MusicUtils musicUtils;
    private static MusicListActivity musicListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        initView();
        musicUtils = new MusicUtils();
        ActivityManager.addActivity(this);

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.8f;
        window.setAttributes(lp);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    private void createList(){
        String listName = etListName.getText().toString();
        if(listName.equals("")) return;
        musicUtils.createList(listName);
        musicListActivity.refreshList();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ib_addlist_ok:
                createList();
                finish();
                break;

            case R.id.ib_addlist_cancel:
                finish();
                break;

            default:
                break;
        }
    }

    private void initView(){
        etListName = (EditText) findViewById(R.id.et_addlist_name);
        ibOk = (ImageButton) findViewById(R.id.ib_addlist_ok);
        ibCancel = (ImageButton) findViewById(R.id.ib_addlist_cancel);
        ibOk.setOnClickListener(this);
        ibCancel.setOnClickListener(this);
    }

    public static void actionStart(Context context, MusicListActivity activity){
        Intent intent = new Intent(context, AddListActivity.class);
        context.startActivity(intent);
        musicListActivity = activity;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        lp.width = getResources().getDimensionPixelSize(R.dimen.add_list_width);
        lp.height = getResources().getDimensionPixelSize(R.dimen.add_list_height);
        getWindowManager().updateViewLayout(view, lp);
    }
}
