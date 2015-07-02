package com.tgs.servey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	Button btn_Login=null;
	
	
	EditText et_UserName=null;
	EditText et_Password=null;
	String strUsername="", str_pref_UserName="";
	String strPassword="", str_pref_Password;
	SharedPreferences mypref=null;
	GPSTracker gpsTracker;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		et_UserName=(EditText) findViewById(R.id.et_username);
		et_Password=(EditText) findViewById(R.id.et_pwd);
		btn_Login=(Button) findViewById(R.id.btn_login);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Login");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.iconbg));
		mypref=getSharedPreferences("Credentials", 0);
		str_pref_UserName=mypref.getString("UserName", "");
		str_pref_Password=mypref.getString("Password", "");	
		/*if(str_pref_UserName.equals(""))
			btn_Login.setText("Register");
		else
			btn_Login.setText("Login");
		*/gpsTracker=new GPSTracker(LoginActivity.this);	
		
		btn_Login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				strUsername=et_UserName.getText().toString().trim();
				strPassword=et_Password.getText().toString().trim();
				
			
			if(strUsername.length()!=0 && strPassword.length()!=0){
							if(strUsername.equalsIgnoreCase("admin") && strPassword.equalsIgnoreCase("admin"))
							{
								//
								
								Intent mapIntent=new Intent(LoginActivity.this, MainActivity.class);
							
								startActivity(mapIntent);
							}
							else
							{
								Toast.makeText(LoginActivity.this, "Invalid credentials, please check and try again", Toast.LENGTH_LONG).show();
							}
							
				}else{
					if(strUsername.length()==0)
					{
						et_UserName.setError("Invalid");
					}
					if(strPassword.length()==0)
					{
						et_Password.setError("Invalid");
					}
					Toast.makeText(LoginActivity.this, "Please enter Username and Password", Toast.LENGTH_LONG).show();
					
				}
						}
					//}
			//}
		});
		
		
	}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	gpsTracker.getLocation();
	Log.d("Loc data","lat:"+gpsTracker.getLatitude());
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
