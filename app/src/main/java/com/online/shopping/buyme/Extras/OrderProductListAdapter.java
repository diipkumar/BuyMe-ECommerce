package com.online.shopping.buyme.Extras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.online.shopping.buyme.CustomerExtras.CustomerProductCard;
import com.online.shopping.buyme.R;
import java.util.List;

public class OrderProductListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
private LayoutInflater inflater;
    public Context mcontext;
    private List<CustomerProductCard> items;
    private List<String> quantity;

    public OrderProductListAdapter(Context context) {
    mcontext = context;
    inflater = LayoutInflater.from(context);
        }

public void setData(List<String> quantity,List<CustomerProductCard> items) {
        this.items = items;
        this.quantity = quantity;
        }

@Override
public CustomerProductCard getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition);
        }

@Override
public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
        }

@Override
public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;}

@Override
public int getRealChildrenCount(int groupPosition) {
        return 0;
        }

@Override
public CustomerProductCard getGroup(int groupPosition) {
        return items.get(groupPosition);
        }

@Override
public int getGroupCount() {
        return items.size();
        }

@Override
public long getGroupId(int groupPosition) {
        return groupPosition;
        }

@Override
public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
        convertView = inflater.inflate(R.layout.product_list, parent, false);
        }
    CustomerProductCard currentproduct = items.get(groupPosition);
    TextView name,price,quantity;
    name = (TextView)convertView.findViewById(R.id.productlistname);
    price = (TextView)convertView.findViewById(R.id.productlistprice);
    quantity= (TextView)convertView.findViewById(R.id.quantity);
    name.setText(currentproduct.productsAccount.getname());
    price.setText(currentproduct.productsAccount.getPrice());
    quantity.setText(this.quantity.get(groupPosition));
    ImageView image = (ImageView)convertView.findViewById(R.id.productlistimage);
    image.setImageBitmap(currentproduct.resource1);
    //qty remaining
    return convertView;
}

    @Override
public boolean hasStableIds() {
        return true;
        }

@Override
public boolean isChildSelectable(int arg0, int arg1) {
        return true;
        }
        }