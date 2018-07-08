package com.example.bhanu.portinfokerala;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static android.app.Activity.RESULT_OK;

public class CompletePaymentDialog extends DialogFragment {

    private  PayUmoneySdkInitializer.PaymentParam paymentParam;

    Button cancel_btn, complete_payment_btn;
    TextView booking_id, port_name, zone_name, quantity, time, status, booking_method;



    String id;
    String port;
    String zone;
    String request_ton;
    String booking_time;
    String route;
    String distance;
    String request_status;
    String current_status;
    String phone_no;
    Double price;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_complete_payment, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("portinfo", Context.MODE_PRIVATE);
        phone_no = prefs.getString("phone_number", "UNKNOWN");

        cancel_btn = view.findViewById(R.id.cancel_btn);
        complete_payment_btn = view.findViewById(R.id.complete_payment_btn);
        booking_id = view.findViewById(R.id.booking_id);
        port_name = view.findViewById(R.id.port_name);
        zone_name = view.findViewById(R.id.zone_name);
        quantity = view.findViewById(R.id.quantity);
        time = view.findViewById(R.id.time);
        status = view.findViewById(R.id.status);
        booking_method = view.findViewById(R.id.booking_method);

        //Get Argument that passed from activity in "data" key value
        String getArgument = getArguments().getString("data");

        id = getArguments().getString("id");
        port = getArguments().getString("port");
        zone = getArguments().getString("zone");
        request_ton = getArguments().getString("quantity");
        price = Double.parseDouble(request_ton);
        request_ton += " tons";
        booking_time = getArguments().getString("time");
        route = getArguments().getString("route");
        distance = getArguments().getString("distance");
        request_status = getArguments().getString("request_status");
        current_status = getArguments().getString("current_status");

        if(request_status.equals("0")) request_status = "Pay Later";
        else request_status = "online";

        booking_id.setText(id);
        port_name.setText(port);
        zone_name.setText(zone);
        quantity.setText(request_ton);
        time.setText(booking_time);
        status.setText(request_status);
        booking_method.setText(current_status);


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        complete_payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPayUMoney();
            }
        });

        return view;
    }

    private void launchPayUMoney() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Done");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Test Account");

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        String txnId = System.currentTimeMillis() + "";
        String phone = phone_no;
        String productName = "Sand";
        String firstName = "Bhanu vikas";
        String email = "yagantibhanuvikas@gmail.com";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";

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
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        String amount = Double.toString(price);
//        String merchantHash = hashCal("SHA-512", hashSequence);

        //paymentParam.setMerchantHash(merchantHash);
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(txnId, amount, productName, firstName, email, "customer", udf1, udf2, udf3, udf4, udf5);
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
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
                Toast.makeText(getActivity().getApplicationContext(), "Could not generate hash", Toast.LENGTH_SHORT).show();
            }
            else {

                Log.e("context checking", getActivity().toString());
                paymentParam.setMerchantHash(merchantHash);
                getDialog().dismiss();
                PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, getActivity(), R.style.AppTheme_default, false);
                //Toast.makeText(BookingConfirmationActivity.this, "Hash generated succesfully", Toast.LENGTH_SHORT).show();
            }
        }





    }






}
