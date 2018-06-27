package com.example.bhanu.portinfokerala;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sand_booking);
        destination_tv = (TextView) findViewById(R.id.destination_tv);
        origin_tv = (TextView) findViewById(R.id.origin_tv);
        distance_tv = (TextView) findViewById(R.id.distance_tv);
        time_tv = (TextView) findViewById(R.id.time_tv);

        //Port Names Spinner
        Spinner portNamesSpinner = (Spinner) findViewById(R.id.port_name_spinner);
        ArrayAdapter<CharSequence> portNameAdapter = ArrayAdapter.createFromResource(this, R.array.port_names, android.R.layout.simple_spinner_item);
        portNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        portNamesSpinner.setAdapter(portNameAdapter);


        //Zone Names Spinner
        Spinner zoneNamesSpinner = (Spinner) findViewById(R.id.zone_name_spinner);
        ArrayAdapter<CharSequence> zoneNameAdapter = ArrayAdapter.createFromResource(this, R.array.zone_names, android.R.layout.simple_spinner_item);
        zoneNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zoneNamesSpinner.setAdapter(zoneNameAdapter);

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


                Spinner port_name_spinner = (Spinner)findViewById(R.id.port_name_spinner);
                port_name = port_name_spinner.getSelectedItem().toString();

                Spinner zone_name_spinner = (Spinner)findViewById(R.id.zone_name_spinner);
                zone_name = zone_name_spinner.getSelectedItem().toString();

                Spinner quantity_spinner = (Spinner)findViewById(R.id.quantity_spinner);
                quantity = quantity_spinner.getSelectedItem().toString();


                BookingDetails details = new BookingDetails();
                details.portName = port_name;
                details.zoneName = zone_name;
                details.quantity = quantity;
                details.destination = destinationDetails;
                details.origin = originDetails;
                details.distance = distance;
                details.time = time;

                Intent toBookingConfirmation = new Intent(SandBooking.this, BookingConfirmationActivity.class);
                toBookingConfirmation.putExtra("booking_details", details);
                startActivity(toBookingConfirmation);
                Log.d("In Sand booking", "StartActivity passed");
            }
        });


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


    }


