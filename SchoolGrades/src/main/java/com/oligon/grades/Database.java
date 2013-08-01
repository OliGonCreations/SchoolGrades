package com.oligon.grades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SchoolGrades.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_GRADES = "GRADES";
    private static final String KEY_ID = "ID";
    private static final String KEY_SUBJECT = "SUBJECT";
    private static final String KEY_COLOR = "COLOR";
    private static final String KEY_COUNT = "COUNT";
    private static final String KEY_S = "S";
    private static final String KEY_M = "M";
    private static final String KEY_GRADE1_S = "grade1s";
    private static final String KEY_GRADE2_S = "grade2s";
    private static final String KEY_GRADE3_S = "grade3s";
    private static final String KEY_GRADE4_S = "grade4s";
    private static final String KEY_GRADE1_M = "grade1m";
    private static final String KEY_GRADE2_M = "grade2m";
    private static final String KEY_GRADE3_M = "grade3m";
    private static final String KEY_GRADE4_M = "grade4m";
    private static final String KEY_EXTRA = "EXTRAS";

    private Context context;


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_GRADES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SUBJECT + " TEXT UNIQUE," + KEY_COLOR + " INTEGER," + KEY_COUNT + " INTEGER," + KEY_S + " INTEGER," + KEY_M + " INTEGER," + KEY_GRADE1_S + " INTEGER," + KEY_GRADE2_S + " INTEGER," + KEY_GRADE3_S + " INTEGER," + KEY_GRADE4_S + " INTEGER," + KEY_GRADE1_M + " INTEGER," + KEY_GRADE2_M + " INTEGER," + KEY_GRADE3_M + " INTEGER," + KEY_GRADE4_M + " INTEGER," + KEY_EXTRA + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRADES);
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
        db.close();
    }

    public int[] getGradesS(String subject) {
        int[] grades = new int[4];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?",
                new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < grades.length; i++)
                grades[i] = cursor.getInt(i + 6);
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
                grades[i] = cursor.getInt(i + 10);
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

    public BigDecimal getGradeSAverage(String subject) {
        float f = 0f, fA = 0f;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?",
                new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < 4; i++) {
                if (cursor.getInt(i + 6) != -1) {
                    f += cursor.getInt(i + 6);
                    fA++;
                }
            }
            if (fA > 0)
                f = f / fA;
            else f = -1f;
        }
        db.close();
        BigDecimal bd = new BigDecimal(Float.toString(f));
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public BigDecimal getGradeAverage(String subject) {
        float s = 0f, m = 0f, average = -1f;
        int sA = 0, mA = 0;
        int allocationS, allocationM;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, KEY_SUBJECT + "=?", new String[]{subject}, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < 4; i++) {
                if (cursor.getInt(i + 6) != -1) {
                    s += cursor.getInt(i + 6);
                    sA++;
                }
                if (cursor.getInt(i + 10) != -1) {
                    m += cursor.getInt(i + 10);
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
        return bd;
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
        String[] subjects = new String[0];
        return list.toArray(subjects);
    }

    public int[] getAllSubjectColors() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GRADES + " ORDER BY " + KEY_COUNT + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getInt(2));
            } while (cursor.moveToNext());
        }
        db.close();
        int[] colors = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            colors[i] = list.get(i);
        return colors;
    }

    public String[] getAllCurrentAverages() {
        ArrayList<String> list = new ArrayList<String>();
        float s, m;
        int sA, mA;
        int allocationS, allocationM;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GRADES + " ORDER BY " + KEY_COUNT + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                s = m = sA = mA = allocationS = allocationM = 0;

                for (int i = 0; i < 4; i++) {
                    if (cursor.getInt(i + 6) != -1) {
                        s += cursor.getInt(i + 6);
                        sA++;
                    }
                    if (cursor.getInt(i + 10) != -1) {
                        m += cursor.getInt(i + 10);
                        mA++;
                    }
                }
                allocationS = cursor.getInt(4);
                allocationM = cursor.getInt(5);
                if (allocationM == 0 && allocationS == 0) {
                    allocationM = 50;
                    allocationS = 50;
                }
                if (sA > 0 && mA > 0) {
                    s = s / sA;
                    m = m / mA;
                    list.add(new BigDecimal(Float.toString((s * allocationS + m * allocationM) / (allocationS + allocationM))).setScale(1, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toString());
                } else list.add("-");
            } while (cursor.moveToNext());
        }
        db.close();
        String[] subjects = new String[0];
        return list.toArray(subjects);
    }

}
