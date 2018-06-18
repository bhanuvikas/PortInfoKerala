package com.example.bhanu.portinfokerala;

import android.content.DialogInterface;
import android.content.Intent;
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

import org.w3c.dom.Text;

public class BookingConfirmationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);


        Log.d("Entering Confirmation", "Entered");
        Intent fromSandBooking = getIntent();
        BookingDetails details = (BookingDetails) fromSandBooking.getSerializableExtra("booking_details");

        TextView port_confirmation_tv = (TextView) findViewById(R.id.port_name_confirmation);
        TextView zone_confirmation_tv = (TextView) findViewById(R.id.zone_name_confirmation);
        TextView quantity_confirmation_tv = (TextView) findViewById(R.id.quantity_confirmation);
        TextView origin_confirmation = (TextView) findViewById(R.id.origin_confirmation);
        TextView destination_confirmation = (TextView) findViewById(R.id.destination_confirmation);
        TextView distane_confirmation = (TextView) findViewById(R.id.distance_confirmation);
        TextView time_confirmation = (TextView) findViewById(R.id.time_confirmation);


        port_confirmation_tv.setText(details.portName);
        zone_confirmation_tv.setText(details.zoneName);
        quantity_confirmation_tv.setText(details.quantity);
        origin_confirmation.setText(details.origin);
        destination_confirmation.setText(details.destination);
        distane_confirmation.setText(details.distance);
        time_confirmation.setText(details.time);


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
                                    Intent toCustomerHome = new Intent(BookingConfirmationActivity.this, CustomerHome.class);
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
                    Toast.makeText(BookingConfirmationActivity.this, "Please select one of the options to proceed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
