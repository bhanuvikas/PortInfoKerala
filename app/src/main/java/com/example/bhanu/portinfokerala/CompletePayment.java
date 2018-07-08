package com.example.bhanu.portinfokerala;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CompletePayment extends AppCompatActivity {

    RecyclerView recyclerView;

    String phone_no, method;
    String booking_id;

    ArrayList<DialogFragmentDetails> dfDetails = new ArrayList<>();
    ArrayList<BookingDetailsCard> bdcDetails = new ArrayList<>();

    CompletePaymentAdapter completePaymentAdapter;
    String merchantResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Complete Payment");
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
        phone_no = prefs.getString("phone_number", "UNKNOWN");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        method = "getCompletePaymentDetails";

        FetchCompletePaymentDetails fetchCompletePaymentDetails = new FetchCompletePaymentDetails();
        fetchCompletePaymentDetails.execute(method);

        //setting adapter to recyclerview
        recyclerView.setAdapter(completePaymentAdapter);


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

                SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(CompletePayment.this, MainActivity.class);
                startActivity(intent);

                Toast.makeText(CompletePayment.this, "Successfully Logged Out!!", Toast.LENGTH_SHORT).show();


                break;

        }
        return true;
    }


    public class FetchCompletePaymentDetails extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CompletePayment.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }



        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String completePaymentURL = "http://192.168.43.218/portinfo/getCompletePaymentDetails.php";
            Log.e("IndoInBackgroundTask", "outside");
            Log.e("IndoInBackgroundTask", method);
            if(method.equals("getCompletePaymentDetails")) {

                try {
                    URL url = new URL(completePaymentURL);
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

            int cnt, temp;
            try {
                JSONObject root = new JSONObject(response);
                JSONArray result = root.getJSONArray("result");
                cnt = 0;
                while(cnt<result.length()) {
                    JSONObject row = result.getJSONObject(cnt);
                    DialogFragmentDetails record = new DialogFragmentDetails();
                    BookingDetailsCard record2 = new BookingDetailsCard();
                    record.index = cnt;
                    record2.index = cnt;
                    record.booking_id = row.getString("customer_booking_id");
                    record2.booking_id = row.getString("customer_booking_id");
                    record.port_name = row.getString("port_name");
                    record.zone_name = row.getString("zone_name");
                    record.request_method = row.getString("customer_booking_request_status");
                    record.quantity = row.getString("customer_booking_request_ton");
                    record2.booking_quantity = row.getString("customer_booking_request_ton");
                    record.date = row.getString("customer_booking_requested_timestamp");
                    record2.booking_date = row.getString("customer_booking_requested_timestamp");
                    record.distance = row.getString("customer_booking_distance");
                    record.request_status = row.getString("customer_booking_request_status");
                    record.current_status = row.getString("customer_booking_current_status");
                    record2.status = row.getString("customer_booking_current_status");

                    if(record.current_status.equals("1")) record.current_status = "Booking Successfull";
                    else if(record.request_status.equals("0")) record.current_status = "Payment Pending";
                    else record.current_status = "Booking Failed";

                    if(record.request_status.equals("1")) record.request_method = "Online Booking";
                    else record.request_method = "Pay Later";

                    record2.booking_request_method = record.request_method;

                    dfDetails.add(record);
                    bdcDetails.add(record2);
                    cnt++;
                }

                Log.e("bdcDetails", String.valueOf(bdcDetails.size()));

                recyclerView.setAdapter(new CompletePaymentAdapter(bdcDetails, new CompletePaymentAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BookingDetailsCard item) {

                        int index;
                        index = item.index;
                        booking_id = item.booking_id;
                        String booking_id = item.booking_id;
                        String port_name = dfDetails.get(index).port_name;
                        String zone_name = dfDetails.get(index).zone_name;
                        String request_ton = item.booking_quantity;
                        String time = item.booking_date;
                        String route = dfDetails.get(index).route;
                        String distance = dfDetails.get(index).distance;
                        String request_status = dfDetails.get(index).request_status;
                        String current_status = dfDetails.get(index).current_status;

                        CompletePaymentDialog completePaymentDialog = new CompletePaymentDialog();

                        Bundle data = new Bundle();//Use bundle to pass data
                        data.putString("id", booking_id);//put string, int, etc in bundle with a key value
                        data.putString("port", port_name);
                        data.putString("zone", zone_name);
                        data.putString("quantity", request_ton);
                        data.putString("time", time);
                        data.putString("route", route);
                        data.putString("distance", distance);
                        data.putString("request_status", request_status);
                        data.putString("current_status", current_status);

                        completePaymentDialog.setArguments(data);//Finally set argument bundle to fragment

                        completePaymentDialog.show(getFragmentManager(), "CompletePaymentDialog");

                    }
                }));



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("in onActivityResult", "of CompletePayment");

        // Result Code is -1 send from Payumoney activity
        Log.d("onActivityResult", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);


            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){
                    //Successfull Transaction
                    Toast.makeText(CompletePayment.this, "Payment Successfull!!!",
                            Toast.LENGTH_LONG).show();
                } else{
                    //Failure Transaction
                    Toast.makeText(CompletePayment.this, "Payment Failure!!!",
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



                    UpdateCompletePayment updateCompletePayment = new UpdateCompletePayment();
                    updateCompletePayment.execute(phone_no, booking_id);




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

    private class UpdateCompletePayment extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(CompletePayment.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }

        @Override
        protected String doInBackground(String... postParams) {
            String phone_no = postParams[0];
            String booking_id = postParams[1];

            String data = null;
            try {
                data = URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&" +
                        URLEncoder.encode("booking_id", "UTF-8") + "=" + URLEncoder.encode(booking_id, "UTF-8");

                URL writeURL = new URL("http://192.168.43.218/portinfo/updateCompletePayment.php");
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

            Intent toCustomerHome = new Intent(CompletePayment.this, CustomerHome.class);
            toCustomerHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            toCustomerHome.putExtra("back from booking", true);
            startActivity(toCustomerHome);

        }
    }

}
