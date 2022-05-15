package com.example.rdvmanager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    // Table Name
    public static final String TABLE_NAME = "RDVs";
    // Table columns
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String CONTACT = "contact";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String ISDONE = "isDone";

    // Database Information
    static final String DB_NAME = "RDVManager.DB";
    // database version
    static final int DB_VERSION = 17;
    // Creating table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE +
            " TEXT NOT NULL, " + DATE + " TEXT, " + TIME + " TEXT, " + CONTACT + " TEXT, " + PHONE + " TEXT, " + ADDRESS + " TEXT, "+ ISDONE + " INTEGER DEFAULT 0);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void open() throws SQLException {
        database = this.getWritableDatabase();
    }

    public void close() { database.close(); }

    public void add(RDV rdv){
        ContentValues contentValues= new ContentValues();
        contentValues.put(TITLE,rdv.getTitle());
        contentValues.put(DATE,rdv.getDate());
        contentValues.put(TIME,rdv.getTime());
        contentValues.put(CONTACT,rdv.getContact());
        contentValues.put(PHONE,rdv.getPhone());
        contentValues.put(ADDRESS,rdv.getAddress());
        contentValues.put(ISDONE,rdv.getIsDone());
        database.insert(TABLE_NAME,null,contentValues);
    }


    public Cursor getAllRDVs(){
        String[] projection = {_ID,TITLE,DATE,TIME,CONTACT,PHONE,ADDRESS,ISDONE};
        Cursor cursor = database.query(TABLE_NAME, projection, null, null, null, null, null, null);

        return cursor;
    }


    public int update(RDV rdv) {
        Long _id= rdv.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, rdv.getTitle());
        contentValues.put(DATE, rdv.getDate());
        contentValues.put(TIME, rdv.getTime());
        contentValues.put(CONTACT, rdv.getContact());
        contentValues.put(PHONE, rdv.getPhone());
        contentValues.put(ADDRESS, rdv.getAddress());
        contentValues.put(ISDONE, rdv.getIsDone());
        int count = database.update(TABLE_NAME, contentValues, this._ID + " = " + _id, null);
        return count;
    }

    public void delete(long _id)
    {
        Log.d("Test", "delete");
        database.delete(TABLE_NAME, _ID + "=" + _id, null);

    }

}