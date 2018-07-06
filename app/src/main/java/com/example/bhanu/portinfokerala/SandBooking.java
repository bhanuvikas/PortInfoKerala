package com.example.bhanu.portinfokerala;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SandBooking extends AppCompatActivity {

    String port_name;
    String zone_name;
    String quantity;
    String distance;
    String time;
    String destinationDetails;
    String originDetails;

//  int PLACE_PICKER_REQUEST_1 = 1;
//  int PLACE_PICKER_REQUEST_2 = 1;

    TextView destination_tv;
    TextView origin_tv;
    TextView distance_tv;
    TextView time_tv;

    Place destination;
    Place origin;

    Location destinationLoc = new Location("");
    Location originLoc = new Location("");

    Boolean flag1 = false;
    Boolean flag2 = false;

    double latitude1 = 0;
    double latitude2 = 0;
    double longitude1 = 0;
    double longitude2 = 0;

    LatLng destinationLatLng;
    LatLng originLatLng;

    String method;

    ArrayList<PortZones> portZones = new ArrayList<>();
    ArrayList<String> ports = new ArrayList<>();
    ArrayList<String> zones = new ArrayList<>();
    String selectedPort = null;
    String selectedZone = null;
    int selectedPostition = 0;
    Spinner portNamesSpinner;
    Spinner zoneNamesSpinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sand_booking);
        destination_tv = (TextView) findViewById(R.id.destination_tv);
        origin_tv = (TextView) findViewById(R.id.origin_tv);
        distance_tv = (TextView) findViewById(R.id.distance_tv);
        time_tv = (TextView) findViewById(R.id.time_tv);
        method = "getPorts";
        portNamesSpinner = (Spinner) findViewById(R.id.port_name_spinner);
        zoneNamesSpinner = (Spinner) findViewById(R.id.zone_name_spinner);
        SandBooking.GetPortsBackgroundTask getPortsBackgroundTask = new GetPortsBackgroundTask();
        getPortsBackgroundTask.execute(method);


        //Quantity Spinner
        final Spinner quantitySpinner = (Spinner) findViewById(R.id.quantity_spinner);
        ArrayAdapter<CharSequence> quantityAdapter = ArrayAdapter.createFromResource(this, R.array.quantity, android.R.layout.simple_spinner_item);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantityAdapter);


        //Destination Selector
        Button destination_btn = (Button) findViewById(R.id.destination_btn);
        destination_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SandBooking.this), 1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        //Origin Selector
        Button origin_btn = (Button) findViewById(R.id.origin_btn);
        origin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SandBooking.this), 2);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        //Distance button
        Button distance_btn = (Button) findViewById(R.id.distance_btn);
        distance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag1 && flag2) {
                    CalculateDistanceTime distance_task = new CalculateDistanceTime(SandBooking.this);
                    distance_task.getDirectionsUrl(destinationLatLng, originLatLng);

                    distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                        @Override
                        public void taskCompleted(String[] time_distance) {
                            time = time_distance[1];
                            distance = time_distance[0];

                            time_tv.setText("" + time_distance[1]);
                            distance_tv.setText("" + time_distance[0]);
                        }

                    });
                }
                else {
                    String message = "First fill destination and origin";
                    distance_tv.setText(message);
                }
            }
        });

        //Confirm Booking Btn
        Button confirm_booking_btn = (Button)findViewById(R.id.confirm_booking_btn);
        confirm_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                port_name = selectedPort;
                zone_name = selectedZone;

                Spinner quantity_spinner = (Spinner)findViewById(R.id.quantity_spinner);
                quantity = quantity_spinner.getSelectedItem().toString();

                if(quantity.equals("Select")) {
                    TextView errorText = (TextView)quantity_spinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Select quantity of sand");//changes the selected item text to this
                    Toast.makeText(SandBooking.this, "Select quantity of Sand", Toast.LENGTH_SHORT).show();
                } else {

                    BookingDetails details = new BookingDetails();
                    details.portName = port_name;
                    details.zoneName = zone_name;
                    details.quantity = quantity;
                    details.destination = destinationDetails;
                    details.origin = originDetails;
                    details.distance = distance;
                    details.time = time;

                    Intent toBookingConfirmationActivity = new Intent(SandBooking.this, BookingConfirmationActivity.class);
                    toBookingConfirmationActivity.putExtra("booking_details", details);
                    startActivity(toBookingConfirmationActivity);
                    Log.d("In Sand booking", "StartActivity passed");
                }

            }
        });

        Log.e("AfterExecute", "OMG are you kidding");
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                destination = PlacePicker.getPlace(this, data);
                destination_tv.setText(destination.getAddress());
                latitude1 = destination.getLatLng().latitude;
                longitude1 = destination.getLatLng().longitude;

                destinationLoc.setLatitude(latitude1);
                destinationLoc.setLongitude(longitude1);

                destinationLatLng = destination.getLatLng();

                destinationDetails = destination.getName().toString();
                //.toString() + ", " + destination.getAddress();

                flag1 = true;
                /*if(flag2) {
                    float distanceTemp = destinationLoc.distanceTo(originLoc);
                    distanceTemp /= 1000;
                    Log.d("distance", ""+distanceTemp);
                    String distance;
                    distance = "Distance: " + distanceTemp;
                    distance_tv.setText(distance);
                }*/
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                origin = PlacePicker.getPlace(this, data);
                origin_tv.setText(origin.getAddress());
                latitude2 = origin.getLatLng().latitude;
                longitude2 = origin.getLatLng().longitude;

                originLoc.setLatitude(latitude2);
                originLoc.setLongitude(longitude2);

                originLatLng = origin.getLatLng();

                originDetails = origin.getName().toString();
                        //+ ", " + origin.getAddress();


                flag2 = true;

                /*if(flag1) {
                    *//*float distanceTemp = destinationLoc.distanceTo(originLoc);
                    distanceTemp /= 1000;
                    Log.d("distance", ""+distanceTemp);
                    String distance;
                    distance = "Distance: " + distanceTemp;
                    distance_tv.setText(distance);*//*


                    float results[] = new float[1];
                    Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results);
                    String distance;
                    distance = "Distance is : " + results[0];
                    distance_tv.setText(distance);*/
                }
            }
        }



    public class GetPortsBackgroundTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SandBooking.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("InpreExecute", "nothing");

        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String getPortsURL = "http://192.168.43.218/portinfo/getPorts.php";
            Log.e("IndoInBackgroundTask", "outside");
            Log.e("IndoInBackgroundTask", method);
            if(method.equals("getPorts")) {

                try {
                    URL url = new URL(getPortsURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    Log.e("IndoInBackgroundTask", "after opening connection");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    Log.e("IndoInBackgroundTask", "before opening stream");
                    OutputStream os = urlConnection.getOutputStream();
                    Log.e("old background: ", "Output stream opeded");
                    InputStream is = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                    StringBuilder sb = new StringBuilder();
                    String line = null;
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
            progressDialog.dismiss();
            //Log.e("in onPostExecute", response);

            try {
                int cnt, temp;
                Log.e("in onPostExecute", "in TryBlock");
                JSONObject root = new JSONObject(response);
                JSONArray result = root.getJSONArray("result");
                Log.e("in onPostExecute", result.toString());
                cnt = 0;
                while(cnt<result.length()) {
                    JSONObject row = result.getJSONObject(cnt);
                    PortZones record = new PortZones();
                    record.port_name = row.getString("port_name");
                    record.port_id = row.getInt("port_id");
                    record.zone_id = row.getInt("port_zone_id");
                    record.zone_name = row.getString("zone_name");
                    portZones.add(record);
                    cnt++;
                }
                cnt = 0;
                temp = 0;
                ports.add(portZones.get(0).port_name);

                while(cnt < portZones.size()) {
                    if(!(ports.get(temp).equals(portZones.get(cnt).port_name))) {
                        ports.add(portZones.get(cnt).port_name);

                        temp++;
                    }
                    cnt++;
                }

                Log.e("in onPostExecute ports", ports.toString());


                //TODO: change this arrayAdapter to customAdapter

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, ports);
                CustomAdapterForPadding customAdapterForPadding = new CustomAdapterForPadding(getApplicationContext(), ports);



                portNamesSpinner.setAdapter(customAdapterForPadding);
                portNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedPort = ports.get(position);
                        selectedPostition = position;
                        int cnt = 0;
                        zones.clear();
                        while(cnt < portZones.size()) {
                            if(portZones.get(cnt).port_name.equals(selectedPort)) {
                                zones.add(portZones.get(cnt).zone_name);
                            }
                            cnt++;
                        }
                        //Zone Names Spinner
                        ArrayAdapter<String> zoneNameAdapter = new ArrayAdapter<String>(SandBooking.this, android.R.layout.simple_spinner_item, zones);
                        zoneNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        zoneNamesSpinner.setAdapter(zoneNameAdapter);
                        zoneNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedZone = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });






            } catch (JSONException e) {
                Log.e("in onPostExecute", "in catch");
                e.printStackTrace();
            }

        }

    }



}


