package com.online.shopping.buyme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.online.shopping.buyme.Cognalys.CheckNetworkConnection;
import com.online.shopping.buyme.Cognalys.VerifyMobile;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.ParseAccount.CustomerAccount;
import com.online.shopping.buyme.ParseAccount.RetailerAccount;
import com.online.shopping.buyme.RegisterUser.CustomerAccess;
import com.online.shopping.buyme.RegisterUser.RetailerAccess;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.ConnectionDetector;
import com.online.shopping.buyme.common.CustomeActivity;
import java.util.Random;

public class LoginAndSignup extends CustomeActivity implements View.OnClickListener {
    TextView Head;
    EditText loginusername,loginpassword;
    boolean Verification_for_dialog;
    TextView login;
    UtilityFunction utility;
    private Dialog forgot_password_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        final ConnectionDetector connectionDetector =new ConnectionDetector(this);
        if(!connectionDetector.isConnectingToInternet())
        {
            showDialog();
            return;
        }
        setAnimation();
        setUpListeners();
        setUpHeadName();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(smsread);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setAnimation() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LinearLayout loginbodytop = (LinearLayout)findViewById(R.id.login_body_top);
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fadeanim);
        loginbodytop.startAnimation(fade);
    }

    private void setUpHeadName() {
        if(ConfigData.isCustomer)
            Head.setText(getResources().getString(R.string.customer));
        else
            Head.setText(getResources().getString(R.string.retailer));
    }


    private void setUpListeners() {
        registerReceiver(smsread,new IntentFilter("SmS_Read"));
        utility = new UtilityFunction(this);
        Head = (TextView) findViewById(R.id.loginname);
        loginusername = (EditText) findViewById(R.id.loginusername);
        loginpassword = (EditText) findViewById(R.id.loginpassword);
        login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(this);
        TextView forgotpassword =  (TextView) findViewById(R.id.forgotpassword);
        forgotpassword.setOnClickListener(this);
    }

    private void clearBoxes() {
        loginusername.setText("");
        loginpassword.setText("");
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.login:
                if(!isSigninValidated())
                    return;
                    Login();
                break;
            case R.id.forgotpassword:
                if(!isForogotValidated())
                    return;
                Verification_for_dialog = true;
                Do_Verification();
                break;
        }
    }

    public void Do_Signup()
    {
        Verification_for_dialog = false;
        Do_Verification();
    }

    private void Do_Verification() {
        String mobile = "91"+ loginusername.getText().toString();
        Intent in = new Intent(LoginAndSignup.this, VerifyMobile.class);
        in.putExtra("app_id", "67271ee7ad6742febdcc2e1");
        in.putExtra("access_token","8a368ce249d5e89b663f5ecc84d480a6a4a405c5");
        in.putExtra("mobile", mobile);
        if (CheckNetworkConnection.isConnectionAvailable(getApplicationContext())) {
            startActivityForResult(in, VerifyMobile.REQUEST_CODE);
        } else {
            Toast.makeText(getApplicationContext(),"no internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isForogotValidated() {
        if(loginusername.getText().toString().length()!=10)
        {
            utility.SanckMessage("Please enter the Mobile Number");
            return false;
        }
        return true;
    }

    private void showForgotPassword() {
        forgot_password_dialog= new Dialog(this);
        forgot_password_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        forgot_password_dialog.setContentView(R.layout.dialog_changepassword);
        TextView ok = (TextView)forgot_password_dialog.findViewById(R.id.Change_password_ok);
        final TextView cancel = (TextView)forgot_password_dialog.findViewById(R.id.Change_password_cancel);
        final EditText newpass = (EditText) forgot_password_dialog.findViewById(R.id.newpassword);
        final EditText confirmnew = (EditText) forgot_password_dialog.findViewById(R.id.verifypassword);
        forgot_password_dialog.findViewById(R.id.verification_layout).setVisibility(View.GONE);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newpass.getText().toString().length() < 6) {
                    utility.SanckMessage("Enter New Password More Than 6 characters");
                    return;
                }
                if (!newpass.getText().toString().equals(confirmnew.getText().toString())) {
                    utility.SanckMessage("Password Doesnt Match");
                    return;
                }
                showprogess();
                if (ConfigData.isCustomer) {
                    ParseQuery<CustomerAccount> query = ParseQuery.getQuery("CustomerAccount");
                    query.whereEqualTo("Username", loginusername.getText().toString());
                    query.getFirstInBackground(new GetCallback<CustomerAccount>() {
                        @Override
                        public void done(CustomerAccount customerAccount, ParseException e) {
                            if (e != null) {
                                stopprogress();
                                utility.SanckMessage(getResources().getString(R.string.networkerror));
                                return;
                            }
                            if (customerAccount == null) {
                                stopprogress();
                                utility.SanckMessage(getResources().getString(R.string.mobilenumber_doesnt_exist));
                                return;
                            }
                            customerAccount.setpassword(newpass.getText().toString());
                            customerAccount.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    stopprogress();
                                    if (e != null) {
                                        utility.SanckMessage(getResources().getString(R.string.networkerror));
                                        return;
                                    }
                                    forgot_password_dialog.dismiss();
                                    utility.SanckMessage(getResources().getString(R.string.passwordsuccess));
                                }
                            });
                        }
                    });
                } else {
                    ParseQuery<RetailerAccount> query = ParseQuery.getQuery("RetailerAccount");
                    query.whereEqualTo("Username", loginusername.getText().toString());
                    query.getFirstInBackground(new GetCallback<RetailerAccount>() {
                        @Override
                        public void done(RetailerAccount customerAccount, ParseException e) {
                            if (e != null) {
                                stopprogress();
                                utility.SanckMessage(getResources().getString(R.string.networkerror));
                                return;
                            }
                            if (customerAccount == null) {
                                stopprogress();
                                utility.SanckMessage(getResources().getString(R.string.mobilenumber_doesnt_exist));
                                return;
                            }
                            customerAccount.setpassword(newpass.getText().toString());
                            customerAccount.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    stopprogress();
                                    if (e != null) {
                                        utility.SanckMessage(getResources().getString(R.string.networkerror));
                                        return;
                                    }
                                    forgot_password_dialog.dismiss();
                                    utility.SanckMessage(getResources().getString(R.string.passwordsuccess));
                                }
                            });
                        }
                    });
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgot_password_dialog.dismiss();
            }
        });
        forgot_password_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        forgot_password_dialog.show();
    }

    BroadcastReceiver smsread= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Verification_for_dialog)
                showForgotPassword();
            else
                Signup();
        }
    };

    private void Login() {
        showprogess();
        if(!ConfigData.isCustomer) {
            RetailerAccess temp = new RetailerAccess();
            temp.login(loginusername.getText().toString(), loginpassword.getText().toString(), utility);
        }
        else {
            CustomerAccess temp = new CustomerAccess();
            temp.login(loginusername.getText().toString(), loginpassword.getText().toString(), utility);
        }
    }

    private boolean isSigninValidated() {
        if(loginusername.getText().toString().length()!=10)
        {
            utility.SanckMessage(getResources().getString(R.string.mobilenumber_invalid));
            return false;
        }
        if(loginpassword.getText().toString().length() ==0)
        {
            utility.SanckMessage("Enter Password");
            return false;
        }
        return true;
    }

    private void Signup() {
        showprogess();
        if (!ConfigData.isCustomer) {
            RetailerAccess temp = new RetailerAccess();
            temp.signup(loginusername.getText().toString(), loginpassword.getText().toString(), utility);
        } else {
            CustomerAccess temp = new CustomerAccess();
            temp.signup(loginusername.getText().toString(), loginpassword.getText().toString(), utility);
        }
        clearBoxes();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);

        if (arg0 == VerifyMobile.REQUEST_CODE) {
            String message = arg2.getStringExtra("message");
            //int result = arg2.getIntExtra("result", 0);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}