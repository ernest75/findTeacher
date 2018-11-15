package com.b2ngames.findmyteacherapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by xavi on 01/12/2016.
 */

public class FindTeacherContract {

    public static final String CONTENT_AUTHORITY = "com.b2ngames.findmyteacherapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TEACHER_SEARCH = "users";
    public static final String PATH_TEACHER_UPDATE = "usersUpdate";
    public static final String PATH_SYNC_SERVER_DEVICE_DB = "syncServerDeviceDB";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_CATEGORY_TRANSL = "categoryTransl";
    public static final String PATH_LANGUAGE = "language";
    public static final String PATH_SUBJECT = "subject";
    public static final String PATH_SUBJECT_TRANSL = "subjectTransl";
    public static final String PATH_SUBJECT_INFO = "subjectInfo";
    public static final String PATH_MY_USER_INFO = "myUserInfo";
    public static final String PATH_MY_SUBJECTS = "mySubjects";
    public static final String PATH_MY_AVAILABILITY = "myAvailability";
    public static final String PATH_TEACHER_AVAILABILITY = "teacherAvailability";
    public static final String PATH_MY_TEACHERS = "myTeachers";
    public static final String PATH_MY_STUDENTS= "myStudents";
    public static final String PATH_MY_CHATS= "myChats";
    public static final String PATH_MY_CONVERSATIONS= "myConversations";
    public static final String PATH_MY_MESSAGES = "myMessages";
    public static final String PATH_MY_USER_REALTION = "myUserRelation";
    public static final String PATH_REALTIONS = "relations";
    public static final String PATH_MY_USERS_INFO_RELATIONS = "myUsersInfoReltions";


    public static final class Users implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEACHER_SEARCH).build();
        public static final Uri CONTENT_UPDATE = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEACHER_UPDATE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEACHER_SEARCH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEACHER_SEARCH;

        // Table Constants
        public static final String TABLE_NAME = "UserInfo";

        public static final String COLUMN_TEACHER_NAME = "username";
        public static final String COLUMN_URL_IMAGE = "img_path";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_REMOTE_ID = "id_remote";
        public static final String COLUMN_REMOTE_ID_JSON_NAME = "id";
        public static final String COLUMN_PRICE_HOUR = "price_hour";
        public static final String COLUMN_MARK= "mark";
        public static final String COLUMN_LAT= "lat";
        public static final String COLUMN_LNG= "lng";
    }

    public static final class SyncServerDeviceDB implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SYNC_SERVER_DEVICE_DB).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SYNC_SERVER_DEVICE_DB;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SYNC_SERVER_DEVICE_DB;

        // Table Constants
        public static final String TABLE_NAME = "SyncServerDeviceDB";

        public static final String COLUMN_VERSION = "version";
    }

    public static final class Category implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        // Table Constants
        public static final String TABLE_NAME = "Category";

        public static final String COLUMN_NAME= "name";

    }

    public static final class CategoryTransl implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY_TRANSL).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY_TRANSL;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY_TRANSL;

        // Table Constants
        public static final String TABLE_NAME = "CategoryTransl";

        public static final String COLUMN_ID_CAT = "idCat";
        public static final String COLUMN_ID_LANG = "idLang";
        public static final String COLUMN_NAME= "name";

    }

    public static final class Language implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LANGUAGE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LANGUAGE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LANGUAGE;

        // Table Constants
        public static final String TABLE_NAME = "Language";

        public static final String COLUMN_CODE_NAME = "codeName";

    }

    public static final class Subject implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBJECT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT;

        // Table Constants
        public static final String TABLE_NAME = "Subject";

        public static final String COLUMN_ID_CAT = "idCat";
        public static final String COLUMN_NAME= "name";

    }

    public static final class SubjectTransl implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBJECT_TRANSL).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT_TRANSL;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT_TRANSL;

        // Table Constants
        public static final String TABLE_NAME = "SubjectTransl";

        public static final String COLUMN_ID_SUBJECT = "idSubject";
        public static final String COLUMN_ID_LANG = "idLang";
        public static final String COLUMN_NAME= "name";

    }

    public static final class SubjectInfo implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBJECT_INFO).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT_INFO;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT_INFO;

        // Table Constants
        public static final String TABLE_NAME = "SubjectInfo";

        public static final String COLUMN_ID_SUBJECT = "idSubject";
        public static final String COLUMN_ID_USER = "idUser";
        public static final String COLUMN_CLASS_DESCRIPTION = "classDescription";
        public static final String COLUMN_EXPERIENCE = "experience";
        public static final String COLUMN_PRICE_HOUR = "price_hour";
        public static final String COLUMN_MARK = "mark";
        public static final String COLUMN_URL_IMAGE = "img_path";
    }

    public static final class MyUserInfo implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_USER_INFO).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_USER_INFO;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_USER_INFO;

        // Table Constants
        public static final String TABLE_NAME = "MyUserInfo";

        public static final String COLUMN_USER_NAME = "username";
        public static final String COLUMN_ID_REMOTE = "idRemote";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_ADRESS = "ltbs_address";
        public static final String COLUMN_PRICE_HOUR = "price_hour";
        public static final String COLUMN_MARK = "mark";
        public static final String COLUMN_URL_IMAGE = "img_path";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LNG = "lng";
        public static final String COLUMN_IS_TEACHER= "isTeacher";
        public static final String COLUMN_MOBILITY= "mobility";
    }

    public static final class MySubjects implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_SUBJECTS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_SUBJECTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_SUBJECTS;

        // Table Constants
        public static final String TABLE_NAME = "MySubjects";

        public static final String COLUMN_ID_SUBJECT = "idSubject";
        public static final String COLUMN_CLASS_DESCRIPTION = "classDescription";
        public static final String COLUMN_EXPERIENCE = "experience";
        public static final String COLUMN_PRICE_HOUR = "priceHour";
        //id of teacherSubject on server
        public static final String COLUMN_ID_TEACHER_SUBJECT_REMOTE = "idTeacherSubjectRemote";
    }

    public static final class MyAvailability implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_AVAILABILITY).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_AVAILABILITY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_AVAILABILITY;

        // Table Constants
        public static final String TABLE_NAME = "MyAvailabilitiy";

        public static final String COLUMN_ID_DAY = "idDay";
        public static final String COLUMN_ID_TIME= "idTime";
    }

    public static final class TeacherAvailability implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEACHER_AVAILABILITY).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEACHER_AVAILABILITY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEACHER_AVAILABILITY;

        // Table Constants
        public static final String TABLE_NAME = "TeacherAvailabilitiy";

        public static final String COLUMN_ID_DAY = "idDay";
        public static final String COLUMN_ID_TIME= "idTime";
        public static final String COLUMN_ID_TEACHER= "idTeacher";
    }

    public static final class MyTeachers implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_TEACHERS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_TEACHERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_TEACHERS;

        // Table Constants
        public static final String TABLE_NAME = "MyTeachers";

        public static final String COLUMN_ID_TEACHER = "idTeacher";
        public static final String COLUMN_URL_IMAGE_TEACHER ="urlTeacherImage";
        public static final String COLUMN_ID_SUBJECT = "idSubject";
        public static final String COLUMN_TEACHER_NAME = "teacherName";
    }

    public static final class MyStudents implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_STUDENTS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_STUDENTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_STUDENTS;

        // Table Constants
        public static final String TABLE_NAME = "MyStudents";

        public static final String COLUMN_ID_STUDENT= "idStudent";
        public static final String COLUMN_URL_IMAGE_STUDENT ="urlStudentImage";
        public static final String COLUMN_ID_SUBJECT = "idSubject";
        public static final String COLUMN_STUDENT_NAME = "studentName";
    }

    public static final class MyUsersRelation implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_USER_REALTION).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_USER_REALTION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_USER_REALTION;

        // Table Constants
        public static final String TABLE_NAME = "MyUserRelation";

        public static final String COLUMN_ID_USER_RELATION= "userRelation";
        public static final String COLUMN_ID_CONVERSATION="idConversation";
    }

    public static final class Relations implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REALTIONS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_REALTIONS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_REALTIONS;

        // Table Constants
        public static final String TABLE_NAME = "Relations";

        public static final String COLUMN_RELATION_NAME = "relationName";

    }

    public static final class MyChats implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_CHATS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_CHATS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_CHATS;

        // Table Constants
        public static final String TABLE_NAME = "MyChats";

        public static final String COLUMN_CHAT_NAME = "chatName";
        public static final String COLUMN_ID_CHAT_USER = "idChatUser";
    }

    public static final class MyConversations implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_CONVERSATIONS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_CONVERSATIONS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_CONVERSATIONS;

        // Table Constants
        public static final String TABLE_NAME = "MyConversations";

        public static final String COLUMN_CONVERSATION_NAME = "conversationName";
        public static final String COLUMN_ID_REMOTE_CONVERSATION = "idRemoteConversation";
        public static final String COLUMN_IS_GROUP= "isGroup";
        public static final String COLUMN_TIME_STAMP= "timeStamp";

    }

    public static final class MyMessages implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_MESSAGES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_MESSAGES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_MESSAGES;

        // Table Constants
        public static final String TABLE_NAME = "MyMessages";

        public static final String COLUMN_ID_RECIVER = "idReciver";
        public static final String COLUMN_ID_CONVERSATION = "idConversation";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_TIME_STAMP = "timeStamp";
        public static final String COLUMN_ID_REMOTE_MESSAGE = "idRemoteMessage";
    }

    public static final class MyUsersInfoRelations implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_USERS_INFO_RELATIONS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_USER_REALTION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_MY_USER_REALTION;

        // Table Constants
        public static final String TABLE_NAME = "MyUsersInfoRelations";

        public static final String COLUMN_ID_SERVER_USER_RELATION = "userServerRelation";
        public static final String COLUMN_NAME ="name";
        public static final String COLUMN_URL ="url";
        public static final String COLUMN_ID_RELATION ="idRelation";
        public static final String COLUMN_ID_CONVERSATION = "idConversation";
        public static final String COLUMN_TIME_STAMP = "timeStamp";
    }


}
