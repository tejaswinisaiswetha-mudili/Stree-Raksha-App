package com.example.streerakshaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    Button b1, b2, b3;
    private FusedLocationProviderClient client;
    DatabaseHandler myDB;
    private final int REQUEST_CHECK_CODE = 8990;
    private LocationSettingsRequest.Builder builder;
    String x = "", y = "";
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    SmsManager smsManager;
    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        b1 = findViewById(R.id.button);
        b3 = findViewById(R.id.button2);
        b2 = findViewById(R.id.button3);
        myDB = new DatabaseHandler(this);
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS();
        } else {
            startTrack();
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });
        b2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mp.start();
                Toast.makeText(getApplicationContext(), "PANIC BUTTON STARTED", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                loadData();
            }
        });
    }

    private void loadData() {
        ArrayList<String> thelist = new ArrayList<>();
        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) {
            Toast.makeText(this, "no content to show", Toast.LENGTH_SHORT).show();
        } else {
            call();
            String number = " ";
            while (data.moveToNext()) {
                thelist.add(data.getString(1));
                number = number + data.getString(1) + (data.isLast() ? "" : ";");
                if (!thelist.isEmpty()) {
                    sendSms();
                }
            }
        }
    }

    public void sendSms() {
        String msg = "I NEED HELP LATITUDE: " + x + "LONGITUDE: " + y;
        SmsManager smsManager = SmsManager.getDefault();
        StringBuffer smsBody = new StringBuffer();
        smsBody.append(Uri.parse(msg));
        ArrayList<String> thelist = new ArrayList<>();
        Cursor data = myDB.getListContents();
        while (data.moveToNext()) {
            String number =" ";
            thelist.add(data.getString(1));
            number = number + data.getString(1) + (data.isLast() ? "" : ";");
            smsManager.sendTextMessage(number, null, smsBody.toString(), null, null);
        }
        Toast.makeText(MainActivity2.this, "message sent", Toast.LENGTH_SHORT).show();
    }

    private void call() {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel: 1000"));
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(i);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }
    }


    public void startTrack() {
        if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double lon = locationGPS.getLongitude();
                x = String.valueOf(lat);
                y = String.valueOf(lon);
                Toast.makeText(MainActivity2.this,"location started",Toast.LENGTH_SHORT).show();
            }
            // else{
            //     Toast.makeText(this, "UNABLE TO FIND LOCATION", Toast.LENGTH_SHORT).show();
            //  }
        }
    }
    

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage("Enable GPS" ).setCancelable( false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity( new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS ) );
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final  AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}