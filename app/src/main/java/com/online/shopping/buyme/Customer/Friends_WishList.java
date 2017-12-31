package com.online.shopping.buyme.Customer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.online.shopping.buyme.Extras.ItemAdapter;
import com.online.shopping.buyme.ParseAccount.WishListAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Friends_WishList extends CustomeActivity implements TextWatcher {
    public static Set<String> To_Be_loaded_Products;
    private ListView listview;
    HashMap<String,Set<String> > User_to_Object ;
    ItemAdapter adapter,tempadapter;
    List<Contact> itemData,tempitemData;
    UtilityFunction utilityFunction;
    HashMap<String,String> User_to_Number ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_wishlist);
        showprogess();
        setListeners();
        fetchContacts();
    }

    private void setListeners() {
        utilityFunction = new UtilityFunction(this);
        EditText search = (EditText) findViewById(R.id.contact_search);
        listview=(ListView)findViewById(R.id.example_swipe_lv_list);
        itemData=new ArrayList<>();
        adapter=new ItemAdapter(this, R.layout.friends_wishlist_row,itemData);
        listview.setAdapter(adapter);
        listview.setDivider(null);
        listview.setDividerHeight(0);
        search.addTextChangedListener(this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                To_Be_loaded_Products = User_to_Object.get(adapter.getItem(position).Number);
                utilityFunction.goActivity(FriendList.class);
            }
        });
    }

    public void fetchContacts() {
        User_to_Number = new HashMap<>();
        Set<String> temparray = new HashSet<>();
        String phoneNumber = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
        if (cursor!=null&&cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0)
                {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                    while (phoneCursor!=null&&phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    }
                    if(phoneCursor!=null)
                        phoneCursor.close();
                }
                if(User_to_Number.containsKey(phoneNumber))
                    continue;
                if(phoneNumber!=null&&phoneNumber.length()>10)
                    phoneNumber=phoneNumber.substring(phoneNumber.length()-10,phoneNumber.length());
                User_to_Number.put(phoneNumber,name);
                temparray.add(phoneNumber);
            }
        }
        if(cursor!=null)
            cursor.close();
        User_to_Object = new HashMap<>();
        ParseQuery<WishListAccount> query= ParseQuery.getQuery("WishList");
        query.whereContainedIn("User", temparray);
        query.findInBackground(new FindCallback<WishListAccount>() {
            @Override
            public void done(List<WishListAccount> wishListAccounts, ParseException e) {
                if (e != null) {
                    stopprogress();
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                }
                for (WishListAccount i : wishListAccounts) {
                    if (User_to_Object.containsKey(i.getUser()))
                    {
                        User_to_Object.get(i.getUser()).add(i.getObject());
                    }
                    else {
                        Set<String> temp = new HashSet<>();
                        temp.add(i.getObject());
                        User_to_Object.put(i.getUser(), temp);
                        itemData.add(new Contact(User_to_Number.get(i.getUser()), i.getUser()));
                    }
                }
                stopprogress();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int textlength = s.length();
        tempitemData=new ArrayList<>();
        for(Contact c: itemData){
            if (textlength <= c.Name.length()) {
                if (c.Name.toLowerCase().contains(s.toString().toLowerCase())) {
                    tempitemData.add(c);
                }
            }
        }
        tempadapter=new ItemAdapter(this,R.layout.friends_wishlist_row,tempitemData);
        listview.setAdapter(tempadapter);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
    }


    @Override
    protected void onPause() {
        stopprogress();
        super.onPause();
    }

}
