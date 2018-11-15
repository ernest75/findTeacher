package com.b2ngames.findmyteacherapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.b2ngames.findmyteacherapp.data.FindTeacherContract;


public class TeacherInfoSubjectActivity extends FT_ActivityBase {
    private Context mContext;
    TextView tvExperience, tvClassDescription;
    String experience, classDescription;
    int idUser;
    long idSubject;
    LinearLayout llTeacherInfoSubject;
    RelativeLayout rlWaitingCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info_subject);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        super.setSlideMenu();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mContext = this;

        Intent intent = getIntent();
        idUser = intent.getIntExtra("idUser",0);
        idSubject = intent.getLongExtra("idSubject",0);

        tvExperience = (TextView)findViewById(R.id.tvExperience);
        tvClassDescription = (TextView)findViewById(R.id.tvClassDescription);

        llTeacherInfoSubject = (LinearLayout)findViewById(R.id.llTeacherInfoSubject);
        rlWaitingCircle = (RelativeLayout)findViewById(R.id.rlWaitingCircle);

        llTeacherInfoSubject.setVisibility(View.INVISIBLE);
        rlWaitingCircle.setVisibility(View.VISIBLE);

        Cursor curExpAndClass = getContentResolver().query(
                FindTeacherContract.SubjectInfo.CONTENT_URI,
                new String[]{FindTeacherContract.SubjectInfo.COLUMN_EXPERIENCE,
                        FindTeacherContract.SubjectInfo.COLUMN_CLASS_DESCRIPTION},
                        FindTeacherContract.SubjectInfo.COLUMN_ID_USER + " =? AND " +
                        FindTeacherContract.SubjectInfo.TABLE_NAME + "."+
                        FindTeacherContract.SubjectInfo.COLUMN_ID_SUBJECT + " =? ",
                new String[]{Integer.toString(idUser),Long.toString(idSubject)},
                null

        );
        curExpAndClass.moveToFirst();
        experience = curExpAndClass.getString(curExpAndClass.getColumnIndex(FindTeacherContract.SubjectInfo.COLUMN_EXPERIENCE));
        classDescription = curExpAndClass.getString(curExpAndClass.getColumnIndex(FindTeacherContract.SubjectInfo.COLUMN_CLASS_DESCRIPTION));

        tvExperience.setText(experience);
        tvClassDescription.setText(classDescription);
        llTeacherInfoSubject.setVisibility(View.VISIBLE);
        rlWaitingCircle.setVisibility(View.INVISIBLE);



    }




}
