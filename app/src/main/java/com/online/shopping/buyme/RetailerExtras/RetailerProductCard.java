package com.online.shopping.buyme.RetailerExtras;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

public class RetailerProductCard extends Card {

    public ProductsAccount productsAccount;
    private RetailerProductCard CardPointer;
    private Context context;
    public Bitmap resource1,resource2,resource3;
    public ImageView basket;
    public ImageView LikeImage;
    public TextView LikestextView;
    public ImageView Wishlist;

    public RetailerProductCard(Context context,Bitmap imgDecodableString1, Bitmap imgDecodableString2, Bitmap imgDecodableString3,ProductsAccount productsAccount) {
        super(context, R.layout.gridviewitem_lowercontent);
        this.context = context;
        this.productsAccount = productsAccount;
        resource1 = imgDecodableString1;
        resource2 = imgDecodableString2;
        resource3 = imgDecodableString3;
        CardPointer = this;
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
        CardPointer = this;
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                ((RetailerAdapterInterFace) context).cardClicked(CardPointer);
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
        LikeImage.setImageResource(R.drawable.liked);
        basket.setImageResource(R.drawable.edit);
        Wishlist.setVisibility(View.INVISIBLE);
        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    ((RetailerAdapterInterFace)context).EditOptionClicked(CardPointer);
            }
        });
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
}
