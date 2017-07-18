package com.bignerdranch.android.voice.database.DiaryDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.voice.Diary;

import java.util.Date;
import java.util.UUID;

import static com.bignerdranch.android.voice.database.DiaryDbSchema.DiaryDbSchema.*;

/**
 * Created by 我 on 2017/6/1.
 */
public class DiaryCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public DiaryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Diary getDiary(){
        String uuidString=getString(getColumnIndex(DiaryTable.Cols.UUID));
        String title=getString(getColumnIndex(DiaryTable.Cols.TITLE));
        long date=getLong(getColumnIndex(DiaryTable.Cols.DATE));
        String imageId=getString(getColumnIndex(DiaryTable.Cols.IMAGE_ID));
        String content=getString(getColumnIndex(DiaryTable.Cols.CONTENT));

        Diary diary=new Diary(title,imageId,UUID.fromString(uuidString));
        diary.setDate(new Date(date));
        diary.setContent(content);
        return diary;
    }
}
