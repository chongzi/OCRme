package com.ashomok.imagetotext.ocr_result.tab_fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashomok.imagetotext.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/31/17.
 */

public class TextFragment extends TabFragment implements View.OnClickListener {
    private GestureDetector gestureDetector;
    private String imageLink = "gs://imagetotext-149919.appspot.com/IMG_9229.JPG";
    private String textResult = "dummy text dummy text dummy text dummy text dummy text dummy text " +
            "dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy textdummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy textdummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy textdummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy textdummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy textdummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy textdummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy textdummy text dummy text dummy text dummy text dummy text dummy text dummy text dummy text";
    private static final String TAG = DEV_TAG + TextFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.text_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void doStaff() {
        initImage();
        initText();
        initBottomPanel();
    }

    private void initBottomPanel() {
        View copyBtn = getActivity().findViewById(R.id.copy_btn);
        copyBtn.setOnClickListener(this);

        View translateBtn = getActivity().findViewById(R.id.translate_btn);
        translateBtn.setOnClickListener(this);

        View shareBtn = getActivity().findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(this);

        View badResult = getActivity().findViewById(R.id.bad_result_btn);
        badResult.setOnClickListener(this);
    }


    private void initText() {
        TextView mTextView = (TextView) getActivity().findViewById(R.id.text);
        mTextView.setText(textResult);
    }

    private void initImage() {

        final ImageView mImageView = (ImageView) getActivity().findViewById(R.id.image);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mStorageRef = storage.getReferenceFromUrl(imageLink);

        // Load the image using Glide
        Glide.with(getActivity())
                .using(new FirebaseImageLoader())
                .load(mStorageRef)
                .fitCenter()
                .into(mImageView);

        //scroll to centre
        final ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.image_scroll_view);
        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int centreHeight = mImageView.getHeight() / 2;
                int centreWidth = mImageView.getWidth() / 2;
                scrollView.scrollTo(centreWidth, centreHeight);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.copy_btn:
                copyTextToClipboard(textResult);
                break;
            case R.id.translate_btn:
              //// TODO: 8/23/17
                break;
            case R.id.share_btn:
                //// TODO: 8/23/17
                break;
            case R.id.bad_result_btn:
                //// TODO: 8/23/17
                break;
            default:
                break;
        }
    }

    private void copyTextToClipboard(CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.text_result), text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), getActivity().getString(R.string.copied), Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 300 milliseconds
        v.vibrate(300);
    }
}
