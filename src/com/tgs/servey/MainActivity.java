package com.tgs.servey;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import android.app.AlertDialog;
import android.app.ProgressDialog;import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tgs.adapter.DatabaseHandler;
import com.tgs.servey.db.PlacesDatabaseHandler;

public class MainActivity extends BaserActinbBar{

	Button btn_startservey,btn_serveyreport,btn_export;


	EditText et_UserName=null;
	EditText et_Password=null;
	TextView txt_noserveys;
	GPSTracker gpsTracker;
	DatabaseHandler db;
	String outFilePath;
	final String prf_name="user_pref";
	private Context _mainContext;
	String toDay_DATE="";
	private boolean isCanExportReport=false;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		_mainContext=this;
		btn_startservey=(Button) findViewById(R.id.btn_startservey);
		btn_serveyreport=(Button)findViewById(R.id.btn_report);
		btn_export=(Button)findViewById(R.id.btn_export);
		txt_noserveys=(TextView)findViewById(R.id.txt_noserveys);
		db=new DatabaseHandler(getApplicationContext());
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Home");


		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		toDay_DATE= sdf.format(new Date());

		TextView dateView=(TextView)findViewById(R.id.date_info);
		dateView.setText("Date:"+toDay_DATE);

		//exportCSV();
		Cursor c=db.RetriveData("Select * from SERVEY_DATA WHERE CREATED_DATE >='"+toDay_DATE+"_00:00:00'");

		if(c!=null){
			txt_noserveys.setText("Number of serveys: "+c.getCount());
			
			if(c.getCount()>0)
			isCanExportReport=true;
		}else{
			txt_noserveys.setText("Number of serveys: 0");
			isCanExportReport=false;
		}
		c.close();

		btn_startservey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(),MyServey.class);
				startActivity(i);
				finish();
			}
		});
		
		

		btn_serveyreport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(),Servey_Report.class);
				startActivity(i);
				finish();
			}
		});


		//Master data adding.
		SharedPreferences preferences=getSharedPreferences(prf_name, MODE_PRIVATE);
		boolean isMasterCreated =preferences.getBoolean("IS_MASTER_DB_CREATED", false);

		if(!isMasterCreated)
		{
			MyInserTask inserTask=new MyInserTask();
			inserTask.execute();
		}
		else{
			Log.d(getClass().getName(), "Master data created!");
		}


		
		if(!isCanExportReport)
		{
			 btn_export.setBackgroundColor(getResources().getColor(R.color.app_bg_color));
		 
			btn_export.setEnabled(false);
		}
		else
		{
			btn_export.setEnabled(true);
		}

		btn_export.setOnClickListener(new View.OnClickListener() {
			SQLiteDatabase sqldb = db.getReadableDatabase(); //My Database class
			Cursor cursor = null;

			@Override
			public void onClick(View v) { //main code begins here
				try {

					//  where player_id='"+TournamentMainFragment.loginPreferences.getString(Util.intent_userId, "")+"'");
					cursor = sqldb.rawQuery("select * from SERVEY_DATA WHERE CREATED_DATE >='"+toDay_DATE+"_00:00:00'", null);
					int rowcount = 0;
					int colcount = 0;
					File sdCardDir = Environment.getExternalStorageDirectory();
					String filename = "MyBackUp.csv";
					// the name of the file to export with
					File saveFile = new File(sdCardDir, filename);
					FileWriter fw = new FileWriter(saveFile);

					BufferedWriter bw = new BufferedWriter(fw);
					rowcount = cursor.getCount();
					colcount = cursor.getColumnCount();
					if (rowcount > 0) {
						cursor.moveToFirst();

						for (int i = 0; i < colcount; i++) {
							if (i != colcount - 1) {

								bw.write(cursor.getColumnName(i) + ",");

							} else {

								bw.write(cursor.getColumnName(i));

							}
						}
						bw.newLine();

						for (int i = 0; i < rowcount; i++) {
							cursor.moveToPosition(i);

							for (int j = 0; j < colcount; j++) {
								if (j != colcount - 1)
									bw.write(cursor.getString(j) + ",");
								else
									bw.write(cursor.getString(j));
							}
							bw.newLine();
						}
						bw.flush();
						//   infotext.setText("Exported Successfully.");

						new CSVToExcelConverter().execute();
					}
					if(!cursor.isClosed())
						cursor.close();
				} catch (Exception ex) {
					if (sqldb.isOpen()) {
						sqldb.close();
						if(cursor!=null )
						{
							if(!cursor.isClosed())
								cursor.close();
						}
						//     infotext.setText(ex.getMessage().toString());
					}

				} finally {

				}

			}
		});

	}



	class MyInserTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog progess=null;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progess=new ProgressDialog(_mainContext);
			progess.setCancelable(false);
			progess.setTitle(getString(R.string.app_name));
			progess.setIcon(R.drawable.ic_launcher);
			progess.setMessage("Loading master data...");
			progess.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			PlacesDatabaseHandler databaseHandler=new PlacesDatabaseHandler(getApplicationContext());
			databaseHandler.insertDefaultData();
			System.out.println("Master data ready!");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			SharedPreferences preferences=getSharedPreferences(prf_name,MODE_PRIVATE);
			SharedPreferences.Editor edit=preferences.edit();

			edit.putBoolean("IS_MASTER_DB_CREATED", true);
			edit.commit();


			progess.dismiss();
		}
	}


	public class CSVToExcelConverter extends AsyncTask<String, Void, Boolean> {


		private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Exporting to excel...");
		this.dialog.show();}

		@Override
		protected Boolean doInBackground(String... params) {
			ArrayList arList=null;
			ArrayList al=null;

			//File dbFile= new File(getDatabasePath("database_name").toString());
			//File dbFile=getDatabasePath(DatabaseHandler.DATABASE_NAME);
			//String yes= dbFile.getAbsolutePath();



			File exportDir = new File(Environment.getExternalStorageDirectory(), "/servey");        
			if (!exportDir.exists()) 
			{
				exportDir.mkdirs();
			}  

			//  tv_date.setText(ft.format(dNow)+"-"+Integer.toString(level)+"%");
			String inFilePath = Environment.getExternalStorageDirectory().toString()+"/MyBackUp.csv";
			outFilePath = Environment.getExternalStorageDirectory().toString()+"/servey/"+toDay_DATE+".xls";
			String thisLine;
			int count=0;

			try {

				FileInputStream fis = new FileInputStream(inFilePath);
				DataInputStream myInput = new DataInputStream(fis);
				int i=0;
				arList = new ArrayList();
				while ((thisLine = myInput.readLine()) != null)
				{
					al = new ArrayList();
					String strar[] = thisLine.split(",");
					for(int j=0;j<strar.length;j++)
					{
						al.add(strar[j]);
					}
					arList.add(al);

					i++;
				}} catch (Exception e) {
					e.printStackTrace();
				}

			try
			{
				HSSFWorkbook hwb = new HSSFWorkbook();
				HSSFSheet sheet = hwb.createSheet("OD Survey "+toDay_DATE);
				for(int k=0;k<arList.size();k++)
				{
					ArrayList ardata = (ArrayList)arList.get(k);
					HSSFRow row = sheet.createRow((short) 0+k);
					for(int p=0;p<ardata.size();p++)
					{
						HSSFCell cell = row.createCell((short) p);
						String data = ardata.get(p).toString();
						if(data.startsWith("=")){
							cell.setCellType(cell.CELL_TYPE_STRING);
							data=data.replaceAll("\"", "");
							data=data.replaceAll("=", "");
							cell.setCellValue(data);
						}else if(data.startsWith("\"")){
							data=data.replaceAll("\"", "");
							cell.setCellType(cell.CELL_TYPE_STRING);
							cell.setCellValue(data);
						}else{
							data=data.replaceAll("\"", "");
							cell.setCellType(cell.CELL_TYPE_NUMERIC);
							cell.setCellValue(data);
						}

					}

				}
				FileOutputStream fileOut = new FileOutputStream(outFilePath);
				hwb.write(fileOut);
				fileOut.close();
				System.out.println("Your excel file has been generated");
			} catch ( Exception ex ) {
				ex.printStackTrace();
			} //main method ends
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success)

		{

			if (this.dialog.isShowing())

			{

				this.dialog.dismiss();

			}

			if (success)

			{

				Toast.makeText(MainActivity.this, "File exported successfully :"+outFilePath, Toast.LENGTH_LONG).show();
				
				 showCutomDialog();

			}

			else

			{

				Toast.makeText(MainActivity.this, "File export failed", Toast.LENGTH_SHORT).show();

			}

		}



	}

	private void showCutomDialog() {
		 
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MainActivity.this);

		// set title
		alertDialogBuilder.setTitle(getString(R.string.app_name));
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);

		// set dialog message
		alertDialogBuilder
		.setMessage("Do you want to send report?")
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {

  
				sendEmail();
				 
				 
			}

			
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		
	}
	
	private void sendEmail() {
		 
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"rajesh.mca008@gmail.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "OD Survey as on :"+toDay_DATE);
		intent.putExtra(Intent.EXTRA_TEXT, "OD Survey Report as on :"+toDay_DATE +"<br/> Sent from Android "+android.os.Build.MODEL);
		File root = Environment.getExternalStorageDirectory();
		File file = new File(outFilePath);
		if (!file.exists() || !file.canRead()) {
		    Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
		    finish();
		    return;
		}
		Uri uri = Uri.parse("file://" + file);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(intent, "Send email..."));
		
	}
}
