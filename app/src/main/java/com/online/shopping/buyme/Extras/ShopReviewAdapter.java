package com.online.shopping.buyme.Extras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.online.shopping.buyme.ParseAccount.RatingReviews;
import com.online.shopping.buyme.R;

import java.util.List;

public class ShopReviewAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
private LayoutInflater inflater;
    public Context mcontext;
    private List<RatingReviews> items;

public ShopReviewAdapter(Context context) {
    mcontext = context;
    inflater = LayoutInflater.from(context);
        }

public void setData(List<RatingReviews> items) {
        this.items = items;
        }

@Override
public RatingReviews getChild(int groupPosition, int childPosition) {
        return items.get(childPosition);
        }

@Override
public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
        }

@Override
public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    if (convertView == null) {
        convertView = inflater.inflate(R.layout.user_reviews, parent, false);
    }
    TextView naame,comment;
    ImageView star1,star2,star3,star4,star5;
    star1 = (ImageView)convertView.findViewById(R.id.reviewimage1);
    star2 = (ImageView)convertView.findViewById(R.id.reviewimage2);
    star3 = (ImageView)convertView.findViewById(R.id.reviewimage3);
    star4 = (ImageView)convertView.findViewById(R.id.reviewimage4);
    star5 = (ImageView)convertView.findViewById(R.id.reviewimage5);
    naame = (TextView)convertView.findViewById(R.id.review_username);
    comment = (TextView)convertView.findViewById(R.id.reviewcomment);
    RatingReviews ratingReviews = items.get(childPosition);
    naame.setText(ratingReviews.getUser());
    comment.setText(ratingReviews.getcomment());
    switch (ratingReviews.getStar().length())
    {
        case 5:
            star5.setImageResource(R.drawable.star_filled);
        case 4:
            star4.setImageResource(R.drawable.star_filled);
        case 3:
            star3.setImageResource(R.drawable.star_filled);
        case 2:
            star2.setImageResource(R.drawable.star_filled);
        case 1:
            star1.setImageResource(R.drawable.star_filled);
    }
    return convertView;

}

@Override
public int getRealChildrenCount(int groupPosition) {
        return items.size();
        }

@Override
public List<RatingReviews> getGroup(int groupPosition) {
        return items;
        }

@Override
public int getGroupCount() {
        return 1;
        }

@Override
public long getGroupId(int groupPosition) {
        return groupPosition;
        }

@Override
public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    if (convertView == null) {
        convertView = inflater.inflate(R.layout.group_item, parent, false);
    }
    ((TextView)convertView.findViewById(R.id.textTitle)).setText("Users Reviews");
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