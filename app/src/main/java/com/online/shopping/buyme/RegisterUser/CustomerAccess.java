package com.online.shopping.buyme.RegisterUser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.online.shopping.buyme.LoginAndSignup;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Customer.CustomerMain;
import com.online.shopping.buyme.CustomerExtras.WishedDBhelper;
import com.online.shopping.buyme.Extras.SimpleProductCard;
import com.online.shopping.buyme.ParseAccount.CustomerAccount;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.WishListAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.List;

public class CustomerAccess {

    public CustomerAccess()
    {}
    public void signup(final String name,final String password,final UtilityFunction utility)
    {
        ParseQuery<CustomerAccount> query = ParseQuery.getQuery("CustomerAccount");
        query.whereEqualTo("Username", name);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                if (e == null) {
                    if (i > 0) {
                        ConfigData.currentActivity.stopprogress();
                        utility.SanckMessage(utility.activity.getResources().getString(R.string.mobile_number_already_exist));
                    } else {
                        Createaccount(name, password, utility);
                    }
                } else {
                    ConfigData.currentActivity.stopprogress();
                    utility.SanckMessage(utility.activity.getResources().getString(R.string.networkerror));
                }
            }
        });
    }

    private void Createaccount(final String name, final String password, final UtilityFunction utility) {
        CustomerAccount temp = new CustomerAccount(name,password);
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
        ParseQuery<CustomerAccount> query = ParseQuery.getQuery("CustomerAccount");
        query.whereEqualTo("Username", username);
        query.findInBackground(new FindCallback<CustomerAccount>() {
            public void done(List<CustomerAccount> Users, ParseException e) {
                ConfigData.currentActivity.stopprogress();
                if (e == null) {
                    if(Users.size()==0) {
                        utility.SanckMessage(utility.activity.getResources().getString(R.string.MobileNumber_Not_Registered));
                        ((LoginAndSignup)utility.activity).Do_Signup();
                        return;
                        }

                    if(!Users.get(0).getpassword().equals(password))
                    {
                        utility.SanckMessage(utility.activity.getResources().getString(R.string.Password_Incorrect));
                        return;
                    }
                    SyncallData(utility,username);
                } else {
                    utility.SanckMessage(utility.activity.getResources().getString(R.string.networkerror));
                }
            }

            private void SyncallData(final UtilityFunction utility, final String username) {
                ConfigData.currentActivity.showprogess();
                ConfigData.currentuser = username;
                final WishedDBhelper wishedDBhelper = new WishedDBhelper(utility.activity);
                ParseQuery<WishListAccount> query = ParseQuery.getQuery("WishList");
                query.whereEqualTo("User", username);
                query.findInBackground(new FindCallback<WishListAccount>() {
                    @Override
                    public void done(List<WishListAccount> wishListAccounts, ParseException e) {
                        if(e!=null) {
                            ConfigData.currentActivity.stopprogress();
                            utility.SanckMessage(utility.activity.getResources().getString(R.string.networkerror));
                        }
                        else
                        {
                            wishedDBhelper.clearUserWishList(username);
                            final List<String> objectid = new ArrayList<>();
                            for(WishListAccount i:wishListAccounts) {
                                objectid.add(i.getObject());
                                wishedDBhelper.InsertProduct(i.getObject());
                            }
                            ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
                            query.whereContainedIn("objectId", objectid);
                            query.findInBackground(new FindCallback<ProductsAccount>() {
                                @Override
                                public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                                    if (e != null) {
                                        ConfigData.currentActivity.stopprogress();
                                        utility.ToastMessage("Error in Syncing Data");
                                        return;
                                    }
                                    wishedDBhelper.close();
                                    ProductDBhelper productDBhelper = new ProductDBhelper(utility.activity);
                                    getSimpleProductCard(0, productsAccounts,utility,productDBhelper,username);
                                }
                            });

                        }
                    }
                });
            }
        });
    }

    private void getSimpleProductCard(final int pos, final List<ProductsAccount> productsAccounts, final UtilityFunction utilityFunction, final ProductDBhelper productDBhelper, final String username) {
        if(pos > productsAccounts.size()-1)
        {
            productDBhelper.close();
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", username);
            installation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    ConfigData.currentActivity.stopprogress();
                    if (e != null) {
                        utilityFunction.SanckMessage(utilityFunction.activity.getResources().getString(R.string.networkerror));
                        return;
                    }
                    ConfigData.currentuser = username;
                    utilityFunction.SanckMessage(utilityFunction.activity.getResources().getString(R.string.Login_success));
                    utilityFunction.cleargoActivity(CustomerMain.class);
                    utilityFunction.activity.finish();
                    ConfigData.commonFunction.savelogin();
                }
            });
            return;
        }
        final ProductsAccount Currentproduct = productsAccounts.get(pos);
        String imagescount = Currentproduct.getimages();
        final Bitmap[] bmp1 = new Bitmap[1];
        final Bitmap bmp2 = null,bmp3 = null;
        final SimpleProductCard[] card = {productDBhelper.CheckAndReturnSimpleProductCard(Currentproduct)};
        if(card[0] !=null)
        {
            getSimpleProductCard(pos + 1, productsAccounts,utilityFunction,productDBhelper, username);
            return;
        }
        if (imagescount.contains("1"))
        {
            Currentproduct.getParse1().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(e!=null)
                    {
                        utilityFunction.SanckMessage(utilityFunction.activity.getResources().getString(R.string.networkerror));
                        getSimpleProductCard(pos + 1, productsAccounts,utilityFunction,productDBhelper, username);
                        return;
                    }
                    bmp1[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    card[0] = new SimpleProductCard (utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
                    productDBhelper.InsertProduct(card[0]);
                    getSimpleProductCard(pos + 1, productsAccounts,utilityFunction,productDBhelper, username);
                }
            });
        }
        else {
            bmp1[0] = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card[0] = new SimpleProductCard (utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
            productDBhelper.InsertProduct(card[0]);
            getSimpleProductCard(pos + 1, productsAccounts,utilityFunction,productDBhelper, username);
        }
    }
}
