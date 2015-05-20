package ssui.miti.org.thedroneapp;

        import android.app.Activity;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.view.View;

public class ColorPickerActivity extends Activity implements
        ColorPicker.OnColorChangedListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);
        this.activity = this;
    }

    @Override
    public void colorChanged(String str,int color) {
        ColorPickerActivity.this.findViewById(android.R.id.content)
                .setBackgroundColor(color);
    }

    Activity activity;

    public void getColor(View v) {
        new ColorPicker(activity, this, "", Color.BLACK, Color.WHITE).show();
    }
}
