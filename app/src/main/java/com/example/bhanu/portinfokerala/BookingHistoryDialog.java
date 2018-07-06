package com.example.bhanu.portinfokerala;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BookingHistoryDialog extends DialogFragment{

    Button ok_btn;
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



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_booking_history, container, false);

        ok_btn = view.findViewById(R.id.ok_btn);
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
        request_ton = getArguments().getString("quantity") + " tons";
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


        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }
}
