package ca.cheems.iotproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button pm25, tvoc, eCO2;
    ImageView face;

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

        face = findViewById(R.id.face_indicator);
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