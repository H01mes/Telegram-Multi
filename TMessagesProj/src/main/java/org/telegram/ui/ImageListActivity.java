package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public class ImageListActivity extends BaseFragment {
    private int arrayId;
    private CustomListAdapter listAdapter;

    private class CustomListAdapter extends ArrayAdapter<String> {
        private final Integer[] imgid;
        private final String[] itemname;
        private final Context mContext;

        public CustomListAdapter(Context context, String[] itemname, Integer[] imgid) {
            super(context, R.layout.imagelist, itemname);
            this.mContext = context;
            this.itemname = itemname;
            this.imgid = imgid;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            String name = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).getString(ImageListActivity.this.arrayId == 0 ? "chatBubbleStyle" : "chatCheckStyle", this.itemname[0]);
            view = inflater.inflate(R.layout.imagelist, parent, false);
            if (name.equals(this.itemname[position])) {
                view.setBackgroundColor(Color.BLACK); //TODO Multi colors
            } else {
                view.setBackgroundColor(Color.BLACK);
            }
            ImageView inImageView = (ImageView) view.findViewById(R.id.bubble_in);
            ImageView outImageView = (ImageView) view.findViewById(R.id.bubble_out);
            ((TextView) view.findViewById(R.id.bubble_title)).setText(this.itemname[position]);
            inImageView.setImageResource(this.imgid[position].intValue());
            outImageView.setImageResource(this.imgid[this.itemname.length + position].intValue());
            if (ImageListActivity.this.arrayId == 1) {
                view.setPadding(50, 0, 0, 0);
                inImageView.getLayoutParams().width = 70;
                inImageView.setColorFilter(0, Mode.SRC_ATOP);
                outImageView.getLayoutParams().width = 70;
                outImageView.setColorFilter(0, Mode.SRC_ATOP);
                inImageView.setColorFilter(Theme.chatChecksColor, Mode.SRC_IN);
                outImageView.setColorFilter(Theme.chatChecksColor, Mode.SRC_IN);
            } else {
                inImageView.setColorFilter(Theme.chatLBubbleColor, Mode.SRC_IN);
                outImageView.setColorFilter(Theme.chatRBubbleColor, Mode.SRC_IN);
            }
            return view;
        }
    }

    public ImageListActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        this.arrayId = this.arguments.getInt("array_id", 0);
        if (this.arrayId != 0) {
            super.onFragmentCreate();
        } else {
            super.onFragmentCreate();
        }
        return true;
    }

    public View createView(Context context) {
        CharSequence string;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBar actionBar = this.actionBar;
        if (this.arrayId == 0) {
            string = LocaleController.getString("BubbleStyle", R.string.BubbleStyle);
        } else {
            string = LocaleController.getString("CheckStyle", R.string.CheckStyle);
        }
        actionBar.setTitle(string);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ImageListActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = getParentActivity().getLayoutInflater().inflate(R.layout.imagelistlayout, null, false);
        this.listAdapter = new CustomListAdapter(context, this.arrayId == 0 ? Theme.bubblesNamesArray : Theme.checksNamesArray, this.arrayId == 0 ? Theme.imgid : Theme.checkid);
        ListView list = (ListView) this.fragmentView.findViewById(R.id.list);
        list.setAdapter(this.listAdapter);
        list.setDivider(null);
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedItem = ImageListActivity.this.arrayId == 0 ? Theme.bubblesNamesArray[position] : Theme.checksNamesArray[position];
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
                String key = ImageListActivity.this.arrayId == 0 ? "chatBubbleStyle" : "chatCheckStyle";
                if (!preferences.getString(key, "").equals(selectedItem)) {
                    Editor editor = preferences.edit();
                    editor.putString(key, selectedItem);
                    editor.apply();
                    if (ImageListActivity.this.arrayId == 0) {
                        Theme.setBubbles(ImageListActivity.this.getParentActivity());
                    } else {
                        Theme.setChecks(ImageListActivity.this.getParentActivity());
                    }
                    Theme.applyChatTheme(false);
                    Theme.applyDialogsTheme();
                }
                ImageListActivity.this.listAdapter.notifyDataSetChanged();
                ImageListActivity.this.finishFragment();
            }
        });
        return this.fragmentView;
    }
}
