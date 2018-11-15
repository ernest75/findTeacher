package com.b2ngames.findmyteacherapp.data;

/**
 * Created by xavi on 01/12/2016.
 */

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.Locale;


/**
 * Created by xavi on 15/11/2016.
 */

public class FindTeacherProvider extends ContentProvider
{
    private static final String LOG_TAG = FindTeacherProvider.class.getSimpleName();

    private static final Uri uriServer = Uri.parse("https://www.findmyteacherapp.com");
    private static final String directoryName = "app";
    private static final String phpFileName = "check_serverdevicedb_sync.php";
    private static final Uri URI_UPLOAD_URL = uriServer.buildUpon().appendPath(directoryName).appendPath(phpFileName).build();
    private static final String UPLOAD_URL = URI_UPLOAD_URL.toString();

    private static final SQLiteQueryBuilder sCategoryListByLangaugeQueryBuilder;
    private static final SQLiteQueryBuilder sSubjectWithSubjectTranslQueryBuilder;
    private static final SQLiteQueryBuilder sSubjectInfoInfoWithSubjectTranslQueryBuilder;
    private static final SQLiteQueryBuilder sMySubjectsWithSubjectTranslQueryBuilder;



    static
    {
        sCategoryListByLangaugeQueryBuilder = new SQLiteQueryBuilder();
        sSubjectWithSubjectTranslQueryBuilder = new SQLiteQueryBuilder();
        sSubjectInfoInfoWithSubjectTranslQueryBuilder = new SQLiteQueryBuilder();
        sMySubjectsWithSubjectTranslQueryBuilder = new SQLiteQueryBuilder();


        //Inner Join: Tables to ask for data
        //Weather INNER JOIN location ON weather.location_id = location._id
        sCategoryListByLangaugeQueryBuilder.setTables(
                FindTeacherContract.CategoryTransl.TABLE_NAME + " INNER JOIN " +
                        FindTeacherContract.Language.TABLE_NAME +
                        " ON " + FindTeacherContract.CategoryTransl.TABLE_NAME +
                        "." + FindTeacherContract.CategoryTransl.COLUMN_ID_LANG +
                        " = " + FindTeacherContract.Language.TABLE_NAME +
                        "." + FindTeacherContract.Language._ID

        );

        sSubjectWithSubjectTranslQueryBuilder.setTables(
                FindTeacherContract.Subject.TABLE_NAME + " INNER JOIN " +
                     FindTeacherContract.SubjectTransl.TABLE_NAME + " ON " +
                     FindTeacherContract.SubjectTransl.TABLE_NAME + "." +
                     FindTeacherContract.SubjectTransl.COLUMN_ID_SUBJECT + " = " +
                     FindTeacherContract.Subject.TABLE_NAME + "." +
                     FindTeacherContract.Subject._ID
        );

        sSubjectInfoInfoWithSubjectTranslQueryBuilder.setTables(
                FindTeacherContract.SubjectInfo.TABLE_NAME + " INNER JOIN " +
                     FindTeacherContract.SubjectTransl.TABLE_NAME + " ON " +
                     FindTeacherContract.SubjectInfo.TABLE_NAME + "." +
                     FindTeacherContract.SubjectInfo.COLUMN_ID_SUBJECT + " = " +
                     FindTeacherContract.SubjectTransl.TABLE_NAME + "." +
                     FindTeacherContract.SubjectTransl.COLUMN_ID_SUBJECT
        );

        sMySubjectsWithSubjectTranslQueryBuilder.setTables(
                FindTeacherContract.MySubjects.TABLE_NAME + " INNER JOIN " +
                        FindTeacherContract.SubjectTransl.TABLE_NAME + " ON " +
                        FindTeacherContract.SubjectTransl.TABLE_NAME + "." +
                        FindTeacherContract.SubjectTransl.COLUMN_ID_SUBJECT + " = " +
                        FindTeacherContract.MySubjects.TABLE_NAME + "." +
                        FindTeacherContract.MySubjects.COLUMN_ID_SUBJECT
        );


    }

    private FindTeacherDbHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int TEACHER_SEARCH = 100;
    static final int TEACHER_UPDATE= 101;
    static final int CATEGORY_CONTENT = 200;
    static final int SUBJECT_CONTENT = 201;
    static final int SUBJECT_INFO = 300;
    static final int MY_USER_INFO = 400;
    static final int MY_SUBJECTS = 401;
    static final int MY_AVAILABILITY = 402;
    static final int TEACHER_AVAILABILITY = 403;
    static final int MY_MESSAGES = 500;

    static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = FindTeacherContract.CONTENT_AUTHORITY;

        // TEACHER SEARCH
        matcher.addURI(authority, FindTeacherContract.PATH_TEACHER_SEARCH, TEACHER_SEARCH);
        matcher.addURI(authority, FindTeacherContract.PATH_TEACHER_UPDATE, TEACHER_UPDATE);
        matcher.addURI(authority, FindTeacherContract.PATH_CATEGORY_TRANSL,CATEGORY_CONTENT);
        matcher.addURI(authority, FindTeacherContract.PATH_SUBJECT_TRANSL,SUBJECT_CONTENT);
        matcher.addURI(authority, FindTeacherContract.PATH_SUBJECT_INFO, SUBJECT_INFO);
        matcher.addURI(authority, FindTeacherContract.PATH_MY_USER_INFO, MY_USER_INFO);
        matcher.addURI(authority, FindTeacherContract.PATH_MY_SUBJECTS, MY_SUBJECTS);
        matcher.addURI(authority, FindTeacherContract.PATH_MY_AVAILABILITY, MY_AVAILABILITY);
        matcher.addURI(authority, FindTeacherContract.PATH_TEACHER_AVAILABILITY, TEACHER_AVAILABILITY);
        matcher.addURI(authority, FindTeacherContract.PATH_MY_MESSAGES, MY_MESSAGES);

        return matcher;
    }

    // Open the dbHelper
    @Override
    public boolean onCreate()
    {
        mOpenHelper = new FindTeacherDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri)
    {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case TEACHER_SEARCH:
                return FindTeacherContract.Users.CONTENT_TYPE;
            case TEACHER_UPDATE:
                return FindTeacherContract.Users.CONTENT_TYPE;
            case CATEGORY_CONTENT:
                return FindTeacherContract.CategoryTransl.CONTENT_TYPE;
            case SUBJECT_CONTENT:
                return FindTeacherContract.SubjectTransl.CONTENT_TYPE;
            case SUBJECT_INFO:
                return FindTeacherContract.SubjectInfo.CONTENT_TYPE;
            case MY_USER_INFO:
                return FindTeacherContract.MyUserInfo.CONTENT_TYPE;
            case MY_AVAILABILITY:
                return FindTeacherContract.MyAvailability.CONTENT_TYPE;
            case TEACHER_AVAILABILITY:
                return FindTeacherContract.TeacherAvailability.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            // "weather/*/#"
            case TEACHER_SEARCH: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FindTeacherContract.Users.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CATEGORY_CONTENT: {
                // Si El te, demanar les categories
                // Si no el te, posar per defecte "angles"
                String codeName = Locale.getDefault().toString();
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
                    if (codeName.compareTo(codeNameDb) == 0) {
                        deviceLangaugeIsOnDb = true;
                        break;
                    }
                }
                if (!deviceLangaugeIsOnDb) {
                    codeName = "en_GB";
                }
                retCursor = sCategoryListByLangaugeQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                Log.e(LOG_TAG + " retCursor",Integer.toString(retCursor.getCount()));
                break;
            }

            case SUBJECT_CONTENT:{
                retCursor = sSubjectWithSubjectTranslQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            }

            case SUBJECT_INFO:{
               retCursor=  sSubjectInfoInfoWithSubjectTranslQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            }

            case MY_USER_INFO: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FindTeacherContract.MyUserInfo.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

                case MY_SUBJECTS: {
                    retCursor = sMySubjectsWithSubjectTranslQueryBuilder.query(
                            mOpenHelper.getReadableDatabase(),
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );

                    break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id = 0;
        switch (sUriMatcher.match(uri)){
            case MY_USER_INFO:{
                id =  db.insert(
                        FindTeacherContract.MyUserInfo.TABLE_NAME,
                        null,
                        values);
                break;
            }

            case MY_SUBJECTS:{
              id =  db.insert(
                        FindTeacherContract.MySubjects.TABLE_NAME,
                        null,
                        values);
                break;
            }

            case MY_MESSAGES:{
                id =  db.insert(
                        FindTeacherContract.MyMessages.TABLE_NAME,
                        null,
                        values);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(FindTeacherContract.MySubjects.TABLE_NAME + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MY_USER_INFO: {
                db.delete(FindTeacherContract.MyUserInfo.TABLE_NAME, null, null);
                return 1;
            }

            case MY_SUBJECTS: {
                db.delete(FindTeacherContract.MySubjects.TABLE_NAME, selection, selectionArgs);
                return 1;
            }

            case MY_AVAILABILITY: {
                db.delete(FindTeacherContract.MyAvailability.TABLE_NAME, selection, selectionArgs);
                return 1;
            }

            case TEACHER_AVAILABILITY: {
                db.delete(FindTeacherContract.MyAvailability.TABLE_NAME, selection, selectionArgs);
                return 1;
            }


        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int count;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/#"
            case TEACHER_SEARCH: {
                count = mOpenHelper.getReadableDatabase().update(
                        FindTeacherContract.Users.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }

            case MY_USER_INFO:{
                count = mOpenHelper.getWritableDatabase().update(
                        FindTeacherContract.MyUserInfo.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }

            case MY_SUBJECTS: {
                count = mOpenHelper.getReadableDatabase().update(
                        FindTeacherContract.MySubjects.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs

                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        Log.e(LOG_TAG, "In bulkInsert");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match)
        {

            case TEACHER_SEARCH: {
                // Erase all Rows
                db.delete(FindTeacherContract.Users.TABLE_NAME, "1", null);
                db.delete(FindTeacherContract.SubjectInfo.TABLE_NAME, null, null);
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FindTeacherContract.Users.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(FindTeacherContract.Users.CONTENT_URI, null);
//                    db.endTransaction();
//                    getContext().getContentResolver().notifyChange(FindTeacherContract.Users.CONTENT_URI, null);
                    Log.e(LOG_TAG, "Notify added " + returnCount + " teachers.");


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case TEACHER_UPDATE: {
                // Fill all Rows with Data
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FindTeacherContract.Users.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(FindTeacherContract.Users.CONTENT_URI, null);
                    Log.e(LOG_TAG, "Notify added " + returnCount + " teachers.");


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case SUBJECT_INFO: {
                Log.e(LOG_TAG,"inside teacher info");

                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FindTeacherContract.SubjectInfo.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(FindTeacherContract.SubjectInfo.CONTENT_URI, null);
                    Log.e(LOG_TAG, " Added " + returnCount + " rows to teacherInfo");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case MY_USER_INFO:{
                db.delete(FindTeacherContract.MyUserInfo.TABLE_NAME, null, null);
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FindTeacherContract.MyUserInfo.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(FindTeacherContract.SubjectInfo.CONTENT_URI, null);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case MY_SUBJECTS:{
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FindTeacherContract.MySubjects.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(FindTeacherContract.MySubjects.CONTENT_URI, null);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case MY_AVAILABILITY:{
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FindTeacherContract.MyAvailability.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(FindTeacherContract.MyAvailability.CONTENT_URI, null);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case TEACHER_AVAILABILITY:{
                db.delete(FindTeacherContract.TeacherAvailability.TABLE_NAME, null, null);
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FindTeacherContract.TeacherAvailability.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    getContext().getContentResolver().notifyChange(FindTeacherContract.TeacherAvailability.CONTENT_URI, null);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

     }

        return returnCount;
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
