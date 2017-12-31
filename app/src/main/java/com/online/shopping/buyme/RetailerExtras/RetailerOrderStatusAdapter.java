package com.online.shopping.buyme.RetailerExtras;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.aphidmobile.utils.AphidLog;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.CustomerExtras.CustomerProductCard;
import com.online.shopping.buyme.Extras.AnimatedExpandableListView;
import com.online.shopping.buyme.Extras.OrderProductListAdapter;
import com.online.shopping.buyme.ParseAccount.OrderStatusAccount;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.common.ProductDBhelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.StreamHandler;

public class RetailerOrderStatusAdapter extends BaseAdapter {
   private final Activity context;
    private LayoutInflater inflater;
    public List<OrderStatusAccount> data;

    public RetailerOrderStatusAdapter(Activity context, List<OrderStatusAccount> data) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.data = data;
  }

  @Override
  public int getCount() {
    return data.size();
  }

  @Override
  public Object getItem(int position) {
    return position;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View layout = convertView;
    if (convertView == null) {
      layout = inflater.inflate(R.layout.order_status, null);
      AphidLog.d("created new view from adapter: %d", position);
    }
      final OrderStatusAccount orderStatusAccount= data.get(position);
      final TextView orderheading,orderno,ordername,orderaddress,ordernumber,totalprice;
      totalprice = (TextView) layout.findViewById(R.id.totalprice);
      orderheading = (TextView) layout.findViewById(R.id.ordertitle);
      orderno = (TextView) layout.findViewById(R.id.orderno);
      ordername = (TextView) layout.findViewById(R.id.ordestatusname);
      orderaddress = (TextView) layout.findViewById(R.id.orderstatusaddress);
      ordernumber = (TextView) layout.findViewById(R.id.orderphonenumber);
      final Spinner orderstatusspinner = (Spinner) layout.findViewById(R.id.orderstatusspinner);
      List<String> temp = Arrays.asList(ConfigData.retailerspinner);
      ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,temp);
      dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      orderstatusspinner.setAdapter(dataAdapter);
      orderstatusspinner.setSelection(Integer.valueOf(orderStatusAccount.getStatus()));
      orderstatusspinner.setTag(Integer.valueOf(orderStatusAccount.getStatus()));
      orderstatusspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
              final int previous = (int) orderstatusspinner.getTag();
              if(i==previous)
                  return;
              if(i<previous)
              {
                  makeToast("Invalid Selected Option");
                  orderstatusspinner.setSelection(previous);
                  return;
              }
              if (i == 1) {
                  AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                  alertDialog.setTitle(context.getResources().getString(R.string.Proceed_order_Confirmation_title));
                  alertDialog.setMessage(context.getResources().getString(R.string.Proceed_order_Confirmation_message));
                  alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          ConfigData.currentActivity.showprogess();
                          orderStatusAccount.setStatus(String.valueOf(i));
                          orderStatusAccount.saveInBackground(new SaveCallback() {
                              @Override
                              public void done(ParseException e) {
                                  ConfigData.currentActivity.stopprogress();
                                  if(e!=null)
                                  {
                                      orderstatusspinner.setSelection(previous);
                                      return;
                                  }
                                  orderstatusspinner.setTag(i);
                              }
                          });
                      }
                  });
                  alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.cancel();
                      }
                  });
                  alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                      @Override
                      public void onCancel(DialogInterface dialogInterface) {
                          orderstatusspinner.setSelection(previous);
                      }
                  });
                  alertDialog.show();
                  return;
              }
              if(i==2)
              {
                  AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                  alertDialog.setTitle(context.getResources().getString(R.string.Proceed_order_Delivered_title));
                  alertDialog.setMessage(context.getResources().getString(R.string.Proceed_order_Ddelivered_message));
                  alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          ConfigData.currentActivity.showprogess();
                          orderStatusAccount.setStatus(String.valueOf(i));
                          orderStatusAccount.saveInBackground(new SaveCallback() {
                              @Override
                              public void done(ParseException e) {
                                  ConfigData.currentActivity.stopprogress();
                                  if(e!=null)
                                  {
                                      orderstatusspinner.setSelection(previous);
                                      return;
                                  }
                                  ParseQuery pushQuery = ParseInstallation.getQuery();
                                  pushQuery.whereEqualTo("user", orderStatusAccount.getCustomer());
                                  ParsePush push = new ParsePush();
                                  push.setQuery(pushQuery);
                                  push.setMessage("Your order has been delivered.");
                                  push.sendInBackground();
                                  orderstatusspinner.setTag(i);
                              }
                          });
                      }
                  });
                  alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.cancel();
                      }
                  });
                  alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                      @Override
                      public void onCancel(DialogInterface dialogInterface) {
                          orderstatusspinner.setSelection(previous);
                      }
                  });
                  alertDialog.show();

              }
              if(i==3)
              {
                  if(previous!=0)
                  {
                     makeToast("You cannot cancel the order now");
                     orderstatusspinner.setSelection(previous);
                     return;
                  }
                  AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                  alertDialog.setTitle(context.getResources().getString(R.string.Proceed_order_CANCELLATION_title));
                  alertDialog.setMessage(context.getResources().getString(R.string.Proceed_order_CANCELLATION_message));
                  alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          ConfigData.currentActivity.showprogess();
                          orderStatusAccount.setStatus(String.valueOf(i));
                          orderStatusAccount.setRetailer("");
                          orderStatusAccount.saveInBackground(new SaveCallback() {
                              @Override
                              public void done(ParseException e) {
                                  ConfigData.currentActivity.stopprogress();
                                  if (e != null) {
                                      orderstatusspinner.setSelection(previous);
                                      return;
                                  }
                                  orderstatusspinner.setTag(i);
                              }
                          });
                      }
                  });
                  alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.cancel();
                      }
                  });
                  alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                      @Override
                      public void onCancel(DialogInterface dialogInterface) {
                          orderstatusspinner.setSelection(previous);
                      }
                  });
                  alertDialog.show();
              }
          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {
          }
      });
      orderheading.setText(context.getResources().getString(R.string.orderstatustitle_for_retailer));
      String orderedno =" ";
      int iterate = 4-String.valueOf(position+1).length();
      for(int i=0;i<iterate;i++)
          orderedno +='0';
          orderno.setText(orderedno + String.valueOf(position + 1));
        ordername.setText(orderStatusAccount.getname());
        orderaddress.setText(orderStatusAccount.getaddress());
        ordernumber.setText(orderStatusAccount.getNumber());
        final AnimatedExpandableListView productlist = (AnimatedExpandableListView) layout.findViewById(R.id.productlist);
        OrderProductListAdapter orderProductListAdapter = new OrderProductListAdapter(context);
        orderProductListAdapter.setData(getAllquantity(orderStatusAccount),getAllOrderProducts(orderStatusAccount));
        productlist.setAdapter(orderProductListAdapter);
      totalprice.setText(getAllProductPrice(orderStatusAccount));
      ImageView call = (ImageView) layout.findViewById(R.id.callmobile);
      call.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent callintent =  new Intent(Intent.ACTION_DIAL);
              callintent.setData(Uri.parse("tel:" + ordernumber.getText().toString()));
              context.startActivity(callintent);
          }
      });

      return layout;
  }

    private String getAllProductPrice(OrderStatusAccount orderStatusAccount) {
        Integer totalprice=0;
        List<String> object = orderStatusAccount.getProducts();
        ProductDBhelper productDBhelper = new ProductDBhelper(context);
        for(String i:object)
        {
            String product_object_id = i.substring(0, i.indexOf(",;,"));
            Integer price = productDBhelper.GetProductPrice_From_Object_id(product_object_id);
            totalprice+=price;
        }
        productDBhelper.close();
        return String.valueOf(totalprice);
    }

    private void makeToast(String s) {
        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
    }

    private List<String> getAllquantity(OrderStatusAccount orderStatusAccount) {
        List<String> customerquantity = new ArrayList<>();
        List<String> object = orderStatusAccount.getProducts();
        for(String i:object)
        {
            String product_object_id = i.substring(i.indexOf(",;,") + 3);
            customerquantity.add(product_object_id);
        }
        return customerquantity;
    }

    private List<CustomerProductCard> getAllOrderProducts(OrderStatusAccount orderStatusAccount) {
        List<CustomerProductCard> customerProductCards = new ArrayList<>();
        List<String> object = orderStatusAccount.getProducts();
        ProductDBhelper productDBhelper = new ProductDBhelper(context);
        for(String i:object)
        {
            String product_object_id = i.substring(0, i.indexOf(",;,"));
            CustomerProductCard customerProductCard = productDBhelper.GetCustomerProductCard_From_Object_id(product_object_id);
            if(customerProductCard!=null)
                customerProductCards.add(customerProductCard);
        }
        productDBhelper.close();
        return customerProductCards;
    }
}
