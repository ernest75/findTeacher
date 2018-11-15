package com.b2ngames.findmyteacherapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherProvider;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_LoginStringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;


public class UserAreaActivity extends FT_ActivityBase implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = UserAreaActivity.class.getSimpleName();
    private static final String url = "https://www.findmyteacherapp.com/app/become_teacher.php";

    private static final int LOADER_USER_INFO_AREA_ID = 20;

    TextView mTvUserName;
    Button mBtnTeacherArea, mBtnBecomeTeacher, mBtnChangePassword;
    RelativeLayout mRlUserArea, mRlWaitingBecome;

    protected NetworkImageView nivUserPicture;

    int mIdRemote;
    int mRequestCodeChangePasswordIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        mRequestCodeChangePasswordIntent = 2200;
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setSlideMenu();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTvUserName = (TextView)findViewById(R.id.tvUserName);
        mBtnTeacherArea =(Button)findViewById(R.id.btnTeacherArea);
        mBtnBecomeTeacher =(Button)findViewById(R.id.btnBecomeTeacher);
        mBtnChangePassword = (Button)findViewById(R.id.btnChangePasw);
        nivUserPicture = (NetworkImageView)findViewById(R.id.nivUserImage);
        nivUserPicture.setImageUrl(mNivUserPicture,VolleySingleton.getInstance(mContext).getImageLoader());

        mRlUserArea = (RelativeLayout)findViewById(R.id.rlUserArea);
        mRlWaitingBecome = (RelativeLayout)findViewById(R.id.rlWaitingBecomeTeacher);

        getSupportLoaderManager().initLoader(LOADER_USER_INFO_AREA_ID, null, this);

        mBtnBecomeTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlUserArea.setVisibility(View.GONE);
                mRlWaitingBecome.setVisibility(View.VISIBLE);
                becomeTeacher();
            }
        });
        mBtnTeacherArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,TeacherAreaActivity.class);
                startActivity(intent);
            }
        });

        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ChangePasswordActivity.class);
                startActivityForResult(intent,mRequestCodeChangePasswordIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedLogin = mContext.getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE);
        int isLoged = sharedLogin.getInt(FindMyTeacherConstants.IS_LOGED, 0);
        if(isLoged==0){
            finish();
        }
    }

    @Override
    public void onBackPressedImplementation() {
        super.onBackPressedImplementation();

    }

    @Override
    public void setSlideMenu() {
        super.setSlideMenu();
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                FindTeacherContract.MyUserInfo.CONTENT_URI,
                new String[]{FindTeacherContract.MyUserInfo.COLUMN_USER_NAME,
                        FindTeacherContract.MyUserInfo.COLUMN_IS_TEACHER,
                        FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE},
                FindTeacherContract.MyUserInfo._ID + " =? ",
                new String[]{Integer.toString(1)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //DATA WORK

        data.moveToFirst();
        String name = data.getString(data.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_USER_NAME));
        int isTeacher = data.getInt(data.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_IS_TEACHER));
        mIdRemote = data.getInt(data.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE));
        if(isTeacher==0)
        {
            mBtnBecomeTeacher.setVisibility(View.VISIBLE);
            mBtnTeacherArea.setVisibility(View.INVISIBLE);

        }else{
            mBtnBecomeTeacher.setVisibility(View.INVISIBLE);
            mBtnTeacherArea.setVisibility(View.VISIBLE);

        }
        mTvUserName.setText(name);
        Log.e(LOG_TAG , name);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void parseUserBecomeTeacherJson(String jsonString, ContentValues values) {
        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                int isTeacher = jo.getInt(FindTeacherContract.MyUserInfo.COLUMN_IS_TEACHER);
                Log.e("Url parsejada", url);

                values.put(FindTeacherContract.MyUserInfo.COLUMN_IS_TEACHER, isTeacher);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void becomeTeacher() {
        StringRequest stringRequest = new FT_LoginStringRequest(this, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        mRlUserArea.setVisibility(View.VISIBLE);
                        mRlWaitingBecome.setVisibility(View.GONE);
                        Log.e(LOG_TAG + "ONRESPONSE", s);
                        int updated = 0;
                        ContentValues contentValues = new ContentValues();
                        parseUserBecomeTeacherJson(s,contentValues);
                        updated  = getBaseContext().getContentResolver().update(
                                FindTeacherContract.MyUserInfo.CONTENT_URI,
                                contentValues,
                                FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE + " =? ",
                                new String[]{Integer.toString(mIdRemote)});
                        Log.e(LOG_TAG, updated + "");
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(UserAreaActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        try{
                            FT_ActivityBase.performLogout(UserAreaActivity.this);
                        } catch(Exception e){
                            e.printStackTrace();
                            Log.e(LOG_TAG, e.toString());
                        } finally {
                            Intent intent = new Intent(mContext, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("idRemote",Integer.toString(mIdRemote));
                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,"Register not valid",Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==mRequestCodeChangePasswordIntent && resultCode==RESULT_OK){
            Toast.makeText(mContext,"Password changed correctly",Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
