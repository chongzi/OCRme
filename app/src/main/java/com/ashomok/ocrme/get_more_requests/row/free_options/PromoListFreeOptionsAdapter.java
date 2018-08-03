package com.ashomok.ocrme.get_more_requests.row.free_options;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.get_more_requests.row.free_options.option_delegates.FreeOptionRowViewHolder;

import java.util.List;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

public class PromoListFreeOptionsAdapter extends RecyclerView.Adapter<FreeOptionRowViewHolder>
        implements FreeOptionRowViewHolder.OnButtonClickListener {
    private static final String TAG = DEV_TAG + PromoListFreeOptionsAdapter.class.getSimpleName();
    private final List<PromoRowFreeOptionData> dataList;

    @Inject
    public UiDelegatesFactory uiDelegatesFactory;

    @Inject
    public PromoListFreeOptionsAdapter(List<PromoRowFreeOptionData> rowData) {
        this.dataList = rowData;
    }

    @NonNull
    @Override
    public FreeOptionRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promo_list_free_option_row, parent, false);

        return new FreeOptionRowViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FreeOptionRowViewHolder holder, int position) {
        PromoRowFreeOptionData data = getItem(position);
        uiDelegatesFactory.onBindViewHolder(data, holder);
    }

    private PromoRowFreeOptionData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onButtonClicked(int position) {
        PromoRowFreeOptionData data = getItem(position);
        uiDelegatesFactory.onButtonClicked(data);
    }
}
