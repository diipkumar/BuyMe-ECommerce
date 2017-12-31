package com.online.shopping.buyme.Customer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.BasketDBhelper;
import com.online.shopping.buyme.CustomerExtras.CustomerProductInfo;
import com.online.shopping.buyme.CustomerExtras.CustomerWishedCard;
import com.online.shopping.buyme.CustomerExtras.LikeDBhelper;
import com.online.shopping.buyme.CustomerExtras.WishedDBhelper;
import com.online.shopping.buyme.CustomerExtras.WishedInterFace;
import com.online.shopping.buyme.Extras.SimpleProductCard;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
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

public class WishList extends CustomeActivity implements WishedInterFace{
    BasketDBhelper basketDBhelper;
    ProductDBhelper productDBhelper;
    WishedDBhelper wishedDBhelper;
    LikeDBhelper likeDBhelper;
    UtilityFunction utilityFunction;
    public static CardArrayAdapter cardArrayAdapter;
    CardListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        setupDbhelper();
        setListeners();
        init();
        setAnimation();
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
    }

    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    private  void init() {
        cardArrayAdapter = new CardArrayAdapter(this,new ArrayList<Card>());
        ArrayList<CustomerWishedCard> customerBasketCards=wishedDBhelper.getAllCustomerProduct(productDBhelper);
        int count=0;
        for(CustomerWishedCard i:customerBasketCards)
        {
            i.setSwipeable(true);
            i.setId(String.valueOf(count++));
            cardArrayAdapter.add(i);
        }
        cardArrayAdapter.setEnableUndo(true);
        listView.setAdapter(cardArrayAdapter);
        Check_Wished_One_Time(customerBasketCards);
    }

    private void Check_Wished_One_Time(ArrayList<CustomerWishedCard> customerBasketCards) {
        if(ConfigData.WishListchecked)
            return;
        final List<String> objectid = new ArrayList<>();
        final HashMap<String,Boolean> map = new HashMap<>();
        for(CustomerWishedCard i:customerBasketCards)
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
            ConfigData.WishListchecked = true;
            init();
            return;
        }
        final ProductsAccount Currentproduct = productsAccounts.get(pos);
        String imagescount = Currentproduct.getimages();
        final Bitmap[] bmp1 = new Bitmap[1];
        final Bitmap bmp2 = null,bmp3 = null;
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
                    card[0] = new SimpleProductCard (utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
                    productDBhelper.InsertProduct(card[0]);
                    getSimpleProductCard(pos + 1, productsAccounts);
                }
            });
        }
        else {
            bmp1[0] = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card[0] = new SimpleProductCard (utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
            productDBhelper.InsertProduct(card[0]);
            getSimpleProductCard(pos + 1, productsAccounts);
        }
    }

    @Override
    public void WishedCardClicked(CustomerWishedCard card) {
        showprogess();
        Intent i = new Intent(this, CustomerProductInfo.class);
        i.putExtra("objectId",card.card.productsAccount.getObjectId());
        startActivity(i);
        stopprogress();
    }

    @Override
    public void LikeOptionClicked(final CustomerWishedCard cardPointer, final ImageView view) {
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
                        }
                    }
                });
            }
        });
    }

    @Override
    public void WishedBasketOptionClicked(CustomerWishedCard cardPointer, ImageView view) {
        boolean isBasket = (boolean) (view).getTag();
        view.setTag(!isBasket);
        if(isBasket)
        {
            basketDBhelper.deleteProduct(cardPointer.card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.cart);
        }
        else
        {
            basketDBhelper.InsertProduct(cardPointer.card.productsAccount.getObjectId());
            (view).setImageResource(R.drawable.carted);
        }
    }

    @Override
    public void WishedSwiped(CustomerWishedCard card) {
            wishedDBhelper.deleteProduct(card.card.productsAccount.getObjectId());
        }

    @Override
    public void WishedUndoOption(CustomerWishedCard cardpointer) {
        wishedDBhelper.InsertProduct(cardpointer.card.productsAccount.getObjectId());
        init();
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
