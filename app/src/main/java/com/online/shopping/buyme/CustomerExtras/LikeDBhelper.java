package com.online.shopping.buyme.CustomerExtras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.online.shopping.buyme.Config.ConfigData;

public class LikeDBhelper extends SQLiteOpenHelper {

    public Context context;
    public final static String DATABASE_NAME = "Like.db";
    public final String CONTACTS_TABLE_NAME = "Like";
    public final String CONTACTS_COLUMN_OBJECT = "objectid";
    public final String CONTACTS_COLUMN_NAME = "username";
    public LikeDBhelper(Context context)
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

    public boolean InsertProduct(String ObjectId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
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

}