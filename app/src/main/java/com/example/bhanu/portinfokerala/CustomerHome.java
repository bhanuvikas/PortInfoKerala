package com.example.bhanu.portinfokerala;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class CustomerHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_customer_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("DashBoard");
        setSupportActionBar(toolbar);



        //Sand Booking
        Button sand_booking_btn = findViewById(R.id.sand_booking_btn);
        sand_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSandBooking = new Intent(CustomerHome.this, SandBooking.class);
                startActivity(toSandBooking);
            }
        });

        //Booking Status
        Button booking_status_btn = findViewById(R.id.booking_status_btn);
        booking_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toBookingStatus = new Intent(CustomerHome.this, BookingStatus.class);
                startActivity(toBookingStatus);
            }
        });

        //Booking History
        Button booking_history_btn = findViewById(R.id.booking_history_btn);
        booking_history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toBookingHistory = new Intent(CustomerHome.this, BookingHistory.class);
                startActivity(toBookingHistory);
            }
        });

        //Complete Payment
        Button complete_payment_btn = findViewById(R.id.complete_payment_btn);
        complete_payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCompletePayment = new Intent(CustomerHome.this, CompletePayment.class);
                startActivity(toCompletePayment);
            }
        });

        //Spot Booking
        Button spot_booking_btn = findViewById(R.id.spot_booking_btn);
        spot_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomerHome.this, "Spot Booking", Toast.LENGTH_SHORT).show();
                Intent toSpotBooking = new Intent(CustomerHome.this, SpotBooking.class);
                startActivity(toSpotBooking);
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
                SharedPreferences prefs = getSharedPreferences("portinfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(CustomerHome.this, MainActivity.class);
                startActivity(intent);

                Toast.makeText(CustomerHome.this, "Successfully Logged Out!!", Toast.LENGTH_SHORT).show();



                break;

        }
        return true;
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }*/

    /*Boolean backPress = false;
    @Override
    public void onBackPressed() {

        Toast.makeText(CustomerHome.this, "Press back again to Exit!!", Toast.LENGTH_SHORT).show();


        if( getIntent().getBooleanExtra("back from booking", false)){
            //Toast.makeText(CustomerHome.this, "Oh man", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        } else {

            if(backPress) {*//*
                Intent intent = new Intent(this, CustomerHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);*//*
                finish();
            }

            backPress = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPress = false;
                }
            }, 3000);

        }
    }*/
}
