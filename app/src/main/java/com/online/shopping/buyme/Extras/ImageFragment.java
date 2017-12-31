package com.online.shopping.buyme.Extras;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.online.shopping.buyme.Config.ConfigData;

public class ImageFragment extends Fragment {

    Bitmap bmp;
    public ImageFragment()
    {}
    public void setBitmap(Bitmap bmp2)
    {
        bmp = bmp2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        ImageView iv = new ImageView(getActivity());
        iv.setImageBitmap(bmp);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigData.ProductImageView = bmp;
                startActivity(new Intent(getActivity(), ProductImageView.class));
            }
        });
        return iv;
    }

}
