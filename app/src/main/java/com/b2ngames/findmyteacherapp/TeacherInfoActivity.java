package com.b2ngames.findmyteacherapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.b2ngames.findmyteacherapp.Utilities.FindMyTeacherConstants;
import com.b2ngames.findmyteacherapp.Utilities.FullLengthListView;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;
import com.b2ngames.findmyteacherapp.volleyhelper.FT_StringRequest;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

/* TODO: Ernest
    (1) : Crear un fitxer teacherInfo.php que donat un id (id de servidor) de teacher, retorni el
          preu hora en un json i pujarlo al servidor.
    (2) : Crear un request a un fitxer php teacherInfo.php fent servir un codi com el que tenim a
          MainActivity:
            parseTeachersJson
            refreshTeachers
          En comptes d'un insert a la DB has de fer un update. Així que hauras de guardarte el id_local
          d'alguna forma per saber a quina row hauras d'actulitzar les dades.
          Els nostres metodes es diran:
            parserTeacherInfoJson
            obtainTeacherInfoById
    (3) : Crear un Loader que obtingui del profesor les seves dades amb el id_local que ha rebut l'activitat i refresqui
          la interficie d'usuari en el metode onLoadFinished del LoaderManager.LoaderCallbacks<Cursor>.
    (4) : Aquest Loader, de moment nomes pregunta a la taula UserInfo així que per ara podem agafar la mateixa URI
          FindTeacherContract.Users.CONTENT_URI per a fer la consulta (Pero amb un projection que inclogui el preu i un selection que inclogui el user id),
          al fer el update, també utilitzaras aquesta uri FindTeacherContract.Users.CONTENT_URI i així notificara a tots els observers.
    (5) : Coses a testejar que no estic segur: Segurament onLoadFinished es cridará 2 cops, el primer cop que vas a la DB i el segon quan fem el update.
          Asegurat amb un log de que es així.
 */


/* MainActivity *******************************************************
    [XLLR]: Methods Order:
        (1) : Activity Life Cycle Methods
        (2) : Menu and Navigation
        (3) : LoaderManager.LoaderCallbacks<Cursor>: Implementations
        (4) : Activity General Methods
 **********************************************************************/
public class TeacherInfoActivity extends FT_ActivityBase implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = TeacherInfoActivity.class.getSimpleName();
    private static final String url = "https://www.findmyteacherapp.com/app/teacher_info.php";
    private static final String urlgetAvailability = "https://www.findmyteacherapp.com/app/get_teacher_availability.php";
    private static final int LOADER_USER_INFO_ID = 10;
    private static final int LOADER_SUBJECT_INFO_ID = 11;
    TextView tvTeacherName;
    TextView tvDistance;
    TextView tvPriceHour;
    NetworkImageView niTeacherImage;
    RatingBar rbTeacherMark;
    FullLengthListView lvTeacherSubjects;
    int mIdLocal;
    int mIdRemote;
    int mTimesOnLoadFinishedIsCalled;
    int mRequestCodeForIntentSendMessage;
    float mDistance;
    String mTeacherName;
    float mMark;
    String mUrlImage;
    int mPriceHour;
    int mIdLang;
    Button mbtTeacherAvailability, mbtLocation, mbtChat;

    Context mContext;

    RelativeLayout mRlTeacherInfo;
    RelativeLayout mRlWaitingCircle;

    private FindTeacherDbHelper mOpenHelper;

    private SimpleCursorAdapter mLvTeachersAdapter;
    LoaderManager.LoaderCallbacks<Cursor> mCursorLoaderCallbacks;

    /***
     * Activity Life Cycle Methods
     ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mCursorLoaderCallbacks = this;

        mSharedPreferencesLogIn = getSharedPreferences(FindMyTeacherConstants.SHARED_PREFERENCES_LOGIN, Context.MODE_PRIVATE);
        mLogin = mSharedPreferencesLogIn.getInt(FindMyTeacherConstants.IS_LOGED,0);

        Intent intent = getIntent();
        mIdLocal = intent.getIntExtra("id_local", -1);
        if (mIdLocal == -1) {
            finish();
        }

        Cursor cursorTeacherIdRemote = getContentResolver().query(
                FindTeacherContract.Users.CONTENT_URI,
                new String[]{FindTeacherContract.Users.COLUMN_REMOTE_ID, FindTeacherContract.Users.COLUMN_LAT,
                            FindTeacherContract.Users.COLUMN_LNG},
                FindTeacherContract.Users._ID + " = ?",
                new String[]{Integer.toString(mIdLocal)},
                null
        );
        cursorTeacherIdRemote.moveToFirst();
        mIdRemote = cursorTeacherIdRemote.getInt(cursorTeacherIdRemote
                .getColumnIndex(FindTeacherContract.Users.COLUMN_REMOTE_ID));

        mContext = this;

        mRlTeacherInfo = (RelativeLayout) findViewById(R.id.activity_teacher_info);
        mRlWaitingCircle = (RelativeLayout) findViewById(R.id.rlWaitingCircle);
        mRlTeacherInfo.setVisibility(View.GONE);
        mRlWaitingCircle.setVisibility(View.VISIBLE);

        lvTeacherSubjects = (FullLengthListView) findViewById(R.id.lvTeacherSubjects);
        lvTeacherSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, TeacherInfoSubjectActivity.class);
                intent.putExtra("idUser", mIdRemote);
                intent.putExtra("idSubject", id);
                startActivity(intent);

            }
        });


        mOpenHelper = new FindTeacherDbHelper(mContext);

        Cursor cursorCheckHasLocalData = mOpenHelper.getReadableDatabase().query(
                FindTeacherContract.SubjectInfo.TABLE_NAME,
                new String[]{FindTeacherContract.SubjectInfo.COLUMN_ID_USER},
                FindTeacherContract.SubjectInfo.COLUMN_ID_USER + " =? ",
                new String[]{Integer.toString(mIdRemote)},
                null,
                null,
                null
        );
        boolean userIsLocal = cursorCheckHasLocalData.getCount() > 0;

        mIdLang = mOpenHelper.getIdLang(Locale.getDefault().toString());
        tvTeacherName = (TextView) findViewById(R.id.tvTeacherNameTeacherInfoActivity);
        niTeacherImage = (NetworkImageView) findViewById(R.id.ivTeacherImage);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvPriceHour = (TextView) findViewById(R.id.tvPriceHour);
        rbTeacherMark = (RatingBar) findViewById(R.id.rbTeacherMark);

        if (!userIsLocal) {
            getUserSubjectInfoFromServer(mIdRemote);
        } else{
            getSupportLoaderManager().initLoader(LOADER_SUBJECT_INFO_ID, null, this);
        }
        Log.e(LOG_TAG + "onCreate act", 1+ "");
        getSupportLoaderManager().initLoader(LOADER_USER_INFO_ID, null, this);

        mbtTeacherAvailability = (Button)findViewById(R.id.btnAvailavility);
        mbtTeacherAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,TeacherAvailabilityActivity.class);
                intent.putExtra("canUpdateAvailability",false);
                intent.putExtra("idTeacher",mIdRemote);
                intent.putExtra(FindMyTeacherConstants.CALLING_ACTIVITY_KEY, FindMyTeacherConstants.TEACHER_INFO_ACTIVITY);
                startActivity(intent);
            }
        });

        getTeacherAvailabilityFromServer(mIdRemote);

        mbtLocation = (Button)findViewById(R.id.btnLocation);
        mbtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectLocation.class);
                intent.putExtra(FindMyTeacherConstants.CALLING_ACTIVITY_KEY, FindMyTeacherConstants.TEACHER_INFO_ACTIVITY);
                intent.putExtra("teacherId",mIdRemote);
                startActivity(intent);

            }
        });

        mbtChat = (Button)findViewById(R.id.btnChat);
        mbtChat.setVisibility(View.GONE);
        View lastShadow = findViewById(R.id.divider6);
        lastShadow.setVisibility(View.GONE);

        mRequestCodeForIntentSendMessage = 200;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG + "OnLoadFinshedIsCalled ",Integer.toString(mTimesOnLoadFinishedIsCalled));
    }

    @Override
    protected void onStop() {
        Log.e(LOG_TAG ," onStop is called");
        getSupportLoaderManager().getLoader(LOADER_USER_INFO_ID).reset();
        getSupportLoaderManager().getLoader(LOADER_SUBJECT_INFO_ID).reset();

        super.onStop();
    }

    /***
     * Menu and Navigation
     ***/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_teacher:

                break;

            case R.id.register_login:
                Intent intent = new Intent(mContext,FindMyTeacherLoginActivity.class);
                startActivity(intent);
                break;

            case R.id.send_message:
                Intent intentSendMessage = new Intent(mContext,SendMessageActivity.class);
                intentSendMessage.putExtra("teacherId",mIdRemote);
                startActivityForResult(intentSendMessage,mRequestCodeForIntentSendMessage);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * LoaderManager.LoaderCallbacks<Cursor>: Implementations
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG + "onCreate loader", 2+ "");
        switch(id) {
            case LOADER_USER_INFO_ID :
                CursorLoader loaderUserInfoId = new CursorLoader(mContext,
                        FindTeacherContract.Users.CONTENT_URI,
                        new String[]{FindTeacherContract.Users.COLUMN_TEACHER_NAME,
                                FindTeacherContract.Users.COLUMN_PRICE_HOUR,
                                FindTeacherContract.Users.COLUMN_URL_IMAGE,
                                FindTeacherContract.Users.COLUMN_MARK,
                                FindTeacherContract.Users.COLUMN_DISTANCE},
                        FindTeacherContract.Users.TABLE_NAME + "." +
                                FindTeacherContract.Users._ID + " =? ",
                        new String[]{Integer.toString(mIdLocal)},
                        null);
                loaderUserInfoId.registerListener(LOADER_USER_INFO_ID,null);
                return loaderUserInfoId;
            case LOADER_SUBJECT_INFO_ID :
               CursorLoader loaderSubjectInfoId = new CursorLoader(mContext,
                        FindTeacherContract.SubjectInfo.CONTENT_URI,
                        new String[]{FindTeacherContract.SubjectInfo.TABLE_NAME + "." +
                                FindTeacherContract.SubjectInfo.COLUMN_ID_SUBJECT,
                                FindTeacherContract.SubjectInfo.TABLE_NAME + "." +
                                FindTeacherContract.SubjectInfo.COLUMN_ID_USER
                                },
                        FindTeacherContract.SubjectInfo.TABLE_NAME + "." +
                                FindTeacherContract.SubjectInfo.COLUMN_ID_USER + " = ? ",
                        new String[]{Integer.toString(mIdRemote)},
                        null);
                loaderSubjectInfoId.registerListener(LOADER_SUBJECT_INFO_ID,null);
                return  loaderSubjectInfoId;

            default:
                return null;
            }


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG + "onLoad Finished", 3+ "");

        switch (loader.getId()){
            case LOADER_USER_INFO_ID :
                data.moveToFirst();
                mDistance = data.getFloat(data.getColumnIndex(FindTeacherContract.Users.COLUMN_DISTANCE));
                mMark = data.getFloat(data.getColumnIndex(FindTeacherContract.Users.COLUMN_MARK));
                mPriceHour = data.getInt(data.getColumnIndex(FindTeacherContract.Users.COLUMN_PRICE_HOUR));
                mTeacherName = data.getString(data.getColumnIndex(FindTeacherContract.Users.COLUMN_TEACHER_NAME));
                mUrlImage = data.getString(data.getColumnIndex(FindTeacherContract.Users.COLUMN_URL_IMAGE));
                rbTeacherMark.setProgress((int)mMark);
                tvTeacherName.setText(mTeacherName);
                niTeacherImage.setImageUrl(mUrlImage, VolleySingleton.getInstance(mContext).getImageLoader());
                tvPriceHour.setText(Integer.toString(mPriceHour) + " €/hour");
                tvDistance.setText(Float.toString(mDistance).format("%.2f",mDistance) + " Km");

                break;

            case LOADER_SUBJECT_INFO_ID:
                Log.e(LOG_TAG + " data loader subject id", data.getCount() + "");
                if(data.getCount() == 0)
                    return;
                data.moveToFirst();
                int idUser = data.getInt(data.getColumnIndex(FindTeacherContract.SubjectInfo.COLUMN_ID_USER));

                Log.e(LOG_TAG + "idlANG",mIdLang + "");

                Cursor cursorSubjectsTranslNames = getContentResolver().query(
                        FindTeacherContract.SubjectInfo.CONTENT_URI,
                        new String[]{FindTeacherContract.SubjectTransl.COLUMN_NAME,
                                FindTeacherContract.SubjectTransl.TABLE_NAME + "." +
                                FindTeacherContract.SubjectTransl.COLUMN_ID_SUBJECT + " AS _id"},
                        FindTeacherContract.SubjectInfo.COLUMN_ID_USER + " = ? AND " +
                                FindTeacherContract.SubjectTransl.COLUMN_ID_LANG + " = ? ",
                        new String[]{Integer.toString(idUser), Integer.toString(mIdLang)},
                        null

                );
                Log.e(LOG_TAG + "CURSOR SUBJETCS", cursorSubjectsTranslNames.getCount() + "");
                mLvTeachersAdapter = new SimpleCursorAdapter(
                        mContext,
                        android.R.layout.simple_list_item_1,
                        cursorSubjectsTranslNames,
                        new String[]{FindTeacherContract.SubjectTransl.COLUMN_NAME},
                        new int[]{android.R.id.text1},
                        0);
                lvTeacherSubjects.setAdapter(mLvTeachersAdapter);
                mRlTeacherInfo.setVisibility(View.VISIBLE);
                mRlWaitingCircle.setVisibility(View.GONE);

                break;
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLvTeachersAdapter.swapCursor(null);
    }


    /***
     * Activity General Methods
     ***/

    void parseSubjectInfoJson(String jsonString, Vector<ContentValues> vecTeacherValues) {

        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                int idUser = jo.getInt(FindTeacherContract.SubjectInfo.COLUMN_ID_USER);
                int priceHour = jo.getInt(FindTeacherContract.SubjectInfo.COLUMN_PRICE_HOUR);
                int idSubject = jo.getInt(FindTeacherContract.SubjectInfo.COLUMN_ID_SUBJECT);
                String classDescription = jo.getString(FindTeacherContract.SubjectInfo.COLUMN_CLASS_DESCRIPTION);
                String experience = jo.getString(FindTeacherContract.SubjectInfo.COLUMN_EXPERIENCE);
                Log.e(LOG_TAG + " Url parsejada", url);
                ContentValues values = new ContentValues();
                values.put(FindTeacherContract.SubjectInfo.COLUMN_ID_USER, idUser);
                values.put(FindTeacherContract.SubjectInfo.COLUMN_PRICE_HOUR, priceHour);
                values.put(FindTeacherContract.SubjectInfo.COLUMN_ID_SUBJECT, idSubject);
                values.put(FindTeacherContract.SubjectInfo.COLUMN_CLASS_DESCRIPTION, classDescription);
                values.put(FindTeacherContract.SubjectInfo.COLUMN_EXPERIENCE, experience);


                vecTeacherValues.add(values);
                Log.e(LOG_TAG + " VALUES COUNT", vecTeacherValues.size()+ "");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }

    }

    public void getUserSubjectInfoFromServer(final int idRemote) {
        Log.e(LOG_TAG , "INSIDE OBTAIN");

        StringRequest stringRequest = new FT_StringRequest(this,Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e(LOG_TAG + " onResponse", 4+ "");
                int inserted = 0;
                Vector<ContentValues> vecTeacherValues = new Vector<>();
                parseSubjectInfoJson(s, vecTeacherValues);
                ContentValues[] cvArray = new ContentValues[vecTeacherValues.size()];
                vecTeacherValues.toArray(cvArray);
                inserted = getBaseContext().getContentResolver()
                        .bulkInsert(FindTeacherContract.SubjectInfo.CONTENT_URI, cvArray);

                Log.e(LOG_TAG + " ONRESPONSE", s);



               Log.e(LOG_TAG , inserted + "");
               getSupportLoaderManager().initLoader(LOADER_SUBJECT_INFO_ID, null, mCursorLoaderCallbacks);
                mRlTeacherInfo.setVisibility(View.VISIBLE);
                mRlWaitingCircle.setVisibility(View.GONE);


            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(TeacherInfoActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParamsQuery () {
                Map<String, String> params = new Hashtable<String, String>();

                params.put("idRemote",Integer.toString(idRemote));
                Log.e(LOG_TAG + " params idRemote",idRemote + "");
                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext,"Teachers request not valid",Toast.LENGTH_SHORT);
        }

    }

    void parseTeacherAvailabilityJson(String jsonString, Vector<ContentValues> vecTeacherValues) {

        try {
            JSONArray teacherArray = new JSONArray(jsonString);
            for (int i = 0; i < teacherArray.length(); i++) {
                JSONObject jo = teacherArray.getJSONObject(i);
                int idTeacher = jo.getInt(FindTeacherContract.TeacherAvailability.COLUMN_ID_TEACHER);
                int idDay = jo.getInt(FindTeacherContract.TeacherAvailability.COLUMN_ID_DAY);
                int idTime = jo.getInt(FindTeacherContract.TeacherAvailability.COLUMN_ID_TIME);
                ContentValues values = new ContentValues();
                values.put(FindTeacherContract.TeacherAvailability.COLUMN_ID_TEACHER, idTeacher);
                values.put(FindTeacherContract.TeacherAvailability.COLUMN_ID_DAY, idDay);
                values.put(FindTeacherContract.TeacherAvailability.COLUMN_ID_TIME, idTime);
                vecTeacherValues.add(values);
                Log.e(LOG_TAG + " VALUES COUNT", vecTeacherValues.size()+ "");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }

    }


    public void getTeacherAvailabilityFromServer(final int idTeacher) {
        Log.e(LOG_TAG, "INSIDE OBTAIN");

        StringRequest stringRequest = new FT_StringRequest(this, Request.Method.POST, urlgetAvailability,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int inserted = 0;
                        Vector<ContentValues> vecTeacherValues = new Vector<>();
                        parseTeacherAvailabilityJson(s, vecTeacherValues);
                        ContentValues[] cvArray = new ContentValues[vecTeacherValues.size()];
                        vecTeacherValues.toArray(cvArray);
                        inserted = mContext.getContentResolver()
                                .bulkInsert(FindTeacherContract.TeacherAvailability.CONTENT_URI, cvArray);

                        Log.e(LOG_TAG + "ONRESPONSE", s);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(TeacherInfoActivity.this,
                                volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParamsQuery() {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("idTeacher", Integer.toString(idTeacher));

                return params;
            }
        }.getStringRequest();
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Teachers request not valid", Toast.LENGTH_SHORT);
        }

    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerOrLogin = menu.findItem(R.id.register_login);
        MenuItem addTeacher = menu.findItem(R.id.add_teacher);
        MenuItem sendMessage = menu.findItem(R.id.send_message);
        if(mLogin==0)
        {
            registerOrLogin.setVisible(true);
            addTeacher.setVisible(false);
            sendMessage.setVisible(false);
        }
        else
        {
            registerOrLogin.setVisible(false);
            addTeacher.setVisible(true);
            sendMessage.setVisible(true);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == mRequestCodeForIntentSendMessage && resultCode==RESULT_OK) {
            Toast.makeText(mContext, "Message Send", Toast.LENGTH_SHORT).show();
            String result = data.getStringExtra("response");
            //Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
