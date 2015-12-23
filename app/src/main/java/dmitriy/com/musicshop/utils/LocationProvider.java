package dmitriy.com.musicshop.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class LocationProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation = new Location("");
    private boolean mIsFusedConnected = false;

    public LocationProvider() {
    }

    public void start(Context context) {
        buildGoogleApiClient(context);
    }

    public void stop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    public Location getLocation() {
        if (mIsFusedConnected) {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastKnownLocation != null)
                mLastLocation = lastKnownLocation;
        }
        return mLastLocation;
    }

    private void buildGoogleApiClient(Context context) {
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (isGooglePlayServicesAvailable == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mIsFusedConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mIsFusedConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mIsFusedConnected = false;
    }
}