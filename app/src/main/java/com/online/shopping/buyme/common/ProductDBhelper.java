package com.online.shopping.buyme.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.online.shopping.buyme.CustomerExtras.CustomerProductCard;
import com.online.shopping.buyme.Extras.SimpleProductCard;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.RetailerExtras.RetailerProductCard;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDBhelper extends SQLiteOpenHelper {

    public Context context;
    public final static String DATABASE_NAME = "Product.db";
    public final String CONTACTS_TABLE_NAME = "Products";
    public final String CONTACTS_COLUMN_User_NAME = "username";
    public final String CONTACTS_COLUMN_NAME = "products";
    public final String CONTACTS_COLUMN_IMAGECOUNT = "imagescount";
    public final String CONTACTS_COLUMN_PRICE = "price";
    public final String CONTACTS_COLUMN_DESCRIPTION = "desccription";
    public final String CONTACTS_COLUMN_IMAGE1 = "image1";
    public final String CONTACTS_COLUMN_IMAGE2 = "image2";
    public final String CONTACTS_COLUMN_IMAGE3 = "image3";
    public final String CONTACTS_COLUMN_UPDATED_TIME = "upadtedtime";
    public final String CONTACTS_COLUMN_TYPE = "type";
    public final String CONTACTS_COLUMN_LIKES = "likes";
    public final String CONTACTS_COLUMN_OBJECT = "objectid";
    public final String CONTACTS_COLUMN_Parse1 = "Parseurl1";
    public final String CONTACTS_COLUMN_Parse2= "Parseurl2";
    public final String CONTACTS_COLUMN_Parse3 = "Parseurl3";
    public final String CONTACTS_COLUMN_ALL_LOADED = "AllimagesLoaded";

    public ProductDBhelper (Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String query ="create table "+CONTACTS_TABLE_NAME +"(";
        query+=CONTACTS_COLUMN_NAME+" text,";
        query+=CONTACTS_COLUMN_User_NAME+" text,";
        query+=CONTACTS_COLUMN_IMAGECOUNT+" text,";
        query+=CONTACTS_COLUMN_PRICE+" text,";
        query+=CONTACTS_COLUMN_DESCRIPTION+" text,";
        query+=CONTACTS_COLUMN_IMAGE1+" blob,";
        query+=CONTACTS_COLUMN_IMAGE2+" blob,";
        query+=CONTACTS_COLUMN_IMAGE3+" blob,";
        query+=CONTACTS_COLUMN_UPDATED_TIME+" text,";
        query+=CONTACTS_COLUMN_TYPE+" text,";
        query+=CONTACTS_COLUMN_OBJECT+" text,";
        query+=CONTACTS_COLUMN_ALL_LOADED+" text,";
        query+=CONTACTS_COLUMN_Parse1+" text,";
        query+=CONTACTS_COLUMN_Parse2+" text,";
        query+=CONTACTS_COLUMN_Parse3+" text,";
        query+=CONTACTS_COLUMN_LIKES+" text";
        query+=")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public void deleteProduct(String objectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CONTACTS_TABLE_NAME + " where " + CONTACTS_COLUMN_OBJECT + "='" + objectId + "'");
    }

    public boolean InsertProduct(RetailerProductCard retailerProductCard)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, retailerProductCard.productsAccount.getname());
        contentValues.put(CONTACTS_COLUMN_User_NAME, retailerProductCard.productsAccount.getUser());
        contentValues.put(CONTACTS_COLUMN_IMAGECOUNT, retailerProductCard.productsAccount.getimages());
        contentValues.put(CONTACTS_COLUMN_PRICE, retailerProductCard.productsAccount.getPrice());
        contentValues.put(CONTACTS_COLUMN_DESCRIPTION, retailerProductCard.productsAccount.getDescription());
        if(retailerProductCard.productsAccount.getimages().contains("1"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse1,retailerProductCard.productsAccount.getParse1().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE1, getByteArray(retailerProductCard.resource1));
        }
        if(retailerProductCard.productsAccount.getimages().contains("2"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse2,retailerProductCard.productsAccount.getParse2().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE2, getByteArray(retailerProductCard.resource2));
        }
        if(retailerProductCard.productsAccount.getimages().contains("3"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse3,retailerProductCard.productsAccount.getParse3().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE3, getByteArray(retailerProductCard.resource3));
        }
        contentValues.put(CONTACTS_COLUMN_UPDATED_TIME, retailerProductCard.productsAccount.getUpdatedAt().toString());
        contentValues.put(CONTACTS_COLUMN_TYPE, retailerProductCard.productsAccount.getType());
        contentValues.put(CONTACTS_COLUMN_LIKES, retailerProductCard.productsAccount.getLikes());
        contentValues.put(CONTACTS_COLUMN_OBJECT, retailerProductCard.productsAccount.getObjectId());
        contentValues.put(CONTACTS_COLUMN_ALL_LOADED,"1");
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean InsertProduct(CustomerProductCard retailerProductCard)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, retailerProductCard.productsAccount.getname());
        contentValues.put(CONTACTS_COLUMN_User_NAME, retailerProductCard.productsAccount.getUser());
        contentValues.put(CONTACTS_COLUMN_IMAGECOUNT, retailerProductCard.productsAccount.getimages());
        contentValues.put(CONTACTS_COLUMN_PRICE, retailerProductCard.productsAccount.getPrice());
        contentValues.put(CONTACTS_COLUMN_DESCRIPTION, retailerProductCard.productsAccount.getDescription());
        if(retailerProductCard.productsAccount.getimages().contains("1"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse1,retailerProductCard.productsAccount.getParse1().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE1, getByteArray(retailerProductCard.resource1));
        }
        if(retailerProductCard.productsAccount.getimages().contains("2"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse2,retailerProductCard.productsAccount.getParse2().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE2, getByteArray(retailerProductCard.resource2));
        }
        if(retailerProductCard.productsAccount.getimages().contains("3"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse3,retailerProductCard.productsAccount.getParse3().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE3, getByteArray(retailerProductCard.resource3));
        }
        contentValues.put(CONTACTS_COLUMN_UPDATED_TIME, retailerProductCard.productsAccount.getUpdatedAt().toString());
        contentValues.put(CONTACTS_COLUMN_TYPE, retailerProductCard.productsAccount.getType());
        contentValues.put(CONTACTS_COLUMN_LIKES, retailerProductCard.productsAccount.getLikes());
        contentValues.put(CONTACTS_COLUMN_OBJECT, retailerProductCard.productsAccount.getObjectId());
        contentValues.put(CONTACTS_COLUMN_ALL_LOADED,"0");
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean InsertProduct(SimpleProductCard retailerProductCard)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, retailerProductCard.productsAccount.getname());
        contentValues.put(CONTACTS_COLUMN_User_NAME, retailerProductCard.productsAccount.getUser());
        contentValues.put(CONTACTS_COLUMN_IMAGECOUNT, retailerProductCard.productsAccount.getimages());
        contentValues.put(CONTACTS_COLUMN_PRICE, retailerProductCard.productsAccount.getPrice());
        contentValues.put(CONTACTS_COLUMN_DESCRIPTION, retailerProductCard.productsAccount.getDescription());
        if(retailerProductCard.productsAccount.getimages().contains("1"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse1,retailerProductCard.productsAccount.getParse1().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE1, getByteArray(retailerProductCard.resource1));
        }
        if(retailerProductCard.productsAccount.getimages().contains("2"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse2,retailerProductCard.productsAccount.getParse2().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE2, getByteArray(retailerProductCard.resource2));
        }
        if(retailerProductCard.productsAccount.getimages().contains("3"))
        {
            contentValues.put(CONTACTS_COLUMN_Parse3,retailerProductCard.productsAccount.getParse3().getUrl());
            contentValues.put(CONTACTS_COLUMN_IMAGE3, getByteArray(retailerProductCard.resource3));
        }
        contentValues.put(CONTACTS_COLUMN_UPDATED_TIME, retailerProductCard.productsAccount.getUpdatedAt().toString());
        contentValues.put(CONTACTS_COLUMN_TYPE, retailerProductCard.productsAccount.getType());
        contentValues.put(CONTACTS_COLUMN_LIKES, retailerProductCard.productsAccount.getLikes());
        contentValues.put(CONTACTS_COLUMN_OBJECT, retailerProductCard.productsAccount.getObjectId());
        contentValues.put(CONTACTS_COLUMN_ALL_LOADED,"0");
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    private RetailerProductCard getRetailerProductCard(Cursor res) {
        ProductsAccount productsAccount = new ProductsAccount(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)),
                res.getString(res.getColumnIndex(CONTACTS_COLUMN_PRICE)),res.getString(res.getColumnIndex(CONTACTS_COLUMN_DESCRIPTION))
                ,null,null,null,res.getString(res.getColumnIndex(CONTACTS_COLUMN_IMAGECOUNT)),
                res.getString(res.getColumnIndex(CONTACTS_COLUMN_User_NAME)),res.getString(res.getColumnIndex(CONTACTS_COLUMN_TYPE)),null);
        productsAccount.setLikes(Integer.valueOf(res.getString(res.getColumnIndex(CONTACTS_COLUMN_LIKES))));
        productsAccount.setObjectId(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
        productsAccount.updateTime = res.getString(res.getColumnIndex(CONTACTS_COLUMN_UPDATED_TIME));
        Bitmap image1= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE1)));
        Bitmap image2= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE2)));
        Bitmap image3= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE3)));
        return new RetailerProductCard(context,image1,image2,image3,productsAccount);
    }

    public ArrayList<RetailerProductCard> RetrieveRetailerProducts(String username)
    {
        ArrayList<RetailerProductCard> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + CONTACTS_TABLE_NAME + " where " + CONTACTS_COLUMN_User_NAME + "=" + username + "", null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            array_list.add(getRetailerProductCard(res));
            res.moveToNext();
        }
        return array_list;
    }

    public RetailerProductCard GetProductCard_From_Object_id(String objectid)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+objectid+"'", null );
        if(res.getCount()==0)
            return null;
        res.moveToFirst();
        return getRetailerProductCard(res);
    }

    public void UpdateLikesInformation(ProductsAccount productsAccount) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+productsAccount.getObjectId()+"'", null );
        if(res.getCount()==0)
            return;
        res.moveToFirst();
        if(res.getString(res.getColumnIndex(CONTACTS_COLUMN_UPDATED_TIME)).equals(productsAccount.getUpdatedAt().toString()))
            return;
        else
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONTACTS_COLUMN_LIKES,productsAccount.getLikes());
            db.update(CONTACTS_TABLE_NAME,contentValues,CONTACTS_COLUMN_OBJECT+"=?",new String[]{productsAccount.getObjectId()});
            return;
        }
    }

    public RetailerProductCard CheckAndReturnProduct(ProductsAccount productsAccount) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+productsAccount.getObjectId()+"'", null );
        if(res.getCount()==0)
            return null;
        res.moveToFirst();
        if(res.getString(res.getColumnIndex(CONTACTS_COLUMN_UPDATED_TIME)).equals(productsAccount.getUpdatedAt().toString()))
            return getRetailerProductCard(res);
        else
            {
                String url1,url2,url3;
                url1 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse1));
                url2 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse2));
                url3 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse3));
                if(productsAccount.getimages().contains("1")&&!productsAccount.getParse1().getUrl().equals(url1))
                {
                    deleteProduct(productsAccount.getObjectId());
                    return null;
                }
                if(productsAccount.getimages().contains("2")&&!productsAccount.getParse2().getUrl().equals(url2))
                {
                    deleteProduct(productsAccount.getObjectId());
                    return null;
                }
                if(productsAccount.getimages().contains("3")&&!productsAccount.getParse3().getUrl().equals(url3))
                {
                    deleteProduct(productsAccount.getObjectId());
                    return null;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(CONTACTS_COLUMN_NAME, productsAccount.getname());
                contentValues.put(CONTACTS_COLUMN_User_NAME, productsAccount.getUser());
                contentValues.put(CONTACTS_COLUMN_IMAGECOUNT, productsAccount.getimages());
                contentValues.put(CONTACTS_COLUMN_PRICE, productsAccount.getPrice());
                contentValues.put(CONTACTS_COLUMN_DESCRIPTION, productsAccount.getDescription());
                contentValues.put(CONTACTS_COLUMN_UPDATED_TIME, productsAccount.getUpdatedAt().toString());
                contentValues.put(CONTACTS_COLUMN_TYPE, productsAccount.getType());
                contentValues.put(CONTACTS_COLUMN_LIKES,productsAccount.getLikes());
                contentValues.put(CONTACTS_COLUMN_OBJECT, productsAccount.getObjectId());
                db.update(CONTACTS_TABLE_NAME,contentValues,CONTACTS_COLUMN_OBJECT+"=?",new String[]{productsAccount.getObjectId()});
                return CheckAndReturnProduct(productsAccount);
            }
    }

    public boolean CheckIfProductExist(String currentuser, String productname, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_User_NAME+"='"+currentuser+"' and "
                +CONTACTS_COLUMN_NAME+"='"+productname+"' and "+CONTACTS_COLUMN_TYPE+"='"+type+"'", null );
        if(res.getCount()>0)
        {
            res.close();
            return true;
        }
        else
        {
            res.close();
            return false;
        }
    }

    private SimpleProductCard getSimpleProductCard(Cursor res) {
        ProductsAccount productsAccount = new ProductsAccount(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)),
                res.getString(res.getColumnIndex(CONTACTS_COLUMN_PRICE)),res.getString(res.getColumnIndex(CONTACTS_COLUMN_DESCRIPTION))
                ,null,null,null,res.getString(res.getColumnIndex(CONTACTS_COLUMN_IMAGECOUNT)),
                res.getString(res.getColumnIndex(CONTACTS_COLUMN_User_NAME)),res.getString(res.getColumnIndex(CONTACTS_COLUMN_TYPE)),null);
        productsAccount.setLikes(Integer.valueOf(res.getString(res.getColumnIndex(CONTACTS_COLUMN_LIKES))));
        productsAccount.setObjectId(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
        productsAccount.updateTime = res.getString(res.getColumnIndex(CONTACTS_COLUMN_UPDATED_TIME));
        Bitmap image1= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE1)));
        Bitmap image2= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE2)));
        Bitmap image3= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE3)));

        SimpleProductCard retailerProductCard = new SimpleProductCard(context,image1,image2,image3,productsAccount);
        if(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ALL_LOADED)).equals("1"))
            retailerProductCard.AlreadyLoaded = 1;
        return retailerProductCard;
    }

    private CustomerProductCard getCustomerProductCard(Cursor res) {
        ProductsAccount productsAccount = new ProductsAccount(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)),
                res.getString(res.getColumnIndex(CONTACTS_COLUMN_PRICE)),res.getString(res.getColumnIndex(CONTACTS_COLUMN_DESCRIPTION))
                ,null,null,null,res.getString(res.getColumnIndex(CONTACTS_COLUMN_IMAGECOUNT)),
                res.getString(res.getColumnIndex(CONTACTS_COLUMN_User_NAME)),res.getString(res.getColumnIndex(CONTACTS_COLUMN_TYPE)),null);
        productsAccount.setLikes(Integer.valueOf(res.getString(res.getColumnIndex(CONTACTS_COLUMN_LIKES))));
        productsAccount.setObjectId(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
        productsAccount.updateTime = res.getString(res.getColumnIndex(CONTACTS_COLUMN_UPDATED_TIME));
        Bitmap image1= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE1)));
        Bitmap image2= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE2)));
        Bitmap image3= getImageBitmap(res.getBlob(res.getColumnIndex(CONTACTS_COLUMN_IMAGE3)));
        CustomerProductCard retailerProductCard = new CustomerProductCard(context,image1,image2,image3,productsAccount);
        if(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ALL_LOADED)).equals("1"))
            retailerProductCard.AlreadyLoaded = 1;
        return retailerProductCard;
    }

    public CustomerProductCard GetCustomerProductCard_From_Object_id(String objectid)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+objectid+"'", null );
        if(res.getCount()==0)
            return null;
        res.moveToFirst();
        return getCustomerProductCard(res);
    }


    public int GetProductPrice_From_Object_id(String objectid)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+objectid+"'", null );
        if(res.getCount()==0)
            return -1;
        res.moveToFirst();
        return Integer.valueOf(res.getString(res.getColumnIndex(CONTACTS_COLUMN_PRICE)));
    }

    public SimpleProductCard CheckAndReturnSimpleProductCard(ProductsAccount productsAccount) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+productsAccount.getObjectId()+"'", null );
        if(res.getCount()==0)
            return null;
        res.moveToFirst();
        if(res.getString(res.getColumnIndex(CONTACTS_COLUMN_UPDATED_TIME)).equals(productsAccount.getUpdatedAt().toString()))
            return getSimpleProductCard(res);
        else {
            String url1,url2,url3;
            url1 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse1));
            url2 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse2));
            url3 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse3));
            if(productsAccount.getimages().contains("1")&&!productsAccount.getParse1().getUrl().equals(url1))
            {
                deleteProduct(productsAccount.getObjectId());
                return null;
            }
            if(productsAccount.getimages().contains("2")&&!productsAccount.getParse2().getUrl().equals(url2))
            {
                deleteProduct(productsAccount.getObjectId());
                return null;
            }
            if(productsAccount.getimages().contains("3")&&!productsAccount.getParse3().getUrl().equals(url3))
            {
                deleteProduct(productsAccount.getObjectId());
                return null;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONTACTS_COLUMN_NAME, productsAccount.getname());
            contentValues.put(CONTACTS_COLUMN_User_NAME, productsAccount.getUser());
            contentValues.put(CONTACTS_COLUMN_IMAGECOUNT, productsAccount.getimages());
            contentValues.put(CONTACTS_COLUMN_PRICE, productsAccount.getPrice());
            contentValues.put(CONTACTS_COLUMN_DESCRIPTION, productsAccount.getDescription());
            contentValues.put(CONTACTS_COLUMN_UPDATED_TIME, productsAccount.getUpdatedAt().toString());
            contentValues.put(CONTACTS_COLUMN_TYPE, productsAccount.getType());
            contentValues.put(CONTACTS_COLUMN_LIKES,productsAccount.getLikes());
            contentValues.put(CONTACTS_COLUMN_OBJECT, productsAccount.getObjectId());
            db.update(CONTACTS_TABLE_NAME,contentValues,CONTACTS_COLUMN_OBJECT+"=?",new String[]{productsAccount.getObjectId()});
            return CheckAndReturnSimpleProductCard(productsAccount);
        }
    }

    public void update_Remaining_Images(CustomerProductCard card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(card.productsAccount.getimages().contains("2"))
            contentValues.put(CONTACTS_COLUMN_IMAGE2, getByteArray(card.resource2));
        if(card.productsAccount.getimages().contains("3"))
            contentValues.put(CONTACTS_COLUMN_IMAGE3, getByteArray(card.resource3));
        contentValues.put(CONTACTS_COLUMN_ALL_LOADED,"1");
        db.update(CONTACTS_TABLE_NAME,contentValues,CONTACTS_COLUMN_OBJECT+"=?",new String[]{card.productsAccount.getObjectId()});
    }

    public CustomerProductCard CheckAndReturnCustomerProduct(ProductsAccount productsAccount) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+productsAccount.getObjectId()+"'", null );
        if(res.getCount()==0)
            return null;
        res.moveToFirst();
        if(res.getString(res.getColumnIndex(CONTACTS_COLUMN_UPDATED_TIME)).equals(productsAccount.getUpdatedAt().toString()))
            return getCustomerProductCard(res);
        else
        {
            String url1,url2,url3;
            url1 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse1));
            url2 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse2));
            url3 = res.getString(res.getColumnIndex(CONTACTS_COLUMN_Parse3));
            if(productsAccount.getimages().contains("1")&&!productsAccount.getParse1().getUrl().equals(url1))
            {
                deleteProduct(productsAccount.getObjectId());
                return null;
            }
            if(productsAccount.getimages().contains("2")&&!productsAccount.getParse2().getUrl().equals(url2))
            {
                deleteProduct(productsAccount.getObjectId());
                return null;
            }
            if(productsAccount.getimages().contains("3")&&!productsAccount.getParse3().getUrl().equals(url3))
            {
                deleteProduct(productsAccount.getObjectId());
                return null;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONTACTS_COLUMN_NAME, productsAccount.getname());
            contentValues.put(CONTACTS_COLUMN_User_NAME, productsAccount.getUser());
            contentValues.put(CONTACTS_COLUMN_IMAGECOUNT, productsAccount.getimages());
            contentValues.put(CONTACTS_COLUMN_PRICE, productsAccount.getPrice());
            contentValues.put(CONTACTS_COLUMN_DESCRIPTION, productsAccount.getDescription());
            contentValues.put(CONTACTS_COLUMN_UPDATED_TIME, productsAccount.getUpdatedAt().toString());
            contentValues.put(CONTACTS_COLUMN_TYPE, productsAccount.getType());
            contentValues.put(CONTACTS_COLUMN_LIKES,productsAccount.getLikes());
            contentValues.put(CONTACTS_COLUMN_OBJECT, productsAccount.getObjectId());
            db.update(CONTACTS_TABLE_NAME,contentValues,CONTACTS_COLUMN_OBJECT+"=?",new String[]{productsAccount.getObjectId()});
            return CheckAndReturnCustomerProduct(productsAccount);
        }

    }

    private Bitmap getImageBitmap(byte[] blob) {
        if(blob == null)
            return null;
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }

    private byte[] getByteArray(Bitmap resource1) {
        if(resource1==null)
            return null;
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        resource1.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    public void UpdateLike(String objectId,boolean increment) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+objectId+"'", null );
        if(res.getCount()==0)
            return;
        res.moveToFirst();
        String like=res.getString(res.getColumnIndex(CONTACTS_COLUMN_LIKES));
        res.close();
        if(increment)
            like=String.valueOf(Integer.valueOf(like)+1);
        else
        like =String.valueOf(Integer.valueOf(like)-1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_LIKES,like );
        db.update(CONTACTS_TABLE_NAME,contentValues,CONTACTS_COLUMN_OBJECT+"=?",new String[]{objectId});
    }

    public int GetCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME, null );
        return res.getCount();
    }

    public int GetLike(String objectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+objectId+"'", null );
        if(res.getCount()==0)
            return 0;
        res.moveToFirst();
        String like=res.getString(res.getColumnIndex(CONTACTS_COLUMN_LIKES));
        return Integer.valueOf(like);
    }

    public SimpleProductCard GetSimpleCard_From_Object_id(String string) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+string+"'", null );
        if(res.getCount()==0)
            return null;
        res.moveToFirst();
        return getSimpleProductCard(res);
    }

    public void CheckAndDeleteDeletedProducts(List<ProductsAccount> productsAccounts, String username) {
        HashMap<String,Boolean> map = new HashMap<>();
        for(ProductsAccount i:productsAccounts)
            map.put(i.getObjectId(),true);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_User_NAME+"="+username+"", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            if(!map.containsKey(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT))))
                deleteProduct(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
            res.moveToNext();
        }
        res.close();
    }

    public void clearAllOtherProducts(HashMap<String, Boolean> products_needed) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            if(!products_needed.containsKey(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT))))
                deleteProduct(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
            res.moveToNext();
        }
        res.close();
    }
}