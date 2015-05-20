package ssui.miti.org.thedroneapp;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by corzo on 5/16/15.
 */
public class menu3_Fragment extends android.support.v4.app.Fragment {
    View rootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rootview = inflater.inflate(R.layout.menu3_layout,container, false);

        int RGB = android.graphics.Color.rgb(0,181,236);
        rootview.setBackgroundColor(RGB);
        TextView tv = (TextView) rootview.findViewById(R.id.mainTextView);
        tv.setTextColor(Color.WHITE);
        return rootview;
    }
}
