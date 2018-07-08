package com.example.bhanu.portinfokerala;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

public class BookingConfirmationActivity extends AppCompatActivity {


    private  PayUmoneySdkInitializer.PaymentParam paymentParam;
    public double price;
    String key = "vupGJOnU";
    String salt = "d3sTxYdWZn";
    BookingDetails details;
    String phone_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Booking Confirmation");
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
        phone_no = prefs.getString("phone_number", "UNKNOWN");


        Log.d("Entering Confirmation", "Entered");
        Intent fromSandBooking = getIntent();
        details = (BookingDetails) fromSandBooking.getSerializableExtra("booking_details");

        TextView port_confirmation_tv = findViewById(R.id.port_name_confirmation);
        TextView zone_confirmation_tv = findViewById(R.id.zone_name_confirmation);
        TextView quantity_confirmation_tv = findViewById(R.id.quantity_confirmation);
        TextView origin_confirmation = findViewById(R.id.origin_confirmation);
        TextView destination_confirmation = findViewById(R.id.destination_confirmation);
        TextView distane_confirmation = findViewById(R.id.distance_confirmation);
        TextView time_confirmation = findViewById(R.id.time_confirmation);


        port_confirmation_tv.setText(details.portName);
        zone_confirmation_tv.setText(details.zoneName);
        quantity_confirmation_tv.setText(details.quantity);
        origin_confirmation.setText(details.origin);
        destination_confirmation.setText(details.destination);
        distane_confirmation.setText(details.distance);
        time_confirmation.setText(details.time);




        //confirming booking details and so displaying payment options
        final LinearLayout ll = findViewById(R.id.invisible_ll);
        final Button confirm_booking_btn = findViewById(R.id.final_booking_confirm_btn);
        confirm_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_booking_btn.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
            }
        });


        //proceeding after displaying payment options
        Button final_proceed_btn = findViewById(R.id.final_proceed_btn);
        RadioGroup payment_decision_radio = findViewById(R.id.payment_decision_radio);
        final RadioButton proceed_to_payment = findViewById(R.id.proceed_to_payment);
        final RadioButton pay_later = findViewById(R.id.pay_later);

        final_proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(proceed_to_payment.isChecked())
                {
                    price = (double) Character.getNumericValue(details.quantity.charAt(0));
                    launchPayUMoney();
                }
                else if(pay_later.isChecked())
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(BookingConfirmationActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(BookingConfirmationActivity.this);
                    }
                    builder.setTitle("Booking successful")
                            .setMessage("You have booked sand successfully and you can pay later through complete payment option in your dashboard")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String name = "";
                                    String route = details.origin + " to " + details.destination;
                                    String ip_addr = "192.16.123.123";
                                    String request_status = "0";
                                    String current_status = "0";
                                    WriteDetailsForSandBooking writeDetailsForSandBooking = new WriteDetailsForSandBooking();
                                    writeDetailsForSandBooking.execute(name, phone_no, details.quantity, details.destination, route, details.distance, details.origin, details.quantity, ip_addr, details.zoneName, current_status, request_status);

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();




                }
                else
                {
                    Toast.makeText(BookingConfirmationActivity.this, "Please select one of the options to proceed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuLogout:
                Toast.makeText(this, "You clicked logout", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }

    /**
     * This was for testing
     */

    /**
     *    This function will execute an instance of GetHashesFromServerTask and calls payumoney server
     */

/*    String hashSequence = key|txnid|price|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||salt;
    public static String hashCal(String type, String hashString) {
        StringBuilder hash = new StringBuilder();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(type);
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash.toString();
    }*/




    private void launchPayUMoney() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Done");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Test Account");

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        String txnId = System.currentTimeMillis() + "";
        String phone = "9494001965";
        String productName = "Sand";
        String firstName = "Bhanu vikas";
        String email = "yagantibhanuvikas@gmail.com";
        String udf1 = details.destination.replaceAll("[^a-zA-Z]", "");
        String udf2 = details.origin.replaceAll("[^a-zA-Z]", "");
        String udf3 = "";
        String udf4 = details.zoneName;
        String udf5 = details.distance;



        Application application = new Application();
        Environment appEnvironment = application.getEnvironment();
        builder.setAmount(price)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(Environment.TEST.surl())
                .setfUrl(Environment.TEST.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(true)
                .setKey(Environment.TEST.merchant_Key())
                .setMerchantId(Environment.TEST.merchant_ID());


        try {
            paymentParam = builder.build();
            // generateHashFromServer(paymentParam);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        String amount = Double.toString(price);
//        String merchantHash = hashCal("SHA-512", hashSequence);

        //paymentParam.setMerchantHash(merchantHash);
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(txnId, amount, productName, firstName, email, udf1, udf2, udf3, udf4, udf5);
    }


     /**
      * This AsyncTask generates hash from server.
      */
     private class GetHashesFromServerTask extends AsyncTask<String, String, String> {

         private ProgressDialog progressDialog;


         @Override
         protected void onPreExecute() {
             progressDialog = new ProgressDialog(BookingConfirmationActivity.this);
             progressDialog.setMessage("Please wait...");
             progressDialog.show();
         }


         @Override
         protected String doInBackground(String... postParams) {

             String merchantHash = "";
             try {

                 String txnid = postParams[0];
                 String amount = postParams[1];
                 String productinfo = postParams[2];
                 String firstname = postParams[3];
                 String email = postParams[4];
                 String udf1 = postParams[5];
                 String udf2 = postParams[6];
                 String udf3 = postParams[7];
                 String udf4 = postParams[8];
                 String udf5 = postParams[9];

                 String data = URLEncoder.encode("txnid", "UTF-8") + "=" + URLEncoder.encode(txnid, "UTF-8");
                 data += "&" + URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8");
                 data += "&" + URLEncoder.encode("productinfo", "UTF-8") + "=" + URLEncoder.encode(productinfo, "UTF-8");
                 data += "&" + URLEncoder.encode("firstname", "UTF-8") + "=" + URLEncoder.encode(firstname, "UTF-8");
                 data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                 data += "&" + URLEncoder.encode("udf1", "UTF-8") + "=" + URLEncoder.encode(udf1, "UTF-8");
                 data += "&" + URLEncoder.encode("udf2", "UTF-8") + "=" + URLEncoder.encode(udf2, "UTF-8");
                 data += "&" + URLEncoder.encode("udf3", "UTF-8") + "=" + URLEncoder.encode(udf3, "UTF-8");
                 data += "&" + URLEncoder.encode("udf4", "UTF-8") + "=" + URLEncoder.encode(udf4, "UTF-8");
                 data += "&" + URLEncoder.encode("udf5", "UTF-8") + "=" + URLEncoder.encode(udf5, "UTF-8");

                 URL url = new URL("http://192.168.43.218/portinfo/getHashCode.php");
                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                 conn.setDoOutput(true);
                 conn.setRequestMethod("POST");

                 OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                 Log.e("returning response", "cool");

                 wr.write(data);
                 wr.flush();

                 BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                 StringBuilder sb = new StringBuilder();
                 String line = null;

                 // Read Server Response
                 while((line = reader.readLine()) != null) {
                     sb.append(line);
                     break;
                 }
                 return sb.toString();

             } catch (IOException e) {
                 e.printStackTrace();
             }
             Log.e("empty string", "lol");
             return "";

         }


         @Override
         protected void onPostExecute(String response) {

             progressDialog.dismiss();

             String merchantHash = "";

             try {
                 JSONObject jsonObject = null;
                 jsonObject = new JSONObject(response);
                 merchantHash += jsonObject.getString("payment_hash");

             } catch (JSONException e) {
                 e.printStackTrace();
             }

             if(merchantHash.isEmpty() || merchantHash.equals("")) {
                 Toast.makeText(BookingConfirmationActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
             }
             else {
                 paymentParam.setMerchantHash(merchantHash);
                 PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, BookingConfirmationActivity.this, R.style.AppTheme_default, false);
                 //Toast.makeText(BookingConfirmationActivity.this, "Hash generated succesfully", Toast.LENGTH_SHORT).show();
             }
         }


         /*@Override
         protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
             if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
                 if (data != null) {

                     *//**
                      * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                      * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                      *
                      * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                      * for identifying status of transaction. There are two possible status like, success or failure
                      * *//*
                     new AlertDialog.Builder(this)
                             .setCancelable(false)
                             .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                             .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                 public void onClick(DialogInterface dialog, int whichButton) {
                                     dialog.dismiss();
                                 }
                             }).show();

                 } else {
                     Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
                 }
             }
         }*/



     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("onActivityResult", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);


            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){
                    //Successfull Transaction
                    Toast.makeText(BookingConfirmationActivity.this, "Payment Successfull!!!",
                            Toast.LENGTH_LONG).show();
                } else{
                    //Failure Transaction
                    Toast.makeText(BookingConfirmationActivity.this, "Payment Failure!!!",
                            Toast.LENGTH_LONG).show();
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();
                try {
                    JSONObject response = new JSONObject(payuResponse);
                    JSONObject result = response.getJSONObject("result");
                    String name = result.getString("firstname");
                    String phone = result.getString("phone");
                    String quantity = result.getString("amount");    //
                    String destination = result.getString("udf1"); //
                    String origin = result.getString("udf2");      //
                    String route = origin + " to " + destination;       //
                    String distance = result.getString("udf5");   //
                    String amount = result.getString("amount");
                    String ip_addr = "192.16.123.123";
                    String zone = result.getString("udf4");    //
                    String request_status = "1";
                    String current_status;
                    if(result.getString("status").equals("success")) {
                        current_status = "1";
                    } else current_status = "0";

                    BookingConfirmationActivity.WriteDetailsForSandBooking writeDetailsForSandBooking = new BookingConfirmationActivity.WriteDetailsForSandBooking();
                    writeDetailsForSandBooking.execute(name, phone, quantity, destination, route,  distance, origin, amount, ip_addr, zone, current_status, request_status );





                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("PayUResponse: ", payuResponse);
                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                Log.e("TransationDetails", merchantResponse+"");
            }  else if (resultModel != null && resultModel.getError() != null) {
                Log.e("Error response : " ,  resultModel.getError().getTransactionResponse().toString());
            } else {
                Log.e("Both objects are null!", " ");
            }
        }
    }


    private class WriteDetailsForSandBooking extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(BookingConfirmationActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }

        @Override
        protected String doInBackground(String... postParams) {
            String name = postParams[0];
            String phone_no = postParams[1];
            String quantity = postParams[2];
            String destination = postParams[3];
            String route = postParams[4];
            String distance = postParams[5];
            String origin = postParams[6];
            String amount = postParams[7];
            String ip_addr = postParams[8];
            String zone = postParams[9];
            String current_status = postParams[10];
            String request_status = postParams[11];


            String data = null;
            try {
                data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&" +
                        URLEncoder.encode("quantity", "UTF-8") + "=" + URLEncoder.encode(quantity, "UTF-8") + "&" +
                        URLEncoder.encode("destination", "UTF-8") + "=" + URLEncoder.encode(destination, "UTF-8") + "&" +
                        URLEncoder.encode("route", "UTF-8") + "=" + URLEncoder.encode(route, "UTF-8") + "&" +
                        URLEncoder.encode("distance", "UTF-8") + "=" + URLEncoder.encode(distance, "UTF-8") + "&" +
                        URLEncoder.encode("origin", "UTF-8") + "=" + URLEncoder.encode(origin, "UTF-8") + "&" +
                        URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8") + "&" +
                        URLEncoder.encode("ip_addr", "UTF-8") + "=" + URLEncoder.encode(ip_addr, "UTF-8") + "&" +
                        URLEncoder.encode("zone", "UTF-8") + "=" + URLEncoder.encode(zone, "UTF-8") + "&" +
                        URLEncoder.encode("current_status", "UTF-8") + "=" + URLEncoder.encode(current_status, "UTF-8") + "&" +
                        URLEncoder.encode("request_status", "UTF-8") + "=" + URLEncoder.encode(request_status, "UTF-8");

                URL writeURL = new URL("http://192.168.43.218/portinfo/writeDetailsForSandBooking.php");
                HttpURLConnection urlConnection = (HttpURLConnection) writeURL.openConnection();
                Log.e("IndoInBackgroundTask", "after opening connection");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                Log.e("IndoInBackgroundTask", "before opening stream");
                OutputStream os = urlConnection.getOutputStream();
                Log.e("current background: ", "Output stream opeded");
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(data);
                bw.flush();
                bw.close();

                Log.e("inDoINBackGround:", "started reading response");
                InputStream is = urlConnection.getInputStream();
                Log.e("inDoINBackground", "got input stream");
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                Log.e("inDoInBackground", "created br");
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine())!=null) {
                    Log.e("indoinbackground", "in while");
                    sb.append(line);
                }

                Log.e("inDoinbackaoid", "out while");
                Log.e("doInBackgroundResponse", sb.toString());


            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String response) {

            super.onPostExecute(response);
            progressDialog.dismiss();
            Log.e("InpostExecute response:", response);

            Intent toCustomerHome = new Intent(BookingConfirmationActivity.this, CustomerHome.class);
            toCustomerHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            toCustomerHome.putExtra("back from booking", true);
            startActivity(toCustomerHome);
        }
    }






}
