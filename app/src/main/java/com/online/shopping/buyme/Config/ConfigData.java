package com.online.shopping.buyme.Config;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.online.shopping.buyme.Utility.CommonFunction;
import com.online.shopping.buyme.common.CustomeActivity;

public class ConfigData {
    public static boolean BasketChecked,WishListchecked;
    public static CommonFunction commonFunction ;
    public static boolean isCustomer = true;
    public static String currentuser;
    public static ParseGeoPoint location;
    public static String DrawerHeading[]={"Electronics","Fashion And Styles","Home Appliances","Food And Groceries","Home Furnishing","Gift Articles","Others"};
    public static String DrawerChild[][]={{"Mobiles","Mobile Accesores","Laptop Accesories","Cases and Covers","Personal Applicances","Home Applicances","Others"},
            {"Mens","Shirt","T Shirts","Jackets","Glasses","Formal Shoes","Casual Shoes","Sandals","Slippers","Watches","Belts","Wallets","Perfumes","Others","Womens","Tops","Pants","Kurtis","Dresses","Sarees","Shoes","Sandals","Cosmetics","Glasses","Watches","Designer Wears","Bags","Others","Kids","Shirts","T Shirts","Pants","New Borns","Gift Sets","Toys","Others"},{"Bathroom Products","Kitchen Products","Cleaning Products","Others"},{"Cakes","Groceries","Others"},{"Bed Sheets","Sofa Covers","Curtains","Door mats","Carpets","Furniture","Others"},{"ShowPiece","Mugs","Frames","Statues","Cards","Chocolates","Pens","Others"},{"others"}};
    public static LatLng signuplatlng;
    public static String search_query;
    public static boolean is_Search;
    public static String SharedProductObjectID;
    public static String retailerspinner[] = {"Pending","Confirmed","Delievered","Cancelled"};
    public static boolean HomeChecked;
    public static CustomeActivity currentActivity;
    public static int filter_dist;
    public static boolean filter_cod;
    public static int filter_price_high,filter_price_low;
    public static Bitmap ProductImageView;
}
