package com.ashomok.imagetotext.menu;


import com.ashomok.imagetotext.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iuliia on 8/8/16.
 */
public class Menu {

    public static List<Row> getRows()
   {
       List<Row> menuItems = new ArrayList<>();
       menuItems.add(new Row(R.drawable.ic_android_black_24dp, R.string.my_docs));
       menuItems.add(new Row(R.drawable.ic_android_black_24dp, R.string.about));
       menuItems.add(new Row(R.drawable.ic_android_black_24dp, R.string.share));
       menuItems.add(new Row(R.drawable.ic_android_black_24dp, R.string.rate));
       return menuItems;
   }
}
