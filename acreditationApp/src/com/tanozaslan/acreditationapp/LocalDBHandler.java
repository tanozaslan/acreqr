package com.tanozaslan.acreditationapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
 
public class LocalDBHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    //Application Name
    private static final String PACKACE_NAME = "com.tanozaslan.acreditationapp";
    // Database Name
    private static final String DATABASE_NAME = "calisanAkre";
 
    //GIRIS CIKIS TABLE
    //table names
    private static final String TABLE_GIRISCIKIS = "giriscikis";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "ID";
    private static final String CALISAN_ID = "calisanid";
    private static final String KEY_GIRIS = "giris";    
    private static final String KEY_CIKIS = "cikis";
    
    // PICTURE TABLE
    private static final String TABLE_PICTURES = "pictures";
    // Contacts Table Columns names
    private static final String PICTURE_PATH = "URI";    
    
    public LocalDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.createTableGirisCikis();
        this.createTablePictures();
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIRISCIKIS);
 
        // Create tables again
        //onCreate(db);
        //Log.d("OnUpgrade_Table Exist?",String.valueOf(isTableExists(db,TABLE_GIRISCIKIS, false)));

    }
    
    public void createTableGirisCikis(){
        SQLiteDatabase db = this.getWritableDatabase();

    	if(!isTableExists(db,TABLE_GIRISCIKIS, false)){
        
	        String CREATE_CONTACTS_TABLE = "create table " + TABLE_GIRISCIKIS + "("
	                + KEY_ID + " integer primary key autoincrement, "
	        		+ CALISAN_ID + " INTEGER NOT NULL, "
	        		+ KEY_GIRIS + " TEXT, "
	                + KEY_CIKIS + " TEXT);";
	                
	        db.execSQL(CREATE_CONTACTS_TABLE);
	        Log.d("**********","Creating GirisCikis Table");
    	}
        Log.d("createTableGirisCikis_Table Exist?",String.valueOf(isTableExists(db,TABLE_GIRISCIKIS, false)));
        db.close();

    }
    
    public void createTablePictures(){
    	SQLiteDatabase db = this.getWritableDatabase();

    	if(!isTableExists(db,TABLE_PICTURES, false)){
        
	        String CREATE_CONTACTS_TABLE = "create table " + TABLE_PICTURES + "("
	                + KEY_ID + " integer primary key autoincrement, "
	        		+ CALISAN_ID + " INTEGER NOT NULL, "
	                + PICTURE_PATH + " TEXT);";
	                
	        db.execSQL(CREATE_CONTACTS_TABLE);
	        Log.d("**********","Creating PICTURES Table");
    	}
        //Log.d("createTableGirisCikis_Table Exist?",String.valueOf(isTableExists(db,TABLE_GIRISCIKIS, false)));
    	db.close();
    }
 
    public void updateGiris(int id, String time) {
    	
    	createTableGirisCikis();
    	
    	SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(CALISAN_ID, id); // Calisan ID
        values.put(KEY_GIRIS, time); // Calisan Giris
        values.put(KEY_CIKIS, "-"); // Calisan Cikis
 
        Log.d("Table Exist?",String.valueOf(isTableExists(db,TABLE_GIRISCIKIS, false)));
       
        // Inserting Row
        db.insert(TABLE_GIRISCIKIS, null, values);
        db.close(); // Closing database connection
    }
    
    public void updateCikis(int id, String time) {
    	createTableGirisCikis();
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(CALISAN_ID, id); // Calisan ID
        values.put(KEY_GIRIS, "-"); // Calisan Giris
        values.put(KEY_CIKIS, time); // Calisan Cikis

        Log.d("updateCikis_Table Exist?",String.valueOf(isTableExists(db,TABLE_GIRISCIKIS, false)));
        Log.d("DataBasePathVersion",db.getPath()+"-"+db.getVersion());
        
        // Inserting Row
        db.insert(TABLE_GIRISCIKIS, null, values);
        db.close(); // Closing database connection
    }
    
    public void updatePicture(int id,String picturePath){
    	createTablePictures();
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(CALISAN_ID, id); // Calisan ID
        values.put(PICTURE_PATH, picturePath); // Image Path

        Log.d("updatePicture_Table Exist?",String.valueOf(isTableExists(db,TABLE_PICTURES, false)));
        //Log.d("DataBasePathVersion",db.getPath()+"-"+db.getVersion());
        
        // Inserting Row
        db.insert(TABLE_PICTURES, null, values);
                
        db.close(); // Closing database connection
    }
    
    public String getPicture(int id){
    	String picturePath; 
    	createTablePictures();
    	Log.d("PCCTURE TABLE COUNT:",String.valueOf(getTableCount(TABLE_PICTURES)));
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
       
        //Cursor cursor = db.rawQuery(" SELECT"+PICTURE_URI+"FROM"+TABLE_PICTURES+" WHERE usuario='"+Integer.toString(id)+"'");
        String[] args = new String[] {Integer.toString(id)};
        Cursor cursor = db.query(TABLE_PICTURES, null,CALISAN_ID+"=?",args,null, null, null);
 
        Log.d("GetPicture_Cursor",cursor.toString());
        cursor.moveToFirst();
        
        if(cursor.getCount()==0){
        	db.close();
        	return "noPicture";
        }
                        
        do {
            Log.d("Element=",cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
            picturePath=cursor.getString(2);
        }while (cursor.moveToNext());
        db.close(); // Closing database connection

        return picturePath;
                
    }
    
    public int getTableCount(String TableName) {    	
    	SQLiteDatabase db=this.getWritableDatabase();
		Cursor cur= db.rawQuery("Select * from "+TableName, null);
		int x= cur.getCount();
		cur.close();
		db.close();
		return x;    	 
    }
    
    private boolean isTableExists(SQLiteDatabase mDatabase,String tableName, boolean openDb) {
        if(openDb) {
            if(mDatabase == null || !mDatabase.isOpen()) {
                mDatabase = getReadableDatabase();
            }

            if(!mDatabase.isReadOnly()) {
                mDatabase.close();
                mDatabase = getReadableDatabase();
            }
        }

        Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                                cursor.close();
                return true;
            }
                        cursor.close();
        }
        return false;
    }

    public void bckupDBtoSD(){
    	try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            SQLiteDatabase db = this.getWritableDatabase();

            Log.d("File Data",String.valueOf(data));
            Log.d("File SD",String.valueOf(sd));
            Log.d("DB Directory",db.getPath());
            
            if (sd.canWrite()) {
                String currentDBPath = "//data//"+ PACKACE_NAME +"//databases//"+DATABASE_NAME;
                String backupDBPath = DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.d("currentDB",currentDB.getAbsolutePath());
                
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    
                    Log.d("bckupDBtoSD","Database is Backed up to SD");
                    
                }
                else Log.d("BackUp Error","No database");
            }
            else Log.d("BackUp Error","cant write SD");
        } catch (Exception e) {
        }
    }
    
}