package com.example.bhanu.portinfokerala;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SpotBookingConfirmation extends AppCompatActivity {


    private  PayUmoneySdkInitializer.PaymentParam paymentParam;
    public double price;
    String key = "vupGJOnU";
    String salt = "d3sTxYdWZn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_booking_confirmation);


        Log.d("Entering Confirmation", "Entered");
        Intent fromSpotBooking = getIntent();
        final SpotBookingDetails details = (SpotBookingDetails) fromSpotBooking.getSerializableExtra("spot_booking_details");

        TextView port_confirmation_tv = (TextView) findViewById(R.id.port_name_confirmation);
        TextView zone_confirmation_tv = (TextView) findViewById(R.id.zone_name_confirmation);
        TextView quantity_confirmation_tv = (TextView) findViewById(R.id.quantity_confirmation);
        TextView origin_confirmation = (TextView) findViewById(R.id.origin_confirmation);
        TextView destination_confirmation = (TextView) findViewById(R.id.destination_confirmation);
        TextView distane_confirmation = (TextView) findViewById(R.id.distance_confirmation);
        TextView time_confirmation = (TextView) findViewById(R.id.time_confirmation);
        TextView name_confirmation = (TextView) findViewById(R.id.spot_name_tv);
        TextView aadhar_confirmation = (TextView) findViewById(R.id.aadhar_no_tv);
        TextView phone_confirmation = (TextView) findViewById(R.id.phone_no_tv);

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


        //confirming booking details and so displaying payment options
        final LinearLayout ll = (LinearLayout) findViewById(R.id.invisible_ll);
        final Button confirm_booking_btn = (Button)findViewById(R.id.final_booking_confirm_btn);
        confirm_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_booking_btn.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
            }
        });


        //proceeding after displaying payment options
        Button final_proceed_btn = (Button)findViewById(R.id.final_proceed_btn);
        RadioGroup payment_decision_radio = (RadioGroup) findViewById(R.id.payment_decision_radio);
        final RadioButton proceed_to_payment = (RadioButton) findViewById(R.id.proceed_to_payment);
        final RadioButton pay_later = (RadioButton) findViewById(R.id.pay_later);

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
                        builder = new AlertDialog.Builder(SpotBookingConfirmation.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(SpotBookingConfirmation.this);
                    }
                    builder.setTitle("Booking successful")
                            .setMessage("You have booked sand successfully and you can pay later through complete payment option in your dashboard")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent toCustomerHome = new Intent(SpotBookingConfirmation.this, CustomerHome.class);
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
                }
                else
                {
                    Toast.makeText(SpotBookingConfirmation.this, "Please select one of the options to proceed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

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
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";

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
}
