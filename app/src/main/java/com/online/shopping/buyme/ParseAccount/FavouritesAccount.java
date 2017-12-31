package com.online.shopping.buyme.ParseAccount;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Favourites")
public class FavouritesAccount extends ParseObject {
    public FavouritesAccount()
    {
    }
    public FavouritesAccount(String retailer, String customer, String retailername) {
        this.setCustomer(customer);
        this.setRetailer(retailer);
        this.setRetailerName(retailername);
    }

    public void setRetailerName(String username)
    {
        this.put("RetailerName",username);
    }
    public String getRetailerName()
    {
        return this.getString("RetailerName");
    }


    public void setRetailer(String username)
    {
        this.put("Retailer",username);
    }
    public String getRetailer()
    {
        return this.getString("Retailer");
    }

    public void setCustomer(String username)
    {
        this.put("Customer",username);
    }
    public String getCustomer()
    {
        return this.getString("Customer");
    }
}

