package com.example.rdvmanager;
import android.content.Context;
import android.database.Cursor;
import androidx.loader.content.CursorLoader;
public class MyLoader extends CursorLoader {
    DatabaseHelper myHelper;
    public MyLoader(Context context, DatabaseHelper helper) {
        super(context);
        myHelper=helper;
    }
    @Override
    public Cursor loadInBackground() {
        return myHelper.getAllRDVs();
    }
}
