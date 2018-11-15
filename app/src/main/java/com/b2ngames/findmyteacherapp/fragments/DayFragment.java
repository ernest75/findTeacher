package com.b2ngames.findmyteacherapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.MainActivity;
import com.b2ngames.findmyteacherapp.R;
import com.b2ngames.findmyteacherapp.TeacherInfoActivity;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_LoginStringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Ernest on 15/06/2017.
 */

public class DayFragment extends Fragment {
    protected Switch mSwMorning;
    protected Switch mSwMidday;
    protected Switch mSwAfternoon;
    protected Switch mSwNight;
    protected int mIdDay;
    protected FindTeacherDbHelper mOpenHelper;
    protected int[] mDayTimes;
    protected int mIdTeacher;
    protected boolean mCanUpdateAvailability; // if true the user can change the parameters
    private static final String LOG_TAG = DayFragment.class.getSimpleName();
    Cursor mCGetTimesCheckByDay;
    protected ArrayList<Switch> mSwitches;
    protected boolean[] mDayTimesBool;
    private static final String urlInsert = "https://www.findmyteacherapp.com/app/insert_teacher_availability.php";
    private static final String urlDelete = "https://www.findmyteacherapp.com/app/delete_teacher_availability.php";
    Context mContext;
//    private static final String urlgetAvailability = "https://www.findmyteacherapp.com/app/getTeacherAvailability.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mDayTimesBool = new boolean[4];
        mDayTimes = new int[4];
        mSwitches = new ArrayList<Switch>();
        Bundle args = getArguments();
        mIdDay = args.getInt("day", 0);
        mIdTeacher = args.getInt("teacher", -1);
        mCanUpdateAvailability = new Boolean(args.getBoolean("canUpdate", false));
        Log.e(LOG_TAG + " updateavai", mCanUpdateAvailability + "");
        mOpenHelper = new FindTeacherDbHelper(getActivity());
        if (mCanUpdateAvailability) {
            mCGetTimesCheckByDay = mOpenHelper.getReadableDatabase().query(
                    FindTeacherContract.MyAvailability.TABLE_NAME,
                    new String[]{FindTeacherContract.MyAvailability.COLUMN_ID_TIME},
                    FindTeacherContract.MyAvailability.COLUMN_ID_DAY + " =? ",
                    new String[]{Integer.toString(mIdDay)},
                    null,
                    null,
                    null);

            if (mCGetTimesCheckByDay.getCount() > 0) {
                mCGetTimesCheckByDay.moveToFirst();
                int idTime = mCGetTimesCheckByDay.getInt(mCGetTimesCheckByDay.
                        getColumnIndex(FindTeacherContract.MyAvailability.COLUMN_ID_TIME));
                mDayTimesBool[idTime - 1] = true;

                while (mCGetTimesCheckByDay.moveToNext()) {

                    idTime = mCGetTimesCheckByDay.getInt(mCGetTimesCheckByDay.
                            getColumnIndex(FindTeacherContract.MyAvailability.COLUMN_ID_TIME));
                    mDayTimesBool[idTime - 1] = true;
                }
            }
        } else {

            mCGetTimesCheckByDay = mOpenHelper.getReadableDatabase().query(
                    FindTeacherContract.TeacherAvailability.TABLE_NAME,
                    new String[]{FindTeacherContract.TeacherAvailability.COLUMN_ID_TIME},
                    FindTeacherContract.TeacherAvailability.COLUMN_ID_DAY + " =? AND " +
                            FindTeacherContract.TeacherAvailability.COLUMN_ID_TEACHER + " =? ",
                    new String[]{Integer.toString(mIdDay),Integer.toString(mIdTeacher)},
                    null,
                    null,
                    null);
            Log.e(LOG_TAG + " CURSOR SIZE",mCGetTimesCheckByDay.getCount()+"");
            if (mCGetTimesCheckByDay.getCount() > 0) {
                mCGetTimesCheckByDay.moveToFirst();
                int idTime = mCGetTimesCheckByDay.getInt(mCGetTimesCheckByDay.
                        getColumnIndex(FindTeacherContract.TeacherAvailability.COLUMN_ID_TIME));
                mDayTimesBool[idTime - 1] = true;
                while (mCGetTimesCheckByDay.moveToNext()) {
                    idTime = mCGetTimesCheckByDay.getInt(mCGetTimesCheckByDay.
                            getColumnIndex(FindTeacherContract.TeacherAvailability.COLUMN_ID_TIME));
                    mDayTimesBool[idTime - 1] = true;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.time_day_options, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Handle here code for this fragment
        mSwMorning = (Switch)getView().findViewById(R.id.swMorning);
        mSwMidday = (Switch)getView().findViewById(R.id.swMidday);
        mSwAfternoon = (Switch)getView().findViewById(R.id.swAfternoon);
        mSwNight = (Switch)getView().findViewById(R.id.swNight);

        mSwitches.add(0,mSwMorning);
        mSwitches.add(1,mSwMidday);
        mSwitches.add(2,mSwAfternoon);
        mSwitches.add(3,mSwNight);

        mSwMorning.setClickable(mCanUpdateAvailability);
        mSwMidday.setClickable(mCanUpdateAvailability);
        mSwAfternoon.setClickable(mCanUpdateAvailability);
        mSwNight.setClickable(mCanUpdateAvailability);

        //boolean BMorning = setTimeZone(mDayTimes[0]);
        for(int i = 0 ; i< mSwitches.size(); i++){
            mSwitches.get(i).setChecked(mDayTimesBool[i]);
            if(mDayTimesBool[i]){
                mSwitches.get(i).setText(getResources().getString(R.string.switch_domicily_services_true));
            }else{
                mSwitches.get(i).setText(getResources().getString(R.string.switch_domicily_services_false));
            }
        }


        if(mCanUpdateAvailability){
            for(int i = 0 ; i < mSwitches.size() ; i++){
                setSwitchListener(mSwitches.get(i),i+1);
            }

        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void setSwitchListener(final Switch switchTime, final int idTime) {
        switchTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchTime.setText(getResources().getString(R.string.switch_domicily_services_true));
                    insertAvailabilityOnServer(idTime);

                } else {
                    switchTime.setText(getResources().getString(R.string.switch_domicily_services_false));
                    deleteAvailabilityOnServer(idTime);
                }

            }
        });
    }

    @Override
    public void onStop() {
        mSwitches.clear();
        mCGetTimesCheckByDay.close();
        super.onStop();
    }
    public void deleteAvailabilityOnServer(final int idTime) {
        Log.e(LOG_TAG , "INSIDE OBTAIN");
        StringRequest stringRequest = new FT_LoginStringRequest(getActivity(), Request.Method.POST, urlDelete, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e(LOG_TAG + "ONRESPONSE", s);
                Log.e(LOG_TAG , "INSIDE ON RESPONSE");
                Log.e(LOG_TAG , mIdDay + "");
                Log.e(LOG_TAG , idTime + "");

                int deleted = getContext().getContentResolver().delete(
                        FindTeacherContract.MyAvailability.CONTENT_URI,
                        FindTeacherContract.MyAvailability.COLUMN_ID_DAY + " = ? AND " +
                                FindTeacherContract.MyAvailability.COLUMN_ID_TIME + " = ? ",
                        new String[]{Integer.toString(mIdDay),Integer.toString(idTime)});

                Log.e(LOG_TAG, deleted + "");

            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();

                params.put("idDay",Integer.toString(mIdDay));
                params.put("idTime",Integer.toString(idTime));
                params.put("idTeacher",Integer.toString(mIdTeacher));

                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(),"Teachers request not valid",Toast.LENGTH_SHORT);
        }

    }

    void parseTeachersAvailabilityJson(String jsonString, Vector<ContentValues> vecTeacherValues) {
        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                int idDay = jo.getInt(FindTeacherContract.MyAvailability.COLUMN_ID_DAY);
                int idTime = jo.getInt(FindTeacherContract.MyAvailability.COLUMN_ID_TIME);
                ContentValues teacherValue = new ContentValues();
                teacherValue.put(FindTeacherContract.MyAvailability.COLUMN_ID_DAY, idDay);
                teacherValue.put(FindTeacherContract.MyAvailability.COLUMN_ID_TIME, idTime);
                vecTeacherValues.add(teacherValue);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }
    }

    public void insertAvailabilityOnServer(final int idTime) {
        Log.e(LOG_TAG , "INSIDE OBTAIN");

        StringRequest stringRequest = new FT_LoginStringRequest(getActivity(), Request.Method.POST, urlInsert, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Vector<ContentValues> vecTeacherValues = new Vector<>();
                parseTeachersAvailabilityJson(s,vecTeacherValues);
                ContentValues[] cvArray = new ContentValues[vecTeacherValues.size()];
                vecTeacherValues.toArray(cvArray);
                int inserted = mContext.getContentResolver().bulkInsert(
                        FindTeacherContract.MyAvailability.CONTENT_URI,
                        cvArray);
                Log.e(LOG_TAG + "ONRESPONSE", s);

            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();

                params.put("idDay",Integer.toString(mIdDay));
                params.put("idTime",Integer.toString(idTime));
                params.put("idTeacher",Integer.toString(mIdTeacher));

                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(),"Teachers request not valid",Toast.LENGTH_SHORT);
        }

    }

}
