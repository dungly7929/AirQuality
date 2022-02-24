package ca.cheems.iotproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button pm25, tvoc, eCO2;
    ImageView face, temp_pic;
    TextView temperature, humid,temp_ban, humid_ban, location;
    double temp, humd, voc, pm, co2;
    LinearLayout top;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       getID();
       buttonPress();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sensorvalue");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                temp = dataSnapshot.child("temp").getValue(double.class);
                humd = dataSnapshot.child("humid").getValue(double.class);
                voc = dataSnapshot.child("tvoc").getValue(double.class);
                pm = dataSnapshot.child("pm25").getValue(double.class);
                co2 = dataSnapshot.child("eco2").getValue(double.class);

                aq_levels_indicators();
                setTextOnScreen();
                changeTempPic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    public void buttonPress(){
        pm25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openPM25activity();
            }
        });

        tvoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTVOCactivity();
            }
        });

        eCO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openeCO2activity();
            }
        });
    }

    public void changeTempPic(){
        if (temp <= 10){
            temp_pic.setImageResource(R.drawable.cold);
        }
        else{
            temp_pic.setImageResource(R.drawable.hot);
        }
    }

    public void getID(){
        pm25 = (Button) findViewById(R.id.pm25button);
        tvoc = (Button) findViewById(R.id.tvocbutton);
        eCO2 = (Button) findViewById(R.id.eCO2button);
        temp_pic = (ImageView) findViewById(R.id.temp_pic);

        //The face imageview will change according to the level of
        face =(ImageView) findViewById(R.id.face_indicator);
        //temp and humidity
        temperature =(TextView) findViewById(R.id.aq_temp);
        humid = (TextView)  findViewById(R.id.aq_humd);
        top = (LinearLayout) findViewById(R.id.aqilayout);
        temp_ban = (TextView) findViewById(R.id.aq_temp_banner);
        humid_ban = (TextView) findViewById(R.id.aq_humd_banner);

    }

    public void setTextOnScreen(){
        temperature.setText(String.valueOf(temp) +" °C");
        humid.setText(String.valueOf(humd) + " %");
        tvoc.setText("tVOC = "+ String.valueOf(voc) + " ppb");
        pm25.setText("PM2.5 = " +String.valueOf(pm) + " μg/m3");
        eCO2.setText("eCO2 = " +String.valueOf(co2) + " ppm");
    }

    public void aq_levels_indicators(){
        //Change face due to the air quality condition
                if (voc <= 250){
                   happy();
                }else if(voc > 250 && voc <= 2000){
                    worry();
                }else{
                    angry();
                }
    }

    public void happy(){
        face.setImageResource(R.drawable.happy);
        top.setBackground(getDrawable(R.drawable.background_top_good));
        temp_ban.setTextColor(Color.parseColor("#000000"));
        humid_ban.setTextColor(Color.parseColor("#000000"));
    }

    public void worry(){
        face.setImageResource(R.drawable.worry);
        top.setBackground(getDrawable(R.drawable.background_top_medium));
        temp_ban.setTextColor(Color.parseColor("#E6EDf5"));
        humid_ban.setTextColor(Color.parseColor("#E6EDf5"));
    }

    public void angry(){
        face.setImageResource(R.drawable.angry);
        top.setBackground(getDrawable(R.drawable.background_top_bad));
        temp_ban.setTextColor(Color.parseColor("#FFFFFF"));
        humid_ban.setTextColor(Color.parseColor("#FFFFFF"));
    }

    public void openeCO2activity(){
        Intent intent = new Intent(this, eCO2Activity.class);
        startActivity(intent);

    }

    public void openTVOCactivity() {
        Intent intent = new Intent(this, tvocActivity.class);
        startActivity(intent);
    }

    public void openPM25activity(){
        Intent intent = new Intent(this, pm25Activity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setMessage("Do you really want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                    .create()
                    .show();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    //Back button pressed
    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setMessage("Do you really want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }
}