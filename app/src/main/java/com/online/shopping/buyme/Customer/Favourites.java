package com.online.shopping.buyme.Customer;

import android.os.Bundle;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.ParseAccount.FavouritesAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import java.util.List;

public class Favourites extends CustomeActivity {

    UtilityFunction utilityFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);
        setListeners();
        SyncFavourties();
    }

    private void setListeners() {
        utilityFunction = new UtilityFunction(this);
    }

    @Override
    protected void onPause() {
        stopprogress();
        super.onPause();
    }

    private void SyncFavourties() {
        showprogess();
        ParseQuery<FavouritesAccount> query = ParseQuery.getQuery("Favourites");
        query.whereEqualTo("Customer", ConfigData.currentuser);
        query.findInBackground(new FindCallback<FavouritesAccount>() {
            @Override
            public void done(final List<FavouritesAccount> favouriteses, ParseException e) {
                stopprogress();
                if(e==null)
                {
                    FavouritesListAdapter listAdapter = new FavouritesListAdapter(Favourites.this,favouriteses);
                    ListView listView = (ListView) findViewById(R.id.listview);
                    listView.setAdapter(listAdapter);
                }
                else
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
            }
        });}

}
