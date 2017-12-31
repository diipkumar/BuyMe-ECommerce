package com.online.shopping.buyme.Extras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.online.shopping.buyme.R;
import java.util.List;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class HorizontalAdapter extends ArrayAdapter<SimpleProductCard> {

    public List<SimpleProductCard> mItems;
    LayoutInflater mInflater;

    public HorizontalAdapter(Context context, List<SimpleProductCard> objects) {
        super( context, R.layout.hlistviewitem, objects );
        mInflater = LayoutInflater.from( context );
        mItems = objects;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
            convertView = mInflater.inflate(R.layout.hlistviewitem, parent, false);
        ((CardViewNative)convertView.findViewById(R.id.list_cardId)).setCard(mItems.get(position));
        return convertView;
    }
}