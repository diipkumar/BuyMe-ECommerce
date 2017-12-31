package com.online.shopping.buyme.ParseAccount;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Rating")
public class RatingReviews extends ParseObject {
    public RatingReviews()
    {
    }

    public RatingReviews(String name, String star,String comment,String retailer) {
        this.setUser(name);
        this.setcomment(comment);
        this.setStar(star);
        this.setRetailer(retailer);

    }

    public void setRetailer(String username)
    {
        this.put("Retailer",username);
    }
    public String getRetailer()
    {
        return this.getString("Retailer");
    }

    public void setStar(String username)
    {
        this.put("Star",username);
    }
    public String getStar()
    {
        return this.getString("Star");
    }

    public void setUser(String username)
    {
        this.put("User",username);
    }
    public String getUser()
    {
        return this.getString("User");
    }

    public void setcomment(String username)
    {
        this.put("Comment",username);
    }
    public String getcomment()
    {
        return this.getString("Comment");
    }
}

