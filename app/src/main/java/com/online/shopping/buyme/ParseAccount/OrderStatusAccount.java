package com.online.shopping.buyme.ParseAccount;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("OrderStatus")
public class OrderStatusAccount extends ParseObject {
    public OrderStatusAccount()
    {

    }

    public OrderStatusAccount(String name, String number,String address,String Retailername, String Retailernumber,String Retaileraddress,List<String> products,String customernumber,String retailernumber) {
        this.setname(name);
        this.setNumber(number);
        this.setaddress(address);
        this.setRetailername(Retailername);
        this.setRetailerNumber(Retailernumber);
        this.setRetaileraddress(Retaileraddress);
        this.setProducts(products);
        this.setStatus("0");
        this.setCustomer(customernumber);
        this.setRetailer(retailernumber);
    }

    public void setStatus(String pending) {
        this.put("status",pending);
    }

    public String getStatus() {
        return this.getString("status");
    }
    public void setProducts(List<String> username)
    {
        this.put("User",username);
    }
    public List<String> getProducts()
    {
        return this.getList("User");
    }

    public void setname(String username)
    {
        this.put("CustomerName",username);
    }
    public String getname()
    {
        return this.getString("CustomerName");
    }
    public void setNumber(String username)
    {
        this.put("Customernumber",username);
    }
    public String getNumber()
    {
        return this.getString("Customernumber");
    }
    public void setaddress(String username)
    {
        this.put("CustomerAddress",username);
    }
    public String getaddress()
    {
        return this.getString("CustomerAddress");
    }

    public void setRetailername(String username)
    {
        this.put("RetailerName",username);
    }
    public String getRetailername()
    {
        return this.getString("RetailerName");
    }
    public void setRetailerNumber(String username)
    {
        this.put("Retailernumber",username);
    }
    public String getRetailerNumber()
    {
        return this.getString("Retailernumber");
    }
    public void setRetaileraddress(String username)
    {
        this.put("RetailerAddress",username);
    }
    public String getRetaileraddress()
    {
        return this.getString("RetailerAddress");
    }

    public void setCustomer(String username)
    {
        this.put("customeruser",username);
    }
    public String getCustomer()
    {
        return this.getString("customeruser");
    }
    public void setRetailer(String username)
    {
        this.put("retaileruser",username);
    }
    public String getRetailer()
    {
        return this.getString("retaileruser");
    }

}

