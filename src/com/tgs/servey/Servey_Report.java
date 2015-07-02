package com.tgs.servey;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tgs.adapter.DatabaseHandler;
import com.tgs.bean.ServeyBean;

public class Servey_Report extends Activity{
	
	private ServeyBean ItemMyDiary=null;
	
	private ListView serveyreport_list=null;
	
	private ImageView im_veh=null;
	TextView tv_vehRegno,tv_origin,tv_destination,tv_date;
	
	DatabaseHandler dbHandler;
	
	public static ArrayList<ServeyBean> arr_SERVEY=new ArrayList<ServeyBean>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servey_report);
		serveyreport_list=(ListView)findViewById(R.id.serveyreport_list);
		dbHandler=new DatabaseHandler(getApplicationContext());
		
		Cursor c=dbHandler.RetriveData("Select * from SERVEY_DATA");
		
		if(c!=null){
			arr_SERVEY.clear();
			if(c.getCount()>0){
				c.moveToFirst();
				do{
					ServeyBean sbean=new ServeyBean();
					
					sbean.setCOMMODITY(c.getString(c.getColumnIndex(DatabaseHandler.COMMODITY)));
					sbean.setCREATED_DATE(c.getString(c.getColumnIndex(DatabaseHandler.CREATED_DATE)));
					sbean.setDESTINATION(c.getString(c.getColumnIndex(DatabaseHandler.DESTINATION)));
					sbean.setIMAGEPATH(c.getString(c.getColumnIndex(DatabaseHandler.IMAGEPATH)));
					sbean.setLATITUDE(c.getString(c.getColumnIndex(DatabaseHandler.LATITUDE_LONGITUDE)));
				//	sbean.setLONGITUDE(c.getString(c.getColumnIndex(DatabaseHandler.LONGITUDE)));
					sbean.setMONTHLY_PASS(c.getString(c.getColumnIndex(DatabaseHandler.MONTHLY_PASS)));
					sbean.setORIGIN(c.getString(c.getColumnIndex(DatabaseHandler.ORIGIN)));
					sbean.setPAY_TOLL(c.getString(c.getColumnIndex(DatabaseHandler.DESTINATION)));
					sbean.setRETURN_TRIP(c.getString(c.getColumnIndex(DatabaseHandler.RETURN_TRIP)));
					sbean.setTRIP_FREQ(c.getString(c.getColumnIndex(DatabaseHandler.TRIP_FREQ)));
					sbean.setVEH_REG_NO(c.getString(c.getColumnIndex(DatabaseHandler.VEH_REG_NO)));
					
					arr_SERVEY.add(sbean);
					
					
				}while(c.moveToNext());
			}
		}
		
		serveyreport_list.setAdapter(new serveyListListAdapter(getApplicationContext(), arr_SERVEY));
	}
	
	
	class serveyListListAdapter extends BaseAdapter{
		List<ServeyBean> arr_SERVEY ;
		
		 Context mContext;
	
		public serveyListListAdapter(Context _MyContext, List<ServeyBean> arr_seveyList) {
			// TODO Auto-generated constructor stub
			this.arr_SERVEY=arr_seveyList;
			//mContext = _MyContext;
			this.mContext=_MyContext;
			
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr_SERVEY.size();
		}
	
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arr_SERVEY.get(position);
		}
	
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		@SuppressLint("ViewHolder")
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			View MyView = arg1;
			//	final View MyView1 = convertView;
				 
				try {
					ItemMyDiary = (ServeyBean) getItem(arg0);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				
				if(MyView==null){
				
					 
					 LayoutInflater inflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				//	 LayoutInflater inflater=getLayoutInflater();
				  MyView=inflater.inflate(R.layout.serveyreport_child, arg2, false);
				 }
				
		
				im_veh=(ImageView)MyView.findViewById(R.id.image);
				
				tv_vehRegno=(TextView)MyView.findViewById(R.id.vehregno);
				tv_origin=(TextView)MyView.findViewById(R.id.txt_origin);
				tv_destination=(TextView)MyView.findViewById(R.id.txt_destnce);
				tv_date=(TextView)MyView.findViewById(R.id.txt_datetime);
				
				tv_vehRegno.setText("Vehcle_Reg.No"+ItemMyDiary.getVEH_REG_NO());
				tv_destination.setText("Destination"+ItemMyDiary.getDESTINATION());
				tv_origin.setText("Origin"+ItemMyDiary.getORIGIN());
				tv_date.setText("Date"+ItemMyDiary.getCREATED_DATE());
			    
			        
			return MyView;
		}
		
	}
}
