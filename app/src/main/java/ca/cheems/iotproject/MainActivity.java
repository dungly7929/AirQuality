package ca.cheems.iotproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    ImageView face;
    TextView temperature, humid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pm25 = (Button) findViewById(R.id.pm25button);
        tvoc = (Button) findViewById(R.id.tvocbutton);
        eCO2 = (Button) findViewById(R.id.eCO2button);

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

        //The face imageview will change according to the level of
        face = findViewById(R.id.face_indicator);

        temperature = findViewById(R.id.aq_temp);
        humid = findViewById(R.id.aq_humd);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sensorvalue");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double temp = dataSnapshot.child("temp").getValue(double.class);
                temperature.setText(String.valueOf(temp) +" °C");
                double humd = dataSnapshot.child("humid").getValue(double.class);
                humid.setText(String.valueOf(humd) + " %");
                double voc = dataSnapshot.child("tvoc").getValue(double.class);
                tvoc.setText(String.valueOf(voc));
                double pm = dataSnapshot.child("pm25").getValue(double.class);
                pm25.setText(String.valueOf(pm));
                double co2 = dataSnapshot.child("eco2").getValue(double.class);
                eCO2.setText(String.valueOf(co2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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