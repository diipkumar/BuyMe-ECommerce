package com.online.shopping.buyme.Extras;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.common.CustomeActivity;

public class ProductImageView extends CustomeActivity {
    ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prodctimageview);
        ((ImageView)findViewById(R.id.productimageview)).setImageBitmap(ConfigData.ProductImageView);
        ConfigData.ProductImageView = null ;
        setupActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupActionBar() {
        ab=getActionBar();
    }

}
