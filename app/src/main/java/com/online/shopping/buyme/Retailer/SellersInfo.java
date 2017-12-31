package com.online.shopping.buyme.Retailer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Extras.AnimatedExpandableListView;
import com.online.shopping.buyme.Extras.ShopReviewAdapter;
import com.online.shopping.buyme.ParseAccount.FavouritesAccount;
import com.online.shopping.buyme.ParseAccount.RatingReviews;
import com.online.shopping.buyme.ParseAccount.RetailerProfileAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;

import java.util.List;

public class SellersInfo extends CustomeActivity {

    String user;
    UtilityFunction utilityFunction;
    private FloatingActionButton fab;
    RetailerProfileAccount retailerAccount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellers_info);
        user = getIntent().getStringExtra("User");
        setListeners();
        syncProfileData();
        SyncUserRevviews();
        SyncRating();
    }

    private void SyncFavourties() {
        ParseQuery<FavouritesAccount> query = ParseQuery.getQuery("Favourites");
        query.whereEqualTo("Customer", ConfigData.currentuser);
        query.whereEqualTo("Retailer",retailerAccount.getUser());
        query.findInBackground(new FindCallback<FavouritesAccount>() {
            @Override
            public void done(final List<FavouritesAccount> favouriteses, ParseException e) {
                if(e==null)
                {
                    if(favouriteses.size()>0)
                    {
                        fab.setTag(true);
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.favourite_enabled));
                    }
                    else
                    {
                        fab.setTag(false);
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.favourite_disabled));
                    }
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FavourtiesClicked();
                        }
                    });
                }
                else
                {
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                }
            }
        });}

    private void FavourtiesClicked() {
               if(retailerAccount==null)
                   return;
                final boolean isenabled = (boolean) fab.getTag();
                fab.setTag(!isenabled);
                if(isenabled)
                {
                    fab.setImageResource(R.drawable.favourite_disabled);
                    ParseQuery<FavouritesAccount> query = ParseQuery.getQuery("Favourites");
                    query.whereEqualTo("Customer", ConfigData.currentuser);
                    query.findInBackground(new FindCallback<FavouritesAccount>() {
                        @Override
                        public void done(List<FavouritesAccount> favouriteses, ParseException e) {
                            if(e!=null)
                            {
                                fab.setTag(true);
                                fab.setImageResource(R.drawable.favourite_disabled);
                                utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                                return;
                            }
                            if(favouriteses.size()==0)
                                return;
                            favouriteses.get(0).deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        fab.setTag(true);
                                        fab.setImageResource(R.drawable.favourite_disabled);
                                        utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                                    }
                                }
                            });
                        }
                    });
                }
                else
                {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.favourite_enabled));
                    final FavouritesAccount favourite = new FavouritesAccount(retailerAccount.getUser(),ConfigData.currentuser,retailerAccount.get_shop_name());
                    favourite.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e!=null)
                            {
                                fab.setTag(false);
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.favourite_enabled));
                                utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                            }
                        }
                    });
                }
            }

    private void SyncRating() {
        ParseQuery<RatingReviews> query = ParseQuery.getQuery("Rating");
        query.whereEqualTo("Retailer", user);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
            if(e!= null)
            {
                utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                return;
            }
                ((TextView)findViewById(R.id.rate_number)).setText(String.valueOf(i)+" ");
            }
        });
    }

    private void SyncUserRevviews() {
        ParseQuery<RatingReviews> query = ParseQuery.getQuery("Rating");
        query.whereEqualTo("Retailer", user);
        query.setLimit(10);
        query.findInBackground(new FindCallback<RatingReviews>() {
            @Override
            public void done(List<RatingReviews> ratingReviewses, ParseException e) {
                if(e != null) {
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    return;
                }
                final AnimatedExpandableListView ShopreviewsList= (AnimatedExpandableListView) findViewById(R.id.shop_Reviews);
                ShopReviewAdapter ShopinfoAdapter = new ShopReviewAdapter(getApplicationContext());
                ShopinfoAdapter.setData(ratingReviewses);
                ShopreviewsList.setAdapter(ShopinfoAdapter);
            }
        });
    }

    private void syncProfileData() {
        showprogess();
        ParseQuery<RetailerProfileAccount> query = ParseQuery.getQuery("RetailerProfile");
        query.whereEqualTo("User", user);
        query.getFirstInBackground(new GetCallback<RetailerProfileAccount>() {
            @Override
            public void done(RetailerProfileAccount retailerProfileAccount, ParseException e) {
                stopprogress();
                if(retailerProfileAccount==null ||e != null) {
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    return;
                }

                retailerAccount = retailerProfileAccount;
                SyncFavourties();
                TextView name,description,cod,codtoast;
                name = (TextView)findViewById(R.id.shopinfo_name);
                description= (TextView)findViewById(R.id.shopinfo_description);
                cod= (TextView)findViewById(R.id.shopinfo_cod);
                codtoast= (TextView)findViewById(R.id.shop_info_cod_toast);
                name.setText(retailerProfileAccount.get_shop_name());
                description.setText(retailerProfileAccount.get_Description());
                if(retailerProfileAccount.getCashOnDelivery())
                   cod.setText("Available");
                else
                    cod.setText("Not available");
                codtoast.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SellersInfo.this,getResources().getString(R.string.cash_on_delivery),Toast.LENGTH_LONG).show();
                    }
                });

                TextView number,address,mail,website;
                number= (TextView)findViewById(R.id.shop_contact_number);
                address= (TextView)findViewById(R.id.shop_contact_address);
                mail= (TextView)findViewById(R.id.shop_info_contact_mail);
                website= (TextView)findViewById(R.id.shop_info_website);
                number.setText(retailerProfileAccount.getPhoneNumber());
                address.setText(retailerProfileAccount.getAddress());
                mail.setText(retailerProfileAccount.getMailId());
                website.setText(retailerProfileAccount.getWebsite());

                ImageView star1,star2,star3,star4,star5;
                star1 = (ImageView) findViewById(R.id.sellersimage1);
                star2 = (ImageView) findViewById(R.id.sellersimage2);
                star3 = (ImageView) findViewById(R.id.sellersimage3);
                star4 = (ImageView) findViewById(R.id.sellersimage4);
                star5 = (ImageView) findViewById(R.id.sellersimage5);
                switch (retailerProfileAccount.getStar())
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
            }});
    }

    private void setListeners() {
        utilityFunction = new UtilityFunction(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }
}
