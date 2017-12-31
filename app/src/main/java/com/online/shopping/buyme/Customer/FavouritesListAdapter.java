package com.online.shopping.buyme.Customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.online.shopping.buyme.ParseAccount.FavouritesAccount;
import com.online.shopping.buyme.R;

import java.util.List;

public class FavouritesListAdapter extends BaseAdapter {
    List<FavouritesAccount> mItems;
    Context context;
    private static LayoutInflater inflater=null;

    public FavouritesListAdapter(Context mainActivity, List<FavouritesAccount> mItems) {
        context=mainActivity;
        this.mItems = mItems;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView;
        rowView = inflater.inflate(R.layout.favourites_listitem, null);
        ((TextView)rowView.findViewById(R.id.name_Favourites)).setText(mItems.get(position).getRetailerName());
        rowView.findViewById(R.id.remove_Favourites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(context.getResources().getString(R.string.favourites_delete));
                alertDialog.setMessage(context.getResources().getString(R.string.favourites_delete_question));
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mItems.get(position).deleteInBackground();
                        mItems.remove(position);
                        notifyDataSetChanged();
                }});
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        return rowView;
    }


}
