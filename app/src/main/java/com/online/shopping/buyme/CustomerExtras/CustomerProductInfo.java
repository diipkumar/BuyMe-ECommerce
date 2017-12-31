package com.online.shopping.buyme.CustomerExtras;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Extras.CustomViewPageAdaptor;
import com.online.shopping.buyme.Extras.CustomViewPager;
import com.online.shopping.buyme.Extras.ImageFragment;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.WishListAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.Retailer.SellersInfo;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.List;

public class CustomerProductInfo extends CustomeActivity implements View.OnClickListener {

    BasketDBhelper basketDBhelper;
    ProductDBhelper productDBhelper;
    WishedDBhelper wishedDBhelper;
    CustomViewPager pager;
    CustomViewPageAdaptor pageAdapter;
    CustomerProductCard card;
    ImageView wishlist,basket,image1,image2,image3;
    TextView productname,price,description,sellersinfo;
    UtilityFunction utilityFunction;
    Bitmap Loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_info);
        Loading= BitmapFactory.decodeResource(getResources(), RetailerMain.DefaultImage);
        setupDbhelper();
        card = productDBhelper.GetCustomerProductCard_From_Object_id(getIntent().getStringExtra("objectId"));
        SyncProduct();
        ConfigData.SharedProductObjectID = "";
        initialize();
    }

    private void SyncProduct() {
        card = productDBhelper.GetCustomerProductCard_From_Object_id(getIntent().getStringExtra("objectId"));
        if(card!=null&&card.AlreadyLoaded==1)
            return;
        ParseQuery<ProductsAccount> query2 = ParseQuery.getQuery("Products");
        query2.whereEqualTo("objectId",getIntent().getStringExtra("objectId"));
        query2.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                if(e!=null)
             {
                 utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                 finish();
                 return;
             }
             if(productsAccounts.size()==0)
             {
                 productDBhelper.deleteProduct(card.productsAccount.getObjectId());
                 utilityFunction.SanckMessage(getResources().getString(R.string.product_doesnt_exist));
                 finish();
                 return;
             }
            card =productDBhelper.CheckAndReturnCustomerProduct(productsAccounts.get(0));
            if(card==null)
            {
                LoadImage1(productsAccounts);
            }
            else
            {
                LoadImage2(productsAccounts);
            }
            }
        });
    }

    private void LoadImage1(final List<ProductsAccount> productsAccounts) {
        if (productsAccounts.get(0).getimages().contains("1"))
        {
            productsAccounts.get(0).getParse1().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if(e!=null)
                    {
                        utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        return;
                    }
                    Bitmap bmp1 = BitmapFactory.decodeByteArray(data, 0, data.length);
                    card = new CustomerProductCard(CustomerProductInfo.this,bmp1,null,null,productsAccounts.get(0));
                    productDBhelper.InsertProduct(card);
                    initialize();
                    image1.setImageBitmap(card.resource1);
                    LoadImage2(productsAccounts);
                }
            });
        }
        else
        {
            Bitmap bmp1 = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card = new CustomerProductCard(CustomerProductInfo.this,bmp1,null,null,productsAccounts.get(0));
            productDBhelper.InsertProduct(card);
            image1.setImageBitmap(card.resource1);
            initialize();
            LoadImage2(productsAccounts);
        }
    }

    private void LoadImage3(final List<ProductsAccount> productsAccounts) {
        if (productsAccounts.get(0).getimages().contains("3"))
        {
            productsAccounts.get(0).getParse3().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if(e!=null)
                    {
                        utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        return;
                    }
                    card.resource3= BitmapFactory.decodeByteArray(data, 0, data.length);
                    card.AlreadyLoaded=1;
                    setListeners();
                    initialize();
                    productDBhelper.update_Remaining_Images(card);
                    image3.setImageBitmap(card.resource3);
                }
            });
        }
        else
        {
            card.resource3 = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card.AlreadyLoaded=1;
            initialize();
            productDBhelper.update_Remaining_Images(card);
            image3.setImageBitmap(card.resource3);
        }
    }

    private void LoadImage2(final List<ProductsAccount> productsAccounts) {
        if (productsAccounts.get(0).getimages().contains("2"))
        {
            productsAccounts.get(0).getParse2().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if(e!=null)
                    {
                        utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        return;
                    }
                    card.resource2= BitmapFactory.decodeByteArray(data, 0, data.length);
                    image2.setImageBitmap(card.resource2);
                    LoadImage3(productsAccounts);
                }
            });
        }
        else
        {
            card.resource2 = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            image2.setImageBitmap(card.resource2);
            LoadImage3(productsAccounts);
        }
    }

    private void initialize() {
        setListeners();
        setupData();
        setPager();
    }

    private void setupDbhelper() {
        wishedDBhelper = new WishedDBhelper(this);
        productDBhelper = new ProductDBhelper(this);
        basketDBhelper = new BasketDBhelper(this);
    }

    private void setupData() {
        if(card==null)
            return;
        productname.setText(card.productsAccount.getname());
        price.setText(" "+card.productsAccount.getPrice());
        description.setText(card.productsAccount.getDescription());
        image1.setImageBitmap(card.resource1);
        if(card.AlreadyLoaded==1)
        {
            image2.setImageBitmap(card.resource2);
            image3.setImageBitmap(card.resource3);
        }
    }

    private void setListeners() {
        getActionBar();
        utilityFunction = new UtilityFunction(this);
        if(card==null)
            return;
        productname = (TextView) findViewById(R.id.productname);
        price = (TextView) findViewById(R.id.productprice);
        description = (TextView) findViewById(R.id.productdescription);
        sellersinfo = (TextView) findViewById(R.id.ProductSellersInfo);
        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);
        ImageView product_share = (ImageView) findViewById(R.id.productshare);
        wishlist = (ImageView) findViewById(R.id.wishlist);
        basket = (ImageView) findViewById(R.id.basket);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        wishlist.setOnClickListener(this);
        basket.setOnClickListener(this);
        sellersinfo.setOnClickListener(this);
        product_share.setOnClickListener(this);

        if (basketDBhelper.Contain_Product(card.productsAccount.getObjectId())) {
                basket.setTag(true);
                basket.setImageResource(R.drawable.carted);
            } else {
                basket.setTag(false);
                basket.setImageResource(R.drawable.cart);
            }


        if(wishedDBhelper.Contain_Product(card.productsAccount.getObjectId()))
        {
            wishlist.setTag(true);
            wishlist.setImageResource(R.drawable.wishlist_filled);
        }
        else
        {
            wishlist.setTag(false);
            wishlist.setImageResource(R.drawable.wishlist_empty);
        }
    }

    @Override
    public void onClick(View view) {
        int id =view.getId();
        switch (id)
        {
            case R.id.ProductSellersInfo:
                Intent i =new Intent(CustomerProductInfo.this,SellersInfo.class);
                i.putExtra("User",card.productsAccount.getUser());
                startActivity(i);
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
            case R.id.basket:
                if(view.getTag()==null)
                    return;
                BasketOptionClicked((ImageView) view);
                    break;
            case R.id.wishlist:
                if(view.getTag()==null)
                    return;
               WishedOptionClicked((ImageView) view);
                break;
            case R.id.productshare:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check out this product http://"+getResources().getString(R.string.AppProductShareLink) +"/"+card.productsAccount.getObjectId()+" "+getResources().getString(R.string.ProductShareMessage);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
        }
    }

    private void WishedOptionClicked(final ImageView view) {
        boolean isWished = (boolean) (view).getTag();
        view.setTag(!isWished);
        if(isWished)
        {
            wishedDBhelper.deleteProduct(card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.wishlist_empty);
        }
        else
        {
            wishedDBhelper.InsertProduct(card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.wishlist_filled);
        }

        if (isWished) {
            ParseQuery<WishListAccount> query2 = ParseQuery.getQuery("WishList");
            query2.whereEqualTo("ObjectId", card.productsAccount.getObjectId());
            query2.whereEqualTo("User", ConfigData.currentuser);
            query2.findInBackground(new FindCallback<WishListAccount>() {
                @Override
                public void done(final List<WishListAccount> wishListAccounts, ParseException e) {
                if(e!=null)
                {
                    view.setTag(true);
                    wishedDBhelper.InsertProduct(card.productsAccount.getObjectId());
                    (view).setImageResource(R.drawable.wishlist_filled);
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    return;
                }
                if(wishListAccounts.size()==0)
                {
                    return;
                }
                wishListAccounts.get(0).deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null)
                        {
                            view.setTag(true);
                            (view).setImageResource(R.drawable.wishlist_filled);
                            wishedDBhelper.InsertProduct(card.productsAccount.getObjectId());
                            utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                        }
                    }
                });
                }
            });
        } else {
            WishListAccount basketAccount = new WishListAccount(card.productsAccount.getObjectId(),ConfigData.currentuser);
            basketAccount.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        view.setTag(false);
                        wishedDBhelper.deleteProduct(card.productsAccount.getObjectId());
                        (view).setImageResource(R.drawable.wishlist_filled);
                        utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    }
                }
            });
        }
    }

    public void BasketOptionClicked(final ImageView view) {
        boolean isBasket = (boolean) (view).getTag();
        view.setTag(!isBasket);
        if(isBasket)
        {
            basketDBhelper.deleteProduct(card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.cart);
        }
        else
        {
            basketDBhelper.InsertProduct(card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.carted);
        }
    }

    private void setPager() {
        List<Fragment> fragments = getFragments();
        if(getFragmentManager()==null)
            return;
        pageAdapter = new CustomViewPageAdaptor(getFragmentManager(), fragments);
        pager = (CustomViewPager)findViewById(R.id.viewpager);
        if(isFinishing())
            return;
        pager.setAdapter(pageAdapter);
        pager.setScrollDurationFactor(2);
        setalpha(1, .50f, .50f);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<>();
        ImageFragment a = new ImageFragment();
        ImageFragment c = new ImageFragment();
        ImageFragment b = new ImageFragment();
        if(card==null) {
            a.setBitmap(Loading);
            b.setBitmap(Loading);
            c.setBitmap(Loading);
        }
        else
        {
            a.setBitmap(card.resource1);
            b.setBitmap(card.resource2);
            c.setBitmap(card.resource3);
        }
        fList.add(a);
        fList.add(b);
        fList.add(c);
        return fList;
    }

    private void setalpha(float i, float v, float v1) {
        if(image1==null||image2==null||image3==null)
            return;
        image1.setAlpha(i);
        image2.setAlpha(v);
        image3.setAlpha(v1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wishedDBhelper.close();
        productDBhelper.close();
        basketDBhelper.close();
    }

}

