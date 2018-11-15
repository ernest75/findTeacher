package com.b2ngames.findmyteacherapp.volleyhelper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.FindMyTeacherLoginActivity;
import com.b2ngames.findmyteacherapp.MainActivity;
import com.b2ngames.findmyteacherapp.R;
import com.b2ngames.findmyteacherapp.Utilities.DialogCustom;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by xavi on 16/04/2017.
 */

public class FT_LoginStringRequest{
    private static final String LOG_TAG = FT_LoginStringRequest.class.getSimpleName();
    private static final int RESURRECTION_INTENT_ID = 998877;

    StringRequest mStringRequest;
    Activity mActContext;
    int mRequestMethod;
    String mUrl;
    Response.Listener<String> mOkListener;
    Response.ErrorListener mErrorListener;
    private FindTeacherDbHelper mOpenHelper;

    public FT_LoginStringRequest(Activity actContext, int requestMethod, String url, final Response.Listener<String> okListener, final Response.ErrorListener errorListener){
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
                SharedPreferences shPrefsL = mActContext.getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE);
                String token = shPrefsL.getString(FindMyTeacherConstants.TOKEN, "");
                String strTokenDate = shPrefsL.getString(FindMyTeacherConstants.TOKEN_DATE, "");
//                SimpleDateFormat formatter = new SimpleDateFormat(FindMyTeacherConstants.TOKEN_DATE_FORMAT);
//                try {
//                    Date tokenDate = formatter.parse(strTokenDate);
//                    Date currDate = Calendar.getInstance().getTime();
//
//                    if(((currDate.getTime()-tokenDate.getTime())/1000.0f) > FindMyTeacherConstants.TOKEN_SECONDS_TO_UPDATE){
//
//                        throw new AuthFailureError("Error: Token need refresh.");
//                    }
//
//                } catch(ParseException e){
//                    throw new AuthFailureError("Error: Token date no parseable.");
//                }


                Cursor c_myuserinfo = mActContext.getContentResolver().query(
                                                FindTeacherContract.MyUserInfo.CONTENT_URI,
                                                new String[]{FindTeacherContract.MyUserInfo.COLUMN_EMAIL},
                                                null,
                                                null,
                                                null

                                        );
                if(c_myuserinfo.getCount() == 1) {
                    c_myuserinfo.moveToFirst();
                    String email = c_myuserinfo.getString(c_myuserinfo.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_EMAIL));
                    params.put("email", email);
                    Log.e(LOG_TAG, "TOKEN: " + token);
                    Log.e(LOG_TAG, "EMAIL: " + email);
                    params.put("token", token);
                    params.put("tokenDate",strTokenDate);
                }

                return params;
            }
        };
    }

    public void onVolleyError(VolleyError error){
        mErrorListener.onErrorResponse(error);
    }

    public void onServerResponse(String s){
        Log.e(LOG_TAG, "Server FT_LoginResponse: " + s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("data_query")) {
                if(jsonObject.has(FindMyTeacherConstants.TOKEN)){
                    String newToken = jsonObject.getString(FindMyTeacherConstants.TOKEN);
                    SharedPreferences sharedPreferencesLogIn = mActContext.getSharedPreferences("prefsLogin", Context.MODE_PRIVATE);
                    SharedPreferences.Editor sharedLoginEditor = sharedPreferencesLogIn.edit();
                    sharedLoginEditor.putInt(FindMyTeacherConstants.IS_LOGED,1);
                    sharedLoginEditor.putString(FindMyTeacherConstants.TOKEN, newToken);
                    String dateInString = new java.text.SimpleDateFormat(FindMyTeacherConstants.TOKEN_DATE_FORMAT)
                            .format(Calendar.getInstance().getTime());
                    sharedLoginEditor.putString(FindMyTeacherConstants.TOKEN_DATE,dateInString);
                    sharedLoginEditor.commit();
                }
                mOkListener.onResponse(jsonObject.getString("data_query"));
            } else if(jsonObject.has("server_fixing")){
                // TODO XLLR: show a message that currently the server is under mantainment
            } else if(jsonObject.has("ErrorAuth")){
                //iniciar activiyt login??
                Intent intent = new Intent(mActContext,FindMyTeacherLoginActivity.class);
                mActContext.startActivity(intent);
                Toast.makeText(mActContext," Too many days since your last Login, need to Login again",Toast.LENGTH_LONG).show();
                performLogout(mActContext);
                mActContext.finish();

                //onVolleyError(new VolleyError(jsonObject.getString("ErrorAuth")));

            }else if(jsonObject.has("db_upgrade_script")){
                String dbScript = jsonObject.getString("db_upgrade_script");
                Log.e(LOG_TAG, dbScript);
                mOpenHelper.upgradeDatabase(new FindTeacherDbHelper.UpgradeDatabaseListener(){
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
            else if(jsonObject.has("error")) {
//                final Dialog dialog = new Dialog(mActContext, R.style.CustomDialog);
                final RelativeLayout mRlWaitingCircle = (RelativeLayout) mActContext.findViewById(R.id.rlWaitingCircle);
                final RelativeLayout mRlActivity = (RelativeLayout) mActContext.findViewById(R.id.rlActivity);
                final EditText etUserEmail = (EditText) mActContext.findViewById(R.id.etUserEmail);
                final EditText etUserPassword = (EditText) mActContext.findViewById(R.id.etPasword);
                final String error = jsonObject.getString("error");
                final DialogCustom dialog = new DialogCustom(mActContext, R.style.CustomDialog, R.layout.dialog_error);

                dialog.setOnDialogButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        mRlWaitingCircle.setVisibility(View.INVISIBLE);
                        mRlActivity.setVisibility(View.VISIBLE);
                    }
                });


                if (error.equals("0")) {
                    dialog.setDialogMessage(R.string.error_message_email_is_not_on_db);
                    //tvDialog.setText();
//                    etUserEmail.requestFocus();
//                    InputMethodManager imm = (InputMethodManager) mActContext.getSystemService
//                            (Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(etUserEmail, InputMethodManager.SHOW_FORCED);
                    etUserEmail.setText("");


                } else if (error.equals("1")) {
                    dialog.setDialogMessage(R.string.error_message_wrong_password);
                    etUserPassword.setText("");
                    etUserPassword.requestFocus();
                } else if (error.equals("2")) {
                    dialog.setDialogMessage(R.string.error_message_email_is_already_on_db);
                    etUserEmail.setText("");
                    etUserEmail.requestFocus();
                }

                dialog.show();
            }

        } catch(JSONException je){
            je.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

    public static void performLogout(Activity _actContext) throws Exception {
        try {
            SharedPreferences.Editor editor = _actContext.getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE).edit();
            if (_actContext.getContentResolver().delete(FindTeacherContract.MyUserInfo.CONTENT_URI, null, null) == 1) {
                editor.putInt(FindMyTeacherConstants.IS_LOGED, 0);
                editor.putBoolean("reload", false);
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
