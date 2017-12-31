package com.online.shopping.buyme.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.online.shopping.buyme.Config.ConfigData;

public class NetworkChange extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context,final Intent intent)
    {
        if(ConfigData.currentActivity==null)
            return;
        final ConnectionDetector connectionDetector =new ConnectionDetector(context);
        if(!connectionDetector.isConnectingToInternet())
        {
            ConfigData.currentActivity.showDialog();
        }
        else
        {
            ConfigData.currentActivity.closeDialog();
        }
    }
}
