package com.ashomok.ocrme.update_to_premium;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashomok.ocrme.R;

import java.util.List;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 2/2/18.
 */

public class FeaturesListAdapter extends RecyclerView.Adapter<FeaturesListAdapter.ViewHolder> {
    private static final String TAG = DEV_TAG + FeaturesListAdapter.class.getSimpleName();
    private final List<FeaturesList.FeatureModel> dataList;
    private final Context context;

    FeaturesListAdapter(List<FeaturesList.FeatureModel> featuresList, Context context) {
        this.dataList = featuresList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.premium_feature_row, parent, false);

        return new FeaturesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FeaturesList.FeatureModel item = getItem(position);
        holder.icon.setImageDrawable(context.getResources().getDrawable(item.getDrawableId()));
        holder.text.setText(item.getStringId());
    }

    private FeaturesList.FeatureModel getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    // Provide a reference to the views for each data item
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView text;

        ViewHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.icon);
            text = v.findViewById(R.id.text);
        }
    }
}
