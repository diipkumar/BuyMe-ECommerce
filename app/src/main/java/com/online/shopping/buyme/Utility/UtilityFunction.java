package com.online.shopping.buyme.Utility;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class UtilityFunction {
    String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    public Activity activity;

    public UtilityFunction(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.activity = activity;
    }
    
    public  boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public void goActivity(Class activity)
    {
        if(this.activity == null)
        {
            Log.d("Cannot open", "Another activity");
            return ;
        }
        Intent i =new Intent(this.activity,activity);
        Log.d("Going for", "Another activity");
        this.activity.startActivity(i);
    }

    public void ToastMessage(String message)
    {
        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
    }

    public void SanckMessage(String message)
    {
     ToastMessage(message);
    }

    public void cleargoActivity(Class activityclass) {
        if(this.activity == null)
        {
            Log.d("Cannot open", "Another activity");
            return ;
        }
        Log.d("Going for", "Another activity");
        Intent intent = new Intent(this.activity, activityclass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        this.activity.startActivity(intent);
    }
}
