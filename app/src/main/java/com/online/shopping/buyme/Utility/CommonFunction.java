package com.online.shopping.buyme.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.parse.ParseGeoPoint;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.R;

public class CommonFunction {

    Context mcontext;
    public Animation rotate;
    public CommonFunction(Context context)
    {
        rotate = AnimationUtils.loadAnimation(context, R.anim.rotate);
        mcontext = context;
    }
    public SharedPreferences.Editor getEditor()
    {
        SharedPreferences pref = mcontext.getSharedPreferences("MyPref", mcontext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return editor;
    }

    public void savelogin()
    {
        getEditor().putString("isCustomer", String.valueOf(ConfigData.isCustomer)).putString("mobile", ConfigData.currentuser).commit();
    }

    public void ProfileSaved(ParseGeoPoint getlocation)
    {
        getEditor().putBoolean("profileSaved", true).putString("Retailerlatitude",String.valueOf(getlocation.getLatitude())).putString("Retailerlongitude",String.valueOf(getlocation.getLongitude())).commit();
    }

    public boolean isProfileSaved()
    {
        SharedPreferences pref = mcontext.getSharedPreferences("MyPref", mcontext.MODE_PRIVATE);
        boolean isProfileSaved= pref.getBoolean("profileSaved", false);
        if(isProfileSaved)
            ConfigData.location = new ParseGeoPoint(Double.valueOf(pref.getString("Retailerlatitude","0")),Double.valueOf(pref.getString("Retailerlongitude","0")));
        return isProfileSaved;
    }

    public boolean retrievelogin()
    {
        SharedPreferences pref = mcontext.getSharedPreferences("MyPref", mcontext.MODE_PRIVATE);
        String isCustomer= pref.getString("isCustomer", "novalue");
        String mobile = pref.getString("mobile","nonumber");
        if(isCustomer.equals("novalue")||mobile.equals("nonumber"))
            return false;
        ConfigData.isCustomer = Boolean.valueOf(isCustomer);
        ConfigData.currentuser = mobile;
        return true;
    }

    public void Clearlogin()
    {
        SharedPreferences.Editor pref = mcontext.getSharedPreferences("MyPref", mcontext.MODE_PRIVATE).edit();
        pref.clear();
        pref.commit();
    }

}
