package com.b2ngames.findmyteacherapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;

import java.util.Locale;


public class MySubjectsActivity extends FT_ActivityBase implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private static String LOG_TAG = MySubjectsActivity.class.getSimpleName();
    private static final int LOADER_MY_SUBJECTS_ID = 110;
    private ListView mLvMySubjetcs;
    private FloatingActionButton fabAddSubject;
    int mIdLang;
    private FindTeacherDbHelper mOpenHelper;
    private SimpleCursorAdapter mLvMySubjetcsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subjects);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mContext = this;
        mOpenHelper = new FindTeacherDbHelper(mContext);

        mLvMySubjetcs = (ListView)findViewById(R.id.lvMySubjectsList);
        mLvMySubjetcsAdapter = new SimpleCursorAdapter(
                mContext,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{FindTeacherContract.SubjectTransl.COLUMN_NAME},
                new int[]{android.R.id.text1},
                0);
        mLvMySubjetcs.setAdapter(mLvMySubjetcsAdapter);
        mLvMySubjetcs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext,MySubjectDetailActivity.class);
                intent.putExtra("idSubject",id);
                startActivity(intent);
            }
        });

        fabAddSubject = (FloatingActionButton) findViewById(R.id.fabAddSubject);
        fabAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CategoryListActivity.class);
                startActivity(intent);
            }
        });

        mIdLang = mOpenHelper.getIdLang(Locale.getDefault().toString());

        getSupportLoaderManager().initLoader(LOADER_MY_SUBJECTS_ID, null, this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent upIntent = new Intent(this, TeacherAreaActivity.class);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is not part of the application's task, so
            // create a new task
            // with a synthesized back stack.
            TaskStackBuilder
                    .create(this)
                    .addNextIntent(new Intent(this, TeacherAreaActivity.class))
                    .addNextIntent(upIntent).startActivities();
            finish();
        } else {
            // This activity is part of the application's task, so simply
            // navigate up to the hierarchical parent activity.
            NavUtils.navigateUpTo(this, upIntent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent upIntent = new Intent(this, TeacherAreaActivity.class);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is not part of the application's task, so
            // create a new task
            // with a synthesized back stack.
            TaskStackBuilder
                    .create(this)
                    .addNextIntent(new Intent(this, TeacherAreaActivity.class))
                    .addNextIntent(upIntent).startActivities();
            finish();
        } else {
            // This activity is part of the application's task, so simply
            // navigate up to the hierarchical parent activity.
            NavUtils.navigateUpTo(this, upIntent);
        }
        return true;
    }
//    @Override
//    public void onBackPressedImplementation() {
//        Intent intent = new Intent(mContext,TeacherAreaActivity.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                mContext,
                FindTeacherContract.MySubjects.CONTENT_URI,
                new String[]{FindTeacherContract.SubjectTransl.COLUMN_NAME,
                FindTeacherContract.SubjectTransl.TABLE_NAME + "." +
                FindTeacherContract.SubjectTransl.COLUMN_ID_SUBJECT + " AS _id"},
                FindTeacherContract.SubjectTransl.COLUMN_ID_LANG + " =? ",
                new String[]{Integer.toString(mIdLang)},
                null
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mLvMySubjetcsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



}
