package com.b2ngames.findmyteacherapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.Utilities.DialogCustom;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_LoginStringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import java.security.spec.KeySpec;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class ChangePasswordActivity extends FT_ActivityBase {

    private static final String LOG_TAG = ChangePasswordActivity.class.getSimpleName().toString();
    private static final String urlChangePssw = "https://www.findmyteacherapp.com/app/change_password.php";
    Button mBtnSubmit;
    EditText mEtEmail, mEtOldPsw, mEtNewPsw, mEtRepNewPsw;
    String mOldPssw, mEmail, mNewPssw, mRepNewPssw;
    RelativeLayout mRlRegisterActivity,mRlWaititingCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setSlideMenu();

        //getting references
        mBtnSubmit = (Button)findViewById(R.id.btnSubmit);
        mEtEmail = (EditText)findViewById(R.id.etUserEmail);
        mEtOldPsw = (EditText)findViewById(R.id.etPasword);
        mEtNewPsw = (EditText)findViewById(R.id.etNewPassword);
        mEtRepNewPsw= (EditText)findViewById(R.id.etRepeatNewPassword);

        mRlRegisterActivity = (RelativeLayout)findViewById(R.id.rlActivity);
        mRlWaititingCircle = (RelativeLayout)findViewById(R.id.rlWaitingCircle);


        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mEtEmail.getText().toString();
                mOldPssw = mEtOldPsw.getText().toString();
                mNewPssw = mEtNewPsw.getText().toString();
                mRepNewPssw = mEtRepNewPsw.getText().toString();
                if(FindMyTeacherConstants.isEmailValid(mEmail)&&mNewPssw.equals(mRepNewPssw)) {
                    mRlRegisterActivity.setVisibility(View.GONE);
                    mRlWaititingCircle.setVisibility(View.VISIBLE);
                    changePassword();
                }else {
                    final DialogCustom dialogCustom = new DialogCustom(mContext,R.style.CustomDialog,R.layout.dialog_error);
                    dialogCustom.show();
                    dialogCustom.setOnDialogButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogCustom.dismiss();
                        }
                    });
                    if(!FindMyTeacherConstants.isEmailValid(mEmail)){
                        dialogCustom.setDialogMessage(R.string.error_message_dialog_wrong_email);
                    }
                    else if(!mNewPssw.equals(mRepNewPssw)){
                        dialogCustom.setDialogMessage(R.string.error_message_second_password_is_not_the_same);
                    }
                }
            }
        });


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

    public void changePassword() {
        Log.e(LOG_TAG , "INSIDE OBTAIN");
        StringRequest stringRequest = new FT_LoginStringRequest(this, Request.Method.POST, urlChangePssw,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ChangePasswordActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();

                mEmail = mEtEmail.getText().toString();
                mOldPssw = mEtOldPsw.getText().toString();
                mNewPssw = mEtNewPsw.getText().toString();
                mRepNewPssw = mEtRepNewPsw.getText().toString();


                // Hash Encryption
                KeySpec spec = new PBEKeySpec(mOldPssw.toCharArray(), "FINDMYTEACHER".getBytes(), 8192, 36*8);//, "FINDMYTEACHER", 65536, 128);
                KeySpec spec2 = new PBEKeySpec(mRepNewPssw.toCharArray(), "FINDMYTEACHER".getBytes(), 8192, 36*8);//, "FINDMYTEACHER", 65536, 128);
                try {
                    SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                    byte[] OldpwdHash = f.generateSecret(spec).getEncoded();
                    byte[] NewpwdHash = f.generateSecret(spec2).getEncoded();
                    String base64HashOldPsw = Base64.encodeToString(OldpwdHash, Base64.DEFAULT);
                    String base64HashNewPsw = Base64.encodeToString(NewpwdHash, Base64.DEFAULT);
                    //String pwdHash_utf8 = new String(pwdHash, StandardCharsets.UTF_8);
                    //String pwdHash_ISO_8859_1 = new String(pwdHash, StandardCharsets.ISO_8859_1);
                    Log.e(LOG_TAG, "HashLog: old password" + base64HashOldPsw);
                    Log.e(LOG_TAG, "HashLog: new password" + base64HashNewPsw);
                    params.put("email",mEmail);
                    params.put("pwd", base64HashOldPsw);
                    params.put("newPwd", base64HashNewPsw);
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
}
