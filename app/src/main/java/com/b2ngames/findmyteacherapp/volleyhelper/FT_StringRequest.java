package com.b2ngames.findmyteacherapp.volleyhelper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.MainActivity;
import com.b2ngames.findmyteacherapp.R;
import com.b2ngames.findmyteacherapp.Utilities.DialogCustom;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by xavi on 16/04/2017.
 */

public class FT_StringRequest{
    private static final String LOG_TAG = FT_StringRequest.class.getSimpleName();
    private static final int RESURRECTION_INTENT_ID = 998877;

    StringRequest mStringRequest;
    Activity mActContext;
    int mRequestMethod;
    String mUrl;
    Response.Listener<String> mOkListener;
    Response.ErrorListener mErrorListener;
    private FindTeacherDbHelper mOpenHelper;



    public FT_StringRequest(Activity actContext, int requestMethod, String url, final Response.Listener<String> okListener, final Response.ErrorListener errorListener){
        mActContext = actContext;
        mRequestMethod = requestMethod;
        mUrl = url;
        mOkListener = okListener;
        mErrorListener = errorListener;
        mOpenHelper = new FindTeacherDbHelper(actContext);

        mStringRequest = new StringRequest(mRequestMethod, mUrl,
           new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    onServerResponse(s);
                }
           },
           new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    onVolleyError(volleyError);
                }
           }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = getParamsQuery();

                Cursor c = mOpenHelper.getReadableDatabase().rawQuery(
                        "SELECT " + FindTeacherContract.SyncServerDeviceDB.COLUMN_VERSION + " FROM " +
                                    FindTeacherContract.SyncServerDeviceDB.TABLE_NAME, null);

                if(c.getCount() != 1){
                    throw new AuthFailureError("Error in Database Upgrade, Current Database is corrupted.");
                }

                // Database upgrade revision
                int currentVersion = 0;
                int idx = c.getColumnIndex(FindTeacherContract.SyncServerDeviceDB.COLUMN_VERSION);
                if(c.getCount() != 0) {
                    c.moveToFirst();

                    currentVersion = c.getInt(idx);
                }

                params.put("db_version", Integer.toString(currentVersion));
                params.put("lang", Locale.getDefault().toString());

                // Login Info
                return params;
            }
        };
    }

    // TOdo ERNEST: Introduced the method onVolleyError and onServerResponse
    public void onVolleyError(VolleyError error){
        mErrorListener.onErrorResponse(error);
    }

    public void onServerResponse(String s){
        Log.e(LOG_TAG, "Server FT_Response: " + s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("data_query")) {
                mOkListener.onResponse(jsonObject.getString("data_query"));
            } else if(jsonObject.has("server_fixing")){
                // TODO XLLR: show a message that currently the server is under mantainment
            } else if(jsonObject.has("db_upgrade_script")) {
                String dbScript = jsonObject.getString("db_upgrade_script");
                Log.e(LOG_TAG, dbScript);
                mOpenHelper.upgradeDatabase(new FindTeacherDbHelper.UpgradeDatabaseListener() {
                    @Override
                    public void onPostDatabaseUpgrade() {
                        Intent mStartActivity = new Intent(mActContext, MainActivity.class);
                        int mPendingIntentId = RESURRECTION_INTENT_ID;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(mActContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) mActContext.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
                        mActContext.finishAffinity();
                    }
                }, dbScript);
            }
              else if(jsonObject.has("error")){
//                final Dialog dialog = new Dialog(mActContext, R.style.CustomDialog);
                final RelativeLayout mRlWaitingCircle = (RelativeLayout) mActContext.findViewById(R.id.rlWaitingCircle);
                final RelativeLayout mRlActivity = (RelativeLayout) mActContext.findViewById(R.id.rlActivity);
                final EditText etUserEmail = (EditText) mActContext.findViewById(R.id.etUserEmail);
                final EditText etUserPassword = (EditText) mActContext.findViewById(R.id.etPasword);
                final String error = jsonObject.getString("error");
                final DialogCustom dialog = new DialogCustom(mActContext,R.style.CustomDialog,R.layout.dialog_error);

                dialog.setOnDialogButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        mRlWaitingCircle.setVisibility(View.INVISIBLE);
                        mRlActivity.setVisibility(View.VISIBLE);
                    }
                });



                if(error.equals("0")){
                    dialog.setDialogMessage(R.string.error_message_email_is_not_on_db);
                    //tvDialog.setText();
//                    etUserEmail.requestFocus();
//                    InputMethodManager imm = (InputMethodManager) mActContext.getSystemService
//                            (Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(etUserEmail, InputMethodManager.SHOW_FORCED);
                    etUserEmail.setText("");


                }
                else if(error.equals("1"))
                {
                    dialog.setDialogMessage(R.string.error_message_wrong_password);
                    etUserPassword.setText("");
                    etUserPassword.requestFocus();
                }
                else if(error.equals("2")){
                    dialog.setDialogMessage(R.string.error_message_email_is_already_on_db);
                    etUserEmail.setText("");
                    etUserEmail.requestFocus();
                }

                dialog.show();


            }else {
                Log.e(LOG_TAG, "Parse Error Json");
            }
        } catch(JSONException je){
            je.printStackTrace();
        }
    }

    protected Map<String,String> getParamsQuery() {
        return new HashMap<>();
    }

    protected void updateDatabase(String sqlScript){
        mOpenHelper.getWritableDatabase().execSQL(sqlScript);
    }

    public StringRequest getStringRequest(){
        return mStringRequest;
    }
}
