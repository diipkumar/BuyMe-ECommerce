package com.online.shopping.buyme;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Customer.CustomerMain;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.Utility.CommonFunction;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.ConnectionDetector;
import com.online.shopping.buyme.common.CustomeActivity;
import java.util.List;


public class MainActivity extends CustomeActivity implements View.OnClickListener {
    UtilityFunction utility;
    TextView customer,retailer;

    @Override
    protected void onStart() {
        super.onStart();
        ConfigData.SharedProductObjectID = "";
        final ConnectionDetector connectionDetector =new ConnectionDetector(this);
        if(!connectionDetector.isConnectingToInternet())
        {
            showDialog();
            return;
        }
        if(getIntent().getData()!=null)
        {
            Uri data = getIntent().getData();
            List<String> params = data.getPathSegments();
            if(params.size()>0)
                ConfigData.SharedProductObjectID = params.get(0);
            finish();
        }
        ConfigData.signuplatlng = null;
        ConfigData.commonFunction = new CommonFunction(this);
        ConfigData.WishListchecked = false;
        ConfigData.BasketChecked = false;
        ConfigData.HomeChecked = false;
        utility = new UtilityFunction(this);
        if(ConfigData.commonFunction.retrievelogin())
        {
            if(ConfigData.isCustomer)
                utility.cleargoActivity(CustomerMain.class);
            else
                utility.cleargoActivity(RetailerMain.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupListeners();
    }

    private void setupListeners() {
        customer = (TextView)findViewById(R.id.customer);
        retailer = (TextView)findViewById(R.id.retailer);
        customer.setOnClickListener(this);
        retailer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ConfigData.isCustomer = true;
        int id=v.getId();
        switch (id)
        {
            case R.id.retailer:
                ConfigData.isCustomer = false;
                break;
        }
        utility.goActivity(LoginAndSignup.class);
    }
}
