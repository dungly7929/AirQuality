package ca.cheems.iotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class APIDataActivity extends AppCompatActivity {

    Spinner spinner;
    RequestQueue requestQueue;
    String tokens = "335c6bfed754d30c7a80d76cd33f15a76c0f15c1&fbclid=IwAR2RH7PBaTGKbNz5CBhMFL5743EHy9Hu4QifLGiXhkitCPZF86-5nZ150pc";
    String[] city = {"Toronto","Vancouver","Ottawa","Calgary","Montreal"};
    double temp, humd, voc, pm, co2,o3;
    String url ="https://api.waqi.info/feed/";
    private  String json="";
    private SharedPreferences sharedPreferences;
    TextView api_temp, api_humd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apidata);

        api_temp = findViewById(R.id.api_temp);
        api_humd = findViewById(R.id.api_humd);

        spinner = (Spinner) findViewById(R.id.spiner);
        requestQueue = Volley.newRequestQueue(this);
        try {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.my_dropdown_item, city);
            spinner.setAdapter(arrayAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        RequestWeather(spinner.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        catch (Exception e)
        {

        }
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

    private void RequestWeather(String namcity) {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.waqi.info/feed/" + namcity.toLowerCase() + "/?token=335c6bfed754d30c7a80d76cd33f15a76c0f15c1&fbclid=IwAR2RH7PBaTGKbNz5CBhMFL5743EHy9Hu4QifLGiXhkitCPZF86-5nZ150pc", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String json_new = jsonObject.get("data").toString();
                            JSONObject j1 = new JSONObject(json_new);
                            String j1_new = j1.get("iaqi").toString();
                            String j2_new = j1.get("forecast").toString();
                            JSONObject j_temp = new JSONObject(j1_new);


                            temp = j_temp.getJSONObject("t").getDouble("v");
                            api_temp.setText(String.valueOf(temp) + " °C");
                            humd = j_temp.getJSONObject("h").getDouble("v");
                            api_humd.setText(String.valueOf(humd)+ " %");

                            voc = j_temp.getJSONObject("co").getDouble("v");
                            if (j_temp.has("pm25")) {
                                pm = j_temp.getJSONObject("pm25").getDouble("v");
                            } else {
                                pm = 0;
                            }

                            co2 = j_temp.getJSONObject("no2").getDouble("v");
                            o3 = j_temp.getJSONObject("o3").getDouble("v");
                            json = j2_new;
                            Log.d("CHECKED", j2_new);
                        } catch (JSONException e) {

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                Volley.newRequestQueue(APIDataActivity.this).add(stringRequest);
            }
        }, 1000);


    }
}