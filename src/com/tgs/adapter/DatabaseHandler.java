package com.tgs.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	public static final int DATABASE_VERSION = 1;

	// Database Name
	public static final String DATABASE_NAME = "Servey";

	// questionsData table name
//	Date & Time	Vehicle Type	Vehicle Reg No.	Origin	Destination	Commodity Type	Trip Length	Trip Time	Trip Frequency	Return Trip on same day	Monthly pass	Weight in tons	Willngness to Pay Toll (Y/N)

	public static final String TABLE_servey_Data = "SERVEY_DATA";
	public static final String ID="S_NO";
	public static final String VEH_REG_NO="Vehicle_Reg_No";
	public static final String ORIGIN="Vehicle_ORIGIN";
	public static final String OCCUPANCY="Vehicle_OCCUPANCY";
	public static final String DESTINATION="Vehicle_DESTINATION";
	public static final String COMMODITY="Vehicle_COMMODITY";
	public static final String TRIP_LENGTH="Vehicle_TRIP_LENGTH";
	public static final String TRIP_TIME="Vehicle_TRIP_TIME";
	public static final String TRIP_FREQ="Vehicle_TRIP_FREQ";
	public static final String RETURN_TRIP="Vehicle_RETURN_TRIP";
	public static final String MONTHLY_PASS="Vehicle_MONTHLY_PASS";
	public static final String WEIGHT_IN_TONS="WEIGHT_TONS";
	public static final String PAY_TOLL="PAY_TOLL";
	public static final String LATITUDE_LONGITUDE="LATITUDE_LONGITUDE";
	public static final String LONGITUDE="LONGITUDE";
	public static final String IMAGEPATH="IMAGEPATH";
	public static final String CREATED_DATE="CREATED_DATE";
	


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_OFFLINE_DATA_TABLE = "CREATE TABLE "
				+ TABLE_servey_Data 
				+ " (" 
				+ ID 		+ " INTEGER PRIMARY KEY,"
				+ VEH_REG_NO 	+ " TEXT," 
				+ ORIGIN 	+ " TEXT,"
				+ DESTINATION 	+ " TEXT," 
				+ COMMODITY 	+ " TEXT," 
				+ TRIP_LENGTH	+ " TEXT," 
				+ TRIP_TIME 	+ " TEXT," 
				+ TRIP_FREQ 		+ " TEXT,"
				+ RETURN_TRIP 		+ " TEXT," 
				+ MONTHLY_PASS + " TEXT,"
				+ WEIGHT_IN_TONS + " TEXT,"
				+ IMAGEPATH + " TEXT,"
				+ PAY_TOLL + " TEXT,"
				+ LATITUDE_LONGITUDE + " TEXT,"
				+ OCCUPANCY + " TEXT,"
				+ CREATED_DATE + " TEXT"
				+ ")";

		db.execSQL(CREATE_OFFLINE_DATA_TABLE);
	}

	

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_servey_Data);

		// Create tables again
		onCreate(db);
	}

	
	public long insert(String tableName,ContentValues values) {
		SQLiteDatabase db = this.getWritableDatabase();

		
		long seq=db.insert(tableName, null, values);
		db.close(); 
		return seq;
	}

	
	
	public Cursor RetriveData(String table) 
	{
		try
		{
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor curRetriveData = db.rawQuery(table,null);//"Select * from "+TABLE_Player+"where "+where, null);
			if(curRetriveData!=null)
				curRetriveData.moveToFirst();
			return curRetriveData;
		}
		catch (Exception e) 
		{  
			e.printStackTrace();
			return null;
		}
	}

}
