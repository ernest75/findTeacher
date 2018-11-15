package com.b2ngames.findmyteacherapp.Utilities;

import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.b2ngames.findmyteacherapp.R;

/**
 * Created by xavi on 16/09/2017.
 */

public class DialogCustom extends Dialog {

    TextView mTvMessage;
    Button mBtnDialog;


    public DialogCustom(@NonNull Context context, int style, int layout){
        super(context, style);
        setContentView(layout);
        mTvMessage = (TextView)findViewById(R.id.tvDialogError);
        mBtnDialog = (Button)findViewById(R.id.btnOkDialogError);
        getWindow().setBackgroundDrawableResource(R.color.colorWhite);
    }

    public void setDialogMessage(int message){

        mTvMessage.setText(message);
    }

    public void setOnDialogButtonClickListener(final View.OnClickListener listener){
        mBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             listener.onClick(v);
            }
        });
    }
}
