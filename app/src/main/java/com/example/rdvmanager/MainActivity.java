package com.example.rdvmanager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
LoaderManager.LoaderCallbacks<Cursor> {

    private DatabaseHelper myHelper;
    ListView lvRDVs;
    SimpleCursorAdapter adapter;
    public static final int LOADER_ID =1976;


    private static final int REQUEST_PHONE_CALL = 2;


    String phone;
    String address;

    SharedPreferences app_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app_preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        String theme = app_preferences.getString("currentTheme", "blue");


        switch (theme) {

            case "orange": {
                getTheme().applyStyle(R.style.orangeTheme_RDVManager, true);
                break;
            }
            case "blue": {
                getTheme().applyStyle(R.style.Theme_RDVManager, true);
                break;
            }
            case "green": {
                getTheme().applyStyle(R.style.greenTheme_RDVManager, true);
                break;
            }
            case "red": {
                getTheme().applyStyle(R.style.redTheme_RDVManager, true);
                break;
            }
            case "teal": {
                getTheme().applyStyle(R.style.tealTheme_RDVManager, true);
                break;
            }
        }

        setContentView(R.layout.activity_main);

        myHelper = new DatabaseHelper(this);
        myHelper.open();

        lvRDVs = (ListView) findViewById(R.id.lvRDVs);


        lvRDVs.setEmptyView(findViewById(R.id.tvEmpty));

        chargeData();

        registerForContextMenu(lvRDVs);

        lvRDVs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.d("Test", "Listener");

                String idItem = ((TextView)view.findViewById(R.id.tvId)).getText().toString();
                String titleItem = ((TextView)view.findViewById(R.id.tvTitle)).getText().toString();
                String dateItem = ((TextView)view.findViewById(R.id.tvDate)).getText().toString();
                String timeItem = ((TextView)view.findViewById(R.id.tvTime)).getText().toString();
                String contactItem = ((TextView)view.findViewById(R.id.tvContact)).getText().toString();
                String phoneItem = ((TextView)view.findViewById(R.id.tvPhone)).getText().toString();
                String addressItem = ((TextView)view.findViewById(R.id.tvAddress)).getText().toString();
                int isDoneItem;
                if(((CheckBox)view.findViewById(R.id.cbIsDone)).isChecked()){ isDoneItem = 1; }
                else{ isDoneItem = 0; }


                RDV pRdv = new RDV(Long.parseLong(idItem), titleItem, dateItem, timeItem, contactItem, phoneItem, addressItem, isDoneItem);
                Intent intent = new Intent(getApplicationContext(), RDVDetails.class);
                intent.putExtra("SelectedRDV", pRdv);

                intent.putExtra("fromAdd", false);
                startActivity(intent);



            }


        });




    }



    public void chargeData() {

        Log.d("Test", "chargeData");
        final String[] from = new String[]{DatabaseHelper._ID,
                DatabaseHelper.TITLE, DatabaseHelper.DATE, DatabaseHelper.TIME, DatabaseHelper.CONTACT, DatabaseHelper.PHONE, DatabaseHelper.ADDRESS, DatabaseHelper.ISDONE};
        final int[] to = new int[]{R.id.tvId, R.id.tvTitle, R.id.tvDate, R.id.tvTime, R.id.tvContact, R.id.tvPhone, R.id.tvAddress, R.id.cbIsDone};


        //Cursor c = myHelper.getAllRDVs();
        //SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.rdv_item_view, c, from, to, 0);
        adapter = new SimpleCursorAdapter(this, R.layout.rdv_item_view, null, from, to, 0);

        adapter.notifyDataSetChanged();
        lvRDVs.setAdapter(adapter);


        LoaderManager.getInstance(this).initLoader(LOADER_ID,null,this);


    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new MyLoader(this,myHelper);
    }
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rdv_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu,menu);

        phone = ((TextView)v.findViewById(R.id.tvPhone)).getText().toString();
        address = ((TextView)v.findViewById(R.id.tvAddress)).getText().toString();


    }


    public void shareMethod() {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Meeting");
        sendIntent.setType("text/plain");
        //startActivity(sendIntent);
        startActivity(Intent.createChooser(sendIntent, "Share App"));
    }

    public void launchMaps() {
        String map = "http://maps.google.co.in/maps?q=" + address ;
        Uri gmmIntentUri = Uri.parse(map);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        startActivity(mapIntent);

    }


    public void callPhoneNumber(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_PHONE_CALL);
        } else{
            Log.d("Test", "call");
            //Intent intent = new Intent(this, RDVDetails.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+phone));
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {


            case REQUEST_PHONE_CALL:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+phone));
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }




    public void onClickCheckBox(View v){
        CheckBox cbIsDone = (CheckBox) findViewById(R.id.cbIsDone);
        if (cbIsDone.isChecked())
            Toast.makeText(this, "Rdv done", Toast.LENGTH_SHORT).show();
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        if (item.getItemId()==R.id.share){
            shareMethod();
            return true;
        }
        if (item.getItemId()==R.id.launchMaps){
            launchMaps();
            return true;
        }

        if (item.getItemId()==R.id.callPhoneNumber){
            callPhoneNumber();
            return true;
        }

        if (item.getItemId()==R.id.delete){
            myHelper.delete(info.id);

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            //chargeData();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_rdv: {
                Intent intent = new Intent(this, RDVDetails.class);
                intent.putExtra("fromAdd",true);

                startActivity(intent);
                return true;
            }
            case R.id.search: {
                Toast.makeText(this, "Search", Toast.LENGTH_LONG).show();
                return true;
            }
            case R.id.settings: {
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
