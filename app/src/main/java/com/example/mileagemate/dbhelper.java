package com.example.mileagemate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class dbhelper extends SQLiteOpenHelper {
    private static  final String DATABASE_NAME="MileageMate";
    private static  final int DATABASE_VERSION=1;
    private static  final String TABLE_NAME="User_Data";
    private static  final String TASK_ID="User_Data";
    private static  final String TASK_NAME="task_name";
    private static  final String TASK_DATE="task_date";
    private static  final String TASK_DESC="task_desc";

recycleviewadapter adapter;
    public dbhelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
            TASK_ID + " INTEGER PRIMARY KEY," +
            TASK_NAME + " TEXT," +
            TASK_DATE + " TEXT," +
            TASK_DESC + " TEXT)"
    );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
       sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
       onCreate(sqLiteDatabase);
    }
    public void insert(datamodel dm){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("TASK_NAME",dm.getTask_name());
        values.put("TASK_DATE",dm.getTask_date());
        values.put("TASK_DESC",dm.getTask_desc());
        db.insert(TABLE_NAME,null,values);
    }

    public ArrayList<datamodel> selectAlldata(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        ArrayList<datamodel> dm=new ArrayList<>();
        while(cursor.moveToNext()){
            datamodel data =new datamodel();
            data.setTask_name(cursor.getString(1));
            data.setTask_date(cursor.getString(2));
            data.setTask_desc(cursor.getString(3));
            dm.add(data);
        }
        return dm;

    }

    public boolean updatedata(datamodel data,int index){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(TASK_NAME,data.getTask_name());
        cv.put(TASK_DATE,data.getTask_date());
        cv.put(TASK_DESC,data.getTask_desc());
       int success=db.update(TABLE_NAME,cv,TASK_ID +"="+index,null);
        if(success==1) {
            return true;
        }
        else {
            return false;
        }
    }

    public void deleteall(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

    }

    public void deleterow(int id){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_NAME,TASK_ID+ " = ? ",new String[]{String.valueOf(id)});
    }

}
