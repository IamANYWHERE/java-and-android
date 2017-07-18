package com.bignerdranch.android.voice.database.DiaryDbSchema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.voice.Diary;

import static com.bignerdranch.android.voice.database.DiaryDbSchema.DiaryDbSchema.*;

/**
 * Created by 我 on 2017/6/1.
 */
public class DiaryBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION=1;
    private static final String DATABASE_NAME="diaryBase.db";

    private static final String COMMA_SEP=",";

    public DiaryBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ DiaryTable.NAME+"("+
                "_id integer primary key autoincrement, "+
                DiaryTable.Cols.UUID+COMMA_SEP+
                DiaryTable.Cols.TITLE+COMMA_SEP+
                DiaryTable.Cols.DATE+COMMA_SEP+
                DiaryTable.Cols.IMAGE_ID+COMMA_SEP+
                DiaryTable.Cols.CONTENT+
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
