package com.tanozaslan.acreditationapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Size;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class calisanIzle extends Activity implements OnClickListener{
	
	private String [] calisanOzellik;
	private InputStream is;
	private calisan calisanGelen;
	private ImageView ivResim;
	private TextView tvInternet;
	private EditText etIsim, etTCKimlik, etFirma, etGorev, etBolge;
	private RelativeLayout llmain;
	private Button btnGiris, btnCikis;
	private boolean isInternetActive;
	private boolean isThereQueryError=false;
	private Uri imageUri;
	private String resimPath;

	
	static final int TAKE_PICTURE = 0;
    LocalDBHandler localDB;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calisan);
		
		getCalisanOzellik();
		
	    initializeUI();

		//SET CALISANGELEN
		if(isInternetActive){
			getCalisanWithId(Integer.parseInt(calisanOzellik[0]));
			tvInternet.setText("ONLINE");
		}
		else{
			getCalisanFromQR();
			tvInternet.setText("OFFLINE");
		}
	    	    
	    setCalisanViews();
		
	    localDB = new LocalDBHandler(this);

		Log.d("CalisanIzle_SetImageView_OnCreate",localDB.getPicture(Integer.parseInt(calisanOzellik[0])));

		setImageView(calisanGelen.getId());
		
	    Log.d("**********",String.valueOf(calisanGelen.getIceride()));	
	    
	    //Log.d("",localDB);
	    
	}
		
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
	}

	private void getCalisanOzellik(){
		Bundle extras = getIntent().getExtras();
        if(extras !=null) {
        	calisanOzellik = extras.getStringArray("calisanOzellikStringArr");
        	isInternetActive = extras.getBoolean("isInternetActive");
        }
	}
	
	private void initializeUI(){		
		llmain=(RelativeLayout)findViewById(R.id.llmain);
		
		tvInternet=(TextView)findViewById(R.id.tvInternet);
		
		ivResim=(ImageView)findViewById(R.id.ivResim);
		ivResim.setOnClickListener(this);
		
		etIsim=(EditText)findViewById(R.id.etIsim);
		etTCKimlik=(EditText)findViewById(R.id.etTCKimlik);
		etFirma=(EditText)findViewById(R.id.etFirma);
		etGorev=(EditText)findViewById(R.id.etGorev);
		etBolge=(EditText)findViewById(R.id.etBolge);
		
		btnGiris=(Button)findViewById(R.id.btnGiris);
		btnGiris.setOnClickListener(this);		
		btnCikis=(Button)findViewById(R.id.btnCikis);
		btnCikis.setOnClickListener(this);
		
	}
	
	private void setCalisanViews(){
		
		int bolge=calisanGelen.getBolge();
		etIsim.setText(calisanGelen.getIsim());
		etTCKimlik.setText(String.valueOf(calisanGelen.getKimlikno()));
		etFirma.setText(calisanGelen.getFirma());
		etGorev.setText(calisanGelen.getGorev());
		etBolge.setText(String.valueOf(calisanGelen.getBolge()));
		
		switch(bolge){
		case 1:
			llmain.setBackgroundColor(Color.argb(255, 115, 159, 48));
			llmain.invalidate();
			break;
		case 2:
			llmain.setBackgroundColor(Color.argb(255, 64, 126, 245));
			llmain.invalidate();
			break;
		case 3:
			llmain.setBackgroundColor(Color.argb(255, 212, 74, 73));
			llmain.invalidate();
			break;
		}
		
		updateGirisCikisBtn();
	}
		
	private void getCalisanFromQR() {
		// TODO Auto-generated method stub
		calisanGelen = new calisan(Integer.parseInt(calisanOzellik[0]),calisanOzellik[1],Integer.parseInt(calisanOzellik[2]),calisanOzellik[3],calisanOzellik[4],1,true);
	}
	
	private void getCalisanWithId(int id){
		String result = ""; ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
		nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(id))); 
		// Send the HTTP POST request. 
		try { 
			HttpClient httpclient = new DefaultHttpClient(); 
			HttpPost httppost = new HttpPost("http://www.tanozaslan.com/getPeopleWithId.php"); 
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			HttpResponse response = httpclient.execute(httppost); 
			HttpEntity entity = response.getEntity(); 
			is = entity.getContent(); 
			} 
		catch(Exception ex) { 			
			Toast toast = Toast.makeText(this, "Error in HTTP connection: " + ex.toString(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
			//toast("Error in HTTP connection: " + ex.toString()); 
			} 

		// Read the data into a string. 		
		try { 
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8); 
			StringBuilder sb = new StringBuilder(); 
			String line = null; 
			while ((line = reader.readLine()) != null) sb.append(line + "\n"); 
			is.close(); result=sb.toString(); 
			} 
		catch(Exception ex) { 
			
			Toast toast = Toast.makeText(this, "Error reading server response: " + ex.toString(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
			//toast("Error reading server response: " + ex.toString()); 
			} 
		
		// Parse the JSON data. 
		try { 
			JSONArray jArray = new JSONArray(result); 
			for(int i=0;i<jArray.length();i++) { 
				JSONObject json_data = jArray.getJSONObject(i); 
				
				boolean iceride=false;
				
				
				if(json_data.getInt("iceride")==0)iceride=false;
				else iceride=true;
				
				calisanGelen = new calisan(json_data.getInt("id"),json_data.getString("isim"),json_data.getInt("kimlikno"),json_data.getString("firma"),json_data.getString("gorev"),Integer.parseInt(json_data.getString("bolge")),iceride);
				
				
				/*Toast toast = Toast.makeText(this, "id: " + json_data.getInt("id") + 
												   ", isim: " + json_data.getString("isim") + 
												   ", kimlikno: " + json_data.getInt("kimlikno") + 
												   ", firma: " + json_data.getString("firma") + 
												   ", gorev: " + json_data.getString("gorev") +
												   ", bolge: " + json_data.getString("bolge") + 
												   ", iceride: "+ json_data.getInt("iceride"), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();*/
								
				//toast("id: " + json_data.getInt("id") + ", name: " + json_data.getString("name") + ", sex: " + json_data.getInt("sex") + ", birthyear: "+ json_data.getInt("birthyear")); 
				} 
			} 
		catch(JSONException ex) { 
			
			Toast toast = Toast.makeText(this, "Error parsing JSON data: " + ex.toString(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
			//toast("Error parsing JSON data: " + ex.toString()); 
			} 
	}
		
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.ivResim:
			takePhoto();
			break;
		case R.id.btnGiris:
			if(isInternetActive){
				updateIcerideGiris(calisanGelen.getId());
				insertGirisZaman(calisanGelen.getId());
			}
			else{
				localDB.updateGiris(calisanGelen.getId(), DateFormat.getDateTimeInstance().format(new Date()));
				Toast toast = Toast.makeText(this,String.valueOf(localDB.getTableCount("giriscikis")), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				calisanGelen.setIceride(!calisanGelen.getIceride());
				localDB.bckupDBtoSD();				
			}
			updateGirisCikisBtn();
			break;
		case R.id.btnCikis:
			if(isInternetActive){
				updateIcerideCikis(calisanGelen.getId());
				insertCikisZaman(calisanGelen.getId());
			}
			else{
				localDB.updateCikis(calisanGelen.getId(), DateFormat.getDateTimeInstance().format(new Date()));
				Toast toast = Toast.makeText(this,String.valueOf(localDB.getTableCount("giriscikis")), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				calisanGelen.setIceride(!calisanGelen.getIceride());	
				localDB.bckupDBtoSD();		
			}
			updateGirisCikisBtn();
			break;
		}
	}
	
	private void takePhoto(){				
		//Intent photoIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        //startActivityForResult(photoIntent, 0);
		        
        //STACKOVERFLOW http://stackoverflow.com/questions/2729267/android-camera-intent
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		resimPath="/Pictures/AkreQR/"+calisanGelen.getIsim()+".jpg";
	    File photo = new File(Environment.getExternalStorageDirectory(),resimPath);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
	    imageUri = Uri.fromFile(photo);
	    startActivityForResult(intent, TAKE_PICTURE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    switch (requestCode) {
	    case TAKE_PICTURE:
	        if (resultCode == Activity.RESULT_OK) {
	            Uri selectedImage = imageUri;
	            getContentResolver().notifyChange(selectedImage, null);
	            ContentResolver cr = getContentResolver();
	            Bitmap bitmap;
	            try {
	                 bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
	                 
	                 ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	                 rotateImage(Bitmap.createScaledBitmap(bitmap, 
	                		 	(int)(bitmap.getWidth()/3), 
	                		 	(int)(bitmap.getHeight()/3), true),90)
	                		 	.compress(Bitmap.CompressFormat.JPEG, 20, bytes);

	                 //you can create a new file in sdcard folder.
	                 File f = new File(Environment.getExternalStorageDirectory(),resimPath);
	                 f.createNewFile();
	                 FileOutputStream fo = new FileOutputStream(f);
	                 fo.write(bytes.toByteArray());
	     		     
	                 localDB.updatePicture(calisanGelen.getId(), resimPath);

	                 setImageView(calisanGelen.getId());
                 
	            } catch (Exception e) {
	                 Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
	                 Log.d("Camera", e.toString());
	            }
	        }
	    }		
	}

	private void setImageView(int id){
		String picturePath=localDB.getPicture(id);
        //ivResim.setImageBitmap(rotateImage(Bitmap.createScaledBitmap(bitmap, 300, 200, true),90));
		File imgFile = new  File(Environment.getExternalStorageDirectory()+picturePath);
		if(imgFile.exists()){
		    ivResim.setImageBitmap(
		    					Bitmap.createScaledBitmap(BitmapFactory.decodeFile(
		    				    imgFile.getAbsolutePath()), 120, 180, true));
		    calisanGelen.setLocalResimExist(true);		    
		}
		else{
			Log.d("NoImageFle",Environment.getExternalStorageDirectory()+picturePath);
		    calisanGelen.setLocalResimExist(false);
		}
	}
	
	private Bitmap rotateImage(Bitmap b, int degrees) {
	    if (degrees != 0 && b != null) {
	        Matrix m = new Matrix();

	        m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
	        try {
	            Bitmap b2 = Bitmap.createBitmap(
	                    b, 0, 0, b.getWidth(), b.getHeight(), m, true);
	            if (b != b2) {
	                b.recycle();
	                b = b2;
	            }
	        } catch (OutOfMemoryError ex) {
	           throw ex;
	        }
	    }
	    return b;
	}
	
	private void updateIcerideGiris(int id){
		String result = ""; ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
		nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(id))); 
		// Send the HTTP POST request. 
		try { 
			HttpClient httpclient = new DefaultHttpClient(); 
			HttpPost httppost = new HttpPost("http://www.tanozaslan.com/updateIcerideGiris.php"); 
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			HttpResponse response = httpclient.execute(httppost); 
			HttpEntity entity = response.getEntity(); 
			is = entity.getContent(); 
			Log.d("**********","GIRIS YAPILIYOR BEKLE");
			
			isThereQueryError=false;

			} 
		catch(Exception ex) { 			
			Toast toast = Toast.makeText(this, "Error in HTTP connection: " + ex.toString(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
			isThereQueryError=true;
			//toast("Error in HTTP connection: " + ex.toString()); 
			}
	 
		calisanGelen.setIceride(!calisanGelen.getIceride());

	}
	
	private void updateIcerideCikis(int id){
		String result = ""; ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
		nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(id))); 
		// Send the HTTP POST request. 
		try { 
			HttpClient httpclient = new DefaultHttpClient(); 
			HttpPost httppost = new HttpPost("http://www.tanozaslan.com/updateIcerideCikis.php"); 
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			HttpResponse response = httpclient.execute(httppost); 
			HttpEntity entity = response.getEntity(); 
			is = entity.getContent(); 
			
			isThereQueryError=false;

			} 
		catch(Exception ex) { 			
			Toast toast = Toast.makeText(this, "Error in HTTP connection: " + ex.toString(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			isThereQueryError=true;

			//toast("Error in HTTP connection: " + ex.toString()); 
			}
		calisanGelen.setIceride(!calisanGelen.getIceride());
	}
	
	private void updateGirisCikisBtn(){
		//if(!isThereQueryError){
			if(calisanGelen.getIceride()){
				btnGiris.setEnabled(false);
				btnCikis.setEnabled(true);
			}
			else{
				btnGiris.setEnabled(true);
				btnCikis.setEnabled(false);
			}
			btnGiris.invalidate();
			btnCikis.invalidate();
		//}
	}

	private void insertGirisZaman(int id){
		
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

		
		String result = ""; ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
		nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(id))); 
		nameValuePairs.add(new BasicNameValuePair("giriszaman",String.valueOf(currentDateTimeString)));
		// Send the HTTP POST request. 
		try { 
			HttpClient httpclient = new DefaultHttpClient(); 
			HttpPost httppost = new HttpPost("http://www.tanozaslan.com/insertGiris.php"); 
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			HttpResponse response = httpclient.execute(httppost); 
			HttpEntity entity = response.getEntity(); 
			is = entity.getContent(); 
			Log.d("**********","GIRIS YAPILIYOR BEKLE");
			isThereQueryError=false;

			} 
		catch(Exception ex) { 			
			Toast toast = Toast.makeText(this, "Error in HTTP connection: " + ex.toString(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			isThereQueryError=true;

			//toast("Error in HTTP connection: " + ex.toString()); 
			}
	}

	private void insertCikisZaman(int id){
		
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

		
		String result = ""; ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
		nameValuePairs.add(new BasicNameValuePair("id",String.valueOf(id))); 
		nameValuePairs.add(new BasicNameValuePair("cikiszaman",String.valueOf(currentDateTimeString)));
		// Send the HTTP POST request. 
		try { 
			HttpClient httpclient = new DefaultHttpClient(); 
			HttpPost httppost = new HttpPost("http://www.tanozaslan.com/insertCikis.php"); 
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			HttpResponse response = httpclient.execute(httppost); 
			HttpEntity entity = response.getEntity(); 
			is = entity.getContent(); 
			Log.d("**********","CIKIS YAPILIYOR BEKLE");
			isThereQueryError=false;

			} 
		catch(Exception ex) { 			
			Toast toast = Toast.makeText(this, "Error in HTTP connection: " + ex.toString(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			isThereQueryError=true;

			//toast("Error in HTTP connection: " + ex.toString()); 
			}
	}

	private String getRealPathFromURI(Uri contentURI) {
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null); 
	    cursor.moveToFirst(); 
	    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	    return cursor.getString(idx); 
	}


}
