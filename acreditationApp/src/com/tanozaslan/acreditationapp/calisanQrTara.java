package com.tanozaslan.acreditationapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class calisanQrTara extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	private Button btnQrCodeTara;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initializeUI();
    }
    
    private void initializeUI(){
    	btnQrCodeTara=(Button)findViewById(R.id.btnQrCodeTara);
    	btnQrCodeTara.setOnClickListener(this);
    }
     
    private void runScan(){
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
		Log.d("2","2");

	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		String [] urunKodIsim;	
		   if (requestCode == 0) {
		      if (resultCode == RESULT_OK) {
			     // Handle successful scan

		         String contents = intent.getStringExtra("SCAN_RESULT");
		         //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
		         
		         startCalisanIzleActivity(parseQrContent(contents));         
		         
		      } else if (resultCode == RESULT_CANCELED) {
		         // Handle cancel
		      }
		   }
		}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnQrCodeTara:
			//getDbContent();
			Log.d("1","1");
			runScan();
			break;
		}
	}
	
	private String[] parseQrContent(String content){
		String [] calisanOzellik = new String [5];
		String finishString="&/&";
		String codeQue="id";
		String nameQue="isim";
		String kimlikQue="kimlikno";
		String firmaQue="firma";
		String gorevQue="gorev";
		
		Log.d("CONTENT 1",content);
		//content=content.replace(" ","");
		Log.d("CONTENT 2",content);
		
		//ID Parsing
		if(content.indexOf(codeQue)==-1){  
			calisanOzellik[0]="no id";		
		}else{			
			int finishIndex=content.indexOf(finishString,content.indexOf(codeQue));		
			calisanOzellik[0]=content.substring(content.indexOf(codeQue)+codeQue.length(),finishIndex);		
			Log.d("id",calisanOzellik[0]);
		}
		
		//ISIM Parsing		
		if(content.indexOf(nameQue)==-1){  
			calisanOzellik[1]="no name";		
		}else{			
			int finishIndex=content.indexOf(finishString,content.indexOf(nameQue));		
			calisanOzellik[1]=content.substring(content.indexOf(nameQue)+nameQue.length(),finishIndex);		
			Log.d("Isim",calisanOzellik[1]);
		}
		
		//KimlikNo Parsing
		if(content.indexOf(kimlikQue)==-1){  
			calisanOzellik[2]="no kimlik";		
		}else{			
			int finishIndex=content.indexOf(finishString,content.indexOf(kimlikQue));		
			calisanOzellik[2]=content.substring(content.indexOf(kimlikQue)+kimlikQue.length(),finishIndex);		
			Log.d("kimlikno",calisanOzellik[2]);
		}

		//Firma Parsing
		if(content.indexOf(firmaQue)==-1){  
			calisanOzellik[3]="no firma";		
		}else{			
			int finishIndex=content.indexOf(finishString,content.indexOf(firmaQue));		
			calisanOzellik[3]=content.substring(content.indexOf(firmaQue)+firmaQue.length(),finishIndex);		
			Log.d("firma",calisanOzellik[3]);
		}	
		
		//Gorev Parsing
		if(content.indexOf(gorevQue)==-1){  
			calisanOzellik[4]="no gorev";		
		}else{			
			int finishIndex=content.indexOf(finishString,content.indexOf(gorevQue));		
			calisanOzellik[4]=content.substring(content.indexOf(gorevQue)+gorevQue.length(),finishIndex);		
			Log.d("gorev",calisanOzellik[4]);
		}		
		
		
	
		return calisanOzellik;
		
	}

	private void startCalisanIzleActivity(String[] calisanOzellik){
		Intent intentCalisanIzle = new Intent(calisanQrTara.this, calisanIzle.class);
		intentCalisanIzle.putExtra("calisanOzellikStringArr", calisanOzellik);
		intentCalisanIzle.putExtra("isInternetActive", isInternetActive());
		startActivity(intentCalisanIzle);
	}
	
	private boolean isInternetActive(){
		final boolean internetState; 
		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
		    internetState=true;
		} else {
		    internetState=false;
		}	
		
		return internetState;
		
	}
    
}