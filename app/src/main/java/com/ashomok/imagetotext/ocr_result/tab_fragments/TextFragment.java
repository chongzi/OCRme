package com.ashomok.imagetotext.ocr_result.tab_fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.language_choser.LanguageActivity;
import com.ashomok.imagetotext.ocr_result.translate.TranslateActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.ashomok.imagetotext.Settings.appPackageName;
import static com.ashomok.imagetotext.language_choser.LanguageActivity.CHECKED_LANGUAGES;
import static com.ashomok.imagetotext.ocr_result.OcrResultActivity.LANGUAGE_CHANGED_REQUEST_CODE;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/31/17.
 */

//todo Forbidd splitter to go out of screen bounds. https://github.com/bieliaievays/OCRme/issues/2
public class TextFragment extends TabFragment implements View.OnClickListener {
    public static final String EXTRA_TEXT = "com.ashomokdev.imagetotext.TEXT";
    private CharSequence textResult;

    private long requestID = 123456789;
    private String imageLink = "gs://imagetotext-149919.appspot.com/IMG_9229.JPG";
    private static final String TAG = DEV_TAG + TextFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_fragment, container, false);
        Bundle bundle = getArguments();
        textResult = bundle.getCharSequence(EXTRA_TEXT);
        return view;
    }


    @Override
    protected void doStaff() {
        initImage();
        initText();
        initBottomPanel();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.copy_btn:
                copyTextToClipboard(textResult);
                break;
            case R.id.translate_btn:
                onTranslateClicked();
                break;
            case R.id.share_text_btn:
                onShareClicked();
                break;
            case R.id.bad_result_btn:
                onBadResultClicked();
                break;
            default:
                break;
        }
    }

    private void initBottomPanel() {
        View copyBtn = getActivity().findViewById(R.id.copy_btn);
        copyBtn.setOnClickListener(this);

        View translateBtn = getActivity().findViewById(R.id.translate_btn);
        translateBtn.setOnClickListener(this);

        View shareBtn = getActivity().findViewById(R.id.share_text_btn);
        shareBtn.setOnClickListener(this);

        View badResult = getActivity().findViewById(R.id.bad_result_btn);
        badResult.setOnClickListener(this);
    }


    private void initText() {
        EditText mTextView = (EditText) getActivity().findViewById(R.id.text);
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


    private void onBadResultClicked() {
        Intent intent = new Intent(getActivity(), LanguageActivity.class);
        intent.putExtra(CHECKED_LANGUAGES, getCurrentLanguages());
        startActivityForResult(intent, LANGUAGE_CHANGED_REQUEST_CODE);
    }

    @NonNull
    private ArrayList<String> getCurrentLanguages() {
        Set<String> checkedLanguageNames = obtainSavedLanguages();
        ArrayList<String> checkedLanguages = new ArrayList<>();
        checkedLanguages.addAll(checkedLanguageNames);
        return checkedLanguages;
    }

    private Set<String> obtainSavedLanguages() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> auto = new HashSet<String>() {{
            add(getString(R.string.auto));
        }};
        TreeSet<String> checkedLanguagesNames = new TreeSet<>(sharedPref.getStringSet(CHECKED_LANGUAGES, auto));
        return checkedLanguagesNames;
    }

    @SuppressWarnings("deprecation")
    private void onShareClicked() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        Resources res = getActivity().getResources();
        String linkToApp = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String sharedBody =
                String.format(res.getString(R.string.share_text_message), textResult, linkToApp);

        Spanned styledText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            styledText = Html.fromHtml(sharedBody, Html.FROM_HTML_MODE_LEGACY);
        } else {
            styledText = Html.fromHtml(sharedBody);
        }

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, res.getString(R.string.text_result));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, styledText);
        getActivity().startActivity(Intent.createChooser(sharingIntent, res.getString(R.string.send_text_result_to)));
    }

    private void onTranslateClicked() {
        Intent intent = new Intent(getActivity(), TranslateActivity.class);
//        intent.putExtra(EXTRA_LANGUAGE, language);
        intent.putExtra(EXTRA_TEXT, textResult);
        startActivity(intent);
    }

    private void copyTextToClipboard(CharSequence text) {
        ClipboardManager clipboard =
                (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.text_result), text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), getActivity().getString(R.string.copied),
                Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 300 milliseconds
        v.vibrate(300);
    }
}
