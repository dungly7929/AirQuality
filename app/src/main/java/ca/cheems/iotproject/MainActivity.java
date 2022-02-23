package ca.cheems.iotproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button pm25, tvoc, eCO2;
    ImageView face, temp_pic;
    TextView temperature, humid;
    double temp, humd, voc, pm, co2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       getID();

        pm25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPM25activity();
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
        face = findViewById(R.id.face_indicator);
        temperature = findViewById(R.id.aq_temp);
        humid = findViewById(R.id.aq_humd);
    }

    public void setTextOnScreen(){
        temperature.setText(String.valueOf(temp) +" °C");
        humid.setText(String.valueOf(humd) + " %");
        tvoc.setText(String.valueOf(voc) + " ppb");
        pm25.setText(String.valueOf(pm) + " μg/m3");
        eCO2.setText(String.valueOf(co2) + " ppm");
    }

    public void aq_levels_indicators(){
        //Change face due to the air quality condition
                if (voc <= 250){
                    face.setImageResource(R.drawable.happy);
                }else if(voc > 250 && voc <= 2000){
                    face.setImageResource(R.drawable.worry);
                }else{
                    face.setImageResource(R.drawable.angry);
                }

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