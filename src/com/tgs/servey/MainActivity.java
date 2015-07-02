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

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tgs.adapter.DatabaseHandler;

public class MainActivity extends ActionBarActivity{

	Button btn_startservey,btn_serveyreport,btn_export;
	
	
	EditText et_UserName=null;
	EditText et_Password=null;
	TextView txt_noserveys;
	GPSTracker gpsTracker;
	DatabaseHandler db;
	 String outFilePath;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		btn_startservey=(Button) findViewById(R.id.btn_startservey);
		btn_serveyreport=(Button)findViewById(R.id.btn_report);
		btn_export=(Button)findViewById(R.id.btn_export);
		txt_noserveys=(TextView)findViewById(R.id.txt_noserveys);
		db=new DatabaseHandler(getApplicationContext());
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Home");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.iconbg));
		//exportCSV();
		Cursor c=db.RetriveData("Select * from SERVEY_DATA");
		
		if(c!=null){
			txt_noserveys.setText("Number of serveys: "+c.getCount());
			
		}else{
			txt_noserveys.setText("Number of serveys: 0");
		}
		
	
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
		
		
		
		
		 
		btn_export.setOnClickListener(new View.OnClickListener() {
            SQLiteDatabase sqldb = db.getReadableDatabase(); //My Database class
            Cursor c = null;
 
            @Override
            public void onClick(View v) { //main code begins here
                try {
                	//  where player_id='"+TournamentMainFragment.loginPreferences.getString(Util.intent_userId, "")+"'");
                    c = sqldb.rawQuery("select * from SERVEY_DATA", null);
                    int rowcount = 0;
                    int colcount = 0;
                    File sdCardDir = Environment.getExternalStorageDirectory();
                    String filename = "MyBackUp.csv";
                                        // the name of the file to export with
                    File saveFile = new File(sdCardDir, filename);
                    FileWriter fw = new FileWriter(saveFile);
 
                    BufferedWriter bw = new BufferedWriter(fw);
                    rowcount = c.getCount();
                    colcount = c.getColumnCount();
                    if (rowcount > 0) {
                        c.moveToFirst();
 
                        for (int i = 0; i < colcount; i++) {
                            if (i != colcount - 1) {
 
                                bw.write(c.getColumnName(i) + ",");
 
                            } else {
 
                                bw.write(c.getColumnName(i));
 
                            }
                        }
                        bw.newLine();
 
                        for (int i = 0; i < rowcount; i++) {
                            c.moveToPosition(i);
 
                            for (int j = 0; j < colcount; j++) {
                                if (j != colcount - 1)
                                    bw.write(c.getString(j) + ",");
                                else
                                    bw.write(c.getString(j));
                            }
                            bw.newLine();
                        }
                        bw.flush();
                     //   infotext.setText("Exported Successfully.");
                        
                        new CSVToExcelConverter().execute();
                    }
                } catch (Exception ex) {
                    if (sqldb.isOpen()) {
                        sqldb.close();
                   //     infotext.setText(ex.getMessage().toString());
                    }
 
                } finally {
 
                }
 
            }
        });
		
	}
	
	/*public void exportCSV(){
		 File dbFile=getDatabasePath("Servey.db");
		 
		 System.out.println("db path"+dbFile);
	     DatabaseHandler dbhelper = new DatabaseHandler(getApplicationContext());
	      File exportDir = new File(Environment.getExternalStorageDirectory(), "");        
	     if (!exportDir.exists()) 
	     {
	         exportDir.mkdirs();
	     }  
	     
	     System.out.println("Export dir:"+exportDir);

	     File file = new File(exportDir, "servey.csv");
	     try    
	     {
	         file.createNewFile();                
	         CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
	         SQLiteDatabase db = dbhelper.getReadableDatabase();
	         Cursor curCSV = db.rawQuery("SELECT * FROM SERVEY_DATA",null);
	         csvWrite.writeNext(curCSV.getColumnNames());
	         while(curCSV.moveToNext())
	         {
	            //Which column you want to exprort
	             String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4)};//,curCSV.getString(5),curCSV.getString(6),curCSV.getString(7),curCSV.getString(8),curCSV.getString(9),curCSV.getString(10),curCSV.getString(11)};//,curCSV.getString(12),curCSV.getString(13)};
	             csvWrite.writeNext(arrStr);
	         }   
	         csvWrite.close();
	         curCSV.close();
	     }
	     catch(Exception sqlEx)
	     {
	         Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
	     }
	 
	}*/
	public class CSVToExcelConverter extends AsyncTask<String, Void, Boolean> {


	private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

	@Override
	protected void onPreExecute()
	{this.dialog.setMessage("Exporting to excel...");
	this.dialog.show();}

	@Override
	protected Boolean doInBackground(String... params) {
	   ArrayList arList=null;
	   ArrayList al=null;

	   //File dbFile= new File(getDatabasePath("database_name").toString());
	   File dbFile=getDatabasePath(DatabaseHandler.DATABASE_NAME);
	   String yes= dbFile.getAbsolutePath();

	   
	
	   File exportDir = new File(Environment.getExternalStorageDirectory(), "/servey");        
	     if (!exportDir.exists()) 
	     {
	         exportDir.mkdirs();
	     }  
	     
	     Date dNow = new Date( );
	      SimpleDateFormat ft = 
	      new SimpleDateFormat ("dd-MM-yyyy");
	      Animation animBlink;
		//  tv_date.setText(ft.format(dNow)+"-"+Integer.toString(level)+"%");
	     String inFilePath = Environment.getExternalStorageDirectory().toString()+"/MyBackUp.csv";
		    outFilePath = Environment.getExternalStorageDirectory().toString()+"/servey/"+"ft.format(dNow).xls";
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
	   System.out.println();
	   i++;
	   }} catch (Exception e) {
	       System.out.println("shit");
	   }

	   try
	   {
	   HSSFWorkbook hwb = new HSSFWorkbook();
	   HSSFSheet sheet = hwb.createSheet("new sheet");
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
	   System.out.println();
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

	protected void onPostExecute(final Boolean success)

	{

	   if (this.dialog.isShowing())

	   {

	       this.dialog.dismiss();

	   }

	   if (success)

	   {

	       Toast.makeText(MainActivity.this, "File exported successfully :"+outFilePath, Toast.LENGTH_LONG).show();

	   }

	   else

	   {

	       Toast.makeText(MainActivity.this, "File export failed", Toast.LENGTH_SHORT).show();

	   }

	}


	}

}
