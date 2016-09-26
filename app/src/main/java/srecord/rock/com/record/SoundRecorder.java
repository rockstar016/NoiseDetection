package srecord.rock.com.record;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by RockStar0116 on 2016.09.19.
 */
public class SoundRecorder {
    MediaRecorder recorder = new MediaRecorder();
    final String path;

    public SoundRecorder(String path) {
        this.path = sanitizePath(path);
    }

    private String sanitizePath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.contains(".")) {
            path += ".mp3";
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
    }

    public void start() throws IOException {
        String state = android.os.Environment.getExternalStorageState();
        if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
            throw new IOException("SD Card is not mounted.  It is " + state + ".");
        }

        // make sure the directory we plan to store the recording in exists
        File directory = new File(path).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Path to file could not be created.");
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(path);
        recorder.prepare();
        recorder.start();
    }

    public void stop(){

        try {
            if(recorder == null){
                Log.d("recorder:","null");
            }
            else {
                recorder.stop();
                recorder.release();
            }
        }catch (Exception e){
            Log.d("Recorder:","Stop");
        }
    }
}
