package srecord.rock.com.record;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl{
    ListView listview;
    List<String> myList;
    File file;
    RadioButton play_radio, rename_radio, delete_radio;
    int current_radio_button = 0;
    MediaController controller;
    MediaPlayer player;
    private Handler handler = new Handler();
    ArrayAdapter<String> adapter;
    String current_file_path = new String();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        listview = (ListView)findViewById(R.id.listView);
        play_radio = (RadioButton)findViewById(R.id.radio_play);
        rename_radio = (RadioButton)findViewById(R.id.radio_rename);
        delete_radio = (RadioButton)findViewById(R.id.radio_delete);
        play_radio.setOnCheckedChangeListener(this);
        rename_radio.setOnCheckedChangeListener(this);
        delete_radio.setOnCheckedChangeListener(this);
        initAdapter();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long Id) {
                switch (current_radio_button) {
                    case 0:
                        playItem(adapter.getItem(position));
                        break;
                    case 1:
                        renameItem(adapter.getItem(position));
                        break;
                    case 2:
                        current_file_path = adapter.getItem(position);
                        deleteItem(adapter.getItem(position));
                        break;
                    default:
                        break;
                }
            }
        });




    }
    private void initAdapter(){
        myList = new ArrayList<String>();
        file = new File( Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/test/" );
        boolean tmp_value = file.exists();
        File list[] = file.listFiles();
        if(list != null) {
            for (int i = 0; i < list.length; i++) {
                if (checkExtension(list[i].getName())) {
                    myList.add(list[i].getName());
                }
            }
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, myList);
        listview.setAdapter(adapter); //Set all the file in the list.
    }
    private void playItem(String item) {
        if(player == null) {
            player = new MediaPlayer();
        }
        else{
            player.stop();
            player.release();
            player = new MediaPlayer();
        }
        player.setOnPreparedListener(this);
        controller = new MediaController(this);
        try {
            player.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/" + item);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void renameItem(String item) {
        RenameDialog dialog = new RenameDialog(this, item);
        dialog.show();
    }
    private void deleteItem(String item) {
        AlertDialog.Builder ab = new AlertDialog.Builder(ShowActivity.this);
        ab.setMessage("Are you sure to delete this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    File temp_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/" + current_file_path);
                    if(temp_file.exists()){
                        temp_file.delete();
                    }
                    initAdapter();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked

                    break;
            }
        }
    };




    private boolean checkExtension( String fileName ) {
        String ext = getFileExtension(fileName);
        if ( ext == null) return false;
        try {
            if ( ext.toUpperCase().equals("MP3") == true ) {
                return true;
            }
        } catch(IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    public String getFileExtension( String fileName ) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        } else
            return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(controller != null)
            controller.hide();
        if(player != null) {
            player.stop();
            player.release();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        if(controller != null)
        controller.show();
        return false;
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if(b == true) {
            switch (id) {
                case R.id.radio_play:
                    current_radio_button = 0;
                    break;
                case R.id.radio_rename:
                    current_radio_button = 1;
                    break;
                case R.id.radio_delete:
                    current_radio_button = 2;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.main_view));
        handler.post(new Runnable() {
            public void run() {
                controller.setEnabled(true);
                controller.show();
            }
        });
    }
    public void refreshList(){
        initAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
