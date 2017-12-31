package com.online.shopping.buyme.ParseAccount;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Products")
public class ProductsAccount extends ParseObject {
    public String updateTime;

    public ProductsAccount()
    {
    }
    public ProductsAccount(String name, String price,String Description,ParseFile p1,ParseFile p2,ParseFile p3,String images,String mobile,String Producttype,ParseGeoPoint geoPoint) {
        this.setUser(mobile);
        this.setimages(images);
        this.setname(name);
        this.setPrice(price);
        this.setDescription(Description);
        if(p1!=null)
        this.setParse1(p1);
        if(p2!=null)
            this.setParse2(p2);
        if(p3!=null)
            this.setParse3(p3);
        if(geoPoint!=null)
        this.setLocation(geoPoint);
        this.setType(Producttype);
        this.setLikes(0);
    }

    public void setLocation(ParseGeoPoint i) {
        this.put("Location",i);
    }

    public void setLikes(int i) {
        this.put("Like",i);
    }

    public int getLikes() {
        return this.getInt("Like");
    }
    public void setType(String username)
    {
        this.put("Type",username);
    }
    public String getType()
    {
        return this.getString("Type");
    }

    public void setname(String username)
    {
        this.put("Name",username);
    }
    public String getname()
    {
        return this.getString("Name");
    }

    public void setUser(String username)
    {
        this.put("User",username);
    }
    public String getUser()
    {
        return this.getString("User");
    }
    public void setimages(String username)
    {
        this.put("setimages",username);
    }
    public String getimages()
    {
        return this.getString("setimages");
    }

    public void setPrice(String username)
    {
        this.put("Price",Integer.valueOf(username));
    }
    public String getPrice()
    {
        return String.valueOf(getInt("Price"));
    }
    public void setDescription(String username)
    {
        this.put("Description",username);
    }
    public String getDescription()
    {
        return this.getString("Description");
    }
    public void setParse1(ParseFile p1)
    {
        this.put("Image1",p1);
    }
    public ParseFile getParse1()
    {
        return this.getParseFile("Image1");
    }
    public void setParse2(ParseFile p1)
    {
        this.put("Image2",p1);
    }
    public ParseFile getParse2()
    {
        return this.getParseFile("Image2");
    }
    public void setParse3(ParseFile p1)
    {
        this.put("Image3",p1);
    }
    public ParseFile getParse3()
    {
        return this.getParseFile("Image3");
    }
}

