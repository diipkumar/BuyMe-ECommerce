package com.online.shopping.buyme.Customer;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.online.shopping.buyme.ParseAccount.CustomerAccount;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.BasketDBhelper;
import com.online.shopping.buyme.CustomerExtras.CustomerProductInfo;
import com.online.shopping.buyme.CustomerExtras.LikeDBhelper;
import com.online.shopping.buyme.CustomerExtras.WishedDBhelper;
import com.online.shopping.buyme.Extras.AnimatedExpandableListView;
import com.online.shopping.buyme.ParseAccount.RetailerAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.DrawerListAdapter;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.List;

public class CustomerBaseAcctivity extends CustomeActivity {

    protected static final int FILTER_CODE = 4324;
    public static int MAP_CODE=2342;
    public BasketDBhelper basketDBhelper;
    public WishedDBhelper wishedDBhelper;
    public LikeDBhelper likeDBhelper;
    boolean isCategoryFragment;
    protected DrawerLayout mDrawerLayout;
    public static String SelectedProducts;
    public static UtilityFunction utilityFunction;
    GPSTracker gpsTracker;
    ImageView search;
    protected AnimatedExpandableListView mDrawerList;
    public ProductDBhelper productDBhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void Check_If_ProductShared_Notification() {
        if(ConfigData.SharedProductObjectID==null||ConfigData.SharedProductObjectID.equals(""))
            return;
        Intent i = new Intent(this, CustomerProductInfo.class);
        i.putExtra("objectId",ConfigData.SharedProductObjectID);
        startActivity(i);
    }

    protected void setupDbhelper() {
        wishedDBhelper = new WishedDBhelper(this);
        productDBhelper = new ProductDBhelper(this);
        basketDBhelper = new BasketDBhelper(this);
        likeDBhelper = new LikeDBhelper(this);
    }

    protected void setListeners() {
        productDBhelper = new ProductDBhelper(this);
        isCategoryFragment = false;
        utilityFunction = new UtilityFunction(this);
        search = (ImageView)findViewById(R.id.search_button);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopprogress();
    }

    protected void setupDrawer() {
        ActionBar ab =getActionBar();
        if(ab!=null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
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
        for (int i = 0; i < ConfigData.DrawerHeading.length; i++) {
            DrawerListAdapter.GroupItem item = new DrawerListAdapter.GroupItem();
            item.title = ConfigData.DrawerHeading[i];
            for (int j = 0; j < ConfigData.DrawerChild[i].length; j++) {
                DrawerListAdapter.ChildItem child = new DrawerListAdapter.ChildItem();
                child.title = ConfigData.DrawerChild[i][j];
                item.items.add(child);
            }
            items.add(item);
        }
        DrawerListAdapter adapter = new DrawerListAdapter(this);
        adapter.setData(items);
        mDrawerList.setAdapter(adapter);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_more:
                utilityFunction.goActivity(com.online.shopping.buyme.Customer.Settings.class);
                break;
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
                break;
            case R.id.menu_basket_item:
                utilityFunction.goActivity(Basket.class);
                break;
            case R.id.menu_change_password:
                Do_Change_Password();
                break;
            case R.id.menu_order_status:
                utilityFunction.goActivity(CustomerOrderStatus.class);
                break;
            case R.id.filters:
                final Intent intent = new Intent(this,FilterProduct.class);
                startActivityForResult(intent, FILTER_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                if (newpass.getText().toString().length() < 6) {
                    utilityFunction.ToastMessage("Enter New Password More Than 6 characters");
                    return;
                }
                if (!newpass.getText().toString().equals(confirmnew.getText().toString())) {
                    utilityFunction.ToastMessage("Password Doesnt Match");
                    return;
                }
                if (old.getText().toString().length() == 0) {
                    utilityFunction.ToastMessage("Enter Old Password");
                    return;
                }
                showprogess();
                ParseQuery<CustomerAccount> query = ParseQuery.getQuery("CustomerAccount");
                query.whereEqualTo("Username", ConfigData.currentuser);
                query.getFirstInBackground(new GetCallback<CustomerAccount>() {
                    @Override
                    public void done(CustomerAccount customerAccount, ParseException e) {
                        if (e != null) {
                            stopprogress();
                            utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                            return;
                        }
                        if (!old.getText().toString().equals(customerAccount.getpassword())) {
                            stopprogress();
                            utilityFunction.ToastMessage(getResources().getString(R.string.passworderror));
                            return;
                        }
                        customerAccount.setpassword(newpass.getText().toString());
                        customerAccount.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                stopprogress();
                                if (e != null) {
                                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                                    return;
                                }
                                dialog.dismiss();
                                utilityFunction.ToastMessage(getResources().getString(R.string.passwordsuccess));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wishedDBhelper.close();
        productDBhelper.close();
        basketDBhelper.close();
        likeDBhelper.close();
    }

    @Override
    protected void onPause() {
        stopprogress();
        super.onPause();
    }
}