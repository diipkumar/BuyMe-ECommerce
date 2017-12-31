package com.online.shopping.buyme.Retailer;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.camera.CropImageIntentBuilder;
import com.github.clans.fab.FloatingActionButton;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.online.shopping.buyme.ParseAccount.FeedBack;
import com.online.shopping.buyme.ParseAccount.RetailerAccount;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Extras.AnimatedExpandableListView;
import com.online.shopping.buyme.MainActivity;
import com.online.shopping.buyme.ParseAccount.CustomerAccount;
import com.online.shopping.buyme.ParseAccount.FavouritesAccount;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.RetailerProfileAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.RetailerExtras.RetailerAdapterInterFace;
import com.online.shopping.buyme.RetailerExtras.RetailerOrderStatus;
import com.online.shopping.buyme.RetailerExtras.RetailerProductCard;
import com.online.shopping.buyme.RetailerExtras.RetailerProductInfo;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;

public class RetailerMain extends CustomeActivity implements RetailerAdapterInterFace{

    private static final int STATIC_INTEGER_VALUE = 1251;
    private static int REQUEST_PICTURE = 4;
    int setCamera;
    String imgDecodableString1, imgDecodableString2, imgDecodableString3;
    private DrawerLayout mDrawerLayout;
    private AnimatedExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private FloatingActionButton fab;
    private Dialog addproduct_dialog;
    private UtilityFunction utilityFunction;
    private ImageView img1, img2, img3;
    CardGridView gridView;
    private String imgDecodableString, SelectedProducts;
    HashMap<String, CardGridArrayAdapter> map;
    public static int DefaultImage = R.drawable.notavailable;
    ProductDBhelper productDBhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_main);
        setListeners();
        syncData();
        setupDrawer();
        initCards();
        invalidateOptionsMenu();
        setAnimation();
    }

    private void setAnimation() {
        Animation enter = AnimationUtils.loadAnimation(this, R.anim.layoutanim);
        it.gmariotti.cardslib.library.view.CardGridView layout1 = (it.gmariotti.cardslib.library.view.CardGridView)findViewById(R.id.carddemo_grid_base1);
        layout1.startAnimation(enter);
    }

    private void syncData() {
        showprogess();
        map = new HashMap<>();
        for (int i = 0; i < ConfigData.DrawerChild.length; i++)
            for (int j = 0; j < ConfigData.DrawerChild[i].length; j++)
                map.put(ConfigData.DrawerChild[i][j], new CardGridArrayAdapter(this, new ArrayList<Card>()));
        ArrayList<RetailerProductCard> arrayList = productDBhelper.RetrieveRetailerProducts(ConfigData.currentuser);
        List<String> objectid = new ArrayList<>();
        for(RetailerProductCard retailerProductCard:arrayList)
        {
            map.get(retailerProductCard.productsAccount.getType()).add(retailerProductCard);
            objectid.add(retailerProductCard.productsAccount.getObjectId());
        }
        syncLikes(objectid);
        stopprogress();
    }

    private void syncLikes(List<String> objectid) {
        ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
        query.whereContainedIn("objectId", objectid);
        query.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(List<ProductsAccount> list, ParseException e) {
                if (e == null) {
                    for (ProductsAccount i : list)
                        productDBhelper.UpdateLikesInformation(i);
                }
            }
        });
    }

    private void setListeners() {
        productDBhelper = new ProductDBhelper(this);
        SelectedProducts = ConfigData.DrawerChild[0][0];
        utilityFunction = new UtilityFunction(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addproductdialog();
            }
        });
    }

    private void addproductdialog() {
        imgDecodableString1 = imgDecodableString2 = imgDecodableString3 = null;
        addproduct_dialog = new Dialog(this);
        addproduct_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addproduct_dialog.setContentView(R.layout.dialog_addproduct);
        ((TextView) addproduct_dialog.findViewById(R.id.add_product_heading)).setText(SelectedProducts);
        final EditText name = (EditText) addproduct_dialog.findViewById(R.id.name);
        final EditText price = (EditText) addproduct_dialog.findViewById(R.id.price);
        final EditText description = (EditText) addproduct_dialog.findViewById(R.id.description);
        final CheckBox notify = (CheckBox)addproduct_dialog.findViewById(R.id.add_product_Notify_Users);
        img1 = (ImageView) addproduct_dialog.findViewById(R.id.img1);
        img2 = (ImageView) addproduct_dialog.findViewById(R.id.img2);
        img3 = (ImageView) addproduct_dialog.findViewById(R.id.img3);
        TextView ok = (TextView) addproduct_dialog.findViewById(R.id.ok);
        final TextView cancel = (TextView) addproduct_dialog.findViewById(R.id.cancel);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCamera = 1;
                startGallery();
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCamera = 2;
                startGallery();
            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCamera = 3;
                startGallery();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productname = name.getText().toString();
                String productprice = price.getText().toString();
                String productdescription = description.getText().toString();

                if (productname.length() == 0) {
                    utilityFunction.SanckMessage(getResources().getString(R.string.product_add_name_error));
                    return;
                }

                if (productprice.length() == 0) {
                    utilityFunction.SanckMessage(getResources().getString(R.string.product_add_price_error));
                    return;
                }

                if (productprice.length() > 6) {
                    utilityFunction.SanckMessage(getResources().getString(R.string.product_add_price_limit_error));
                    return;
                }

                addproduct_dialog.dismiss();
                CheckProfileFirst(productname, productprice, productdescription,notify.isChecked());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addproduct_dialog.cancel();
            }
        });
        addproduct_dialog.show();
    }

    private void CheckProfileFirst(final String productname, final String productprice, final String productdescription,final boolean notify_other_users) {
        showprogess();
        if (ConfigData.commonFunction.isProfileSaved()) {
            SaveProductInserver(productname, productprice, productdescription,notify_other_users);
            return;
        }
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
                if (retailerProfileAccounts.size() == 0) {
                    stopprogress();
                    utilityFunction.SanckMessage("First Set Profile");
                    utilityFunction.goActivity(RetailerProfile.class);
                    return;
                }
                ConfigData.commonFunction.ProfileSaved(retailerProfileAccounts.get(0).getlocation());
                SaveProductInserver(productname, productprice, productdescription,notify_other_users);
            }
        });
    }

    private void SaveProductInserver(final String productname, final String productprice, final String productdescription,final boolean notify_other_users) {
        ParseFile image1 = getImageParseFile(productname + "1", imgDecodableString1);
        ParseFile image2 = getImageParseFile(productname + "2", imgDecodableString2);
        ParseFile image3 = getImageParseFile(productname + "3", imgDecodableString3);
        String imagescount = "";
        if (imgDecodableString1 != null)
            imagescount += "1";
        if (imgDecodableString2 != null)
            imagescount += "2";
        if (imgDecodableString3 != null)
            imagescount += "3";
        final ProductsAccount productsAccount = new ProductsAccount(productname, productprice, productdescription, image1, image2, image3, imagescount, ConfigData.currentuser, SelectedProducts, ConfigData.location);
        productsAccount.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                stopprogress();
                if (e == null) {
                    RetailerProductCard card = new RetailerProductCard(RetailerMain.this,BitmapFactory.decodeFile(imgDecodableString1),BitmapFactory.decodeFile(imgDecodableString2),BitmapFactory.decodeFile(imgDecodableString3),productsAccount);
                    map.get(SelectedProducts).add(card);
                    map.get(SelectedProducts).notifyDataSetChanged();
                    productDBhelper.InsertProduct(card);
                    utilityFunction.SanckMessage(getResources().getString(R.string.saved));
                    if(notify_other_users)
                        SyncData();
                } else {
                    utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                }
            }


            private void SyncData() {
                showprogess();
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
                            NotifyOtherUser(retailerProfileAccounts.get(0).get_shop_name());
                        }
                    }
                });
            }
            private void NotifyOtherUser(final String shop_name) {
                        showprogess();
                        ParseQuery<FavouritesAccount> query = ParseQuery.getQuery("Favourites");
                        query.whereEqualTo("Retailer", ConfigData.currentuser);
                        query.findInBackground(new FindCallback<FavouritesAccount>() {
                            @Override
                            public void done(List<FavouritesAccount> retailerAccounts, ParseException e) {
                                if (e == null) {
                                    for(FavouritesAccount i:retailerAccounts) {
                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user", i.getCustomer());
                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
                                        push.setMessage(shop_name+" added new product.");
                                        push.sendInBackground();
                                    }
                                    stopprogress();
                                } else {
                                    stopprogress();
                                    utilityFunction.SanckMessage("NetworkError");
                                }
                            }
                        });
            }
        });
    }

    private ParseFile getImageParseFile(String productname, String imgDecodableString1) {
        byte[] data;
        imgDecodableString = imgDecodableString1;
        if (imgDecodableString1 == null)
            return null;
        else
            data = getCompressed();
        return new ParseFile(productname+ ".webp", data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CROP_PICTURE = 5;
        try {
            if ((requestCode == REQUEST_PICTURE) && (resultCode == RESULT_OK)) {
                File croppedImageFile ;

                if(setCamera==3) {
                    croppedImageFile = new File(getFilesDir(), "test1.jpg");
                }
                else if(setCamera==2) {
                    croppedImageFile = new File(getFilesDir(), "test2.jpg");
                }
                else {
                    croppedImageFile = new File(getFilesDir(), "test3.jpg");
                }
                Uri croppedImage = Uri.fromFile(croppedImageFile);
                CropImageIntentBuilder cropImage = new CropImageIntentBuilder(500, 500, croppedImage);
                cropImage.setOutlineColor(0xFF03A9F4);
                cropImage.setSourceImage(data.getData());
                startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
            } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == RESULT_OK)) {
                File croppedImageFile ;
                if(setCamera==3) {
                    croppedImageFile = new File(getFilesDir(), "test1.jpg");
                    imgDecodableString3 = croppedImageFile.getAbsolutePath();
                    img3.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString3));
                }
                else if(setCamera==2) {
                    croppedImageFile = new File(getFilesDir(), "test2.jpg");
                    imgDecodableString2 = croppedImageFile.getAbsolutePath();
                    img2.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString2));
                }
                else {
                    croppedImageFile = new File(getFilesDir(), "test3.jpg");
                    imgDecodableString1 = croppedImageFile.getAbsolutePath();
                    img1.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString1));
                }
            }else if ((requestCode == STATIC_INTEGER_VALUE) && (resultCode == RESULT_OK)) {
                syncData();
                gridView.setAdapter(map.get(SelectedProducts));
            }
            } catch (Exception e) {
            utilityFunction.SanckMessage("Something went wrong");
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private void initCards() {
        gridView = (CardGridView) findViewById(R.id.carddemo_grid_base1);
        fab.show(true);
        if (gridView != null) {
            gridView.setAdapter(map.get(SelectedProducts));
        }
    }

    private void setupDrawer() {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerList = (AnimatedExpandableListView) findViewById(R.id.navdrawer);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        List<DrawerListAdapter.GroupItem> items = new ArrayList<>();
        for(int i = 0; i < ConfigData.DrawerHeading.length; i++) {
            DrawerListAdapter.GroupItem item = new DrawerListAdapter.GroupItem();
            item.title = ConfigData.DrawerHeading[i];
            for(int j = 0; j < ConfigData.DrawerChild[i].length; j++) {
                DrawerListAdapter.ChildItem child = new DrawerListAdapter.ChildItem();
                child.title = ConfigData.DrawerChild[i][j];
                item.items.add(child);
            }
            items.add(item);
        }
        DrawerListAdapter adapter = new DrawerListAdapter(this);
        adapter.setData(items);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, final View view, final int group, int child, long id) {
                String selectedproduct = ConfigData.DrawerChild[group][child];
                if(selectedproduct.equals("Mens")||selectedproduct.equals("Womens")||selectedproduct.equals("Kids"))
                    return true;
                SelectedProducts = selectedproduct ;
                getActionBar().setTitle(selectedproduct);
                gridView.setAdapter(map.get(SelectedProducts));
                if (mDrawerLayout.isDrawerOpen(mDrawerList))
                        mDrawerLayout.closeDrawer(mDrawerList);
                    return true;
            }
        });
        mDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, final View v, final int groupPosition, long id) {
                final boolean isExpanded = mDrawerList.isGroupExpanded(groupPosition);
                final ImageView arrow = (ImageView) v.findViewById(R.id.expand_arrow);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) arrow.getLayoutParams();
                RotateAnimation rotate ;
                if(isExpanded)
                    rotate = new RotateAnimation(180,360,layoutParams.leftMargin+(arrow.getWidth()/2),layoutParams.topMargin+(arrow.getHeight()/2));
                else
                    rotate = new RotateAnimation(0,180,layoutParams.leftMargin+(arrow.getWidth()/2),layoutParams.topMargin+(arrow.getHeight()/2));
                rotate.setDuration(200);
                rotate.setFillEnabled(true);
                rotate.setFillAfter(true);
                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if(isExpanded)
                            mDrawerList.collapseGroupWithAnimation(groupPosition);
                        else
                            mDrawerList.expandGroupWithAnimation(groupPosition);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                arrow.startAnimation(rotate);
                return true;
            }
        });
    }

    private void Share_App() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = getResources().getString(R.string.ShareAppBody);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void Do_Change_Password() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_changepassword);
        final EditText old,newpass,confirmnew;
        old = (EditText)dialog.findViewById(R.id.oldpassword);
        newpass = (EditText)dialog.findViewById(R.id.newpassword);
        confirmnew = (EditText)dialog.findViewById(R.id.verifypassword);
        TextView ok,cancel;
        ok = (TextView)dialog.findViewById(R.id.Change_password_ok);
        cancel = (TextView)dialog.findViewById(R.id.Change_password_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newpass.getText().toString().length()<6)
                {
                    utilityFunction.SanckMessage("Enter New Password More Than 6 characters");
                    return;
                }
                if(!newpass.getText().toString().equals(confirmnew.getText().toString()))
                {
                    utilityFunction.SanckMessage("Password Doesnt Match");
                    return;
                }
                if(old.getText().toString().length()==0)
                {
                    utilityFunction.SanckMessage("Enter Old Password");
                    return;
                }
                showprogess();
                ParseQuery<RetailerAccount> query = ParseQuery.getQuery("RetailerAccount");
                query.whereEqualTo("Username",ConfigData.currentuser);
                query.getFirstInBackground(new GetCallback<RetailerAccount>() {
                    @Override
                    public void done(RetailerAccount retailerAccount, ParseException e) {
                        if(e!=null)
                        {
                            stopprogress();
                            utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                            return;
                        }
                        if(!old.getText().toString().equals(retailerAccount.getpassword()))
                        {
                            stopprogress();
                            utilityFunction.SanckMessage(getResources().getString(R.string.passworderror));
                            return;
                        }
                        retailerAccount.setpassword(newpass.getText().toString());
                        retailerAccount.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                stopprogress();
                                if(e!=null)
                                {
                                    utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                                    return;
                                }
                                if(dialog!=null&&dialog.isShowing())
                                dialog.dismiss();
                                utilityFunction.SanckMessage(getResources().getString(R.string.passwordsuccess));
                            }
                        });
                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
                            utilityFunction.SanckMessage("NetworkError");
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

    @Override
    public void cardClicked(RetailerProductCard card) {
        Intent i = new Intent(this, RetailerProductInfo.class);
        i.putExtra("objectId",card.productsAccount.getObjectId());
        startActivityForResult(i, STATIC_INTEGER_VALUE);
    }

    @Override
    public void EditOptionClicked(final RetailerProductCard cardPointer) {
        imgDecodableString1 = imgDecodableString2 = imgDecodableString3 = null;
        addproduct_dialog = new Dialog(this);
        addproduct_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addproduct_dialog.setContentView(R.layout.dialog_addproduct);

        final EditText name = (EditText)addproduct_dialog.findViewById(R.id.name);
        final EditText price = (EditText)addproduct_dialog.findViewById(R.id.price);
        final EditText description = (EditText)addproduct_dialog.findViewById(R.id.description);
        img1 = (ImageView)addproduct_dialog.findViewById(R.id.img1);
        img2 = (ImageView)addproduct_dialog.findViewById(R.id.img2);
        img3 = (ImageView)addproduct_dialog.findViewById(R.id.img3);
        name.setText(cardPointer.productsAccount.getname());
        price.setText(cardPointer.productsAccount.getPrice());
        description.setText(cardPointer.productsAccount.getDescription());
        img1.setImageBitmap(cardPointer.resource1);
        img2.setImageBitmap(cardPointer.resource2);
        img3.setImageBitmap(cardPointer.resource3);
        TextView ok = (TextView)addproduct_dialog.findViewById(R.id.ok);
        final TextView cancel = (TextView)addproduct_dialog.findViewById(R.id.cancel);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCamera=1;
                startGallery();
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCamera=2;
                startGallery();
            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCamera=3;
                startGallery();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productname = name.getText().toString();
                String productprice = price.getText().toString();
                String productdescription = description.getText().toString();

                if(productname.length() == 0){
                    utilityFunction.SanckMessage("Please enter Name");
                    return;
                }

                if(productprice.length() == 0){
                    utilityFunction.SanckMessage("Please enter Price");
                    return;
                }
                addproduct_dialog.dismiss();
                EditProductInServer(cardPointer,productname, productprice, productdescription);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addproduct_dialog.cancel();
            }
        });
        addproduct_dialog.show();
    }

    private void EditProductInServer(final RetailerProductCard cardPointer, final String productname, final String productprice, final String productdescription) {
        showprogess();
        if(!productname.equals(cardPointer.productsAccount.getname())&&productDBhelper.CheckIfProductExist(ConfigData.currentuser,productname,cardPointer.productsAccount.getType()))
        {
            stopprogress();
            utilityFunction.SanckMessage("Product Name Already Exist");
            return;
        }
                    ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
                    query.whereEqualTo("objectId", cardPointer.productsAccount.getObjectId());
                    query.getFirstInBackground(new GetCallback<ProductsAccount>() {
                        @Override
                        public void done(final ProductsAccount productsAccount, ParseException e) {
                            if(e!=null)
                            {
                                stopprogress();
                                utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                                return;
                            }
                            if(productsAccount==null)
                            {
                                stopprogress();
                                utilityFunction.SanckMessage(getResources().getString(R.string.something_went_wrong)+"Please Delete The Product and Add again");
                                return;
                            }
                            productsAccount.setPrice(productprice);
                            productsAccount.setname(productname);
                            productsAccount.setDescription(productdescription);
                            String imagescount = productsAccount.getimages();
                            if (imgDecodableString1 != null) {
                                cardPointer.resource1 = BitmapFactory.decodeFile(imgDecodableString1);
                                ParseFile image1 = getImageParseFile(productname + "1", imgDecodableString1);
                                productsAccount.setParse1(image1);
                                if (!imagescount.contains("1"))
                                    imagescount += "1";
                            }
                            if (imgDecodableString2 != null) {
                                cardPointer.resource2 = BitmapFactory.decodeFile(imgDecodableString2);
                                ParseFile image1 = getImageParseFile(productname + "2", imgDecodableString2);
                                productsAccount.setParse2(image1);
                                if (!imagescount.contains("2"))
                                    imagescount += "2";
                            }
                            if (imgDecodableString3 != null) {
                                cardPointer.resource3 = BitmapFactory.decodeFile(imgDecodableString3);
                                ParseFile image1 = getImageParseFile(productname + "3", imgDecodableString3);
                                productsAccount.setParse3(image1);
                                if (!imagescount.contains("3"))
                                    imagescount += "3";
                            }
                            productsAccount.setimages(imagescount);
                            productsAccount.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    stopprogress();
                                    if (e == null) {
                                        productDBhelper.deleteProduct(cardPointer.productsAccount.getObjectId());
                                        cardPointer.productsAccount = productsAccount;
                                        cardPointer.init();
                                        productDBhelper.InsertProduct(cardPointer);
                                        utilityFunction.SanckMessage(getResources().getString(R.string.saved));
                                        map.get(cardPointer.productsAccount.getType()).notifyDataSetChanged();
                                    } else {
                                        utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                                    }
                                }
                            });
                        }
                    });
    }

    private void startGallery() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.createChooser(intent, "Select picture");
        startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_PICTURE);
    }

    private byte[] getCompressed() {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imgDecodableString, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 616.0f;
        float maxWidth = 412.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            bmp = BitmapFactory.decodeFile(imgDecodableString, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        ExifInterface exif;
        try {
            exif = new ExifInterface(imgDecodableString);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytedata(scaledBitmap);
    }

    private byte[] bytedata(Bitmap scaledBitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.WEBP, 100, bos);
        return bos.toByteArray();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        switch (item.getItemId())
        {
            case R.id.mennu_profile_item:
                utilityFunction.goActivity(RetailerProfile.class);
                break;
            case R.id.menu_logout:
                Do_Logout();
                break;
            case R.id.menu_change_password:
                Do_Change_Password();
                break;
            case R.id.menu_order_status:
                utilityFunction.goActivity(RetailerOrderStatus.class);
                break;
            case R.id.menu_Share_App:
                Share_App();
                break;
            case R.id.menu_order_feedback:
                Show_Feedback();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.retailer_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productDBhelper.close();
    }

    @Override
    protected void onPause() {
        stopprogress();
        super.onPause();
    }

}
