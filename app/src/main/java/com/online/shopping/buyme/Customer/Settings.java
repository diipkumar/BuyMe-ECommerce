package com.online.shopping.buyme.Customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.MainActivity;
import com.online.shopping.buyme.ParseAccount.FeedBack;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.placecomplete.GoogleMapServices;

public class Settings extends CustomeActivity implements View.OnClickListener {
    UtilityFunction utilityFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        utilityFunction = new UtilityFunction(this);
        setupListeners();
    }

    private void setupListeners() {
        TextView favourites,wishlist,friends_wishlist,change_location,share_app,logout,feedback;
        favourites = (TextView)findViewById(R.id.settings_favourites);
        wishlist= (TextView)findViewById(R.id.settingss_wishlist);
        friends_wishlist= (TextView)findViewById(R.id.settings_friends_wishlist);
        change_location= (TextView)findViewById(R.id.settings_change_location);
        share_app= (TextView)findViewById(R.id.settings_share_app);
        feedback= (TextView)findViewById(R.id.settings_feedback);
        logout = (TextView)findViewById(R.id.settings_logout);
        favourites.setOnClickListener(this);
        wishlist.setOnClickListener(this);
        friends_wishlist.setOnClickListener(this);
        change_location.setOnClickListener(this);
        share_app.setOnClickListener(this);
        feedback.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.settings_favourites:
                utilityFunction.goActivity(Favourites.class);
                break;
            case R.id.settingss_wishlist:
                utilityFunction.goActivity(WishList.class);
                break;
            case R.id.settings_logout:
                Do_Logout();
                break;
            case R.id.settings_friends_wishlist:
                utilityFunction.goActivity(Friends_WishList.class);
                break;
            case R.id.settings_share_app:
                Share_App();
                break;
            case R.id.settings_change_location:
                change_location();
                break;
            case R.id.settings_feedback:
                Show_Feedback();
                break;
        }
    }

    private void Show_Feedback() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedback_dialog);
        final EditText message;
        message= (EditText)dialog.findViewById(R.id.feedback_message);
        TextView submit;
        submit= (TextView)dialog.findViewById(R.id.feedback_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().toString().length()==0) {
                    utilityFunction.ToastMessage("Enter Message");
                    return;
                }
                showprogess();
                FeedBack feedback = new FeedBack(message.getText().toString());
                feedback.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        stopprogress();
                        if(e==null)
                            utilityFunction.ToastMessage(getResources().getString(R.string.feedback_saved));
                        else
                            utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    }
                });
            }
        });
        dialog.show();

    }

    private void change_location() {
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        if (!gpsTracker.canGetLocation()) {
            showGpsAlert();
            return;
        }
        ConfigData.signuplatlng = null;
        Intent i = new Intent(this,GoogleMapServices.class);
        i.putExtra("latitude",gpsTracker.getLatitude());
        i.putExtra("longitude",gpsTracker.getLongitude());
        startActivityForResult(i,CustomerBaseAcctivity.MAP_CODE);
    }

    protected void showGpsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void Share_App() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = getResources().getString(R.string.ShareAppBody);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void Do_Logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.Logout));
        alertDialog.setMessage(getResources().getString(R.string.LogoutQuestion));
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showprogess();
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("user", "");
                installation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        stopprogress();
                        if(e == null)
                        {
                            ConfigData.commonFunction.Clearlogin();
                            utilityFunction.cleargoActivity(MainActivity.class);
                        }
                        else
                        {
                            utilityFunction.ToastMessage("NetworkError");
                        }
                    }
                });
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

}
