package ca.cheems.iotproject;

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

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button pm25, tvoc, eCO2,O3;
    ImageView face, temp_pic;
    TextView temperature, humid,temp_ban, humid_ban, location;
    double temp, humd, voc, pm, co2,o3;
    LinearLayout top;
    String[] city = {"Select State","Vancouver","Toronto","Ottawa","Calgary","Montreal"};
    Spinner spinner;
    String tokens = "335c6bfed754d30c7a80d76cd33f15a76c0f15c1&fbclid=IwAR2RH7PBaTGKbNz5CBhMFL5743EHy9Hu4QifLGiXhkitCPZF86-5nZ150pc";
    String url ="https://api.waqi.info/feed/";
    RequestQueue requestQueue;
    private  String json="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       getID();
       buttonPress();

//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sensorvalue");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                temp = dataSnapshot.child("temp").getValue(double.class);
//                humd = dataSnapshot.child("humid").getValue(double.class);
//                voc = dataSnapshot.child("tvoc").getValue(double.class);
//                pm = dataSnapshot.child("pm25").getValue(double.class);
//                co2 = dataSnapshot.child("eco2").getValue(double.class);
//
//                aq_levels_indicators();
//                setTextOnScreen();
//                changeTempPic();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
        requestQueue = Volley.newRequestQueue(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,city);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    RequestWeather(spinner.getSelectedItem().toString());
                }else{
                    RequestWeather(city[2]);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void RequestWeather(String namcity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.waqi.info/feed/"+namcity.toLowerCase()+"/?token=335c6bfed754d30c7a80d76cd33f15a76c0f15c1&fbclid=IwAR2RH7PBaTGKbNz5CBhMFL5743EHy9Hu4QifLGiXhkitCPZF86-5nZ150pc", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String json_new =  jsonObject.get("data").toString();
                            JSONObject j1  = new JSONObject(json_new);
                            String j1_new = j1.get("iaqi").toString();
                            String j2_new = j1.get("forecast").toString();
                            JSONObject j_temp = new JSONObject(j1_new);
                            temp = j_temp.getJSONObject("t").getDouble("v");
                            humd =  j_temp.getJSONObject("h").getDouble("v");
                            voc = j_temp.getJSONObject("co").getDouble("v");
                            if(j_temp.has("pm25")){
                                pm = j_temp.getJSONObject("pm25").getDouble("v");
                            }else{
                                 pm =0;
                            }

                            co2 = j_temp.getJSONObject("no2").getDouble("v");
                            o3 = j_temp.getJSONObject("o3").getDouble("v");
                            json = j2_new;
                            Log.d("CHECKED",j2_new);
                            aq_levels_indicators();
                            setTextOnScreen();
                            changeTempPic();
                        } catch (JSONException e) {

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                Volley.newRequestQueue(MainActivity.this).add(stringRequest);
            }
        },1000);


    }


    public void buttonPress(){
        pm25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openPM25activity(json);
            }
        });

        O3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTVOCactivity(json);
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
        pm25.setText("PM2.5 \n" +String.valueOf(pm) + " μg/m3");
        eCO2.setText("eCO2 \n" +String.valueOf(co2) + " ppm");
        O3.setText("O3 \n" +String.valueOf(o3) + " ppm");
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


    public void openTVOCactivity(String json) {
        Intent intent = new Intent(this, O3Activity.class);
        intent.putExtra("JSON",json);
        startActivity(intent);
    }

    public void openPM25activity(String json){
        Intent intent = new Intent(this, PM25Activity.class);
        intent.putExtra("JSON",json);
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