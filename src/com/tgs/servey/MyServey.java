package com.tgs.servey;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tgs.adapter.CustomAdapter;
import com.tgs.adapter.DatabaseHandler;
import com.tgs.bean.SpinnerModel;

public class MyServey extends ActionBarActivity {
	String[] goodsTypeArray ;
	String[] passengerTypeArray ;
	
	String[] passengervehTypeArray ;
	String[] goodsvehTypeArray ;
	Button btn_save;
	String str_adress="";
	EditText et_origin,et_destination,et_times,et_tripLength;
	double latitude;
	double longitude;
	String toDay_DATE;
	String[] language;
	String[] commodity;
	String[] tripfrq;
	  public  ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
	    TextView output = null;
	    CustomAdapter adapter;
	    MyServey activity = null;
	    RadioGroup rg_vehtype,rg_roundTrip;
	    Spinner  spinner_vehType,spn_tripfreq,spn_commodity;
	EditText et_UserName=null;
	EditText et_Password=null;
	GPSTracker gpsTracker;
	
	DatabaseHandler db_Handler;
	
	
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	private static final int ACTION_TAKE_VIDEO = 3;

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private ImageView mImageView;
	private Bitmap mImageBitmap;

	private static final String VIDEO_STORAGE_KEY = "viewvideo";
	private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
	//private VideoView mVideoView;
	private Uri mVideoUri;

	private String mCurrentPhotoPath;
	private final String USER_PREF_FILE_NAME="user_time";

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	
	/* Photo album for this application */
	private String getAlbumName() {
		
		
		String time=getCurrentTimeStamp();
		String prefixDate=time.split(":")[0];
		 
		int minuts=Integer.parseInt(time.split(":")[1]);
		
		String minutFrame="";
		if(minuts<=30)
		{
			  minutFrame="0-29";
		}
		else{
			 minutFrame="30-59";
		}
		
		String dirName=prefixDate+minutFrame;
		 
		
		
		
		
		
		 
	//	return getString(R.string.album_name);
		return prefixDate;
	}
	
	
	public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}

	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mImageView.setVisibility(View.VISIBLE);
		mImageView.setImageBitmap(bitmap);
		mVideoUri = null;
		//mVideoView.setVisibility(View.INVISIBLE);
	}

/*	private void galleryAddPic() {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = new File(mCurrentPhotoPath);
			
			System.out.println("Current photo path:"+mCurrentPhotoPath);
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}*/

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}

	 

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			//galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}

 
	Button.OnClickListener mTakePicOnClickListener = 
		new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
		}
	};

	Geocoder geocoder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servey);
		
		db_Handler=new DatabaseHandler(getApplicationContext());
 
		et_origin=(EditText)findViewById(R.id.et_origin);
		et_destination=(EditText)findViewById(R.id.et_destination);
		et_times=(EditText)findViewById(R.id.et_times);
		et_tripLength=(EditText)findViewById(R.id.et_triplength);
		btn_save=(Button) findViewById(R.id.btn_save);
		spinner_vehType = (Spinner)findViewById(R.id.spn_vehtype);
		spn_commodity=(Spinner)findViewById(R.id.spn_commodity);
		spn_tripfreq=(Spinner)findViewById(R.id.spn_tripfrq);
		  goodsTypeArray = getResources().getStringArray(R.array.gooodstype);
		  passengerTypeArray= getResources().getStringArray(R.array.passengertype);
		  tripfrq=getResources().getStringArray(R.array.tripFreq);
		  goodsvehTypeArray = getResources().getStringArray(R.array.goodsvehArr);
		  commodity=getResources().getStringArray(R.array.commodity);
		  language=getResources().getStringArray(R.array.regno_Arr);
		  passengervehTypeArray = getResources().getStringArray(R.array.passengerVehArr);
		rg_vehtype=(RadioGroup)findViewById(R.id.myRadioGroup);
		rg_roundTrip=(RadioGroup)findViewById(R.id.rg_roundtrip);
		
		
		  activity  = this;
		   geocoder = new Geocoder(this, Locale.ENGLISH);
		//  gpsTracker = new GPSTracker(MyServey.this);
		   ArrayAdapter<String> acadapter = new ArrayAdapter<String>  
           (this,android.R.layout.select_dialog_item,language);  
       //Getting the instance of AutoCompleteTextView  
          AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);  
          actv.setThreshold(1);//will start working from first character  
          actv.setAdapter(acadapter);//setting the adapter data into the AutoCompleteTextView  
          actv.setTextColor(Color.WHITE);  
	        setGoodsListData();
	        
	        Resources res = getResources(); 
	        adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res);
	        spinner_vehType.setAdapter(adapter);
	        
	        
	        
	        
	        ArrayAdapter<String> adapter_commodity = new ArrayAdapter<String>(this,  android.R.layout.simple_dropdown_item_1line, commodity);
	        ArrayAdapter<String> adapter_tripfreq = new ArrayAdapter<String>(this,  android.R.layout.simple_dropdown_item_1line, tripfrq);
	        spn_commodity.setAdapter(adapter_commodity);
	        spn_tripfreq.setAdapter(adapter_tripfreq);
	        
		rg_vehtype.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		  
		 

            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // find which radio button is selected

                if(checkedId == R.id.goods) {
                	CustomListViewValuesArr.clear();
                	setGoodsListData();
                	 Resources res = getResources(); 
                     adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res);
                     spinner_vehType.setAdapter(adapter);

                } else if(checkedId == R.id.passengers) {
                	CustomListViewValuesArr.clear();
					setPassengerListData();
					Resources res = getResources(); 
					adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res);
					spinner_vehType.setAdapter(adapter);
                  

                }
                
                
                

            }

        });
		

		
		 mImageView = (ImageView) findViewById(R.id.catute_image);
		//mVideoView = (VideoView) findViewById(R.id.videoView1);
	//mImageBitmap = null;
	//	mVideoUri = null;

	 	Button picBtn = (Button) findViewById(R.id.btn_camera);
		setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);

	
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
			       String vehregno =CustomListViewValuesArr.get(spinner_vehType.getSelectedItemPosition()).getCompanyName().toString();
			       String veh_comd = spn_commodity.getSelectedItem().toString();
			       String veh_frq = spn_tripfreq.getSelectedItem().toString();
			       System.out.println("veh reg no"+vehregno);
			     
			       
			       SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
					toDay_DATE= sdf.format(new Date());
			       
			       ContentValues cv_values=new ContentValues();
			       cv_values.put(DatabaseHandler.VEH_REG_NO,vehregno);
			       cv_values.put(DatabaseHandler.ORIGIN,et_origin.getText().toString());
			       cv_values.put(DatabaseHandler.DESTINATION,et_destination.getText().toString());
			       cv_values.put(DatabaseHandler.COMMODITY,veh_comd);
			       cv_values.put(DatabaseHandler.TRIP_TIME,et_times.getText().toString());
			       cv_values.put(DatabaseHandler.TRIP_FREQ,veh_frq);
			       cv_values.put(DatabaseHandler.RETURN_TRIP,"NO");
			       cv_values.put(DatabaseHandler.MONTHLY_PASS,"");
			       cv_values.put(DatabaseHandler.WEIGHT_IN_TONS,"");
			       cv_values.put(DatabaseHandler.PAY_TOLL,"");
			       cv_values.put(DatabaseHandler.LATITUDE,latitude);
			       cv_values.put(DatabaseHandler.LONGITUDE,longitude);
			       cv_values.put(DatabaseHandler.IMAGEPATH,mCurrentPhotoPath);
			       cv_values.put(DatabaseHandler.CREATED_DATE,toDay_DATE);
			       db_Handler.insert(DatabaseHandler.TABLE_servey_Data, cv_values);
			       
			      finish();
			}
		});



		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Servey-Up");
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.iconbg));
	
		
		gpsTracker = new GPSTracker(MyServey.this);
		// check if GPS enabled		
       if(gpsTracker.canGetLocation()){
    	 latitude = gpsTracker.getLatitude();
      	 longitude = gpsTracker.getLongitude();
       }else{
    	    gpsTracker.showSettingsAlert();
    	   Toast.makeText(MyServey.this, "Please turn on GPS", Toast.LENGTH_LONG).show();
       } 
		
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	
	 public void startSurvey()

	    {
			Intent mapIntent=new Intent(getApplicationContext(), MyServey.class);
			
			startActivity(mapIntent);
	    }
	   public void setGoodsListData()
	    {
	    
			for (int i =0; i < goodsTypeArray.length; i++) {
				
				final SpinnerModel sched = new SpinnerModel();
				
				    
				   sched.setCompanyName(goodsTypeArray[i]);
				   
				   sched.setImage(goodsvehTypeArray[i]);
				   CustomListViewValuesArr.add(sched);
			}
		
				
		
			
	    }
	   public void setPassengerListData()
	    {
	    	
			for (int i = 0; i <passengerTypeArray.length; i++) {
				
				final SpinnerModel sched = new SpinnerModel();
				    
				   sched.setCompanyName(passengerTypeArray[i]);
				   sched.setImage(passengervehTypeArray[i]);
				   
				CustomListViewValuesArr.add(sched);
			}
			
	    }
	   
	      
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			switch (requestCode) {
			case ACTION_TAKE_PHOTO_B: {
				if (resultCode == RESULT_OK) {
					handleBigCameraPhoto();
				}
				break;
			} // ACTION_TAKE_PHOTO_B

			} // switch
		}
	   
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			
			Intent i=new Intent(getApplicationContext(),MainActivity.class);
			startActivity(i);
		}
	 
}
