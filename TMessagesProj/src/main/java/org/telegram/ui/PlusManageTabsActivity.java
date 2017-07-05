package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Arrays;

public class PlusManageTabsActivity extends BaseFragment {
    private int color = Theme.prefTitleColor;
    private int disabledColor = AndroidUtilities.getIntAlphaColor(this.color, 0.33f);
    private RecyclerListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean needReorder;
    private int[] tabs = new int[]{0, 1, 2, 3, 4, 5, 6};
    private ArrayList<Integer> tabsArray;
    private ArrayList<Integer> tabs_list;
    private int[] visible = new int[]{0, 0, 0, 0, 0, 0, 0};
    private boolean visibleChanged;

    private interface ItemTouchHelperAdapter {
        void swapElements(int i, int i2);
    }

    private interface ItemTouchHelperViewHolder {
        void onItemClear();

        void onItemSelected();
    }

    public class TabCheckCell extends FrameLayout {
        private CheckBoxSquare checkBox;
        private Drawable dRight = getResources().getDrawable(R.drawable.ic_swap_vertical);
        private boolean needDivider = true;
        private boolean show = false;
        private TextView textView;

        public TabCheckCell(Context context) {
            super(context);
            this.dRight.setColorFilter(PlusManageTabsActivity.this.color, Mode.SRC_IN);
            this.dRight = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(((BitmapDrawable) this.dRight).getBitmap(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), true));
            this.textView = new TextView(context);
            this.textView.setTextColor(PlusManageTabsActivity.this.color);
            this.textView.setTextSize(1, 20.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(19);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(15.0f));
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 3, 17.0f, 0.0f, 54.0f, 0.0f));
            this.checkBox = new CheckBoxSquare(context, false);
            this.checkBox.setDuplicateParentStateEnabled(false);
            this.checkBox.setFocusable(false);
            this.checkBox.setFocusableInTouchMode(false);
            this.checkBox.setClickable(true);
            addView(this.checkBox, LayoutHelper.createFrame(25, 25.0f, 21, 0.0f, 5.0f, 20.0f, 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(48.0f), MeasureSpec.EXACTLY));
        }

        public void setText(String text) {
            this.textView.setText(text);
            LayoutParams layoutParams = (LayoutParams) this.textView.getLayoutParams();
            layoutParams.height = -1;
            layoutParams.topMargin = 0;
            this.textView.setLayoutParams(layoutParams);
            setWillNotDraw(true);
        }

        public void setChecked(boolean checked) {
            this.checkBox.setChecked(checked, true);
        }

        public void setTextAndIcon(String text, int resId) {
            Drawable drawable = null;
            try {
                this.textView.setText(text);
                Drawable d = getResources().getDrawable(resId);
                TextView textView = this.textView;
                if (this.show) {
                    drawable = this.dRight;
                }
                textView.setCompoundDrawablesWithIntrinsicBounds(d, null, drawable, null);
                this.textView.setPadding(this.textView.getPaddingLeft(), this.textView.getPaddingTop(), AndroidUtilities.dp(25.0f), this.textView.getPaddingBottom());
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setIconColor(int color) {
            Drawable drawable = null;
            try {
                Drawable d = this.textView.getCompoundDrawables()[0];
                d.setColorFilter(color, Mode.SRC_IN);
                TextView textView = this.textView;
                if (this.show) {
                    drawable = this.dRight;
                }
                textView.setCompoundDrawablesWithIntrinsicBounds(d, null, drawable, null);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    private class RecyclerListAdapter extends Adapter<RecyclerListAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

        public class ItemViewHolder extends ViewHolder implements ItemTouchHelperViewHolder, OnClickListener {
            public final TabCheckCell tabCell;

            ItemViewHolder(View itemView) {
                super(itemView);
                this.tabCell = (TabCheckCell) itemView;
                if (this.tabCell.getChildAt(1) instanceof CheckBoxSquare) {
                    this.tabCell.getChildAt(1).setOnClickListener(this);
                }
            }

            public void onItemSelected() {
                this.itemView.setBackgroundColor(Color.BLACK);
            } //Todo multi color

            public void onItemClear() {
                this.itemView.setBackgroundColor(0);
            }

            public void onClick(View view) {
                PlusManageTabsActivity.this.visibleChanged = true;
                if (PlusManageTabsActivity.this.visible[getPosition()] == -1) {
                    this.tabCell.setTextColor(PlusManageTabsActivity.this.color);
                    this.tabCell.setIconColor(PlusManageTabsActivity.this.color);
                    this.tabCell.setChecked(true);
                    PlusManageTabsActivity.this.visible[getPosition()] = 0;
                    return;
                }
                this.tabCell.setTextColor(PlusManageTabsActivity.this.disabledColor);
                this.tabCell.setIconColor(PlusManageTabsActivity.this.disabledColor);
                this.tabCell.setChecked(false);
                PlusManageTabsActivity.this.visible[getPosition()] = -1;
            }
        }

        private RecyclerListAdapter() {
        }

        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TabCheckCell view = new TabCheckCell(parent.getContext());
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new ItemViewHolder(view);
        }

        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.tabCell.setTextAndIcon(Theme.tabTitles[PlusManageTabsActivity.this.tabs[position]], Theme.tabIcons[PlusManageTabsActivity.this.tabs[position]]);
            if (PlusManageTabsActivity.this.visible[position] < 0) {
                holder.tabCell.setTextColor(PlusManageTabsActivity.this.disabledColor);
                holder.tabCell.setIconColor(PlusManageTabsActivity.this.disabledColor);
                holder.tabCell.setChecked(false);
                return;
            }
            holder.tabCell.setTextColor(PlusManageTabsActivity.this.color);
            holder.tabCell.setIconColor(PlusManageTabsActivity.this.color);
            holder.tabCell.setChecked(true);
        }

        public int getItemCount() {
            return PlusManageTabsActivity.this.tabs.length;
        }

        public void swapElements(int fromIndex, int toIndex) {
            if (fromIndex != toIndex) {
                PlusManageTabsActivity.this.needReorder = true;
            }
            int t2 = PlusManageTabsActivity.this.tabs[toIndex];
            int t1 = PlusManageTabsActivity.this.tabs[fromIndex];
            PlusManageTabsActivity.this.tabs[fromIndex] = t2;
            PlusManageTabsActivity.this.tabs[toIndex] = t1;
            int v2 = PlusManageTabsActivity.this.visible[toIndex];
            int v1 = PlusManageTabsActivity.this.visible[fromIndex];
            if (v1 != v2) {
                PlusManageTabsActivity.this.visibleChanged = true;
            }
            PlusManageTabsActivity.this.visible[fromIndex] = v2;
            PlusManageTabsActivity.this.visible[toIndex] = v1;
            notifyItemMoved(fromIndex, toIndex);
        }
    }

    private class SimpleItemTouchHelperCallback extends Callback {
        private final ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            this.mAdapter = adapter;
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder source, ViewHolder target) {
            this.mAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }

        public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                ((ItemTouchHelperViewHolder) viewHolder).onItemSelected();
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            ((ItemTouchHelperViewHolder) viewHolder).onItemClear();
        }
    }

    private void storeVisibilityArray() {
        ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putString("tabs_visible", Arrays.toString(this.visible)).apply();
        refreshVisibility();
    }

    private void storeTabsArray() {
        ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putString("tabs_array", Arrays.toString(this.tabs)).apply();
    }

    private void saveArray() {
        storeTabsArray();
        this.tabs_list = new ArrayList();
        Editor mEdit = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
        mEdit.remove("tabs_size");
        int size = this.tabs.length;
        int i = 0;
        while (i < this.tabs.length) {
            mEdit.remove("tab_" + i);
            if (!this.visibleChanged || this.visible[i] >= 0) {
                this.tabs_list.add(Integer.valueOf(this.tabs[i]));
            } else {
                size--;
            }
            i++;
        }
        mEdit.commit();
        storeTabsArrayList();
        boolean changed = false;
        if (size < 2) {
            Theme.plusHideTabs = true;
            changed = true;
        } else if (Theme.plusHideTabs) {
            Theme.plusHideTabs = false;
            changed = true;
        }
        if (changed) {
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
            editor.putBoolean("hideTabs", Theme.plusHideTabs);
            editor.apply();
        }
    }

    private void storeTabsArrayList() {
        ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putString("tabs_list", this.tabs_list.toString()).apply();
    }

    private void refreshVisibility() {
        for (int i = 0; i < this.visible.length; i++) {
            changeVisibility(Theme.tabType[this.tabs[i]], this.visible[i] != 0);
        }
    }

    private void getVisibilityArray() {
        String stringArray = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getString("tabs_visible", null);
        if (stringArray != null) {
            String[] split = stringArray.substring(1, stringArray.length() - 1).split(", ");
            for (int i = 0; i < split.length; i++) {
                try {
                    String s = split[i];
                    if (s.length() > 0) {
                        this.visible[i] = Integer.parseInt(s);
                    }
                } catch (Throwable e) {
                    this.visible[i] = -1;
                    FileLog.e(e);
                }
            }
        }
    }

    private void getTabsArray() {
        String stringArray = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).getString("tabs_array", null);
        if (stringArray != null) {
            String[] split = stringArray.substring(1, stringArray.length() - 1).split(", ");
            for (int i = 0; i < split.length; i++) {
                try {
                    String s = split[i];
                    if (s.length() > 0) {
                        this.tabs[i] = Integer.parseInt(s);
                    }
                } catch (Throwable e) {
                    this.tabs[i] = i;
                    FileLog.e(e);
                }
            }
        }
    }

    private void getTabsArrayList() {
        this.tabs_list = new ArrayList();
        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
        String stringArray = plusPreferences.getString("tabs_list", null);
        int i;
        if (stringArray != null) {
            String[] split = stringArray.substring(1, stringArray.length() - 1).split(", ");
            for (String s : split) {
                try {
                    if (s.length() > 0) {
                        this.tabs_list.add(Integer.valueOf(Integer.parseInt(s)));
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            return;
        }
        for (i = 0; i < plusPreferences.getInt("tabs_size", this.tabs.length); i++) {
            int p = plusPreferences.getInt("tab_" + i, -1);
            ArrayList arrayList = this.tabs_list;
            if (p == -1) {
                p = i;
            }
            arrayList.add(Integer.valueOf(p));
        }
    }

    private void changeVisibility(int type, boolean visible) {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
        switch (type) {
            case 0:
                Theme.plusHideAllTab = visible;
                editor.putBoolean("hideAllTab", visible);
                break;
            case 3:
                Theme.plusHideUsersTab = visible;
                editor.putBoolean("hideUsers", visible);
                break;
            case 4:
                Theme.plusHideGroupsTab = visible;
                editor.putBoolean("hideGroups", visible);
                break;
            case 5:
                Theme.plusHideChannelsTab = visible;
                editor.putBoolean("hideChannels", visible);
                break;
            case 6:
                Theme.plusHideBotsTab = visible;
                editor.putBoolean("hideBots", visible);
                break;
            case 7:
                Theme.plusHideSuperGroupsTab = visible;
                editor.putBoolean("hideSGroups", visible);
                break;
            case 8:
                Theme.plusHideFavsTab = visible;
                editor.putBoolean("hideFavs", visible);
                break;
        }
        editor.apply();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (this.tabsArray == null) {
            getVisibilityArray();
            this.tabsArray = new ArrayList();
            getTabsArray();
            getTabsArrayList();
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        sendReorder();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Tabs", R.string.Tabs));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PlusManageTabsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.BLACK); //TODO Multi color
        frameLayout.addView(layout, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setFocusable(true);
        if (Theme.usePlusTheme) {
            this.listView.setBackgroundColor(Theme.prefBGColor);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(1);
        this.listView.setLayoutManager(layoutManager);
        this.listAdapter = new RecyclerListAdapter();
        new ItemTouchHelper(new SimpleItemTouchHelperCallback(this.listAdapter)).attachToRecyclerView(this.listView);
        layout.addView(this.listView, LayoutHelper.createLinear(-1, 0, 1.0f, 3));
        this.listView.setAdapter(this.listAdapter);
        TextInfoPrivacyCell textInfoCell = new TextInfoPrivacyCell(context);
        textInfoCell.setText(LocaleController.getString("TabsScreenInfo", R.string.TabsScreenInfo));
        layout.addView(textInfoCell, LayoutHelper.createLinear(-1, -2, 83));
        return this.fragmentView;
    }

    private void sendReorder() {
        if (this.visibleChanged) {
            storeVisibilityArray();
            this.needReorder = true;
        }
        if (this.needReorder) {
            this.needReorder = false;
            saveArray();
            this.visibleChanged = false;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(15));
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (Theme.usePlusTheme) {
            updateTheme();
        }
    }

    private void updateTheme() {
        this.actionBar.setBackgroundColor(Theme.prefActionbarColor);
        this.actionBar.setTitleColor(Theme.prefActionbarTitleColor);
        Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        back.setColorFilter(Theme.prefActionbarIconsColor, Mode.MULTIPLY);
        this.actionBar.setBackButtonDrawable(back);
    }
}
