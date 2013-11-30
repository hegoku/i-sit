package com.dragonballsoft.isit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite extends SQLiteOpenHelper{
	private static final int VERSION=1;
	public Sqlite(Context context,String name,CursorFactory factory,int version){
		super(context,name,factory,version);
	}

	public Sqlite(Context context,String name){
		this(context,name,VERSION);
	}

	public Sqlite(Context context,String name,int version){
		this(context,name,null,version);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("create table account(teacherID varchar(255),pwd varchar(255))");
		db.execSQL("INSERT INTO account VALUES('##^%&###','^&####^&###%')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
		// TODO Auto-generated method stub
		
	}
}
