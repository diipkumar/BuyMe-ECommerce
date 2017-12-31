package com.online.shopping.buyme.CustomerExtras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.common.ProductDBhelper;

import java.util.ArrayList;

public class BasketDBhelper extends SQLiteOpenHelper {

    public Context context;
    public final static String DATABASE_NAME = "Basket.db";
    public final String CONTACTS_TABLE_NAME = "Basket";
    public final String CONTACTS_COLUMN_OBJECT = "objectid";
    public final String CONTACTS_COLUMN_NAME = "username";
    public final String CONTACTS_COLUMN_QUANTITY= "quantity";
    public BasketDBhelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String query ="create table "+CONTACTS_TABLE_NAME +"(";
        query+=CONTACTS_COLUMN_NAME+" text, ";
        query+=CONTACTS_COLUMN_QUANTITY+" integer, ";
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


    public void update_quantity(CustomerBasketCard card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_OBJECT, card.card.productsAccount.getObjectId());
        contentValues.put(CONTACTS_COLUMN_QUANTITY, card.quantitynumber);
        db.update(CONTACTS_TABLE_NAME,contentValues,CONTACTS_COLUMN_OBJECT+"=?",new String[]{card.card.productsAccount.getObjectId()});
    }

    public boolean InsertProduct(String ObjectId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_OBJECT, ObjectId);
        contentValues.put(CONTACTS_COLUMN_NAME, ConfigData.currentuser);
        contentValues.put(CONTACTS_COLUMN_QUANTITY, 1);
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

    public ArrayList<CustomerBasketCard> getAllCustomerProduct(ProductDBhelper productDBhelper) {
        ArrayList<CustomerBasketCard> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_NAME+"="+ConfigData.currentuser+"", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            CustomerBasketCard temp = getBasketCard(res,productDBhelper);
            if(temp!=null)
                array_list.add(temp);
            res.moveToNext();
        }
        return array_list;
    }

    private CustomerBasketCard getBasketCard(Cursor res, ProductDBhelper productDBhelper) {
        CustomerProductCard customerProductCard = productDBhelper.GetCustomerProductCard_From_Object_id(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
        if(customerProductCard==null)
            return null;
        return new CustomerBasketCard(context,customerProductCard,res.getInt(res.getColumnIndex(CONTACTS_COLUMN_QUANTITY)));
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
        return array_list;
    }


}