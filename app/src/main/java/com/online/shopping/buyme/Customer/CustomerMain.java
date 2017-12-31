package com.online.shopping.buyme.Customer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.CustomerProductInfo;
import com.online.shopping.buyme.CustomerExtras.HistoryDbhelper;
import com.online.shopping.buyme.CustomerExtras.SimpleCardInterFace;
import com.online.shopping.buyme.Extras.HorizontalAdapter;
import com.online.shopping.buyme.Extras.SimpleProductCard;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;

public class CustomerMain extends CustomerBaseAcctivity implements SimpleCardInterFace{
    private HashMap<String,HListView> map;
    HListView popularlistview;
    HistoryDbhelper historyDbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        Check_If_ProductShared_Notification();
        setupDbhelper();
        setListeners();
        setupDrawer();
        syncPopularData();
        setHorizontalListView();
        LoadAllData("");
        setUpListeners();
        setupDefaultFilter();
        ClearCache();
    }

    private void ClearCache() {
        if(productDBhelper.GetCount()>500)
        {
            HashMap<String,Boolean> Products_Needed = new HashMap<>();
            List<String> BasketProducts =basketDBhelper.getAllProducts();
            List<String> WishList=wishedDBhelper.getAllProducts();
            List<String> History=historyDbhelper.getAllProducts();
            for(String i:BasketProducts)
                Products_Needed.put(i,true);
            for(String i:WishList)
                Products_Needed.put(i,true);
            for(String i:History)
                Products_Needed.put(i,true);
            productDBhelper.clearAllOtherProducts(Products_Needed);
        }
    }

    private void setupDefaultFilter() {
        ConfigData.filter_dist = -1;
        ConfigData.filter_price_low = -1;
        ConfigData.filter_price_high = 50000;
        ConfigData.filter_cod = false;
    }

    private void setUpListeners() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigData.search_query = ((EditText) findViewById(R.id.search_query)).getText().toString();
                if(ConfigData.search_query.length()==0)
                    return;
                go_to_NextFragment(true);
            }
        });

        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int group, int child, long id) {
                String selectedproduct = ConfigData.DrawerChild[group][child];
                SelectedProducts = selectedproduct;
                if(selectedproduct.equals("Mens")||selectedproduct.equals("Womens")||selectedproduct.equals("Kids"))
                    return true;
                if (mDrawerLayout.isDrawerOpen(mDrawerList))
                    mDrawerLayout.closeDrawer(mDrawerList);
                go_to_NextFragment(false);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopprogress();
    }

    private void go_to_NextFragment(boolean is_search) {
        gpsTracker = new GPSTracker(getApplicationContext());
        ConfigData.is_Search = is_search;
        if (!gpsTracker.canGetLocation()) {
            showGpsAlert();
            return;
        }

        if(!is_search)
            ((EditText)findViewById(R.id.search_query)).setText("");
        utilityFunction.goActivity(CustomerCategoryActivity.class);
    }



    @Override
    public void cardClicked(SimpleProductCard card) {
        Intent i = new Intent(this, CustomerProductInfo.class);
        i.putExtra("objectId",card.productsAccount.getObjectId());
        startActivity(i);
    }


    private void LoadAllData(String tablename) {
        if(tablename.equals("")||tablename.equals("popular")) {
            List<SimpleProductCard> simpleProductCards = historyDbhelper.getAllCustomerProduct("popular", productDBhelper);
            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(CustomerMain.utilityFunction.activity, simpleProductCards);
            if (popularlistview != null) {
                popularlistview.setAdapter(horizontalAdapter);
            }
        }

        for(int i=0;i<=2;i++)
        {
            if(tablename.equals("")||tablename.equals(ConfigData.DrawerHeading[i]))
            {
                List<SimpleProductCard> simpleProductCard = historyDbhelper.getAllCustomerProduct(ConfigData.DrawerHeading[i],productDBhelper);
                HorizontalAdapter horizontalAdapter = new HorizontalAdapter(CustomerMain.utilityFunction.activity,simpleProductCard);
                HListView listView = map.get(ConfigData.DrawerHeading[i]);
                if (listView != null) {
                    listView.setAdapter(horizontalAdapter);
                }
            }
        }
    }

    private void syncPopularData() {
        if(ConfigData.HomeChecked)
            return;
        ConfigData.HomeChecked = true;
        ParseQuery<ProductsAccount> query2 = ParseQuery.getQuery("Products");
        query2.setLimit(10);
        query2.orderByDescending("Like");
        query2.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                if (e != null) {
                    CustomerMain.utilityFunction.ToastMessage("Error in Syncing Data");
                    return;
                }
                getSimpleProductCard(0, productsAccounts, "popular",new ArrayList<String>());
                syncData(0);
            }
        });
    }

    private void getSimpleProductCard(final int pos, final List<ProductsAccount> productsAccounts, final String tablename, final List<String> objectsid) {
        if(pos > productsAccounts.size()-1)
        {
            historyDbhelper.InsertProducts(tablename,objectsid);
            LoadAllData(tablename);
            return;
        }
        final ProductsAccount Currentproduct = productsAccounts.get(pos);
        String imagescount = Currentproduct.getimages();
        final Bitmap[] bmp1 = new Bitmap[1];
        final Bitmap bmp2 = null,bmp3 = null;
        final SimpleProductCard[] card = {productDBhelper.CheckAndReturnSimpleProductCard(Currentproduct)};
        if(card[0] !=null)
        {
            objectsid.add(card[0].productsAccount.getObjectId());
            getSimpleProductCard(pos + 1, productsAccounts,tablename,objectsid);
            return;
        }
        if (imagescount.contains("1"))
        {
            Currentproduct.getParse1().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(e!=null)
                    {
                        CustomerMain.utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        getSimpleProductCard(pos + 1, productsAccounts,tablename,objectsid);
                        return;
                    }
                    bmp1[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    card[0] = new SimpleProductCard (CustomerMain.utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
                    objectsid.add(card[0].productsAccount.getObjectId());
                    productDBhelper.InsertProduct(card[0]);
                    getSimpleProductCard(pos + 1, productsAccounts,tablename,objectsid);
                }
            });
        }
        else {
            bmp1[0] = BitmapFactory.decodeResource(CustomerMain.utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card[0] = new SimpleProductCard (CustomerMain.utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
            productDBhelper.InsertProduct(card[0]);
            objectsid.add(card[0].productsAccount.getObjectId());
            getSimpleProductCard(pos + 1, productsAccounts,tablename,objectsid);
        }
    }

    private void syncData(final int i) {
        if(i>2)
            return;
        ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
        query.whereContainedIn("Type", new HashSet<>(Arrays.asList(ConfigData.DrawerChild[i])));
        query.setLimit(10);
        query.orderByDescending("Like");
        query.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                if (e != null) {
                    CustomerMain.utilityFunction.ToastMessage("Error in Syncing Data");
                    return;
                }
                getSimpleProductCard(0,productsAccounts,ConfigData.DrawerHeading[i],new ArrayList<String>());
                syncData(i+1);
            }
        });
    }


    private void setHorizontalListView() {
        productDBhelper = new ProductDBhelper(CustomerMain.utilityFunction.activity);
        historyDbhelper = new HistoryDbhelper(CustomerMain.utilityFunction.activity);
        popularlistview = (HListView) findViewById(R.id.PopularListView);
        map = new HashMap<>();
        map.put(ConfigData.DrawerHeading[0],(HListView) findViewById(R.id.ElectronicListView));
        map.put(ConfigData.DrawerHeading[1], (HListView) findViewById(R.id.FashionAndStyleListView));
        map.put(ConfigData.DrawerHeading[2],(HListView) findViewById(R.id.HomeApplicanesListView));
    }

}
