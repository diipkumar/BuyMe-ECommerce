package com.online.shopping.buyme.Customer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.BasketCopyInterFace;
import com.online.shopping.buyme.CustomerExtras.BasketDBhelper;
import com.online.shopping.buyme.CustomerExtras.CustomerBasketCopyCard;
import com.online.shopping.buyme.CustomerExtras.CustomerProductCard;
import com.online.shopping.buyme.CustomerExtras.CustomerProductInfo;
import com.online.shopping.buyme.CustomerExtras.LikeDBhelper;
import com.online.shopping.buyme.CustomerExtras.WishedDBhelper;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.WishListAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.common.ProductDBhelper;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

public class FriendList extends CustomeActivity implements BasketCopyInterFace {
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
        setContentView(R.layout.activity_friendlist);
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

    private  void init() {
        cardArrayAdapter = new CardArrayAdapter(this,new ArrayList<Card>());
        ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
        query.whereContainedIn("objectId", Friends_WishList.To_Be_loaded_Products);
        query.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                if (e != null) {
                    stopprogress();
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                }
                getSimpleProductCard(0,productsAccounts,cardArrayAdapter);
                listView.setAdapter(cardArrayAdapter);
            }
        });
    }

    private void getSimpleProductCard(final int pos, final List<ProductsAccount> productsAccounts, final CardArrayAdapter horizontalAdapter) {
        if(pos > productsAccounts.size()-1)
        {
            stopprogress();
            return;
        }
        final ProductsAccount Currentproduct = productsAccounts.get(pos);
        String imagescount = Currentproduct.getimages();
        final Bitmap[] bmp1 = new Bitmap[1];
        final Bitmap bmp2 = null,bmp3 = null;
        final CustomerProductCard[] card = {productDBhelper.CheckAndReturnCustomerProduct(Currentproduct)};

        if(card[0] !=null)
        {
            horizontalAdapter.add(new CustomerBasketCopyCard(FriendList.this,card[0]));
            horizontalAdapter.notifyDataSetChanged();
            getSimpleProductCard(pos + 1, productsAccounts,horizontalAdapter);
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
                        getSimpleProductCard(pos + 1, productsAccounts, horizontalAdapter);
                        return;
                    }
                    bmp1[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    card[0] = new CustomerProductCard(utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
                    productDBhelper.InsertProduct(card[0]);
                    horizontalAdapter.add(new CustomerBasketCopyCard(FriendList.this,card[0]));
                    horizontalAdapter.notifyDataSetChanged();
                    getSimpleProductCard(pos + 1, productsAccounts, horizontalAdapter);
                }
            });
        }
        else {
            bmp1[0] = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card[0] = new CustomerProductCard(utilityFunction.activity, bmp1[0], bmp2, bmp3,Currentproduct);
            productDBhelper.InsertProduct(card[0]);
            horizontalAdapter.add(new CustomerBasketCopyCard(FriendList.this,card[0]));
            horizontalAdapter.notifyDataSetChanged();
            getSimpleProductCard(pos + 1, productsAccounts, horizontalAdapter);
        }
    }

    @Override
    public void BasketCardClicked(CustomerBasketCopyCard customerBasketCard) {
        showprogess();
        Intent i = new Intent(this, CustomerProductInfo.class);
        i.putExtra("objectId",customerBasketCard.card.productsAccount.getObjectId());
        startActivity(i);
        stopprogress();
    }

    @Override
    public void LikeOptionClicked(final CustomerBasketCopyCard cardPointer, final ImageView view) {
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
    public void BasketWishedOptionClicked(final CustomerBasketCopyCard cardPointer, final ImageView view) {
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
    public void BasketSwiped(CustomerBasketCopyCard card) {
    }

    @Override
    public void BasketUndoOption(CustomerBasketCopyCard cardpointer) {
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
