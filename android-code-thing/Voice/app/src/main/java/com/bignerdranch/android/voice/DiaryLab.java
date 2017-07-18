package com.bignerdranch.android.voice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bignerdranch.android.voice.database.DiaryDbSchema.DiaryBaseHelper;
import com.bignerdranch.android.voice.database.DiaryDbSchema.DiaryCursorWrapper;
import com.bignerdranch.android.voice.database.DiaryDbSchema.DiaryDbSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.bignerdranch.android.voice.database.DiaryDbSchema.DiaryDbSchema.*;

/**
 * Created by 我 on 2017/6/1.
 */
public class DiaryLab {
    private static DiaryLab sDiaryLab;
    private static final String TAG="DiaryLab";

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DiaryLab get(Context context){
        if (sDiaryLab==null){
            sDiaryLab=new DiaryLab(context);
        }
        return sDiaryLab;
    }

    private DiaryLab(Context context){
        mContext=context.getApplicationContext();
        mDatabase=new DiaryBaseHelper(mContext)
                .getWritableDatabase();
    }
    private DiaryCursorWrapper queryDiaries(String whereClause,String[] whereArgs){
        Cursor cursor=mDatabase.query(
                DiaryTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new DiaryCursorWrapper(cursor);
    }

    public List<Diary> getDiaries(){
        List<Diary> diaries=new ArrayList<>();
        DiaryCursorWrapper cursor=queryDiaries(null,null);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                diaries.add(cursor.getDiary());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return diaries;
    }

    public Diary getDiary(UUID uuid){

        DiaryCursorWrapper cursor=queryDiaries(DiaryTable.Cols.UUID+"=?",new String[]{uuid.toString()});
        try {
            if (cursor.getCount()==0){
                Log.i(TAG,"can not get diary");
                return null;
            }
            cursor.moveToFirst();
            return cursor.getDiary();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        } finally{
            cursor.close();
        }
    }

    public void addDiary(Diary diary){
        ContentValues values=getContentValues(diary);
        mDatabase.insert(DiaryTable.NAME,null,values);
    }

    public void deleteCrime(UUID uuid){
        mDatabase.delete(DiaryTable.NAME,DiaryTable.Cols.UUID+"=?",new String[]{uuid.toString()});
    }

    public void updateDiary(Diary diary){
        String uuidString=diary.getUUID().toString();
        ContentValues values=getContentValues(diary);

        mDatabase.update(DiaryTable.NAME,values,DiaryTable.Cols.UUID+"=?",new String[]{uuidString});
    }

    private static  ContentValues getContentValues(Diary diary){
        ContentValues values=new ContentValues();
        values.put(DiaryTable.Cols.UUID,diary.getUUID().toString());
        values.put(DiaryTable.Cols.TITLE,diary.getTitle());
        values.put(DiaryTable.Cols.DATE,diary.getDate().getTime());
        values.put(DiaryTable.Cols.IMAGE_ID,diary.getImageId());
        values.put(DiaryTable.Cols.CONTENT,diary.getContent());
        return values;
    }
}
