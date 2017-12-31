package com.online.shopping.buyme.CustomerExtras;

import android.widget.ImageView;

public interface WishedInterFace {
    void WishedCardClicked(CustomerWishedCard onCardClickListener);
    void LikeOptionClicked(CustomerWishedCard cardPointer, ImageView view);

    void WishedBasketOptionClicked(CustomerWishedCard cardPointer, ImageView view);

    void WishedSwiped(CustomerWishedCard card);

    void WishedUndoOption(CustomerWishedCard cardpointer);
}
