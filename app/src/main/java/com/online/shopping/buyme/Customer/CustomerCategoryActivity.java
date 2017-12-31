package com.online.shopping.buyme.Customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.CustomerProductCard;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.RetailerProfileAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;

public class CustomerCategoryActivity extends CustomerBaseAcctivity {
    public static CardGridArrayAdapter cardGridArrayAdapter;
    private CardGridView gridView;
    int skip=0;
    int load_at_time=15;
    int totalloaded=load_at_time;
    boolean firstloaded =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_category);
        Check_If_ProductShared_Notification();
        setupDbhelper();
        setListeners();
        setupDrawer();
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
        SetListener();
        Start_Work();
    }

    private void Start_Work() {
        skip=0;
        totalloaded=5;
        firstloaded =true;

        if(!ConfigData.is_Search)
            syncData();
        else
            Do_Search_query();
    }

    private void SetListener() {
        cardGridArrayAdapter = new CardGridArrayAdapter(CustomerMain.utilityFunction.activity, new ArrayList<Card>());
        gridView = (CardGridView) findViewById(R.id.carddemo_grid_base1);
    }

    private void Do_Search_query() {
        getActionBar().setTitle(getResources().getString(R.string.app_name));
        getLocation();
        if(!isFinishing())
            showprogess();
        cardGridArrayAdapter = new CardGridArrayAdapter(CustomerMain.utilityFunction.activity,new ArrayList<Card>());
        final List<String> search_query = new ArrayList<>();
        final String []temp = ConfigData.search_query.split(" ");
        String regex="";
        for(String i:temp) {
            String regexString="";
            i = i.toLowerCase();
            for(int j=0;j<i.length();j++)
            {
                regexString+="["+i.charAt(j)+(i.charAt(j)+32)+"]";
            }
            regex+= "^.*"+regexString+".*$|";
            search_query.add(i);
        }
        ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
        query.whereMatches("Name",regex.substring(0,regex.length()-2));
        if(ConfigData.signuplatlng.latitude!=0) {
            final ParseGeoPoint templocation = new ParseGeoPoint(ConfigData.signuplatlng.latitude, ConfigData.signuplatlng.longitude);
            if (ConfigData.filter_dist == -1)
                query.whereNear("Location", templocation);
            else {
                //query.addDescendingOrder("Like");
                query.whereWithinKilometers("Location", templocation, ConfigData.filter_dist);
            }
        }
        if(ConfigData.filter_price_low!=-1) {
            query.whereGreaterThanOrEqualTo("Price", ConfigData.filter_price_low);
            query.whereLessThanOrEqualTo("Price", ConfigData.filter_price_high);
        }
        query.setLimit(100);
        query.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                stopprogress();
                if (e != null) {
                    CustomerMain.utilityFunction.ToastMessage("Error in Syncing Data");
                    return;
                }
                productsAccounts = ArrangeAccordingtoSearch(productsAccounts,search_query);
                if (gridView != null) {
                    gridView.setAdapter(cardGridArrayAdapter);
                }
                if(productsAccounts.size()==0)
                {
                    ShowAlert_NoProductsAvailable();
                }
                if(ConfigData.filter_cod)
                    getRetailerProducts(productsAccounts,cardGridArrayAdapter);
                else
                    getSimpleProductCard(0,productsAccounts,cardGridArrayAdapter, null);
                cardGridArrayAdapter.notifyDataSetChanged();    }
        });
    }

    private void ShowAlert_NoProductsAvailable() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("No Products Available");
        alertDialog.setMessage("No products available in this category, please change category or filter options");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        if(!this.isFinishing())
            alertDialog.show();
    }

    private List<ProductsAccount> ArrangeAccordingtoSearch(List<ProductsAccount> productsAccounts, List<String> search_query) {
        HashMap<ProductsAccount,Integer> map = new HashMap<>();
        for(ProductsAccount i:productsAccounts)
        {
            for(String j:search_query)
                if(i.getname().contains(j))
                    if(map.containsKey(i))
                        map.put(i,map.get(i)+1);
                    else
                        map.put(i,1);
        }
        HashMap<Integer,List<ProductsAccount> > matching= new HashMap<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int value = (int) pair.getValue();
            ProductsAccount tempproduct = (ProductsAccount) pair.getKey();
            if (!matching.containsKey(value))
                matching.put(value, new ArrayList<ProductsAccount>());
            matching.get(value).add(tempproduct);
        }
        int total_string =search_query.size();
        List<ProductsAccount> answer = new ArrayList<>();
        for(int i=total_string;i>0;i--)
        {
            if(!matching.containsKey(i))
                continue;
            List<ProductsAccount> productsAccountList = matching.get(i);
            for(ProductsAccount temp:productsAccountList)
                answer.add(temp);
        }
        return answer;
    }

    private void getSimpleProductCard(final int pos, final List<ProductsAccount> productsAccounts, final CardGridArrayAdapter cardGridArrayAdapter, final HashMap<String, Boolean> map) {
        if(pos > productsAccounts.size()-1)
            return;
        final ProductsAccount Currentproduct = productsAccounts.get(pos);
        if(ConfigData.filter_cod&&map!=null&&!map.containsKey(Currentproduct.getUser()))
        {
            getSimpleProductCard(pos + 1, productsAccounts,cardGridArrayAdapter, map);
            return;
        }
        String imagescount = Currentproduct.getimages();
        final Bitmap[] bmp1 = new Bitmap[1];
        final Bitmap bmp2 = null,bmp3 = null;
        final CustomerProductCard[] card = {productDBhelper.CheckAndReturnCustomerProduct(Currentproduct)};
        if(card[0] !=null)
        {
            cardGridArrayAdapter.add(card[0]);
            cardGridArrayAdapter.notifyDataSetChanged();
            getSimpleProductCard(pos + 1, productsAccounts,cardGridArrayAdapter, map);
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
                        getSimpleProductCard(pos + 1, productsAccounts, cardGridArrayAdapter, map);
                        return;
                    }
                    bmp1[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    card[0] = new CustomerProductCard(CustomerMain.utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
                    productDBhelper.InsertProduct(card[0]);
                    cardGridArrayAdapter.add(card[0]);
                    cardGridArrayAdapter.notifyDataSetChanged();
                    getSimpleProductCard(pos + 1, productsAccounts, cardGridArrayAdapter, map);
                }
            });
        }
        else {
            bmp1[0] = BitmapFactory.decodeResource(CustomerMain.utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card[0] = new CustomerProductCard(CustomerMain.utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
            productDBhelper.InsertProduct(card[0]);
            cardGridArrayAdapter.add(card[0]);
            cardGridArrayAdapter.notifyDataSetChanged();
            getSimpleProductCard(pos + 1, productsAccounts, cardGridArrayAdapter, map);
        }
    }

    private void syncData() {
        getActionBar().setTitle(CustomerMain.SelectedProducts);
        getLocation();
        if(firstloaded)
            showprogess();
        ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
        query.whereEqualTo("Type", CustomerMain.SelectedProducts);
        final ParseGeoPoint temp =new ParseGeoPoint(ConfigData.signuplatlng.latitude,ConfigData.signuplatlng.longitude);
        if(ConfigData.signuplatlng.latitude!=0) {
            if(ConfigData.filter_dist==-1)
                query.whereNear("Location", temp);
            else
            {
                query.addDescendingOrder("Like");
                query.whereWithinKilometers("Location",temp,ConfigData.filter_dist);
            }
        }
        query.setSkip(skip * load_at_time);
        query.setLimit(load_at_time);
        if(ConfigData.filter_price_low!=-1) {
            query.whereGreaterThanOrEqualTo("Price", ConfigData.filter_price_low);
            query.whereLessThanOrEqualTo("Price", ConfigData.filter_price_high);
        }
        query.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(final List<ProductsAccount> productsAccounts, ParseException e) {
                stopprogress();
                if(e != null)
                {
                    CustomerMain.utilityFunction.ToastMessage("Error in Syncing Data");
                    return;
                }
                if (gridView != null) {
                    gridView.setAdapter(cardGridArrayAdapter);
                }
                gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {
                    }

                    @Override
                    public void onScroll(AbsListView absListView, int firstvisible, int visibleitem, final int totalitem) {
                        if(firstvisible+visibleitem >=totalloaded)
                        {
                            skip++;
                            totalloaded+=load_at_time;
                            syncData();
                        }
                    }
                });
                if(firstloaded&&productsAccounts.size()==0)
                {
                    ShowAlert_NoProductsAvailable();
                }
                firstloaded=false;
                if(ConfigData.filter_cod)
                    getRetailerProducts(productsAccounts,cardGridArrayAdapter);
                else
                    getSimpleProductCard(0, productsAccounts, cardGridArrayAdapter, null);
                cardGridArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getRetailerProducts(final List<ProductsAccount> productsAccounts, final CardGridArrayAdapter cardGridArrayAdapter) {
        List<String> retailers = new ArrayList<>();
        final HashMap<String,Boolean> map = new HashMap<>();
        for(ProductsAccount i:productsAccounts)
            if(!map.containsKey(i.getUser()))
            {
                retailers.add(i.getUser());
                map.put(i.getUser(),true);
            }
        ParseQuery<RetailerProfileAccount> query = ParseQuery.getQuery("RetailerProfile");
        query.whereContainedIn("User", retailers);
        query.whereEqualTo("CashOnDelivery",true);
        query.findInBackground(new FindCallback<RetailerProfileAccount>() {
            @Override
            public void done(List<RetailerProfileAccount> list, ParseException e) {
                if(e!=null){
                    stopprogress();
                    utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                }
                else
                {
                    map.clear();
                    for(RetailerProfileAccount i:list)
                        map.put(i.getUser(),true);
                    getSimpleProductCard(0,productsAccounts,cardGridArrayAdapter,map);
                }
            }
        });
    }

    private void getLocation() {
        //if(ConfigData.signuplatlng != null&&ConfigData.signuplatlng.latitude != 0.0&&ConfigData.signuplatlng.longitude != 0.0)
        //    return;
        GPSTracker gpsTracker = new GPSTracker(CustomerMain.utilityFunction.activity);
        if (gpsTracker.canGetLocation()) {
            ConfigData.signuplatlng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cardGridArrayAdapter==null)
            return;
        cardGridArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopprogress();
    }

    private void go_to_NextFragment(boolean is_search) {
        gpsTracker = new GPSTracker(getApplicationContext());
        ConfigData.is_Search = is_search;
        cardGridArrayAdapter.clear();
        if (!gpsTracker.canGetLocation()) {
            showGpsAlert();
            return;
        }
        if(!is_search)
            ((EditText)findViewById(R.id.search_query)).setText("");
        Start_Work();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == FILTER_CODE){
                cardGridArrayAdapter.clear();
                Start_Work();
            }
    }

}
