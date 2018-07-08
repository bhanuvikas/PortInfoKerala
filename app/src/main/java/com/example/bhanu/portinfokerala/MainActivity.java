package com.example.bhanu.portinfokerala;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.widget.ViewSwitcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    ViewSwitcher viewSwitcher;
    TextInputEditText phone_et;
    TextInputEditText otp_et;
    String otp;
    String phone_no;
    String method;
    public String status;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("PortInfo Kerala");
        setSupportActionBar(toolbar);



        SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
        phone_no = prefs.getString("phone_number", "");
        if(!phone_no.isEmpty()) {
            Intent toCustomerHome = new Intent(MainActivity.this, CustomerHome.class);
            toCustomerHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(toCustomerHome);
        }

        context = this;
        phone_et = findViewById(R.id.phone_no_et);

        Button request_otp_btn = findViewById(R.id.request_otp_btn);
        request_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "";
                phone_no = phone_et.getText().toString();
                if(phone_no.length()==10) {
                    method = "phoneNumberValidation";
                    BackgroundTask backgroundTask = new BackgroundTask();
                    backgroundTask.execute(method, phone_no);
                    Log.e("inonclick method status", status);

                    /*viewSwitcher = findViewById(R.id.view_switcher);
                    viewSwitcher.showNext();*/
                } else {
                    phone_et.setError("Number should be exactly of 10 digits");
                }

            }
        });

        Button login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp_et = findViewById(R.id.otp_et);
                otp = otp_et.getText().toString();
                if(otp.equals("0000")) {

                    SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("phone_number", phone_no);
                    editor.apply();

                    Intent toCustomerHome = new Intent(MainActivity.this, CustomerHome.class);
                    toCustomerHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(toCustomerHome);

                    Toast.makeText(MainActivity.this, "Login Success!!", Toast.LENGTH_SHORT).show();
                } else {
                    otp_et.setError("Wrong OTP entered!!");

                }
            }
        });


        //Customer Registration
        Button reg_btn = findViewById(R.id.reg_btn);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Registration not available!!")
                        .setMessage("Please visit portinfo kerala website for customer registration")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


                /*Uri uri = Uri.parse("http://portinfo.kerala.gov.in/index.php/Master/customerregistration_add");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                finish();*/
            }
        });

        //Customer Registration
        Button reg_btn2 = findViewById(R.id.reg_btn2);
        reg_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Registration not available!!")
                        .setMessage("Please visit portinfo kerala website for customer registration")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

                /*Uri uri = Uri.parse("http://portinfo.kerala.gov.in/index.php/Master/customerregistration_add");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                finish();*/
            }
        });

        //spot booking

        Button spot_booking_btn = findViewById(R.id.spot_booking_btn);
        spot_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSpotBooking = new Intent(MainActivity.this, SpotBooking.class);
                startActivity(toSpotBooking);
            }
        });

        //spot booking 2

        Button spot_booking_btn2 = findViewById(R.id.spot_booking_btn2);
        spot_booking_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSpotBooking = new Intent(MainActivity.this, SpotBooking.class);
                startActivity(toSpotBooking);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return true;
    }


    Boolean loginPressBack = false;
    Boolean otpPressBack = false;
    @Override
    public void onBackPressed() {
        if(this.findViewById(R.id.phone_no_layout).getVisibility() == View.GONE) {
            Toast.makeText(MainActivity.this, "Please wait for OTP to reach!! or Press back again to exit!!", Toast.LENGTH_SHORT).show();
            if(otpPressBack) {
                viewSwitcher = findViewById(R.id.view_switcher);
                viewSwitcher.showPrevious();
            }

            otpPressBack = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    otpPressBack = false;
                }
            }, 3000);


        }
        else {
            Toast.makeText(MainActivity.this, "Press back again to Exit!!", Toast.LENGTH_SHORT).show();
            if(loginPressBack) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                System.exit(0);
            }

            loginPressBack = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loginPressBack = false;
                }
            }, 3000);

        }
    }


    public class BackgroundTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String phoneURL = "http://192.168.43.218/portinfo/validatePhoneNumber.php";
            Log.e("IndoInBackgroundTask", "outside");
            Log.e("IndoInBackgroundTask", method);
            if(method.equals("phoneNumberValidation")) {
                String phone_no;
                phone_no = params[1];
                try {
                    URL url = new URL(phoneURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    Log.e("IndoInBackgroundTask", "after opening connection");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    Log.e("IndoInBackgroundTask", "before opening stream");
                    OutputStream os = urlConnection.getOutputStream();
                    Log.e("current Background: ", "Output stream opeded");
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    String data = URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    Log.e("IndoInBackgroundTask: ", "Data Written to connection");
                    InputStream is = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    Log.e("IndoInBackgroundTask", "Starting reading response");
                    while ((line = br.readLine())!=null) {
                        sb.append(line);
                    }

                    Log.e("doInBackgroundResponse", sb.toString());
                    return sb.toString();



                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return "";

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            super.onPostExecute(response);
            progressDialog.dismiss();
            Log.e("InpostExecute response:", response);

            status=response;
            Log.e("InpostExecute status:", status);
            if(status.equals("valid")) {
                viewSwitcher = findViewById(R.id.view_switcher);
                viewSwitcher.showNext();
            }
            else {
                phone_et.setError("Number is not registered");
            }
        }

    }

}

