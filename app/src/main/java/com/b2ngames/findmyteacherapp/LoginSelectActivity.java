package com.b2ngames.findmyteacherapp;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


public class LoginSelectActivity extends AppCompatActivity {

    private Context mContext;
    private static final String LOG_TAG = LoginSelectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_select);
        mContext = this;

//        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
//        mNavigationView.setNavigationItemSelectedListener(this);
//        super.setSlideMenu();
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_common);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button btnLoginFacebook = (Button)findViewById(R.id.btnLogInFaceBook);
        Button btnLoginGoogle = (Button)findViewById(R.id.btnLogInGooglePlus);
        Button btnLoginFindMyTeacher = (Button)findViewById(R.id.btnLogInFindMyTeacherApp);

        btnLoginFindMyTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,FindMyTeacherLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
