package com.example.bhanu.portinfokerala;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import java.util.List;

public class BookingHistory extends AppCompatActivity {

    RecyclerView recyclerView;

    String phone_no, method;

    ArrayList<DialogFragmentDetails> dfDetails = new ArrayList<>();
    ArrayList<BookingDetailsCard> bdcDetails = new ArrayList<>();

    BookingDetailsAdapter bookingDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
        phone_no = prefs.getString("phone_number", "UNKNOWN");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        method = "getBookingHistoryDetails";

        FetchBookingHistoryDetails fetchBookingHistoryDetails = new FetchBookingHistoryDetails();
        fetchBookingHistoryDetails.execute(method);

        //setting adapter to recyclerview
        recyclerView.setAdapter(bookingDetailsAdapter);

    }


    public class FetchBookingHistoryDetails extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(BookingHistory.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }



        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String historyURL = "http://192.168.43.218/portinfo/getBookingHistoryDetails.php";
            Log.e("IndoInBackgroundTask", "outside");
            Log.e("IndoInBackgroundTask", method);
            if(method.equals("getBookingHistoryDetails")) {

                try {
                    URL url = new URL(historyURL);
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

                recyclerView.setAdapter(new BookingDetailsAdapter(bdcDetails, new BookingDetailsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BookingDetailsCard item) {

                        int index;
                        index = item.index;
                        String booking_id = item.booking_id;
                        String port_name = dfDetails.get(index).port_name;
                        String zone_name = dfDetails.get(index).zone_name;
                        String request_ton = item.booking_quantity;
                        String time = item.booking_date;
                        String route = dfDetails.get(index).route;
                        String distance = dfDetails.get(index).distance;
                        String request_status = dfDetails.get(index).request_status;
                        String current_status = dfDetails.get(index).current_status;

                        BookingHistoryDialog bookingHistoryDialog = new BookingHistoryDialog();

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

                        bookingHistoryDialog.setArguments(data);//Finally set argument bundle to fragment

                        bookingHistoryDialog.show(getFragmentManager(), "BookingHistoryDialog");

                    }
                }));



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
