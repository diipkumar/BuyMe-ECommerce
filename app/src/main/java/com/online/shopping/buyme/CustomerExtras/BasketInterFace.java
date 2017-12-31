package com.online.shopping.buyme.CustomerExtras;

import android.widget.ImageView;

public interface BasketInterFace {
    void BasketCardClicked(CustomerBasketCard onCardClickListener);
    void LikeOptionClicked(CustomerBasketCard cardPointer, ImageView view);

    void BasketWishedOptionClicked(CustomerBasketCard cardPointer, ImageView view);

    void BasketSwiped(CustomerBasketCard card);

    void BasketUndoOption(CustomerBasketCard cardpointer);

    void quantitySelected(CustomerBasketCard customerBasketCard);
}
