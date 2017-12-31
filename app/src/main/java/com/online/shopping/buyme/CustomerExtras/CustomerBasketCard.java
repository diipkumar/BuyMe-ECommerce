package com.online.shopping.buyme.CustomerExtras;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.online.shopping.buyme.R;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

public class CustomerBasketCard extends Card {
    private ImageView basketlike;
    private Context context;
    public int quantitynumber;
    public final CustomerProductCard card;
    ImageView add_to_another_list;
    TextView basketProductname;
    private boolean isGrey;

    public CustomerBasketCard(Context context, CustomerProductCard customerProductCard, int quantity) {
        super(context, R.layout.basket_item);
        this.context =context;
        this.quantitynumber = quantity;
        this.card = customerProductCard;
        isGrey = false;
        init();
    }

    public void init() {
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card acard, View view) {
                ((BasketInterFace) context).BasketCardClicked(CustomerBasketCard.this);
            }
        });
        setSwipeable(true);
        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                ((BasketInterFace)context).BasketSwiped(CustomerBasketCard.this);
            }
        });
        setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
            @Override
            public void onUndoSwipe(Card card) {
                ((BasketInterFace)context).BasketUndoOption(CustomerBasketCard.this);
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
        final Spinner quantity = (Spinner)view.findViewById(R.id.qunatityspinner);
        List<String> list = new ArrayList<>();
        for(int i=0;i<10;i++)
            list.add(String.valueOf(i+1));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantity.setAdapter(dataAdapter);
        quantity.setSelection(quantitynumber-1);
        final TextView basketProductprice = (TextView) view.findViewById(R.id.basket_product_price);
        quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                quantitynumber = i+1;
                basketProductprice.setText("Rs "+String.valueOf(Integer.valueOf(card.productsAccount.getPrice())*quantitynumber));
                ((BasketInterFace) context).quantitySelected(CustomerBasketCard.this);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                ((BasketInterFace) context).BasketWishedOptionClicked(CustomerBasketCard.this, (ImageView) view);
            }
        });

        basketProductname = (TextView) view.findViewById(R.id.basket_product_name);
        if(isGrey)
        basketProductname.setTextColor(context.getResources().getColor(R.color.red));
        basketProductname.setText(card.productsAccount.getname());
        basketProductprice.setText("Rs "+String.valueOf(Integer.valueOf(card.productsAccount.getPrice())*quantitynumber));
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
                ((BasketInterFace)context).LikeOptionClicked(CustomerBasketCard.this,(ImageView)view);
            }
        });
    }

    public void setBackGroundGrey() {
        isGrey = true;
    }
}
