package com.online.shopping.buyme;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.online.shopping.buyme.ParseAccount.CustomerAccount;
import com.online.shopping.buyme.ParseAccount.FavouritesAccount;
import com.online.shopping.buyme.ParseAccount.FeedBack;
import com.online.shopping.buyme.ParseAccount.OrderStatusAccount;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.RatingReviews;
import com.online.shopping.buyme.ParseAccount.RetailerAccount;
import com.online.shopping.buyme.ParseAccount.RetailerProfileAccount;
import com.online.shopping.buyme.ParseAccount.WishListAccount;

public class ParseApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Parse.initialize(this);
    Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
    ParseObject.registerSubclass(RetailerAccount.class);
    ParseObject.registerSubclass(CustomerAccount.class);
    ParseObject.registerSubclass(ProductsAccount.class);
      ParseObject.registerSubclass(RetailerProfileAccount.class);
      ParseObject.registerSubclass(FavouritesAccount.class);
      ParseObject.registerSubclass(WishListAccount.class);
      ParseObject.registerSubclass(OrderStatusAccount.class);
      ParseObject.registerSubclass(RatingReviews.class);
      ParseObject.registerSubclass(FeedBack.class);
      ParseInstallation.getCurrentInstallation().saveInBackground();
      ParsePush.subscribeInBackground("", new SaveCallback() {
          @Override
          public void done(ParseException e) {
              if (e == null) {
                  Log.e("com.parse.push", "successfully subscribed to the broadcast channel.");
              } else {
                  Log.e("com.parse.push", "failed to subscribe for push", e);
              }
          }
      });

  }
}
