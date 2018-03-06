package com.ashomok.imagetotext.get_more_requests;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.get_more_requests.row.PromoRowData;

import java.util.ArrayList;
import java.util.List;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

//todo delete
public class PromoList {
    public static final String TAG = DEV_TAG + PromoList.class.getSimpleName();
    public static final String WATCH_VIDEO_ID = "watch_video_id";
    public static final String LOGIN_TO_SYSTEM_ID = "login_to_system_id";
    public static final String WRITE_FEEDBACK_ID = "write_feedback_id";
    public static final String FOLLOW_US_ON_FB_ID = "follow_us_on_fb_id";

    private List<PromoRowData> list;

    public PromoList() {
        list = initList();
    }

    public List<PromoRowData> getList() {
        return list;
    }

    private List<PromoRowData> initList() {
        List<PromoRowData> result = new ArrayList<>();
        result.add(new PromoRowData(
                WATCH_VIDEO_ID, R.drawable.ic_translate_black_24dp, R.string.watch_video_ads, R.string.best_choise, 5));
        result.add(new PromoRowData(
                LOGIN_TO_SYSTEM_ID, R.drawable.ic_translate_black_24dp, R.string.login_to_system, 0, 5));
        result.add(new PromoRowData(
                WRITE_FEEDBACK_ID, R.drawable.ic_translate_black_24dp, R.string.write_feedback, 0, 10));
        result.add(new PromoRowData(
                FOLLOW_US_ON_FB_ID, R.drawable.ic_translate_black_24dp, R.string.follow_us_on_fb, 0, 5));
        return result;
    }

}
