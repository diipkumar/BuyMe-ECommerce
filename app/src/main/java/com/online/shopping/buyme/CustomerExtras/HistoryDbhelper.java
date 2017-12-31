package com.online.shopping.buyme.CustomerExtras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.online.shopping.buyme.Extras.SimpleProductCard;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.List;

public class HistoryDbhelper extends SQLiteOpenHelper {

    public Context context;
    public final static String DATABASE_NAME = "History.db";
    public final String CONTACTS_TABLE_NAME = "history";
    public final String CONTACTS_COLUMN_OBJECT = "objectid";
    public final String CONTACTS_COLUMN_NAME = "heading";

    public HistoryDbhelper(Context context)
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

    public boolean InsertProduct(String heading,String objectid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_OBJECT, objectid);
        contentValues.put(CONTACTS_COLUMN_NAME, heading);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean InsertProducts(String heading,List<String> objects)
    {
        deleteProduct(heading);
        for(String i:objects)
            InsertProduct(heading,i);
        return true;
    }

    public void deleteProduct(String heading) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_NAME+"='"+heading+"'");
    }

    public ArrayList<SimpleProductCard> getAllCustomerProduct(String heading, ProductDBhelper productDBhelper) {
        ArrayList<SimpleProductCard> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_NAME+"='"+heading+"'", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            SimpleProductCard simpleProductCard =getsimpleCard(res, productDBhelper);
            if(simpleProductCard!=null)
                array_list.add(simpleProductCard);
            res.moveToNext();
        }
        return array_list;
    }

    private SimpleProductCard getsimpleCard(Cursor res, ProductDBhelper productDBhelper) {
        SimpleProductCard simpleProductCard= productDBhelper.GetSimpleCard_From_Object_id(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OBJECT)));
        if(simpleProductCard==null||simpleProductCard.productsAccount==null)
            return null;
        return simpleProductCard;
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