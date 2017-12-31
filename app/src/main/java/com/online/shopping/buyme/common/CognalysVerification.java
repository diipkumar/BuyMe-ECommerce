package com.online.shopping.buyme.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CognalysVerification extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String mobile = intent.getStringExtra("mobilenumber");
		//Toast.makeText(context,"Verified Mobile : "+ mobile, Toast.LENGTH_SHORT).show();
		Intent BroadCastIntent = new Intent("SmS_Read");
		context.sendBroadcast(BroadCastIntent);
	}

}