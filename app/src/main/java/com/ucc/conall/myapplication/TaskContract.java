package com.ucc.conall.myapplication;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.ucc.conall.myapplication.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_INFO = "info";
        public static final String COL_TASK_DATE = "date";
    }
}
