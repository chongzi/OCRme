package com.ashomok.imagetotext.get_more_requests.row;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashomok.imagetotext.R;

import java.util.List;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

public class PromoListAdapter extends RecyclerView.Adapter<RowViewHolder> implements RowViewHolder.OnButtonClickListener {
    private static final String TAG = DEV_TAG + PromoListAdapter.class.getSimpleName();
    private final List<PromoRowData> dataList;

    @Inject
    public UiDelegatesFactory uiDelegatesFactory;

    @Inject
    public PromoListAdapter(List<PromoRowData> rowData) {
        this.dataList = rowData;
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promo_row, parent, false);

        return new RowViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        PromoRowData data = getItem(position);
        uiDelegatesFactory.onBindViewHolder(data, holder);
    }

    private PromoRowData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onButtonClicked(int position) {
        PromoRowData data = getItem(position);
        uiDelegatesFactory.onButtonClicked(data);
    }
}
