package com.ashomok.imagetotext.my_docs;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResult;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 12/26/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final MyDocsActivity.RecyclerViewCallback callback;
    private final Context context;
    private List<Object> mDataList;
    private List<OcrResult> multiSelectDataList;
    private static final int DOC = 0;
    private static final int NATIVE_AD = 1;

    public static final String TAG = DEV_TAG + RecyclerViewAdapter.class.getSimpleName();

    public RecyclerViewAdapter(Context context,
                               List<Object> mDataList,
                               List<OcrResult> multiSelectDataList,
                               MyDocsActivity.RecyclerViewCallback callback) {
        this.context = context;
        this.mDataList = mDataList;
        this.multiSelectDataList = multiSelectDataList;
        this.callback = callback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == DOC) {
            View docItem = inflater.inflate(R.layout.my_doc_view, parent, false);
            return new ViewHolder(docItem);
        } else if (viewType == NATIVE_AD) {
            View nativeAdItem = inflater.inflate(R.layout.mydocs_native_ad_layout, parent, false);
            return new NativeAdViewHolder(nativeAdItem);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        int itemType = getItemViewType(position);
        if (itemType == DOC) {
            ViewHolder holder = (ViewHolder) holder1;
            OcrResult item = (OcrResult) mDataList.get(position);;

            //check if select mode and update design
            if (multiSelectDataList.size() > 0) {
                //select mode

                holder.checkbox.setVisibility(View.VISIBLE);
                holder.darkHint.setVisibility(View.VISIBLE);
                if (multiSelectDataList.contains(item)) {
                    //current card selected - check checkbox
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }
            } else {
                //not select mode
                holder.checkbox.setVisibility(View.GONE);
                holder.darkHint.setVisibility(View.GONE);
            }

            //timestamp
            holder.timeStamp.setText(item.getTimeStamp());

            //source image
            try {
                // Create a reference to a file from a Google Cloud Storage URI
                StorageReference gsReference =
                        FirebaseStorage.getInstance().getReferenceFromUrl(item.getSourceImageUrl());
                // Load the image using Glide
                Glide.with(holder.cardView.getContext())
                        .using(new FirebaseImageLoader())
                        .load(gsReference)
                        .crossFade()
                        .centerCrop()
                        .into(holder.sourceImage);
            } catch (Exception e) {
                //ignore
                e.printStackTrace();
            }

            //card menu
            holder.menuBtn.setOnClickListener(view -> showPopupMenu(holder.menuBtn, position));

            //cardview init click callback
            holder.cardView.setOnClickListener(view -> callback.onItemClick(position));
            holder.cardView.setOnLongClickListener(view -> {
                callback.onItemLongClick(position);
                return true;
            });
        } else if (itemType == NATIVE_AD) {
            NativeAdViewHolder nativeAdViewHolder = (NativeAdViewHolder) holder1;
            NativeAd nativeAd = (NativeAd) mDataList.get(position);

            ImageView adImage = nativeAdViewHolder.adImage;
            TextView tvAdTitle = nativeAdViewHolder.tvAdTitle;
            Button btnCTA = nativeAdViewHolder.btnCTA;
            LinearLayout adChoicesContainer = nativeAdViewHolder.adChoicesContainer;

            tvAdTitle.setText(nativeAd.getAdTitle());
            NativeAd.downloadAndDisplayImage(nativeAd.getAdIcon(), adImage);
            btnCTA.setText(nativeAd.getAdCallToAction());
            AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);
            adChoicesContainer.addView(adChoicesView);

            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(adImage);
            clickableViews.add(btnCTA);
            //todo add more

            nativeAd.registerViewForInteraction(nativeAdViewHolder.container, clickableViews);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mDataList.get(position);
        if (item instanceof OcrResult) {
            return DOC;
        } else if (item instanceof Ad) {
            return NATIVE_AD;
        } else {
            return -1;
        }
    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.doc_cardview, popup.getMenu());
        popup.setOnMenuItemClickListener(new DocMenuItemClickListener(position));
        popup.show();
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class DocMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;

        public DocMenuItemClickListener(int positon) {
            this.position = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete:
                    callback.onItemDelete(position);
                    return true;
                case R.id.share_text:
                    callback.onItemShareText(position);
                    return true;
                case R.id.share_pdf:
                    callback.onItemSharePdf(position);
                    return true;
                default:
            }
            return false;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView sourceImage;
        TextView timeStamp;
        ImageButton menuBtn;
        CheckBox checkbox;
        ImageView darkHint;

        ViewHolder(View v) {
            super(v);
            sourceImage = v.findViewById(R.id.sourceImage);
            timeStamp = v.findViewById(R.id.timeStamp);
            cardView = v.findViewById(R.id.cardView);
            menuBtn = v.findViewById(R.id.card_menu_btn);
            checkbox = v.findViewById(R.id.checkBox);
            darkHint = v.findViewById(R.id.hint);
        }
    }

    private static class NativeAdViewHolder extends RecyclerView.ViewHolder {
        ImageView adImage;
        TextView tvAdTitle;
        Button btnCTA;
        View container;
        LinearLayout adChoicesContainer;

        NativeAdViewHolder(View itemView) {
            super(itemView);
            this.container = itemView;
            adImage = itemView.findViewById(R.id.native_ad_icon);
            tvAdTitle = itemView.findViewById(R.id.native_ad_title);
            btnCTA = itemView.findViewById(R.id.native_ad_call_to_action);
            adChoicesContainer = itemView.findViewById(R.id.ad_choices_container);
        }
    }
}
