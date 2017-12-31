package com.online.shopping.buyme.Extras;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.online.shopping.buyme.CustomerExtras.SimpleCardInterFace;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

public class SimpleProductCard extends Card {
    private Context context;
    public Bitmap resource1,resource2,resource3;
    public int AlreadyLoaded = 0;
    public ProductsAccount productsAccount;

    public SimpleProductCard(Context context, Bitmap imgDecodableString1, Bitmap imgDecodableString2, Bitmap imgDecodableString3,ProductsAccount productsAccount) {
        super(context, R.layout.simplegridviewitem_lowercontent);
        this.context = context;
        resource1 = imgDecodableString1;
        resource2 = imgDecodableString2;
        resource3 = imgDecodableString3;
        this.productsAccount = productsAccount;
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
        GplayGridThumb thumbnail = new GplayGridThumb(getContext(),resource1);
        thumbnail.setExternalUsage(true);
        addCardThumbnail(thumbnail);
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                ((SimpleCardInterFace)context).cardClicked(SimpleProductCard.this);
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView ProducctPrice = (TextView) view.findViewById(R.id.product_price);
        ProducctPrice.setText("Rs "+productsAccount.getPrice());
        TextView Producctname = (TextView) view.findViewById(R.id.product_name);
        Producctname.setText(productsAccount.getname());
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
