package com.oligon.grades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SchoolGrades.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_GRADES = "GRADES";
    private static final String TABLE_EXAMS = "EXAMS";
    public static final String KEY_ID = "_id";
    public static final String KEY_SUBJECT = "SUBJECT";
    public static final String KEY_COLOR = "COLOR";
    public static final String KEY_COUNT = "COUNT";
    public static final String KEY_S = "S";
    public static final String KEY_M = "M";
    public static final String KEY_GRADE1_S = "grade1s";
    public static final String KEY_GRADE2_S = "grade2s";
    public static final String KEY_GRADE3_S = "grade3s";
    public static final String KEY_GRADE4_S = "grade4s";
    public static final String KEY_GRADE1_M = "grade1m";
    public static final String KEY_GRADE2_M = "grade2m";
    public static final String KEY_GRADE3_M = "grade3m";
    public static final String KEY_GRADE4_M = "grade4m";
    public static final String KEY_CURRENT_AVERAGE = "CURRENT_AVERAGE";
    public static final String KEY_EXTRA = "EXTRAS";
    public static final String KEY_DATE = "DATE";

    private Context context;

    String[] dummyString = new String[0];
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_GRADES = "CREATE TABLE " + TABLE_GRADES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SUBJECT + " TEXT UNIQUE," + KEY_COLOR + " INTEGER," + KEY_COUNT + " INTEGER," + KEY_S + " INTEGER," + KEY_M + " INTEGER," + KEY_CURRENT_AVERAGE + " TEXT," + KEY_GRADE1_S + " INTEGER," + KEY_GRADE2_S + " INTEGER," + KEY_GRADE3_S + " INTEGER," + KEY_GRADE4_S + " INTEGER," + KEY_GRADE1_M + " INTEGER," + KEY_GRADE2_M + " INTEGER," + KEY_GRADE3_M + " INTEGER," + KEY_GRADE4_M + " INTEGER," + KEY_EXTRA + " TEXT)";
        String CREATE_TABLE_EXAMS = "CREATE TABLE " + TABLE_EXAMS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SUBJECT + " TEXT," + KEY_DATE + " TEXT," + KEY_EXTRA + " TEXT," + KEY_COLOR + " INTEGER)";
        db.execSQL(CREATE_TABLE_GRADES);
        db.execSQL(CREATE_TABLE_EXAMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRADES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAMS);
        onCreate(db);
    }

    public void addSubjects(ArrayList<String> prim, ArrayList<String> sec) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> all = new ArrayList<String>();
        all.addAll(prim);
        all.addAll(sec);
        String[] array = new String[0];
        String query = "DELETE FROM " + TABLE_GRADES + " WHERE " + KEY_SUBJECT + " NOT IN (SELECT " + KEY_SUBJECT + " FROM " + TABLE_GRADES + " WHERE " + KEY_SUBJECT + " IN ('%s'))";
        db.execSQL(String.format(query, TextUtils.join("', '", all.toArray(array))));
        ContentValues values = new ContentValues();
        for (String value : prim) {
            values.put(KEY_SUBJECT, value);
            values.put(KEY_COLOR, context.getResources().getColor(R.color.main_orange));
            values.put(KEY_COUNT, 4);
            values.put(KEY_S, 2);
            values.put(KEY_M, 1);
            values.put(KEY_CURRENT_AVERAGE, "-");
            values.put(KEY_GRADE1_S, -1);
            values.put(KEY_GRADE2_S, -1);
            values.put(KEY_GRADE3_S, -1);
            values.put(KEY_GRADE4_S, -1);
            values.put(KEY_GRADE1_M, -1);
            values.put(KEY_GRADE2_M, -1);
            values.put(KEY_GRADE3_M, -1);
            values.put(KEY_GRADE4_M, -1);
            db.insert(TABLE_GRADES, null, values);
        }
        for (String value : sec) {
            values.put(KEY_SUBJECT, value);
            values.put(KEY_COLOR, context.getResources().getColor(R.color.main_red));
            values.put(KEY_COUNT, 2);
            db.insert(TABLE_GRADES, null, values);
        }
        db.close();
    }

    public void updateGrade(String subject, int term, int grade, String attr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String[] terms = new String[]{"grade1", "grade2", "grade3", "grade4"};
        values.put(terms[term] + attr, grade);
        db.update(TABLE_GRADES, values, KEY_SUBJECT + "=?", new String[]{subject});
        db.close();
    }

    public void updateAllocation(String subject, int s, int m, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_S, s);
        values.put(KEY_M, m);
        values.put(KEY_COLOR, color);
        db.update(TABLE_GRADES, values, KEY_SUBJECT + "=?", new String[]{subject});
        values.clear();
        values.put(KEY_COLOR, color);
        db.update(TABLE_EXAMS, values, KEY_SUBJECT + "=?", new String[]{subject});
        db.close();
    }

    public int[] getGradesS(String subject) {
        int[] grades = new int[4];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?",
                new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < grades.length; i++)
                grades[i] = cursor.getInt(i + 7);
        }
        db.close();
        return grades;
    }

    public int[] getGradesM(String subject) {
        int[] grades = new int[4];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?",
                new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < grades.length; i++)
                grades[i] = cursor.getInt(i + 11);
        }
        db.close();
        return grades;
    }

    public int[] getAllocation(String subject) {
        int[] allocation = new int[3];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?", new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            allocation[0] = cursor.getInt(4);
            allocation[1] = cursor.getInt(5);
            allocation[2] = cursor.getInt(2);
        }
        if (allocation[0] == 0 && allocation[1] == 0) {
            allocation[0] = 50;
            allocation[1] = 50;
        }
        db.close();
        return allocation;
    }

    /*public boolean isMainSubject(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isMain = false;
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?", new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(cursor.getColumnIndex(KEY_COUNT))==4)
                isMain = true;
        }
        db.close();
        return isMain;
    }*/

    public BigDecimal getGradeAverage(String subject) {
        float s = 0f, m = 0f, average = -1f;
        int sA = 0, mA = 0;
        int allocationS, allocationM;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?", new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < 4; i++) {
                if (cursor.getInt(i + 7) != -1) {
                    s += cursor.getInt(i + 7);
                    sA++;
                }
                if (cursor.getInt(i + 11) != -1) {
                    m += cursor.getInt(i + 11);
                    mA++;
                }
            }
            allocationS = cursor.getInt(4);
            allocationM = cursor.getInt(5);
            db.close();
            if (allocationM == 0 && allocationS == 0) {
                allocationM = 50;
                allocationS = 50;
            }
            if (sA > 0 && mA > 0) {
                s = s / sA;
                m = m / mA;
                average = (s * allocationS + m * allocationM) / (allocationS + allocationM);
            }
        }
        BigDecimal bd = new BigDecimal(Float.toString(average));
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CURRENT_AVERAGE, bd.intValue() != -1 ? bd.toString() : "-");
        db.update(TABLE_GRADES, values, KEY_SUBJECT + "=?", new String[]{subject});
        db.close();
        return bd;
    }

    public BigDecimal getGradeSAverage(String subject) {
        float s = 0f, average = -1f;
        int sA = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?", new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < 4; i++) {
                if (cursor.getInt(i + 7) != -1) {
                    s += cursor.getInt(i + 7);
                    sA++;
                }
            }
            if (sA > 0)
                average = s / sA;
        }
        BigDecimal bd = new BigDecimal(Float.toString(average));
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getGradeMAverage(String subject) {
        float m = 0f, average = -1f;
        int mA = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?", new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < 4; i++) {
                if (cursor.getInt(i + 11) != -1) {
                    m += cursor.getInt(i + 11);
                    mA++;
                }
            }
            if (mA > 0)
                average = m / mA;
        }
        BigDecimal bd = new BigDecimal(Float.toString(average));
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP);
    }

    public Cursor getSubjectCursor(String order) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(TABLE_GRADES, new String[]{KEY_ID, KEY_SUBJECT, KEY_COLOR, KEY_CURRENT_AVERAGE}, null, null, null, null, order) : null;
    }

    public String[] getAllSubjectTitles() {
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GRADES + " ORDER BY " + KEY_COUNT + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        db.close();
        return list.toArray(dummyString);
    }

    public void addExam(String subject, Calendar date, String extra) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?", new String[]{subject}, null, null, null, null);
        int color = context.getResources().getColor(R.color.main_orange);
        if (cursor.moveToFirst())
            color = cursor.getInt(2);
        db.close();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, subject);
        values.put(KEY_DATE, sdf.format(date.getTime()));
        values.put(KEY_EXTRA, extra);
        values.put(KEY_COLOR, color);
        db.insert(TABLE_EXAMS, null, values);
        db.close();
    }

    public Cursor getExamCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(TABLE_EXAMS, new String[]{KEY_ID, KEY_SUBJECT, KEY_DATE, KEY_EXTRA, KEY_COLOR}, null, null, null, null, KEY_DATE) : null;
    }

    public void deleteExam(String subject, Calendar date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXAMS, KEY_SUBJECT + "=? AND " + KEY_DATE + "=?", new String[]{subject, sdf.format(date.getTime())});
        db.close();
    }
}
