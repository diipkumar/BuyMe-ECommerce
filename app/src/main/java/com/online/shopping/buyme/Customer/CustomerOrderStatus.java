package com.online.shopping.buyme.Customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aphidmobile.flip.FlipViewController;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.CustomerProductCard;
import com.online.shopping.buyme.ParseAccount.OrderStatusAccount;
import com.online.shopping.buyme.ParseAccount.ProductsAccount;
import com.online.shopping.buyme.ParseAccount.RatingReviews;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Retailer.RetailerMain;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.CustomeActivity;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.List;

public class CustomerOrderStatus extends CustomeActivity {
    CustomerOrderStatusAdapter adapter ;
    private FlipViewController flipView;
    UtilityFunction utilityFunction;
    private ProductDBhelper productDBhelper;
    ImageView star1,star2,star3,star4,star5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_status);
        flipView = new FlipViewController(this,FlipViewController.HORIZONTAL);
        flipView.setAnimationBitmapFormat(Bitmap.Config.ARGB_8888);
        flipView.setOnViewFlipListener(new FlipViewController.ViewFlipListener() {
            @Override
            public void onViewFlipped(View view, int position) {
                OrderStatusAccount orderStatusAccount = adapter.data.get(position);
                if(orderStatusAccount.getStatus().equals("3"))
                    showCancelOrder(orderStatusAccount);
                else if(orderStatusAccount.getStatus().equals("2"))
                    showRatingDialog(orderStatusAccount);
            }
        });
        setContentView(flipView);
        setListeners();
        syncData();
    }

    private void showCancelOrder(final OrderStatusAccount orderStatusAccount) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(this.getResources().getString(R.string.Proceed_order_CANCELLATION_title));
        alertDialog.setMessage(this.getResources().getString(R.string.Proceed_order_Customer_CANCELLATION_message));
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showprogess();
                orderStatusAccount.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        stopprogress();
                        if (e != null) {
                            utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                            return;
                        }
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    private void setListeners() {
        productDBhelper = new ProductDBhelper(this);
        utilityFunction = new UtilityFunction(this);
    }

    private void syncData() {
        showprogess();
        final ParseQuery<OrderStatusAccount> query= ParseQuery.getQuery("OrderStatus");
        query.whereEqualTo("customeruser",ConfigData.currentuser);
        query.findInBackground(new FindCallback<OrderStatusAccount>() {
            @Override
            public void done(final List<OrderStatusAccount> orderStatusAccounts, ParseException e) {
                if(e!= null)
                {
                    stopprogress();
                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                    finish();
                    return;
                }
                if(orderStatusAccounts.size()==0)
                {
                    Toast.makeText(getApplicationContext(),"No OrderStatus Available",Toast.LENGTH_LONG).show();
                }
                adapter = new CustomerOrderStatusAdapter(CustomerOrderStatus.this, orderStatusAccounts);
                List<String> object = new ArrayList<>();
                for(OrderStatusAccount i:orderStatusAccounts)
                {
                    List<String> temp=i.getProducts();
                    for(String j:temp)
                    {
                        String product_object_id = j.substring(0,j.indexOf(",;,"));
                        object.add(product_object_id);
                    }
                }
                ParseQuery<ProductsAccount> query = ParseQuery.getQuery("Products");
                query.whereContainedIn("objectId", object);
                query.findInBackground(new FindCallback<ProductsAccount>() {
                    @Override
                    public void done(final List<ProductsAccount> productsAccounts, ParseException e) {
                        if(e != null)
                        {
                            stopprogress();
                            utilityFunction.ToastMessage("Error in Syncing Data");
                            finish();
                            return;
                        }
                        getSimpleProductCard(0,productsAccounts);
                    }
                });
            }
        });
    }

    private void getSimpleProductCard(final int pos, final List<ProductsAccount> productsAccounts) {
        if(pos > productsAccounts.size()-1)
        {
            stopprogress();
            flipView.setAdapter(adapter);
            return;
        }
        final ProductsAccount Currentproduct = productsAccounts.get(pos);
        String imagescount = Currentproduct.getimages();
        final Bitmap[] bmp1 = new Bitmap[1];
        final Bitmap bmp2 = null,bmp3 = null;
        final CustomerProductCard[] card = {productDBhelper.CheckAndReturnCustomerProduct(Currentproduct)};
        if(card[0] !=null)
        {
            getSimpleProductCard(pos + 1, productsAccounts);
            return;
        }
        if (imagescount.contains("1"))
        {
            Currentproduct.getParse1().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(e!=null)
                    {
                        stopprogress();
                        utilityFunction.SanckMessage(getResources().getString(R.string.networkerror));
                        finish();
                        return;
                    }
                    bmp1[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    card[0] = new CustomerProductCard(CustomerOrderStatus.this, bmp1[0], bmp2, bmp3,Currentproduct);
                    productDBhelper.InsertProduct(card[0]);
                    getSimpleProductCard(pos + 1, productsAccounts);
                }
            });
        }
        else {
            bmp1[0] = BitmapFactory.decodeResource(utilityFunction.activity.getResources(), RetailerMain.DefaultImage);
            card[0] = new CustomerProductCard(CustomerOrderStatus.this, bmp1[0], bmp2, bmp3,Currentproduct);
            productDBhelper.InsertProduct(card[0]);
            getSimpleProductCard(pos + 1, productsAccounts);
        }
    }

    private void showRatingDialog(final OrderStatusAccount orderStatusAccount) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rating);
        final EditText comment;
        comment = (EditText) dialog.findViewById(R.id.comment);
        star1 = (ImageView)dialog.findViewById(R.id.image1);
        star2 = (ImageView)dialog.findViewById(R.id.image2);
        star3 = (ImageView)dialog.findViewById(R.id.image3);
        star4 = (ImageView)dialog.findViewById(R.id.image4);
        star5 = (ImageView)dialog.findViewById(R.id.image5);
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fill(1);
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fill(2);
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fill(3);
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fill(4);}
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fill(5);
            }
        });
        fill(3);
        TextView ok,cancel;
        ok = (TextView)dialog.findViewById(R.id.ok);
        cancel = (TextView)dialog.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getText().toString().length()==0)
                {
                    utilityFunction.ToastMessage("Please enter comment");
                    return;
                }
                showprogess();
                String star="";
                if(star1.getTag()==1)
                    star+="1";
                if(star2.getTag()==1)
                    star+="2";
                if(star3.getTag()==1)
                    star+="3";
                if(star4.getTag()==1)
                    star+="4";
                if(star5.getTag()==1)
                    star+="5";

                final String finalStar = star;
                orderStatusAccount.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            stopprogress();
                            return;
                        }
                        RatingReviews ratingReviews = new RatingReviews(orderStatusAccount.getname(), finalStar, comment.getText().toString(), ConfigData.currentuser);
                        ratingReviews.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                stopprogress();
                                if (e != null) {
                                    utilityFunction.ToastMessage(getResources().getString(R.string.networkerror));
                                }
                                dialog.dismiss();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });

                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void fill(int i) {
        star1.setImageResource(R.drawable.star_empty);
        star2.setImageResource(R.drawable.star_empty);
        star3.setImageResource(R.drawable.star_empty);
        star4.setImageResource(R.drawable.star_empty);
        star5.setImageResource(R.drawable.star_empty);
        star1.setTag(0);
        star2.setTag(0);
        star3.setTag(0);
        star4.setTag(0);
        star5.setTag(0);
        switch (i)
        {
            case 5:
                star5.setTag(1);
                star5.setImageResource(R.drawable.star_filled);
            case 4:
                star4.setTag(1);
                star4.setImageResource(R.drawable.star_filled);
            case 3:
                star3.setTag(1);
                star3.setImageResource(R.drawable.star_filled);
            case 2:
                star2.setTag(1);
                star2.setImageResource(R.drawable.star_filled);
            case 1:
                star1.setTag(1);
                star1.setImageResource(R.drawable.star_filled);
        }
    }


    @Override
    protected void onPause() {
        stopprogress();
        super.onPause();
    }
}
