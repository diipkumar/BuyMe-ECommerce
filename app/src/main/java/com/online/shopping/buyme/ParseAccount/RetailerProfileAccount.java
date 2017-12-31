package com.online.shopping.buyme.ParseAccount;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("RetailerProfile")
public class RetailerProfileAccount extends ParseObject {
    public RetailerProfileAccount()
    {
    }


    public void setCashOnDelivery(boolean username)
    {
        this.put("CashOnDelivery",username);
    }

    public boolean getCashOnDelivery()
    {
        return this.getBoolean("CashOnDelivery");
    }

    public void setStar(int username)
    {
        this.put("Stars",username);
    }

    public int getStar()
    {
        return this.getInt("Stars");
    }

    public void setRated(int username)
    {
        this.put("Total_Rate",username);
    }

    public int getRated()
    {
        return this.getInt("Total_Rate");
    }

    public void setUser(String username)
    {
        this.put("User",username);
    }

    public String getUser()
    {
        return this.getString("User");
    }

    public void set_shop_name(String username)
    {
        this.put("Shop_Name",username);
    }

    public String get_shop_name()
    {
      return this.getString("Shop_Name");
    }

    public void set_Description(String username)
    {
        this.put("Desccription",username);
    }

    public String get_Description()
    {
        return this.getString("Desccription");
    }


    public void setLocation(ParseGeoPoint a)
    {
        this.put("Location",a);
    }

    public ParseGeoPoint getlocation()
    {
        return this.getParseGeoPoint("Location");
    }

    public void setPhoneNumber(String username)
    {
        this.put("Phone_Number",username);
    }

    public String getPhoneNumber()
    {
        return this.getString("Phone_Number");
    }

    public void setAddress(String username)
    {
        this.put("Address",username);
    }

    public String getAddress()
    {
        return this.getString("Address");
    }

    public void setMailId(String username)
    {
        this.put("Mail_ID",username);
    }

    public String getMailId()
    {
        return this.getString("Mail_ID");
    }

    public void setWebsite(String username)
    {
        this.put("Website",username);
    }

    public String getWebsite()
    {
        return this.getString("Website");
    }
}

