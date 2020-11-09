package com.example.ipub;

import java.util.Comparator;

// Sorting the pubs list to show the closest to the phone location
public class DistanceComparator implements Comparator<Pub> {
    @Override
    public int compare(Pub o1, Pub o2) {
        return Double.compare(o1.getDistance() ,o2.getDistance());
    }
}
