package srecord.rock.com.record;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button start_button, stop_button, show_button;
    SeekBar seek_bar;
    public int current_seek_value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        120);
            } else {
                initializeView();
            }
        }
        else{
            initializeView();
        }
    }
    private void initializeView(){
        start_button = (Button) findViewById(R.id.start_button);
        stop_button = (Button) findViewById(R.id.stop_button);
        show_button = (Button) findViewById(R.id.show_button);
        start_button.setOnClickListener(this);
        stop_button.setOnClickListener(this);
        show_button.setOnClickListener(this);
        seek_bar = (SeekBar) findViewById(R.id.seekBar);
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                current_seek_value = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_seek_value = seekBar.getProgress() + 1;
                upgradeThreshold();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 120: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initializeView();
                } else {
                    Toast.makeText(this.getBaseContext(), "You must allow permission record audio to your mobile device.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            // Add additional cases for other permissions you may have asked for
        }
    }

    public void upgradeThreshold(){
        if(DetectService.getInstance() != null) {
            DetectService.getInstance().setmThreshold(current_seek_value);
        }
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.start_button) {
            startService();
        }
        else if(view.getId() == R.id.stop_button){
            stopService();
        }
        else if(view.getId() == R.id.show_button){
            gotoShowScreen();
        }
    }
    public void gotoShowScreen(){
        Intent i = new Intent(this, ShowActivity.class);
        startActivity(i);
        finish();
    }
    public void startService() {
        Toast.makeText(MainActivity.this, "start activity", Toast.LENGTH_SHORT).show();
        show_button.setEnabled(false);
        if(DetectService.getInstance() == null)
            startService(new Intent(getBaseContext(), DetectService.class).putExtra("value",current_seek_value));
        else
            Toast.makeText(MainActivity.this, "already running", Toast.LENGTH_SHORT).show();
    }
    public void stopService() {
        show_button.setEnabled(true);
        if(DetectService.getInstance() != null)
            stopService(new Intent(getBaseContext(), DetectService.class));
    }
}
