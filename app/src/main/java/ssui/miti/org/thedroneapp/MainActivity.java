package ssui.miti.org.thedroneapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ColorPicker.OnColorChangedListener {

    private static final String TAG = "MainActivity";

//    Button b;
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    String serverIPAdress = "192.168.1.3";
    int serverPort = 3001;
    byte[] response = new byte[256];
    Fragment firstFragment = null;
    Fragment secondFragment = null;
    //ImageView droneImage = null;
    //TextView theText = null;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

   // public TextView theText;
    //public ImageView droneImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setTitle("mantis");
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        this.activity = this;

        new AsyncTask() {
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
        }.execute();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Fragment objFragment = null;

        switch(position)
        {
            case 0:
                objFragment = new menu1_Fragment();
                firstFragment = objFragment;
                break;
            case 1:
                objFragment = new menu2_Fragment();
                break;
            case 2:
                objFragment = new menu3_Fragment();
                break;
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
    }

    Activity activity;

    public void getColor(View v) {
        new ColorPicker(activity, this, "", Color.BLACK, Color.WHITE).show();
    }

    @Override
    public void colorChanged(String str,int color) {

        /*this.getFragmentManager().findFragmentById(0).getView().findViewById(android.R.id.content)
        .setBackgroundColor(color);*/


        firstFragment.getView().setBackgroundColor(color);

        if (Color.red(color) + Color.green(color) + Color.blue(color) < 384)
        {
            ((menu1_Fragment)firstFragment).theText.setTextColor(Color.WHITE);
            ((menu1_Fragment)firstFragment).droneImage.setImageResource(R.drawable.dronewhite);
        }
        else
        {
            ((menu1_Fragment)firstFragment).theText.setTextColor(Color.BLACK);
            ((menu1_Fragment)firstFragment).droneImage.setImageResource(R.drawable.droneblack);
        }

        Intent i = new Intent(this, VideoViewActivity.class);
        startActivityForResult(i,0);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theColor", color);
        editor.commit();


        //Toast.makeText(this, "Chosen color: " + color, Toast.LENGTH_SHORT).show();
    }

    public void onTestVideoStreamClick(View v){
        Intent i = new Intent(this, VideoViewActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0) {
            if (data.hasExtra("returnKey1")) {


                Toast.makeText(this, "Colored detected at Region " + data.getExtras().getString("returnKey1"),
                        Toast.LENGTH_SHORT).show();


                // Region 1 (Top Left): Takeoff + Up + Stop + Left + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R1") == 0) {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();


                        }
                    }, 2000);

                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms

                            new CommandWorkerThread("[\"up\",[0.2],2]\n").start();
                        }
                    }, 3000);

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"left\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 8000);

                    final Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }
                else  // Region 2 (Top Front): Takeoff + Up + Stop + Front + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R2") == 0)
                {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();


                        }
                    }, 3000);

                    /*final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms

                            new CommandWorkerThread("[\"up\",[0.2],2]\n").start();
                        }
                    }, 4000);*/

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"front\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 5900);

                    final Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }
                else // Region 3 (Top Right): Takeoff + Up + Stop + Right + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R3") == 0)
                {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();


                        }
                    }, 2000);

                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms

                            new CommandWorkerThread("[\"up\",[0.2],2]\n").start();
                        }
                    }, 3000);

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"right\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 8000);

                    final Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }
                else // Region 4 (Middle Left): Takeoff + Stop + Left + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R4") == 0)
                {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();

                        }
                    }, 3000);

                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"left\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 8000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }
                else // Region 5 (Middle Front): Takeoff + Stop + Front + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R5") == 0)
                {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();

                        }
                    }, 3000);

                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"front\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 8000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }
                else // Region 6 (Middle Right): Takeoff + Stop + Right + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R6") == 0)
                {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();

                        }
                    }, 3000);

                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"right\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 8000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }
                else // Region 7 (Bottom Left): Takeoff + Down + Stop + Left + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R7") == 0)
                {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();


                        }
                    }, 2000);

                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms

                            new CommandWorkerThread("[\"down\",[0.2],2]\n").start();
                        }
                    }, 3000);

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"left\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 8000);

                    final Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }
                else // Region 8 (Bottom Front): Takeoff + Down + Stop + Front + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R8") == 0)
                {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();


                        }
                    }, 2000);

                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms

                            new CommandWorkerThread("[\"down\",[0.2],2]\n").start();
                        }
                    }, 3000);

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"front\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 8000);

                    final Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }
                else // Region 9 (Bottom Right): Takeoff + Down + Stop + Right + Stop + Land
                if(data.getExtras().getString("returnKey1").compareTo("R9") == 0)
                {
                    new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();


                        }
                    }, 2000);

                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms

                            new CommandWorkerThread("[\"down\",[0.2],2]\n").start();
                        }
                    }, 3000);

                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"right\",[0.2],2]\n").start();
                        }
                    }, 5000);

                    final Handler handler4 = new Handler();
                    handler4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"stop\",[],1]\n").start();
                        }
                    }, 8000);

                    final Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            new CommandWorkerThread("[\"land\",[],1]\n").start();
                        }
                    }, 10000);
                }

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (dataInputStream != null) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void onCommandClick(View v){
        new CommandWorkerThread("[\"takeoff\",[],1]\n").start();

        /*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                new CommandWorkerThread("[\"land\",[],1]\n").start();
            }
        }, 6000);
    */


    }

    public void doTakeoff(){
        System.out.println("Lo va a hacer!");
        new CommandWorkerThread("[\"takeoff\",[],1]\n[\"land\",[],1]\n").start();
        System.out.println("Lo hizo!");
    }

    public void onStopClick(View v){
        new CommandWorkerThread("[\"stop\",[],1]\n").start();
    }

    public void onDisableEmergency(View v){
        new CommandWorkerThread("[\"disableEmergency\",[],1]\n").start();
    }

    public void onLeftClick(View v){
        new CommandWorkerThread("[\"left\",[0.2],2]\n").start();
    }


    public void onRightClick(View v){
        new CommandWorkerThread("[\"right\",[0.2],2]\n").start();
    }

    public void onCalibrateDroneClick(View v){
        new CommandWorkerThread("[\"calibrate\",[],1]\n").start();
    }

    public void onUpClick(View v){
        new CommandWorkerThread("[\"up\",[0.2],2]\n").start();

    }

    public void onDownClick(View v){
        new CommandWorkerThread("[\"down\",[0.2],1]\n").start();
    }

    public void onFrontClick(View v){
        new CommandWorkerThread("[\"front\",[0.2],2]\n").start();

    }

    public void onBackClick(View v){
        new CommandWorkerThread("[\"back\",[0.2],2]\n").start();

    }


    public void landCommandClick(View v){
        new CommandWorkerThread("[\"land\",[],1]\n").start();
    }

    private class CommandWorkerThread extends Thread{

        private String _command="";

        public CommandWorkerThread(String command){
            _command = command;
        }

        @Override
        public void run(){
            try {

                Log.i(TAG, "sending request");
                dataOutputStream.writeBytes(_command);
                dataInputStream.readFully(response);
                dataOutputStream.flush();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
