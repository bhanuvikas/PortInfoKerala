package com.example.bhanu.portinfokerala;

import java.util.Comparator;

public class PortZones {

    int port_id;
    int zone_id;
    String port_name;
    String zone_name;

    public String getPort_name() {
        return port_name;
    }


    public static Comparator<PortZones> portNameComparator = new Comparator<PortZones>() {
        @Override
        public int compare(PortZones o1, PortZones o2) {
            String port_name1 = o1.getPort_name();
            String port_name2 = o2.getPort_name();

            return port_name2.compareTo(port_name1);
        }
    };
}
