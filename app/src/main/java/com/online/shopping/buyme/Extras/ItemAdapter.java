package com.online.shopping.buyme.Extras;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.shopping.buyme.Customer.Contact;
import com.online.shopping.buyme.R;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Contact> {
    List<Contact>   data;
    Context context;
    int layoutResID;

    public ItemAdapter(Context context, int layoutResourceId,List<Contact> data) {
        super(context, layoutResourceId, data);

        this.data=data;
        this.context=context;
        this.layoutResID=layoutResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsHolder holder;
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResID, parent, false);
            holder = new NewsHolder();
            holder.itemName = (TextView)row.findViewById(R.id.row_name);
            holder.icon=(ImageView)row.findViewById(R.id.row_image);
            row.setTag(holder);
        }
        else
        {
            holder = (NewsHolder)row.getTag();
        }
        final Contact itemdata = data.get(position);
        holder.itemName.setText(itemdata.Name);
        holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.circle));
        return row;
    }

    static class NewsHolder{
        TextView itemName;
        ImageView icon;
    }
}