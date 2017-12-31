package com.online.shopping.buyme.common;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.R;

public class CustomeActivity extends Activity {

    private Dialog dialog;
    ProgressDialog progress;
    private boolean showing;
    InputMethodManager im;

    @Override
    protected void onResume() {
        ConfigData.currentActivity = this;
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigData.currentActivity = this;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.connection_error);
        dialog.setCancelable(false);
        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage(getResources().getString(R.string.progressmessage));
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        im = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onPause() {
        stopprogress();
        super.onPause();
    }

    public void showDialog()
    {
        stopprogress();
        if(!dialog.isShowing())
            dialog.show();
    }

    public void closeDialog()
    {
        if(dialog.isShowing())
        {
            dialog.dismiss();
            Intent i= getIntent();
            finish();
            startActivity(i);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showprogess()
    {
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if(!showing) {
            showing =true;
            if (progress != null && !isFinishing())
                progress.show();
        }
    }

    public void stopprogress()
    {
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if(showing) {
            showing = false;
            if (progress.isShowing() == true && !isFinishing())
                progress.dismiss();
        }
    }


}
