package com.b2ngames.findmyteacherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.data.FindTeacherDbHelper;

import java.util.Locale;


public class CategoryListActivity extends FT_ActivityBase implements LoaderManager.LoaderCallbacks<Cursor>{

    private static String LOG_TAG = CategoryListActivity.class.getSimpleName();
    private ListView lvCategoryList;
    private static final int ID_CATEGORY_LOADER = 10;
    SimpleCursorAdapter mCategoryAdapter;
    Context mContext;
    public final static int SUBJECT_REQUEST = 11;
    int mLang, mIdSubject, mIdCat, mIdSubjectTransl;
    String mCategory, mSubject, mCodeName ;
    FindTeacherDbHelper mOpenHelper;
    String mCallingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        mContext = this;
        mOpenHelper = new FindTeacherDbHelper(mContext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        checkLangaugeOnDb();

        if(this.getCallingActivity()== null){
          mCallingActivity = null;
        }else{
            mCallingActivity = this.getCallingActivity().getClassName().toString();
        }

        lvCategoryList = (ListView)findViewById(R.id.lvCategoryList);
        mCategoryAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{FindTeacherContract.Category.COLUMN_NAME},
                new int[]{android.R.id.text1},
                0);

        lvCategoryList.setAdapter(mCategoryAdapter);

        getSupportLoaderManager().initLoader(ID_CATEGORY_LOADER,null,this);

        lvCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(mContext, SubjectListActivity.class);
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    mIdCat = cursor.getInt(cursor.getColumnIndex(FindTeacherContract.Subject.COLUMN_ID_CAT));
                    mLang = cursor.getInt(cursor.getColumnIndex("idLang"));
                    mCategory = cursor.getString(cursor.getColumnIndex(FindTeacherContract.Category.COLUMN_NAME));
                    intent.putExtra("idCat", mIdCat);
                    intent.putExtra("idLang", mLang);
                    intent.putExtra("categoryName", mCategory);
                    if(mCallingActivity!=null) {
                        startActivityForResult(intent, SUBJECT_REQUEST);
                    }else{
                        startActivity(intent);
                    }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SUBJECT_REQUEST && resultCode== Activity.RESULT_OK)
        {
            mIdSubject = (int)data.getLongExtra("idSubject",0);
            Log.e(LOG_TAG + " id subject",mIdSubject + "");
            mIdSubjectTransl = data.getIntExtra("idSubjectTransl",0);
            mSubject = data.getStringExtra("subject");
            Intent intent = new Intent();
            intent.putExtra("idSubject",mIdSubject);
            intent.putExtra("idSubjectTransl",mIdSubjectTransl);
            intent.putExtra("subject",mSubject);
            intent.putExtra("idCat",mIdCat);
            intent.putExtra("categoryName",mCategory);
            setResult(Activity.RESULT_OK,intent);
            finish();

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
                FindTeacherContract.CategoryTransl.CONTENT_URI,
                new String[]{FindTeacherContract.Subject.COLUMN_ID_CAT,
                        FindTeacherContract.SubjectTransl.COLUMN_ID_LANG,
                        FindTeacherContract.CategoryTransl.COLUMN_NAME,
                        FindTeacherContract.CategoryTransl.TABLE_NAME + "." + FindTeacherContract.Category._ID},
                FindTeacherContract.Language.TABLE_NAME + "." + FindTeacherContract.Language.COLUMN_CODE_NAME + " = ?",
                new String[]{mCodeName},
                null);



    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCategoryAdapter.swapCursor(data);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCategoryAdapter.swapCursor(null);
    }


    private void checkLangaugeOnDb() {
        mCodeName = Locale.getDefault().toString();
        String codeNameDb;
        boolean deviceLangaugeIsOnDb = false;
        Cursor cursorCodeNamesDb = mOpenHelper.getReadableDatabase().query(FindTeacherContract.Language.TABLE_NAME,
                new String[]{FindTeacherContract.Language.COLUMN_CODE_NAME},
                null,
                null,
                null,
                null,
                null);

        cursorCodeNamesDb.moveToFirst();
        Log.e(LOG_TAG,Integer.toString(cursorCodeNamesDb.getCount()));
        while (cursorCodeNamesDb.moveToNext()) {
            codeNameDb = cursorCodeNamesDb.getString(cursorCodeNamesDb.getColumnIndex(FindTeacherContract.Language.COLUMN_CODE_NAME));
            Log.e(LOG_TAG,codeNameDb);
            if (mCodeName.compareTo(codeNameDb) == 0) {
                deviceLangaugeIsOnDb = true;
                break;
            }
        }
        if (!deviceLangaugeIsOnDb) {
            mCodeName = "en_GB";
        }
    }
}
