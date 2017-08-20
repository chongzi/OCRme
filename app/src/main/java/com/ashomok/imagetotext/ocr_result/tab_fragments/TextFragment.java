package com.ashomok.imagetotext.ocr_result.tab_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashomok.imagetotext.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by iuliia on 5/31/17.
 */

public class TextFragment extends Fragment {

    private String imageLink = "gs://imagetotext-149919.appspot.com/IMG_9229.JPG";
    private StorageReference mStorageRef;  //Reference to an image file in Firebase Storage

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.text_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        mStorageRef = storage.getReferenceFromUrl(imageLink);

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.image);

        // Load the image using Glide
        Glide.with(getActivity())
                .using(new FirebaseImageLoader())
                .load(mStorageRef)
                .into(imageView);
    }

}
