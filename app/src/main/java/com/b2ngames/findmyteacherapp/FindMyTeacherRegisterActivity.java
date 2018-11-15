package com.b2ngames.findmyteacherapp;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class FindMyTeacherRegisterActivity extends AppCompatActivity {

    private Context mContext;
    private static final String LOG_TAG = FindMyTeacherRegisterActivity.class.getSimpleName();
    private static final String url = "https://www.findmyteacherapp.com/app/register_user.php";
    EditText mEtUserName ,mEtUserEmail, mEtPwd, mEtRepeatPwd;
    String mName, mEmail, mPwd;
    RelativeLayout mRlRegisterActivity,mRlWaititingCircle;
    ProgressBar mPbWaitingCicle;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_teacher_register);

        mContext = this;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mEtUserName = (EditText)findViewById(R.id.etUserName);
        mEtUserEmail = (EditText)findViewById(R.id.etUserEmail);
        mEtPwd = (EditText) findViewById(R.id.etPasword);
        mEtRepeatPwd = (EditText)findViewById(R.id.etRepeatPasword);
        mRlRegisterActivity = (RelativeLayout)findViewById(R.id.rlActivity);
        mRlWaititingCircle = (RelativeLayout)findViewById(R.id.rlWaitingCircle);
        mPbWaitingCicle = (ProgressBar)findViewById(R.id.pbWaitingCircle);

        Button register = (Button)findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mEtUserName.getText().toString();
                String psw = mEtPwd.getText().toString();
                String repeatPsw = mEtRepeatPwd.getText().toString();
                String userEmail = mEtUserEmail.getText().toString();
                Boolean emailIsValid = FindMyTeacherConstants.isEmailValid(userEmail);
                if(!userName.isEmpty()&&!psw.isEmpty()&& psw.equals(repeatPsw)&&emailIsValid) {
                    Log.e(LOG_TAG + " ENTRA", " REGISTER USER");
                    mRlRegisterActivity.setVisibility(View.GONE);
                    mRlWaititingCircle.setVisibility(View.VISIBLE);
                    registerUser();
                }else{
                    final DialogCustom dialogCustom = new DialogCustom(mContext,R.style.CustomDialog,R.layout.dialog_error);
                    dialogCustom.show();
                    dialogCustom.setOnDialogButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogCustom.dismiss();
                        }
                    });
                    if(userName.isEmpty()){
                        dialogCustom.setDialogMessage(R.string.error_message_user_name_is_empty);
                        mEtUserName.requestFocus();
                    }
                    else if(psw.isEmpty()){
                        dialogCustom.setDialogMessage(R.string.error_message_register_password_is_empty);
                        mEtPwd.requestFocus();
                    }
                    else if(!psw.equals(repeatPsw)){
                        dialogCustom.setDialogMessage(R.string.error_message_second_password_is_not_the_same);
                        mEtRepeatPwd.requestFocus();
                    }
                    else if(!emailIsValid){
                        dialogCustom.setDialogMessage(R.string.error_message_dialog_wrong_email);
                        mEtUserEmail.requestFocus();
                    }

                }
            }
        });
    }

    void parseUserRegisteredInfoJson(String jsonString, Vector<ContentValues> vecTeacherValues, StringBuilder userToken) {
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
                vecTeacherValues.add(teacherValue);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void registerUser() {
        StringRequest stringRequest = new FT_StringRequest(this, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            Log.e(LOG_TAG + " ONRESPONSE", s);
                            int inserted = 0;
                            Vector<ContentValues> vecTeacherValues = new Vector<>();
                            StringBuilder userToken = new StringBuilder();
                            parseUserRegisteredInfoJson(s, vecTeacherValues, userToken);
                            ContentValues[] cvArray = new ContentValues[vecTeacherValues.size()];
                            vecTeacherValues.toArray(cvArray);
                            inserted = getBaseContext().getContentResolver()
                                    .bulkInsert(FindTeacherContract.MyUserInfo.CONTENT_URI, cvArray);

                            SharedPreferences sharedPreferencesLogIn = getSharedPreferences("prefsLogin", Context.MODE_PRIVATE);
                            SharedPreferences.Editor sharedLoginEditor = sharedPreferencesLogIn.edit();
                            sharedLoginEditor.putInt(FindMyTeacherConstants.IS_LOGED, 1);
                            sharedLoginEditor.putString(FindMyTeacherConstants.TOKEN, userToken.toString());
                            String dateInString = new java.text.SimpleDateFormat(FindMyTeacherConstants.TOKEN_DATE_FORMAT)
                                    .format(Calendar.getInstance().getTime());
                            sharedLoginEditor.putString(FindMyTeacherConstants.TOKEN_DATE, dateInString);
                            sharedLoginEditor.commit();

                            // Date dtTime = Date.parse(sharedPreferencesLogIn.getString(FindMyTeacherConstants.TOKEN_DATE, ""));
                            Intent intent = new Intent(mContext, UserAreaActivity.class);
                            startActivity(intent);

                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        finally {
                            finish();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FindMyTeacherRegisterActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();
                mName = mEtUserName.getText().toString();
                mEmail = mEtUserEmail.getText().toString();
                mPwd = mEtPwd.getText().toString();
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date currentTime = Calendar.getInstance().getTime();
                String reportDate = df.format(currentTime);
                Log.e(LOG_TAG + " Date",reportDate);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                String tokenFirebase = sharedPreferences.getString("tokenFirebase",null);
                Log.e(LOG_TAG + " device token: ", tokenFirebase);
                String lat = mSharedPreferences.getString("lat","41.369595");
                String lng = mSharedPreferences.getString("lng","2.168427");
                Log.e(LOG_TAG , lat + lng);
                params.put("userName",mName);
                params.put("email",mEmail);
                params.put("tokenFirebase",tokenFirebase);
                params.put("dateToken",reportDate);
                params.put("lat",lat);
                params.put("lng",lng);


                // Hash Encryption
                KeySpec spec = new PBEKeySpec(mPwd.toCharArray(), "FINDMYTEACHER".getBytes(), 8192, 36*8);//, "FINDMYTEACHER", 65536, 128);
                try {
                    SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                    byte[] pwdHash = f.generateSecret(spec).getEncoded();
                    String base64Hash = Base64.encodeToString(pwdHash, Base64.DEFAULT);
                    Log.e(LOG_TAG, "HashReg: " + base64Hash);
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
            Toast.makeText(mContext,"Register not valid",Toast.LENGTH_SHORT);
        }
    }


}
