package com.omni.omnilocation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAResourceManager;

/**
 * Created by wiliiamwang on 11/12/2017.
 */

public class OGLocationService implements IARegion.Listener, IALocationListener {

    public interface OGLocationListener {
        void onLocationChanged(Location location, boolean isIndoor, float certainty);
        void onEnterVenue(String venueId);
        void onEnterFloor(String floorId);
    }

    private Context mContext;
    private boolean mIsIndoor = false;
    private OGLocationListener mOGLocationListener;
    private Location mLocation;
    private IALocationManager mIALocationManager;
    private IAResourceManager mIAResourceManager;

    public OGLocationService(Context context) {
        mContext = context;
    }

    public void registerLocationService(OGLocationListener listener) {

        mOGLocationListener = listener;

        initLocationService();
        IALocationRequest request = IALocationRequest.create();
        request.setFastestInterval(1000);
        request.setSmallestDisplacement(0.6f);

        mIALocationManager.removeLocationUpdates(this);
        mIALocationManager.requestLocationUpdates(request, this);
    }

    private void initLocationService() {
        if (mIALocationManager == null) {
            mIALocationManager = IALocationManager.create(mContext);
        } else {
            mIALocationManager.unregisterRegionListener(this);
        }
        mIALocationManager.registerRegionListener(this);
        if (mIAResourceManager == null) {
            mIAResourceManager = IAResourceManager.create(mContext);
        }
    }

    public void unRegisterLocationService() {
        if (mIALocationManager != null) {
            mIALocationManager.removeLocationUpdates(this);
            mIALocationManager.unregisterRegionListener(this);
        }
    }

    public void destroy() {
        if (mIALocationManager != null) {
            mIALocationManager.destroy();
            mIALocationManager = null;
        }
    }

    @Override
    public void onEnterRegion(IARegion iaRegion) {
        if (iaRegion.getType() == IARegion.TYPE_UNKNOWN) {
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            mIsIndoor = false;

            mOGLocationListener.onEnterVenue(iaRegion.getId());
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            mIsIndoor = true;

            mOGLocationListener.onEnterFloor(iaRegion.getId());
        }
    }

    @Override
    public void onExitRegion(IARegion iaRegion) {
        if (iaRegion.getType() == IARegion.TYPE_UNKNOWN) {
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            mIsIndoor = false;
        }
    }

    @Override
    public void onLocationChanged(IALocation iaLocation) {
        mLocation = iaLocation.toLocation();

        if (mIsIndoor) {
            mOGLocationListener.onLocationChanged(mLocation, mIsIndoor, iaLocation.getFloorCertainty());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }
}
