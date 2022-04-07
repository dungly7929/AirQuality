package ca.cheems.iotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class APIDataActivity extends AppCompatActivity {

    Spinner spinner;
    RequestQueue requestQueue;
    String tokens = "335c6bfed754d30c7a80d76cd33f15a76c0f15c1&fbclid=IwAR2RH7PBaTGKbNz5CBhMFL5743EHy9Hu4QifLGiXhkitCPZF86-5nZ150pc";
    String[] city = {"Toronto", "Vancouver", "Ottawa", "Calgary", "Montreal"};
    double temp, humd, voc, pm, co2, o3;
    String url = "https://api.waqi.info/feed/";
    private String json = "";
    private SharedPreferences sharedPreferences;
    TextView api_temp, api_humd, api_showData;
    RadioGroup api_radioGroup;

    BarChart barChart;
    BarDataSet barDataSet;
    BarData barData;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<ForcastDaily> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apidata);

        api_temp = findViewById(R.id.api_temp);
        api_humd = findViewById(R.id.api_humd);
        api_radioGroup = findViewById(R.id.api_radiogroup);
        RadioButton checkedRadioButton = (RadioButton) api_radioGroup.findViewById(api_radioGroup.getCheckedRadioButtonId());
        api_showData = findViewById(R.id.current_data);
        barChart = findViewById(R.id.barchart);
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
        } catch (Exception e) {

        }
    }


    private void RequestWeather(String namcity) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.waqi.info/feed/" + namcity.toLowerCase() + "/?token=335c6bfed754d30c7a80d76cd33f15a76c0f15c1&fbclid=IwAR2RH7PBaTGKbNz5CBhMFL5743EHy9Hu4QifLGiXhkitCPZF86-5nZ150pc",
                new Response.Listener<String>() {
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
                            api_humd.setText(String.valueOf(humd) + " %");

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

                            try {
                                RadioButton radcheck = (RadioButton) api_radioGroup.findViewById(api_radioGroup.getCheckedRadioButtonId());
                                if (radcheck.isChecked()) {
                                    if (radcheck.getText().equals("tVOC")) {
                                        api_showData.setText("tVOC = " + String.valueOf(voc) + " ppb");
                                    } else if (radcheck.getText().equals("O3")) {
                                        api_showData.setText("O3 level = " + String.valueOf(o3) + " mmol/m2");
                                    }
                                }
                            } catch (Exception e) {

                            }

                            api_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    switch (checkedId) {
                                        case R.id.api_tvocradio:
                                            api_showData.setText("tVOC = " + String.valueOf(voc) + " ppb");
                                            break;
                                        case R.id.api_o3radio:
                                            api_showData.setText("O3 level = " + String.valueOf(o3) + " mmol/m2");
                                            buildgraph("o3");
                                            break;
                                        case R.id.api_pm25radio:
                                            api_showData.setText("PM 2.5 level = " + String.valueOf(pm) + " μg/m3");
                                            buildgraph("pm25");
                                            break;
                                        case R.id.api_co2radio:
                                            api_showData.setText("eCO2 level = " + String.valueOf(co2) + " ppm");
                                            break;
                                        default:
                                            api_showData.setText("Please select data above to view current Air quality");
                                    }
                                }
                            });

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

    public void buildgraph(String graphName) {

        arrayList = new ArrayList<>();
        barEntryArrayList = new ArrayList<>();
        try {
            JSONObject j_daily = new JSONObject(json);
            JSONObject j_all = new JSONObject(j_daily.get("daily").toString());
            JSONArray jsonArray = new JSONArray(j_all.get(graphName).toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject_chart = jsonArray.getJSONObject(i);
                arrayList.add(new ForcastDaily(jsonObject_chart.getInt("avg"), jsonObject_chart.getString("day")));
            }
            for (int i = 0; i < arrayList.size(); i++) {
                ForcastDaily daily = arrayList.get(i);
                barEntryArrayList.add(new BarEntry((i + 1), daily.avg));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        barDataSet = new BarDataSet(barEntryArrayList, "Compare " + graphName + " in " + arrayList.size() + " days");
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barData.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
    }


}