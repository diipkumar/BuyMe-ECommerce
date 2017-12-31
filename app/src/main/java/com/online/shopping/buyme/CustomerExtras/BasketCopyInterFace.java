package com.online.shopping.buyme.CustomerExtras;

import android.widget.ImageView;

public interface BasketCopyInterFace {
    void BasketCardClicked(CustomerBasketCopyCard onCardClickListener);
    void LikeOptionClicked(CustomerBasketCopyCard cardPointer, ImageView view);

    void BasketWishedOptionClicked(CustomerBasketCopyCard cardPointer, ImageView view);

    void BasketSwiped(CustomerBasketCopyCard card);

    void BasketUndoOption(CustomerBasketCopyCard cardpointer);
}
