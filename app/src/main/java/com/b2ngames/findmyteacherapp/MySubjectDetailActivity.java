package com.b2ngames.findmyteacherapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_LoginStringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;


public class MySubjectDetailActivity extends FT_ActivityBase implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = MySubjectDetailActivity.class.getSimpleName();
    private static final int LOADER_MY_SUBJECT_INFO_ID = 10;
    private static final String UPLOAD_URL = "https://www.findmyteacherapp.com/app/my_subject_insert.php";
    Context mContext;
    SeekBar mSbPriceHour;
    TextView mTvPriceHour, mTvSubjectName;
    FindTeacherDbHelper mOpenHelper;
    String mSubjectTitle;
    int mPriceHour, mIdSubject, mIdUserRemote, mIdTeacherSubjectRemote;
    String mClassDescription, mCurriculum;
    Button mBtnSave;
    EditText mEtCurriculum, mEtClassDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subject_detail);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        mContext = this;
        mOpenHelper = new FindTeacherDbHelper(mContext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTvSubjectName = (TextView) findViewById(R.id.tvMySubjectTitle);

        Intent intent = getIntent();

        mIdSubject = (int) intent.getLongExtra("idSubject", 0);
        int idLang = mOpenHelper.getIdLang(Locale.getDefault().toString());
        Log.e(LOG_TAG + " idLang", idLang + "");
        Cursor c = getContentResolver().query(
                FindTeacherContract.SubjectTransl.CONTENT_URI,
                new String[]{FindTeacherContract.SubjectTransl.TABLE_NAME + "." +
                        FindTeacherContract.SubjectTransl.COLUMN_NAME},
                FindTeacherContract.SubjectTransl.COLUMN_ID_SUBJECT + " =? AND " +
                        FindTeacherContract.SubjectTransl.COLUMN_ID_LANG + "=? ",
                new String[]{Integer.toString(mIdSubject), Integer.toString(idLang)},
                null);
        c.moveToFirst();

        mSubjectTitle = c.getString(c.getColumnIndex(FindTeacherContract.SubjectTransl.COLUMN_NAME));
        mTvSubjectName.setText(mSubjectTitle);

        Cursor cIdRemote = getContentResolver().query(
                FindTeacherContract.MyUserInfo.CONTENT_URI,
                new String[]{FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE},
                FindTeacherContract.MyUserInfo.TABLE_NAME + "." +
                        FindTeacherContract.MyUserInfo._ID + "=?",
                new String[]{Integer.toString(1)},
                null);
        cIdRemote.moveToFirst();
        mIdUserRemote = cIdRemote.getInt(cIdRemote.getColumnIndex(FindTeacherContract.MyUserInfo.COLUMN_ID_REMOTE));
        Log.e(LOG_TAG + " ID REMOTE",mIdUserRemote + "");

        Cursor cIdTeacherSubjectRemote = getContentResolver().query(
                FindTeacherContract.MySubjects.CONTENT_URI,
                new String[]{FindTeacherContract.MySubjects.COLUMN_ID_TEACHER_SUBJECT_REMOTE},
                FindTeacherContract.MySubjects.TABLE_NAME + "." +
                        FindTeacherContract.MySubjects.COLUMN_ID_SUBJECT + " =? ",
                new String[]{Integer.toString(mIdSubject)},
                null
        );

        if(cIdTeacherSubjectRemote.getCount()>0) {
            cIdTeacherSubjectRemote.moveToFirst();
            mIdTeacherSubjectRemote = cIdTeacherSubjectRemote.getInt(cIdTeacherSubjectRemote.getColumnIndex(
                    FindTeacherContract.MySubjects.COLUMN_ID_TEACHER_SUBJECT_REMOTE));
        }else{
            mIdTeacherSubjectRemote = 0;
        }

        Log.e(LOG_TAG , mIdTeacherSubjectRemote + "");

        mSbPriceHour = (SeekBar) findViewById(R.id.sbSubjectPrice);
        mTvPriceHour = (TextView) findViewById(R.id.tvSeekBarPriceDisplay);
        mEtClassDescription = (EditText) findViewById(R.id.etClassDescription);
        mEtCurriculum = (EditText) findViewById(R.id.etCurriculum);
        mBtnSave = (Button)findViewById(R.id.btnSave);


        mSbPriceHour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPriceHour = progress;
                mTvPriceHour.setText(Integer.toString(mPriceHour) + " €/hour");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getContentResolver().delete(
                        FindTeacherContract.MySubjects.CONTENT_URI,
                        FindTeacherContract.MySubjects.COLUMN_ID_TEACHER_SUBJECT_REMOTE + "=?",
                        new String[]{Integer.toString(mIdTeacherSubjectRemote)});
                insertMySubject();
            }
        });

        getSupportLoaderManager().initLoader(LOADER_MY_SUBJECT_INFO_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                FindTeacherContract.MySubjects.CONTENT_URI,
                new String[]{FindTeacherContract.MySubjects.TABLE_NAME + "." +
                        FindTeacherContract.MySubjects.COLUMN_ID_SUBJECT,
                        FindTeacherContract.MySubjects.COLUMN_EXPERIENCE,
                        FindTeacherContract.MySubjects.COLUMN_CLASS_DESCRIPTION,
                        FindTeacherContract.MySubjects.COLUMN_PRICE_HOUR},
                FindTeacherContract.MySubjects.TABLE_NAME + "." +
                        FindTeacherContract.MySubjects.COLUMN_ID_SUBJECT + " =? ",
                new String[]{Integer.toString(mIdSubject)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() > 0) {
            data.moveToFirst();
            mPriceHour = data.getInt(data.getColumnIndex(FindTeacherContract.MySubjects.COLUMN_PRICE_HOUR));
            mClassDescription = data.getString(data.getColumnIndex(FindTeacherContract.MySubjects.COLUMN_CLASS_DESCRIPTION));
            mCurriculum = data.getString(data.getColumnIndex(FindTeacherContract.MySubjects.COLUMN_EXPERIENCE));
            mSbPriceHour.setProgress(mPriceHour);
            mEtCurriculum.setText(mCurriculum);
            mEtClassDescription.setText(mClassDescription);
            mTvPriceHour.setText(Integer.toString(mPriceHour) + " € /hour");

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    void parseMySubjectInfoJson(String jsonString, ContentValues values) {

        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                int priceHour = jo.getInt(FindTeacherContract.MySubjects.COLUMN_PRICE_HOUR);
                int idSubject = jo.getInt(FindTeacherContract.MySubjects.COLUMN_ID_SUBJECT);
                String classDescription = jo.getString(FindTeacherContract.MySubjects.COLUMN_CLASS_DESCRIPTION);
                String experience = jo.getString(FindTeacherContract.MySubjects.COLUMN_EXPERIENCE);
                int idTeacherSubjectRemote = jo.getInt("id");
                values.put(FindTeacherContract.MySubjects.COLUMN_PRICE_HOUR, priceHour);
                values.put(FindTeacherContract.MySubjects.COLUMN_ID_SUBJECT, idSubject);
                values.put(FindTeacherContract.MySubjects.COLUMN_CLASS_DESCRIPTION, classDescription);
                values.put(FindTeacherContract.MySubjects.COLUMN_EXPERIENCE, experience);
                values.put(FindTeacherContract.MySubjects.COLUMN_ID_TEACHER_SUBJECT_REMOTE,idTeacherSubjectRemote);


            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }
    }

    public void insertMySubject() {

        StringRequest stringRequest = new FT_LoginStringRequest(this, Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                ContentValues values = new ContentValues();
                parseMySubjectInfoJson(s, values);

                Uri uri = getBaseContext().getContentResolver()
                        .insert(FindTeacherContract.MySubjects.CONTENT_URI,values);


                Log.e(LOG_TAG, s);
                Log.e(LOG_TAG, uri.toString());

                Intent intent = new Intent(mContext,MySubjectsActivity.class);
                startActivity(intent);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Showing toast
                        Toast.makeText(MySubjectDetailActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.e(LOG_TAG, volleyError.getMessage().toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParamsQuery() {

                //Creating parameters
                Map<String, String> params = new Hashtable<>();
                mClassDescription = mEtClassDescription.getText().toString();
                mCurriculum = mEtCurriculum.getText().toString();
                //Adding parameters
                params.put("classDescription", mClassDescription);
                params.put("experience", mCurriculum);
                params.put("idSubject", Integer.toString(mIdSubject));
                params.put("idUser", Integer.toString(mIdUserRemote));
                params.put("priceHour", Integer.toString(mPriceHour));
                params.put("idTeacherSubjRemote", Integer.toString(mIdTeacherSubjectRemote));

                return params;
            }
        }.getStringRequest();

        //Creating a Request Queue
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Teachers request not valid", Toast.LENGTH_SHORT);
        }
    }
}
