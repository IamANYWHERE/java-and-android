package com.bignerdranch.android.voice.database.DiaryDbSchema;

/**
 * Created by 我 on 2017/6/1.
 */
public class DiaryDbSchema {

    public static final class DiaryTable {
        public static final String NAME = "Diaries";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String IMAGE_ID = "image_id";
            public static final String CONTENT="content";
        }
    }
}
