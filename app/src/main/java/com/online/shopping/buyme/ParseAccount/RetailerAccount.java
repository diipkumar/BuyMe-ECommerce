package com.online.shopping.buyme.ParseAccount;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("RetailerAccount")
public class RetailerAccount extends ParseObject {
    public RetailerAccount()
    {
    }

    public RetailerAccount(String name, String password) {
        this.setUsername(name);
        this.setpassword(password);
    }

    public void setUsername(String username)
    {
        this.put("Username",username);
    }

    public String getUsername()
    {
        return this.getString("Username");
    }

    public void setpassword(String username)
    {
        this.put("password",username);
    }

    public String getpassword()
    {
        return this.getString("password");
    }
}
