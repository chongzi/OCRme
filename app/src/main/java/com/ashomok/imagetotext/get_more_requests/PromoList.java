package com.ashomok.imagetotext.get_more_requests;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.get_more_requests.row.PromoRowData;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.FollowUsOnFbDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.LoginToSystemDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.WatchVideoDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.RateAppDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

public class PromoList {
    public static final String TAG = DEV_TAG + PromoList.class.getSimpleName();

    public static List<PromoRowData> getList() {
        List<PromoRowData> result = new ArrayList<>();
        result.add(new PromoRowData(
                WatchVideoDelegate.ID, R.drawable.video_black_24dp, R.string.watch_video_ads, R.string.best_choise, 5));
        result.add(new PromoRowData(
                LoginToSystemDelegate.ID, R.drawable.ic_arrow_forward_black_24dp, R.string.login_to_system, 0, 5));
        result.add(new PromoRowData(
                RateAppDelegate.ID, R.drawable.ic_star_black_24dp, R.string.write_feedback, 0, 10));
        result.add(new PromoRowData(
                FollowUsOnFbDelegate.ID, R.drawable.ic_facebook, R.string.follow_us_on_fb, 0, 5));
        return result;
    }
}
