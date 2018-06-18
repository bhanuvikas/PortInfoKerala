package com.example.bhanu.portinfokerala;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //validate phone number


        //switch the view in the same activity

        Button request_otp_btn = (Button)findViewById(R.id.request_otp_btn);
        request_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.view_switcher);
                switcher.showNext();
            }
        });

        //validate otp


        //switching to customer_home_page

        Button login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCustomerHome = new Intent(MainActivity.this, CustomerHome.class);
                startActivity(toCustomerHome);
            }
        });


    }

}

