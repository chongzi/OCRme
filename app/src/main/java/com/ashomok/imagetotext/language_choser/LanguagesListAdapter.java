package com.ashomok.imagetotext.language_choser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.IntStream;
import com.ashomok.imagetotext.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ashomok.imagetotext.utils.InfoSnackbarUtil.showError;

/**
 * Created by iuliia on 12/11/16.
 */

public class LanguagesListAdapter extends RecyclerView.Adapter<LanguagesListAdapter.ViewHolder> {

    private static final String TAG = LanguagesListAdapter.class.getSimpleName();
    private static final int MAX_CHECKED_ALLOWED = 3;
    private final LanguageOcrActivity.StateChangedNotifier notifier;
    private List<String> allLanguages;
    private ResponsableList<String> checkedLanguages;


    LanguagesListAdapter(@Nullable List<String> allLanguages,
                         @Nullable ResponsableList<String> checkedLanguages,
                         LanguageOcrActivity.StateChangedNotifier notifier) {
        this.allLanguages = allLanguages;
        this.notifier = notifier;

        this.checkedLanguages = (checkedLanguages == null) ? new ResponsableList<>(new ArrayList<>()) : checkedLanguages;
        this.checkedLanguages.addOnListChangedListener(o -> {
            String checkedLanguage = (String) o;

            int changedPos = IntStream.range(0, allLanguages.size())
                    .filter(i -> checkedLanguage.equals(allLanguages.get(i)))
                    .findFirst().orElse(-1);

            notifyItemChanged(changedPos);
        });
    }

    List<String> getCheckedLanguages() {
        return checkedLanguages;
    }

    private void addToChecked(String language) {
        if (checkedLanguages.size() < MAX_CHECKED_ALLOWED) {
            checkedLanguages.add(language);
        } else {
            Log.w(TAG, "attempt to add checked language when max amount reached");
        }

        if (checkedLanguages.size() > 0) {
            notifier.changeAutoState(false);
        }
    }

    void onAutoStateChanged(boolean isAutoChecked) {
        if (isAutoChecked) {
            //unchecked all items
            checkedLanguages.clear();
            notifyDataSetChanged();
        }
    }

    private void removeFromChecked(String language) {
        checkedLanguages.remove(language);
    }


    String getItem(int i) {
        return allLanguages.get(i);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ocr_language_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = getItem(position);
        View parent = holder.languageLayout.getRootView();

        holder.languageName.setText(item);

        holder.languageLayout.setOnClickListener(view -> {
            if (checkedLanguages.contains(item)) {
                //checked - uncheck
                removeFromChecked(item);
                holder.updateUi(false);
            } else {
                //unchecked - check
                if (checkedLanguages.size() < MAX_CHECKED_ALLOWED) {
                    addToChecked(item);
                    holder.updateUi(true);
                } else {
                    String message = String.format(view.getContext().getString(R.string.max_checked_allowed),
                            String.valueOf(MAX_CHECKED_ALLOWED));
                    showError(message, parent);
                }
            }
        });

        if (checkedLanguages.contains(item)) {
            holder.updateUi(true);
        } else {
            holder.updateUi(false);
        }
    }

    @Override
    public int getItemCount() {
        return allLanguages.size();
    }

    // Provide a reference to the views for each data item
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout languageLayout;
        ImageView checkedIcon;
        TextView languageName;
        ImageView add;
        ImageView remove;

        ViewHolder(View v) {
            super(v);
            checkedIcon = v.findViewById(R.id.checked_icon);
            languageName = v.findViewById(R.id.language_name);
            add = v.findViewById(R.id.add);
            remove = v.findViewById(R.id.remove);
            languageLayout = v.findViewById(R.id.ocr_language_layout);
        }

        void updateUi(boolean checked) {
            checkedIcon.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
            add.setVisibility(checked ? View.GONE : View.VISIBLE);
            remove.setVisibility(checked ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * list with add / remove element event
     *
     * @param <E>
     */
    static class ResponsableList<E> extends ArrayList<E> {

        private List<OnListChangedListener> listenerList = new ArrayList<>();

        ResponsableList(@NonNull Collection<? extends E> c) {
            super(c);
        }

        void addOnListChangedListener(OnListChangedListener listener) {
            listenerList.add(listener);

        }

        @Override
        public boolean add(E e) {
            for (OnListChangedListener listener : listenerList) {
                listener.onListChangedFor(e);
            }
            return super.add(e);
        }

        @Override
        public boolean remove(Object o) {
            for (OnListChangedListener listener : listenerList) {
                listener.onListChangedFor(o);
            }
            return super.remove(o);
        }
    }

    public interface OnListChangedListener {
        void onListChangedFor(Object o);
    }
}
