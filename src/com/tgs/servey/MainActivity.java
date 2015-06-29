package com.tgs.servey;

import com.tgs.adapter.DatabaseHandler;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity{

	Button btn_startservey,btn_serveyreport;
	
	
	EditText et_UserName=null;
	EditText et_Password=null;
	TextView txt_noserveys;
	GPSTracker gpsTracker;
	DatabaseHandler db;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		btn_startservey=(Button) findViewById(R.id.btn_startservey);
		btn_serveyreport=(Button)findViewById(R.id.btn_report);
		txt_noserveys=(TextView)findViewById(R.id.txt_noserveys);
		db=new DatabaseHandler(getApplicationContext());
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Home");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.iconbg));
		
		Cursor c=db.RetriveData("Select * from SERVEY_DATA");
		
		if(c.getCount()==0 || c==null){
			txt_noserveys.setText("Number of serveys: 0");
		}else{
			txt_noserveys.setText("Number of serveys: "+c.getCount());
		}
		
	
		btn_startservey.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(),MyServey.class);
				startActivity(i);
			}
		});
		
		btn_serveyreport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(),Servey_Report.class);
				startActivity(i);
			}
		});
		
	}
	


}
