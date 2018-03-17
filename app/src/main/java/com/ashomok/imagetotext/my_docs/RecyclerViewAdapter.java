package com.ashomok.imagetotext.my_docs;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ashomok.imagetotext.utils.GlideApp;
import com.bumptech.glide.Glide;
import com.facebook.ads.AdChoicesView;
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

    RecyclerViewAdapter(Context context,
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
            return new DocViewHolder(docItem);
        } else if (viewType == NATIVE_AD) {
            View nativeAdItem = inflater.inflate(R.layout.mydocs_native_ad_layout, parent, false);
            return new NativeAdViewHolder(nativeAdItem);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == DOC) {
            bindViewHolder((DocViewHolder) holder, position);
        } else if (itemType == NATIVE_AD) {
            bindViewHolder((NativeAdViewHolder) holder, position);
        }
    }

    private void bindViewHolder(NativeAdViewHolder holder, int position) {
        NativeAd nativeAd = (NativeAd) mDataList.get(position);

        ImageView adImage = holder.adImage;
        TextView adTitle = holder.tvAdTitle;
        Button btnCTA = holder.btnCTA;
        LinearLayout adChoicesContainer = holder.adChoicesContainer;

        adTitle.setText(nativeAd.getAdTitle());
        NativeAd.downloadAndDisplayImage(nativeAd.getAdIcon(), adImage);
        btnCTA.setText(nativeAd.getAdCallToAction());
        AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);

        if (adChoicesContainer.getChildCount() == 0) {
            adChoicesContainer.addView(adChoicesView);
        }

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(adImage);
        clickableViews.add(btnCTA);
        clickableViews.add(adTitle);

        nativeAd.registerViewForInteraction(holder.container, clickableViews);
    }

    private void bindViewHolder(DocViewHolder holder, int position) {
        OcrResult item = (OcrResult) mDataList.get(position);

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
            GlideApp.with(holder.cardView.getContext())
                    .load(gsReference)
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
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mDataList.get(position);
        if (item instanceof OcrResult) {
            return DOC;
        } else if (item instanceof NativeAd) {
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

        DocMenuItemClickListener(int position) {
            this.position = position;
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

    static class DocViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView sourceImage;
        TextView timeStamp;
        ImageButton menuBtn;
        CheckBox checkbox;
        ImageView darkHint;

        DocViewHolder(View v) {
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
