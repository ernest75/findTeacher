package com.b2ngames.findmyteacherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;

import java.util.Locale;

/*******************************************************************
    [XLLR]: Methods Order:
        (1) : Activity Life Cycle Methods
        (2) : Menu and Navigation

 **********************************************************************/
public class FiltersActivity extends FT_ActivityBase {

    String LOG_TAG = FiltersActivity.class.getSimpleName();
    int mDistance, mUseCurrentPosition, mDomicilyServices, mMaxHourPrice,mIdLang, mIdSubject, mIdCategory;
    float mMark;
    String lat, lng, mCategory , mSubject, codeName;
    Boolean mDomicilyServicesChecked;
    Context mContext;
    static final int SEARCH_LOCATION_REQUEST = 1;
    public final static  int CATEGORY_AND_SUBJECT_REQUEST = 100;
    TextView tvSubjectSelected, tvSeekBarPriceDisplay, tvSeekbarDistanceDisplay;
    SeekBar sbDistance, sbMaxPrice;
    Switch swDomicilyService;
    Cursor mCSubject;
    SharedPreferences mSharedPreferences;
    FindTeacherDbHelper mOpenHelper;
    SharedPreferences.Editor mSharedEditor;
    RatingBar ratingBar;


    /***
     * Activity Life Cycle Methods
     ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        super.setSlideMenu();

        header = mNavigationView.getHeaderView(0);
        TextView tvUserNameSlideMenu = (TextView)mNavigationView.getHeaderView(0).findViewById(R.id.tvUserName);
        tvUserNameSlideMenu.setTextColor(Color.WHITE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedEditor = mSharedPreferences.edit();
        lat = mSharedPreferences.getString("lat", null);
        lng = mSharedPreferences.getString("lng", null);
        mSubject = mSharedPreferences.getString("subject","No subject selected");
        mMaxHourPrice = mSharedPreferences.getInt("maxHourPrice",100);
        mUseCurrentPosition = mSharedPreferences.getInt("mUseCurrentPosition", 1);
        mIdSubject = mSharedPreferences.getInt("idSubject",0);
        mDistance = mSharedPreferences.getInt("distance",100);
        mDomicilyServices = mSharedPreferences.getInt("domicilyServices",0);
        mMark = mSharedPreferences.getFloat("mark",0);

        ratingBar = (RatingBar) findViewById(R.id.sbMark);
        ratingBar.setProgress(((int)mMark));
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorGold), PorterDuff.Mode.SRC_ATOP);
        if(mDomicilyServices==0)
        {
            mDomicilyServicesChecked = false;
        }else{
            mDomicilyServicesChecked = true;
        }
        mContext = this;
        mOpenHelper = new FindTeacherDbHelper(mContext);
        tvSubjectSelected = (TextView) findViewById(R.id.tvSubjectSelected);
        tvSeekbarDistanceDisplay = (TextView)findViewById(R.id.tvSeekBarDistanceDisplay);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mMark = rating;
                mSharedEditor.putFloat("mark",mMark);
                mSharedEditor.commit();
            }
        });

        LinearLayout llSubject = (LinearLayout)findViewById(R.id.llSubject);
        llSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CategoryListActivity.class);
                startActivityForResult(intent,CATEGORY_AND_SUBJECT_REQUEST);

            }
        });
        swDomicilyService = (Switch) findViewById(R.id.swDomicilyService);
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

                if (isChecked) {
                    swDomicilyService.setText(getResources().getString(R.string.switch_domicily_services_true));
                    mDomicilyServices = 1;

                } else {
                    swDomicilyService.setText(getResources().getString(R.string.switch_domicily_services_false));
                    mDomicilyServices = 0;
                }
                mSharedEditor.putInt("domicilyServices",mDomicilyServices);
                mSharedEditor.commit();
            }
        });

        LinearLayout layoutMap = (LinearLayout) findViewById(R.id.llSearchCenter);
        layoutMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,SelectLocation.class);
                intent.putExtra(FindMyTeacherConstants.CALLING_ACTIVITY_KEY,FindMyTeacherConstants.FILTERS_ACTIVITY);
                startActivityForResult(intent,SEARCH_LOCATION_REQUEST);

            }
        });

        sbDistance = (SeekBar)findViewById(R.id.sbDistance);
        sbDistance.setProgress(mDistance);
        tvSeekbarDistanceDisplay.setText(mDistance +" Km");
        sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekbarDistanceDisplay.setText(progress +" Km");
                mDistance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSharedEditor.putInt("distance",mDistance);
                mSharedEditor.commit();

            }
        });

        tvSeekBarPriceDisplay = (TextView)findViewById(R.id.tvSeekBarPriceDisplay);
        tvSeekBarPriceDisplay.setText(Integer.toString(mMaxHourPrice)+ " €/Hour");
        sbMaxPrice = (SeekBar)findViewById(R.id.sbMaxPrice);
        sbMaxPrice.setProgress(mMaxHourPrice);
        tvSeekBarPriceDisplay.setText(mMaxHourPrice +" €/Hour");
        sbMaxPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekBarPriceDisplay.setText(progress +" €/Hour");
                mMaxHourPrice = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("maxHourPrice",mMaxHourPrice);
                editor.commit();

            }
        });


        mIdLang = mOpenHelper.getIdLang(Locale.getDefault().toString());


        Log.e(LOG_TAG,"onCreate is called");

    }

    @Override
    public void onBackPressedImplementation() {
        super.onBackPressedImplementation();
        
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Carefull with shared preferences editor here, probably will have to put the changes on the editor when they happen
        //because of the lifgecycle with main activity

        Log.e(LOG_TAG + " maxPrice", Integer.toString(mMaxHourPrice));

   }

    @Override
    protected void onStart() {
        super.onStart();
        getSubjectAndSetUI();
        Log.e(LOG_TAG, "On start is called");

     }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(requestCode==SEARCH_LOCATION_REQUEST && resultCode==RESULT_OK)
        {
            lat = data.getStringExtra("lat");
            lng = data.getStringExtra("lng");
            mUseCurrentPosition = data.getIntExtra("useCurrentPosition",1);

            editor.putString("lat",lat);
            editor.putString("lng",lng);
            editor.putInt("useCurrentPosition", mUseCurrentPosition);
            editor.commit();
        }
        else if(requestCode==CATEGORY_AND_SUBJECT_REQUEST && resultCode==RESULT_OK)
        {
            mCategory = data.getStringExtra("category");
            mSubject = data.getStringExtra("subject");
            mIdCategory = data.getIntExtra("idCategory",0);
            mIdSubject = data.getIntExtra("idSubject",0);
            Log.e(LOG_TAG + "idSubjectOn Activity", mIdSubject + "");
            editor.putInt("idSubject",mIdSubject);
            editor.putString("subject",mSubject);
            editor.commit();

        }
    }

    /***
     * Menu and Navigation
     ***/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filters_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                resetFilters();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    private void getSubjectAndSetUI() {
        mCSubject = mOpenHelper.getReadableDatabase().query(FindTeacherContract.SubjectTransl.TABLE_NAME,
                new String[]{FindTeacherContract.SubjectTransl.COLUMN_NAME},
                FindTeacherContract.SubjectTransl.COLUMN_ID_SUBJECT + " = ? "
                        + " AND " + FindTeacherContract.SubjectTransl.COLUMN_ID_LANG + " = ? ",
                new String[]{Integer.toString(mIdSubject),Integer.toString(mIdLang)},
                null,
                null,
                null);

        if(mCSubject!=null && mCSubject.getCount()>0) {
            mCSubject.moveToFirst();
            mSubject = mCSubject.getString(mCSubject.getColumnIndex(FindTeacherContract.SubjectTransl.COLUMN_NAME));
            if (mSubject != null) {
                tvSubjectSelected.setText(mSubject);
            } else {
                tvSubjectSelected.setText("No subject selected");
            }
        }else{
            tvSubjectSelected.setText("No subject selected");
        }
        tvSeekbarDistanceDisplay.setText(Integer.toString(mDistance)+ " Km");
    }

    private void resetFilters() {
        mDomicilyServices = 0;
        mMaxHourPrice = 100;
        mIdSubject = 0;
        mDistance = 100;
        mUseCurrentPosition = 1;
        mMark = 0;
        mSharedEditor.putInt("idSubject",mIdSubject);
        mSharedEditor.putInt("maxHourPrice",mMaxHourPrice);
        mSharedEditor.putInt("distance",mDistance);
        mSharedEditor.putInt("domicilyServices",mDomicilyServices);
        mSharedEditor.putInt("useCurrentPosition", mUseCurrentPosition);
        mSharedEditor.putFloat("mark",mMark);
        mSharedEditor.commit();
        getSubjectAndSetUI();
        tvSeekbarDistanceDisplay.setText(Integer.toString(mDistance));
        sbDistance.setProgress(mDistance);
        tvSeekBarPriceDisplay.setText(Integer.toString(mMaxHourPrice));
        sbMaxPrice.setProgress(mMaxHourPrice);
        swDomicilyService.setChecked(false);
        swDomicilyService.setText(R.string.switch_domicily_services_false);
        ratingBar.setProgress((int)mMark);

    }
    
}
