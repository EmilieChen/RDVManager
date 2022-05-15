

package com.example.rdvmanager;



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    MediaPlayer player;
    AudioManager manager;

    private MusicService myService;
    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((MusicService.MyActivityBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
        }
    };



    public void startMusic() {
        manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (!manager.isMusicActive()) {
            Intent serviceIntent = new Intent(getApplicationContext(), MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, myServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMusic();
    }

    public void stopMusic() {
        manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (manager.isMusicActive()) {
            stopService(new Intent(this, MusicService.class));
            unbindService(myServiceConnection);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }

    public void playMusic(View v) {

        CheckBox box = (CheckBox) findViewById(R.id.cbPlayMusic);
        if (box.isChecked()) {
            startMusic();
        } else {
            stopMusic();
        }

    }


    SharedPreferences app_preferences;

    SharedPreferences.Editor editor;

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

        setContentView(R.layout.activity_settings);

    }


    public void setColor(View v) {

        editor = app_preferences.edit();
        Intent intent = new Intent(this, Settings.class);
        editor.clear();
        switch (v.getId()) {

            case (R.id.btnBlue): {
                editor.putString("currentTheme", "blue");
                break;
            }

            case (R.id.btnOrange): {
                editor.putString("currentTheme", "orange");
                break;
            }
            case (R.id.btnGreen): {
                editor.putString("currentTheme", "green");
                break;
            }
            case (R.id.btnRed): {
                editor.putString("currentTheme", "red");
                break;
            }
            case (R.id.btnTeal): {
                editor.putString("currentTheme", "teal");
                break;
            }
        }
        editor.apply();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.returnMainActivity: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


