package com.online.shopping.buyme.ParseAccount;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("WishList")
public class WishListAccount extends ParseObject {
    public WishListAccount()
    {
    }

    public WishListAccount(String objecctid, String User) {
        this.setUser(User);
        this.setObject(objecctid);
    }

    public void setObject(String username)
    {
        this.put("ObjectId",username);
    }
    public String getObject()
    {
        return this.getString("ObjectId");
    }

    public void setUser(String username)
    {
        this.put("User",username);
    }
    public String getUser()
    {
        return this.getString("User");
    }
}

