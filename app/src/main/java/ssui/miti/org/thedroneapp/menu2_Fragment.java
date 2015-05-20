package ssui.miti.org.thedroneapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ssui.miti.org.thedroneapp.R;


public class menu2_Fragment extends Fragment {
    private static final String TAG = "MainActivity";

    Button b;

    View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        FragmentActivity faActivity  = (FragmentActivity)    super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load

        rootview = inflater.inflate(R.layout.menu2_layout,container, false);


        int RGB = android.graphics.Color.rgb(0,181,236);
        rootview.setBackgroundColor(RGB);
       // RelativeLayout llLayout    = (RelativeLayout)    inflater.inflate(R.layout.menu2_layout, container, false);

        b = (Button)rootview.findViewById(R.id.my_button);


       /* new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    socket = new Socket(serverIPAdress, serverPort);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();*/
        // Of course you will want to faActivity and llLayout in the class and not this method to access them in the rest of
        // the class, just initialize them here

        // Content of previous onCreate() here
        // ...

        // Don't use this method, it's handled by inflater.inflate() above :
        // setContentView(R.layout.activity_layout);

        // The FragmentActivity doesn't contain the layout directly so we must use our instance of     LinearLayout :
        //llLayout.findViewById(R.id.someGuiElement);
        // Instead of :
        // findViewById(R.id.someGuiElement);
        return rootview; // We must return the loaded Layout


    }

    public void onTestVideoStreamClick(View v){
        Intent i = new Intent(super.getActivity(), VideoViewActivity.class);
        startActivityForResult(i,0);
    }



   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
