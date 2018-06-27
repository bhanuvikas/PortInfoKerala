package com.example.bhanu.portinfokerala;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflter;
    ArrayList<String> ports;
    ArrayList<Integer> balances;
    ArrayList<Integer> color_code;
    String[] ports1;
    Integer[] balances1;
    Integer[] color_code1;

    public CustomAdapter(Context applicationContext, ArrayList<String> ports, ArrayList<Integer> balances, ArrayList<Integer> color_code) {
        this.context = applicationContext;
        this.ports = ports;
        this.balances = balances;
        this.color_code = color_code;
        inflter = (LayoutInflater.from(applicationContext));
        ports1 = ports.toArray(new String[ports.size()]);
        balances1 = balances.toArray(new Integer[balances.size()]);
        color_code1 = color_code.toArray(new Integer[color_code.size()]);
    }

    @Override
    public int getCount() {
        return ports.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        TextView port_name = (TextView) view.findViewById(R.id.portName);
        TextView balance = (TextView) view.findViewById(R.id.balance);
        port_name.setText(ports1[i]);
        balance.setText(String.valueOf(balances1[i]));
        if(color_code1[i]==0) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.lightRed));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGreen));
        }
        return view;
    }
}
