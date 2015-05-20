package ssui.miti.org.thedroneapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by corzo on 5/16/15.
 */
public class menu1_Fragment extends Fragment {
    View rootview;
    public TextView theText;
    public ImageView droneImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.menu1_layout,container, false);

        int RGB = android.graphics.Color.rgb(0,181,236);
        rootview.setBackgroundColor(RGB);

        theText =   (TextView) rootview.findViewById(R.id.mainTextView);
        droneImage =   (ImageView) rootview.findViewById(R.id.theImage);
        /*
        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), ColorPickerActivity.class);
                myIntent.putExtra("key", 0); //Optional parameters
                getActivity().startActivity(myIntent);
            }
        });
*/
        return rootview;
    }
}
