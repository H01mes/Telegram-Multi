package org.telegram.ui.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantEditor;
import org.telegram.tgnet.TLRPC.TL_channelParticipantModerator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChatDialogsView extends FrameLayout {
    private int avatarSize = 40;
    private ImageView btn;
    private int chat_id;
    private int classGuid;
    private int creatorID;
    private Chat currentChat;
    private ChatDialogsViewDelegate delegate;
    private Adapter dialogsAdapter;
    private int dialogsType;
    private boolean disableLongCick;
    private ChatFull info;
    private LinearLayoutManager layoutManager;
    private int listHeight = ((this.avatarSize + this.textSize) + 25);
    private RecyclerListView listView;
    private int listWidth = (this.avatarSize + 20);
    private int loadMoreMembersRow;
    private boolean loadingUsers;
    private Adapter membersAdapter;
    private int membersCount;
    private ArrayList<Integer> membersMap;
    private ChatActivity parentFragment;
    private boolean refresh;
    private boolean showMembers;
    private ArrayList<Integer> sortedUsers;
    private int textSize = 10;
    private TextView tv;
    private boolean vertical;
    private boolean visible;

    public class ChatDialogCell extends FrameLayout {
        private ImageView adminImage;
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private Drawable countDrawable;
        private Drawable countDrawableGrey;
        private StaticLayout countLayout;
        private TextPaint countPaint;
        private int countWidth;
        private long dialog_id;
        private boolean hideCounter;
        private BackupImageView imageView;
        private int lastUnreadCount;
        private TextView nameTextView;

        public ChatDialogCell(Context context) {

            super(context);
            int i;
            SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
            this.imageView = new BackupImageView(context);
            this.imageView.setRoundRadius(AndroidUtilities.dp(54.0f));
            this.avatarDrawable.setRadius(AndroidUtilities.dp(54.0f));
            addView(this.imageView, LayoutHelper.createFrame(ChatDialogsView.this.avatarSize, (float) ChatDialogsView.this.avatarSize, 49, 0.0f, 5.0f, 0.0f, 0.0f));
            this.nameTextView = new TextView(context);
            TextView textView = this.nameTextView;
            if (Theme.usePlusTheme) {
                i = Theme.chatQuickBarNamesColor;
            } else {
                i = Theme.getColor(Theme.key_chat_goDownButtonIcon);
            }
            textView.setTextColor(i);
            this.nameTextView.setTextSize(1, (float) ChatDialogsView.this.textSize);
            this.nameTextView.setMaxLines(2);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(2);
            this.nameTextView.setEllipsize(TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, (float) (ChatDialogsView.this.avatarSize + 5), 6.0f, 0.0f));
            if (this.countDrawable == null) {
                this.countDrawable = getResources().getDrawable(R.drawable.bluecounter);
                this.countDrawableGrey = getResources().getDrawable(R.drawable.bluecounter);
                this.countDrawable.setColorFilter(Theme.usePlusTheme ? themePrefs.getInt("chatsCountBGColor", Theme.defColor) : Theme.getColor(Theme.key_chat_goDownButtonCounterBackground), Mode.SRC_IN);
                this.countDrawableGrey.setColorFilter(Theme.usePlusTheme ? themePrefs.getInt("chatsCountSilentBGColor", themePrefs.getInt("chatsCountBGColor", -4605511)) : Theme.getColor(Theme.key_chat_goDownButtonCounterBackground), Mode.SRC_IN);
                this.countPaint = new TextPaint(1);
                this.countPaint.setTextSize((float) AndroidUtilities.dp(11.0f));
                this.countPaint.setColor(Theme.usePlusTheme ? themePrefs.getInt("chatsCountColor", -1) : Theme.getColor(Theme.key_chat_goDownButtonCounter));
                this.countPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            }
            this.adminImage = new ImageView(context);
            this.adminImage.setVisibility(GONE);
            addView(this.adminImage, LayoutHelper.createFrame(16, 16, 53));
        }

        public void setIsAdmin(int value) {
            if (this.adminImage != null) {
                this.adminImage.setVisibility(value != 0 ? VISIBLE : GONE);
                if (value == 1) {
                    this.adminImage.setImageResource(R.drawable.admin_star);
                    this.adminImage.setColorFilter(Theme.profileRowCreatorStarColor, Mode.SRC_IN);
                } else if (value == 2) {
                    this.adminImage.setImageResource(R.drawable.admin_star);
                    this.adminImage.setColorFilter(Theme.profileRowAdminStarColor, Mode.SRC_IN);
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
                getBackground().setHotspot(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }

        public void checkUnreadCounter(int mask) {
            if (mask == 0 || (mask & 256) != 0 || (mask & 2048) != 0) {
                TL_dialog dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
                if (dialog == null || dialog.unread_count == 0) {
                    if (this.countLayout != null) {
                        if (mask != 0) {
                            invalidate();
                        }
                        this.lastUnreadCount = 0;
                        this.countLayout = null;
                    }
                } else if (this.lastUnreadCount != dialog.unread_count) {
                    this.lastUnreadCount = dialog.unread_count;
                    String countString = String.format("%d", new Object[]{Integer.valueOf(this.lastUnreadCount)});
                    if (this.lastUnreadCount > 99) {
                        countString = "+99";
                    }
                    this.countWidth = Math.max(AndroidUtilities.dp(5.0f), (int) Math.ceil((double) this.countPaint.measureText(countString)));
                    this.countLayout = new StaticLayout(countString, this.countPaint, this.countWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    if (mask != 0) {
                        invalidate();
                    }
                }
            }
        }

        public void hideCounter(boolean hide) {
            this.hideCounter = hide;
        }

        public void setDialog(long id) {
            this.dialog_id = id;
            TLObject photo = null;
            int lower_id = (int) id;
            int high_id = (int) (id >> 32);
            User user = null;
            Chat chat = null;
            if (lower_id == 0) {
                EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
                }
            } else if (lower_id > 0) {
                user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
            } else {
                chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
            }
            if (user != null) {
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                if (user.photo != null) {
                    photo = user.photo.photo_small;
                }
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
                this.avatarDrawable.setInfo(chat);
                if (chat.photo != null) {
                    photo = chat.photo.photo_small;
                }
            } else {
                this.nameTextView.setText("");
            }
            this.imageView.setImage(photo, "50_50", this.avatarDrawable);
            if (!this.hideCounter) {
                checkUnreadCounter(0);
            }
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean result = super.drawChild(canvas, child, drawingTime);
            if (child == this.imageView && this.countLayout != null) {
                int top = AndroidUtilities.dp(2.0f);
                int left = AndroidUtilities.dp(8.0f);
                int x = left - AndroidUtilities.dp(5.5f);
                if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                    this.countDrawableGrey.setBounds(x, AndroidUtilities.dp(2.0f) + top, (this.countWidth + x) + AndroidUtilities.dp(11.0f), (this.countDrawableGrey.getIntrinsicHeight() + top) - AndroidUtilities.dp(4.0f));
                    this.countDrawableGrey.draw(canvas);
                } else {
                    this.countDrawable.setBounds(x, AndroidUtilities.dp(2.0f) + top, (this.countWidth + x) + AndroidUtilities.dp(11.0f), (this.countDrawable.getIntrinsicHeight() + top) - AndroidUtilities.dp(4.0f));
                    this.countDrawable.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) left, (float) (AndroidUtilities.dp(4.0f) + top));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            return result;
        }

        public long getDialogId() {
            return this.dialog_id;
        }
    }

    public interface ChatDialogsViewDelegate {
        void didLongPressedOnSubDialog(long j, int i);

        void didPressedOnBtn(boolean z);

        void didPressedOnSubDialog(long j);
    }

    public class OnSwipeTouchListener implements OnTouchListener {
        private final GestureDetector gestureDetector;

        private final class GestureListener extends SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 10;
            private static final int SWIPE_VELOCITY_THRESHOLD = 10;

            private GestureListener() {
            }

            public boolean onDown(MotionEvent e) {
                return false;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                ChatDialogsView.this.disableLongCick = true;
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > 10.0f && Math.abs(velocityX) > 10.0f) {
                        if (diffX > 0.0f) {
                            OnSwipeTouchListener.this.onSwipeRight();
                        } else {
                            OnSwipeTouchListener.this.onSwipeLeft();
                        }
                    }
                } else if (Math.abs(diffY) > 10.0f && Math.abs(velocityY) > 10.0f) {
                    if (diffY > 0.0f) {
                        OnSwipeTouchListener.this.onSwipeBottom();
                    } else {
                        OnSwipeTouchListener.this.onSwipeTop();
                    }
                }
                return true;
            }
        }

        public OnSwipeTouchListener(Context ctx) {
            this.gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        public boolean onTouch(View v, MotionEvent event) {
            return this.gestureDetector.onTouchEvent(event);
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

    public class ChatDialogsAdapter extends Adapter {
        private int ChatDialogRow;
        private int InvisibleRow;
        private int NoChatsRow;
        private long chatId;
        private Context mContext;

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

        private ChatDialogsAdapter(Context context, long chat_id) {
            this.InvisibleRow = 0;
            this.NoChatsRow = 1;
            this.ChatDialogRow = 2;
            this.mContext = context;
            this.chatId = chat_id;
        }

        public int getItemCount() {
            return getDialogsArray().size();
        }

        private ArrayList<TL_dialog> getDialogsArray() {
            switch (ChatDialogsView.this.dialogsType) {
                case 0:
                    return MessagesController.getInstance().dialogs;
                case 3:
                    return MessagesController.getInstance().dialogsUsers;
                case 4:
                    return MessagesController.getInstance().dialogsGroups;
                case 5:
                    return MessagesController.getInstance().dialogsChannels;
                case 6:
                    return MessagesController.getInstance().dialogsBots;
                case 7:
                    return MessagesController.getInstance().dialogsMegaGroups;
                case 8:
                    return MessagesController.getInstance().dialogsFavs;
                default:
                    return MessagesController.getInstance().dialogs;
            }
        }

        public long getItemId(int i) {
            ArrayList<TL_dialog> arrayList = getDialogsArray();
            if (i < 0 || i >= arrayList.size()) {
                return 0;
            }
            return ((TL_dialog) arrayList.get(i)).id;
        }

        public int getItemViewType(int i) {
            if (this.chatId != getItemId(i)) {
                return this.ChatDialogRow;
            }
            if (ChatDialogsView.this.dialogsType == 0 || getItemCount() > 1) {
                return this.InvisibleRow;
            }
            return this.NoChatsRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int i = -1;
            View view = null;
            if (viewType == this.ChatDialogRow) {
                view = new ChatDialogCell(this.mContext);
                view.setLayoutParams(new LayoutParams(AndroidUtilities.dp((float) ChatDialogsView.this.listWidth), AndroidUtilities.dp((float) ChatDialogsView.this.listHeight)));
            } else if (viewType == this.NoChatsRow) {
                view = new TextView(this.mContext);
                int dp = Theme.plusVerticalQuickBar ? AndroidUtilities.dp((float) ChatDialogsView.this.listWidth) : -1;
                if (!Theme.plusVerticalQuickBar) {
                    i = AndroidUtilities.dp((float) ChatDialogsView.this.listHeight);
                }
                view.setLayoutParams(new LayoutParams(dp, i));
            } else if (viewType == this.InvisibleRow) {
                view = new View(this.mContext);
                view.setLayoutParams(new LayoutParams(0, 0));
                view.setVisibility(GONE);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int i) {
            if (holder.getItemViewType() == this.ChatDialogRow) {
                long id = getItemId(i);
                ChatDialogCell cell = (ChatDialogCell) holder.itemView;
                cell.setTag(Long.valueOf(id));
                cell.setDialog(id);
            } else if (holder.getItemViewType() == this.NoChatsRow) {
                TextView tv =(TextView) holder.itemView;
                tv.setGravity(17);
                tv.setText(LocaleController.formatString("NoChatsYet", R.string.NoChatsYet, Integer.valueOf(getTitleRes())));
            }
        }

        private int getTitleRes() {
            switch (ChatDialogsView.this.dialogsType) {
                case 3:
                    return R.string.Users;
                case 4:
                    return R.string.Groups;
                case 5:
                    return R.string.Channels;
                case 6:
                    return R.string.Bots;
                case 7:
                    return R.string.SuperGroups;
                case 8:
                    return R.string.Favorites;
                default:
                    return R.string.ChatHints;
            }
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ChatDialogCell(this.mContext);
            view.setLayoutParams(new LayoutParams(AndroidUtilities.dp((float) ChatDialogsView.this.listWidth), AndroidUtilities.dp((float) ChatDialogsView.this.listHeight)));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int i) {
            ChatParticipant part;
            if (ChatDialogsView.this.sortedUsers.isEmpty()) {
                part = (ChatParticipant) ChatDialogsView.this.info.participants.participants.get(i);
            } else {
                part = (ChatParticipant) ChatDialogsView.this.info.participants.participants.get(((Integer) ChatDialogsView.this.sortedUsers.get(i)).intValue());
            }
            if (part != null) {
                ChatDialogCell cell =(ChatDialogCell) holder.itemView;
                if (part instanceof TL_chatChannelParticipant) {
                    ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) part).channelParticipant;
                    if (channelParticipant instanceof TL_channelParticipantCreator) {
                        cell.setIsAdmin(1);
                    } else if ((channelParticipant instanceof TL_channelParticipantEditor) || (channelParticipant instanceof TL_channelParticipantModerator)) {
                        cell.setIsAdmin(2);
                    } else {
                        cell.setIsAdmin(0);
                    }
                } else if (part instanceof TL_chatParticipantCreator) {
                    cell.setIsAdmin(1);
                } else if (ChatDialogsView.this.currentChat.admins_enabled && (part instanceof TL_chatParticipantAdmin)) {
                    cell.setIsAdmin(2);
                } else {
                    cell.setIsAdmin(0);
                }
                long did = (long) part.user_id;
                cell.setTag(Long.valueOf(did));
                cell.hideCounter(true);
                cell.setDialog(did);
            }
        }

        public int getItemCount() {
            if (ChatDialogsView.this.currentChat.megagroup) {
                return (ChatDialogsView.this.info == null || ChatDialogsView.this.info.participants == null || ChatDialogsView.this.info.participants.participants.isEmpty()) ? 0 : ChatDialogsView.this.info.participants.participants.size();
            } else {
                return ChatDialogsView.this.sortedUsers.size();
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }

        private int getTitleRes() {
            return R.string.ChannelMembers;
        }
    }

    public void setChatInfo(ChatFull chatInfo) {
        if (this.showMembers) {
            this.info = chatInfo;
            if (this.currentChat == null) {
                this.currentChat = this.parentFragment.getCurrentChat();
            }
            if (this.currentChat.megagroup) {
                this.membersCount = this.info.participants_count;
                fetchUsersFromChannelInfo();
            } else {
                this.membersCount = this.info.participants.participants.size();
            }
            if (this.membersCount <= 1) {
                this.showMembers = false;
                if (this.listView.getAdapter() != this.dialogsAdapter) {
                    this.listView.setAdapter(this.dialogsAdapter);
                    this.dialogsAdapter.notifyDataSetChanged();
                }
            }
            updateOnlineCount();
        }
    }

    private void getChannelParticipants(boolean reload) {
        int i = 0;
        if (!this.loadingUsers && this.membersMap != null && this.info != null) {
            final int delay;
            this.loadingUsers = true;
            if (this.membersMap.isEmpty() || !reload) {
                delay = 0;
            } else {
                delay = 300;
            }
            final TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = MessagesController.getInputChannel(this.chat_id);
            req.filter = new TL_channelParticipantsRecent();
            if (!reload) {
                i = this.membersMap.size();
            }
            req.offset = i;
            req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
                                MessagesController.getInstance().putUsers(res.users, false);
                                if (req.offset == 0) {
                                    ChatDialogsView.this.membersMap.clear();
                                    ChatDialogsView.this.info.participants = new TL_chatParticipants();
                                    MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                                    MessagesStorage.getInstance().updateChannelUsers(ChatDialogsView.this.chat_id, res.participants);
                                }
                                for (int a = 0; a < res.participants.size(); a++) {
                                    TL_chatChannelParticipant participant = new TL_chatChannelParticipant();
                                    participant.channelParticipant = (ChannelParticipant) res.participants.get(a);
                                    participant.inviter_id = participant.channelParticipant.inviter_id;
                                    participant.user_id = participant.channelParticipant.user_id;
                                    participant.date = participant.channelParticipant.date;
                                    if (!ChatDialogsView.this.membersMap.contains(Integer.valueOf(participant.user_id))) {
                                        ChatDialogsView.this.info.participants.participants.add(participant);
                                        ChatDialogsView.this.membersMap.add(Integer.valueOf(participant.user_id));
                                    }
                                }
                            }
                            ChatDialogsView.this.updateOnlineCount();
                            ChatDialogsView.this.loadingUsers = false;
                            if (!(ChatDialogsView.this.info == null || ChatDialogsView.this.info.participants == null || ChatDialogsView.this.info.participants.participants.isEmpty() || ChatDialogsView.this.membersMap.size() <= ChatDialogsView.this.loadMoreMembersRow)) {
                                ChatDialogsView.this.loadMoreMembersRow = ChatDialogsView.this.info.participants.participants.size();
                            }
                            if (ChatDialogsView.this.listView.getAdapter() != null) {
                                ChatDialogsView.this.listView.getAdapter().notifyDataSetChanged();
                            }
                        }
                    }, (long) delay);
                }
            }), this.classGuid);
        }
    }

    private void fetchUsersFromChannelInfo() {
        if ((this.info instanceof TL_channelFull) && this.info.participants != null) {
            for (int a = 0; a < this.info.participants.participants.size(); a++) {
                ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(a);
                if (((TL_chatChannelParticipant) chatParticipant).channelParticipant instanceof TL_channelParticipantCreator) {
                    this.creatorID = chatParticipant.user_id;
                }
            }
        }
    }

    private void updateOnlineCount() {
        int currentTime = ConnectionsManager.getInstance().getCurrentTime();
        this.sortedUsers.clear();
        if ((this.info instanceof TL_chatFull) || ((this.info instanceof TL_channelFull) && this.info.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.info.participants != null)) {
            for (int a = 0; a < this.info.participants.participants.size(); a++) {
                ChatParticipant participant = (ChatParticipant) this.info.participants.participants.get(a);
                User user = MessagesController.getInstance().getUser(Integer.valueOf(participant.user_id));
                if (user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getClientUserId()) || user.status.expires <= 10000)) {
                    this.sortedUsers.add(Integer.valueOf(a));
                } else {
                    this.sortedUsers.add(Integer.valueOf(a));
                }
                if (participant instanceof TL_chatParticipantCreator) {
                    this.creatorID = participant.user_id;
                }
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator<Integer>() {
                    public int compare(Integer lhs, Integer rhs) {
                        User user1 = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) ChatDialogsView.this.info.participants.participants.get(rhs.intValue())).user_id));
                        User user2 = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) ChatDialogsView.this.info.participants.participants.get(lhs.intValue())).user_id));
                        int status1 = 0;
                        int status2 = 0;
                        if (!(user1 == null || user1.status == null)) {
                            if (user1.id == UserConfig.getClientUserId()) {
                                status1 = ConnectionsManager.getInstance().getCurrentTime() + 50000;
                            } else {
                                status1 = user1.status.expires;
                            }
                            if (user1.id == ChatDialogsView.this.creatorID) {
                                status1 = (ConnectionsManager.getInstance().getCurrentTime() + 50000) - 100;
                            }
                        }
                        if (!(user2 == null || user2.status == null)) {
                            if (user2.id == UserConfig.getClientUserId()) {
                                status2 = ConnectionsManager.getInstance().getCurrentTime() + 50000;
                            } else {
                                status2 = user2.status.expires;
                            }
                            if (user2.id == ChatDialogsView.this.creatorID) {
                                status2 = (ConnectionsManager.getInstance().getCurrentTime() + 50000) - 100;
                            }
                        }
                        if (status1 <= 0 || status2 <= 0) {
                            if (status1 >= 0 || status2 >= 0) {
                                if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
                                    return -1;
                                }
                                if ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) {
                                    return 0;
                                }
                                return 1;
                            } else if (status1 > status2) {
                                return 1;
                            } else {
                                if (status1 < status2) {
                                    return -1;
                                }
                                return 0;
                            }
                        } else if (status1 > status2) {
                            return 1;
                        } else {
                            if (status1 < status2) {
                                return -1;
                            }
                            return 0;
                        }
                    }
                });
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.listView.getAdapter() != null) {
                this.listView.getAdapter().notifyItemRangeChanged(0, this.sortedUsers.size());
            }
        }
    }

    public void setDelegate(ChatDialogsViewDelegate delegate) {
        this.delegate = delegate;
    }

    public ChatDialogsView(Context context, BaseFragment fragment, long chat_id) {
        super(context);
        float f;
        float f2;
        float f3;
        this.parentFragment = (ChatActivity) fragment;
        this.vertical = Theme.plusVerticalQuickBar;
        this.visible = false;
        this.refresh = false;
        this.dialogsType = Theme.plusQuickBarDialogType;
        if (this.vertical) {
            setTranslationX((float) AndroidUtilities.dp((float) this.listWidth));
        } else {
            setTranslationY((float) (-AndroidUtilities.dp((float) this.listHeight)));
        }
        ((ViewGroup) fragment.getFragmentView()).setClipToPadding(false);
        setBackgroundColor(0);
        if (chat_id < 0 && Theme.plusQuickBarShowMembers) {
            this.currentChat = this.parentFragment.getCurrentChat();
            if (this.currentChat != null && (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup)) {
                this.showMembers = true;
                this.sortedUsers = new ArrayList();
                if (this.currentChat.megagroup) {
                    this.loadMoreMembersRow = 32;
                    this.membersMap = new ArrayList();
                    this.classGuid = this.parentFragment.getCurrentClassGuid();
                    this.chat_id = -((int) chat_id);
                    getChannelParticipants(true);
                } else {
                    this.membersMap = null;
                }
                updateOnlineCount();
            }
        }
        if (!this.showMembers && this.dialogsType == -1) {
            this.dialogsType = 0;
            Theme.plusQuickBarDialogType = 0;
        }
        this.listView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent e) {
                if (!(getParent() == null || getParent().getParent() == null)) {
                    getParent().getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(e);
            }
        };
        this.listView.setTag(Integer.valueOf(9));
        this.listView.setBackgroundColor(Theme.usePlusTheme ? Theme.chatQuickBarColor : Theme.getColor(Theme.key_chat_goDownButton));
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.layoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager.setOrientation(this.vertical ? 1 : 0);
        this.listView.setLayoutManager(this.layoutManager);
        this.dialogsAdapter = new ChatDialogsAdapter(context, chat_id);
        this.membersAdapter = new ListAdapter(context);
        RecyclerListView recyclerListView = this.listView;
        Adapter adapter = (!this.showMembers || this.dialogsType >= 0) ? this.dialogsAdapter : this.membersAdapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (ChatDialogsView.this.delegate != null) {
                    try {
                        ChatDialogsView.this.delegate.didPressedOnSubDialog(((Long) view.getTag()).longValue());
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemClick(View view, int position) {
                if (ChatDialogsView.this.delegate != null) {
                    try {
                        ChatDialogsView.this.delegate.didLongPressedOnSubDialog(((Long) view.getTag()).longValue(), ChatDialogsView.this.dialogsType);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                return true;
            }
        });
        addView(this.listView, LayoutHelper.createFrame(-1, -1, 5));
        this.btn = new ImageView(context);
        ImageView imageView = this.btn;
        int color = Theme.usePlusTheme ? Theme.chatQuickBarNamesColor != -14606047 ? Theme.chatQuickBarNamesColor : Theme.defColor : Theme.getColor(Theme.key_chat_goDownButtonIcon);
        imageView.setColorFilter(color, Mode.SRC_IN);
        this.btn.setImageResource(this.vertical ? R.drawable.ic_bar_open : R.drawable.search_down);
        this.btn.setScaleType(ScaleType.CENTER);
        Drawable d = context.getResources().getDrawable(this.vertical ? R.drawable.ic_bar_bg_v : R.drawable.ic_bar_bg);
        d.setColorFilter(Theme.usePlusTheme ? Theme.chatQuickBarColor : Theme.getColor(Theme.key_chat_goDownButton), Mode.MULTIPLY);
        this.btn.setBackgroundDrawable(d);
        View view = this.btn;
        int i = this.vertical ? Theme.plusCenterQuickBarBtn ? 16 : 80 : Theme.plusCenterQuickBarBtn ? 1 : 53;
        if (this.vertical) {
            f = 0.0f;
        } else {
            f = (float) this.listHeight;
        }
        if (this.vertical) {
            f2 = (float) this.listWidth;
        } else {
            f2 = 0.0f;
        }
        if (this.vertical) {
            int i2;
            if (Theme.plusCenterQuickBarBtn) {
                i2 = 0;
            } else {
                i2 = this.listWidth;
            }
            f3 = (float) i2;
        } else {
            f3 = 0.0f;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, 0.0f, f, f2, f3));
        this.btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ChatDialogsView.this.btnPressed();
            }
        });
        this.btn.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (!ChatDialogsView.this.visible || ChatDialogsView.this.disableLongCick) {
                    return false;
                }
                ChatDialogsView.this.changeDialogType();
                return true;
            }
        });
        this.btn.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeTop() {
                if (ChatDialogsView.this.visible && !ChatDialogsView.this.vertical) {
                    ChatDialogsView.this.btnPressed();
                }
            }

            public void onSwipeRight() {
                if (ChatDialogsView.this.visible && ChatDialogsView.this.vertical) {
                    ChatDialogsView.this.btnPressed();
                }
            }

            public void onSwipeLeft() {
                if (!ChatDialogsView.this.visible && ChatDialogsView.this.vertical) {
                    ChatDialogsView.this.btnPressed();
                }
            }

            public void onSwipeBottom() {
                if (!ChatDialogsView.this.visible && !ChatDialogsView.this.vertical) {
                    ChatDialogsView.this.btnPressed();
                }
            }
        });
        this.tv = new TextView(context);
        this.tv.setTextColor(Theme.usePlusTheme ? Theme.chatQuickBarNamesColor : Theme.getColor(Theme.key_chat_goDownButtonIcon));
        this.tv.setTextSize(1, 9.0f);
        this.tv.setBackgroundColor(Theme.usePlusTheme ? Theme.chatQuickBarColor : Theme.getColor(Theme.key_chat_goDownButton));
        this.tv.setVisibility(INVISIBLE);
        addView(this.tv, LayoutHelper.createFrame(-2, -2.0f, 49, this.vertical ? (float) AndroidUtilities.dp(4.0f) : 0.0f, 0.0f, 0.0f, 0.0f));
        if (this.showMembers) {
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (ChatDialogsView.this.listView.getAdapter() == ChatDialogsView.this.membersAdapter && ChatDialogsView.this.membersMap != null && ChatDialogsView.this.layoutManager.findLastVisibleItemPosition() > ChatDialogsView.this.loadMoreMembersRow - 5) {
                        ChatDialogsView.this.getChannelParticipants(false);
                    }
                }
            });
        }
    }

    public void changeDialogType() {
        switch (this.dialogsType) {
            case -1:
                this.dialogsType = 0;
                Theme.plusQuickBarDialogType = 0;
                if (this.showMembers && this.listView.getAdapter() != this.dialogsAdapter) {
                    this.listView.setAdapter(this.dialogsAdapter);
                    break;
                }
            case 0:
                this.dialogsType = 8;
                Theme.plusQuickBarDialogType = 8;
                if (MessagesController.getInstance().dialogsFavs.size() == 0) {
                    changeDialogType();
                    break;
                }
                break;
            case 3:
                this.dialogsType = 4;
                Theme.plusQuickBarDialogType = 4;
                if (MessagesController.getInstance().dialogsGroups.size() == 0) {
                    changeDialogType();
                    break;
                }
                break;
            case 4:
                this.dialogsType = 7;
                Theme.plusQuickBarDialogType = 7;
                if (MessagesController.getInstance().dialogsMegaGroups.size() == 0) {
                    changeDialogType();
                    break;
                }
                break;
            case 5:
                this.dialogsType = 6;
                Theme.plusQuickBarDialogType = 6;
                if (MessagesController.getInstance().dialogsBots.size() == 0) {
                    changeDialogType();
                    break;
                }
                break;
            case 6:
                if (!this.showMembers) {
                    this.dialogsType = 0;
                    Theme.plusQuickBarDialogType = 0;
                    if (this.listView.getAdapter() != this.dialogsAdapter) {
                        this.listView.setAdapter(this.dialogsAdapter);
                        break;
                    }
                }
                this.dialogsType = -1;
                Theme.plusQuickBarDialogType = -1;
                if (this.listView.getAdapter() != this.membersAdapter) {
                    this.listView.setAdapter(this.membersAdapter);
                    break;
                }
                break;
            case 7:
                this.dialogsType = 5;
                Theme.plusQuickBarDialogType = 5;
                if (MessagesController.getInstance().dialogsChannels.size() == 0) {
                    changeDialogType();
                    break;
                }
                break;
            case 8:
                this.dialogsType = 3;
                Theme.plusQuickBarDialogType = 3;
                if (MessagesController.getInstance().dialogsUsers.size() == 0) {
                    changeDialogType();
                    break;
                }
                break;
            default:
                this.dialogsType = 0;
                Theme.plusQuickBarDialogType = 0;
                break;
        }
        int title = R.string.ChatHints;
        if (this.listView != null) {
            if (this.listView.getAdapter() != null) {
                this.listView.getAdapter().notifyDataSetChanged();
                if (this.listView.getAdapter() instanceof ChatDialogsAdapter) {
                    title = ((ChatDialogsAdapter) this.listView.getAdapter()).getTitleRes();
                } else {
                    title = ((ListAdapter) this.listView.getAdapter()).getTitleRes();
                }
            }
            this.listView.scrollToPosition(0);
        }
        this.tv.setText(title);
        this.tv.setVisibility(VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500);
        animation.setStartOffset(1000);
        this.tv.startAnimation(animation);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                ChatDialogsView.this.tv.setVisibility(INVISIBLE);
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }
        });
        ApplicationLoader.applicationContext.getSharedPreferences("plusconfig", 0).edit().putInt("quickBarDialogType", this.dialogsType).apply();
    }

    public void refreshList() {
        if (this.listView.getAdapter() != null) {
            this.listView.getAdapter().notifyDataSetChanged();
        }
    }

    public void btnPressed() {
        if (this.delegate != null) {
            this.delegate.didPressedOnBtn(this.visible);
        }
        if (!this.visible) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ChatDialogsView.this.disableLongCick = false;
                }
            }, 500);
        }
        this.visible = !this.visible;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.refresh) {
            if (this.listView.getAdapter() != null) {
                this.listView.getAdapter().notifyDataSetChanged();
            }
            this.refresh = false;
        }
    }

    public void onDestroy() {
        this.delegate = null;
        this.dialogsAdapter = null;
        this.membersAdapter = null;
    }

    public void needRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public int getListHeight() {
        return this.listHeight;
    }

    public int getListWidth() {
        return this.listWidth;
    }

    public void setBtnResId(int res) {
        this.btn.setImageResource(res);
    }
}
