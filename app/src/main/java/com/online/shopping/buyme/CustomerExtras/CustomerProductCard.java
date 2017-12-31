package com.online.shopping.buyme.CustomerExtras;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Customer.CustomerBaseAcctivity;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.WishListAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import java.util.List;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

public class CustomerProductCard extends Card {

    public ProductsAccount productsAccount;
    public int AlreadyLoaded = 0;
    private Context context;
    public Bitmap resource1,resource2,resource3;
    public ImageView basket;
    public ImageView LikeImage;
    public TextView LikestextView;
    public ImageView Wishlist;

    public CustomerProductCard(Context context, Bitmap imgDecodableString1, Bitmap imgDecodableString2, Bitmap imgDecodableString3, ProductsAccount productsAccount) {
        super(context, R.layout.gridviewitem_lowercontent);
        this.context = context;
        this.productsAccount = productsAccount;
        resource1 = imgDecodableString1;
        resource2 = imgDecodableString2;
        resource3 = imgDecodableString3;
        AlreadyLoaded = 0;
        init();
    }

    public void init() {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), RetailerMain.DefaultImage);
        if(!productsAccount.getimages().contains("1"))
            resource1 = icon;
        if(!productsAccount.getimages().contains("2"))
            resource2 = icon;
        if(!productsAccount.getimages().contains("3"))
            resource3 = icon;
        CardHeader header = new CardHeader(getContext(),R.layout.gridviewitem_header);
        header.setButtonOverflowVisible(true);
        header.setTitle(productsAccount.getname());
        addCardHeader(header);
        GplayGridThumb thumbnail = new GplayGridThumb(getContext(),resource1);
        thumbnail.setExternalUsage(true);
        addCardThumbnail(thumbnail);
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent i = new Intent(context, CustomerProductInfo.class);
                i.putExtra("objectId",productsAccount.getObjectId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        init();
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        LikestextView = (TextView) view.findViewById(R.id.product_likes);
        Wishlist = (ImageView) view.findViewById(R.id.add_to_wishlist);
        basket = (ImageView) view.findViewById(R.id.add_to_basket);
        LikeImage= (ImageView) view.findViewById(R.id.likeimage);

        if(((CustomerBaseAcctivity)context).wishedDBhelper.Contain_Product(productsAccount.getObjectId()))
        {
            Wishlist.setTag(true);
            Wishlist.setImageResource(R.drawable.wishlist_filled);
        }
        else
        {
            Wishlist.setTag(false);
            Wishlist.setImageResource(R.drawable.wishlist_empty);
        }

        if(((CustomerBaseAcctivity)context).basketDBhelper.Contain_Product(productsAccount.getObjectId()))
        {
            basket.setTag(true);
            basket.setImageDrawable(context.getResources().getDrawable(R.drawable.carted));
        }
        else
        {
            basket.setTag(false);
            basket.setImageDrawable(context.getResources().getDrawable(R.drawable.cart));
        }

        if(((CustomerBaseAcctivity)context).likeDBhelper.Contain_Product(productsAccount.getObjectId()))
        {
            LikeImage.setTag(true);
            LikeImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.liked));
        }
        else
        {
            LikeImage.setTag(false);
            LikeImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.like));
        }

        LikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeOptionClicked((ImageView) view);
            }
        });
        Wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WishListoptionClicked((ImageView) view);
            }
        });
        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    basketOptionClicked((ImageView) view);
            }
        });
        productsAccount.setLikes(((CustomerBaseAcctivity)context).productDBhelper.GetLike(productsAccount.getObjectId()));
        LikestextView.setText(String.valueOf(productsAccount.getLikes()));
        TextView ProducctPrice = (TextView) view.findViewById(R.id.product_price);
        ProducctPrice.setText("Rs "+productsAccount.getPrice());

    }


    class GplayGridThumb extends CardThumbnail {

        private Bitmap bitmap;

        public GplayGridThumb(Context context, Bitmap resource1) {
            super(context);
            bitmap = resource1;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {
            ImageView iv = (ImageView)viewImage;
            iv.setImageBitmap(bitmap);
        }
    }

    public void LikeOptionClicked(final ImageView view) {
        final boolean isLiked = (boolean) (view).getTag();
        if(isLiked)
        {
            LikestextView.setText(String.valueOf(Integer.valueOf(LikestextView.getText().toString())-1));
            ((CustomerBaseAcctivity)context).productDBhelper.UpdateLike(productsAccount.getObjectId(),false);
            ((CustomerBaseAcctivity)context).likeDBhelper.deleteProduct(productsAccount.getObjectId());
            ( view).setImageResource(R.drawable.like);
        }
        else
        {
            LikestextView.setText(String.valueOf(Integer.valueOf(LikestextView.getText().toString())+1));
            ((CustomerBaseAcctivity)context).productDBhelper.UpdateLike(productsAccount.getObjectId(),true);
            ((CustomerBaseAcctivity)context).likeDBhelper.InsertProduct(productsAccount.getObjectId());
            (view).setImageResource(R.drawable.liked);
        }
        view.setTag(!isLiked);
        ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
        query.whereEqualTo("objectId", productsAccount.getObjectId());
        query.findInBackground(new FindCallback<ProductsAccount>() {
            @Override
            public void done(List<ProductsAccount> productsAccounts, ParseException e) {
                if (productsAccounts.size()==0||e != null) {
                    view.setTag(isLiked);
                    if(isLiked)
                    {

                        LikestextView.setText(String.valueOf(Integer.valueOf(LikestextView.getText().toString())+1));
                        ((CustomerBaseAcctivity)context).productDBhelper.UpdateLike(productsAccount.getObjectId(),true);
                        ((CustomerBaseAcctivity)context).likeDBhelper.InsertProduct(productsAccount.getObjectId());
                        (view).setImageResource(R.drawable.liked);
                    }
                    else
                    {
                        LikestextView.setText(String.valueOf(Integer.valueOf(LikestextView.getText().toString())-1));
                        ((CustomerBaseAcctivity)context).productDBhelper.UpdateLike(productsAccount.getObjectId(),false);
                        ((CustomerBaseAcctivity)context).likeDBhelper.deleteProduct(productsAccount.getObjectId());
                        (view).setImageResource(R.drawable.like);
                    }
                    ((CustomerBaseAcctivity)context).utilityFunction.ToastMessage(context.getResources().getString(R.string.networkerror));
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
                                LikestextView.setText(String.valueOf(Integer.valueOf(LikestextView.getText().toString())+1));
                                ((CustomerBaseAcctivity)context).productDBhelper.UpdateLike(productsAccount.getObjectId(),true);
                                ((CustomerBaseAcctivity)context).likeDBhelper.InsertProduct(productsAccount.getObjectId());
                                (view).setImageResource(R.drawable.liked);
                            }
                            else
                            {
                                LikestextView.setText(String.valueOf(Integer.valueOf(LikestextView.getText().toString())-1));
                                ((CustomerBaseAcctivity)context).productDBhelper.UpdateLike(productsAccount.getObjectId(),false);
                                ((CustomerBaseAcctivity)context).likeDBhelper.deleteProduct(productsAccount.getObjectId());
                                (view).setImageResource(R.drawable.like);
                            }
                            ((CustomerBaseAcctivity)context).utilityFunction.ToastMessage(context.getResources().getString(R.string.networkerror));
                        }
                    }
                });
            }
        });
    }

    private void WishListoptionClicked(final ImageView view) {
        boolean isWished = (boolean) (view).getTag();
        view.setTag(!isWished);
        if(isWished)
        {
            ((CustomerBaseAcctivity)context).wishedDBhelper.deleteProduct(productsAccount.getObjectId());
            (view).setImageResource(R.drawable.wishlist_empty);
        }
        else
        {
            ((CustomerBaseAcctivity)context).wishedDBhelper.InsertProduct(productsAccount.getObjectId());
            (view).setImageResource(R.drawable.wishlist_filled);
        }

        if (isWished) {
            ParseQuery<WishListAccount> query2 = ParseQuery.getQuery("WishList");
            query2.whereEqualTo("ObjectId", productsAccount.getObjectId());
            query2.whereEqualTo("User", ConfigData.currentuser);
            query2.findInBackground(new FindCallback<WishListAccount>() {
                @Override
                public void done(final List<WishListAccount> wishListAccounts, ParseException e) {
                    if(e!=null)
                    {
                        view.setTag(true);
                        ((CustomerBaseAcctivity)context).wishedDBhelper.InsertProduct(productsAccount.getObjectId());
                        (view).setImageResource(R.drawable.wishlist_filled);
                        ((CustomerBaseAcctivity)context).utilityFunction.ToastMessage(context.getResources().getString(R.string.networkerror));
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
                                ((CustomerBaseAcctivity)context).wishedDBhelper.InsertProduct(productsAccount.getObjectId());
                                ((CustomerBaseAcctivity)context).utilityFunction.ToastMessage(context.getResources().getString(R.string.networkerror));
                            }
                        }
                    });
                }
            });
        } else {
            WishListAccount basketAccount = new WishListAccount(productsAccount.getObjectId(),ConfigData.currentuser);
            basketAccount.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        view.setTag(false);
                        (view).setImageResource(R.drawable.wishlist_filled);
                        ((CustomerBaseAcctivity)context).wishedDBhelper.deleteProduct(productsAccount.getObjectId());
                        ((CustomerBaseAcctivity)context).utilityFunction.ToastMessage(context.getResources().getString(R.string.networkerror));
                    }
                }
            });
        }
    }

    public void basketOptionClicked(ImageView view) {
        boolean isBasket = (boolean) (view).getTag();
        view.setTag(!isBasket);
        if(isBasket)
        {
            ((CustomerBaseAcctivity)context).basketDBhelper.deleteProduct(productsAccount.getObjectId());
            (view).setImageResource(R.drawable.cart);
        }
        else
        {
            ((CustomerBaseAcctivity)context).basketDBhelper.InsertProduct(productsAccount.getObjectId());
            (view).setImageResource(R.drawable.carted);
        }
    }
}
