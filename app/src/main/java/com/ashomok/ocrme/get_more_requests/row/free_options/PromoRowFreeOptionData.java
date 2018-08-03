package com.ashomok.ocrme.get_more_requests.row.free_options;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by iuliia on 3/4/18.
 */
public class PromoRowFreeOptionData {
    private String id;
    private int requestsCost; //each promo gives some requests amount
    @DrawableRes
    private int drawableIconId;
    @StringRes
    private int titleStringId;

    @StringRes
    private int subtitleStringId;

    public int getDrawableIconId() {
        return drawableIconId;
    }

    public int getTitleStringId() {
        return titleStringId;
    }

    public int getSubtitleStringId() {
        return subtitleStringId;
    }

    public String getId() {
        return id;
    }


    public int getRequestsCost() {
        return requestsCost;
    }

    public PromoRowFreeOptionData(String id, @DrawableRes int drawableIconId,
                                  @StringRes int titleStringId,
                                  @StringRes int subtitleStringId, int requestsCost) {
        this.drawableIconId = drawableIconId;
        this.titleStringId = titleStringId;
        this.id = id;
        this.requestsCost = requestsCost;
        this.subtitleStringId = subtitleStringId;
    }

    public boolean isSubtitleExists() {
        return subtitleStringId != 0;
    }
}
