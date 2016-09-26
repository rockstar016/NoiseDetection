package srecord.rock.com.record;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetectService extends Service {
    SoundDetection detection;
    private static int mThreshold;
    private Handler mHandler = new Handler();
    private static final int POLL_INTERVAL = 500;
    public static DetectService _this;
    public  SoundRecorder recorder;
    private static boolean is_recording = false, is_exited = false;
    CountDownTimer countDowntimer;

    private Runnable mPollTask = new Runnable() {
        public void run() {

            if(is_recording == false) {
                double amp = detection.getAmplitude();
                if ((amp > mThreshold)) {
                    callForHelp();
                }
                mHandler.postDelayed(mPollTask, POLL_INTERVAL);
            }
        }
    };

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            detectorStart();
        }
    };

    public void detectorStart(){
        try {
            if(recorder != null)
                recorder.stop();
            detection = new SoundDetection();
            detection.start();
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }catch (Exception e){
            Log.d("service: ","detectorStart");
        }
    }

    public void detectorStop(){
        try {
            mHandler.removeCallbacks(mSleepTask);
            mHandler.removeCallbacks(mPollTask);
            detection.stop();
        }
        catch (Exception e){
            Log.d("service: ","detectorStop");
        }
    }

    public void callForHelp() {
        Toast.makeText(DetectService.this, "Record Started", Toast.LENGTH_SHORT).show();
        is_recording = true;
        try {
            detectorStop();
        }catch(Exception e){
            Log.d("detect:","Detector stop exception");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        String file_name = df.format(new Date());
        File audioFile = new File("/test/"+file_name+".mp3");
        recorder = new SoundRecorder(audioFile.getAbsolutePath());
        try {
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        countDowntimer = new CountDownTimer(20000, 2000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Toast.makeText(DetectService.this, "finish record", Toast.LENGTH_SHORT).show();
                is_recording = false;
                mHandler.post(mSleepTask);
            }
        };
        countDowntimer.start();
    }

    public static DetectService getInstance(){
        return _this;
    }

    public DetectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        _this = this;
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        mHandler.post(mSleepTask);
        mThreshold  = intent.getIntExtra("value",1);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _this = null;
        Toast.makeText(DetectService.this, "My service destoryed", Toast.LENGTH_SHORT).show();
        if(is_recording == false) {
            detectorStop();
            if(recorder != null)
                recorder.stop();
        }
        else{
            if(recorder != null)
                recorder.stop();
            if(countDowntimer != null)
                countDowntimer.cancel();
            is_recording = false;
        }
    }
    public void setmThreshold(int threshold){
        mThreshold = threshold;
    }
}
