package com.ashomok.imagetotext.my_docs;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsResponse;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by iuliia on 12/26/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<MyDocsResponse.MyDoc> mDataList;
    private List<MyDocsResponse.MyDoc> multiSelectDataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView sourceImage;
        public TextView timeStamp;

        ViewHolder(View v) {
            super(v);
            sourceImage = v.findViewById(R.id.sourceImage);
            timeStamp = v.findViewById(R.id.timeStamp);
            cardView = v.findViewById(R.id.cardView);
        }
    }

    //todo use MyDocsModel instead
    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(List<MyDocsResponse.MyDoc> mDataList, List<MyDocsResponse.MyDoc> multiSelectDataList) {
        this.mDataList = mDataList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_doc_view, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        MyDocsResponse.MyDoc item = getItem(position);
        View parent = holder.cardView.getRootView().getRootView();

        holder.timeStamp.setText(item.getTimeStamp());

        ImageView imageView = holder.sourceImage;

        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.getSourceImageUrl());
        // Load the image using Glide
        Glide.with(parent.getContext())
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .crossFade()
                .fitCenter()
                .into(imageView);

        //todo
//        holder.languageLayout.setOnClickListener(view -> {
//            if (checkedLanguageCodes.contains(item)) {
//                //checked - uncheck
//                removeFromChecked(item);
//                holder.updateUi(false);
//            } else {
//                //unchecked - check
//                if (checkedLanguageCodes.size() < MAX_CHECKED_ALLOWED) {
//                    addToChecked(item);
//                    holder.updateUi(true);
//                } else {
//                    String message = String.format(view.getContext().getString(R.string.max_checked_allowed),
//                            String.valueOf(MAX_CHECKED_ALLOWED));
//                    showError(message, parent);
//                }
//            }
//        });
//
//        holder.updateUi(checkedLanguageCodes.contains(item));
    }

    private MyDocsResponse.MyDoc getItem(int position) {
        return mDataList.get(position);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
