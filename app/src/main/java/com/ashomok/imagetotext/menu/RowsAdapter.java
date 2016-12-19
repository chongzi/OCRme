package com.ashomok.imagetotext.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashomok.imagetotext.R;

import java.util.List;

/**
 * Created by iuliia on 8/8/16.
 */
public class RowsAdapter extends ArrayAdapter<Row> {



    public RowsAdapter(Context context, List<Row> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Row row = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_row, parent, false);
        }
        // Lookup view for data population
        TextView text = (TextView) convertView.findViewById(R.id.text);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        // Populate the data into the template view using the data object
        text.setText(getContext().getResources().getString(row.getTextResourceID()));
        image.setImageDrawable(getDrawable(row.getIconResourceID()));
        // Return the completed view to render on screen
        return convertView;
    }

    private Drawable getDrawable(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getContext().getResources().getDrawable(id, getContext().getTheme());
        } else {
            return getContext().getResources().getDrawable(id);
        }
    }
}
