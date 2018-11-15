package com.b2ngames.findmyteacherapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_LoginStringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;


public class SendMessageActivity extends AppCompatActivity {

    private static final String LOG_TAG = SendMessageActivity.class.getSimpleName();
    Context mContext;
    String url = "https://www.findmyteacherapp.com/app/send_teacher_message.php";
    int mTeacherId, mMyServerId;
    String mMessage;
    SharedPreferences mSharedPreferencesLogIn;

    Button mBtnSendMessage;
    EditText mEtMessage;
    ProgressBar mPbWaitingCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        mContext = this;
        mSharedPreferencesLogIn = getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE);
        mMyServerId = mSharedPreferencesLogIn.getInt(FindMyTeacherConstants.MY_SERVER_ID,-1);
        mTeacherId = getIntent().getIntExtra("teacherId",-1);
        mEtMessage = (EditText)findViewById(R.id.etMessage);
        mBtnSendMessage = (Button)findViewById(R.id.send_message);
        mPbWaitingCircle = (ProgressBar)findViewById(R.id.pbWaitingCircle);
        mBtnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPbWaitingCircle.setVisibility(View.VISIBLE);
                mEtMessage .setVisibility(View.GONE);
                mBtnSendMessage.setVisibility(View.GONE);
                sendMessage();
            }
        });


    }

    public void sendMessage() {
        mMessage = mEtMessage .getText().toString();
        Log.e(LOG_TAG , "INSIDE OBTAIN");
        //Insert to local first
        ContentValues messageValues = new ContentValues();
        messageValues.put(FindTeacherContract.MyMessages.COLUMN_ID_RECIVER,mTeacherId);
        messageValues.put(FindTeacherContract.MyMessages.COLUMN_MESSAGE,mMessage);
        messageValues.put(FindTeacherContract.MyMessages.COLUMN_STATE,FindMyTeacherConstants.PENDING_MESSAGE_SEND);
        getContentResolver().insert(FindTeacherContract.MyMessages.CONTENT_URI,messageValues);

        StringRequest stringRequest = new FT_LoginStringRequest(this, Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e(LOG_TAG + " onResponse", s);
                if(checkSendedMessage(s)) {
                    Intent intent = new Intent();
                    intent.putExtra("response", s);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                     Toast.makeText(mContext," Error yout message wasn't send", Toast.LENGTH_SHORT).show();
                }

            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(SendMessageActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("idReciver",Integer.toString(mTeacherId));
                params.put("message",mMessage);
                params.put("idSender",Integer.toString(mMyServerId));
                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,"Send message request not valid",Toast.LENGTH_SHORT);
        }

    }

    boolean checkSendedMessage(String jsonString) {
        int sended = 0;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

                sended = jsonObject.getInt("success");


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
        if(sended==1){
            return true;
        }else{
            return false;
        }
    }
}
