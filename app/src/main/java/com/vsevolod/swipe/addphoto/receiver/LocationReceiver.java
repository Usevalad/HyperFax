package com.vsevolod.swipe.addphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.vsevolod.swipe.addphoto.config.PreferenceHelper;

import br.com.safety.locationlistenerhelper.core.SettingsLocationTracker;

/**
 * Created by vsevolod on 19.05.17.
 */

public class LocationReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && intent.getAction().equals("my.action")) {
            Location locationData = (Location) intent.getParcelableExtra(SettingsLocationTracker.LOCATION_MESSAGE);
            Log.d("Location: ", "Latitude: " + locationData.getLatitude() + "Longitude:" + locationData.getLongitude());
            //send your call to api or do any things with the of location data
            PreferenceHelper helper = new PreferenceHelper();
//            helper.saveString(PreferenceHelper.);
        }
    }
}