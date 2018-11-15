package com.b2ngames.findmyteacherapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;

import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.Adapters.TeachersCursorAdapter;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


/* TODO List for Ernest **********************************************
    
 *********************************************************************/


/* MainActivity *******************************************************
    [XLLR]: Methods Order:
        (1) : Activity Life Cycle Methods
        (2) : Menu and Navigation
        (3) : LoaderManager.LoaderCallbacks<Cursor>: Implementations
        (4) : ConnectionCallbacks: Implementation
        (5) : LocationListener: Implementation
        (6) : Activity General Methods
 **********************************************************************/

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_TEACHERS_ID = 1;
    private TeachersCursorAdapter mAdapter;
    Context mContext;
    int mUseCurrentPosition;
    int mMaxHourPrice;

    // Uri Creation to find teachers
    private static final String UPLOAD_URL = "https://www.findmyteacherapp.com/app/teachers_around.php";

    // Location Info
    private static int mCurrentPosition;
    Location mLastLocation;
    public static GoogleApiClient mGoogleApiClient;
    String mLat, mLng , mAlt;
    private static final int REQUEST_LOCATION_PERMISSION_ID = 1;

    private static final int WRITE_EXTERNAL_STORAGE_ID = 2;
    SharedPreferences mSharedPreferences;
    SharedPreferences mSharedPreferencesLogIn;

    ListView mLvTeachers;
    ProgressBar mPbWaitingCircle;
    ProgressBar mPbWaitingCircleBottom;
    EditText mEtSearchAppBar;

    private FindTeacherDbHelper mOpenHelper;
    Boolean mSharedPreferencesHasChanged;
    private SharedPreferences.OnSharedPreferenceChangeListener mSpChanged;
    Boolean mIsUpdate;
    int mFirstLvPosition;
    Boolean mNoResultFromServer;
    int mSubjectId;
    int mDistance;
    int mDomicilyServices;
    int mIdLang = -1;
    float mMark;
    String mKeySearch;
    Boolean mRegisteredUser;
    public static View mHeader;
    public static Button mBtnRegister;
    public static TextView mTvUserName;
    public static NetworkImageView nivUserPicture;;

    NavigationView mNavigationView;

    int mLogin;

    DrawerLayout mDrawerLayout;
    String mUserName;
    String mNivUserPicture;



    /***
     * Activity Life Cycle Methods
     ***/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRegisteredUser = false;
        mSharedPreferencesLogIn = getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE);
        mLogin = mSharedPreferencesLogIn.getInt(FindMyTeacherConstants.IS_LOGED,0);


        SharedPreferences.Editor editor = mSharedPreferencesLogIn.edit();
        editor.putBoolean("registeredUser",mRegisteredUser);
        editor.commit();
        mUseCurrentPosition = mSharedPreferences.getInt("mUseCurrentPosition",1);
        mCurrentPosition = mSharedPreferences.getInt("mCurrentPosition",0);
        mPbWaitingCircle = (ProgressBar)findViewById(R.id.pbWaitingCircle);
        mPbWaitingCircleBottom = (ProgressBar)findViewById(R.id.pbWaitingCircleBottom);
        Log.e(LOG_TAG, Locale.getDefault().toString());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mIsUpdate = false;
        mNoResultFromServer = false;
        mEtSearchAppBar = (EditText)findViewById(R.id.etSearch);
        mEtSearchAppBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEtSearchAppBar.setText("");
                return false;
            }
        });

        mEtSearchAppBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String keySearch = mEtSearchAppBar.getText().toString();
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("keySearch",keySearch);
                    editor.commit();
                    mCurrentPosition = 0;
                    mLvTeachers.setVisibility(View.GONE);
                    mPbWaitingCircle .setVisibility(View.VISIBLE);
                    refreshTeachers();

                    Log.e(LOG_TAG ," refresh from editTextBar");
                    hide_keyboard(mEtSearchAppBar);
                }
                return false;
            }
        });

        mOpenHelper = new FindTeacherDbHelper(mContext);
        mLvTeachers = (ListView) findViewById(R.id.lvTeachers);
        mLvTeachers.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (mLvTeachers.getLastVisiblePosition() - mLvTeachers.getHeaderViewsCount() -
                        mLvTeachers.getFooterViewsCount()) >= (mAdapter.getCount() - 1)) {

                    // Now your listview has hit the bottom
                    mFirstLvPosition = mLvTeachers.getFirstVisiblePosition();
                    mPbWaitingCircleBottom.setVisibility(View.VISIBLE);
                    refreshTeachers();
                    Log.e(LOG_TAG + " refresh from","onScrollListener");
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

        });

        mAdapter = new TeachersCursorAdapter(getBaseContext(), null, 0);
        mLvTeachers.setAdapter(mAdapter);
        mLvTeachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TeacherInfoActivity.class);
                int id_local = (int)id;
                intent.putExtra("id_local", id_local);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_TEACHERS_ID, null, this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        setSlideMenu();

        mSharedPreferencesHasChanged = false;
        Log.e(LOG_TAG , "onCreate is called");

        //Log.e(LOG_TAG, this.getDatabasePath("findmyteacher.db").toString());
        copyDeviceDatabase();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkPermission()) {
            mUseCurrentPosition = mSharedPreferences.getInt("mUseCurrentPosition",1);
            mMaxHourPrice = mSharedPreferences.getInt("maxHourPrice",100);

            mSpChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    if(key.compareTo("mCurrentPosition")!=0)
                    {
                        mSharedPreferencesHasChanged = true;
                    }
                }
            };
            mSharedPreferences.registerOnSharedPreferenceChangeListener(mSpChanged);

            buildGoogleApiClient();
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawer(GravityCompat.START,false);
            }

            mLogin = mSharedPreferencesLogIn.getInt("login",0);
            if(mLogin==1){
                mRegisteredUser = true;
            }
            SharedPreferences.Editor editor = mSharedPreferencesLogIn.edit();
            editor.putBoolean("registeredUser",mRegisteredUser);
            editor.commit();
            Log.e(LOG_TAG + "REGISTEREDUSER ON START",mRegisteredUser.toString());
            Cursor cGetUserInfo = getContentResolver().query(
                    FindTeacherContract.MyUserInfo.CONTENT_URI,
                    new String[]{FindTeacherContract.MyUserInfo.COLUMN_USER_NAME,
                            FindTeacherContract.MyUserInfo.COLUMN_URL_IMAGE},
                    null,
                    null,
                    null);
            cGetUserInfo.moveToFirst();
            if(cGetUserInfo.getCount()>0) {
                mUserName = cGetUserInfo.getString(cGetUserInfo.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_USER_NAME));
                mNivUserPicture = cGetUserInfo.getString(cGetUserInfo.
                        getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_URL_IMAGE));
            }
            setSlideMenu();
            mCurrentPosition = mSharedPreferences.getInt("mCurrentPosition",0);
        }
        else
            requestPermission();

        Log.e(LOG_TAG , "onStart is called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSpChanged);
        Log.e(LOG_TAG, "onDestroy is called");

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("mCurrentPosition",mCurrentPosition);
        editor.putBoolean("isUpdate",mIsUpdate);
        editor.commit();

    }

    /***
     * Menu and Navigation
     ***/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                Log.e(LOG_TAG, "Call Service to refresh Data");
                refreshTeachers();
                break;

            case R.id.action_filter:
                Intent intent = new Intent(mContext,FiltersActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_teachers) {


        } else if (id == R.id.nav_students) {


        } else if (id == R.id.nav_messages) {


        } else if (id == R.id.nav_help ){


        } else if (id == R.id.nav_logout ){
            SharedPreferences.Editor editor = mSharedPreferencesLogIn.edit();
            if(getContentResolver().delete(FindTeacherContract.MyUserInfo.CONTENT_URI, null, null) == 1) {
                editor.putInt(FindMyTeacherConstants.IS_LOGED, 0);
                editor.putBoolean("reload", false);
                editor.remove(FindMyTeacherConstants.MY_SERVER_ID);
                editor.commit();
                getContentResolver().delete(FindTeacherContract.MySubjects.CONTENT_URI,null,null);
                getContentResolver().delete(FindTeacherContract.MyAvailability.CONTENT_URI,null,null);
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Log.e(LOG_TAG, "Unable to LogOut");
            }

        } else if (id == R.id.nav_help_on_logout ){


        } else if (id == R.id.nav_notifications_on_logout){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * LoaderManager.LoaderCallbacks<Cursor>: Implementations
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FindTeacherContract.Users.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

        if(mIsUpdate) {
            mLvTeachers.smoothScrollToPositionFromTop(mFirstLvPosition+1,0,500);
            mIsUpdate = false;

        }
        else {
            if (!mNoResultFromServer) {

                mLvTeachers.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /*
     * ConnectionCallbacks: Implementation
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double distance = 0;
        if(mLastLocation!=null) {
            double latTemp = mLastLocation.getLatitude();
            double lngTemp = mLastLocation.getLongitude();
            double altTemp = mLastLocation.getAltitude();
            mLat = mSharedPreferences.getString("lat", "0");
            mLng = mSharedPreferences.getString("lng", "0");
            mAlt = mSharedPreferences.getString("alt", "0");
            distance = distance(Double.valueOf(mLat), latTemp, Double.valueOf(mLng), lngTemp, Double.valueOf(mAlt), altTemp);
        }

        mUseCurrentPosition = mSharedPreferences.getInt("mUseCurrentPosition", 1);
        if (mLastLocation != null && mUseCurrentPosition == 1 && distance>200) {
            mLat = String.valueOf(mLastLocation.getLatitude());
            mLng = String.valueOf(mLastLocation.getLongitude());
            mAlt = String.valueOf(mLastLocation.getAltitude());
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("lat", mLat);
            editor.putString("lng", mLng);
            editor.putString("alt",mAlt);
            editor.commit();

        }

        Cursor c = mOpenHelper.getReadableDatabase().rawQuery("SELECT * FROM " + FindTeacherContract.Users.TABLE_NAME, null);
        if (c.getCount() == 0 || mSharedPreferencesHasChanged == true) {
            mLvTeachers.setVisibility(View.GONE);
            mPbWaitingCircle .setVisibility(View.VISIBLE);
            refreshTeachers();
        }

        Log.e(LOG_TAG + " refresh from ", "onConected");
        Log.e(LOG_TAG + " onConnected shared",mSharedPreferencesHasChanged.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /***
     * LocationListener: Implementation
     ***/
    // TODO: This method should be erased if we are not using it, Carefull I thnk it has to be even if is left empty
    @Override
    public void onLocationChanged(Location location) {

    }

    /***
     * Activity General Methods
     ***/

    // Api Client Construction and connection
    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // Parse Json recived from server
    void parseTeachersJson(String jsonString, Vector<ContentValues> vecTeacherValues) {
        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                String teacherName = jo.getString(FindTeacherContract.Users.COLUMN_TEACHER_NAME);
                String url = jo.getString(FindTeacherContract.Users.COLUMN_URL_IMAGE);
                float distance = (float) jo.getDouble(FindTeacherContract.Users.COLUMN_DISTANCE);
                int idRemote = jo.getInt(FindTeacherContract.Users.COLUMN_REMOTE_ID_JSON_NAME);
                float mark = (float)jo.getDouble(FindTeacherContract.Users.COLUMN_MARK);
                int priceHour = jo.getInt(FindTeacherContract.Users.COLUMN_PRICE_HOUR);
                float lat = (float) jo.getDouble("X(ltbs_point)");
                float lng = (float) jo.getDouble("Y(ltbs_point)");
                Log.e("Url parsejada", url);
                ContentValues teacherValue = new ContentValues();
                teacherValue.put(FindTeacherContract.Users.COLUMN_PRICE_HOUR,priceHour);
                teacherValue.put(FindTeacherContract.Users.COLUMN_TEACHER_NAME, teacherName);
                teacherValue.put(FindTeacherContract.Users.COLUMN_URL_IMAGE, url);
                teacherValue.put(FindTeacherContract.Users.COLUMN_DISTANCE, distance);
                teacherValue.put(FindTeacherContract.Users.COLUMN_REMOTE_ID, idRemote);
                teacherValue.put(FindTeacherContract.Users.COLUMN_MARK, mark);
                teacherValue.put(FindTeacherContract.Users.COLUMN_LAT, lat);
                teacherValue.put(FindTeacherContract.Users.COLUMN_LNG, lng);
                vecTeacherValues.add(teacherValue);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }
    }

    // Request Teachers to the server.
    //  Updates database when data is received
    public void refreshTeachers() {
        // TODO : We are checking we have a location to search from. Maybe more checks should be done.
        Log.e(LOG_TAG + "refresh is called", "yes");

        if (mLastLocation == null)
            return;

        StringRequest stringRequest = new FT_StringRequest(this, Request.Method.POST, UPLOAD_URL,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Vector<ContentValues> vecTeacherValues = new Vector<>();
                parseTeachersJson(s, vecTeacherValues);
                Log.e(LOG_TAG+"JSON", s);
                Log.e(LOG_TAG + " CURRENTPOSITION LV", mCurrentPosition + "");
                int inserted = 0;
                ContentValues[] cvArray = new ContentValues[vecTeacherValues.size()];
                vecTeacherValues.toArray(cvArray);
                if (mCurrentPosition == 0|| mSharedPreferencesHasChanged == true) {
                    inserted = getBaseContext().getContentResolver()
                            .bulkInsert(FindTeacherContract.Users.CONTENT_URI, cvArray);
                    if(mSharedPreferencesHasChanged==true) {
                        mSharedPreferencesHasChanged = false;
                    }
                    mNoResultFromServer = false;
                }
                else {
                    inserted = getBaseContext().getContentResolver()
                            .bulkInsert(FindTeacherContract.Users.CONTENT_UPDATE, cvArray);
                    mIsUpdate = true;
                    mNoResultFromServer = false;
                    if(inserted == 0)
                    {
                        Toast.makeText(mContext,"No more teachers found",Toast.LENGTH_SHORT).show();
                        mNoResultFromServer = true;
                        mIsUpdate = false;
                    }
                }
                mCurrentPosition += inserted;
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt("mCurrentPosition",mCurrentPosition);
                editor.apply();
                Log.e(LOG_TAG + "POSITION AFTER INSERT", mCurrentPosition + "");
                mLvTeachers.setVisibility(View.VISIBLE);
                mPbWaitingCircle .setVisibility(View.GONE);
                mPbWaitingCircleBottom.setVisibility(View.INVISIBLE);
                Log.e(LOG_TAG, s);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Showing toast
                        Toast.makeText(MainActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(LOG_TAG, volleyError.getMessage());
                        Log.e(LOG_TAG, volleyError.getMessage().toString());

                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery() {

                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("numOfResult", "5");
                if(mSharedPreferencesHasChanged == true)
                {
                    mCurrentPosition = 0;
                }
                params.put("position", Integer.toString(mCurrentPosition));

                mLat = mSharedPreferences.getString("lat",null);
                mLng = mSharedPreferences.getString("lng",null);
                mSubjectId = mSharedPreferences.getInt("idSubject",0);
                mDistance = mSharedPreferences.getInt("distance",100);
                mDomicilyServices = mSharedPreferences.getInt("domicilyServices",0);
                mMark = mSharedPreferences.getFloat("mark",0);
                mKeySearch = mSharedPreferences.getString("keySearch",null);
                // maxHourPrice = preferences.getInt("maxHourPrice",0);
                params.put("maxHourPrice",Integer.toString(mMaxHourPrice));
                params.put("lat",mLat);
                params.put("lng", mLng);
                params.put("subjectId",Integer.toString(mSubjectId));
                params.put("distance",Integer.toString(mDistance));
                params.put("domicilyServices",Integer.toString(mDomicilyServices));
                params.put("mark",Float.toString(mMark));
                String codeName = Locale.getDefault().toString();
                try {
                    mIdLang = mOpenHelper.getIdLang(codeName);
                   // Log.e(LOG_TAG + "IDLANG", mIdLang + "");
                } catch(Exception e){
                    mIdLang = 1;
                }
                params.put("idLang", Integer.toString(mIdLang));
                if(mKeySearch!=null) {
                    params.put("keySearch", mKeySearch);
                    Log.e(LOG_TAG + "sendData", "" + mMaxHourPrice);
                }
                Log.e(LOG_TAG, mLat);
                Log.e(LOG_TAG, mLng);
                Log.e(LOG_TAG + "price", Integer.toString(mMaxHourPrice));
                Log.e(LOG_TAG + " currentPosition",Integer.toString(mCurrentPosition));
                Log.e(LOG_TAG + " DISTANCE", mDistance + "");
                Log.e(LOG_TAG + " Domicily", mDomicilyServices + "");
                Log.e(LOG_TAG + " MArk", mMark+ "");
               // Log.e(LOG_TAG + " keySearch", mKeySearch);
               // Log.e(LOG_TAG + "keySearch params", params.get("keySearch").toString());

                return params;
            }
        }.getStringRequest();

        //Creating a Request Queue
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,"Teachers request not valid",Toast.LENGTH_SHORT);
        }


    }

    // Handle Permissions
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        Log.e(LOG_TAG + "[checkPermission]",Integer.toString(result));
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_ID);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_ID);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION_ID:
                if (grantResults.length> 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        buildGoogleApiClient();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    boolean copyFile(String pathSrc, String pathDst){
        try {
            InputStream is = new FileInputStream(pathSrc);
            OutputStream os = new FileOutputStream(pathDst);

            byte[] buf = new byte[1024];
            int len;
            while((len = is.read(buf))>0){
                os.write(buf, 0, len);
            }
            is.close();
            os.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return true;
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

        private void setSlideMenu() {

            mHeader = mNavigationView.getHeaderView(0);
            mBtnRegister = (Button)mHeader.findViewById(R.id.btnLogOrRegister);
            nivUserPicture = (NetworkImageView) mHeader.findViewById(R.id.ivUserPicture);
            mTvUserName = (TextView)mHeader.findViewById(R.id.tvUserName);

            if (mLogin==1)
            {
                mBtnRegister.setVisibility(View.GONE);
                nivUserPicture.setVisibility(View.VISIBLE);
                nivUserPicture.setImageUrl(mNivUserPicture, VolleySingleton.getInstance(mContext).getImageLoader());
                mTvUserName.setVisibility(View.VISIBLE);
                mTvUserName.setText(mUserName);
                mNavigationView.getMenu().setGroupVisible(R.id.dmGroupItemsLogged, true);
                mNavigationView.getMenu().setGroupVisible(R.id.dmGroupItemsLogOut, false);

            }else{
                nivUserPicture.setVisibility(View.GONE);
                mTvUserName.setVisibility(View.GONE);
                mBtnRegister.setVisibility(View.VISIBLE);
                mNavigationView.getMenu().setGroupVisible(R.id.dmGroupItemsLogOut, true);
                mNavigationView.getMenu().setGroupVisible(R.id.dmGroupItemsLogged, false);
            }

            mBtnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                Intent intent = new Intent(mContext, LoginSelectActivity.class);
                startActivity(intent);
                    }
                });

            mTvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(mContext, UserAreaActivity.class);
                startActivity(intent);
                }
            });
    }

    private void hide_keyboard(View view) {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void copyDeviceDatabase() {
        File file = new File("/data/user/0/com.b2ngames.findteacherapp/databases/findmyteacher.db");
        if(file.exists()){
            String dstPath = Environment.getExternalStorageDirectory() + "/db_backup.db";
            Log.e(LOG_TAG, dstPath);
            String srcPath =  "/data/user/0/com.b2ngames.findteacherapp/databases/findmyteacher.db";//file.getAbsolutePath();
            copyFile(srcPath, dstPath);
        }
        else {
            Log.e(LOG_TAG, "Db Not Exists");
        }
    }
}
