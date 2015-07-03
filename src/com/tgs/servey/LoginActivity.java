package com.tgs.servey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaserActinbBar {

	Button btn_Login=null;
	
	
	EditText et_UserName=null;
	EditText et_Password=null;
	String strUsername="", str_pref_UserName="";
	String strPassword="", str_pref_Password;
	SharedPreferences mypref=null;
	 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		et_UserName=(EditText) findViewById(R.id.et_username);
		et_Password=(EditText) findViewById(R.id.et_pwd);
		btn_Login=(Button) findViewById(R.id.btn_login);
		/*final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Login");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.iconbg));*/
		mypref=getSharedPreferences("Credentials", 0);
		str_pref_UserName=mypref.getString("UserName", "");
		str_pref_Password=mypref.getString("Password", "");	
		/*if(str_pref_UserName.equals(""))
			btn_Login.setText("Register");
		else
			btn_Login.setText("Login");
		*/ 
		
		btn_Login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				strUsername=et_UserName.getText().toString().trim();
				strPassword=et_Password.getText().toString().trim();
				
			
			if(strUsername.length()!=0 && strPassword.length()!=0){
							if(strUsername.equalsIgnoreCase("Admin") && strPassword.equalsIgnoreCase("admin@123"))
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
			 
		});
		
		
	}
 
}
