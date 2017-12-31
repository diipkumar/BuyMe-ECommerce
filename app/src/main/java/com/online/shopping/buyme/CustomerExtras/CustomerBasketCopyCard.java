package com.online.shopping.buyme.CustomerExtras;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.shopping.buyme.R;

import it.gmariotti.cardslib.library.internal.Card;

public class CustomerBasketCopyCard extends Card {
    private ImageView basketlike;
    private Context context;
    public final CustomerProductCard card;
    ImageView add_to_another_list;
    TextView basketProductname;
    private boolean isGrey;

    public CustomerBasketCopyCard(Context context, CustomerProductCard customerProductCard) {
        super(context, R.layout.basketcopy_item);
        this.context =context;
        this.card = customerProductCard;
        isGrey = false;
        init();
    }

    public void init() {
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card acard, View view) {
                ((BasketCopyInterFace) context).BasketCardClicked(CustomerBasketCopyCard.this);
            }
        });
        setSwipeable(false);
     }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ImageView shareProduct = (ImageView)view.findViewById(R.id.productshare);
        shareProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = context.getResources().getString(R.string.ProductShareMessage) + "http://"+context.getResources().getString(R.string.AppProductShareLink) +"/"+card.productsAccount.getObjectId();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        ImageView basketimage = (ImageView) view.findViewById(R.id.basketimage);
        basketimage.setImageBitmap(card.resource1);
        add_to_another_list = (ImageView)view.findViewById(R.id.add_to_another_list);

        WishedDBhelper wishedDBhelper = new WishedDBhelper(context);
        if(wishedDBhelper.Contain_Product(card.productsAccount.getObjectId()))
        {
            add_to_another_list.setTag(true);
            add_to_another_list.setImageResource(R.drawable.wishlist_filled);
        }
        else
        {
            add_to_another_list.setTag(false);
            add_to_another_list.setImageResource(R.drawable.wishlist_empty);
        }

        add_to_another_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag()==null)
                    return;
                ((BasketCopyInterFace) context).BasketWishedOptionClicked(CustomerBasketCopyCard.this, (ImageView) view);
            }
        });

        basketProductname = (TextView) view.findViewById(R.id.basket_product_name);
        if(isGrey)
        basketProductname.setTextColor(context.getResources().getColor(R.color.grey));
        TextView basketProductprice = (TextView) view.findViewById(R.id.basket_product_price);
        basketProductname.setText(card.productsAccount.getname());
        basketProductprice.setText("Rs "+card.productsAccount.getPrice());
        basketlike = (ImageView)view.findViewById(R.id.basket_like);
        LikeDBhelper dBhelper = new LikeDBhelper(context);
        LikesUpdate(dBhelper.Contain_Product(card.productsAccount.getObjectId()));
    }

    public void LikesUpdate(boolean isLiked) {
        basketlike.setVisibility(View.VISIBLE);
        basketlike.setTag(isLiked);
        if(isLiked)
        {
            basketlike.setImageResource(R.drawable.liked);
        }
        else
        {
            basketlike.setImageResource(R.drawable.like);
        }
        basketlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag()==null)
                    return;
                ((BasketCopyInterFace)context).LikeOptionClicked(CustomerBasketCopyCard.this,(ImageView)view);
            }
        });
    }

}
