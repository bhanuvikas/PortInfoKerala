package com.example.bhanu.portinfokerala;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterForPadding extends BaseAdapter{

    Context context;
    LayoutInflater inflter;
    ArrayList<String> ports;
    String[] ports1;

    public CustomAdapterForPadding(Context applicationContext, ArrayList<String> ports) {
        this.context = applicationContext;
        this.ports = ports;
        inflter = (LayoutInflater.from(applicationContext));
        ports1 = ports.toArray(new String[ports.size()]);
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
        view = inflter.inflate(R.layout.custom_spinner_items_for_padding, null);
        TextView port_name = view.findViewById(R.id.portName);
        port_name.setText(ports1[i]);
        return view;
    }
}
