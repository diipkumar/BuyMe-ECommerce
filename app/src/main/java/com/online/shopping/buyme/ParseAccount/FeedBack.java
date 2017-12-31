package com.online.shopping.buyme.ParseAccount;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Feedback")
public class FeedBack extends ParseObject {
    public FeedBack()
    {
    }
    public FeedBack(String message) {
        this.setMessage(message);
    }

    public void setMessage(String username)
    {
        this.put("Message",username);
    }
    public String getMessage()
    {
        return this.getString("Message");
    }

}

