package ssui.miti.org.thedroneapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import ssui.miti.org.thedroneapp.mjpeg.MjpegInputStream;
import ssui.miti.org.thedroneapp.mjpeg.MjpegView;


public class VideoViewActivity extends ActionBarActivity {

    private static final String TAG = "Video View" ;
    MjpegView videoView;
    ProgressDialog pDialog;
    String videoURL = "http://webcam.st-malo.com/axis-cgi/mjpg/video.cgi?resolution=352x288";  //<-  URL FOR THE DRONE'S MJPEG STREAM


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoURL = "http://192.168.1.3:3002/nodecopter.mjpeg";


        MjpegInputStream mjp = new MjpegInputStream(null);
        mjp = mjp.read(videoURL);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int theColor = sharedPreferences.getInt("theColor", 0) ;

        System.out.println("did i get it " + theColor);

        Log.i(TAG, "stream created");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        videoView = new MjpegView(this, theColor);
        setContentView(videoView);
        //int RGB = android.graphics.Color.rgb(0,181,236);
        //videoView.setBackgroundColor(RGB);
        videoView.setSource(mjp);
        videoView.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        videoView.startPlayback();
        videoView.showFps(true);

    }


    public void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }

    public void sendCommand(String comm){
        Intent data = new Intent();
        data.putExtra("returnKey1", comm);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }
}
