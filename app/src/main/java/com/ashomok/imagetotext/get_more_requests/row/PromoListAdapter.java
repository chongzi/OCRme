package com.ashomok.imagetotext.get_more_requests.row;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashomok.imagetotext.R;

import java.util.List;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

public class PromoListAdapter extends RecyclerView.Adapter<PromoListAdapter.ViewHolder> {
    private static final String TAG = DEV_TAG + PromoListAdapter.class.getSimpleName();
    private final List<PromoRowData> dataList;
    private final Context context;

    PromoListAdapter(List<PromoRowData> promoModelsList, Context context) {
        this.dataList = promoModelsList;
        this.context = context;
    }

    @Override
    public PromoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promo_row, parent, false);

        return new PromoListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PromoListAdapter.ViewHolder holder, int position) {
        PromoRowData item = getItem(position);
        //todo

        holder.icon.setImageDrawable(context.getResources().getDrawable(item.getDrawableIconId()));
        holder.title.setText(item.getTitleStringId());
        if (item.isSubtitleExists()) {
            holder.subtitle.setVisibility(View.VISIBLE);
            holder.subtitle.setText(item.getSubtitleStringId());
        } else {
            holder.subtitle.setVisibility(View.INVISIBLE);
        }

        holder.isDone.setVisibility(item.isDone()? View.VISIBLE: View.GONE);
        holder.requestsCost.setText(context.getString(
                R.string.requests_cost, String.valueOf(item.getRequestsCost())));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
            }
        });
//        holder.icon.setImageDrawable(context.getResources().getDrawable(item.getDrawableId()));
//        holder.text.setText(item.getStringId());
    }

    private PromoRowData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    // Provide a reference to the views for each data item
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;
        TextView subtitle;
        TextView requestsCost;
        ImageView isDone;
        View layout;

        ViewHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.icon);
            title = v.findViewById(R.id.title);
            subtitle = v.findViewById(R.id.subtitle);
            requestsCost = v.findViewById(R.id.requests_cost);
            isDone = v.findViewById(R.id.is_done);
            layout = v.findViewById(R.id.row_parent);
        }
    }
}
