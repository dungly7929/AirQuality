package ca.cheems.iotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    Button pm25, tvoc, eCO2;

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
}