package ssui.miti.org.thedroneapp.mjpeg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;

import ssui.miti.org.thedroneapp.MainActivity;
import ssui.miti.org.thedroneapp.VideoViewActivity;

/**
 * Created by MITI on 08/05/15.
 */
public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {

    public final static int POSITION_UPPER_LEFT  = 9;
    public final static int POSITION_UPPER_RIGHT = 3;
    public final static int POSITION_LOWER_LEFT  = 12;
    public final static int POSITION_LOWER_RIGHT = 6;

    public final static int SIZE_STANDARD   = 1;
    public final static int SIZE_BEST_FIT   = 4;
    public final static int SIZE_FULLSCREEN = 8;

    private MjpegViewThread thread;
    private MjpegInputStream mIn = null;
    private boolean showFps = false;
    private boolean mRun = false;
    private boolean surfaceDone = false;
    private boolean isDone = false;
    private Paint overlayPaint;
    private int overlayTextColor;
    private int overlayBackgroundColor;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private int displayMode;
    private int theColor;
    private int theColorLB;
    private int theColorUB;
    private Context mCtx;

    // Sending drone commands

   /* Button b;
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    String serverIPAdress = "192.168.1.5";
    int serverPort = 3001;
    byte[] response = new byte[256];
*/

    public class MjpegViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private long start;
        private Bitmap ovl;

        public MjpegViewThread(SurfaceHolder surfaceHolder, Context context) {
            Log.i("Mjpeg", "view created");
            mSurfaceHolder = surfaceHolder;
        }

        /*
        private class CommandWorkerThread extends Thread{

            private String _command="";

            public CommandWorkerThread(String command){
                _command = command;
            }

            @Override
            public void run(){
                try {

                    //Log.i(TAG, "sending request");
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
        }*/

        private Rect destRect(int bmw, int bmh) {
            int tempx;
            int tempy;
            if (displayMode == MjpegView.SIZE_STANDARD) {
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_BEST_FIT) {
                float bmasp = (float) bmw / (float) bmh;
                bmw = dispWidth;
                bmh = (int) (dispWidth / bmasp);
                if (bmh > dispHeight) {
                    bmh = dispHeight;
                    bmw = (int) (dispHeight * bmasp);
                }
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_FULLSCREEN) return new Rect(0, 0, dispWidth, dispHeight);
            return null;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized(mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }

        private Bitmap makeFpsOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth = b.width() + 2;
            int bheight = b.height() + 2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(overlayTextColor);
            c.drawText(text, -b.left + 1, (bheight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
            return bm;
        }

        public void run() {
            start = System.currentTimeMillis();
            PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
            Bitmap bm;
            int width;
            int height;
            Rect destRect;
            Canvas c = null;
            Paint p = new Paint();
            String fps = "";
            Log.i("Mjpeg",mRun+" "+surfaceDone);
            int theBMColor;

            /*new AsyncTask() {
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

            /*File root = Environment.getExternalStorageDirectory();

            InputStream raw = null;

            try {
                raw = mCtx.getAssets().open("res/raw/im.jpg");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            Bitmap bMap = BitmapFactory.decodeStream(raw);

            theColor = bMap.getPixel(30,53);
            System.out.println("bitmap region1 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
            theColor = bMap.getPixel(90,53);
            System.out.println("bitmap region2 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
            theColor = bMap.getPixel(150,53);
            System.out.println("bitmap region3 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
            theColor = bMap.getPixel(30,159);
            System.out.println("bitmap region4 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
            theColor = bMap.getPixel(90,159);
            System.out.println("bitmap region5 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
            theColor = bMap.getPixel(150,159);
            System.out.println("bitmap region6 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
            theColor = bMap.getPixel(30,265);
            System.out.println("bitmap region7 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
            theColor = bMap.getPixel(90,265);
            System.out.println("bitmap region8 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
            theColor = bMap.getPixel(150,265);
            System.out.println("bitmap region9 color red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));
*/

            while (mRun) {
                if(surfaceDone) {
                    try {
                        c = mSurfaceHolder.lockCanvas();
                        synchronized (mSurfaceHolder) {
                            try {
                              //  Log.i("Mjpeg","getting first frame");
                                bm = mIn.readMjpegFrame();
                                System.out.println("wow, i got the color: " + theColor);
                                System.out.println("red: " + Color.red(theColor) + " blue:" + Color.blue(theColor) + " green:" + Color.green(theColor));


                               /* if(a1Color != theColor )
                                {

                                }*/

                                /*if(!isDone)
                                {
                                    System.out.println("got here");
                                    isDone = true;
                                    ((VideoViewActivity) mCtx).sendCommand("[\"takeoff\",[],1]\n");
                                    ((VideoViewActivity) mCtx).sendCommand("[\"land\",[],1]\n");

                                }*/

                                System.out.println("bitmap data: W" + bm.getWidth() + " H"+bm.getHeight());

                                boolean match1 = false;
                                boolean match2 = false;
                                boolean match3 = false;
                                boolean match4 = false;
                                boolean match5 = false;
                                boolean match6 = false;
                                boolean match7 = false;
                                boolean match8 = false;
                                boolean match9 = false;

                                System.out.println("the range is: LOW" + theColorUB + " HIGH" + theColorUB);

                                theBMColor = bm.getPixel(53,30);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match1 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region1 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));
                                theBMColor = bm.getPixel(53,90);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match2 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region2 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));
                                theBMColor = bm.getPixel(53,150);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match3 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region3 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));
                                theBMColor = bm.getPixel(159,30);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match4 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region4 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));
                                theBMColor = bm.getPixel(159,90);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match5 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region5 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));
                                theBMColor = bm.getPixel(159,150);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match6 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region6 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));
                                theBMColor = bm.getPixel(265,30);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match7 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region7 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));
                                theBMColor = bm.getPixel(265,90);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match8 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region8 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));
                                theBMColor = bm.getPixel(265,150);
                                theBMColor = theBMColor * -1;
                                if (theBMColor>=theColorLB && theBMColor <= theColorUB)
                                    match9 = true;
                                System.out.println("rawcolor: " + theBMColor);
                                System.out.println("bitmap region9 color red: " + Color.red(theBMColor) + " green:" + Color.green(theBMColor) + " blue:" + Color.blue(theBMColor));


                                if(match1)
                                {
                                    System.out.println("Color MATCH REGION 1");
                                    ((VideoViewActivity) mCtx).sendCommand("R1");
                                }
                                else
                                if(match2)
                                {
                                    System.out.println("Color MATCH REGION 2");
                                    ((VideoViewActivity) mCtx).sendCommand("R2");
                                }
                                else
                                if(match3)
                                {
                                    System.out.println("Color MATCH REGION 3");
                                    ((VideoViewActivity) mCtx).sendCommand("R3");
                                }
                                else
                                if(match4)
                                {
                                    System.out.println("Color MATCH REGION 4");
                                    ((VideoViewActivity) mCtx).sendCommand("R4");
                                }
                                else
                                if(match5)
                                {
                                    System.out.println("Color MATCH REGION 5");
                                    ((VideoViewActivity) mCtx).sendCommand("R5");
                                }
                                else
                                if(match6)
                                {
                                    System.out.println("Color MATCH REGION 6");

                                    ((VideoViewActivity) mCtx).sendCommand("R6");
                                }
                                else
                                if(match7)
                                {
                                    System.out.println("Color MATCH REGION 7");
                                    ((VideoViewActivity) mCtx).sendCommand("R7");
                                }
                                else
                                if(match8)
                                {
                                    System.out.println("Color MATCH REGION 8");
                                    ((VideoViewActivity) mCtx).sendCommand("R8");
                                    //((VideoViewActivity) mCtx).sendCommand1("[\"takeoff\",[],1]\n");
                                }
                                else
                                if(match9)
                                {
                                    System.out.println("Color MATCH REGION 9");
                                    ((VideoViewActivity) mCtx).sendCommand("R9");
                                    //((VideoViewActivity) mCtx).sendCommand1("[\"takeoff\",[],1]\n");
                                }
                                /*System.out.println("bitmap data: W" + bm.getPixel(179,319));
                                System.out.println("bitmap data: W" + bm.getPixel(180,320));
                                System.out.println("bitmap data: W" + bm.getPixel(420,480));*/

                                destRect = destRect(bm.getWidth(), bm.getHeight());
                                c.drawColor(Color.BLACK);
                                c.drawBitmap(bm, null, destRect, p);

                                if(showFps) {
                                    p.setXfermode(mode);
                                    if(ovl != null) {
                                        height = ((ovlPos & 1) == 1) ? destRect.top : destRect.bottom-ovl.getHeight();
                                        width  = ((ovlPos & 8) == 8) ? destRect.left : destRect.right -ovl.getWidth();
                                        c.drawBitmap(ovl, width, height, null);
                                     //   Log.i("test","drawing bitmap");
                                    }
                                    p.setXfermode(null);
                                    frameCounter++;
                                    if((System.currentTimeMillis() - start) >= 1000) {
                                        fps = String.valueOf(frameCounter)+"fps";
                                        frameCounter = 0;
                                        start = System.currentTimeMillis();
                                        ovl = makeFpsOverlay(overlayPaint, fps);
                                    }
                                }
                            } catch (IOException e) {}
                        }
                    } finally { if (c != null) mSurfaceHolder.unlockCanvasAndPost(c); }
                }
            }
        }
    }

    private void init(Context context) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new MjpegViewThread(holder, context);
        setFocusable(true);
        overlayPaint = new Paint();
        overlayPaint.setTextAlign(Paint.Align.LEFT);
        overlayPaint.setTextSize(12);
        overlayPaint.setTypeface(Typeface.DEFAULT);
        overlayTextColor = Color.WHITE;
        overlayBackgroundColor = Color.BLACK;
        ovlPos = MjpegView.POSITION_LOWER_RIGHT;
        displayMode = MjpegView.SIZE_STANDARD;
        dispWidth = getWidth();
        dispHeight = getHeight();
    }

    public void startPlayback() {
        Log.i("Mjpeg","->"+mIn);
        if(mIn != null) {
            Log.i("test","starting playback");
            mRun = true;
            thread.start();
        }
    }

    public void stopPlayback() {
        mRun = false;
        boolean retry = true;
        while(retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    public MjpegView(Context context, AttributeSet attrs) { super(context, attrs); init(context); }
    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) { thread.setSurfaceSize(w, h); }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        stopPlayback();
    }

    public MjpegView(Context context, int theColor) {
        super(context);

        mCtx = context;

        theColor = theColor * -1;
        this.theColor = theColor;

        int extraUp = 0;
        int extraDown = 0;

        theColorLB = theColor - 2000000;
        if(theColorLB<0)
        {
            extraDown = theColorLB*-1;
            theColorLB = 0;
        }

        theColorUB = theColor + 2000000;
        if(theColorUB>16777215)
        {
            extraUp = theColorUB - 16777215;
            theColorUB = 16777215;
        }

        theColorUB = theColorUB+extraDown;
        theColorLB = theColorLB-extraUp;

        init(context);
        Log.i("Mjpeg","construtor");

        System.out.println("this is the color i got " + this.theColor);
        System.out.println("in red: " + Color.red(theColor) + " green:" + Color.green(theColor) + " blue:" + Color.blue(theColor));

    }
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceDone = true;
    }
    public void showFps(boolean b) {
        showFps = b;
    }
    public void setSource(MjpegInputStream source) {
        mIn = source;
      //  startPlayback();
    }
    public void setOverlayPaint(Paint p) {
        overlayPaint = p;
    }
    public void setOverlayTextColor(int c) {
        overlayTextColor = c;
    }
    public void setOverlayBackgroundColor(int c) {
        overlayBackgroundColor = c;
    }
    public void setOverlayPosition(int p) {
        ovlPos = p;
    }
    public void setDisplayMode(int s) {
        displayMode = s;
    }
}