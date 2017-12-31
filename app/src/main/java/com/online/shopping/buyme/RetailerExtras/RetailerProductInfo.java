package com.online.shopping.buyme.RetailerExtras;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Extras.CustomViewPageAdaptor;
import com.online.shopping.buyme.Extras.CustomViewPager;
import com.online.shopping.buyme.Extras.ImageFragment;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.SellersInfo;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.List;

public class RetailerProductInfo extends CustomeActivity implements View.OnClickListener {

    CustomViewPager pager;
    CustomViewPageAdaptor pageAdapter;
    RetailerProductCard retailerProductCard;
    ImageView wishlist,basket,image1,image2,image3,product_share;
    TextView productname,price,description,sellersinfo;
    UtilityFunction utilityFunction;
    ProductDBhelper productDBhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_info);
        ConfigData.SharedProductObjectID = "";
        setListeners();
        retailerProductCard = productDBhelper.GetProductCard_From_Object_id(getIntent().getStringExtra("objectId"));
        setupData();
        setPager();
    }

    private void setupData() {
        productname.setText(retailerProductCard.productsAccount.getname());
        price.setText(" "+retailerProductCard.productsAccount.getPrice());
        description.setText(retailerProductCard.productsAccount.getDescription());
        image1.setImageBitmap(retailerProductCard.resource1);
        image2.setImageBitmap(retailerProductCard.resource2);
        image3.setImageBitmap(retailerProductCard.resource3);
    }

    private void setListeners() {
        getActionBar();
        //   getSupportActionBar().setBackgroundDrawable(new ColorDrawable(R.color.violet));
        productDBhelper = new ProductDBhelper(this);
        utilityFunction = new UtilityFunction(this);
        productname = (TextView) findViewById(R.id.productname);
        price = (TextView) findViewById(R.id.productprice);
        description = (TextView) findViewById(R.id.productdescription);
        sellersinfo = (TextView) findViewById(R.id.ProductSellersInfo);
        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);
        product_share = (ImageView)findViewById(R.id.productshare);
        wishlist = (ImageView) findViewById(R.id.wishlist);
        basket = (ImageView) findViewById(R.id.basket);
            wishlist.setVisibility(View.INVISIBLE);
            basket.setVisibility(View.INVISIBLE);
            sellersinfo.setVisibility(View.INVISIBLE);
            product_share.setVisibility(View.INVISIBLE);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id =view.getId();
        switch (id)
        {
            case R.id.ProductSellersInfo:
                utilityFunction.goActivity(SellersInfo.class);
                break;
            case R.id.image1:
                setalpha(1, .50f, .50f);
                pager.setCurrentItem(0,true);
                break;
            case R.id.image2:
                setalpha(.50f,1, .50f);
                pager.setCurrentItem(1,true);
                break;
            case R.id.image3:
                setalpha(.50f,.50f, 1);
                pager.setCurrentItem(2,true);
                break;
        }
    }

    private void setPager() {
        List<Fragment> fragments = getFragments();
        pageAdapter = new CustomViewPageAdaptor(getFragmentManager(), fragments);
        pager = (CustomViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setScrollDurationFactor(2);
        setalpha(1, .50f, .50f);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<>();
        ImageFragment a = new ImageFragment();
        ImageFragment c = new ImageFragment();
        ImageFragment b = new ImageFragment();
        a.setBitmap(retailerProductCard.resource1);
        b.setBitmap(retailerProductCard.resource2);
        c.setBitmap(retailerProductCard.resource3);

        fList.add(a);
        fList.add(b);
        fList.add(c);
        return fList;
    }

    private void setalpha(float i, float v, float v1) {
        image1.setAlpha(i);
        image2.setAlpha(v);
        image3.setAlpha(v1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.product_info_menu, menu);
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
                case R.id.product_delete:
                ShowDeleteDialog();
                break;
        }
        return true;
    }

    private void ShowDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showprogess();
                        ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
                        query.whereEqualTo("User", ConfigData.currentuser);
                        query.whereEqualTo("Name",retailerProductCard.productsAccount.getname());
                        query.whereEqualTo("Type",retailerProductCard.productsAccount.getType());
                        query.getFirstInBackground(new GetCallback<ProductsAccount>() {
                            @Override
                            public void done(final ProductsAccount productsAccount, ParseException e) {
                                if(e!=null)
                                {
                                    stopprogress();
                                    utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                                }
                                productsAccount.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e!=null)
                                        {
                                            stopprogress();
                                            utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                                        }
                                        else
                                        {
                                            productDBhelper.deleteProduct(retailerProductCard.productsAccount.getObjectId());
                                            stopprogress();
                                            utilityFunction.SanckMessage(getResources().getString(R.string.deleted));
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("objectId",retailerProductCard.productsAccount.getObjectId());
                                            setResult(Activity.RESULT_OK, resultIntent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

