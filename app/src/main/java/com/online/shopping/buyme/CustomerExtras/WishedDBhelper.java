package com.online.shopping.buyme.CustomerExtras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.common.ProductDBhelper;

import java.util.ArrayList;

public class WishedDBhelper extends SQLiteOpenHelper {

    public Context context;
    public final static String DATABASE_NAME = "Wished.db";
    public final String CONTACTS_TABLE_NAME = "Wished";
    public final String CONTACTS_COLUMN_OBJECT = "objectid";
    public final String CONTACTS_COLUMN_NAME = "username";
    public WishedDBhelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String query ="create table "+CONTACTS_TABLE_NAME +"(";
        query+=CONTACTS_COLUMN_NAME+" text, ";
        query+=CONTACTS_COLUMN_OBJECT+" text";
        query+=")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
        onCreate(db);
    }


    public ArrayList<CustomerWishedCard> getAllCustomerProduct(ProductDBhelper productDBhelper) {
        ArrayList<CustomerWishedCard> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_NAME+"="+ConfigData.currentuser+"", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            CustomerWishedCard temp = getBasketCard(res,productDBhelper);
            if(temp!=null)
            array_list.add(temp);
            res.moveToNext();
        }
        return array_list;
    }

    private CustomerWishedCard getBasketCard(Cursor res, ProductDBhelper productDBhelper) {
        CustomerProductCard customerProductCard = productDBhelper.GetCustomerProductCard_From_Object_id(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
        if(customerProductCard==null)
            return null;
        return new CustomerWishedCard(context,customerProductCard);
    }

    public boolean InsertProduct(String ObjectId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+ObjectId+"' and "+CONTACTS_COLUMN_NAME+"='"+ConfigData.currentuser+"'", null );
        if(res.getCount()>0)
            return true;
        res.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_OBJECT, ObjectId);
        contentValues.put(CONTACTS_COLUMN_NAME, ConfigData.currentuser);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean Contain_Product(String objectid)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+objectid+"' and "+CONTACTS_COLUMN_NAME+"='"+ConfigData.currentuser+"'", null );
        if(res.getCount()==0)
            return false;
        res.close();
        return true;
    }


    public void deleteProduct(String objectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_OBJECT+"='"+objectId+"' and "+CONTACTS_COLUMN_NAME+"='"+ConfigData.currentuser+"'");
    }

    public void clearUserWishList(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_NAME+"='"+username+"'");
    }


    public ArrayList<String> getAllProducts() {
        ArrayList<String> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}