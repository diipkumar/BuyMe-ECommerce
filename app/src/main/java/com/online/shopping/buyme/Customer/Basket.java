package com.online.shopping.buyme.Customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.BasketDBhelper;
import com.online.shopping.buyme.CustomerExtras.BasketInterFace;
import com.online.shopping.buyme.CustomerExtras.CustomerBasketCard;
import com.online.shopping.buyme.CustomerExtras.CustomerProductInfo;
import com.online.shopping.buyme.CustomerExtras.LikeDBhelper;
import com.online.shopping.buyme.CustomerExtras.WishedDBhelper;
import com.online.shopping.buyme.Extras.SimpleProductCard;
import com.online.shopping.buyme.ParseAccount.OrderStatusAccount;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.RetailerProfileAccount;
import com.online.shopping.buyme.ParseAccount.WishListAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

public class Basket extends CustomeActivity implements BasketInterFace {

    BasketDBhelper basketDBhelper;
    ProductDBhelper productDBhelper;
    WishedDBhelper wishedDBhelper;
    LikeDBhelper likeDBhelper;
    TextView Buynow;
    UtilityFunction utilityFunction;
    public static CardArrayAdapter cardArrayAdapter;
    CardListView listView;
    private Dialog confirm_order_dialog;
    private TextView totalprices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        setupDbhelper();
        setListeners();
        init();
        setAnimation();
        setTotalPrice();
    }

    private void setTotalPrice() {
        long totalprice =0;
        for(int i=0;i<cardArrayAdapter.getCount();i++)
        {
            CustomerBasketCard customerBasketCard = (CustomerBasketCard) cardArrayAdapter.getItem(i);
            totalprice+=customerBasketCard.quantitynumber*Integer.valueOf(customerBasketCard.card.productsAccount.getPrice());
        }
        totalprices.setText(String.valueOf(totalprice));
    }

    private void setAnimation() {

        Animation enter = AnimationUtils.loadAnimation(this, R.anim.layoutanim);

        it.gmariotti.cardslib.library.view.CardListView card = (it.gmariotti.cardslib.library.view.CardListView)findViewById(R.id.carddemo_list_base1);

        card.startAnimation(enter);
    }

    private void setupDbhelper() {
        wishedDBhelper = new WishedDBhelper(this);
        productDBhelper = new ProductDBhelper(this);
        basketDBhelper = new BasketDBhelper(this);
        likeDBhelper = new LikeDBhelper(this);
    }

    private void setListeners() {
        utilityFunction = new UtilityFunction(this);
        listView= (CardListView) findViewById(R.id.carddemo_list_base1);
        totalprices= (TextView) findViewById(R.id.totalprice);
        Buynow = (TextView) findViewById(R.id.orderbasket);
        Buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_Confirm_order();
            }
        });
    }

    private void show_Confirm_order() {
            confirm_order_dialog= new Dialog(this);
            confirm_order_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            confirm_order_dialog.setContentView(R.layout.dialog_confirmorder);
            TextView ok = (TextView)confirm_order_dialog.findViewById(R.id.ok);
            final TextView cancel = (TextView)confirm_order_dialog.findViewById(R.id.cancel);
            final EditText customername= (EditText) confirm_order_dialog.findViewById(R.id.cust_name);
            final EditText customeraddress = (EditText) confirm_order_dialog.findViewById(R.id.cust_address);
            final EditText customernumber = (EditText) confirm_order_dialog.findViewById(R.id.cust_number);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customeraddress.getText().toString().length() == 0 || customername.getText().toString().length() == 0) {
                        utilityFunction.ToastMessage("Please Fill All Details");
                        return;
                    }
                    if (customernumber.getText().toString().length() != 10) {
                        utilityFunction.ToastMessage("Incorrect Mobile Number");
                        return;
                    }
                    showprogess();
                    List<String> retaileruser = new ArrayList<>();
                    final HashMap<String, List<String>> retailer_to_product = new HashMap<>();
                    final HashMap<String, String> produt_to_quantity= new HashMap<>();
                    ArrayList<CustomerBasketCard> customerBasketCards = basketDBhelper.getAllCustomerProduct(productDBhelper);
                    for (CustomerBasketCard i : customerBasketCards)
                    {
                        produt_to_quantity.put(i.card.productsAccount.getObjectId(),String.valueOf(i.quantitynumber));
                        if (retailer_to_product.containsKey(i.card.productsAccount.getUser())) {
                            retailer_to_product.get(i.card.productsAccount.getUser()).add(i.card.productsAccount.getObjectId());
                        } else {
                            List<String> temp = new ArrayList<>();
                            temp.add(i.card.productsAccount.getObjectId());
                            retailer_to_product.put(i.card.productsAccount.getUser(), temp);
                            retaileruser.add(i.card.productsAccount.getUser());
                        }
                    }
                    ParseQuery<RetailerProfileAccount> query = ParseQuery.getQuery("RetailerProfile");
                    query.whereContainedIn("User", retaileruser);
                    query.findInBackground(new FindCallback<RetailerProfileAccount>() {
                        @Override
                        public void done(final List<RetailerProfileAccount> retailerProfileAccounts, ParseException e) {
                            if (e != null) {
                                stopprogress();
                                utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                                return;
                            }
                            boolean ShowProceedDialog= false;
                            for (RetailerProfileAccount i : retailerProfileAccounts) {
                                if (!i.getCashOnDelivery()) {
                                    ShowProceedDialog = true;
                                    List<String> objecid = retailer_to_product.get(i.getUser());
                                    for(int count =0;count<cardArrayAdapter.getCount();count++)
                                    {
                                        for(String objectid :objecid)
                                        if(((CustomerBasketCard)cardArrayAdapter.getItem(count)).card.productsAccount.getObjectId().equals(objectid))
                                        {
                                            ((CustomerBasketCard)cardArrayAdapter.getItem(count)).setBackGroundGrey();
                                            ((CustomerBasketCard)cardArrayAdapter.getItem(count)).init();
                                        }
                                    }
                                }
                            }
                            cardArrayAdapter.notifyDataSetChanged();
                            if(ShowProceedDialog)
                            {
                                stopprogress();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Basket.this);
                                alertDialog.setTitle(getResources().getString(R.string.Proceed_Basket));
                                alertDialog.setMessage(getResources().getString(R.string.Proceed_Note));
                                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        showprogess();
                                        saveOrder(retailerProfileAccounts,retailer_to_product,customername.getText().toString(),customernumber.getText().toString(), customeraddress.getText().toString(),0,produt_to_quantity);
                                    }
                                });
                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertDialog.show();
                            }
                            else
                                saveOrder(retailerProfileAccounts, retailer_to_product, customername.getText().toString(), customernumber.getText().toString(), customeraddress.getText().toString(), 0,produt_to_quantity);
                            confirm_order_dialog.dismiss();
                        }
                    });
                }});
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirm_order_dialog.dismiss();
                }
            });
            confirm_order_dialog.show();
    }

    private void saveOrder(final List<RetailerProfileAccount> retailerProfileAccounts, final HashMap<String, List<String>> retailer_to_product, final String customername, final String customernumber, final String customeraddress, final int pos, final HashMap<String, String> produt_to_quantity) {
        if(pos > retailerProfileAccounts.size()-1)
        {
            stopprogress();
            init();
            return;
        }
        final RetailerProfileAccount currentRetailerProfileAccount = retailerProfileAccounts.get(pos);
        if(!currentRetailerProfileAccount.getCashOnDelivery())
        {
            saveOrder(retailerProfileAccounts, retailer_to_product, customername,customernumber, customeraddress, pos+1, produt_to_quantity);
            return;
        }
        List<String> products_objectid = retailer_to_product.get(currentRetailerProfileAccount.getUser());
        List<String> product_with_quantity = new ArrayList<>();
        for(String j:products_objectid)
            product_with_quantity.add(j+",;,"+produt_to_quantity.get(j));

        final OrderStatusAccount orderStatusAccount = new OrderStatusAccount(customername,customernumber,customeraddress,
                currentRetailerProfileAccount.get_shop_name(),currentRetailerProfileAccount.getUser(),currentRetailerProfileAccount.getAddress(),
                product_with_quantity,ConfigData.currentuser,currentRetailerProfileAccount.getUser());
        orderStatusAccount.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    stopprogress();
                    init();
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                }
                List<String> object = retailer_to_product.get(currentRetailerProfileAccount.getUser());
                for(String i:object)
                    basketDBhelper.deleteProduct(i);
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("user", currentRetailerProfileAccount.getUser());
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setMessage(getResources().getString(R.string.notifcationmessage));
                push.sendInBackground();
                saveOrder(retailerProfileAccounts, retailer_to_product, customername, customernumber, customeraddress, pos + 1, produt_to_quantity);
            }
        });
    }

    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    private  void init() {
        cardArrayAdapter = new CardArrayAdapter(this,new ArrayList<Card>());
        ArrayList<CustomerBasketCard> customerBasketCards=basketDBhelper.getAllCustomerProduct(productDBhelper);
        int count=0;
        for(CustomerBasketCard i:customerBasketCards)
        {
            i.setSwipeable(true);
            i.setId(String.valueOf(count++));
            cardArrayAdapter.add(i);
        }
        cardArrayAdapter.setEnableUndo(true);
        listView.setAdapter(cardArrayAdapter);
        totalprices.setText("0");
        Check_Basket_One_Time(customerBasketCards);
    }


    private void Check_Basket_One_Time(ArrayList<CustomerBasketCard> customerBasketCards) {
        if(ConfigData.BasketChecked)
            return;
        final List<String> objectid = new ArrayList<>();
        final HashMap<String,Boolean> map = new HashMap<>();
        for(CustomerBasketCard i:customerBasketCards)
        {
            map.put(i.card.productsAccount.getObjectId(),false);
            objectid.add(i.card.productsAccount.getObjectId());
        }
        ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
        query.whereContainedIn("objectId", objectid);
        query.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                if (e != null) {
                    utilityFunction.ToastMessage("Error in Syncing Data");
                    return;
                }
                for(ProductsAccount i:productsAccounts)
                    map.put(i.getObjectId(),true);
                for(String i:objectid)
                if(!map.get(i))
                {
                    productDBhelper.deleteProduct(i);
                    likeDBhelper.deleteProduct(i);
                    wishedDBhelper.deleteProduct(i);
                    basketDBhelper.deleteProduct(i);
                }
                getSimpleProductCard(0, productsAccounts);
            }
        });
    }

    private void getSimpleProductCard(final int pos, final List<ProductsAccount> productsAccounts) {
        if(pos > productsAccounts.size()-1)
        {
            ConfigData.BasketChecked = true;
            init();
            return;
        }
        final ProductsAccount Currentproduct = productsAccounts.get(pos);
        String imagescount = Currentproduct.getimages();
        final Bitmap[] bmp1 = new Bitmap[1];
        final SimpleProductCard[] card = {productDBhelper.CheckAndReturnSimpleProductCard(Currentproduct)};
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
                        utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        getSimpleProductCard(pos + 1, productsAccounts);
                        return;
                    }
                    bmp1[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    card[0] = new SimpleProductCard (utilityFunction.activity, bmp1[0], null,null,Currentproduct);
                    productDBhelper.InsertProduct(card[0]);
                    getSimpleProductCard(pos + 1, productsAccounts);
                }
            });
        }
        else {
            bmp1[0] = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card[0] = new SimpleProductCard (utilityFunction.activity, bmp1[0], null, null,Currentproduct);
            productDBhelper.InsertProduct(card[0]);
            getSimpleProductCard(pos + 1, productsAccounts);
        }
    }

    @Override
    public void BasketCardClicked(CustomerBasketCard customerBasketCard) {
        showprogess();
        Intent i = new Intent(this, CustomerProductInfo.class);
        i.putExtra("objectId",customerBasketCard.card.productsAccount.getObjectId());
        startActivity(i);
        stopprogress();
    }

    @Override
    public void LikeOptionClicked(final CustomerBasketCard cardPointer, final ImageView view) {
        final boolean isLiked = (boolean) (view).getTag();
        if(isLiked)
        {
            productDBhelper.UpdateLike(cardPointer.card.productsAccount.getObjectId(),false);
            likeDBhelper.deleteProduct(cardPointer.card.productsAccount.getObjectId());
            ( view).setImageResource(R.drawable.like);
        }
        else
        {
            productDBhelper.UpdateLike(cardPointer.card.productsAccount.getObjectId(),true);
            likeDBhelper.InsertProduct(cardPointer.card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.liked);
        }
        view.setTag(!isLiked);
            ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
            query.whereEqualTo("objectId", cardPointer.card.productsAccount.getObjectId());
            query.findInBackground(new FindCallback<ProductsAccount>() {
                @Override
                public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                    if (productsAccounts.size()==0||e != null) {
                        view.setTag(isLiked);
                        if(isLiked)
                        {
                            productDBhelper.UpdateLike(cardPointer.card.productsAccount.getObjectId(),true);
                            likeDBhelper.InsertProduct(cardPointer.card.productsAccount.getObjectId());
                            (view).setImageResource(R.drawable.liked);
                        }
                        else
                        {
                            productDBhelper.UpdateLike(cardPointer.card.productsAccount.getObjectId(),false);
                            likeDBhelper.deleteProduct(cardPointer.card.productsAccount.getObjectId());
                            (view).setImageResource(R.drawable.like);
                        }
                        utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                        return;
                    }
                    if(isLiked)
                    productsAccounts.get(0).increment("Like", -1);
                    else
                    productsAccounts.get(0).increment("Like",1);
                    productsAccounts.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                view.setTag(isLiked);
                                if(isLiked)
                                {
                                    likeDBhelper.InsertProduct(cardPointer.card.productsAccount.getObjectId());
                                    productDBhelper.UpdateLike(cardPointer.card.productsAccount.getObjectId(),true);
                                    (view).setImageResource(R.drawable.liked);
                                }
                                else
                                {
                                    productDBhelper.UpdateLike(cardPointer.card.productsAccount.getObjectId(),false);
                                    likeDBhelper.deleteProduct(cardPointer.card.productsAccount.getObjectId());
                                    (view).setImageResource(R.drawable.like);
                                }
                                utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                            }
                        }
                    });
                }
            });
    }

    @Override
    public void BasketWishedOptionClicked(final CustomerBasketCard cardPointer, final ImageView view) {
        boolean isWished = (boolean) (view).getTag();
        view.setTag(!isWished);
        if(isWished)
        {
            wishedDBhelper.deleteProduct(cardPointer.card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.wishlist_empty);
        }
        else
        {
            wishedDBhelper.InsertProduct(cardPointer.card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.wishlist_filled);
        }

        if (isWished) {
            ParseQuery<WishListAccount> query2 = ParseQuery.getQuery("WishList");
            query2.whereEqualTo("ObjectId", cardPointer.card.productsAccount.getObjectId());
            query2.whereEqualTo("User", ConfigData.currentuser);
            query2.findInBackground(new FindCallback<WishListAccount>() {
                @Override
                public void done(final List<WishListAccount> wishListAccounts, ParseException e) {
                    if(e!=null)
                    {
                        view.setTag(true);
                        wishedDBhelper.InsertProduct(cardPointer.card.productsAccount.getObjectId());
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
                                wishedDBhelper.InsertProduct(cardPointer.card.productsAccount.getObjectId());
                                utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                            }
                        }
                    });
                }
            });
        } else {
            WishListAccount basketAccount = new WishListAccount(cardPointer.card.productsAccount.getObjectId(),ConfigData.currentuser);
            basketAccount.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        view.setTag(false);
                        (view).setImageResource(R.drawable.wishlist_filled);
                        wishedDBhelper.deleteProduct(cardPointer.card.productsAccount.getObjectId());
                        utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    }
                }
            });
        }
    }

    @Override
    public void BasketSwiped(CustomerBasketCard card) {
        basketDBhelper.deleteProduct(card.card.productsAccount.getObjectId());
    }

    @Override
    public void BasketUndoOption(CustomerBasketCard cardpointer) {
        basketDBhelper.InsertProduct(cardpointer.card.productsAccount.getObjectId());
        init();
    }

    @Override
    public void quantitySelected(CustomerBasketCard customerBasketCard) {
        basketDBhelper.update_quantity(customerBasketCard);
        setTotalPrice();
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
