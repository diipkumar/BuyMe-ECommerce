package com.online.shopping.buyme.CustomerExtras;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.shopping.buyme.R;

import it.gmariotti.cardslib.library.internal.Card;

public class CustomerWishedCard extends Card {
    public boolean add_to_basket;
    private ImageView basketlike;
    private Context context;
    public final CustomerProductCard card;
    ImageView add_to_another_list;

    public CustomerWishedCard(Context context, CustomerProductCard customerProductCard) {
        super(context, R.layout.basketcopy_item);
        this.context =context;
        this.card = customerProductCard;
        init();
    }

    public void init() {
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card acard, View view) {
                ((WishedInterFace) context).WishedCardClicked(CustomerWishedCard.this);
            }
        });
        setSwipeable(true);
        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                ((WishedInterFace)context).WishedSwiped(CustomerWishedCard.this);
            }
        });
        setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
            @Override
            public void onUndoSwipe(Card card) {
                ((WishedInterFace)context).WishedUndoOption(CustomerWishedCard.this);
            }
        });
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

        BasketDBhelper basketDBhelper = new BasketDBhelper(context);
        if(basketDBhelper.Contain_Product(card.productsAccount.getObjectId()))
        {
            add_to_another_list.setTag(true);
            add_to_another_list.setImageResource(R.drawable.carted);
        }
        else
        {
            add_to_another_list.setTag(false);
            add_to_another_list.setImageResource(R.drawable.cart);
        }

        add_to_another_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag()==null)
                    return;
                ((WishedInterFace) context).WishedBasketOptionClicked(CustomerWishedCard.this, (ImageView) view);
            }
        });

        TextView basketProductname = (TextView) view.findViewById(R.id.basket_product_name);
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
                ((WishedInterFace)context).LikeOptionClicked(CustomerWishedCard.this,(ImageView)view);
            }
        });
    }


}
