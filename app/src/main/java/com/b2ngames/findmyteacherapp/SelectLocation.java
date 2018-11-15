package com.b2ngames.findmyteacherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/*******************************************************************
 [XLLR]: Methods Order:
 (1) : Activity Life Cycle Methods
 (2) : Menu and Navigation

 **********************************************************************/

public class SelectLocation extends FT_ActivityBase implements OnMapReadyCallback{

    private GoogleMap mMap;
    String lat;
    String lng;
    SharedPreferences prefs;
    Context mContext;
    String latDevice,lngDevice;
    int useCurrentPosition;
    String LOG_TAG = SelectLocation.class.getSimpleName();
    int mCallingActivity;
    double latDouble;
    double lngDouble;
    int mIdTeacher;

    /***
     * Activity Life Cycle Methods
     ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        useCurrentPosition = prefs.getInt("mUseCurrentPosition",1);

        Intent intent = getIntent();
        mCallingActivity = intent.getIntExtra(FindMyTeacherConstants.CALLING_ACTIVITY_KEY,0);
        mIdTeacher = intent.getIntExtra("teacherId",0);
        if(mCallingActivity==FindMyTeacherConstants.TEACHER_INFO_ACTIVITY){
            invalidateOptionsMenu();
        }
        //com.b2ngames.findmyteacherapp.FiltersActivity
        // com.b2ngames.findmyteacherapp.TeacherAreaActivity



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(MainActivity.mGoogleApiClient);
        latDevice = String.valueOf(loc.getLatitude());
        lngDevice = String.valueOf(loc.getLongitude());

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lat = prefs.getString("lat", null);
        lng = prefs.getString("lng", null);

        if(mCallingActivity==FindMyTeacherConstants.FILTERS_ACTIVITY) {
            latDouble =  Double.parseDouble(lat);
            lngDouble =  Double.parseDouble(lng);
            Log.e(LOG_TAG, "CALLING WORKS");
        }else if(mCallingActivity==FindMyTeacherConstants.TEACHER_AREA_ACTIVITY){
            Cursor c = getContentResolver().query(
                    FindTeacherContract.MyUserInfo.CONTENT_URI,
                    new String[]{FindTeacherContract.MyUserInfo.COLUMN_LAT,
                            FindTeacherContract.MyUserInfo.COLUMN_LNG},
                    FindTeacherContract.MyUserInfo._ID + " =? ",
                    new String[]{Integer.toString(1)},
                    null
            );
            Log.e(LOG_TAG, "CALLER TEACHER AREA");
            if(c.getCount()>0) {
                c.moveToFirst();
                latDouble = c.getDouble(c.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_LAT));
                lngDouble = c.getDouble(c.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_LNG));
                Log.e(LOG_TAG, " C OK");
                Log.e(LOG_TAG + " latdouble", latDouble + "");
                Log.e(LOG_TAG + " lngdouble", lngDouble + "");
            }else{
                Log.e(LOG_TAG, " C NOT OK");
                latDouble =  Double.parseDouble(latDevice);
                lngDouble =  Double.parseDouble(lngDevice);
                Log.e(LOG_TAG + " latdouble", latDouble + "");
                Log.e(LOG_TAG + " lngdouble", lngDouble + "");
            }
            lat = Double.toString(latDouble);
            lng = Double.toString(lngDouble);

    }else if(mCallingActivity==FindMyTeacherConstants.TEACHER_INFO_ACTIVITY){
            Cursor c = getContentResolver().query(
                    FindTeacherContract.Users.CONTENT_URI,
                    new String[]{FindTeacherContract.Users.COLUMN_LAT,
                            FindTeacherContract.Users.COLUMN_LNG},
                    FindTeacherContract.Users.COLUMN_REMOTE_ID + " =? ",
                    new String[]{Integer.toString(mIdTeacher)},
                    null
            );
            Log.e(LOG_TAG, "CALLER TEACHER_INFO");
            if(c.getCount()>0) {
                c.moveToFirst();
                latDouble = c.getDouble(c.getColumnIndex(FindTeacherContract.Users.COLUMN_LAT));
                lngDouble = c.getDouble(c.getColumnIndex(FindTeacherContract.Users.COLUMN_LNG));
                Log.e(LOG_TAG, " C OK");

            }else{
                Log.e(LOG_TAG, " C NOT OK");
                latDouble =  Double.parseDouble(latDevice);
                lngDouble =  Double.parseDouble(lngDevice);
                Log.e(LOG_TAG + " latdouble", latDouble + "");
                Log.e(LOG_TAG + " lngdouble", lngDouble + "");
            }
            lat = Double.toString(latDouble);
            lng = Double.toString(lngDouble);
        }


        // Add a marker and move the camera
        LatLng currentPos = new LatLng(latDouble, lngDouble);
        mMap.addMarker(new MarkerOptions().position(currentPos).title("Marker in current position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos,15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //lstLatLngs.add(point);
                if(mCallingActivity==FindMyTeacherConstants.TEACHER_INFO_ACTIVITY){
                    return;
                }else{
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    lat = Double.toString(latLng.latitude);
                    lng = Double.toString(latLng.longitude);
                    useCurrentPosition = 0;
                    Log.e(LOG_TAG + "onMapClick",Integer.toString(useCurrentPosition));
                }
            }
        });

    }

    /***
     * Menu and Navigation
     ***/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_location_menu, menu);
        if(mCallingActivity==FindMyTeacherConstants.TEACHER_INFO_ACTIVITY){
            MenuItem item = menu.findItem(R.id.go_home);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                prepareReturnIntent();
                return true;// [Xllr] : Return true to inform Android we have handle the home button clicked action
            case R.id.go_home:
                LatLng devicePos = new LatLng(Double.parseDouble(latDevice),Double.parseDouble(lngDevice));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(devicePos).title("Marker in current position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(devicePos,15));
                lat = latDevice;
                lng = lngDevice;
                useCurrentPosition = 1;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

   /***
    * Activity General Methods
    ***/

    private void prepareReturnIntent() {
        Intent data = new Intent();
        data.putExtra("lat", lat);
        data.putExtra("lng", lng);
        data.putExtra("mUseCurrentPosition", useCurrentPosition);
        setResult(RESULT_OK, data);
        finish();
        Log.e(LOG_TAG,Integer.toString(useCurrentPosition));
        Log.e(LOG_TAG + " LAT ON RETURN INTENT",lat + "");
    }


    @Override
    public void onBackPressedImplementation() {
        prepareReturnIntent();
    }
}
