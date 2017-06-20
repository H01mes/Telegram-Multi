package org.telegram.ui.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.PlusPagerSlidingTabStrip.IconTabProvider;
import org.telegram.ui.Components.PlusPagerSlidingTabStrip.PlusScrollSlidingTabStripDelegate;

import java.util.ArrayList;

public class TabsView extends FrameLayout implements NotificationCenterDelegate {
    private static final String TAG = "TabsView";
    private static final int tabCount = 7;
    private int currentPage;
    private boolean force;
    private Listener listener;
    private ViewPager pager;
    private PlusPagerSlidingTabStrip pagerSlidingTabStrip;
    private int[] positions = new int[]{-1, -1, -1, -1, -1, -1, -1};
    private ArrayList<Tab> tabsArray;
    private LinearLayout tabsContainer;
    private ArrayList<Integer> tabs_list;

    public interface Listener {
        void onPageSelected(int i, int i2);

        void onTabClick();

        void onTabLongClick(int i, int i2);

        void refresh(boolean z);
    }

    class Tab {
        private int position;
        private final int res;
        private final String title;
        private final int type;
        private int unread = 0;

        Tab(String title, int res, int type, int position) {
            this.title = title;
            this.res = res;
            this.type = type;
            this.position = position;
        }

        public String getTitle() {
            return this.title;
        }

        public int getRes() {
            return this.res;
        }

        public int getType() {
            return this.type;
        }

        public int getPosition() {
            return this.position;
        }

        public int getUnread() {
            return this.unread;
        }

        public void setUnread(int unread) {
            this.unread = unread;
        }
    }

    private class TabsAdapter extends PagerAdapter implements IconTabProvider {
        private TabsAdapter() {
        }

        public int getCount() {
            return TabsView.this.tabsArray.size();
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (TabsView.this.pagerSlidingTabStrip != null) {
                TabsView.this.pagerSlidingTabStrip.notifyDataSetChanged();
            }
        }

        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view = new View(viewGroup.getContext());
            viewGroup.addView(view);
            return view;
        }

        public void destroyItem(ViewGroup viewGroup, int position, Object object) {
            viewGroup.removeView((View) object);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }

        public int getPageIconResId(int position) {
            return ((Tab) TabsView.this.tabsArray.get(position)).getRes();
        }

        public String getPageTitle(int position) {
            return ((Tab) TabsView.this.tabsArray.get(position)).getTitle();
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
        for (i = 0; i < plusPreferences.getInt("tabs_size", 7); i++) {
            if (i == 0) {
                if (!Theme.plusHideAllTab) {
                    this.tabs_list.add(Integer.valueOf(i));
                }
            } else if (i == 1) {
                if (!Theme.plusHideUsersTab) {
                    this.tabs_list.add(Integer.valueOf(i));
                }
            } else if (i == 2) {
                if (!Theme.plusHideGroupsTab) {
                    this.tabs_list.add(Integer.valueOf(i));
                }
            } else if (i == 3) {
                if (!Theme.plusHideSuperGroupsTab) {
                    this.tabs_list.add(Integer.valueOf(i));
                }
            } else if (i == 4) {
                if (!Theme.plusHideChannelsTab) {
                    this.tabs_list.add(Integer.valueOf(i));
                }
            } else if (i == 5) {
                if (!Theme.plusHideBotsTab) {
                    this.tabs_list.add(Integer.valueOf(i));
                }
            } else if (i == 6 && !Theme.plusHideFavsTab) {
                this.tabs_list.add(Integer.valueOf(i));
            }
        }
        storeTabsArrayList();
    }

    private void storeTabsArrayList() {
        ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putString("tabs_list", this.tabs_list.toString()).apply();
    }

    private void loadArray() {
        getTabsArrayList();
        this.tabsArray.clear();
        int size = this.tabs_list.size();
        for (int i = 0; i < size; i++) {
            int p = ((Integer) this.tabs_list.get(i)).intValue();
            int type = Theme.tabType[p];
            if (type == 4 && !this.tabs_list.contains(Integer.valueOf(3))) {
                type = 9;
            }
            this.tabsArray.add(new Tab(Theme.tabTitles[p], Theme.tabIcons[p], type, p));
            this.positions[p] = i;
        }
        if (size < 2 && !Theme.plusHideTabs) {
            Theme.plusHideTabs = true;
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
            editor.putBoolean("hideTabs", Theme.plusHideTabs);
            editor.apply();
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.refreshTabs, Integer.valueOf(10));
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    try {
                        Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("TabsWarningMsg", R.string.TabsWarningMsg), Toast.LENGTH_SHORT).show();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            });
        }
        this.pager.setAdapter(null);
        this.pager.setOffscreenPageLimit(size);
        this.pager.setAdapter(new TabsAdapter());
        updatePagerItem();
    }

    public void reloadTabs() {
        loadArray();
        this.pager.getAdapter().notifyDataSetChanged();
    }

    public void updateTabsColors() {
        if (this.tabsContainer != null) {
            paintTabs();
        }
        if (this.pagerSlidingTabStrip != null) {
            this.pagerSlidingTabStrip.notifyDataSetChanged();
        }
    }

    private void paintTabs() {
        if (Theme.usePlusTheme) {
            this.tabsContainer.setBackgroundColor(Theme.chatsTabsBGColor == Theme.defColor ? Theme.chatsHeaderColor : Theme.chatsTabsBGColor);
            int val = Theme.chatsHeaderGradient;
            if (val > 0) {
                Orientation go;
                switch (val) {
                    case 2:
                        go = Orientation.LEFT_RIGHT;
                        break;
                    case 3:
                        go = Orientation.TL_BR;
                        break;
                    case 4:
                        go = Orientation.BL_TR;
                        break;
                    default:
                        go = Orientation.TOP_BOTTOM;
                        break;
                }
                int gradColor = Theme.chatsHeaderGradientColor;
                GradientDrawable gd = new GradientDrawable(go, new int[]{Theme.chatsHeaderColor, gradColor});
                if (Theme.chatsTabsBGColor == Theme.defColor) {
                    this.tabsContainer.setBackgroundDrawable(gd);
                    return;
                }
                return;
            }
            return;
        }
        this.tabsContainer.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
    }

    private void updatePagerItem() {
        int i;
        int size = this.tabsArray.size();
        if (Theme.plusHideTabs) {
            i = 0;
        } else {
            i = ((Tab) this.tabsArray.get(size > Theme.plusSelectedTab ? Theme.plusSelectedTab : size - 1)).getType();
        }
        Theme.plusDialogType = i;
        if (Theme.plusDialogType == 4 && !this.tabs_list.contains(Integer.valueOf(3))) {
            Theme.plusDialogType = 9;
        }
        this.currentPage = Theme.plusSelectedTab;
        this.pager.setCurrentItem(this.currentPage);
    }

    public TabsView(Context context) {
        super(context);
        this.pager = new ViewPager(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        if (this.tabsArray == null) {
            this.tabsArray = new ArrayList();
            loadArray();
        }
        this.tabsContainer = new LinearLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        this.tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        paintTabs();
        addView(this.tabsContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.pagerSlidingTabStrip = new PlusPagerSlidingTabStrip(context);
        this.pagerSlidingTabStrip.setShouldExpand(Theme.plusTabsShouldExpand);
        this.pagerSlidingTabStrip.setViewPager(this.pager);
        this.pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(3.0f));
        this.pagerSlidingTabStrip.setDividerColor(0);
        this.pagerSlidingTabStrip.setUnderlineHeight(0);
        this.pagerSlidingTabStrip.setUnderlineColor(0);
        this.tabsContainer.addView(this.pagerSlidingTabStrip, LayoutHelper.createLinear(0, -1, 1.0f));
        this.pagerSlidingTabStrip.setDelegate(new PlusScrollSlidingTabStripDelegate() {
            public void onTabLongClick(int position) {
                if (Theme.plusSelectedTab == position && TabsView.this.listener != null) {
                    TabsView.this.listener.onTabLongClick(position, ((Tab) TabsView.this.tabsArray.get(position)).getType());
                }
            }

            public void onTabsUpdated() {
                TabsView.this.forceUpdateTabCounters();
                TabsView.this.unreadCount();
            }

            public void onTabClick() {
                if (TabsView.this.listener != null) {
                    TabsView.this.listener.onTabClick();
                }
            }
        });
        this.pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            private boolean loop;

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (TabsView.this.listener != null) {
                    TabsView.this.listener.onPageSelected(position, ((Tab) TabsView.this.tabsArray.get(position)).getType());
                }
                TabsView.this.currentPage = position;
                TabsView.this.saveNewPage();
            }

            public void onPageScrollStateChanged(int state) {
                boolean z = true;
                if (state == 0) {
                    if (this.loop) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                TabsView.this.pager.setCurrentItem(TabsView.this.currentPage == 0 ? TabsView.this.pager.getAdapter().getCount() - 1 : 0);
                            }
                        }, 100);
                        this.loop = false;
                    }
                } else if (state == 1) {
                    if (!(Theme.plusInfiniteTabsSwipe && (TabsView.this.currentPage == 0 || TabsView.this.currentPage == TabsView.this.pager.getAdapter().getCount() - 1))) {
                        z = false;
                    }
                    this.loop = z;
                } else if (state == 2) {
                    this.loop = false;
                }
            }
        });
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1.0f));
        forceUpdateTabCounters();
        unreadCount();
    }

    private void saveNewPage() {
        Theme.plusSelectedTab = this.currentPage;
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit();
        editor.putInt("selectedTab", Theme.plusSelectedTab);
        Theme.plusDialogType = ((Tab) this.tabsArray.get(Theme.plusSelectedTab)).getType();
        editor.putInt("dialogType", Theme.plusDialogType);
        editor.apply();
    }

    public ViewPager getPager() {
        return this.pager;
    }

    public void setListener(Listener value) {
        this.listener = value;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.refreshTabsCounters);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.refreshTabsCounters);
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.refreshTabsCounters && !Theme.plusHideTabs && this.tabsArray != null && this.tabsArray.size() > 1) {
            unreadCount();
        }
    }

    public void forceUpdateTabCounters() {
        this.force = true;
    }

    private void unreadCount() {
        if (!Theme.plusHideFavsTab) {
            unreadCount(MessagesController.getInstance().dialogsFavs, this.positions[6]);
        }
        if (!Theme.plusHideBotsTab) {
            unreadCount(MessagesController.getInstance().dialogsBots, this.positions[5]);
        }
        if (!Theme.plusHideChannelsTab) {
            unreadCount(MessagesController.getInstance().dialogsChannels, this.positions[4]);
        }
        unreadCountGroups();
        if (!Theme.plusHideUsersTab) {
            unreadCount(MessagesController.getInstance().dialogsUsers, this.positions[1]);
        }
        if (!Theme.plusHideAllTab) {
            unreadCountAll(MessagesController.getInstance().dialogs, this.positions[0]);
        }
    }

    private void unreadCountGroups() {
        if (!Theme.plusHideGroupsTab) {
            unreadCount(!Theme.plusHideSuperGroupsTab ? MessagesController.getInstance().dialogsGroups : MessagesController.getInstance().dialogsGroupsAll, this.positions[2]);
        }
        if (!Theme.plusHideSuperGroupsTab) {
            unreadCount(MessagesController.getInstance().dialogsMegaGroups, this.positions[3]);
        }
    }

    private void unreadCount(ArrayList<TL_dialog> dialogs, int position) {
        if (position != -1) {
            SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
            boolean allMuted = true;
            int unreadCount = 0;
            if (!(dialogs == null || dialogs.isEmpty())) {
                for (int a = 0; a < dialogs.size(); a++) {
                    TL_dialog dialg = (TL_dialog) dialogs.get(a);
                    if (dialg != null && dialg.unread_count > 0) {
                        boolean isMuted = MessagesController.getInstance().isDialogMuted(dialg.id);
                        if (!isMuted || !Theme.plusTabsCountersCountNotMuted) {
                            int i = dialg.unread_count;
                            if (i == 0 && plusPreferences.getInt("unread_" + dialg.id, 0) == 1) {
                                i = 1;
                            }
                            if (i > 0) {
                                if (!Theme.plusTabsCountersCountChats) {
                                    unreadCount += i;
                                } else if (i > 0) {
                                    unreadCount++;
                                }
                                if (i > 0 && !isMuted) {
                                    allMuted = false;
                                }
                            }
                        }
                    }
                }
            }
            if (unreadCount != ((Tab) this.tabsArray.get(position)).getUnread() || this.force) {
                ((Tab) this.tabsArray.get(position)).setUnread(unreadCount);
                this.pagerSlidingTabStrip.updateCounter(position, unreadCount, allMuted, this.force);
            }
        }
    }

    private void unreadCountAll(ArrayList<TL_dialog> dialogs, int position) {
        if (position != -1) {
            SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0);
            boolean allMuted = true;
            int unreadCount = 0;
            if (!(dialogs == null || dialogs.isEmpty())) {
                for (int a = 0; a < dialogs.size(); a++) {
                    TL_dialog dialg = (TL_dialog) dialogs.get(a);
                    if (dialg != null && dialg.unread_count > 0) {
                        boolean isMuted = MessagesController.getInstance().isDialogMuted(dialg.id);
                        if (!isMuted || !Theme.plusTabsCountersCountNotMuted) {
                            int i = dialg.unread_count;
                            if (i == 0 && plusPreferences.getInt("unread_" + dialg.id, 0) == 1) {
                                i = 1;
                            }
                            if (i > 0) {
                                if (!Theme.plusTabsCountersCountChats) {
                                    unreadCount += i;
                                } else if (i > 0) {
                                    unreadCount++;
                                }
                                if (i > 0 && !isMuted) {
                                    allMuted = false;
                                }
                            }
                        }
                    }
                }
            }
            if (unreadCount != ((Tab) this.tabsArray.get(position)).getUnread() || this.force) {
                ((Tab) this.tabsArray.get(position)).setUnread(unreadCount);
                this.pagerSlidingTabStrip.updateCounter(position, unreadCount, allMuted, this.force);
                this.force = false;
            }
        }
    }
}
