package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlusChatsStatsActivity extends BaseFragment implements NotificationCenterDelegate, OnCancelListener {
    private static boolean dialogsDidLoad;
    private int adminChannelsCount;
    private int adminChannelsRow;
    private int adminGroupsCount;
    private int adminGroupsRow;
    private int adminHeaderDividerRow;
    private int adminHeaderRow;
    private int adminSuperGroupsCount;
    private int adminSuperGroupsRow;
    private List<Integer> arrayIds;
    private List<CharSequence> arrayType;
    private int botsCount;
    private int channelsCount;
    private int count;
    private Runnable dismissProgressRunnable;
    private int favsCount;
    private int groupsCount;
    private ListAdapter listAdapter;
    private int loadChatQ = 100;
    private int loadSize;
    private ArrayList<TL_dialog> other = new ArrayList();
    private int otherCount;
    private int otherPosition;
    private int ownChannelsCount;
    private int ownChannelsRow;
    private int ownGroupsCount;
    private int ownGroupsRow;
    private int ownHeaderDividerRow;
    private int ownHeaderRow;
    private int ownSuperGroupsCount;
    private int ownSuperGroupsRow;
    private ProgressDialog pDialog;
    private boolean progressCancelled;
    private int rowCount;
    private int secretsCount;
    private int superGroupsCount;
    private int totalBotsRow;
    private int totalChannelsRow;
    private int totalChatsCount;
    private int totalFavsRow;
    private int totalGroupsRow;
    private int totalHeaderRow;
    private int totalOtherRow;
    private int totalRow;
    private int totalSecretsRow;
    private int totalSuperGroupsRow;
    private int totalUsersRow;
    private int usersCount;

    private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == PlusChatsStatsActivity.this.totalHeaderRow || i == PlusChatsStatsActivity.this.totalRow || i == PlusChatsStatsActivity.this.totalUsersRow || i == PlusChatsStatsActivity.this.totalSecretsRow || i == PlusChatsStatsActivity.this.totalGroupsRow || i == PlusChatsStatsActivity.this.totalSuperGroupsRow || i == PlusChatsStatsActivity.this.totalChannelsRow || i == PlusChatsStatsActivity.this.totalBotsRow || i == PlusChatsStatsActivity.this.totalOtherRow || i == PlusChatsStatsActivity.this.totalFavsRow || i == PlusChatsStatsActivity.this.ownHeaderDividerRow || i == PlusChatsStatsActivity.this.ownHeaderRow || i == PlusChatsStatsActivity.this.ownGroupsRow || i == PlusChatsStatsActivity.this.ownSuperGroupsRow || i == PlusChatsStatsActivity.this.ownChannelsRow || i == PlusChatsStatsActivity.this.adminHeaderDividerRow || i == PlusChatsStatsActivity.this.adminHeaderRow || i == PlusChatsStatsActivity.this.adminGroupsRow || i == PlusChatsStatsActivity.this.adminSuperGroupsRow || i == PlusChatsStatsActivity.this.adminChannelsRow;
        }

        public int getCount() {
            return PlusChatsStatsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                return view == null ? new ShadowSectionCell(this.mContext) : view;
            } else {
                View headerCell;
                if (type == 1) {
                    if (view == null) {
                        headerCell = new HeaderCell(this.mContext);
                    }
                    ((HeaderCell) view).setTextSize(16);
                    int k;
                    if (i == PlusChatsStatsActivity.this.totalHeaderRow) {
                        k = PlusChatsStatsActivity.this.totalChatsCount;
                        ((HeaderCell) view).setText(LocaleController.getString("Total", R.string.Total) + (k == 0 ? "" : ": " + k) + (PlusChatsStatsActivity.this.progressCancelled ? " / " + LocaleController.getString("Cancelled", R.string.Cancelled) : ""));
                        return view;
                    } else if (i == PlusChatsStatsActivity.this.ownHeaderRow) {
                        k = (PlusChatsStatsActivity.this.ownGroupsCount + PlusChatsStatsActivity.this.ownSuperGroupsCount) + PlusChatsStatsActivity.this.ownChannelsCount;
                        ((HeaderCell) view).setText(LocaleController.getString("Created", R.string.Created) + (k == 0 ? "" : ": " + k));
                        return view;
                    } else if (i != PlusChatsStatsActivity.this.adminHeaderRow) {
                        return view;
                    } else {
                        k = (PlusChatsStatsActivity.this.adminGroupsCount + PlusChatsStatsActivity.this.adminSuperGroupsCount) + PlusChatsStatsActivity.this.adminChannelsCount;
                        ((HeaderCell) view).setText(LocaleController.getString("Administrator", R.string.Administrator) + (k == 0 ? "" : ": " + k));
                        return view;
                    }
                } else if (type == 2) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("chatsstats", 0);
                    if (view == null) {
                        headerCell = new TextSettingsCell(this.mContext);
                    }
                    TextSettingsCell textCell = (TextSettingsCell) view;
                    int counter = 0;
                    String title = "";
                    String key = "";
                    boolean div = false;
                    if (i == PlusChatsStatsActivity.this.totalRow) {
                        counter = PlusChatsStatsActivity.this.totalChatsCount;
                        key = "totalChatsCount";
                        title = LocaleController.getString("Total", R.string.Total);
                    } else if (i == PlusChatsStatsActivity.this.totalUsersRow) {
                        counter = PlusChatsStatsActivity.this.usersCount;
                        key = "usersCount";
                        title = LocaleController.getString("Users", R.string.Users);
                    } else if (i == PlusChatsStatsActivity.this.totalGroupsRow) {
                        counter = PlusChatsStatsActivity.this.groupsCount;
                        key = "groupsCount";
                        title = LocaleController.getString("Groups", R.string.Groups);
                    } else if (i == PlusChatsStatsActivity.this.totalSuperGroupsRow) {
                        counter = PlusChatsStatsActivity.this.superGroupsCount;
                        key = "superGroupsCount";
                        title = LocaleController.getString("SuperGroups", R.string.SuperGroups);
                    } else if (i == PlusChatsStatsActivity.this.totalChannelsRow) {
                        counter = PlusChatsStatsActivity.this.channelsCount;
                        key = "channelsCount";
                        title = LocaleController.getString("Channels", R.string.Channels);
                    } else if (i == PlusChatsStatsActivity.this.totalBotsRow) {
                        counter = PlusChatsStatsActivity.this.botsCount;
                        key = "botsCount";
                        div = true;
                        title = LocaleController.getString("Bots", R.string.Bots);
                    } else if (i == PlusChatsStatsActivity.this.totalSecretsRow) {
                        counter = PlusChatsStatsActivity.this.secretsCount;
                        key = "secretsCount";
                        title = LocaleController.getString("SecretChat", R.string.SecretChat);
                    } else if (i == PlusChatsStatsActivity.this.totalOtherRow) {
                        counter = PlusChatsStatsActivity.this.otherCount;
                        key = "otherCount";
                        title = LocaleController.getString("ReportChatOther", R.string.ReportChatOther);
                    } else if (i == PlusChatsStatsActivity.this.totalFavsRow) {
                        counter = PlusChatsStatsActivity.this.favsCount;
                        key = "favsCount";
                        title = LocaleController.getString("Favorites", R.string.Favorites);
                    } else if (i == PlusChatsStatsActivity.this.ownGroupsRow) {
                        counter = PlusChatsStatsActivity.this.ownGroupsCount;
                        key = "ownGroupsCount";
                        title = LocaleController.getString("Groups", R.string.Groups);
                    } else if (i == PlusChatsStatsActivity.this.ownSuperGroupsRow) {
                        counter = PlusChatsStatsActivity.this.ownSuperGroupsCount;
                        key = "ownSuperGroupsCount";
                        title = LocaleController.getString("SuperGroups", R.string.SuperGroups);
                    } else if (i == PlusChatsStatsActivity.this.ownChannelsRow) {
                        counter = PlusChatsStatsActivity.this.ownChannelsCount;
                        key = "ownChannelsCount";
                        title = LocaleController.getString("Channels", R.string.Channels);
                    } else if (i == PlusChatsStatsActivity.this.adminGroupsRow) {
                        counter = PlusChatsStatsActivity.this.adminGroupsCount;
                        key = "adminGroupsCount";
                        title = LocaleController.getString("Groups", R.string.Groups);
                    } else if (i == PlusChatsStatsActivity.this.adminSuperGroupsRow) {
                        counter = PlusChatsStatsActivity.this.adminSuperGroupsCount;
                        key = "adminSuperGroupsCount";
                        title = LocaleController.getString("SuperGroups", R.string.SuperGroups);
                    } else if (i == PlusChatsStatsActivity.this.adminChannelsRow) {
                        counter = PlusChatsStatsActivity.this.adminChannelsCount;
                        key = "adminChannelsCount";
                        title = LocaleController.getString("Channels", R.string.Channels);
                    }
                    if (title.isEmpty()) {
                        return view;
                    }
                    String str;
                    int c = 0;
                    if (PlusChatsStatsActivity.this.totalChatsCount > 0) {
                        c = counter - preferences.getInt(key, counter);
                    }
                    StringBuilder append = new StringBuilder().append(title);
                    if (c != 0) {
                        str = " (" + c + ")";
                    } else {
                        str = "";
                    }
                    textCell.setTextAndValue(append.append(str).toString(), String.format("%d", new Object[]{Integer.valueOf(counter)}), div);
                    return view;
                } else {
                    if (view == null) {
                        headerCell = new EmptyCell(this.mContext);
                    }
                    ((EmptyCell) view).setHeight(0);
                    return view;
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == PlusChatsStatsActivity.this.totalHeaderRow || i == PlusChatsStatsActivity.this.ownHeaderRow || i == PlusChatsStatsActivity.this.adminHeaderRow) {
                return 1;
            }
            if (i == PlusChatsStatsActivity.this.totalRow || i == PlusChatsStatsActivity.this.totalUsersRow || i == PlusChatsStatsActivity.this.totalSecretsRow || i == PlusChatsStatsActivity.this.totalGroupsRow || i == PlusChatsStatsActivity.this.totalSuperGroupsRow || i == PlusChatsStatsActivity.this.totalChannelsRow || i == PlusChatsStatsActivity.this.totalBotsRow || i == PlusChatsStatsActivity.this.totalOtherRow || i == PlusChatsStatsActivity.this.totalFavsRow || i == PlusChatsStatsActivity.this.ownGroupsRow || i == PlusChatsStatsActivity.this.ownSuperGroupsRow || i == PlusChatsStatsActivity.this.ownChannelsRow || i == PlusChatsStatsActivity.this.adminGroupsRow || i == PlusChatsStatsActivity.this.adminSuperGroupsRow || i == PlusChatsStatsActivity.this.adminChannelsRow) {
                return 2;
            }
            if (i == PlusChatsStatsActivity.this.ownHeaderDividerRow || i == PlusChatsStatsActivity.this.adminHeaderDividerRow) {
                return 0;
            }
            return -1;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id != NotificationCenter.dialogsNeedReload) {
            return;
        }
        if (MessagesController.getInstance().dialogs.isEmpty()) {
            completeTask();
        } else if (!MessagesController.getInstance().loadingDialogs) {
            if (MessagesController.getInstance().dialogsEndReached || this.progressCancelled) {
                completeTask();
                return;
            }
            int size = MessagesController.getInstance().dialogs.size();
            this.count = (int) (((double) this.loadChatQ) * Math.ceil((double) (size / this.loadChatQ)));
            if (this.loadSize < size) {
                this.loadSize = size;
                CharSequence title = LocaleController.getString("Loading", R.string.Loading) + " " + this.count;
                if (this.pDialog != null) {
                    this.pDialog.setMessage(title);
                }
                MessagesController.getInstance().loadDialogs(-1, this.loadChatQ, true);
                if (this.dismissProgressRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.dismissProgressRunnable);
                }
                Runnable anonymousClass1 = new Runnable() {
                    public void run() {
                        if (PlusChatsStatsActivity.this.dismissProgressRunnable == this) {
                            PlusChatsStatsActivity.this.completeTask();
                        }
                    }
                };
                this.dismissProgressRunnable = anonymousClass1;
                AndroidUtilities.runOnUIThread(anonymousClass1, 3000);
            }
        }
    }

    private void completeTask() {
        dismissProgress();
        loadAll();
    }

    private void dismissProgress() {
        if (this.dismissProgressRunnable != null) {
            this.dismissProgressRunnable = null;
        }
        if (this.pDialog != null) {
            this.pDialog.dismiss();
            this.pDialog = null;
        }
    }

    private void loadAll() {
        this.totalChatsCount = MessagesController.getInstance().dialogs.size();
        this.usersCount = MessagesController.getInstance().dialogsUsers.size();
        this.groupsCount = MessagesController.getInstance().dialogsGroups.size();
        this.superGroupsCount = MessagesController.getInstance().dialogsMegaGroups.size();
        this.channelsCount = MessagesController.getInstance().dialogsChannels.size();
        this.botsCount = MessagesController.getInstance().dialogsBots.size();
        this.favsCount = MessagesController.getInstance().dialogsFavs.size();
        loadAdminChats();
    }

    private void loadAdminChats() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (PlusChatsStatsActivity.this.loadSize <= PlusChatsStatsActivity.this.totalChatsCount) {
                    PlusChatsStatsActivity.this.other.clear();
                    MessagesController.getInstance().dialogsSecrets.clear();
                    MessagesController.getInstance().dialogsOwnGroups.clear();
                    MessagesController.getInstance().dialogsOwnSuperGroups.clear();
                    MessagesController.getInstance().dialogsOwnChannels.clear();
                    MessagesController.getInstance().dialogsAdminGroups.clear();
                    MessagesController.getInstance().dialogsAdminSuperGroups.clear();
                    MessagesController.getInstance().dialogsAdminChannels.clear();
                    for (int a = 0; a < MessagesController.getInstance().dialogs.size(); a++) {
                        TL_dialog d = (TL_dialog) MessagesController.getInstance().dialogs.get(a);
                        int high_id = (int) (d.id >> 32);
                        int lower_id = (int) d.id;
                        if (lower_id == 0 || high_id == 1) {
                            if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id)) != null) {
                                MessagesController.getInstance().dialogsSecrets.add(d);
                            }
                        } else if (DialogObject.isChannel(d)) {
                            TLRPC.Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                            if (chat != null) {
                                if (chat.megagroup) {
                                    if (chat.creator) {
                                        MessagesController.getInstance().dialogsOwnSuperGroups.add(d);
                                    } else if (chat.editor) {
                                        MessagesController.getInstance().dialogsAdminSuperGroups.add(d);
                                    }
                                } else if (chat.creator) {
                                    MessagesController.getInstance().dialogsOwnChannels.add(d);
                                } else if (chat.editor) {
                                    MessagesController.getInstance().dialogsAdminChannels.add(d);
                                }
                            }
                        } else if (lower_id < 0) {
                            TLRPC.Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                            if (chat != null) {
                                if (chat.creator) {
                                    MessagesController.getInstance().dialogsOwnGroups.add(d);
                                } else if (chat.admins_enabled && chat.admin) {
                                    MessagesController.getInstance().dialogsAdminGroups.add(d);
                                }
                            }
                        }
                    }
                }
                PlusChatsStatsActivity.this.secretsCount = MessagesController.getInstance().dialogsSecrets.size();
                PlusChatsStatsActivity.this.ownGroupsCount = MessagesController.getInstance().dialogsOwnGroups.size();
                PlusChatsStatsActivity.this.ownSuperGroupsCount = MessagesController.getInstance().dialogsOwnSuperGroups.size();
                PlusChatsStatsActivity.this.ownChannelsCount = MessagesController.getInstance().dialogsOwnChannels.size();
                PlusChatsStatsActivity.this.adminGroupsCount = MessagesController.getInstance().dialogsAdminGroups.size();
                PlusChatsStatsActivity.this.adminSuperGroupsCount = MessagesController.getInstance().dialogsAdminSuperGroups.size();
                PlusChatsStatsActivity.this.adminChannelsCount = MessagesController.getInstance().dialogsAdminChannels.size();
                PlusChatsStatsActivity.this.updateOther();
                if (PlusChatsStatsActivity.this.listAdapter != null) {
                    PlusChatsStatsActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateCounters() {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("chatsstats", 0).edit();
        editor.putInt("totalChatsCount", this.totalChatsCount);
        editor.putInt("usersCount", this.usersCount);
        editor.putInt("groupsCount", this.groupsCount);
        editor.putInt("superGroupsCount", this.superGroupsCount);
        editor.putInt("channelsCount", this.channelsCount);
        editor.putInt("botsCount", this.botsCount);
        editor.putInt("favsCount", this.favsCount);
        editor.putInt("secretsCount", this.secretsCount);
        editor.putInt("ownGroupsCount", this.ownGroupsCount);
        editor.putInt("ownSuperGroupsCount", this.ownSuperGroupsCount);
        editor.putInt("ownChannelsCount", this.ownChannelsCount);
        editor.putInt("adminGroupsCount", this.adminGroupsCount);
        editor.putInt("adminSuperGroupsCount", this.adminSuperGroupsCount);
        editor.putInt("adminChannelsCount", this.adminChannelsCount);
        editor.putLong("time", System.currentTimeMillis());
        editor.apply();
    }

    private void updateOther() {
        this.other = new ArrayList(MessagesController.getInstance().dialogs);
        this.other.removeAll(MessagesController.getInstance().dialogsUsers);
        this.other.removeAll(MessagesController.getInstance().dialogsGroups);
        this.other.removeAll(MessagesController.getInstance().dialogsMegaGroups);
        this.other.removeAll(MessagesController.getInstance().dialogsChannels);
        this.other.removeAll(MessagesController.getInstance().dialogsBots);
        this.otherCount = this.other.size();
        this.totalChatsCount = (((this.usersCount + this.groupsCount) + this.superGroupsCount) + this.channelsCount) + this.botsCount;
        if (this.otherCount <= 0) {
            this.totalOtherRow = -1;
        } else {
            this.totalOtherRow = this.otherPosition;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.totalHeaderRow = i;
        this.totalRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalUsersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalGroupsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalSuperGroupsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalChannelsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalBotsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalSecretsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherPosition = i;
        this.totalOtherRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalFavsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ownHeaderDividerRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ownHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ownGroupsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ownSuperGroupsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.ownChannelsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.adminHeaderDividerRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.adminHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.adminGroupsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.adminSuperGroupsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.adminChannelsRow = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        dismissProgress();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
        updateCounters();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setTitle(LocaleController.getString("ChatsCounters", R.string.ChatsCounters));
        long t = ApplicationLoader.applicationContext.getSharedPreferences("chatsstats", 0).getLong("time", -1);
        if (t != -1) {
            try {
                this.actionBar.setSubtitle(Html.fromHtml("<small>" + LocaleController.getString("LastAccess", R.string.LastAccess) + " " + new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.US).format(new Date(t)) + "</small>"));
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PlusChatsStatsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        ListView listView = new ListView(context);
        if (Theme.usePlusTheme) {
            listView.setBackgroundColor(Theme.prefBGColor);
        }
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(this.listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, -1, 51));
        this.pDialog = new ProgressDialog(context);
        this.pDialog.setCanceledOnTouchOutside(false);
        this.pDialog.setCancelable(true);
        this.pDialog.setOnCancelListener(this);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (PlusChatsStatsActivity.this.getParentActivity() != null) {
                    if (i == PlusChatsStatsActivity.this.ownGroupsRow) {
                        if (PlusChatsStatsActivity.this.ownGroupsCount > 0) {
                            PlusChatsStatsActivity.this.showChats(LocaleController.getString("Groups", R.string.Groups), MessagesController.getInstance().dialogsOwnGroups);
                        }
                    } else if (i == PlusChatsStatsActivity.this.ownSuperGroupsRow) {
                        if (PlusChatsStatsActivity.this.ownSuperGroupsCount > 0) {
                            PlusChatsStatsActivity.this.showChats(LocaleController.getString("SuperGroups", R.string.SuperGroups), MessagesController.getInstance().dialogsOwnSuperGroups);
                        }
                    } else if (i == PlusChatsStatsActivity.this.ownChannelsRow) {
                        if (PlusChatsStatsActivity.this.ownChannelsCount > 0) {
                            PlusChatsStatsActivity.this.showChats(LocaleController.getString("Channels", R.string.Channels), MessagesController.getInstance().dialogsOwnChannels);
                        }
                    } else if (i == PlusChatsStatsActivity.this.adminGroupsRow) {
                        if (PlusChatsStatsActivity.this.adminGroupsCount > 0) {
                            PlusChatsStatsActivity.this.showChats(LocaleController.getString("Groups", R.string.Groups), MessagesController.getInstance().dialogsAdminGroups);
                        }
                    } else if (i == PlusChatsStatsActivity.this.adminSuperGroupsRow) {
                        if (PlusChatsStatsActivity.this.adminSuperGroupsCount > 0) {
                            PlusChatsStatsActivity.this.showChats(LocaleController.getString("SuperGroups", R.string.SuperGroups), MessagesController.getInstance().dialogsAdminSuperGroups);
                        }
                    } else if (i == PlusChatsStatsActivity.this.adminChannelsRow) {
                        if (PlusChatsStatsActivity.this.adminChannelsCount > 0) {
                            PlusChatsStatsActivity.this.showChats(LocaleController.getString("Channels", R.string.Channels), MessagesController.getInstance().dialogsAdminChannels);
                        }
                    } else if (i == PlusChatsStatsActivity.this.totalSecretsRow) {
                        if (PlusChatsStatsActivity.this.secretsCount > 0) {
                            PlusChatsStatsActivity.this.showSecrets(LocaleController.getString("SecretChat", R.string.SecretChat), MessagesController.getInstance().dialogsSecrets);
                        }
                    } else if (i == PlusChatsStatsActivity.this.totalOtherRow && PlusChatsStatsActivity.this.otherCount > 0) {
                        PlusChatsStatsActivity.this.showOther(LocaleController.getString("ReportChatOther", R.string.ReportChatOther), PlusChatsStatsActivity.this.other);
                    }
                }
            }
        });
        if (MessagesController.getInstance().dialogsEndReached || dialogsDidLoad) {
            loadAll();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    PlusChatsStatsActivity.this.pDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                    PlusChatsStatsActivity.this.pDialog.show();
                    MessagesController.getInstance().loadDialogs(0, PlusChatsStatsActivity.this.loadChatQ, true);
                    PlusChatsStatsActivity.dialogsDidLoad = true;
                }
            }, 200);
        }
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void onCancel(DialogInterface dialog) {
        this.progressCancelled = true;
        dismissProgress();
        loadAll();
    }

    private void showChats(String title, ArrayList<TL_dialog> dlgs) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(title);
        List<CharSequence> array = new ArrayList();
        this.arrayIds = new ArrayList();
        for (int a = 0; a < dlgs.size(); a++) {
            TL_dialog d = (TL_dialog) dlgs.get(a);
            int high_id = (int) (d.id >> 32);
            int lower_id = (int) d.id;
            Chat chat = null;
            boolean isPublic = false;
            if (!(lower_id == 0 || high_id == 1)) {
                chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                isPublic = ChatObject.isChannel(chat) && chat.username != null && chat.username.length() > 0;
            }
            if (chat != null) {
                StringBuilder append = new StringBuilder().append(chat.title);
                String str = !ChatObject.isChannel(chat) ? "" : isPublic ? " (" + LocaleController.getString("ChannelTypePublic", R.string.ChannelTypePublic) + ")" : "";
                array.add(append.append(str).toString());
                this.arrayIds.add(Integer.valueOf(chat.id));
            }
        }
        builder.setItems((CharSequence[]) array.toArray(new CharSequence[array.size()]), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int id = ((Integer) PlusChatsStatsActivity.this.arrayIds.get(which)).intValue();
                Bundle args = new Bundle();
                args.putInt("chat_id", id);
                PlusChatsStatsActivity.this.presentFragment(new ChatActivity(args));
            }
        });
        builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    private void showSecrets(String title, ArrayList<TL_dialog> dlgs) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(title);
        List<CharSequence> array = new ArrayList();
        this.arrayIds = new ArrayList();
        for (int a = 0; a < dlgs.size(); a++) {
            TL_dialog d = (TL_dialog) dlgs.get(a);
            if (((int) d.id) == 0) {
                int high_id = (int) (d.id >> 32);
                EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user != null) {
                        array.add(UserObject.getUserName(user) + (encryptedChat instanceof TL_encryptedChat ? "" : " (" + LocaleController.getString("Cancelled", R.string.Cancelled) + ")"));
                        this.arrayIds.add(Integer.valueOf(high_id));
                    }
                }
            }
        }
        builder.setItems((CharSequence[]) array.toArray(new CharSequence[array.size()]), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int id = ((Integer) PlusChatsStatsActivity.this.arrayIds.get(which)).intValue();
                Bundle args = new Bundle();
                args.putInt("enc_id", id);
                PlusChatsStatsActivity.this.presentFragment(new ChatActivity(args));
            }
        });
        builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    private void showOther(String title, ArrayList<TL_dialog> dlgs) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(title);
        List<CharSequence> array = new ArrayList();
        this.arrayIds = new ArrayList();
        this.arrayType = new ArrayList();
        for (int a = 0; a < dlgs.size(); a++) {
            TL_dialog d = (TL_dialog) dlgs.get(a);
            int high_id = (int) (d.id >> 32);
            int lower_id = (int) d.id;
            if (lower_id == 0 || high_id == 1) {
                array.add(high_id + "");
                this.arrayIds.add(Integer.valueOf(high_id));
                this.arrayType.add("enc_id");
            } else {
                Object obj;
                array.add(lower_id + "");
                this.arrayIds.add(Integer.valueOf(lower_id));
                List list = this.arrayType;
                if (lower_id < 0) {
                    obj = "chat_id";
                } else {
                    obj = "user_id";
                }
                list.add(obj);
            }
        }
        builder.setItems((CharSequence[]) array.toArray(new CharSequence[array.size()]), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int id = ((Integer) PlusChatsStatsActivity.this.arrayIds.get(which)).intValue();
                Bundle args = new Bundle();
                args.putInt(((CharSequence) PlusChatsStatsActivity.this.arrayType.get(which)).toString(), id);
                PlusChatsStatsActivity.this.presentFragment(new ChatActivity(args));
            }
        });
        builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (Theme.usePlusTheme) {
            updateTheme();
        }
        fixLayout();
    }

    private void updateTheme() {
        this.actionBar.setBackgroundColor(Theme.prefActionbarColor);
        this.actionBar.setTitleColor(Theme.prefActionbarTitleColor);
        Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        back.setColorFilter(Theme.prefActionbarIconsColor, Mode.MULTIPLY);
        this.actionBar.setBackButtonDrawable(back);
        getParentActivity().getResources().getDrawable(R.drawable.ic_ab_other).setColorFilter(Theme.prefActionbarIconsColor, Mode.MULTIPLY);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (PlusChatsStatsActivity.this.fragmentView != null) {
                        PlusChatsStatsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }
}
