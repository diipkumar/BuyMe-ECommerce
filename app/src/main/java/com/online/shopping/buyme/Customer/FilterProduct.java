package com.online.shopping.buyme.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.rey.material.widget.CheckBox;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.common.CustomeActivity;

public class FilterProduct extends CustomeActivity implements CompoundButton.OnCheckedChangeListener {
    RadioButton any,dist5,dist10,dist20;
    RadioButton priceany,price500,price500_to_1000,price1000_to_5000,price5000_to_15000,price15000_to_30000,price30000_to_50000,priceabove50000;
    CheckBox cod;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        setListeners();
    }

    private void setListeners() {
        cod = (CheckBox) findViewById(R.id.checkboxcod);
        any = (RadioButton) findViewById(R.id.radiodistanceany);
        dist5= (RadioButton) findViewById(R.id.radiodistance1);
        dist10= (RadioButton) findViewById(R.id.radiodistance2);
        dist20= (RadioButton) findViewById(R.id.radiodistance3);
        priceany = (RadioButton) findViewById(R.id.radioprice0);
        price500 = (RadioButton) findViewById(R.id.radioprice1);
        price500_to_1000= (RadioButton) findViewById(R.id.radioprice2);
        price1000_to_5000= (RadioButton) findViewById(R.id.radioprice3);
        price5000_to_15000= (RadioButton) findViewById(R.id.radioprice4);
        price15000_to_30000= (RadioButton) findViewById(R.id.radioprice5);
        price30000_to_50000= (RadioButton) findViewById(R.id.radioprice6);
        priceabove50000= (RadioButton) findViewById(R.id.radioprice7);
        cod.setOnCheckedChangeListener(this);
        any.setOnCheckedChangeListener(this);
        dist5.setOnCheckedChangeListener(this);
        dist10.setOnCheckedChangeListener(this);
        dist20.setOnCheckedChangeListener(this);
        price500.setOnCheckedChangeListener(this);
        price500_to_1000.setOnCheckedChangeListener(this);
        price1000_to_5000.setOnCheckedChangeListener(this);
        price5000_to_15000.setOnCheckedChangeListener(this);
        price15000_to_30000.setOnCheckedChangeListener(this);
        price30000_to_50000.setOnCheckedChangeListener(this);
        priceabove50000.setOnCheckedChangeListener(this);
        setData();
    }

    private void setData() {
        cod.setChecked(ConfigData.filter_cod);
        switch (ConfigData.filter_dist)
        {
            case -1:
                any.setChecked(true);
                break;
            case 20:
                dist20.setChecked(true);
                break;
            case 10:
                dist10.setChecked(true);
                break;
            case 5:
                dist5.setChecked(true);
                break;
        }
        switch (ConfigData.filter_price_low)
        {
            case -1:
                priceany.setChecked(true);
                break;
            case 0:
                price500.setChecked(true);
                break;
            case 500:
                price500_to_1000.setChecked(true);
                break;
            case 1000:
                price1000_to_5000.setChecked(true);
                break;
            case 5000:
                price5000_to_15000.setChecked(true);
                break;
            case 15000:
                price15000_to_30000.setChecked(true);
                break;
            case 30000:
                price30000_to_50000.setChecked(true);
                break;
            case 50000:
                priceabove50000.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent();
        setResult(CustomerBaseAcctivity.FILTER_CODE,intent);
        finish();//finishing activity
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b)
        {
            switch (compoundButton.getId())
            {
                case R.id.checkboxcod:
                    ConfigData.filter_cod=true;
                    break;
                case R.id.radiodistanceany:
                    ConfigData.filter_dist = -1;
                    break;
                case R.id.radiodistance1:
                    ConfigData.filter_dist = 5;
                    break;
                case R.id.radiodistance2:
                    ConfigData.filter_dist = 10;
                    break;
                case R.id.radiodistance3:
                    ConfigData.filter_dist = 20;
                    break;
                case R.id.radioprice0:
                    ConfigData.filter_price_low= -1;
                    ConfigData.filter_price_high= 500;
                    break;
                case R.id.radioprice1:
                    ConfigData.filter_price_low= 0;
                    ConfigData.filter_price_high= 500;
                    break;
                case R.id.radioprice2:
                    ConfigData.filter_price_low= 500;
                    ConfigData.filter_price_high= 1000;
                    break;
                case R.id.radioprice3:
                    ConfigData.filter_price_low= 1000;
                    ConfigData.filter_price_high= 5000;
                    break;
                case R.id.radioprice4:
                    ConfigData.filter_price_low= 5000;
                    ConfigData.filter_price_high= 15000;
                    break;
                case R.id.radioprice5:
                    ConfigData.filter_price_low= 15000;
                    ConfigData.filter_price_high= 30000;
                    break;
                case R.id.radioprice6:
                    ConfigData.filter_price_low= 30000;
                    ConfigData.filter_price_high= 50000;
                    break;
                case R.id.radioprice7:
                    ConfigData.filter_price_low= 50000;
                    ConfigData.filter_price_high=1000000;
                    break;
            }
        }
        else
        {
            if(compoundButton.getId()==R.id.checkboxcod)
                ConfigData.filter_cod= false;
        }
    }
}
