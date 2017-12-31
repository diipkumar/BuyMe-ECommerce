package com.online.shopping.buyme.Retailer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.online.shopping.buyme.Extras.AnimatedExpandableListView;
import com.online.shopping.buyme.R;
import java.util.ArrayList;
import java.util.List;

public class DrawerListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
private LayoutInflater inflater;
    public Context mcontext;
    public static class GroupItem {
        public String title;
        public List<ChildItem> items = new ArrayList<>();
    }

    public static class ChildItem {
        public String title;
    }

    public static class ChildHolder {
        TextView title;
    }

    public static class GroupHolder {
        TextView title;
    }

    private List<GroupItem> items;

public DrawerListAdapter(Context context) {
    mcontext = context;
    inflater = LayoutInflater.from(context);
}

public void setData(List<GroupItem> items) {
        this.items = items;
        }

@Override
public ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
        }

@Override
public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
        }

@Override
public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder holder;
        ChildItem item = getChild(groupPosition, childPosition);
        holder = new ChildHolder();
        convertView = inflater.inflate(R.layout.list_item, parent, false);
        holder.title = (TextView) convertView.findViewById(R.id.textTitle);
        if(item.title.equals("Mens")||item.title.equals("Womens")||item.title.equals("Kids"))
        {
            holder.title.setTypeface(null, Typeface.BOLD);
            holder.title.setTextColor(mcontext.getResources().getColor(R.color.blue));
        }
        holder.title.setText(item.title);
        return convertView;
}

@Override
public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
        }

@Override
public GroupItem getGroup(int groupPosition) {
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
        GroupHolder holder;
        GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
        holder = new GroupHolder();
        convertView = inflater.inflate(R.layout.group_item, parent, false);
        holder.title = (TextView) convertView.findViewById(R.id.textTitle);
        convertView.setTag(holder);
        } else {
        holder = (GroupHolder) convertView.getTag();
        }
        holder.title.setText(item.title);
    ((ImageView)convertView.findViewById(R.id.expand_arrow)).setImageResource(R.drawable.expand_icon);

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