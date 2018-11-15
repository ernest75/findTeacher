package com.b2ngames.findmyteacherapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.b2ngames.findmyteacherapp.data.FindTeacherContract.Users;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract.SyncServerDeviceDB;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract.*;

/**
 * Created by xavi on 01/12/2016.
 */

public class FindTeacherDbHelper extends SQLiteOpenHelper
{
    private static final String LOG_TAG = FindTeacherDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 55;
    SharedPreferences mSharedPreferences;
    Context mContext;


    static final String DATABASE_NAME = "findmyteacher.db";

    UpgradeDatabaseListener _upgradeDbListener;

    public FindTeacherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_FINDTEACHER_TABLE = "CREATE TABLE " + Users.TABLE_NAME + " (" +
                Users._ID + " INTEGER PRIMARY KEY, " +
                Users.COLUMN_TEACHER_NAME + " TEXT UNIQUE NOT NULL, " +
                Users.COLUMN_URL_IMAGE + " TEXT NOT NULL, " +
                Users.COLUMN_DISTANCE + " REAL, " +
                Users.COLUMN_REMOTE_ID + " INTEGER UNIQUE NOT NULL, " +
                Users.COLUMN_PRICE_HOUR + " INTEGER, " +
                Users.COLUMN_MARK +  " REAL NOT NULL, " +
                Users.COLUMN_LAT + " REAL, " +
                Users.COLUMN_LNG + " REAL)";

        final String SQL_CREATE_SYNCSERVERDEVICEDB_TABLE = "CREATE TABLE " + SyncServerDeviceDB.TABLE_NAME + " (" +
                SyncServerDeviceDB._ID + " INTEGER PRIMARY KEY, " +
                SyncServerDeviceDB.COLUMN_VERSION + " INTEGER NOT NULL DEFAULT 1)";

        final String SQL_INSERT_VERSION = "INSERT INTO " + SyncServerDeviceDB.TABLE_NAME + " (" +
                SyncServerDeviceDB._ID + ", " +
                 SyncServerDeviceDB.COLUMN_VERSION + ") VALUES (1,1);";

        final String SQL_CREATE_SUBJECT_INFO_TABLE = "CREATE TABLE " + SubjectInfo.TABLE_NAME + " (" +
                SubjectInfo._ID + " INTEGER PRIMARY KEY, " +
                SubjectInfo.COLUMN_CLASS_DESCRIPTION + " TEXT, " +
                SubjectInfo.COLUMN_EXPERIENCE + " TEXT, " +
                SubjectInfo.COLUMN_ID_SUBJECT + " INTEGER NOT NULL, " +
                SubjectInfo.COLUMN_ID_USER +  " INTEGER NOT NULL, " +
                SubjectInfo.COLUMN_PRICE_HOUR +  " INTEGER, " +
                "FOREIGN KEY (" +  SubjectInfo.COLUMN_ID_SUBJECT + ") REFERENCES " +
                 Subject.TABLE_NAME + "(" + Subject._ID + ") ON DELETE CASCADE ON UPDATE CASCADE, " +
                "FOREIGN KEY (" + SubjectInfo.COLUMN_ID_USER +") REFERENCES " +
                 Users.TABLE_NAME + "(" + Users.COLUMN_REMOTE_ID + ") ON DELETE CASCADE ON UPDATE CASCADE)";

        final String SQL_CREATE_MY_USER_INFO_TABLE = "CREATE TABLE " + MyUserInfo.TABLE_NAME + " (" +
                MyUserInfo._ID + " INTEGER PRIMARY KEY, " +
                MyUserInfo.COLUMN_USER_NAME + " TEXT , " +
                MyUserInfo.COLUMN_ID_REMOTE + " INTEGER NOT NULL, " +
                MyUserInfo.COLUMN_EMAIL + " TEXT, " +
                MyUserInfo.COLUMN_URL_IMAGE + " TEXT, " +
                MyUserInfo.COLUMN_ADRESS + " TEXT, " +
                MyUserInfo.COLUMN_IS_TEACHER + " INTEGER, " +
                MyUserInfo.COLUMN_MOBILITY + " INTEGER, " +
                MyUserInfo.COLUMN_LAT + " REAL, " +
                MyUserInfo.COLUMN_LNG + " REAL, " +
                MyUserInfo.COLUMN_PRICE_HOUR + " INTEGER, " +
                MyUserInfo.COLUMN_MARK + " REAL)";

        final String SQL_CREATE_MY_SUBJECTS_TABLE = "CREATE TABLE " + MySubjects.TABLE_NAME + " (" +
                MySubjects._ID + " INTEGER PRIMARY KEY, " +
                MySubjects.COLUMN_ID_SUBJECT + " INTEGER NOT NULL, " +
                MySubjects.COLUMN_PRICE_HOUR + " INTEGER, " +
                MySubjects.COLUMN_CLASS_DESCRIPTION + " TEXT, " +
                MySubjects.COLUMN_EXPERIENCE + " TEXT, " +
                MySubjects.COLUMN_ID_TEACHER_SUBJECT_REMOTE + " INTEGER NOT NULL)";

        final String SQL_CREATE_MY_AVAILABILITY_TABLE = "CREATE TABLE " + MyAvailability.TABLE_NAME + " (" +
                MyAvailability._ID + " INTEGER PRIMARY KEY, " +
                MyAvailability.COLUMN_ID_DAY + " INTEGER NOT NULL, " +
                MyAvailability.COLUMN_ID_TIME + " INTEGER NOT NULL)";

        final String SQL_CREATE_TEACHER_AVAILABILITY_TABLE = "CREATE TABLE " + TeacherAvailability.TABLE_NAME + " (" +
                TeacherAvailability._ID + " INTEGER PRIMARY KEY, " +
                TeacherAvailability.COLUMN_ID_TEACHER + " INTEGER NOT NULL, " +
                TeacherAvailability.COLUMN_ID_DAY + " INTEGER NOT NULL, " +
                TeacherAvailability.COLUMN_ID_TIME + " INTEGER NOT NULL)";

        final String SQL_CREATE_MY_TEACHERS_TABLE = "CREATE TABLE " + MyTeachers.TABLE_NAME + " (" +
                MyTeachers._ID + " INTEGER PRIMARY KEY, " +
                MyTeachers.COLUMN_ID_TEACHER + " INTEGER NOT NULL, " +
                MyTeachers.COLUMN_ID_SUBJECT + " INTEGER NOT NULL," +
                MyTeachers.COLUMN_TEACHER_NAME + " TEXT NOT NULL, " +
                MyTeachers.COLUMN_URL_IMAGE_TEACHER + " TEXT NOT NULL)";

        final String SQL_CREATE_MY_CONVERSATIONS_TABLE = "CREATE TABLE " + MyConversations.TABLE_NAME + " (" +
                MyConversations._ID + " INTEGER PRIMARY KEY, " +
                MyConversations.COLUMN_CONVERSATION_NAME + " TEXT NOT NULL, " +
                MyConversations.COLUMN_IS_GROUP + " INTEGER NOT NULL, " +
                MyConversations.COLUMN_ID_REMOTE_CONVERSATION + " INTEGER NOT NULL, " +
                MyConversations.COLUMN_TIME_STAMP + " DATETIME NOT NULL)";

        final String SQL_CREATE_MY_USERS_INFO_RELATIONS_TABLE = "CREATE TABLE " + MyUsersInfoRelations.TABLE_NAME + " (" +
                MyUsersInfoRelations._ID + " INTEGER PRIMARY KEY, " +
                MyUsersInfoRelations.COLUMN_ID_SERVER_USER_RELATION + " INTEGER NOT NULL, " +
                MyUsersInfoRelations.COLUMN_NAME + " TEXT NOT NULL, " +
                MyUsersInfoRelations.COLUMN_URL + " TEXT DEFAULT 'http://www.findmyteacherapp.com/app/images/birrete.jpg', " +
                MyUsersInfoRelations.COLUMN_ID_RELATION + " INTEGER NOT NULL, " +
                MyUsersInfoRelations.COLUMN_ID_CONVERSATION + " INTEGER NOT NULL, " +
                MyUsersInfoRelations.COLUMN_TIME_STAMP + " DATETIME NOT NULL)";

        final String SQL_CREATE_MY_STUDENTS_TABLE = "CREATE TABLE " + MyStudents.TABLE_NAME + " (" +
                MyStudents._ID + " INTEGER PRIMARY KEY, " +
                MyStudents.COLUMN_ID_STUDENT + " INTEGER NOT NULL, " +
                MyStudents.COLUMN_STUDENT_NAME + " TEXT NOT NULL, " +
                MyStudents.COLUMN_URL_IMAGE_STUDENT + " TEXT, " +
                MyStudents.COLUMN_ID_SUBJECT + " INTEGER NOT NULL)";

        final String SQL_CREATE_MY_CHATS_TABLE = "CREATE TABLE " + MyChats.TABLE_NAME + " (" +
                MyChats._ID + " INTEGER PRIMARY KEY, " +
                MyChats.COLUMN_ID_CHAT_USER + " INTEGER NOT NULL, " +
                MyChats.COLUMN_CHAT_NAME + " TEXT NOT NULL)";

        final String SQL_CREATE_MY_MESSAGES_TABLE= "CREATE TABLE " + MyMessages.TABLE_NAME + " (" +
                MyMessages._ID + " INTEGER PRIMARY KEY, " +
                MyMessages.COLUMN_ID_RECIVER + " INTEGER NOT NULL, " +
                MyMessages.COLUMN_ID_CONVERSATION + " INTEGER , " +
                MyMessages.COLUMN_MESSAGE + " TEXT NOT NULL, " +
                MyMessages.COLUMN_STATE + " INTEGER NOT NULL, " +
                MyMessages.COLUMN_TIME_STAMP + " DATETIME , " +
                MyMessages.COLUMN_ID_REMOTE_MESSAGE + " INTEGER , " +
                "FOREIGN KEY (" + MyMessages.COLUMN_ID_CONVERSATION + ") REFERENCES " +
                MyConversations.TABLE_NAME + "(" + MyConversations._ID + ") ON DELETE CASCADE ON UPDATE CASCADE)";




        sqLiteDatabase.execSQL(SQL_CREATE_FINDTEACHER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SYNCSERVERDEVICEDB_TABLE);
        sqLiteDatabase.execSQL(SQL_INSERT_VERSION);
        sqLiteDatabase.execSQL(SQL_CREATE_SUBJECT_INFO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_USER_INFO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_SUBJECTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_AVAILABILITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEACHER_AVAILABILITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_TEACHERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_CONVERSATIONS_TABLE);
       // sqLiteDatabase.execSQL(SQL_CREATE_MY_USER_RELATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_STUDENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_CHATS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_MESSAGES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MY_USERS_INFO_RELATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("mCurrentPosition",0);
        editor.apply();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FindTeacherContract.SyncServerDeviceDB.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SyncServerDeviceDB.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Users.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubjectInfo.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyUserInfo.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MySubjects.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyAvailability.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TeacherAvailability.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyTeachers.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyConversations.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyUsersRelation.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyStudents.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyChats.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyConversations.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyMessages.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyUsersInfoRelations.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void upgradeDatabase(UpgradeDatabaseListener listener, String sqlScript){
        _upgradeDbListener = listener;
        UpgradeDatabaseTask updateDatabaseTask = new UpgradeDatabaseTask();
        updateDatabaseTask.execute(sqlScript);
    }

    private class UpgradeDatabaseTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            Log.e("UpgradeDatabaseTask", params[0]);
            String s[] = params[0].split(";");
           //Log.e("params0",s[0]);
            try {
                for(int i = 0 ; i<s.length  ; i++)
                {
                    getWritableDatabase().execSQL(s[i]);
                    Log.e("params0",s[i]);
                }
            }
            catch (SQLException sqlExc){
                Log.e(LOG_TAG,sqlExc.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _upgradeDbListener.onPostDatabaseUpgrade();
        }
    }

    public interface UpgradeDatabaseListener{
        public void onPostDatabaseUpgrade();
    }

    public int getIdLang(String codeName) {

        int idLang;
        String codeNameDb;
        boolean deviceLangaugeIsOnDb = false;
        Cursor cursorCodeNamesDb = getReadableDatabase().query(FindTeacherContract.Language.TABLE_NAME,
                new String[]{FindTeacherContract.Language.COLUMN_CODE_NAME},
                null,
                null,
                null,
                null,
                null);
        if(cursorCodeNamesDb == null)
            return 3;

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

        Cursor mCLang = getReadableDatabase().query(FindTeacherContract.Language.TABLE_NAME,
                new String[]{FindTeacherContract.Language._ID},
                FindTeacherContract.Language.COLUMN_CODE_NAME + " = ?",
                new String[]{codeName},
                null,
                null,
                null);
        mCLang.moveToFirst();

        if(mCLang!=null) {
            idLang = mCLang.getInt(mCLang.getColumnIndex(FindTeacherContract.Language._ID));
        }
        else{
            idLang = 3;
        }

        return idLang;
    }
}

