package com.b2ngames.findmyteacherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;


public class FT_ActivityBase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SharedPreferences mSharedPreferencesLogIn;
    public static Boolean mRegisteredUser;
    private static final String LOG_TAG = FT_ActivityBase.class.getSimpleName();
    protected NavigationView mNavigationView;
    protected View header;
    protected Button btnRegister;
    protected TextView tvUserName;
    protected NetworkImageView nivUserPicture;
    protected Context mContext;
    DrawerLayout mDrawerLayout;
    int mLogin;
    String mUserName, mNivUserPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ft_base);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mSharedPreferencesLogIn = getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE);
        mLogin = mSharedPreferencesLogIn.getInt(FindMyTeacherConstants.IS_LOGED,0);
        mContext = this;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Cursor cGetUserInfo = getContentResolver().query(
                FindTeacherContract.MyUserInfo.CONTENT_URI,
                new String[]{FindTeacherContract.MyUserInfo.COLUMN_USER_NAME,
                FindTeacherContract.MyUserInfo.COLUMN_URL_IMAGE},
                null,
                null,
                null);
        cGetUserInfo.moveToFirst();
        if(cGetUserInfo.getCount()>0) {
            mUserName = cGetUserInfo.getString(cGetUserInfo.
                    getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_USER_NAME));
            mNivUserPicture = cGetUserInfo.getString(cGetUserInfo.
                    getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_URL_IMAGE));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START,false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_teachers) {


        } else if (id == R.id.nav_students) {


        } else if (id == R.id.nav_messages) {


        } else if (id == R.id.nav_help ){


        } else if (id == R.id.nav_logout ){

//            SharedPreferences.Editor editor = mSharedPreferencesLogIn.edit();
//            if(getContentResolver().delete(FindTeacherContract.MyUserInfo.CONTENT_URI, null, null) == 1) {
//                editor.putInt(FindMyTeacherConstants.IS_LOGED, 0);
//                editor.putBoolean("reload", false);
//                editor.commit();
//                getContentResolver().delete(FindTeacherContract.MySubjects.CONTENT_URI, null, null);
//                getContentResolver().delete(FindTeacherContract.MyAvailability.CONTENT_URI, null, null);
//                Intent intent = new Intent(mContext, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
            try{
                FT_ActivityBase.performLogout(this);
            } catch(Exception e){
                e.printStackTrace();
                Log.e(LOG_TAG, e.toString());
            } finally {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }


        } else if (id == R.id.nav_help_on_logout ){


        } else if (id == R.id.nav_notifications_on_logout){

        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            onBackPressedImplementation();
            super.onBackPressed();
        }
    }

    public void onBackPressedImplementation() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                //getBaseContext().onBackPressed();
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void setSlideMenu() {
        header = mNavigationView.getHeaderView(0);
        btnRegister = (Button)header.findViewById(R.id.btnLogOrRegister);
        nivUserPicture = (NetworkImageView) header.findViewById(R.id.ivUserPicture);
        tvUserName = (TextView)header.findViewById(R.id.tvUserName);


        if (mLogin==1)
        {
            btnRegister.setVisibility(View.GONE);
            nivUserPicture.setVisibility(View.VISIBLE);
            nivUserPicture.setImageUrl(mNivUserPicture, VolleySingleton.getInstance(mContext).getImageLoader());
            tvUserName.setVisibility(View.VISIBLE);
            tvUserName.setText(mUserName);
            mNavigationView.getMenu().setGroupVisible(R.id.dmGroupItemsLogged, true);
            mNavigationView.getMenu().setGroupVisible(R.id.dmGroupItemsLogOut, false);
        }else{
            nivUserPicture.setVisibility(View.GONE);
            tvUserName.setVisibility(View.GONE);
            btnRegister.setVisibility(View.VISIBLE);
            mNavigationView.getMenu().setGroupVisible(R.id.dmGroupItemsLogOut, true);
            mNavigationView.getMenu().setGroupVisible(R.id.dmGroupItemsLogged, false);
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LoginSelectActivity.class);
                startActivity(intent);
            }
        });

        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserAreaActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void performLogout(Activity _actContext) throws Exception {
        try {
            SharedPreferences.Editor editor = _actContext.getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE).edit();
            if (_actContext.getContentResolver().delete(FindTeacherContract.MyUserInfo.CONTENT_URI, null, null) == 1) {
                editor.putInt(FindMyTeacherConstants.IS_LOGED, 0);
                editor.putBoolean("reload", false);
                editor.remove(FindMyTeacherConstants.MY_SERVER_ID);
                editor.commit();
                _actContext.getContentResolver().delete(FindTeacherContract.MySubjects.CONTENT_URI, null, null);
                _actContext.getContentResolver().delete(FindTeacherContract.MyAvailability.CONTENT_URI, null, null);
            } else {
                Log.e(LOG_TAG, "Unable to LogOut");
                throw new Exception("Unable to LogOut");
            }
        } catch(Exception e){
            throw e;
        }
    }


}

