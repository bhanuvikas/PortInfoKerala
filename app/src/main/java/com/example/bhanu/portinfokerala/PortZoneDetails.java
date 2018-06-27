package com.example.bhanu.portinfokerala;

import java.util.Comparator;

public class PortZoneDetails {

    int port_id;
    int zone_id;
    String port_name;
    String zone_name;
    //int spot_limit_balance;

    public String getPort_name() {
        return port_name;
    }

    public static Comparator<PortZoneDetails> portNameComparator = new Comparator<PortZoneDetails>() {
        @Override
        public int compare(PortZoneDetails o1, PortZoneDetails o2) {
            String port_name1 = o1.getPort_name();
            String port_name2 = o2.getPort_name();

            return port_name2.compareTo(port_name1);
        }
    };

}
