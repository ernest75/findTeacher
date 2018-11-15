package com.b2ngames.findmyteacherapp;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.Utilities.DialogCustom;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.KeySpec;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class FindMyTeacherLoginActivity extends AppCompatActivity {

    private Context mContext;
    private static final String LOG_TAG = FindMyTeacherLoginActivity.class.getSimpleName();
    //// TODO: 19/07/2017 Fer binding de login user quant estigui implementat
    private static final String urlLogin = "https://www.findmyteacherapp.com/app/login_user.php";
    private static final String urlMySubjects = "https://www.findmyteacherapp.com/app/insert_my_subjects_on_login.php";
    private static final String urlMyAvailability = "https://www.findmyteacherapp.com/app/insert_my_availability_on_login.php";
    SharedPreferences mSharedPreferencesLogIn;
    RelativeLayout mRlLoginActivity,mRlWaititingCircle;
    ProgressBar mPbWaitingCicle;
    Integer mIdUserRemote, mIsTeacher;
    EditText mEtUserEmail, mEtUserPwd;
    DialogCustom mDialogError;
    Button mBtnOkDialogError;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_teacher_login);

        mContext = this;
        mSharedPreferencesLogIn = getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE);
        mDialogError = new DialogCustom(mContext,R.style.CustomDialog,R.layout.dialog_error);

        mRlLoginActivity = (RelativeLayout)findViewById(R.id.rlActivity);
        mRlWaititingCircle = (RelativeLayout)findViewById(R.id.rlWaitingCircle);
        mRlWaititingCircle.setVisibility(View.GONE);
        mPbWaitingCicle = (ProgressBar)findViewById(R.id.pbWaitingCircle);

        mEtUserEmail = (EditText)findViewById(R.id.etUserEmail);
        mEtUserPwd = (EditText)findViewById(R.id.etPasword);
        mBtnOkDialogError = (Button) mDialogError.findViewById(R.id.btnOkDialogError);
        
        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,FindMyTeacherRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEtUserEmail.getText().toString();
                String password = mEtUserPwd.getText().toString();
                Log.e(LOG_TAG,email);
                final Boolean emailIsValid = FindMyTeacherConstants.isEmailValid(email);
                if(emailIsValid && !password.isEmpty()) {
                    mRlLoginActivity.setVisibility(View.GONE);
                    mRlWaititingCircle.setVisibility(View.VISIBLE);
                    loginUser();

                }else{
                    mDialogError.show();
                    if(!emailIsValid){
                        mDialogError.setDialogMessage(R.string.error_message_dialog_wrong_email);
                    }

                   else if(password.isEmpty()){
                        mDialogError.setDialogMessage(R.string.error_message_password_is_empty);
                        mEtUserPwd.requestFocus();
                    }

                    mBtnOkDialogError.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogError.dismiss();
                            if(!emailIsValid) {
                                mEtUserEmail.requestFocus();
                                mEtUserEmail.setText("");
                            }else{
                                mEtUserPwd.requestFocus();
                            }

                        }
                    });
                }
            }
        });
    }

    void parseUserLoginInfoJson(String jsonString, Vector<ContentValues> vecTeacherValues, StringBuffer userToken) {
        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                String userName = jo.getString(FindTeacherContract.MyUserInfo.COLUMN_USER_NAME);
                String url = jo.getString(FindTeacherContract.MyUserInfo.COLUMN_URL_IMAGE);
                int idRemote = jo.getInt("id");
                float mark = (float)jo.getDouble(FindTeacherContract.MyUserInfo.COLUMN_MARK);
                int priceHour = jo.getInt(FindTeacherContract.MyUserInfo.COLUMN_PRICE_HOUR);
                String userEmail = jo.getString(FindTeacherContract.MyUserInfo.COLUMN_EMAIL);
                String userAdress = jo.getString(FindTeacherContract.MyUserInfo.COLUMN_ADRESS);
                int isTeacher = jo.getInt(FindTeacherContract.MyUserInfo.COLUMN_IS_TEACHER);
                userToken.append(jo.getString("token"));

                Log.e("Url parsejada", url);
                ContentValues teacherValue = new ContentValues();
                teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_PRICE_HOUR, priceHour);
                teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_USER_NAME, userName);
                teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_URL_IMAGE, url);
                teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE, idRemote);
                teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_MARK, mark);
                teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_EMAIL, userEmail);
                teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_ADRESS, userAdress);
                teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_IS_TEACHER, isTeacher);

                if(isTeacher == 1) {

                    if(!jo.get(FindTeacherContract.MyUserInfo.COLUMN_LAT).equals(null)||
                            !jo.get(FindTeacherContract.MyUserInfo.COLUMN_LNG).equals(null)){
                        double lat = jo.getDouble(FindTeacherContract.MyUserInfo.COLUMN_LAT);
                        double lng = jo.getDouble(FindTeacherContract.MyUserInfo.COLUMN_LNG);
                        teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_LAT, lat);
                        teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_LNG, lng);


                    }
                    int mobility = jo.getInt(FindTeacherContract.MyUserInfo.COLUMN_MOBILITY);


                    teacherValue.put(FindTeacherContract.MyUserInfo.COLUMN_MOBILITY, mobility);
                }
                vecTeacherValues.add(teacherValue);
                mIdUserRemote = idRemote;
                mIsTeacher = isTeacher;
                SharedPreferences.Editor editor = mSharedPreferencesLogIn.edit();
                editor.putInt(FindMyTeacherConstants.MY_SERVER_ID,mIdUserRemote);
                editor.commit();
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void loginUser() {
        Log.e(LOG_TAG , "INSIDE OBTAIN");
        // TODO: If user is logged, logout
        StringRequest stringRequest = new FT_StringRequest(this, Request.Method.POST, urlLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            Log.e(LOG_TAG + "ONRESPONSE", s);
                            int inserted = 0;
                            Vector<ContentValues> vecTeacherValues = new Vector<>();
                            StringBuffer userToken = new StringBuffer("");
                            parseUserLoginInfoJson(s, vecTeacherValues, userToken);
                            Log.e(LOG_TAG + "USER REMOTE", mIdUserRemote +"");
                            ContentValues[] cvArray = new ContentValues[vecTeacherValues.size()];
                            vecTeacherValues.toArray(cvArray);
                            inserted = getBaseContext().getContentResolver()
                                    .bulkInsert(FindTeacherContract.MyUserInfo.CONTENT_URI, cvArray);

                            if (inserted == 1) {
                                SharedPreferences sharedPreferencesLogIn = getSharedPreferences("prefsLogin", Context.MODE_PRIVATE);
                                SharedPreferences.Editor sharedLoginEditor = sharedPreferencesLogIn.edit();
                                sharedLoginEditor.putInt(FindMyTeacherConstants.IS_LOGED,1);
                                sharedLoginEditor.putString(FindMyTeacherConstants.TOKEN, userToken.toString());
                                String dateInString = new java.text.SimpleDateFormat(FindMyTeacherConstants.TOKEN_DATE_FORMAT)
                                        .format(Calendar.getInstance().getTime());
                                sharedLoginEditor.putString(FindMyTeacherConstants.TOKEN_DATE,dateInString);
                                sharedLoginEditor.commit();
                                if (mIsTeacher == 1) {
                                    insertMySubjects();
                                    insertMyAvailability();
                                }
                                Intent intent = new Intent(mContext, UserAreaActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                throw new Exception("Error inserting in local database.");
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                            Log.e(LOG_TAG, e.toString());
                        } finally{
                            finish();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FindMyTeacherLoginActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                String tokenFirebase = sharedPreferences.getString("tokenFirebase",null);
                Log.e(LOG_TAG + "tokenfirebase",tokenFirebase);
                String userEmail = mEtUserEmail.getText().toString();
                String userPwd = mEtUserPwd.getText().toString();
                params.put("tokenFirebase",tokenFirebase);
                params.put("email",userEmail);

                // Hash Encryption
                KeySpec spec = new PBEKeySpec(userPwd.toCharArray(), "FINDMYTEACHER".getBytes(), 8192, 36*8);//, "FINDMYTEACHER", 65536, 128);
                try {
                    SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                    byte[] pwdHash = f.generateSecret(spec).getEncoded();
                    String base64Hash = Base64.encodeToString(pwdHash, Base64.DEFAULT);
                    //String pwdHash_utf8 = new String(pwdHash, StandardCharsets.UTF_8);
                    //String pwdHash_ISO_8859_1 = new String(pwdHash, StandardCharsets.ISO_8859_1);
                    Log.e(LOG_TAG, "HashLog: " + base64Hash);
                    params.put("pwd", base64Hash);

                } catch(Exception e){
                    e.printStackTrace();
                }

                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,"Login not valid",Toast.LENGTH_SHORT);
        }
    }

    void parseMySubjectsInfoJson(String jsonString, Vector<ContentValues> vecTeacherValues) {
        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                int idSubject = jo.getInt(FindTeacherContract.MySubjects.COLUMN_ID_SUBJECT);
                int priceHour = jo.getInt(FindTeacherContract.MySubjects.COLUMN_PRICE_HOUR);
                String classDescription = jo.getString(FindTeacherContract.MySubjects.COLUMN_CLASS_DESCRIPTION);
                String experience = jo.getString(FindTeacherContract.MySubjects.COLUMN_EXPERIENCE);
                int idTeacherSubjectRemote = jo.getInt("id");
                Log.e("Url parsejada", urlLogin);
                ContentValues teacherValue = new ContentValues();
                teacherValue.put(FindTeacherContract.MySubjects.COLUMN_PRICE_HOUR, priceHour);
                teacherValue.put(FindTeacherContract.MySubjects.COLUMN_ID_SUBJECT, idSubject);
                teacherValue.put(FindTeacherContract.MySubjects.COLUMN_CLASS_DESCRIPTION,classDescription);
                teacherValue.put(FindTeacherContract.MySubjects.COLUMN_EXPERIENCE,experience);
                teacherValue.put(FindTeacherContract.MySubjects.COLUMN_ID_TEACHER_SUBJECT_REMOTE,idTeacherSubjectRemote);

                vecTeacherValues.add(teacherValue);

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void insertMySubjects(){
        StringRequest stringRequest = new FT_StringRequest(this, Request.Method.POST, urlMySubjects,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.e(LOG_TAG + " ONRESPONSE INSERT SUBS", s);
                        int inserted = 0;
                        Vector<ContentValues> vecTeacherValues = new Vector<>();
                        parseMySubjectsInfoJson(s, vecTeacherValues);
                        ContentValues[] cvArray = new ContentValues[vecTeacherValues.size()];
                        vecTeacherValues.toArray(cvArray);
                        inserted = getBaseContext().getContentResolver()
                                .bulkInsert(FindTeacherContract.MySubjects.CONTENT_URI, cvArray);

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(FindMyTeacherLoginActivity.this, volleyError.getMessage().toString(),
                                Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("idUser",Integer.toString(mIdUserRemote));
                Log.e(LOG_TAG,mIdUserRemote + "");
                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,"Login not valid",Toast.LENGTH_SHORT);
        }
    }

    void parseMyAvailabilityInfoJson(String jsonString, Vector<ContentValues> vecTeacherValues) {
        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                int idDay = jo.getInt(FindTeacherContract.MyAvailability.COLUMN_ID_DAY);
                int idTime = jo.getInt(FindTeacherContract.MyAvailability.COLUMN_ID_TIME);
                Log.e("Url parsejada", urlLogin);
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

    public void insertMyAvailability(){
        StringRequest stringRequest = new FT_StringRequest(this, Request.Method.POST, urlMyAvailability,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.e(LOG_TAG + "ONRESPONSE", s);
                        int inserted = 0;
                        Vector<ContentValues> vecTeacherValues = new Vector<>();
                        parseMyAvailabilityInfoJson(s, vecTeacherValues);
                        ContentValues[] cvArray = new ContentValues[vecTeacherValues.size()];
                        vecTeacherValues.toArray(cvArray);
                        inserted = getBaseContext().getContentResolver()
                                .bulkInsert(FindTeacherContract.MyAvailability.CONTENT_URI, cvArray);
                        Log.e(LOG_TAG + "inserted", Integer.toString(inserted));

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(FindMyTeacherLoginActivity.this, volleyError.getMessage().toString(),
                                Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("idUser",Integer.toString(mIdUserRemote));
                Log.e(LOG_TAG + " AVAIL" , mIdUserRemote + "");
                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,"Login not valid",Toast.LENGTH_SHORT);
        }
    }

}
