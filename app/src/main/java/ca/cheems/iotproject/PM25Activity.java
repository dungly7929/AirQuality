package ca.cheems.iotproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PM25Activity extends AppCompatActivity {
    Toolbar toolbar;
    BarChart barChart;
    BarDataSet barDataSet;
    BarData barData;
    ArrayList<BarEntry> barEntryArrayList;

    String json = "";
   ArrayList<ForcastDaily> arrayList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvoc);
        toolbar  = findViewById(R.id.toolbar);
        barChart = findViewById(R.id.bartchart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PM25");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        json = intent.getStringExtra("JSON");
        arrayList = new ArrayList<>();
        barEntryArrayList = new ArrayList<>();


        try {
             JSONObject j_daily = new JSONObject(json);
             JSONObject j_o3 = new JSONObject(j_daily.get("daily").toString());
             JSONArray jsonArray = new JSONArray(j_o3.get("pm25").toString());
              for(int i = 0  ;i <jsonArray.length() ; i++){
                  JSONObject jsonObject =  jsonArray.getJSONObject(i);
                  arrayList.add(new ForcastDaily(jsonObject.getInt("avg"),jsonObject.getString("day")));
              }
              for(int i = 0 ; i <arrayList.size();i++){
                  ForcastDaily daily = arrayList.get(i);
                  barEntryArrayList.add(new BarEntry((i+1),daily.avg));

              }



        } catch (Exception e) {
            e.printStackTrace();
        }



        barDataSet = new BarDataSet(barEntryArrayList,"Compare PM25 in "+arrayList.size()+" days");
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barData.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);





    }
}