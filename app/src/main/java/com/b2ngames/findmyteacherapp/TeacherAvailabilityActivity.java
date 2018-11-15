package com.b2ngames.findmyteacherapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.Adapters.AvailabilityViewPagerAdapter;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;
import com.b2ngames.findmyteacherapp.fragments.DayFragment;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;


public class TeacherAvailabilityActivity extends FT_ActivityBase {
    protected FindTeacherDbHelper mOpenHelper;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private boolean mCanUpdateAvailability;
    private static String LOG_TAG = TeacherAvailabilityActivity.class.getSimpleName();
    int mIdTeacher;
    int mCallingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_availability);

        mOpenHelper = new FindTeacherDbHelper(this);

        Intent intent = getIntent();
        mCanUpdateAvailability = new Boolean(intent.getBooleanExtra("canUpdateAvailability",false));
        mIdTeacher = intent.getIntExtra("idTeacher",0);
        mCallingActivity = intent.getIntExtra(FindMyTeacherConstants.CALLING_ACTIVITY_KEY,0);
        Log.e(LOG_TAG + "availa1",Boolean.toString(mCanUpdateAvailability));
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);


        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void setupViewPager(ViewPager viewPager) {
        AvailabilityViewPagerAdapter adapter = new AvailabilityViewPagerAdapter(getSupportFragmentManager());

        int idTeacher = getIdTeacher();
        // Monday
        Fragment f = new DayFragment();
        Bundle b = new Bundle();
        b.putInt("teacher", idTeacher);
        b.putInt("day", 1);
        b.putBoolean("canUpdate",mCanUpdateAvailability);
        f.setArguments(b);
        adapter.addFragment(f, getResources().getString(R.string.monday));

        f = new DayFragment();
        b= new Bundle();
        b.putInt("teacher", idTeacher);
        b.putInt("day", 2);
        b.putBoolean("canUpdate",mCanUpdateAvailability);
        f.setArguments(b);
        adapter.addFragment(f, getResources().getString(R.string.tuesday));

        f = new DayFragment();
        b= new Bundle();
        b.putInt("teacher", idTeacher);
        b.putInt("day", 3);
        b.putBoolean("canUpdate",mCanUpdateAvailability);
        f.setArguments(b);
        adapter.addFragment(f, getResources().getString(R.string.wednesday));

        f = new DayFragment();
        b= new Bundle();
        b.putInt("teacher", idTeacher);
        b.putInt("day", 4);
        b.putBoolean("canUpdate",mCanUpdateAvailability);
        f.setArguments(b);
        adapter.addFragment(f, getResources().getString(R.string.thursday));

        f = new DayFragment();
        b= new Bundle();
        b.putInt("teacher", idTeacher);
        b.putInt("day", 5);
        b.putBoolean("canUpdate",mCanUpdateAvailability);
        f.setArguments(b);
        adapter.addFragment(f, getResources().getString(R.string.friday));

        f = new DayFragment();
        b= new Bundle();
        b.putInt("teacher", idTeacher);
        b.putInt("day", 6);
        b.putBoolean("canUpdate",mCanUpdateAvailability);
        f.setArguments(b);
        adapter.addFragment(f, getResources().getString(R.string.saturday));

        f = new DayFragment();
        b= new Bundle();
        b.putInt("teacher", idTeacher);
        b.putInt("day", 7);
        b.putBoolean("canUpdate",mCanUpdateAvailability);
        f.setArguments(b);
        adapter.addFragment(f, getResources().getString(R.string.sunday));

        viewPager.setAdapter(adapter);
    }

    public int getIdTeacher() {
        if (mCallingActivity == FindMyTeacherConstants.TEACHER_INFO_ACTIVITY) {
            return mIdTeacher;
        } else {
            mOpenHelper = new FindTeacherDbHelper(this);
            Cursor cIdTeacher = mOpenHelper.getReadableDatabase().query(
                    FindTeacherContract.MyUserInfo.TABLE_NAME,
                    new String[]{FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE},
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if (cIdTeacher.getCount() > 0) {
                cIdTeacher.moveToFirst();
                return cIdTeacher.getInt(cIdTeacher.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE));
            }else return mIdTeacher;
        }
    }


    }


