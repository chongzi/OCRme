package com.ashomok.imagetotext.menu;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import com.ashomok.imagetotext.R;


/**
 * Created by iuliia on 8/8/16.
 */
public class ItemClickListener {
    private final Context context;


    public ItemClickListener(Context context) {
        this.context = context;
    }

    public void onRowClicked(int position) {
        switch (position) {
            case 0:
                //Categories
//                context.startActivity(
//                        new Intent(context, CategoriesListActivity.class));
                break;
            case 1:
                //Search by name
//                context.startActivity(
//                        new Intent(context, SearchByNamesActivity.class));
                break;
            default:
                break;
        }
    }
}
