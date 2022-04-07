package ca.cheems.iotproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button pm25, tvoc, eCO2,O3;
    ImageView face, temp_pic;
    TextView temperature, humid,temp_ban, humid_ban;
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
        Intent intent = new Intent(this, APIDataActivity.class);
        pm25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                    startActivity(intent);
            }
        });


    }

    public void changeTempPic(){
        if (temp <= 10){
            temp_pic.setImageResource(R.drawable.cold);
        }
        else if (temp > 10 && temp <= 20){
            temp_pic.setImageResource(R.drawable.hot_medium);
        }
        else{
            temp_pic.setImageResource(R.drawable.hot);
        }
    }

    public void getID(){
        pm25 = (Button) findViewById(R.id.citydata);
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
        tvoc.setText("tVOC \n"+ String.valueOf(voc) + " ppb");
        eCO2.setText("eCO2 \n" +String.valueOf(co2) + " ppm");
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