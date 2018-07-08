package com.example.bhanu.portinfokerala;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpotBookingConfirmation extends AppCompatActivity {


    private  PayUmoneySdkInitializer.PaymentParam paymentParam;
    public double price;
    String key = "vupGJOnU";
    String salt = "d3sTxYdWZn";
    SpotBookingDetails details;
    String merchantResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_booking_confirmation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Booking Confirmation");
        setSupportActionBar(toolbar);


        Log.d("Entering Confirmation", "Entered");
        Intent fromSpotBooking = getIntent();
        details = (SpotBookingDetails) fromSpotBooking.getSerializableExtra("spot_booking_details");

        TextView port_confirmation_tv = findViewById(R.id.port_name_confirmation);
        TextView zone_confirmation_tv = findViewById(R.id.zone_name_confirmation);
        TextView quantity_confirmation_tv = findViewById(R.id.quantity_confirmation);
        TextView origin_confirmation = findViewById(R.id.origin_confirmation);
        TextView destination_confirmation = findViewById(R.id.destination_confirmation);
        TextView distane_confirmation = findViewById(R.id.distance_confirmation);
        TextView time_confirmation = findViewById(R.id.time_confirmation);
        final TextView name_confirmation = findViewById(R.id.spot_name_tv);
        final TextView aadhar_confirmation = findViewById(R.id.aadhar_no_tv);
        final TextView phone_confirmation = findViewById(R.id.phone_no_tv);

        port_confirmation_tv.setText(details.portName);
        zone_confirmation_tv.setText(details.zoneName);
        quantity_confirmation_tv.setText(details.quantity);
        origin_confirmation.setText(details.origin);
        destination_confirmation.setText(details.destination);
        distane_confirmation.setText(details.distance);
        time_confirmation.setText(details.time);
        name_confirmation.setText(details.name);
        aadhar_confirmation.setText(details.aadharNumber);
        phone_confirmation.setText(details.phoneNumber);

        checkPermissions();

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
        final RadioButton pay_offline = findViewById(R.id.pay_offline);

        final_proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(proceed_to_payment.isChecked())
                {
                    price = (double) Character.getNumericValue(details.quantity.charAt(0));
                    launchPayUMoney();
                }
                else if(pay_offline.isChecked())
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(SpotBookingConfirmation.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(SpotBookingConfirmation.this);
                    }
                    builder.setTitle("Booking successful")
                            .setMessage("You can use the challan which is downloading for offline payment. Check it in your downloads!!!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent toCustomerHome = new Intent(SpotBookingConfirmation.this, CustomerHome.class);
                                    toCustomerHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(toCustomerHome);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();



                    GetChallanFromServerTask getChallanFromServerTask = new GetChallanFromServerTask();

                    getChallanFromServerTask.execute();


                }
                else
                {
                    Toast.makeText(SpotBookingConfirmation.this, "Please select one of the options to proceed",
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
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                break;
        }
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
        String udf3 = details.aadharNumber;
        String udf4 = details.zoneName;
        String udf5 = details.distance;

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
        SpotBookingConfirmation.GetHashesFromServerTask getHashesFromServerTask = new SpotBookingConfirmation.GetHashesFromServerTask();
        getHashesFromServerTask.execute(txnId, amount, productName, firstName, email, "customer", udf1, udf2, udf3, udf4, udf5);
    }


    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SpotBookingConfirmation.this);
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
                String user_credentials = postParams[5];
                String udf1 = postParams[6];
                String udf2 = postParams[7];
                String udf3 = postParams[8];
                String udf4 = postParams[9];
                String udf5 = postParams[10];

                String data = URLEncoder.encode("txnid", "UTF-8") + "=" + URLEncoder.encode(txnid, "UTF-8");
                data += "&" + URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8");
                data += "&" + URLEncoder.encode("productinfo", "UTF-8") + "=" + URLEncoder.encode(productinfo, "UTF-8");
                data += "&" + URLEncoder.encode("firstname", "UTF-8") + "=" + URLEncoder.encode(firstname, "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("user_credentials", "UTF-8") + "=" + URLEncoder.encode(user_credentials, "UTF-8");
                data += "&" + URLEncoder.encode("udf1", "UTF-8") + "=" + URLEncoder.encode(udf1, "UTF-8");
                data += "&" + URLEncoder.encode("udf2", "UTF-8") + "=" + URLEncoder.encode(udf2, "UTF-8");
                data += "&" + URLEncoder.encode("udf3", "UTF-8") + "=" + URLEncoder.encode(udf3, "UTF-8");
                data += "&" + URLEncoder.encode("udf4", "UTF-8") + "=" + URLEncoder.encode(udf4, "UTF-8");
                data += "&" + URLEncoder.encode("udf5", "UTF-8") + "=" + URLEncoder.encode(udf5, "UTF-8");

                URL url = new URL("http://192.168.43.218/portinfo/getHashCode.php");
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

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
                Toast.makeText(SpotBookingConfirmation.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            }
            else {
                paymentParam.setMerchantHash(merchantHash);
                PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, SpotBookingConfirmation.this, R.style.AppTheme_default, false);
                //Toast.makeText(BookingConfirmationActivity.this, "Hash generated succesfully", Toast.LENGTH_SHORT).show();
            }
        }



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
                    Toast.makeText(SpotBookingConfirmation.this, "Payment Successfull!!!",
                            Toast.LENGTH_LONG).show();
                } else{
                    //Failure Transaction
                    Toast.makeText(SpotBookingConfirmation.this, "Payment Failure!!!",
                            Toast.LENGTH_LONG).show();
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();
                try {
                    JSONObject response = new JSONObject(payuResponse);
                    JSONObject result = response.getJSONObject("result");
                    String name = result.getString("firstname");
                    String aadhar = result.getString("udf3");      //
                    String phone = result.getString("phone");
                    String quantity = result.getString("amount");    //
                    String destination = result.getString("udf1"); //
                    String route = "Origin to Destination";       //
                    String distance = result.getString("udf5");   //
                    String origin = result.getString("udf2");      //
                    String challan = "challan";
                    String amount = result.getString("amount");
                    String ip_addr = "192.16.123.123";
                    String zone = result.getString("udf4");    //
                    String status;
                    if(result.getString("status").equals("success")) {
                        status = "1";
                    } else status = "2";

                    WriteDetailsForSpotBooking writeDetailsForSpotBooking = new WriteDetailsForSpotBooking();
                    writeDetailsForSpotBooking.execute(name, aadhar, phone, quantity, destination, route,  distance, origin, challan, amount, ip_addr, zone, status );





                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("PayUResponse: ", payuResponse);
                // Response from SURl and FURL
                merchantResponse = transactionResponse.getTransactionDetails();
                Log.e("TransationDetails", merchantResponse);
            }  else if (resultModel != null && resultModel.getError() != null) {
                Log.e("Error response : " ,  resultModel.getError().getTransactionResponse().toString());
            } else {
                Log.e("Both objects are null!", " ");
            }


        }
    }



    private class GetChallanFromServerTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(SpotBookingConfirmation.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }

        @Override
        protected String doInBackground(String... postParams) {

            String challanURL = "http://192.168.43.218/portinfo/challan.pdf";
            Log.e("IndoInBackgroundTask", "outside");
            try {
                URL url = new URL(challanURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                String PATH = android.os.Environment.getExternalStorageDirectory() + "/Download/";
                File file = new File(PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File outputFile = new File(file, "challan.pdf");


                FileOutputStream fo = new FileOutputStream(outputFile);
                InputStream is = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fo.write(buffer, 0, len1);
                }
                fo.flush();
                fo.close();
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


            return "";


        }

        @Override
        protected void onPostExecute(String response) {

            super.onPostExecute(response);
            super.onPostExecute(response);
            progressDialog.dismiss();
            Log.e("InpostExecute response:", response);
            WriteDetailsForSpotBooking writeDetailsForSpotBooking = new WriteDetailsForSpotBooking();
            writeDetailsForSpotBooking.execute(details.name, details.aadharNumber, details.phoneNumber, details.quantity, details.destination, "Go Straight and take a dive into hell",  details.distance, details.origin, "You want a challan", details.quantity, "192.16.123.123", details.zoneName, "0" );

        }
    }

    private class WriteDetailsForSpotBooking extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(SpotBookingConfirmation.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }

        @Override
        protected String doInBackground(String... postParams) {
            String name = postParams[0];
            String aadhar = postParams[1];
            String phone_no = postParams[2];
            String quantity = postParams[3];
            String destination = postParams[4];
            String route = postParams[5];
            String distance = postParams[6];
            String origin = postParams[7];
            String challan = postParams[8];
            String amount = postParams[9];
            String ip_addr = postParams[10];
            String zone = postParams[11];
            String status = postParams[12];

            String data = null;
            try {
                data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("aadhar", "UTF-8") + "=" + URLEncoder.encode(aadhar, "UTF-8") + "&" +
                        URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&" +
                        URLEncoder.encode("quantity", "UTF-8") + "=" + URLEncoder.encode(quantity, "UTF-8") + "&" +
                        URLEncoder.encode("destination", "UTF-8") + "=" + URLEncoder.encode(destination, "UTF-8") + "&" +
                        URLEncoder.encode("route", "UTF-8") + "=" + URLEncoder.encode(route, "UTF-8") + "&" +
                        URLEncoder.encode("distance", "UTF-8") + "=" + URLEncoder.encode(distance, "UTF-8") + "&" +
                        URLEncoder.encode("origin", "UTF-8") + "=" + URLEncoder.encode(origin, "UTF-8") + "&" +
                        URLEncoder.encode("challan", "UTF-8") + "=" + URLEncoder.encode(challan, "UTF-8") + "&" +
                        URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8") + "&" +
                        URLEncoder.encode("ip_addr", "UTF-8") + "=" + URLEncoder.encode(ip_addr, "UTF-8") + "&" +
                        URLEncoder.encode("zone", "UTF-8") + "=" + URLEncoder.encode(zone, "UTF-8") + "&" +
                        URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");

                URL writeURL = new URL("http://192.168.43.218/portinfo/writeDetailsForSpotBooking.php");
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

            String phone_no = "";
            SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
            phone_no = prefs.getString("phone_number", "UNKNOWN");

            UpdateSpotBookingBalances updateSpotBookingBalances = new UpdateSpotBookingBalances();
            updateSpotBookingBalances.execute(details.portName, details.zoneName, details.quantity);


        }


    }


    private class UpdateSpotBookingBalances extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(SpotBookingConfirmation.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }

        @Override
        protected String doInBackground(String... postParams) {
            String port_name = postParams[0];
            String zone_name = postParams[1];
            String quantity = postParams[2];

            String data = null;
            try {
                data = URLEncoder.encode("port_name", "UTF-8") + "=" + URLEncoder.encode(port_name, "UTF-8") + "&" +
                        URLEncoder.encode("zone_name", "UTF-8") + "=" + URLEncoder.encode(zone_name, "UTF-8") + "&" +
                        URLEncoder.encode("quantity", "UTF-8") + "=" + URLEncoder.encode(quantity, "UTF-8");

                URL writeURL = new URL("http://192.168.43.218/portinfo/updateSpotBookingBalances.php");
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

            if(response.equals("updated successfully")) {
                Log.e("Sand Balance", "updated succesfully");
            } else {
                Log.e("Sand Balance", "failed to update");
            }

            String phone_no = "";
            SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
            phone_no = prefs.getString("phone_number", "UNKNOWN");

            if(phone_no.isEmpty() || phone_no.equals("UNKNOWN")) {
                Intent toMainActivity = new Intent(SpotBookingConfirmation.this, MainActivity.class);
                toMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toMainActivity);
            } else {

                Intent toCustomerHome = new Intent(SpotBookingConfirmation.this, CustomerHome.class);
                toCustomerHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                toCustomerHome.putExtra("back from booking", true);
                startActivity(toCustomerHome);
            }




        }


    }
}


