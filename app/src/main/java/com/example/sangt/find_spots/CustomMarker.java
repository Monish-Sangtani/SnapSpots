package com.example.sangt.find_spots;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by sangt on 3/25/2017.
 */

public class CustomMarker implements ClusterItem {

    MarkerOptions myMarker;
    CustomMarker()
    {
        myMarker=null;
    }

    CustomMarker(MarkerOptions x)
    {
        myMarker=x;
    }


    @Override
    public LatLng getPosition() {

        if(myMarker!=null)
        {
            return myMarker.getPosition();
        }
        else
        {
            return null;
        }

    }
}
