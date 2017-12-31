package com.online.shopping.buyme.RegisterUser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.online.shopping.buyme.LoginAndSignup;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.RetailerExtras.RetailerProductCard;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.RetailerAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.ProductDBhelper;

import java.util.List;

public class RetailerAccess {
    public RetailerAccount currentuser = null;

    public RetailerAccess()
    {
        currentuser = null;
    }

    public void signup(final String name,final String password,final UtilityFunction utility)
    {
        ParseQuery<RetailerAccount> query = ParseQuery.getQuery("RetailerAccount");
        query.whereEqualTo("Username", name);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                if (e == null) {
                    if(i>0) {
                        ConfigData.currentActivity.stopprogress();
                        utility.SanckMessage(utility.activity.getResources().getString(R.string.mobile_number_already_exist));
                        return;
                    }else {
                        Createaccount(name,password,utility);
                    }
                } else {
                    ConfigData.currentActivity.stopprogress();
                    utility.SanckMessage(utility.activity.getResources().getString(R.string.networkerror));
                }
            }
        });
    }

    private void Createaccount(final String name, final String password, final UtilityFunction utility) {
        RetailerAccount temp = new RetailerAccount(name,password);
        temp.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    login(name,password,utility);
                    utility.SanckMessage(utility.activity.getResources().getString(R.string.Registeration_success));
                }
                else
                {
                    ConfigData.currentActivity.stopprogress();
                    utility.SanckMessage(utility.activity.getResources().getString(R.string.networkerror));
                }
            }
        });
    }

    public void login(final String username, final String password, final UtilityFunction utility) {
        ParseQuery<RetailerAccount> query = ParseQuery.getQuery("RetailerAccount");
        query.whereEqualTo("Username", username);
        query.findInBackground(new FindCallback<RetailerAccount>() {
            public void done(List<RetailerAccount> Users, ParseException e) {
                if (e == null) {
                    if(Users.size()==0) {
                            ConfigData.currentActivity.stopprogress();
                            utility.SanckMessage(utility.activity.getResources().getString(R.string.MobileNumber_Not_Registered));
                            ((LoginAndSignup)utility.activity).Do_Signup();
                            return;
                        }
                        if(Users.get(0).getpassword().equals(password)== false)
                        {
                            ConfigData.currentActivity.stopprogress();
                            utility.SanckMessage(utility.activity.getResources().getString(R.string.Password_Incorrect));
                            return;
                        }
                        SyncallData(utility,username);
            }
            else
                {
                    ConfigData.currentActivity.stopprogress();
                    utility.SanckMessage(utility.activity.getResources().getString(R.string.networkerror));
                }
    }

            private void SyncallData(final UtilityFunction utility, final String username) {
                final ProductDBhelper Database = new ProductDBhelper(utility.activity);
                ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
                query.whereEqualTo("User", username);
                query.findInBackground(new FindCallback<ProductsAccount>() {
                    @Override
                    public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                        if (e != null) {
                            ConfigData.currentActivity.stopprogress();
                            utility.SanckMessage(utility.activity.getResources().getString(R.string.networkerror));
                            return;
                        }
                        Database.CheckAndDeleteDeletedProducts(productsAccounts,username);
                        getRetailerProductCard(0,productsAccounts,utility,Database,username);
                    }
                });
            }
        });
    }


    private void getRetailerProductCard(final int pos, final List<ProductsAccount> productsAccounts, UtilityFunction utility, ProductDBhelper database, String username) {
        if(pos > productsAccounts.size()-1)
        {
            LoginSuccess(utility,username);
            return;
        }
        final ProductsAccount Currentproduct = productsAccounts.get(pos);

        String imagescount = Currentproduct.getimages();
        Bitmap bmp1,bmp2,bmp3;
        RetailerProductCard card = database.CheckAndReturnProduct(Currentproduct);
        if(card!=null)
        {
            getRetailerProductCard(pos + 1, productsAccounts, utility, database, username);
            return;
        }
        if (imagescount.contains("1"))
            bmp1 = getBitmapFromPArseImage(Currentproduct.getParse1());
        else
            bmp1 = BitmapFactory.decodeResource(utility.activity.getResources(), RetailerMain.DefaultImage);

        if (imagescount.contains("2"))
            bmp2 = getBitmapFromPArseImage(Currentproduct.getParse2());
        else
            bmp2 = BitmapFactory.decodeResource(utility.activity.getResources(), RetailerMain.DefaultImage);

        if (imagescount.contains("3"))
            bmp3 = getBitmapFromPArseImage(Currentproduct.getParse3());
        else
            bmp3 = BitmapFactory.decodeResource(utility.activity.getResources(), RetailerMain.DefaultImage);
        card = new RetailerProductCard (utility.activity,bmp1, bmp2, bmp3,Currentproduct);
        database.InsertProduct(card);
        getRetailerProductCard(pos + 1, productsAccounts, utility, database, username);
    }

    private Bitmap getBitmapFromPArseImage(ParseFile parse1) {
        byte[] data = new byte[0];
        try {
            data = parse1.getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private void LoginSuccess(final UtilityFunction utility, final String username) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", username);
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ConfigData.currentActivity.stopprogress();
                if(e!=null)
                {
                    utility.SanckMessage(utility.activity.getResources().getString(R.string.networkerror));
                    return;
                }
                ConfigData.currentuser=username;
                utility.SanckMessage(utility.activity.getResources().getString(R.string.Login_success));
                utility.cleargoActivity(RetailerMain.class);
                utility.activity.finish();
                ConfigData.commonFunction.savelogin();
            }
        });
    }
}
