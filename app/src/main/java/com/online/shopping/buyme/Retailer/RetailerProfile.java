package com.online.shopping.buyme.Retailer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import com.parse.CountCallback;
import com.rey.material.widget.CheckBox;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Customer.GPSTracker;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.RatingReviews;
import com.online.shopping.buyme.ParseAccount.RetailerProfileAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.placecomplete.GoogleMapServices;
import java.util.List;

public class RetailerProfile extends CustomeActivity {

    UtilityFunction utilityFunction;
    ImageView star1,star2,star3,star4,star5;
    TextView rate;
    EditText name,description,phone,address,mailid,website;
    CheckBox checkBox;
    boolean locationChanged;
    private ImageView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        setListeners();
        SyncData();
    }

    private void setAnimation(){
        RelativeLayout profile1 = (RelativeLayout)findViewById(R.id.layout1);
        LinearLayout shop_info = (LinearLayout)findViewById(R.id.profile_shop_info);
        LinearLayout contact_details = (LinearLayout)findViewById(R.id.profile_contact_details);
        TextView note = (TextView)findViewById(R.id.note);
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fadeanim);
        Animation enter = AnimationUtils.loadAnimation(this,R.anim.layoutanim);
        profile1.startAnimation(enter);
        shop_info.startAnimation(fade);
        contact_details.startAnimation(fade);
        note.startAnimation(fade);
    }

    private void SyncData() {
        showprogess();
        SyncRating();
        ParseQuery<RetailerProfileAccount> query = ParseQuery.getQuery("RetailerProfile");
        query.whereEqualTo("User", ConfigData.currentuser);
        query.findInBackground(new FindCallback<RetailerProfileAccount>() {
            @Override
            public void done(List<RetailerProfileAccount> retailerProfileAccounts, ParseException e) {
                stopprogress();
                if (e != null) {
                    utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                    return;
                }
                if (retailerProfileAccounts.size() > 0) {
                    setData(retailerProfileAccounts.get(0));
                }
            }
        });
    }


    private void SyncRating() {
        ParseQuery<RatingReviews> query = ParseQuery.getQuery("Rating");
        query.whereEqualTo("Retailer", ConfigData.currentuser);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                if (e != null) {
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    return;
                }
                rate.setText(String.valueOf(i));
            }
        });
    }

    private void setData(RetailerProfileAccount retailerProfileAccount) {
        ConfigData.commonFunction.ProfileSaved(retailerProfileAccount.getlocation());
        ConfigData.signuplatlng = new LatLng(retailerProfileAccount.getlocation().getLatitude(),retailerProfileAccount.getlocation().getLongitude());
        name.setText(retailerProfileAccount.get_shop_name());
        description.setText(retailerProfileAccount.get_Description());
        phone.setText(retailerProfileAccount.getPhoneNumber());
        address.setText(retailerProfileAccount.getAddress());
        mailid.setText(retailerProfileAccount.getMailId());
        website.setText(retailerProfileAccount.getWebsite());
        checkBox.setChecked(retailerProfileAccount.getCashOnDelivery());
        int stars = retailerProfileAccount.getStar();
        star1.setImageResource(R.drawable.star_empty);
        star2.setImageResource(R.drawable.star_empty);
        star3.setImageResource(R.drawable.star_empty);
        star4.setImageResource(R.drawable.star_empty);
        star5.setImageResource(R.drawable.star_empty);
        switch (stars)
        {
            case 5:
                star5.setImageResource(R.drawable.star_filled);
            case 4:
                star4.setImageResource(R.drawable.star_filled);
            case 3:
                star3.setImageResource(R.drawable.star_filled);
            case 2:
                star2.setImageResource(R.drawable.star_filled);
            case 1:
                star1.setImageResource(R.drawable.star_filled);
        }
        setAnimation();
    }

    private void setListeners() {
        locationChanged = false;
        utilityFunction = new UtilityFunction(this);
        name = (EditText) findViewById(R.id.shopname);
        description= (EditText) findViewById(R.id.shop_description);
        phone = (EditText) findViewById(R.id.shop_phonenumber);
        address = (EditText) findViewById(R.id.shop_address);
        mailid= (EditText) findViewById(R.id.shop_mail_id);
        website= (EditText) findViewById(R.id.shop_website);
        rate = (TextView) findViewById(R.id.shop_totalrate);
        checkBox = (CheckBox)findViewById(R.id.cb1);
        star1 = (ImageView)findViewById(R.id.shop_star1);
        star2 = (ImageView)findViewById(R.id.shop_star2);
        star3 = (ImageView)findViewById(R.id.shop_star3);
        star4 = (ImageView)findViewById(R.id.shop_star4);
        star5 = (ImageView)findViewById(R.id.shop_star5);
        TextView save = (TextView)findViewById(R.id.shop_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckCondition())
                    return;
                showprogess();
                saveData();
            }
        });
        location = (ImageView) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationChanged = true;
                ConfigData.signuplatlng = null;
                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                if (!gpsTracker.canGetLocation()) {
                    showGpsAlert();
                    return;
                }
                ConfigData.signuplatlng = null;
                Intent i = new Intent(RetailerProfile.this, GoogleMapServices.class);
                i.putExtra("latitude", gpsTracker.getLatitude());
                i.putExtra("longitude", gpsTracker.getLongitude());
                startActivity(i);
            }
        });
    }


    protected void showGpsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    private boolean CheckCondition() {
        if(address.getText().toString().length() ==0||name.getText().toString().length() ==0)
        {
            utilityFunction.SanckMessage("Please Fill all Details");
            return false;
        }
        if(phone.getText().toString().length() ==0)
        {
            utilityFunction.SanckMessage("Phone Number Incorrect");
            return false;
        }
        if(ConfigData.signuplatlng==null)
        {
            location.performClick();
            utilityFunction.SanckMessage("Select Location");
            return false;
        }
        if(mailid.getText().toString().length()!=0&&!utilityFunction.isValidEmail(mailid.getText().toString()))
        {
            utilityFunction.SanckMessage("Invalid Email");
            return false;
        }
        return true;
    }

    private void saveData() {
        if(locationChanged)
        {
            ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
            query.whereEqualTo("User", ConfigData.currentuser);
            query.findInBackground(new FindCallback<ProductsAccount>() {
                @Override
                public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                    if(e != null)
                    {
                        stopprogress();
                        utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        return;
                    }
                    ParseGeoPoint temp = new ParseGeoPoint(ConfigData.signuplatlng.latitude,ConfigData.signuplatlng.longitude);
                    for(ProductsAccount i:productsAccounts)
                    {
                        i.setLocation(temp);
                        i.saveInBackground();
                    }
                    saveRealData();
                }
            });
        }
        else
            saveRealData();
    }

    private void saveRealData() {
        ParseQuery<RetailerProfileAccount> query = ParseQuery.getQuery("RetailerProfile");
        query.whereEqualTo("User", ConfigData.currentuser);
        query.findInBackground(new FindCallback<RetailerProfileAccount>() {
            @Override
            public void done(List<RetailerProfileAccount> retailerProfileAccounts, ParseException e) {
                if(e!=null)
                {
                    stopprogress();
                    utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                    return;
                }
                final RetailerProfileAccount temp;
                if(retailerProfileAccounts.size()>0) {
                    temp = retailerProfileAccounts.get(0);
                }
                else
                {
                    temp = new RetailerProfileAccount();
                    temp.setRated(0);
                    temp.setStar(2);
                }
                    temp.setCashOnDelivery(checkBox.isChecked());
                    temp.set_shop_name(name.getText().toString());
                    temp.setAddress(address.getText().toString());
                    temp.setMailId(mailid.getText().toString());
                    temp.setPhoneNumber(phone.getText().toString());
                    temp.set_Description(description.getText().toString());
                    temp.setWebsite(website.getText().toString());
                    temp.setLocation(new ParseGeoPoint(ConfigData.signuplatlng.latitude,ConfigData.signuplatlng.longitude));
                    temp.setUser(ConfigData.currentuser);
                    temp.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            stopprogress();
                            if(e == null)
                            {
                                ConfigData.commonFunction.ProfileSaved(temp.getlocation());
                                utilityFunction.SanckMessage(getResources().getString(R.string.saved));
                                return;
                            }
                            utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        }
                    });
            }
        });
    }

    @Override
    protected void onPause() {
        stopprogress();
        super.onPause();
    }
}
