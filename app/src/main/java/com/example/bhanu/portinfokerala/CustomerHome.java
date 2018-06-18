package com.example.bhanu.portinfokerala;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewSwitcher;

public class CustomerHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        //Sand Booking
        Button sand_booking_btn = (Button)findViewById(R.id.sand_booking_btn);
        sand_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSandBooking = new Intent(CustomerHome.this, SandBooking.class);
                startActivity(toSandBooking);
            }
        });

        //Booking Status
        Button booking_status_btn = (Button)findViewById(R.id.booking_status_btn);
        booking_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Booking History
        Button booking_history_btn = (Button)findViewById(R.id.booking_history_btn);
        booking_history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Complete Payment
        Button complete_payment_btn = (Button)findViewById(R.id.complete_payment_btn);
        complete_payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
