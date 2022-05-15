package com.example.rdvmanager;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.Calendar;

public class RDVDetails extends AppCompatActivity{

    EditText etTitle, etDate, etTime, etContact, etPhone, etAddress;
    CheckBox cbIsDone;
    TextView tvId;

    boolean fromAdd;
    private DatabaseHelper myHelper;

    int year, month, day, hours, minutes;

    SharedPreferences app_preferences;

    private static final int CONTACT_PERMISSION_CODE = 1;
    private static final int REQUEST_PHONE_CALL = 2;


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri contactData= data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        etContact.setText(name);
                    }
                }
            });



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


        setContentView(R.layout.activity_rdv_details);

        tvId = (TextView) findViewById(R.id.tvId);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        etContact = (EditText) findViewById(R.id.etContact);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etAddress = (EditText) findViewById(R.id.etAddress);
        cbIsDone = (CheckBox) findViewById(R.id.cbIsDone);

        myHelper = new DatabaseHelper(this);
        myHelper.open();

        Intent intent = getIntent();
        fromAdd = intent.getBooleanExtra("fromAdd", false);

        if (!fromAdd) {
            Bundle b = intent.getExtras();
            RDV selectedRDV = b.getParcelable("SelectedRDV");
            tvId.setText(String.valueOf(selectedRDV.getId()));
            etTitle.setText(selectedRDV.getTitle());
            etDate.setText(selectedRDV.getDate());
            etTime.setText(selectedRDV.getTime());
            etContact.setText(selectedRDV.getContact());
            etPhone.setText(selectedRDV.getPhone());
            etAddress.setText(selectedRDV.getAddress());
            if (selectedRDV.getIsDone() == 1) {
                cbIsDone.setChecked(true);
            } else {
                cbIsDone.setChecked(false);
            }

        }


    }



    public void pickContact(View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    CONTACT_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            //startActivityForResult(intent, CONTACT_PICK_CODE);
            someActivityResultLauncher.launch(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case CONTACT_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    someActivityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }

            case REQUEST_PHONE_CALL:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+etPhone));
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void onCancelClick(View v){
        //Intent intent = new Intent(this,MainActivity.class);
        //startActivity(intent);
        finish();
    }

    public void saveRDV(View view){
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        String contact = etContact.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();
        int isDone;
        if (cbIsDone.isChecked()) { isDone = 1;}
        else { isDone = 0; }


        if(fromAdd) {
            RDV rdv = new RDV (title, date, time, contact, phone, address, isDone);
            myHelper.add(rdv);

            Intent main = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
        else {
            Long id = Long.parseLong(tvId.getText().toString()); //Long id = Long.parseLong(tvId.getText().toString());
            RDV rdv = new RDV(id,title, date, time, contact, phone, address, isDone);
            int n = myHelper.update(rdv);

            Intent main = new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
    }


    public void pickDate(View view) {
        showDatePicker();
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            etDate.setText(String.format("%02d-%02d-%02d",day,month+1,year));
            /*
            etDate.setText(new StringBuilder().append(day).
                    append("-").append(month).append("-").append(year).append(" "));
        */
        }

    };


    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        date.setArguments(args);
        date.setCallBack(onDate);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    public void pickTime(View view){
        showTimePicker();
    }

    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            hours = hour;
            minutes = minute;
            etTime.setText(String.format("%02d:%02d",hours, minutes));
            //etTime.setText(new StringBuilder().append(hours).append(":").append(minutes));
        }
    };


    private void showTimePicker() {
        TimePickerFragment time= new TimePickerFragment();
        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        Bundle args = new Bundle();
        args.putInt("hours",hours);
        args.putInt("minutes",minutes);
        time.setArguments(args);
        time.setCallBack(onTime);

        time.show(getSupportFragmentManager(),"Time Picker");
    }
}
