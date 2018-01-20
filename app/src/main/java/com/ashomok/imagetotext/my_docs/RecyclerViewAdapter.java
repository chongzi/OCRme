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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsResponse;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 12/26/17.
 */

//todo animate view apearence
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final MyDocsActivity.RecyclerViewCallback callback;
    private List<MyDocsResponse.MyDoc> mDataList;
    private List<MyDocsResponse.MyDoc> multiSelectDataList;
    public static final String TAG = DEV_TAG + RecyclerViewAdapter.class.getSimpleName();


    //todo use MyDocsModel instead
    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(List<MyDocsResponse.MyDoc> mDataList,
                               List<MyDocsResponse.MyDoc> multiSelectDataList,
                               MyDocsActivity.RecyclerViewCallback callback) {
        this.mDataList = mDataList;
        this.multiSelectDataList = multiSelectDataList;
        this.callback = callback;
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
        Context context = holder.cardView.getContext();

        //timestamp
        holder.timeStamp.setText(item.getTimeStamp());

        //source image
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(item.getSourceImageUrl());
        // Load the image using Glide
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .crossFade()
                .centerCrop()
                .into(holder.sourceImage);

        //card menu
        holder.menuBtn.setOnClickListener(view -> showPopupMenu(holder.menuBtn, position));

        //checkbox
        //todo

        //cardview init click callback
        holder.cardView.setOnClickListener(view -> callback.onItemClick(position)
        );
        holder.cardView.setOnLongClickListener(view -> {
            callback.onItemLongClick(position);
            return true;
        });
    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.doc_cardview, popup.getMenu());
        popup.setOnMenuItemClickListener(new DocMenuItemClickListener(position));
        popup.show();
    }

    private MyDocsResponse.MyDoc getItem(int position) {
        return mDataList.get(position);
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

                case R.id.Not_interasted_catugury:

                    //todo
                    Log.d(TAG, "message 1");
                    return true;
                case R.id.No_interasted:

                    //todo
                    Log.d(TAG, "message 2");
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

        ViewHolder(View v) {
            super(v);
            sourceImage = v.findViewById(R.id.sourceImage);
            timeStamp = v.findViewById(R.id.timeStamp);
            cardView = v.findViewById(R.id.cardView);
            menuBtn = v.findViewById(R.id.card_menu_btn);
            checkbox = v.findViewById(R.id.checkBox);
        }
    }
}
