package com.online.shopping.buyme.RetailerExtras;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.aphidmobile.flip.FlipViewController;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.CustomerProductCard;
import com.online.shopping.buyme.ParseAccount.OrderStatusAccount;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.common.ProductDBhelper;

import java.util.ArrayList;
import java.util.List;

public class RetailerOrderStatus extends CustomeActivity {
    RetailerOrderStatusAdapter adapter ;
    private FlipViewController flipView;
    UtilityFunction utilityFunction;
    private ProductDBhelper productDBhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_status);
        flipView = new FlipViewController(this,FlipViewController.HORIZONTAL);
        flipView.setAnimationBitmapFormat(Bitmap.Config.ARGB_8888);
        setContentView(flipView);
        setListeners();
        syncData();
    }

    private void setListeners() {
        productDBhelper = new ProductDBhelper(this);
        utilityFunction = new UtilityFunction(this);
    }

    private void syncData() {
        showprogess();
        final ParseQuery<OrderStatusAccount> query= ParseQuery.getQuery("OrderStatus");
        query.whereEqualTo("retaileruser",ConfigData.currentuser);
        query.findInBackground(new FindCallback<OrderStatusAccount>() {
            @Override
            public void done(final List<OrderStatusAccount> orderStatusAccounts, ParseException e) {
                if(e!= null)
                {
                    stopprogress();
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    finish();
                    return;
                }
                adapter = new RetailerOrderStatusAdapter(RetailerOrderStatus.this, orderStatusAccounts);
                List<String> object = new ArrayList<>();
                for(OrderStatusAccount i:orderStatusAccounts)
                {
                    List<String> temp=i.getProducts();
                    for(String j:temp)
                    {
                        String product_object_id = j.substring(0,j.indexOf(",;,"));
                        object.add(product_object_id);
                    }
                }
                ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
                query.whereContainedIn("objectId", object);
                query.findInBackground(new FindCallback<ProductsAccount>() {
                    @Override
                    public void done(final List<ProductsAccount> productsAccounts, ParseException e) {
                        if(e != null)
                        {
                            stopprogress();
                            utilityFunction.ToastMessage("Error in Syncing Data");
                            finish();
                            return;
                        }
                        getSimpleProductCard(0,productsAccounts);
                    }
                });
            }
        });
    }


    private void getSimpleProductCard(final int pos, final List<ProductsAccount> productsAccounts) {
        if(pos > productsAccounts.size()-1)
        {
            stopprogress();
            flipView.setAdapter(adapter);
            return;
        }
        final ProductsAccount Currentproduct = productsAccounts.get(pos);
        String imagescount = Currentproduct.getimages();
        final Bitmap[] bmp1 = new Bitmap[1];
        final Bitmap bmp2 = null,bmp3 = null;
        final CustomerProductCard[] card = {productDBhelper.CheckAndReturnCustomerProduct(Currentproduct)};
        if(card[0] !=null)
        {
            getSimpleProductCard(pos + 1, productsAccounts);
            return;
        }
        if (imagescount.contains("1"))
        {
            Currentproduct.getParse1().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(e!=null)
                    {
                        stopprogress();
                        utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        finish();
                        return;
                    }
                    bmp1[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    card[0] = new CustomerProductCard(RetailerOrderStatus.this, bmp1[0], bmp2, bmp3,Currentproduct);
                    productDBhelper.InsertProduct(card[0]);
                    getSimpleProductCard(pos + 1, productsAccounts);
                }
            });
        }
        else {
            bmp1[0] = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card[0] = new CustomerProductCard(RetailerOrderStatus.this, bmp1[0], bmp2, bmp3,Currentproduct);
            productDBhelper.InsertProduct(card[0]);
            getSimpleProductCard(pos + 1, productsAccounts);
        }
    }


    @Override
    protected void onPause() {
        stopprogress();
        super.onPause();
    }
}
