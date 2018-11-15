package com.b2ngames.findmyteacherapp;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_LoginStringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;


public class TeacherAreaActivity extends FT_ActivityBase {

    Button mBtnMySubjects, mBtnMyLocation, mBtnAvailability;
    Context mContext;
    private static String LOG_TAG = TeacherAreaActivity.class.getSimpleName();
    public final static  int SET_TEACHER_LOCATION = 100;
    Switch swDomicilyService;
    Boolean mDomicilyServicesChecked;
    int mDomicilyServices, mIdTeacherRemote, mPriceHour;
    private static final String urlSetDomicilyServices =
            "https://www.findmyteacherapp.com/app/update_teacher_domicily_services.php";
    private static final String urlUpdateTeacherLocation =
            "https://www.findmyteacherapp.com/app/update_teacher_location.php";
    private static final String urlupdateTeacherPriceOnServer =
            "https://www.findmyteacherapp.com/app/update_teacher_price.php";
    double mLat, mLng;
    protected SeekBar mSbPrice;
    protected TextView mTvSeekBarPriceDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_area);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mContext = this;
        mBtnMySubjects = (Button)findViewById(R.id.btnSubjects);
        mBtnMySubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,MySubjectsActivity.class);
                startActivity(intent);
            }
        });

        mBtnAvailability = (Button)findViewById(R.id.btnAvailavility);
        mBtnAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,TeacherAvailabilityActivity.class);
                intent.putExtra("canUpdateAvailability",true);
                intent.putExtra(FindMyTeacherConstants.CALLING_ACTIVITY_KEY,FindMyTeacherConstants.TEACHER_AREA_ACTIVITY);
                startActivity(intent);
            }
        });
        mBtnMyLocation = (Button)findViewById(R.id.btnLocation);
        mBtnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,SelectLocation.class);
                intent.putExtra(FindMyTeacherConstants.CALLING_ACTIVITY_KEY,FindMyTeacherConstants.TEACHER_AREA_ACTIVITY);
                startActivityForResult(intent,SET_TEACHER_LOCATION);
            }
        });

        swDomicilyService = (Switch) findViewById(R.id.swDomicilyService);
        Cursor cGetTeacherInfo = getContentResolver().query(
                FindTeacherContract.MyUserInfo.CONTENT_URI,
                new String[]{FindTeacherContract.MyUserInfo.COLUMN_MOBILITY,FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE,
                FindTeacherContract.MyUserInfo.COLUMN_PRICE_HOUR},
                null,
                null,
                null
        );
        cGetTeacherInfo.moveToFirst();
        if (cGetTeacherInfo.getCount()>0){
            mDomicilyServices = cGetTeacherInfo.getInt(cGetTeacherInfo.
                    getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_MOBILITY));
            mIdTeacherRemote = cGetTeacherInfo.getInt(cGetTeacherInfo.
                    getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE));
            mPriceHour = cGetTeacherInfo.getInt(cGetTeacherInfo.
                    getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_PRICE_HOUR));
        }

        Log.e(LOG_TAG + " ID REMOTE",mIdTeacherRemote +"" );

        if(mDomicilyServices==0)
        {
            mDomicilyServicesChecked = false;
        }else{
            mDomicilyServicesChecked = true;
        }
        swDomicilyService.setChecked(mDomicilyServicesChecked);

        if(mDomicilyServicesChecked)
        {
            swDomicilyService.setText(getResources().getString(R.string.switch_domicily_services_true));
        }else{
            swDomicilyService.setText(getResources().getString(R.string.switch_domicily_services_false));
        }
        swDomicilyService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ContentValues values = new ContentValues();

                if (isChecked) {
                    swDomicilyService.setText(getResources().getString(R.string.switch_domicily_services_true));
                    mDomicilyServices = 1;

                } else {
                    swDomicilyService.setText(getResources().getString(R.string.switch_domicily_services_false));
                    mDomicilyServices = 0;
                }
                values.put(FindTeacherContract.MyUserInfo.COLUMN_MOBILITY,mDomicilyServices);
                int updated = getContentResolver().update(
                        FindTeacherContract.MyUserInfo.CONTENT_URI,
                        values,
                        null,
                        null
                        );
                Log.e(LOG_TAG , updated + "");
                setDomicilyServicesOnServer();
            }
        });

        mSbPrice = (SeekBar)findViewById(R.id.sbPrice);
        mTvSeekBarPriceDisplay = (TextView)findViewById(R.id.tvSeekBarPriceDisplay);
        mTvSeekBarPriceDisplay.setText(Integer.toString(mPriceHour)+ " €/h");
        mSbPrice.setProgress(mPriceHour);
        final ContentValues values = new ContentValues();
        mSbPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvSeekBarPriceDisplay.setText(Integer.toString(progress) + " €/h");
                mPriceHour = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                values.put(FindTeacherContract.MyUserInfo.COLUMN_PRICE_HOUR,mPriceHour);
                int updated = getContentResolver().update(
                        FindTeacherContract.MyUserInfo.CONTENT_URI,
                        values,
                        null,
                        null);
                updateTeacherPriceOnServer();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SET_TEACHER_LOCATION && resultCode==RESULT_OK){
            mLat= Double.parseDouble(data.getStringExtra("lat"));
            mLng = Double.parseDouble(data.getStringExtra("lng"));
            Log.e(LOG_TAG + "mlat", mLat + "");
            ContentValues values = new ContentValues();
            values.put(FindTeacherContract.MyUserInfo.COLUMN_LAT,mLat);
            values.put(FindTeacherContract.MyUserInfo.COLUMN_LNG,mLng);
            int updated =  getContentResolver().update(
                    FindTeacherContract.MyUserInfo.CONTENT_URI,
                    values,
                    null,
                    null);
            updatedUserLocation();
            Log.e(LOG_TAG +"updated", updated + "");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    public void setDomicilyServicesOnServer(){

            Log.e(LOG_TAG , "INSIDE OBTAIN");

            StringRequest stringRequest = new FT_LoginStringRequest(this, Request.Method.POST, urlSetDomicilyServices,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.e(LOG_TAG + "onResponse", s);

                }
            },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            Toast.makeText(TeacherAreaActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();

                        }
                    }){
                @Override
                protected Map<String, String> getParamsQuery () {

                    Map<String, String> params = new Hashtable<String, String>();
                    params.put("idRemote",Integer.toString(mIdTeacherRemote));
                    params.put("domicilyServices",Integer.toString(mDomicilyServices));
                    Log.e(LOG_TAG + "idRemote",mIdTeacherRemote + "");
                    Log.e(LOG_TAG + "domicilyServices",mDomicilyServices + "");
                    return params;
                }
            }.getStringRequest();
            try {
                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
            }catch(Exception e){
                e.printStackTrace();

            }

    }

    public void updatedUserLocation(){

        Log.e(LOG_TAG , "INSIDE OBTAIN");

        StringRequest stringRequest = new FT_LoginStringRequest(this, Request.Method.POST, urlUpdateTeacherLocation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.e(LOG_TAG + "onResponse", s);

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(TeacherAreaActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("idRemote",Integer.toString(mIdTeacherRemote));
                params.put("lat",Double.toString(mLat));
                params.put("lng",Double.toString(mLng));
                Log.e(LOG_TAG + " idRemote",mIdTeacherRemote + "");
                Log.e(LOG_TAG + " lat",mLat + "");
                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();

        }
    }

    public void updateTeacherPriceOnServer(){

        Log.e(LOG_TAG , "INSIDE OBTAIN");

        StringRequest stringRequest = new FT_LoginStringRequest(this, Request.Method.POST, urlupdateTeacherPriceOnServer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.e(LOG_TAG + "onResponse", s);

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(TeacherAreaActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("idRemote",Integer.toString(mIdTeacherRemote));
                params.put("priceHour",Integer.toString(mPriceHour));
                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();

        }
    }
}
