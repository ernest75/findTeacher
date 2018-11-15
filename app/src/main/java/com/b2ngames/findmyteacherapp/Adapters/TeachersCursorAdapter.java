package com.b2ngames.findmyteacherapp.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.b2ngames.findmyteacherapp.R;
import com.b2ngames.findmyteacherapp.data.FindTeacherContract;
import com.b2ngames.findmyteacherapp.volleyhelper.VolleySingleton;

/**
 * Created by xavi on 22/12/2016.
 */

public class TeachersCursorAdapter extends CursorAdapter {



    private LayoutInflater mCursorInflater;
    public TeachersCursorAdapter(Context context, Cursor cursor, int flags){
       super(context,cursor,flags);
        mCursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mCursorInflater.inflate(R.layout.teacher_row, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTeacherName = (TextView) view.findViewById(R.id.tvTeacherName);
        TextView tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        tvTeacherName.setText(cursor.getString(cursor.getColumnIndex(FindTeacherContract.Users.COLUMN_TEACHER_NAME)));
        float distance = cursor.getFloat(cursor.getColumnIndex(FindTeacherContract.Users.COLUMN_DISTANCE));
        tvDistance.setText(Float.toString(distance).format("%.2f",distance) + " Km");

        String imgUrl = cursor.getString(cursor.getColumnIndex(FindTeacherContract.Users.COLUMN_URL_IMAGE));
        if((imgUrl != null) && (imgUrl.length() != 0)) {
            NetworkImageView thumbnail = (NetworkImageView) view.findViewById(R.id.idThumbnail);
            thumbnail.setImageUrl(imgUrl, VolleySingleton.getInstance(context).getImageLoader());

        }


    }


}
