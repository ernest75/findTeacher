package com.b2ngames.findmyteacherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.b2ngames.findmyteacherapp.data.FindTeacherContract;


public class SubjectListActivity extends FT_ActivityBase implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = SubjectListActivity.class.getSimpleName();
    private ListView lvSubjectList;
    private static final int ID_SUBJECT_LOADER = 10;
    SimpleCursorAdapter mSubjectAdapter;
    Context mContext;
    int idCat, idLang;
    String mCallingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        lvSubjectList = (ListView)findViewById(R.id.lvSubjectList);
        mSubjectAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{FindTeacherContract.SubjectTransl.COLUMN_NAME},
                new int[]{android.R.id.text1},
                0);

        lvSubjectList.setAdapter(mSubjectAdapter);

        if(this.getCallingActivity()== null){
            mCallingActivity = null;
        }else{
            mCallingActivity = this.getCallingActivity().getClassName().toString();
        }

        lvSubjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(LOG_TAG, Long.toString(id));
                Cursor c = mSubjectAdapter.getCursor();
                if(mCallingActivity!=null) {
                    Intent intent = new Intent();
                    if (c != null) {
                        String subjectName = c.getString(c.getColumnIndex(FindTeacherContract.SubjectTransl.COLUMN_NAME));
                        int idSubjectTransl = c.getInt(c.getColumnIndex("idTransl"));
                        intent.putExtra("idSubject", id);
                        intent.putExtra("idSubjectTransl", idSubjectTransl);
                        intent.putExtra("subject", subjectName);
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Intent intent = new Intent(mContext,MySubjectDetailActivity.class);
                    if (c != null) {
                        String subjectName = c.getString(c.getColumnIndex(FindTeacherContract.SubjectTransl.COLUMN_NAME));
                        int idSubjectTransl = c.getInt(c.getColumnIndex("idTransl"));
                        intent.putExtra("idSubject", id);
                        intent.putExtra("idSubjectTransl", idSubjectTransl);
                        intent.putExtra("subject", subjectName);
                        startActivity(intent);
                    }
                }
            }
        });

        getSupportLoaderManager().initLoader(ID_SUBJECT_LOADER,null,this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getIntent();
        idCat = intent.getIntExtra("idCat",0);
        idLang = intent.getIntExtra("idLang",0);
        return new CursorLoader(this,
                FindTeacherContract.SubjectTransl.CONTENT_URI,
                new String[]{FindTeacherContract.SubjectTransl.TABLE_NAME + "." +FindTeacherContract.SubjectTransl.COLUMN_NAME,
                             FindTeacherContract.SubjectTransl.TABLE_NAME + "." + FindTeacherContract.SubjectTransl._ID + " AS idTransl",
                             FindTeacherContract.Subject.TABLE_NAME + "." + FindTeacherContract.Subject._ID},
                FindTeacherContract.Subject.COLUMN_ID_CAT + " =? " + " AND " +
                FindTeacherContract.SubjectTransl.COLUMN_ID_LANG + " =? ",
                new String[]{Integer.toString(idCat),Integer.toString(idLang)},
                null);



    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mSubjectAdapter.swapCursor(data);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSubjectAdapter.swapCursor(null);
    }
}
