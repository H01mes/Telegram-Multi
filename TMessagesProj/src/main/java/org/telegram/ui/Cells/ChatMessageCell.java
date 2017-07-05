/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewStructure;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ChatMessageCell extends BaseCell implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, MediaController.FileDownloadProgressListener {

    public interface ChatMessageCellDelegate {
        void didLongPressedAvatar(ChatMessageCell chatMessageCell, TLRPC.User user);
        void didPressedUserAvatar(ChatMessageCell cell, TLRPC.User user);
        void didPressedViaBot(ChatMessageCell cell, String username);
        void didPressedChannelAvatar(ChatMessageCell cell, TLRPC.Chat chat, int postId);
        void didPressedCancelSendButton(ChatMessageCell cell);
        void didLongPressed(ChatMessageCell cell);
        void didPressedReplyMessage(ChatMessageCell cell, int id);
        void didPressedUrl(MessageObject messageObject, CharacterStyle url, boolean longPress);
        void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h);
        void didPressedImage(ChatMessageCell cell);
        void didPressedShare(ChatMessageCell cell);
        void didPressedOther(ChatMessageCell cell);
        void didPressedBotButton(ChatMessageCell cell, TLRPC.KeyboardButton button);
        void didPressedInstantButton(ChatMessageCell cell);
        boolean needPlayAudio(MessageObject messageObject);
        boolean canPerformActions();
    }

    private final static int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private final static int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private final static int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private final static int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private final static int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private final static int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private final static int DOCUMENT_ATTACH_TYPE_STICKER = 6;

    private class BotButton {
        private int x;
        private int y;
        private int width;
        private int height;
        private StaticLayout title;
        private TLRPC.KeyboardButton button;
        private int angle;
        private float progressAlpha;
        private long lastUpdateTime;
    }

    private boolean pinnedTop;
    private boolean pinnedBottom;

    private int textX;
    private int textY;
    private int totalHeight;
    private int keyboardHeight;
    private int linkBlockNum;
    private int linkSelectionBlockNum;

    private boolean inLayout;

    private Rect scrollRect = new Rect();

    private int lastVisibleBlockNum;
    private int firstVisibleBlockNum;
    private int totalVisibleBlocksCount;
    private boolean needNewVisiblePart;
    private boolean fullyDraw;

    private RadialProgress radialProgress;
    private ImageReceiver photoImage;
    private AvatarDrawable contactAvatarDrawable;

    private boolean disallowLongPress;

    private boolean isSmallImage;
    private boolean drawImageButton;
    private int documentAttachType;
    private TLRPC.Document documentAttach;
    private boolean drawPhotoImage;
    private boolean hasLinkPreview;
    private boolean hasGamePreview;
    private boolean hasInvoicePreview;
    private int linkPreviewHeight;
    private int mediaOffsetY;
    private int descriptionY;
    private int durationWidth;
    private int descriptionX;
    private int titleX;
    private int authorX;
    private StaticLayout siteNameLayout;
    private StaticLayout titleLayout;
    private StaticLayout descriptionLayout;
    private StaticLayout videoInfoLayout;
    private StaticLayout authorLayout;
    private StaticLayout instantViewLayout;
    private boolean drawInstantView;
    private int instantTextX;
    private int instantWidth;
    private boolean instantPressed;

    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;

    private StaticLayout captionLayout;
    private int captionX;
    private int captionY;
    private int captionHeight;

    private StaticLayout infoLayout;
    private int infoWidth;

    private String currentUrl;

    private int buttonX;
    private int buttonY;
    private int buttonState;
    private int buttonPressed;
    private int otherX;
    private int otherY;
    private boolean imagePressed;
    private boolean otherPressed;
    private boolean photoNotSet;
    private RectF deleteProgressRect = new RectF();
    private RectF rect = new RectF();
    private TLRPC.PhotoSize currentPhotoObject;
    private TLRPC.PhotoSize currentPhotoObjectThumb;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private boolean cancelLoading;

    private CharacterStyle pressedLink;
    private int pressedLinkType;
    private boolean linkPreviewPressed;
    private boolean gamePreviewPressed;
    private ArrayList<LinkPath> urlPathCache = new ArrayList<>();
    private ArrayList<LinkPath> urlPath = new ArrayList<>();
    private ArrayList<LinkPath> urlPathSelection = new ArrayList<>();

    private boolean useSeekBarWaweform;
    private SeekBar seekBar;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarX;
    private int seekBarY;

    private StaticLayout durationLayout;
    private String lastTimeString;
    private int timeWidthAudio;
    private int timeAudioX;

    private StaticLayout songLayout;
    private int songX;

    private StaticLayout performerLayout;
    private int performerX;

    private ArrayList<BotButton> botButtons = new ArrayList<>();
    private HashMap<String, BotButton> botButtonsByData = new HashMap<>();
    private HashMap<String, BotButton> botButtonsByPosition = new HashMap<>();
    private String botButtonsLayout;
    private int widthForButtons;
    private int pressedBotButton;

    //
    private int TAG;

    public boolean isChat;
    private boolean isPressed;
    private boolean forwardName;
    private boolean isHighlighted;
    private boolean mediaBackground;
    private boolean isCheckPressed = true;
    private boolean wasLayout;
    private boolean isAvatarVisible;
    private boolean drawBackground = true;
    private int substractBackgroundHeight;
    private boolean allowAssistant;
    private Drawable currentBackgroundDrawable;
    private int backgroundDrawableLeft;
    private MessageObject currentMessageObject;
    private int viaWidth;
    private int viaNameWidth;
    private int availableTimeWidth;

    private int backgroundWidth = 100;

    private int layoutWidth;
    private int layoutHeight;

    private ImageReceiver avatarImage;
    private AvatarDrawable avatarDrawable;
    private boolean avatarPressed;
    private boolean forwardNamePressed;
    private boolean forwardBotPressed;

    private StaticLayout replyNameLayout;
    private StaticLayout replyTextLayout;
    private ImageReceiver replyImageReceiver;
    private int replyStartX;
    private int replyStartY;
    private int replyNameWidth;
    private float replyNameOffset;
    private int replyTextWidth;
    private float replyTextOffset;
    private boolean needReplyImage;
    private boolean replyPressed;
    private TLRPC.FileLocation currentReplyPhoto;

    private boolean drawShareButton;
    private boolean sharePressed;
    private int shareStartX;
    private int shareStartY;

    private StaticLayout nameLayout;
    private int nameWidth;
    private float nameOffsetX;
    private float nameX;
    private float nameY;
    private boolean drawName;
    private boolean drawNameLayout;

    private StaticLayout[] forwardedNameLayout = new StaticLayout[2];
    private int forwardedNameWidth;
    private boolean drawForwardedName;
    private int forwardNameX;
    private int forwardNameY;
    private float forwardNameOffsetX[] = new float[2];

    private StaticLayout timeLayout;
    private int timeWidth;
    private int timeTextWidth;
    private int timeX;
    private String currentTimeString;
    private boolean drawTime = true;
    private boolean forceNotDrawTime;

    private StaticLayout viewsLayout;
    private int viewsTextWidth;
    private String currentViewsString;

    private TLRPC.User currentUser;
    private TLRPC.Chat currentChat;
    private TLRPC.FileLocation currentPhoto;
    private String currentNameString;

    private TLRPC.User currentForwardUser;
    private TLRPC.User currentViaBotUser;
    private TLRPC.Chat currentForwardChannel;
    private String currentForwardNameString;

    private ChatMessageCellDelegate delegate;

    private int namesOffset;

    private int lastSendState;
    private int lastDeleteDate;
    private int lastViewsCount;
    //Multi
    private static TextPaint senderPaint;
    private int audioDuration;
    protected int avatarSize = AndroidUtilities.dp(42.0f);
    private int checkX = 0;
    private boolean drawStatus;
    private StaticLayout infoLayout2;
    private int infoWidth2;
    protected int leftBound = 48;
    boolean showAvatar = false;
    boolean showMyAvatar = false;
    boolean showMyAvatarGroup = false;
    private GradientDrawable statusBG;
    //


    public ChatMessageCell(Context context) {
        super(context);

        avatarImage = new ImageReceiver();
        avatarImage.setRoundRadius(AndroidUtilities.dp(21));
        avatarDrawable = new AvatarDrawable();
        replyImageReceiver = new ImageReceiver(this);
        TAG = MediaController.getInstance().generateObserverTag();

        contactAvatarDrawable = new AvatarDrawable();
        photoImage = new ImageReceiver(this);
        photoImage.setDelegate(this);
        radialProgress = new RadialProgress(this);
        avatarSize = AndroidUtilities.dp((float) Theme.chatAvatarSize);
        showMyAvatar = Theme.chatShowOwnAvatar;
        showMyAvatarGroup = Theme.chatShowOwnAvatarGroup;
        showAvatar = Theme.chatShowContactAvatar;
        leftBound = Theme.chatAvatarSize + 6;
        avatarImage.setRoundRadius(AndroidUtilities.dp((float) Theme.chatAvatarRadius));
        avatarDrawable.setRadius(AndroidUtilities.dp((float) Theme.chatAvatarRadius));
        statusBG = new GradientDrawable();
        statusBG.setColor(Color.GRAY);
        statusBG.setCornerRadius((float) AndroidUtilities.dp(13.0f));
        if (senderPaint == null) {
            senderPaint = new TextPaint(1);
            senderPaint.setColor(-1);
            senderPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        }
        seekBar = new SeekBar(context);
        seekBar.setDelegate(this);
        seekBarWaveform = new SeekBarWaveform(context);
        seekBarWaveform.setDelegate(this);
        seekBarWaveform.setParentView(this);
    }

    private void setStatusColor(TLRPC.User user) {
        String s = user != null ? LocaleController.formatUserStatus(user) : "";
        if (s.equals(LocaleController.getString("ALongTimeAgo", R.string.ALongTimeAgo))) {
            this.statusBG.setColor(Color.RED); //TODO colors
        } else if (s.equals(LocaleController.getString("Online", R.string.Online))) {
            this.statusBG.setColor(Color.RED);
        } else if (s.equals(LocaleController.getString("Lately", R.string.Lately))) {
            this.statusBG.setColor(Color.RED);
        } else {
            this.statusBG.setColor(Color.RED);
        }
        int l = (user == null || user.status == null) ? -2 : ConnectionsManager.getInstance().getCurrentTime() - user.status.expires;
        if (l > 0 && l < 86400) {
            this.statusBG.setColor(Color.GREEN);
        }
    }

    private void resetPressedLink(int type) {
        if (pressedLink == null || pressedLinkType != type && type != -1) {
            return;
        }
        resetUrlPaths(false);
        pressedLink = null;
        pressedLinkType = -1;
        invalidate();
    }

    private void resetUrlPaths(boolean text) {
        if (text) {
            if (urlPathSelection.isEmpty()) {
                return;
            }
            urlPathCache.addAll(urlPathSelection);
            urlPathSelection.clear();
        } else {
            if (urlPath.isEmpty()) {
                return;
            }
            urlPathCache.addAll(urlPath);
            urlPath.clear();
        }
    }

    private LinkPath obtainNewUrlPath(boolean text) {
        LinkPath linkPath;
        if (!urlPathCache.isEmpty()) {
            linkPath = urlPathCache.get(0);
            urlPathCache.remove(0);
        } else {
            linkPath = new LinkPath();
        }
        if (text) {
            urlPathSelection.add(linkPath);
        } else {
            urlPath.add(linkPath);
        }
        return linkPath;
    }

    private boolean checkTextBlockMotionEvent(MotionEvent event) {
        if (currentMessageObject.type != 0 || currentMessageObject.textLayoutBlocks == null || currentMessageObject.textLayoutBlocks.isEmpty() || !(currentMessageObject.messageText instanceof Spannable)) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP && pressedLinkType == 1) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x >= textX && y >= textY && x <= textX + currentMessageObject.textWidth && y <= textY + currentMessageObject.textHeight) {
                y -= textY;
                int blockNum = 0;
                for (int a = 0; a < currentMessageObject.textLayoutBlocks.size(); a++) {
                    if (currentMessageObject.textLayoutBlocks.get(a).textYOffset > y) {
                        break;
                    }
                    blockNum = a;
                }
                try {
                    MessageObject.TextLayoutBlock block = currentMessageObject.textLayoutBlocks.get(blockNum);
                    x -= textX - (block.isRtl() ? currentMessageObject.textXOffset : 0);
                    y -= block.textYOffset;
                    final int line = block.textLayout.getLineForVertical(y);
                    final int off = block.textLayout.getOffsetForHorizontal(line, x);

                    final float left = block.textLayout.getLineLeft(line);
                    if (left <= x && left + block.textLayout.getLineWidth(line) >= x) {
                        Spannable buffer = (Spannable) currentMessageObject.messageText;
                        CharacterStyle[] link = buffer.getSpans(off, off, ClickableSpan.class);
                        boolean isMono = false;
                        if (link == null || link.length == 0) {
                            link = buffer.getSpans(off, off, URLSpanMono.class);
                            isMono = true;
                        }
                        boolean ignore = false;
                        if (link.length == 0 || link.length != 0 && link[0] instanceof URLSpanBotCommand && !URLSpanBotCommand.enabled) {
                            ignore = true;
                        }
                        if (!ignore) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                pressedLink = link[0];
                                linkBlockNum = blockNum;
                                pressedLinkType = 1;
                                resetUrlPaths(false);
                                try {
                                    LinkPath path = obtainNewUrlPath(false);
                                    int start = buffer.getSpanStart(pressedLink);
                                    int end = buffer.getSpanEnd(pressedLink);
                                    path.setCurrentLayout(block.textLayout, start, 0);
                                    block.textLayout.getSelectionPath(start, end, path);
                                    if (end >= block.charactersEnd) {
                                        for (int a = blockNum + 1; a < currentMessageObject.textLayoutBlocks.size(); a++) {
                                            MessageObject.TextLayoutBlock nextBlock = currentMessageObject.textLayoutBlocks.get(a);
                                            CharacterStyle[] nextLink = buffer.getSpans(nextBlock.charactersOffset, nextBlock.charactersOffset, isMono ? URLSpanMono.class : ClickableSpan.class);
                                            if (nextLink == null || nextLink.length == 0 || nextLink[0] != pressedLink) {
                                                break;
                                            }
                                            path = obtainNewUrlPath(false);
                                            path.setCurrentLayout(nextBlock.textLayout, 0, nextBlock.textYOffset - block.textYOffset);
                                            nextBlock.textLayout.getSelectionPath(0, end, path);
                                            if (end < nextBlock.charactersEnd - 1) {
                                                break;
                                            }
                                        }
                                    }
                                    if (start <= block.charactersOffset) {
                                        int offsetY = 0;
                                        for (int a = blockNum - 1; a >= 0; a--) {
                                            MessageObject.TextLayoutBlock nextBlock = currentMessageObject.textLayoutBlocks.get(a);
                                            CharacterStyle[] nextLink = buffer.getSpans(nextBlock.charactersEnd - 1, nextBlock.charactersEnd - 1, isMono ? URLSpanMono.class : ClickableSpan.class);
                                            if (nextLink == null || nextLink.length == 0 || nextLink[0] != pressedLink) {
                                                break;
                                            }
                                            path = obtainNewUrlPath(false);
                                            start = buffer.getSpanStart(pressedLink);
                                            offsetY -= nextBlock.height;
                                            path.setCurrentLayout(nextBlock.textLayout, start, offsetY);
                                            nextBlock.textLayout.getSelectionPath(start, buffer.getSpanEnd(pressedLink), path);
                                            if (start > nextBlock.charactersOffset) {
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                                invalidate();
                                return true;
                            } else {
                                if (link[0] == pressedLink) {
                                    delegate.didPressedUrl(currentMessageObject, pressedLink, false);
                                    resetPressedLink(1);
                                    return true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else {
                resetPressedLink(1);
            }
        }
        return false;
    }

    private boolean checkCaptionMotionEvent(MotionEvent event) {
        if (!(currentMessageObject.caption instanceof Spannable) || captionLayout == null) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || (linkPreviewPressed || pressedLink != null) && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x >= captionX && x <= captionX + backgroundWidth && y >= captionY && y <= captionY + captionHeight) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        x -= captionX;
                        y -= captionY;
                        final int line = captionLayout.getLineForVertical(y);
                        final int off = captionLayout.getOffsetForHorizontal(line, x);

                        final float left = captionLayout.getLineLeft(line);
                        if (left <= x && left + captionLayout.getLineWidth(line) >= x) {
                            Spannable buffer = (Spannable) currentMessageObject.caption;
                            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                            boolean ignore = false;
                            if (link.length == 0 || link.length != 0 && link[0] instanceof URLSpanBotCommand && !URLSpanBotCommand.enabled) {
                                ignore = true;
                            }
                            if (!ignore) {
                                pressedLink = link[0];
                                pressedLinkType = 3;
                                resetUrlPaths(false);
                                try {
                                    LinkPath path = obtainNewUrlPath(false);
                                    int start = buffer.getSpanStart(pressedLink);
                                    path.setCurrentLayout(captionLayout, start, 0);
                                    captionLayout.getSelectionPath(start, buffer.getSpanEnd(pressedLink), path);
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                                invalidate();
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (pressedLinkType == 3) {
                    delegate.didPressedUrl(currentMessageObject, pressedLink, false);
                    resetPressedLink(3);
                    return true;
                }
            } else {
                resetPressedLink(3);
            }
        }
        return false;
    }

    private boolean checkGameMotionEvent(MotionEvent event) {
        if (!hasGamePreview) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (drawPhotoImage && photoImage.isInsideImage(x, y)) {
                gamePreviewPressed = true;
                return true;
            } else if (descriptionLayout != null && y >= descriptionY) {
                try {
                    x -= textX + AndroidUtilities.dp(10) + descriptionX;
                    y -= descriptionY;
                    final int line = descriptionLayout.getLineForVertical(y);
                    final int off = descriptionLayout.getOffsetForHorizontal(line, x);

                    final float left = descriptionLayout.getLineLeft(line);
                    if (left <= x && left + descriptionLayout.getLineWidth(line) >= x) {
                        Spannable buffer = (Spannable) currentMessageObject.linkDescription;
                        ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                        boolean ignore = false;
                        if (link.length == 0 || link.length != 0 && link[0] instanceof URLSpanBotCommand && !URLSpanBotCommand.enabled) {
                            ignore = true;
                        }
                        if (!ignore) {
                            pressedLink = link[0];
                            linkBlockNum = -10;
                            pressedLinkType = 2;
                            resetUrlPaths(false);
                            try {
                                LinkPath path = obtainNewUrlPath(false);
                                int start = buffer.getSpanStart(pressedLink);
                                path.setCurrentLayout(descriptionLayout, start, 0);
                                descriptionLayout.getSelectionPath(start, buffer.getSpanEnd(pressedLink), path);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pressedLinkType == 2 || gamePreviewPressed) {
                if (pressedLink != null) {
                    if (pressedLink instanceof URLSpan) {
                        Browser.openUrl(getContext(), ((URLSpan) pressedLink).getURL());
                    } else if (pressedLink instanceof ClickableSpan) {
                        ((ClickableSpan) pressedLink).onClick(this);
                    }
                    resetPressedLink(2);
                } else {
                    gamePreviewPressed = false;
                    for (int a = 0; a < botButtons.size(); a++) {
                        BotButton button = botButtons.get(a);
                        if (button.button instanceof TLRPC.TL_keyboardButtonGame) {
                            playSoundEffect(SoundEffectConstants.CLICK);
                            delegate.didPressedBotButton(this, button.button);
                            invalidate();
                            break;
                        }
                    }
                    resetPressedLink(2);
                    return true;
                }
            } else {
                resetPressedLink(2);
            }
        }
        return false;
    }

    private boolean checkLinkPreviewMotionEvent(MotionEvent event) {
        if (currentMessageObject.type != 0 || !hasLinkPreview) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x >= textX && x <= textX + backgroundWidth && y >= textY + currentMessageObject.textHeight && y <= textY + currentMessageObject.textHeight + linkPreviewHeight + AndroidUtilities.dp(8)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (descriptionLayout != null && y >= descriptionY) {
                    try {
                        int checkX = x - (textX + AndroidUtilities.dp(10) + descriptionX);
                        int checkY = y - descriptionY;
                        if (checkY <= descriptionLayout.getHeight()) {
                            final int line = descriptionLayout.getLineForVertical(checkY);
                            final int off = descriptionLayout.getOffsetForHorizontal(line, checkX);

                            final float left = descriptionLayout.getLineLeft(line);
                            if (left <= checkX && left + descriptionLayout.getLineWidth(line) >= checkX) {
                                Spannable buffer = (Spannable) currentMessageObject.linkDescription;
                                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                                boolean ignore = false;
                                if (link.length == 0 || link.length != 0 && link[0] instanceof URLSpanBotCommand && !URLSpanBotCommand.enabled) {
                                    ignore = true;
                                }
                                if (!ignore) {
                                    pressedLink = link[0];
                                    linkBlockNum = -10;
                                    pressedLinkType = 2;
                                    resetUrlPaths(false);
                                    try {
                                        LinkPath path = obtainNewUrlPath(false);
                                        int start = buffer.getSpanStart(pressedLink);
                                        path.setCurrentLayout(descriptionLayout, start, 0);
                                        descriptionLayout.getSelectionPath(start, buffer.getSpanEnd(pressedLink), path);
                                    } catch (Exception e) {
                                        FileLog.e(e);
                                    }
                                    invalidate();
                                    return true;
                                }
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                if (pressedLink == null) {
                    if (drawPhotoImage && drawImageButton && buttonState != -1 && x >= buttonX && x <= buttonX + AndroidUtilities.dp(48) && y >= buttonY && y <= buttonY + AndroidUtilities.dp(48)) {
                        buttonPressed = 1;
                        return true;
                    } else if (drawInstantView) {
                        instantPressed = true;
                        invalidate();
                        return true;
                    } else if (documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT && drawPhotoImage && photoImage.isInsideImage(x, y)) {
                        linkPreviewPressed = true;
                        TLRPC.WebPage webPage = currentMessageObject.messageOwner.media.webpage;
                        if (documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && buttonState == -1 && MediaController.getInstance().canAutoplayGifs() && (photoImage.getAnimation() == null || !TextUtils.isEmpty(webPage.embed_url))) {
                            linkPreviewPressed = false;
                            return false;
                        }
                        return true;
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (instantPressed) {
                    if (delegate != null) {
                        delegate.didPressedInstantButton(this);
                    }
                    playSoundEffect(SoundEffectConstants.CLICK);
                    instantPressed = false;
                    invalidate();
                } else if (pressedLinkType == 2 || buttonPressed != 0 || linkPreviewPressed) {
                    if (buttonPressed != 0) {
                        buttonPressed = 0;
                        playSoundEffect(SoundEffectConstants.CLICK);
                        didPressedButton(false);
                        invalidate();
                    } else if (pressedLink != null) {
                        if (pressedLink instanceof URLSpan) {
                            Browser.openUrl(getContext(), ((URLSpan) pressedLink).getURL());
                        } else if (pressedLink instanceof ClickableSpan) {
                            ((ClickableSpan) pressedLink).onClick(this);
                        }
                        resetPressedLink(2);
                    } else {
                        if (documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && drawImageButton) {
                            if (buttonState == -1) {
                                if (MediaController.getInstance().canAutoplayGifs()) {
                                    delegate.didPressedImage(this);
                                } else {
                                    buttonState = 2;
                                    currentMessageObject.audioProgress = 1;
                                    photoImage.setAllowStartAnimation(false);
                                    photoImage.stopAnimation();
                                    radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                                    invalidate();
                                    playSoundEffect(SoundEffectConstants.CLICK);
                                }
                            } else if (buttonState == 2 || buttonState == 0) {
                                didPressedButton(false);
                                playSoundEffect(SoundEffectConstants.CLICK);
                            }
                        } else {
                            TLRPC.WebPage webPage = currentMessageObject.messageOwner.media.webpage;
                            if (webPage != null && Build.VERSION.SDK_INT >= 16 && !TextUtils.isEmpty(webPage.embed_url)) {
                                delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.title, webPage.url, webPage.embed_width, webPage.embed_height);
                            } else if (buttonState == -1) {
                                delegate.didPressedImage(this);
                                playSoundEffect(SoundEffectConstants.CLICK);
                            } else if (webPage != null) {
                                Browser.openUrl(getContext(), webPage.url);
                            }
                        }
                        resetPressedLink(2);
                        return true;
                    }
                } else {
                    resetPressedLink(2);
                }
            }
        }
        return false;
    }

    private boolean checkOtherButtonMotionEvent(MotionEvent event) {
        boolean allow = currentMessageObject.type == 16;
        if (!allow) {
            allow = !(documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT && currentMessageObject.type != 12 && documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC && documentAttachType != DOCUMENT_ATTACH_TYPE_VIDEO && documentAttachType != DOCUMENT_ATTACH_TYPE_GIF && currentMessageObject.type != 8 || hasGamePreview || hasInvoicePreview);
        }
        if (!allow) {
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        boolean result = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (currentMessageObject.type == 16) {
                if (x >= otherX && x <= otherX + AndroidUtilities.dp(30 + 205) && y >= otherY - AndroidUtilities.dp(14) && y <= otherY + AndroidUtilities.dp(50)) {
                    otherPressed = true;
                    result = true;
                    invalidate();
                }
            } else {
                if (x >= otherX - AndroidUtilities.dp(20) && x <= otherX + AndroidUtilities.dp(20) && y >= otherY - AndroidUtilities.dp(4) && y <= otherY + AndroidUtilities.dp(30)) {
                    otherPressed = true;
                    result = true;
                    invalidate();
                }
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (otherPressed) {
                    otherPressed = false;
                    playSoundEffect(SoundEffectConstants.CLICK);
                    delegate.didPressedOther(this);
                    invalidate();
                }
            }
        }
        return result;
    }

    private boolean checkPhotoImageMotionEvent(MotionEvent event) {
        if (!drawPhotoImage && documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        boolean result = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (buttonState != -1 && x >= buttonX && x <= buttonX + AndroidUtilities.dp(48) && y >= buttonY && y <= buttonY + AndroidUtilities.dp(48)) {
                buttonPressed = 1;
                invalidate();
                result = true;
            } else {
                if (documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    if (x >= photoImage.getImageX() && x <= photoImage.getImageX() + backgroundWidth - AndroidUtilities.dp(50) && y >= photoImage.getImageY() && y <= photoImage.getImageY() + photoImage.getImageHeight()) {
                        imagePressed = true;
                        result = true;
                    }
                } else if (currentMessageObject.type != 13 || currentMessageObject.getInputStickerSet() != null) {
                    if (x >= photoImage.getImageX() && x <= photoImage.getImageX() + backgroundWidth && y >= photoImage.getImageY() && y <= photoImage.getImageY() + photoImage.getImageHeight()) {
                        imagePressed = true;
                        result = true;
                    }
                    if (currentMessageObject.type == 12) {
                        TLRPC.User user = MessagesController.getInstance().getUser(currentMessageObject.messageOwner.media.user_id);
                        if (user == null) {
                            imagePressed = false;
                            result = false;
                        }
                    }
                }
            }
            if (imagePressed) {
                if (currentMessageObject.isSecretPhoto()) {
                    imagePressed = false;
                } else if (currentMessageObject.isSendError()) {
                    imagePressed = false;
                    result = false;
                } else if (currentMessageObject.type == 8 && buttonState == -1 && MediaController.getInstance().canAutoplayGifs() && photoImage.getAnimation() == null) {
                    imagePressed = false;
                    result = false;
                }
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (buttonPressed == 1) {
                    buttonPressed = 0;
                    playSoundEffect(SoundEffectConstants.CLICK);
                    didPressedButton(false);
                    radialProgress.swapBackground(getDrawableForCurrentState());
                    invalidate();
                } else if (imagePressed) {
                    imagePressed = false;
                    if (buttonState == -1 || buttonState == 2 || buttonState == 3) {
                        playSoundEffect(SoundEffectConstants.CLICK);
                        didClickedImage();
                    } else if (buttonState == 0 && documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        playSoundEffect(SoundEffectConstants.CLICK);
                        didPressedButton(false);
                    }
                    invalidate();
                    if (this.currentMessageObject.type != 9) {
                        return false;
                    }
                    final String name = FileLoader.getDocumentFileName(this.currentMessageObject.messageOwner.media.document);
                    if (name.length() <= 0) {
                        return false;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (ChatMessageCell.this.getContext() != null) {
                                Toast toast = Toast.makeText(ChatMessageCell.this.getContext(), name, Toast.LENGTH_SHORT);
                                toast.setGravity(48, 0, AndroidUtilities.dp(90.0f));
                                toast.show();
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    private boolean checkAudioMotionEvent(MotionEvent event) {
        if (documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result;
        if (useSeekBarWaweform) {
            result = seekBarWaveform.onTouch(event.getAction(), event.getX() - seekBarX - AndroidUtilities.dp(13), event.getY() - seekBarY);
        } else {
            result = seekBar.onTouch(event.getAction(), event.getX() - seekBarX, event.getY() - seekBarY);
        }
        if (result) {
            if (!useSeekBarWaweform && event.getAction() == MotionEvent.ACTION_DOWN) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (useSeekBarWaweform && !seekBarWaveform.isStartDraging() && event.getAction() == MotionEvent.ACTION_UP) {
                didPressedButton(true);
            }
            disallowLongPress = true;
            invalidate();
        } else {
            int side = AndroidUtilities.dp(36);
            boolean area;
            if (buttonState == 0 || buttonState == 1 || buttonState == 2) {
                area = x >= buttonX - AndroidUtilities.dp(12) && x <= buttonX - AndroidUtilities.dp(12) + backgroundWidth && y >= namesOffset + mediaOffsetY && y <= layoutHeight;
            } else {
                area = x >= buttonX && x <= buttonX + side && y >= buttonY && y <= buttonY + side;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (area) {
                    buttonPressed = 1;
                    invalidate();
                    result = true;
                    radialProgress.swapBackground(getDrawableForCurrentState());
                }
            } else if (buttonPressed != 0) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    buttonPressed = 0;
                    playSoundEffect(SoundEffectConstants.CLICK);
                    didPressedButton(true);
                    invalidate();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    buttonPressed = 0;
                    invalidate();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (!area) {
                        buttonPressed = 0;
                        invalidate();
                    }
                }
                radialProgress.swapBackground(getDrawableForCurrentState());
            }
        }
        return result;
    }

    private boolean checkBotButtonMotionEvent(MotionEvent event) {
        if (botButtons.isEmpty()) {
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        boolean result = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int addX;
            if (currentMessageObject.isOutOwner()) {
                addX = getMeasuredWidth() - widthForButtons - AndroidUtilities.dp(10);
            } else {
                addX = backgroundDrawableLeft + AndroidUtilities.dp(mediaBackground ? 1 : 7);
            }
            for (int a = 0; a < botButtons.size(); a++) {
                BotButton button = botButtons.get(a);
                int y2 = button.y + layoutHeight - AndroidUtilities.dp(2);
                if (x >= button.x + addX && x <= button.x + addX + button.width && y >= y2 && y <= y2 + button.height) {
                    pressedBotButton = a;
                    invalidate();
                    result = true;
                    break;
                }
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (pressedBotButton != -1) {
                    playSoundEffect(SoundEffectConstants.CLICK);
                    delegate.didPressedBotButton(this, botButtons.get(pressedBotButton).button);
                    pressedBotButton = -1;
                    invalidate();
                }
            }
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentMessageObject == null || !delegate.canPerformActions()) {
            return super.onTouchEvent(event);
        }

        disallowLongPress = false;

        boolean result = checkTextBlockMotionEvent(event);
        if (!result) {
            result = checkOtherButtonMotionEvent(event);
        }
        if (!result) {
            result = checkLinkPreviewMotionEvent(event);
        }
        if (!result) {
            result = checkGameMotionEvent(event);
        }
        if (!result) {
            result = checkCaptionMotionEvent(event);
        }
        if (!result) {
            result = checkAudioMotionEvent(event);
        }
        if (!result) {
            result = checkPhotoImageMotionEvent(event);
        }
        if (!result) {
            result = checkBotButtonMotionEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            buttonPressed = 0;
            pressedBotButton = -1;
            linkPreviewPressed = false;
            otherPressed = false;
            imagePressed = false;
            instantPressed = false;
            result = false;
            resetPressedLink(-1);
        }
        if (!disallowLongPress && result && event.getAction() == MotionEvent.ACTION_DOWN) {
            startCheckLongPress();
        }
        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_MOVE) {
            cancelCheckLongPress();
        }

        if (!result) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (delegate == null || delegate.canPerformActions()) {
                    if (isAvatarVisible && avatarImage.isInsideImage(x, y + getTop())) {
                        avatarPressed = true;
                        result = true;
                    } else if (drawForwardedName && forwardedNameLayout[0] != null && x >= forwardNameX && x <= forwardNameX + forwardedNameWidth && y >= forwardNameY && y <= forwardNameY + AndroidUtilities.dp(32)) {
                        if (viaWidth != 0 && x >= forwardNameX + viaNameWidth + AndroidUtilities.dp(4)) {
                            forwardBotPressed = true;
                        } else {
                            forwardNamePressed = true;
                        }
                        result = true;
                    } else if (drawNameLayout && nameLayout != null && viaWidth != 0 && x >= nameX + viaNameWidth && x <= nameX + viaNameWidth + viaWidth && y >= nameY - AndroidUtilities.dp(4) && y <= nameY + AndroidUtilities.dp(20)) {
                        forwardBotPressed = true;
                        result = true;
                    } else if (currentMessageObject.isReply() && x >= replyStartX && x <= replyStartX + Math.max(replyNameWidth, replyTextWidth) && y >= replyStartY && y <= replyStartY + AndroidUtilities.dp(35)) {
                        replyPressed = true;
                        result = true;
                    } else if (drawShareButton && x >= shareStartX && x <= shareStartX + AndroidUtilities.dp(40) && y >= shareStartY && y <= shareStartY + AndroidUtilities.dp(32)) {
                        sharePressed = true;
                        result = true;
                        invalidate();
                    }
                    if (result) {
                        startCheckLongPress();
                    }
                }
            } else {
                if (event.getAction() != MotionEvent.ACTION_MOVE) {
                    cancelCheckLongPress();
                }
                if (avatarPressed) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        avatarPressed = false;
                        playSoundEffect(SoundEffectConstants.CLICK);
                        if (delegate != null) {
                            if (currentUser != null) {
                                delegate.didPressedUserAvatar(this, currentUser);
                            } else if (currentChat != null) {
                                delegate.didPressedChannelAvatar(this, currentChat, 0);
                            }
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        avatarPressed = false;
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (isAvatarVisible && !avatarImage.isInsideImage(x, y + getTop())) {
                            avatarPressed = false;
                        }
                    }
                } else if (forwardNamePressed) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        forwardNamePressed = false;
                        playSoundEffect(SoundEffectConstants.CLICK);
                        if (delegate != null) {
                            if (currentForwardChannel != null) {
                                delegate.didPressedChannelAvatar(this, currentForwardChannel, currentMessageObject.messageOwner.fwd_from.channel_post);
                            } else if (currentForwardUser != null) {
                                delegate.didPressedUserAvatar(this, currentForwardUser);
                            }
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        forwardNamePressed = false;
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (!(x >= forwardNameX && x <= forwardNameX + forwardedNameWidth && y >= forwardNameY && y <= forwardNameY + AndroidUtilities.dp(32))) {
                            forwardNamePressed = false;
                        }
                    }
                } else if (forwardBotPressed) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        forwardBotPressed = false;
                        playSoundEffect(SoundEffectConstants.CLICK);
                        if (delegate != null) {
                            delegate.didPressedViaBot(this, currentViaBotUser != null ? currentViaBotUser.username : currentMessageObject.messageOwner.via_bot_name);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        forwardBotPressed = false;
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (drawForwardedName && forwardedNameLayout[0] != null) {
                            if (!(x >= forwardNameX && x <= forwardNameX + forwardedNameWidth && y >= forwardNameY && y <= forwardNameY + AndroidUtilities.dp(32))) {
                                forwardBotPressed = false;
                            }
                        } else {
                            if (!(x >= nameX + viaNameWidth && x <= nameX + viaNameWidth + viaWidth && y >= nameY - AndroidUtilities.dp(4) && y <= nameY + AndroidUtilities.dp(20))) {
                                forwardBotPressed = false;
                            }
                        }
                    }
                } else if (replyPressed) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        replyPressed = false;
                        playSoundEffect(SoundEffectConstants.CLICK);
                        if (delegate != null) {
                            delegate.didPressedReplyMessage(this, currentMessageObject.messageOwner.reply_to_msg_id);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        replyPressed = false;
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (!(x >= replyStartX && x <= replyStartX + Math.max(replyNameWidth, replyTextWidth) && y >= replyStartY && y <= replyStartY + AndroidUtilities.dp(35))) {
                            replyPressed = false;
                        }
                    }
                } else if (sharePressed) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sharePressed = false;
                        playSoundEffect(SoundEffectConstants.CLICK);
                        if (delegate != null) {
                            delegate.didPressedShare(this);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        sharePressed = false;
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (!(x >= shareStartX && x <= shareStartX + AndroidUtilities.dp(40) && y >= shareStartY && y <= shareStartY + AndroidUtilities.dp(32))) {
                            sharePressed = false;
                        }
                    }
                    invalidate();
                }
            }
        }
        return result;
    }

    public void updateAudioProgress() {
        if (currentMessageObject == null || documentAttach == null) {
            return;
        }

        if (useSeekBarWaweform) {
            if (!seekBarWaveform.isDragging()) {
                seekBarWaveform.setProgress(currentMessageObject.audioProgress);
            }
        } else {
            if (!seekBar.isDragging()) {
                seekBar.setProgress(currentMessageObject.audioProgress);
            }
        }

        int duration = 0;
        if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            if (!MediaController.getInstance().isPlayingAudio(currentMessageObject)) {
                for (int a = 0; a < documentAttach.attributes.size(); a++) {
                    TLRPC.DocumentAttribute attribute = documentAttach.attributes.get(a);
                    if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                        audioDuration = attribute.duration;
                        break;
                    }
                }
            } else {
                duration = currentMessageObject.audioProgressSec;
            }
//            String timeString = String.format("%02d:%02d", duration / 60, duration % 60);
            String timeString = String.format("%d:%02d / %d:%02d", new Object[]{Integer.valueOf(this.currentMessageObject.audioProgressSec / 60), Integer.valueOf(this.currentMessageObject.audioProgressSec % 60), Integer.valueOf(this.audioDuration / 60), Integer.valueOf(this.audioDuration % 60)});
            if (lastTimeString == null || lastTimeString != null && !lastTimeString.equals(timeString)) {
                lastTimeString = timeString;
                timeWidthAudio = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(timeString));
                durationLayout = new StaticLayout(timeString, Theme.chat_audioTimePaint, timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        } else {
            int currentProgress = 0;
            for (int a = 0; a < documentAttach.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = documentAttach.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    duration = attribute.duration;
                    break;
                }
            }
            if (MediaController.getInstance().isPlayingAudio(currentMessageObject)) {
                currentProgress = currentMessageObject.audioProgressSec;
            }
            String timeString = String.format("%d:%02d / %d:%02d", currentProgress / 60, currentProgress % 60, duration / 60, duration % 60);
            if (this.currentMessageObject.messageOwner.media.document != null) {
                timeString = timeString + "  " + AndroidUtilities.formatFileSize((long) this.currentMessageObject.messageOwner.media.document.size);
            }
            if (lastTimeString == null || lastTimeString != null && !lastTimeString.equals(timeString)) {
                lastTimeString = timeString;
                int timeWidth = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(timeString));
                durationLayout = new StaticLayout(timeString, Theme.chat_audioTimePaint, timeWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
        invalidate();
    }

    private String getCurrentNameString(MessageObject messageObject) {
        TLRPC.User currentUser = null;
        TLRPC.Chat currentChat = null;
        if (messageObject.isFromUser()) {
            currentUser = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
        } else if (messageObject.messageOwner.from_id < 0) {
            currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
        }
        if (currentUser != null) {
            String s = UserObject.getUserName(currentUser);
            String str = currentUser.username;
            return s;
        } else if (currentChat != null) {
            return currentChat.title;
        } else {
            return "DELETED";
        }
    }

    public void downloadAudioIfNeed() {
        if (documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO || documentAttach.size >= 1024 * 1024) {
            return;
        }
        if (buttonState == 2) {
            FileLoader.getInstance().loadFile(documentAttach, true, false);
            buttonState = 4;
            radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        }
    }

    public void setFullyDraw(boolean draw) {
        fullyDraw = draw;
    }

    public void setVisiblePart(int position, int height) {
        if (currentMessageObject == null || currentMessageObject.textLayoutBlocks == null) {
            return;
        }
        position -= textY;

        int newFirst = -1, newLast = -1, newCount = 0;

        int startBlock = 0;
        for (int a = 0; a < currentMessageObject.textLayoutBlocks.size(); a++) {
            if (currentMessageObject.textLayoutBlocks.get(a).textYOffset > position) {
                break;
            }
            startBlock = a;
        }

        for (int a = startBlock; a < currentMessageObject.textLayoutBlocks.size(); a++) {
            MessageObject.TextLayoutBlock block = currentMessageObject.textLayoutBlocks.get(a);
            float y = block.textYOffset;
            if (intersect(y, y + block.height, position, position + height)) {
                if (newFirst == -1) {
                    newFirst = a;
                }
                newLast = a;
                newCount++;
            } else if (y > position) {
                break;
            }
        }

        if (lastVisibleBlockNum != newLast || firstVisibleBlockNum != newFirst || totalVisibleBlocksCount != newCount) {
            lastVisibleBlockNum = newLast;
            firstVisibleBlockNum = newFirst;
            totalVisibleBlocksCount = newCount;
            invalidate();
        }
    }

    private boolean intersect(float left1, float right1, float left2, float right2) {
        if (left1 <= left2) {
            return right1 >= left2;
        }
        return left1 <= right2;
    }

    public static StaticLayout generateStaticLayout(CharSequence text, TextPaint paint, int maxWidth, int smallWidth, int linesCount, int maxLines) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        int addedChars = 0;
        StaticLayout layout = new StaticLayout(text, paint, smallWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        for (int a = 0; a < linesCount; a++) {
            Layout.Directions directions = layout.getLineDirections(a);
            if (layout.getLineLeft(a) != 0 || layout.isRtlCharAt(layout.getLineStart(a)) || layout.isRtlCharAt(layout.getLineEnd(a))) {
                maxWidth = smallWidth;
            }
            int pos = layout.getLineEnd(a);
            if (pos == text.length()) {
                break;
            }
            pos--;
            if (stringBuilder.charAt(pos + addedChars) == ' ') {
                stringBuilder.replace(pos + addedChars, pos + addedChars + 1, "\n");
            } else if (stringBuilder.charAt(pos + addedChars) != '\n') {
                stringBuilder.insert(pos + addedChars, "\n");
                addedChars++;
            }
            if (a == layout.getLineCount() - 1 || a == maxLines - 1) {
                break;
            }
        }
        return StaticLayoutEx.createStaticLayout(stringBuilder, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, AndroidUtilities.dp(1), false, TextUtils.TruncateAt.END, maxWidth, maxLines);
    }

    private void didClickedImage() {
        if (currentMessageObject.type == 1 || currentMessageObject.type == 13) {
            if (buttonState == -1) {
                delegate.didPressedImage(this);
            } else if (buttonState == 0) {
                didPressedButton(false);
            }
        } else if (currentMessageObject.type == 12) {
            TLRPC.User user = MessagesController.getInstance().getUser(currentMessageObject.messageOwner.media.user_id);
            delegate.didPressedUserAvatar(this, user);
        } else if (currentMessageObject.type == 8) {
            if (buttonState == -1) {
                if (MediaController.getInstance().canAutoplayGifs()) {
                    if (!currentMessageObject.isVideoVoice()) { //TODO
                        delegate.didPressedImage(this);
                    }
                } else {
                    buttonState = 2;
                    currentMessageObject.audioProgress = 1;
                    photoImage.setAllowStartAnimation(false);
                    photoImage.stopAnimation();
                    radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else if (buttonState == 2 || buttonState == 0) {
                didPressedButton(false);
            }
        } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (buttonState == 0 || buttonState == 3) {
                didPressedButton(false);
            }
        } else if (currentMessageObject.type == 4) {
            delegate.didPressedImage(this);
        } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            if (buttonState == -1) {
                delegate.didPressedImage(this);
            }
        } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_GIF) {
            if (buttonState == -1) {
                TLRPC.WebPage webPage = currentMessageObject.messageOwner.media.webpage;
                if (webPage != null) {
                    if (Build.VERSION.SDK_INT >= 16 && webPage.embed_url != null && webPage.embed_url.length() != 0) {
                        delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
                    } else {
                        Browser.openUrl(getContext(), webPage.url);
                    }
                }
            }
        } else if (hasInvoicePreview) {
            if (buttonState == -1) {
                delegate.didPressedImage(this);
            }
        }
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        if (messageObject == null || messageObject.isOut()) {
            return;
        }
        String str = messageObject.getSecretTimeString();
        if (str == null) {
            return;
        }
        infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(str));
        CharSequence str2 = TextUtils.ellipsize(str, Theme.chat_infoPaint, infoWidth, TextUtils.TruncateAt.END);
        infoLayout = new StaticLayout(str2, Theme.chat_infoPaint, infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        invalidate();
    }

    private boolean isPhotoDataChanged(MessageObject object) {
        if (object.type == 0 || object.type == 14) {
            return false;
        }
        if (object.type == 4) {
            if (currentUrl == null) {
                return true;
            }
            double lat = object.messageOwner.media.geo.lat;
            double lon = object.messageOwner.media.geo._long;
            String url = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", lat, lon, Math.min(2, (int) Math.ceil(AndroidUtilities.density)), lat, lon);
            if (!url.equals(currentUrl)) {
                return true;
            }
        } else if (currentPhotoObject == null || currentPhotoObject.location instanceof TLRPC.TL_fileLocationUnavailable) {
            return true;
        } else if (currentMessageObject != null && photoNotSet) {
            File cacheFile = FileLoader.getPathToMessage(currentMessageObject.messageOwner);
            if (cacheFile.exists()) { //TODO
                return true;
            }
        }
        return false;
    }

    private boolean isUserDataChanged() {
        if (currentMessageObject != null && (!hasLinkPreview && currentMessageObject.messageOwner.media != null && currentMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) {
            return true;
        }
        if (currentMessageObject == null || currentUser == null && currentChat == null) {
            return false;
        }
        if (lastSendState != currentMessageObject.messageOwner.send_state) {
            return true;
        }
        if (lastDeleteDate != currentMessageObject.messageOwner.destroyTime) {
            return true;
        }
        if (lastViewsCount != currentMessageObject.messageOwner.views) {
            return true;
        }

        TLRPC.User newUser = null;
        TLRPC.Chat newChat = null;
        if (currentMessageObject.isFromUser()) {
            newUser = MessagesController.getInstance().getUser(currentMessageObject.messageOwner.from_id);
        } else if (currentMessageObject.messageOwner.from_id < 0) {
            newChat = MessagesController.getInstance().getChat(-currentMessageObject.messageOwner.from_id);
        } else if (currentMessageObject.messageOwner.post) {
            newChat = MessagesController.getInstance().getChat(currentMessageObject.messageOwner.to_id.channel_id);
        }
        TLRPC.FileLocation newPhoto = null;

        if (isAvatarVisible) {
            if (newUser != null && newUser.photo != null){
                newPhoto = newUser.photo.photo_small;
            } else if (newChat != null && newChat.photo != null) {
                newPhoto = newChat.photo.photo_small;
            }
        }

        if (replyTextLayout == null && currentMessageObject.replyMessageObject != null) {
            return true;
        }

        if (currentPhoto == null && newPhoto != null || currentPhoto != null && newPhoto == null || currentPhoto != null && newPhoto != null && (currentPhoto.local_id != newPhoto.local_id || currentPhoto.volume_id != newPhoto.volume_id)) {
            return true;
        }

        TLRPC.FileLocation newReplyPhoto = null;

        if (currentMessageObject.replyMessageObject != null) {
            TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(currentMessageObject.replyMessageObject.photoThumbs, 80);
            if (photoSize != null && currentMessageObject.replyMessageObject.type != 13) {
                newReplyPhoto = photoSize.location;
            }
        }

        if (currentReplyPhoto == null && newReplyPhoto != null) {
            return true;
        }

        String newNameString = null;
        if (drawName && isChat && !currentMessageObject.isOutOwner()) {
            if (newUser != null) {
                newNameString = UserObject.getUserName(newUser);
            } else if (newChat != null) {
                newNameString = newChat.title;
            }
        }

        if (currentNameString == null && newNameString != null || currentNameString != null && newNameString == null || currentNameString != null && newNameString != null && !currentNameString.equals(newNameString)) {
            return true;
        }

        if (drawForwardedName) {
            newNameString = currentMessageObject.getForwardedName();
            return currentForwardNameString == null && newNameString != null || currentForwardNameString != null && newNameString == null || currentForwardNameString != null && newNameString != null && !currentForwardNameString.equals(newNameString);
        }
        return false;
    }

    public ImageReceiver getPhotoImage() {
        return photoImage;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        avatarImage.onDetachedFromWindow();
        replyImageReceiver.onDetachedFromWindow();
        photoImage.onDetachedFromWindow();
        MediaController.getInstance().removeLoadingFileObserver(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        avatarImage.onAttachedToWindow();
        avatarImage.setParentView((View) getParent());
        replyImageReceiver.onAttachedToWindow();
        if (drawPhotoImage) {
            if (photoImage.onAttachedToWindow()) {
                updateButtonState(false);
            }
        } else {
            updateButtonState(false);
        }
    }

    @Override
    protected void onLongPress() {
        if (pressedLink instanceof URLSpanMono) {
            delegate.didPressedUrl(currentMessageObject, pressedLink, true);
        } else if (pressedLink instanceof URLSpanNoUnderline) {
            URLSpanNoUnderline url = (URLSpanNoUnderline) pressedLink;
            if (url.getURL().startsWith("/")) {
                delegate.didPressedUrl(currentMessageObject, pressedLink, true);
                return;
            }
        } else if (pressedLink instanceof URLSpan) {
            delegate.didPressedUrl(currentMessageObject, pressedLink, true);
            return;
        }
        resetPressedLink(-1);
        if (buttonPressed != 0 || pressedBotButton != -1) {
            buttonPressed = 0;
            pressedBotButton = -1;
            invalidate();
        }
        if (instantPressed) {
            instantPressed = false;
            invalidate();
        }
        if (this.delegate == null) {
            return;
        }
        if (this.avatarPressed) {
            this.delegate.didLongPressedAvatar(this, this.currentUser);
        } else {
            this.delegate.didLongPressed(this);
        }
    }

    public void setCheckPressed(boolean value, boolean pressed) {
        isCheckPressed = value;
        isPressed = pressed;
        radialProgress.swapBackground(getDrawableForCurrentState());
        if (useSeekBarWaweform) {
            seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    public void setHighlighted(boolean value) {
        if (isHighlighted == value) {
            return;
        }
        isHighlighted = value;
        radialProgress.swapBackground(getDrawableForCurrentState());
        if (useSeekBarWaweform) {
            seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        radialProgress.swapBackground(getDrawableForCurrentState());
        if (useSeekBarWaweform) {
            seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    @Override
    public void onSeekBarDrag(float progress) {
        if (currentMessageObject == null) {
            return;
        }
        currentMessageObject.audioProgress = progress;
        MediaController.getInstance().seekToProgress(currentMessageObject, progress);
    }

    private void updateWaveform() {
        if (currentMessageObject == null || documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO) {
            return;
        }
        for (int a = 0; a < documentAttach.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = documentAttach.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                if (attribute.waveform == null || attribute.waveform.length == 0) {
                    MediaController.getInstance().generateWaveform(currentMessageObject);
                }
                useSeekBarWaweform = attribute.waveform != null;
                seekBarWaveform.setWaveform(attribute.waveform);
                break;
            }
        }
    }

    private int createDocumentLayout(int maxWidth, MessageObject messageObject) {
        if (messageObject.type == 0) {
            documentAttach = messageObject.messageOwner.media.webpage.document;
        } else {
            documentAttach = messageObject.messageOwner.media.document;
        }
        if (documentAttach == null) {
            return 0;
        }
        if (MessageObject.isVoiceDocument(documentAttach)) {
            documentAttachType = DOCUMENT_ATTACH_TYPE_AUDIO;
            int duration = 0;
            for (int a = 0; a < documentAttach.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = documentAttach.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    duration = attribute.duration;
                    break;
                }
            }
            availableTimeWidth = (maxWidth - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(String.format("%d:%02d / %d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60), Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)}))));
//            availableTimeWidth = maxWidth - AndroidUtilities.dp(76 + 18) - (int) Math.ceil(Theme.chat_audioTimePaint.measureText("00:00"));
            measureTime(messageObject);
            int minSize = AndroidUtilities.dp(40 + 14 + 20 + 90 + 10) + timeWidth;
            if (!hasLinkPreview) {
                backgroundWidth = Math.min(maxWidth, minSize + duration * AndroidUtilities.dp(10));
            }
            seekBarWaveform.setMessageObject(messageObject);
            return 0;
        } else if (MessageObject.isMusicDocument(documentAttach)) {
            documentAttachType = DOCUMENT_ATTACH_TYPE_MUSIC;

            maxWidth = maxWidth - AndroidUtilities.dp(86);

            CharSequence stringFinal = TextUtils.ellipsize(messageObject.getMusicTitle().replace('\n', ' '), Theme.chat_audioTitlePaint, maxWidth - AndroidUtilities.dp(12), TextUtils.TruncateAt.END);
            songLayout = new StaticLayout(stringFinal, Theme.chat_audioTitlePaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (songLayout.getLineCount() > 0) {
                songX = -(int) Math.ceil(songLayout.getLineLeft(0));
            }

            stringFinal = TextUtils.ellipsize(messageObject.getMusicAuthor().replace('\n', ' '), Theme.chat_audioPerformerPaint, maxWidth, TextUtils.TruncateAt.END);
            performerLayout = new StaticLayout(stringFinal, Theme.chat_audioPerformerPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (performerLayout.getLineCount() > 0) {
                performerX = -(int) Math.ceil(performerLayout.getLineLeft(0));
            }

            radialProgress.setSizeAndType((long) this.documentAttach.size, messageObject.type);
            int duration = 0;
            for (int a = 0; a < documentAttach.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = documentAttach.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    duration = attribute.duration;
                    break;
                }
            }
            int durationWidth = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(String.format("%d:%02d / %d:%02d", duration / 60, duration % 60, duration / 60, duration % 60)));
            availableTimeWidth = backgroundWidth - AndroidUtilities.dp(76 + 18) - durationWidth;
            return durationWidth;
        } else if (MessageObject.isVideoDocument(documentAttach)) {
            documentAttachType = DOCUMENT_ATTACH_TYPE_VIDEO;
            int duration = 0;
            for (int a = 0; a < documentAttach.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = documentAttach.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                    duration = attribute.duration;
                    break;
                }
            }
            int minutes = duration / 60;
            int seconds = duration - minutes * 60;
            String str = String.format("%d:%02d, %s", minutes, seconds, AndroidUtilities.formatFileSize(documentAttach.size));
            infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(str));
            infoLayout = new StaticLayout(str, Theme.chat_infoPaint, infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            return 0;
        } else {
            drawPhotoImage = documentAttach.mime_type != null && documentAttach.mime_type.toLowerCase().startsWith("image/") || documentAttach.thumb instanceof TLRPC.TL_photoSize && !(documentAttach.thumb.location instanceof TLRPC.TL_fileLocationUnavailable);
            if (!drawPhotoImage) {
                maxWidth += AndroidUtilities.dp(30);
            }
            documentAttachType = DOCUMENT_ATTACH_TYPE_DOCUMENT;
            String name = FileLoader.getDocumentFileName(documentAttach);
            if (name == null || name.length() == 0) {
                name = LocaleController.getString("AttachDocument", R.string.AttachDocument);
            }
            docTitleLayout = StaticLayoutEx.createStaticLayout(name, Theme.chat_docNamePaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.MIDDLE, maxWidth, drawPhotoImage ? 2 : 1);
            docTitleOffsetX = Integer.MIN_VALUE;
            int width;
            if (docTitleLayout != null && docTitleLayout.getLineCount() > 0) {
                int maxLineWidth = 0;
                for (int a = 0; a < docTitleLayout.getLineCount(); a++) {
                    maxLineWidth = Math.max(maxLineWidth, (int) Math.ceil(docTitleLayout.getLineWidth(a)));
                    docTitleOffsetX = Math.max(docTitleOffsetX, (int) Math.ceil(-docTitleLayout.getLineLeft(a)));
                }
                width = Math.min(maxWidth, maxLineWidth);
            } else {
                width = maxWidth;
                docTitleOffsetX = 0;
            }

            String str = AndroidUtilities.formatFileSize(documentAttach.size) + " " + FileLoader.getDocumentExtension(documentAttach);
            infoWidth = Math.min(maxWidth - AndroidUtilities.dp(30), (int) Math.ceil(Theme.chat_infoPaint.measureText(str)));
            this.infoLayout2 = null;
            if (this.isChat) {
                String senderName = getCurrentNameString(messageObject);
                this.infoWidth2 = Math.min(maxWidth - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), (int) Math.ceil((double) senderPaint.measureText(senderName)));
                this.infoLayout2 = new StaticLayout(TextUtils.ellipsize(senderName, senderPaint, (float) this.infoWidth2, TextUtils.TruncateAt.END), senderPaint, this.infoWidth2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.infoWidth = Math.max(this.infoWidth2, this.infoWidth);
            }
            CharSequence str2 = TextUtils.ellipsize(str, Theme.chat_infoPaint, infoWidth, TextUtils.TruncateAt.END);
            try {
                if (infoWidth < 0) {
                    infoWidth = AndroidUtilities.dp(10);
                }
                infoLayout = new StaticLayout(str2, Theme.chat_infoPaint, infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Exception e) {
                FileLog.e(e);
            }

            if (drawPhotoImage) {
                currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                photoImage.setNeedsQualityThumb(true);
                photoImage.setShouldGenerateQualityThumb(true);
                photoImage.setParentMessageObject(messageObject);
                if (currentPhotoObject != null) {
                    currentPhotoFilter = "86_86_b";
                    photoImage.setImage(null, null, null, null, currentPhotoObject.location, currentPhotoFilter, 0, null, true);
                } else {
                    photoImage.setImageBitmap((BitmapDrawable) null);
                }
            }
            return width;
        }
    }

    private void calcBackgroundWidth(int maxWidth, int timeMore, int maxChildWidth) {
        if (hasLinkPreview || hasGamePreview || hasInvoicePreview || maxWidth - currentMessageObject.lastLineWidth < timeMore || currentMessageObject.hasRtl) {
            totalHeight += AndroidUtilities.dp(14);
            backgroundWidth = Math.max(maxChildWidth, currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31);
            backgroundWidth = Math.max(backgroundWidth, timeWidth + AndroidUtilities.dp(31));
        } else {
            int diff = maxChildWidth - currentMessageObject.lastLineWidth;
            if (diff >= 0 && diff <= timeMore) {
                backgroundWidth = maxChildWidth + timeMore - diff + AndroidUtilities.dp(31);
            } else {
                backgroundWidth = Math.max(maxChildWidth, currentMessageObject.lastLineWidth + timeMore) + AndroidUtilities.dp(31);
            }
        }
    }

    public void setHighlightedText(String text) {
        if (currentMessageObject.messageOwner.message == null || currentMessageObject == null || currentMessageObject.type != 0 || TextUtils.isEmpty(currentMessageObject.messageText) || text == null) {
            if (!urlPathSelection.isEmpty()) {
                linkSelectionBlockNum = -1;
                resetUrlPaths(true);
                invalidate();
            }
            return;
        }
        int start = TextUtils.indexOf(currentMessageObject.messageOwner.message.toLowerCase(), text.toLowerCase());
        if (start == -1) {
            if (!urlPathSelection.isEmpty()) {
                linkSelectionBlockNum = -1;
                resetUrlPaths(true);
                invalidate();
            }
            return;
        }
        int end = start + text.length();
        for (int c = 0; c < currentMessageObject.textLayoutBlocks.size(); c++) {
            MessageObject.TextLayoutBlock block = currentMessageObject.textLayoutBlocks.get(c);
            if (start >= block.charactersOffset && start < block.charactersOffset + block.textLayout.getText().length()) {
                linkSelectionBlockNum = c;
                resetUrlPaths(true);
                try {
                    LinkPath path = obtainNewUrlPath(true);
                    int length = block.textLayout.getText().length();
                    path.setCurrentLayout(block.textLayout, start, 0);
                    block.textLayout.getSelectionPath(start, end - block.charactersOffset, path);
                    if (end >= block.charactersOffset + length) {
                        for (int a = c + 1; a < currentMessageObject.textLayoutBlocks.size(); a++) {
                            MessageObject.TextLayoutBlock nextBlock = currentMessageObject.textLayoutBlocks.get(a);
                            length = nextBlock.textLayout.getText().length();
                            path = obtainNewUrlPath(true);
                            path.setCurrentLayout(nextBlock.textLayout, 0, nextBlock.height);
                            nextBlock.textLayout.getSelectionPath(0, end - nextBlock.charactersOffset, path);
                            if (end < block.charactersOffset + length - 1) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                invalidate();
                break;
            }
        }
    }

    public void setMessageObject(MessageObject messageObject, boolean bottomNear, boolean topNear) {
        if (messageObject.checkLayout()) {
            currentMessageObject = null;
        }
        boolean messageIdChanged = currentMessageObject == null || currentMessageObject.getId() != messageObject.getId();
        boolean messageChanged = currentMessageObject != messageObject || messageObject.forceUpdate;
        boolean dataChanged = currentMessageObject == messageObject && (isUserDataChanged() || photoNotSet);
        if (messageChanged || dataChanged || isPhotoDataChanged(messageObject) || pinnedBottom != bottomNear || pinnedTop != topNear) {
            pinnedBottom = bottomNear;
            pinnedTop = topNear;
            currentMessageObject = messageObject;
            lastSendState = messageObject.messageOwner.send_state;
            lastDeleteDate = messageObject.messageOwner.destroyTime;
            lastViewsCount = messageObject.messageOwner.views;
            isPressed = false;
            isCheckPressed = true;
            isAvatarVisible = false;
            wasLayout = false;
            drawShareButton = checkNeedDrawShareButton(messageObject);
            replyNameLayout = null;
            replyTextLayout = null;
            replyNameWidth = 0;
            replyTextWidth = 0;
            viaWidth = 0;
            viaNameWidth = 0;
            currentReplyPhoto = null;
            currentUser = null;
            currentChat = null;
            currentViaBotUser = null;
            drawNameLayout = false;

            resetPressedLink(-1);
            messageObject.forceUpdate = false;
            drawPhotoImage = false;
            hasLinkPreview = false;
            hasGamePreview = false;
            hasInvoicePreview = false;
            instantPressed = false;
            linkPreviewPressed = false;
            buttonPressed = 0;
            pressedBotButton = -1;
            linkPreviewHeight = 0;
            mediaOffsetY = 0;
            documentAttachType = DOCUMENT_ATTACH_TYPE_NONE;
            documentAttach = null;
            descriptionLayout = null;
            titleLayout = null;
            videoInfoLayout = null;
            siteNameLayout = null;
            authorLayout = null;
            captionLayout = null;
            docTitleLayout = null;
            drawImageButton = false;
            currentPhotoObject = null;
            currentPhotoObjectThumb = null;
            currentPhotoFilter = null;
            infoLayout = null;
            cancelLoading = false;
            buttonState = -1;
            currentUrl = null;
            photoNotSet = false;
            drawBackground = true;
            drawName = false;
            useSeekBarWaweform = false;
            drawInstantView = false;
            drawForwardedName = false;
            mediaBackground = false;
            availableTimeWidth = 0;
            photoImage.setNeedsQualityThumb(false);
            photoImage.setShouldGenerateQualityThumb(false);
            photoImage.setParentMessageObject(null);
            photoImage.setRoundRadius(AndroidUtilities.dp(3));
            if (currentMessageObject.isFromUser() && MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id)).bot) {
                isAvatarVisible = false;
                showMyAvatar = false;
                showMyAvatarGroup = false;
                showAvatar = false;
            }
            if (messageChanged) {
                firstVisibleBlockNum = 0;
                lastVisibleBlockNum = 0;
                needNewVisiblePart = true;
            }

            if (messageObject.type == 0) {
                drawForwardedName = true;

                int maxWidth;
                if (AndroidUtilities.isTablet()) {
                    if (((this.isChat || this.showAvatar) && !messageObject.isOutOwner() && messageObject.isFromUser()) || (((this.showMyAvatar && !this.isChat) || (this.showMyAvatarGroup && this.isChat)) && messageObject.isOutOwner())) {
                        maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp((float) (this.leftBound + 74));
                        drawName = true;
                    } else {
                        drawName = messageObject.messageOwner.to_id.channel_id != 0 && !messageObject.isOutOwner();
                        maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(80);
                    }
                } else {
//                } else if (((this.isChat || this.showAvatar) && !messageObject.isOutOwner() && messageObject.isFromUser()) || (((this.showMyAvatar && !this.isChat) || (this.showMyAvatarGroup && this.isChat)) && messageObject.isOutOwner())) {
                    if (((this.isChat || this.showAvatar) && !messageObject.isOutOwner() && messageObject.isFromUser()) || (((this.showMyAvatar && !this.isChat) || (this.showMyAvatarGroup && this.isChat)) && messageObject.isOutOwner())) {
                        maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(122);
                        drawName = true;
                    } else {
                        maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(80);
                        drawName = messageObject.messageOwner.to_id.channel_id != 0 && !messageObject.isOutOwner();
                    }
                }
                availableTimeWidth = maxWidth;
                measureTime(messageObject);
                int timeMore = timeWidth + AndroidUtilities.dp(6);
                if (messageObject.isOutOwner()) {
                    timeMore += AndroidUtilities.dp(20.5f);
                }

                hasGamePreview = messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame && messageObject.messageOwner.media.game instanceof TLRPC.TL_game;
                hasInvoicePreview = messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice;
                hasLinkPreview = messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && messageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage;
                drawInstantView = Build.VERSION.SDK_INT >= 16 && hasLinkPreview && messageObject.messageOwner.media.webpage.cached_page != null;
                backgroundWidth = maxWidth;
                if (hasLinkPreview || hasGamePreview || hasInvoicePreview || maxWidth - messageObject.lastLineWidth < timeMore) {
                    backgroundWidth = Math.max(backgroundWidth, messageObject.lastLineWidth) + AndroidUtilities.dp(31);
                    backgroundWidth = Math.max(backgroundWidth, timeWidth + AndroidUtilities.dp(31));
                } else {
                    int diff = backgroundWidth - messageObject.lastLineWidth;
                    if (diff >= 0 && diff <= timeMore) {
                        backgroundWidth = backgroundWidth + timeMore - diff + AndroidUtilities.dp(31);
                    } else {
                        backgroundWidth = Math.max(backgroundWidth, messageObject.lastLineWidth + timeMore) + AndroidUtilities.dp(31);
                    }
                }
                availableTimeWidth = backgroundWidth - AndroidUtilities.dp(31);

                setMessageObjectInternal(messageObject);

                backgroundWidth = messageObject.textWidth + ((hasGamePreview || hasInvoicePreview) ? AndroidUtilities.dp(10) : 0);
                totalHeight = messageObject.textHeight + AndroidUtilities.dp(19.5f) + namesOffset;
                if (pinnedTop) {
                    namesOffset -= AndroidUtilities.dp(1);
                }

                int maxChildWidth = Math.max(backgroundWidth, nameWidth);
                maxChildWidth = Math.max(maxChildWidth, forwardedNameWidth);
                maxChildWidth = Math.max(maxChildWidth, replyNameWidth);
                maxChildWidth = Math.max(maxChildWidth, replyTextWidth);
                int maxWebWidth = 0;

                if (hasLinkPreview || hasGamePreview || hasInvoicePreview) {
                    int linkPreviewMaxWidth;
                    if (AndroidUtilities.isTablet()) {
                        if (messageObject.isFromUser() && (currentMessageObject.messageOwner.to_id.channel_id != 0 || currentMessageObject.messageOwner.to_id.chat_id != 0) && !currentMessageObject.isOut()) {
                            linkPreviewMaxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(122);
                        } else {
                            linkPreviewMaxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp((float) (this.leftBound + 70));
//                            linkPreviewMaxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(80);
                        }
                    } else {
                        if ((!messageObject.isFromUser() || ((this.currentMessageObject.messageOwner.to_id.channel_id == 0 && this.currentMessageObject.messageOwner.to_id.chat_id == 0 && !this.showAvatar) || this.currentMessageObject.isOutOwner())) && !(this.showMyAvatar && this.currentMessageObject.isOutOwner())) {
                            linkPreviewMaxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp((float) (this.leftBound + 70));
//                            linkPreviewMaxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(122);
                        } else {
                            linkPreviewMaxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(80);
                        }
                    }
                    if (drawShareButton) {
                        linkPreviewMaxWidth -= AndroidUtilities.dp(20);
                    }
                    String site_name;
                    String title;
                    String author;
                    String description;
                    TLRPC.Photo photo;
                    TLRPC.Document document;
                    TLRPC.TL_webDocument webDocument;
                    int duration;
                    boolean smallImage;
                    String type;
                    if (hasLinkPreview) {
                        TLRPC.TL_webPage webPage = (TLRPC.TL_webPage) messageObject.messageOwner.media.webpage;
                        site_name = webPage.site_name;
                        title = webPage.title;
                        author = webPage.author;
                        description = webPage.description;
                        photo = webPage.photo;
                        webDocument = null;
                        document = webPage.document;
                        type = webPage.type;
                        duration = webPage.duration;
                        if (site_name != null && photo != null && site_name.toLowerCase().equals("instagram")) {
                            linkPreviewMaxWidth = Math.max(AndroidUtilities.displaySize.y / 3, currentMessageObject.textWidth);
                        }
                        smallImage = !drawInstantView && type != null && (type.equals("app") || type.equals("profile") || type.equals("article"));
                        isSmallImage = !drawInstantView && description != null && type != null && (type.equals("app") || type.equals("profile") || type.equals("article")) && currentMessageObject.photoThumbs != null;
                    } else if (hasInvoicePreview) {
                        site_name = messageObject.messageOwner.media.title;
                        title = null;
                        description = null;
                        photo = null;
                        author = null;
                        document = null;
                        webDocument = ((TLRPC.TL_messageMediaInvoice) messageObject.messageOwner.media).photo;
                        duration = 0;
                        type = "invoice";
                        isSmallImage = false;
                        smallImage = false;
                    } else {
                        TLRPC.TL_game game = messageObject.messageOwner.media.game;
                        site_name = game.title;
                        title = null;
                        webDocument = null;
                        description = TextUtils.isEmpty(messageObject.messageText) ? game.description : null;
                        photo = game.photo;
                        author = null;
                        document = game.document;
                        duration = 0;
                        type = "game";
                        isSmallImage = false;
                        smallImage = false;
                    }

                    int additinalWidth = hasInvoicePreview ? 0 : AndroidUtilities.dp(10);
                    int restLinesCount = 3;
                    int additionalHeight = 0;
                    linkPreviewMaxWidth -= additinalWidth;

                    if (currentMessageObject.photoThumbs == null && photo != null) {
                        currentMessageObject.generateThumbs(true);
                    }

                    if (site_name != null) {
                        try {
                            int width = (int) Math.ceil(Theme.chat_replyNamePaint.measureText(site_name));
                            siteNameLayout = new StaticLayout(site_name, Theme.chat_replyNamePaint, Math.min(width, linkPreviewMaxWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            int height = siteNameLayout.getLineBottom(siteNameLayout.getLineCount() - 1);
                            linkPreviewHeight += height;
                            totalHeight += height;
                            additionalHeight += height;
                            width = siteNameLayout.getWidth();
                            maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                            maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }

                    boolean titleIsRTL = false;
                    if (title != null) {
                        try {
                            titleX = Integer.MAX_VALUE;
                            if (linkPreviewHeight != 0) {
                                linkPreviewHeight += AndroidUtilities.dp(2);
                                totalHeight += AndroidUtilities.dp(2);
                            }
                            int restLines = 0;
                            if (!isSmallImage || description == null) {
                                titleLayout = StaticLayoutEx.createStaticLayout(title, Theme.chat_replyNamePaint, linkPreviewMaxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, AndroidUtilities.dp(1), false, TextUtils.TruncateAt.END, linkPreviewMaxWidth, 4);
                            } else {
                                restLines = restLinesCount;
                                titleLayout = generateStaticLayout(title, Theme.chat_replyNamePaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(48 + 4), restLinesCount, 4);
                                restLinesCount -= titleLayout.getLineCount();
                            }
                            int height = titleLayout.getLineBottom(titleLayout.getLineCount() - 1);
                            linkPreviewHeight += height;
                            totalHeight += height;
                            boolean checkForRtl = true;
                            for (int a = 0; a < titleLayout.getLineCount(); a++) {
                                int lineLeft = (int) titleLayout.getLineLeft(a);
                                if (lineLeft != 0) {
                                    titleIsRTL = true;
                                }
                                if (titleX == Integer.MAX_VALUE) {
                                    titleX = -lineLeft;
                                } else {
                                    titleX = Math.max(titleX, -lineLeft);
                                }
                                int width;
                                if (lineLeft != 0) {
                                    width = titleLayout.getWidth() - lineLeft;
                                } else {
                                    width = (int) Math.ceil(titleLayout.getLineWidth(a));
                                }
                                if (a < restLines || lineLeft != 0 && isSmallImage) {
                                    width += AndroidUtilities.dp(48 + 4);
                                }
                                maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                                maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }

                    boolean authorIsRTL = false;
                    if (author != null && title == null) {
                        try {
                            if (linkPreviewHeight != 0) {
                                linkPreviewHeight += AndroidUtilities.dp(2);
                                totalHeight += AndroidUtilities.dp(2);
                            }
                            if (restLinesCount == 3 && (!isSmallImage || description == null)) {
                                authorLayout = new StaticLayout(author, Theme.chat_replyNamePaint, linkPreviewMaxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            } else {
                                authorLayout = generateStaticLayout(author, Theme.chat_replyNamePaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(48 + 4), restLinesCount, 1);
                                restLinesCount -= authorLayout.getLineCount();
                            }
                            int height = authorLayout.getLineBottom(authorLayout.getLineCount() - 1);
                            linkPreviewHeight += height;
                            totalHeight += height;
                            int lineLeft = (int) authorLayout.getLineLeft(0);
                            authorX = -lineLeft;
                            int width;
                            if (lineLeft != 0) {
                                width = authorLayout.getWidth() - lineLeft;
                                authorIsRTL = true;
                            } else {
                                width = (int) Math.ceil(authorLayout.getLineWidth(0));
                            }
                            maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                            maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }

                    if (description != null) {
                        try {
                            descriptionX = 0;
                            currentMessageObject.generateLinkDescription();
                            if (linkPreviewHeight != 0) {
                                linkPreviewHeight += AndroidUtilities.dp(2);
                                totalHeight += AndroidUtilities.dp(2);
                            }
                            int restLines = 0;
                            if (restLinesCount == 3 && !isSmallImage) {
                                descriptionLayout = StaticLayoutEx.createStaticLayout(messageObject.linkDescription, Theme.chat_replyTextPaint, linkPreviewMaxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, AndroidUtilities.dp(1), false, TextUtils.TruncateAt.END, linkPreviewMaxWidth, 6);
                            } else {
                                restLines = restLinesCount;
                                descriptionLayout = generateStaticLayout(messageObject.linkDescription, Theme.chat_replyTextPaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(48 + 4), restLinesCount, 6);
                            }
                            int height = descriptionLayout.getLineBottom(descriptionLayout.getLineCount() - 1);
                            linkPreviewHeight += height;
                            totalHeight += height;

                            boolean hasRTL = false;
                            for (int a = 0; a < descriptionLayout.getLineCount(); a++) {
                                int lineLeft = (int) Math.ceil(descriptionLayout.getLineLeft(a));
                                if (lineLeft != 0) {
                                    hasRTL = true;
                                    if (descriptionX == 0) {
                                        descriptionX = -lineLeft;
                                    } else {
                                        descriptionX = Math.max(descriptionX, -lineLeft);
                                    }
                                }
                            }

                            int textWidth = descriptionLayout.getWidth();
                            for (int a = 0; a < descriptionLayout.getLineCount(); a++) {
                                int lineLeft = (int) Math.ceil(descriptionLayout.getLineLeft(a));
                                if (lineLeft == 0 && descriptionX != 0) {
                                    descriptionX = 0;
                                }

                                int width;
                                if (lineLeft != 0) {
                                    width = textWidth - lineLeft;
                                } else {
                                    if (hasRTL) {
                                        width = textWidth;
                                    } else {
                                        width = Math.min((int) Math.ceil(descriptionLayout.getLineWidth(a)), textWidth);
                                    }
                                }
                                if (a < restLines || restLines != 0 && lineLeft != 0 && isSmallImage) {
                                    width += AndroidUtilities.dp(48 + 4);
                                }
                                if (maxWebWidth < width + additinalWidth) {
                                    if (titleIsRTL) {
                                        titleX += (width + additinalWidth - maxWebWidth);
                                    }
                                    if (authorIsRTL) {
                                        authorX += (width + additinalWidth - maxWebWidth);
                                    }
                                    maxWebWidth = width + additinalWidth;
                                }
                                maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }

                    if (smallImage && (descriptionLayout == null || descriptionLayout != null && descriptionLayout.getLineCount() == 1)) {
                        smallImage = false;
                        isSmallImage = false;
                    }
                    int maxPhotoWidth = smallImage ? AndroidUtilities.dp(48) : linkPreviewMaxWidth;

                    if (document != null) {
                        if (MessageObject.isGifDocument(document)){
                            if (!MediaController.getInstance().canAutoplayGifs()) {
                                messageObject.audioProgress = 1;
                            }
                            photoImage.setAllowStartAnimation(messageObject.audioProgress != 1);
                            currentPhotoObject = document.thumb;
                            if (currentPhotoObject != null && (currentPhotoObject.w == 0 || currentPhotoObject.h == 0)) {
                                for (int a = 0; a < document.attributes.size(); a++) {
                                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize || attribute instanceof TLRPC.TL_documentAttributeVideo) {
                                        currentPhotoObject.w = attribute.w;
                                        currentPhotoObject.h = attribute.h;
                                        break;
                                    }
                                }
                                if (currentPhotoObject.w == 0 || currentPhotoObject.h == 0) {
                                    currentPhotoObject.w = currentPhotoObject.h = AndroidUtilities.dp(150);
                                }
                            }
                            documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                        } else if (MessageObject.isVideoDocument(document)) {
                            currentPhotoObject = document.thumb;
                            if (currentPhotoObject != null && (currentPhotoObject.w == 0 || currentPhotoObject.h == 0)) {
                                for (int a = 0; a < document.attributes.size(); a++) {
                                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                                    if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                                        currentPhotoObject.w = attribute.w;
                                        currentPhotoObject.h = attribute.h;
                                        break;
                                    }
                                }
                                if (currentPhotoObject.w == 0 || currentPhotoObject.h == 0) {
                                    currentPhotoObject.w = currentPhotoObject.h = AndroidUtilities.dp(150);
                                }
                            }
                            createDocumentLayout(0, messageObject);
                        } else if (MessageObject.isStickerDocument(document)) {
                            currentPhotoObject = document.thumb;
                            if (currentPhotoObject != null && (currentPhotoObject.w == 0 || currentPhotoObject.h == 0)) {
                                for (int a = 0; a < document.attributes.size(); a++) {
                                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                                        currentPhotoObject.w = attribute.w;
                                        currentPhotoObject.h = attribute.h;
                                        break;
                                    }
                                }
                                if (currentPhotoObject.w == 0 || currentPhotoObject.h == 0) {
                                    currentPhotoObject.w = currentPhotoObject.h = AndroidUtilities.dp(150);
                                }
                            }
                            documentAttach = document;
                            documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                        } else {
                            calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                            if (!MessageObject.isStickerDocument(document)) {
                                if (backgroundWidth < maxWidth + AndroidUtilities.dp(20)) {
                                    backgroundWidth = maxWidth + AndroidUtilities.dp(20);
                                }
                                if (MessageObject.isVoiceDocument(document)) {
                                    createDocumentLayout(backgroundWidth - AndroidUtilities.dp(10), messageObject);
                                    mediaOffsetY = currentMessageObject.textHeight + AndroidUtilities.dp(8) + linkPreviewHeight;
                                    totalHeight += AndroidUtilities.dp(30 + 14);
                                    linkPreviewHeight += AndroidUtilities.dp(44);
                                    calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                                } else if (MessageObject.isMusicDocument(document)) {
                                    int durationWidth = createDocumentLayout(backgroundWidth - AndroidUtilities.dp(10), messageObject);
                                    mediaOffsetY = currentMessageObject.textHeight + AndroidUtilities.dp(8) + linkPreviewHeight;
                                    totalHeight += AndroidUtilities.dp(42 + 14);
                                    linkPreviewHeight += AndroidUtilities.dp(56);

                                    maxWidth = maxWidth - AndroidUtilities.dp(86);
                                    maxChildWidth = Math.max(maxChildWidth, durationWidth + additinalWidth + AndroidUtilities.dp(86 + 8));
                                    if (songLayout != null && songLayout.getLineCount() > 0) {
                                        maxChildWidth = (int) Math.max(maxChildWidth, songLayout.getLineWidth(0) + additinalWidth + AndroidUtilities.dp(86));
                                    }
                                    if (performerLayout != null && performerLayout.getLineCount() > 0) {
                                        maxChildWidth = (int) Math.max(maxChildWidth, performerLayout.getLineWidth(0) + additinalWidth + AndroidUtilities.dp(86));
                                    }

                                    calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                                } else {
                                    createDocumentLayout(backgroundWidth - AndroidUtilities.dp(86 + 24 + 58), messageObject);
                                    drawImageButton = true;
                                    if (drawPhotoImage) {
                                        totalHeight += AndroidUtilities.dp(86 + 14);
                                        linkPreviewHeight += AndroidUtilities.dp(86);
                                        photoImage.setImageCoords(0, totalHeight + namesOffset, AndroidUtilities.dp(86), AndroidUtilities.dp(86));
                                    } else {
                                        mediaOffsetY = currentMessageObject.textHeight + AndroidUtilities.dp(8) + linkPreviewHeight;
                                        photoImage.setImageCoords(0, totalHeight + namesOffset - AndroidUtilities.dp(14), AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                                        totalHeight += AndroidUtilities.dp(50 + 14);
                                        linkPreviewHeight += AndroidUtilities.dp(50);
                                    }
                                }
                            }
                        }
                    } else if (photo != null) {
                        drawImageButton = type != null && type.equals("photo");
                        currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, drawImageButton ? AndroidUtilities.getPhotoSize() : maxPhotoWidth, !drawImageButton);
                        currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                        if (currentPhotoObjectThumb == currentPhotoObject) {
                            currentPhotoObjectThumb = null;
                        }
                    } else if (webDocument != null) {
                        if (!webDocument.mime_type.startsWith("image/")) {
                            webDocument = null;
                        }
                        drawImageButton = false;
                    }

                    if (documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC && documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        if (currentPhotoObject != null || webDocument != null) {
                            drawImageButton = type != null && (type.equals("photo") || type.equals("document") && documentAttachType != DOCUMENT_ATTACH_TYPE_STICKER || type.equals("gif") || documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO);
                            if (linkPreviewHeight != 0) {
                                linkPreviewHeight += AndroidUtilities.dp(2);
                                totalHeight += AndroidUtilities.dp(2);
                            }

                            if (documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
                                if (AndroidUtilities.isTablet()) {
                                    maxPhotoWidth = (int) (AndroidUtilities.getMinTabletSide() * 0.5f);
                                } else {
                                    maxPhotoWidth = (int) (AndroidUtilities.displaySize.x * 0.5f);
                                }
                            }

                            maxChildWidth = Math.max(maxChildWidth, maxPhotoWidth - (hasInvoicePreview ? AndroidUtilities.dp(12) : 0)  + additinalWidth);
                            if (currentPhotoObject != null) {
                                currentPhotoObject.size = -1;
                                if (currentPhotoObjectThumb != null) {
                                    currentPhotoObjectThumb.size = -1;
                                }
                            } else {
                                webDocument.size = -1;
                            }

                            int width;
                            int height;
                            if (smallImage) {
                                width = height = maxPhotoWidth;
                            } else {
                                if (hasGamePreview || hasInvoicePreview) {
                                    width = 640;
                                    height = 360;
                                    float scale = width / (float) (maxPhotoWidth - AndroidUtilities.dp(2));
                                    width /= scale;
                                    height /= scale;
                                } else {
                                    width = currentPhotoObject.w;
                                    height = currentPhotoObject.h;
                                    float scale = width / (float) (maxPhotoWidth - AndroidUtilities.dp(2));
                                    width /= scale;
                                    height /= scale;
                                    if (site_name == null || site_name != null && !site_name.toLowerCase().equals("instagram") && documentAttachType == 0) {
                                        if (height > AndroidUtilities.displaySize.y / 3) {
                                            height = AndroidUtilities.displaySize.y / 3;
                                        }
                                    }
                                }
                            }
                            if (isSmallImage) {
                                if (AndroidUtilities.dp(50) + additionalHeight > linkPreviewHeight) {
                                    totalHeight += AndroidUtilities.dp(50) + additionalHeight - linkPreviewHeight + AndroidUtilities.dp(8);
                                    linkPreviewHeight = AndroidUtilities.dp(50) + additionalHeight;
                                }
                                linkPreviewHeight -= AndroidUtilities.dp(8);
                            } else {
                                totalHeight += height + AndroidUtilities.dp(12);
                                linkPreviewHeight += height;
                            }

                            photoImage.setImageCoords(0, 0, width, height);

                            currentPhotoFilter = String.format(Locale.US, "%d_%d", width, height);
                            currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", width, height);

                            if (webDocument != null) {
                                photoImage.setImage(webDocument, null, currentPhotoFilter, null, null, "b1", webDocument.size, null, true);
                            } else {
                                if (documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
                                    photoImage.setImage(documentAttach, null, currentPhotoFilter, null, currentPhotoObject != null ? currentPhotoObject.location : null, "b1", documentAttach.size, "webp", true);
                                } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                                    photoImage.setImage(null, null, currentPhotoObject.location, currentPhotoFilter, 0, null, false);
                                } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_GIF) {
                                    boolean photoExist = messageObject.mediaExists;
                                    String fileName = FileLoader.getAttachFileName(document);
                                    if (hasGamePreview || photoExist || MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_GIF) || FileLoader.getInstance().isLoadingFile(fileName)) {
                                        photoNotSet = false;
                                        photoImage.setImage(document, null, currentPhotoObject.location, currentPhotoFilter, document.size, null, false);
                                    } else {
                                        photoNotSet = true;
                                        photoImage.setImage(null, null, currentPhotoObject.location, currentPhotoFilter, 0, null, false);
                                    }
                                } else {
                                    boolean photoExist = messageObject.mediaExists;
                                    String fileName = FileLoader.getAttachFileName(currentPhotoObject);
                                    if (hasGamePreview || photoExist || MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_PHOTO) || FileLoader.getInstance().isLoadingFile(fileName)) {
                                        photoNotSet = false;
                                        photoImage.setImage(currentPhotoObject.location, currentPhotoFilter, currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null, currentPhotoFilterThumb, 0, null, false);
                                    } else {
                                        photoNotSet = true;
                                        if (currentPhotoObjectThumb != null) {
                                            photoImage.setImage(null, null, currentPhotoObjectThumb.location, String.format(Locale.US, "%d_%d_b", width, height), 0, null, false);
                                        } else {
                                            photoImage.setImageBitmap((Drawable) null);
                                        }
                                    }
                                }
                            }
                            drawPhotoImage = true;

                            if (type != null && type.equals("video") && duration != 0) {
                                int minutes = duration / 60;
                                int seconds = duration - minutes * 60;
                                String str = String.format("%d:%02d", minutes, seconds);
                                durationWidth = (int) Math.ceil(Theme.chat_durationPaint.measureText(str));
                                videoInfoLayout = new StaticLayout(str, Theme.chat_durationPaint, durationWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            } else if (hasGamePreview) {
                                String str = LocaleController.getString("AttachGame", R.string.AttachGame).toUpperCase();
                                durationWidth = (int) Math.ceil(Theme.chat_gamePaint.measureText(str));
                                videoInfoLayout = new StaticLayout(str, Theme.chat_gamePaint, durationWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            }
                        } else {
                            photoImage.setImageBitmap((Drawable) null);
                            linkPreviewHeight -= AndroidUtilities.dp(6);
                            totalHeight += AndroidUtilities.dp(4);
                        }
                        if (hasInvoicePreview) {
                            CharSequence str;
                            if ((messageObject.messageOwner.media.flags & 4) != 0) {
                                str = LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt).toUpperCase();
                            } else {
                                if (messageObject.messageOwner.media.test) {
                                    str = LocaleController.getString("PaymentTestInvoice", R.string.PaymentTestInvoice).toUpperCase();
                                } else {
                                    str = LocaleController.getString("PaymentInvoice", R.string.PaymentInvoice).toUpperCase();
                                }
                            }
                            String price = LocaleController.getInstance().formatCurrencyString(messageObject.messageOwner.media.total_amount, messageObject.messageOwner.media.currency);
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(price + " " + str);
                            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            durationWidth = (int) Math.ceil(Theme.chat_shipmentPaint.measureText(stringBuilder, 0, stringBuilder.length()));
                            videoInfoLayout = new StaticLayout(stringBuilder, Theme.chat_shipmentPaint, durationWidth + AndroidUtilities.dp(10), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            if (!drawPhotoImage) {
                                totalHeight += AndroidUtilities.dp(6);
                                if (durationWidth + timeWidth + AndroidUtilities.dp(6) > maxWidth) {
                                    maxChildWidth = Math.max(durationWidth, maxChildWidth);
                                    totalHeight += AndroidUtilities.dp(12);
                                } else {
                                    maxChildWidth = Math.max(durationWidth + timeWidth + AndroidUtilities.dp(6), maxChildWidth);
                                }
                            }
                        }
                        if (hasGamePreview && messageObject.textHeight != 0) {
                            linkPreviewHeight += messageObject.textHeight + AndroidUtilities.dp(6);
                            totalHeight += AndroidUtilities.dp(4);
                        }
                        calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                    }
                    if (drawInstantView) {
                        instantWidth = AndroidUtilities.dp(12 + 9 + 12);
                        String str = LocaleController.getString("InstantView", R.string.InstantView);
                        int mWidth = backgroundWidth - AndroidUtilities.dp(10 + 24 + 10 + 31);
                        instantViewLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, mWidth, TextUtils.TruncateAt.END), Theme.chat_instantViewPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (instantViewLayout != null && instantViewLayout.getLineCount() > 0) {
                            instantTextX = (int) -instantViewLayout.getLineLeft(0);
                            instantWidth += instantViewLayout.getLineWidth(0) + instantTextX;
                        }
                        linkPreviewHeight += AndroidUtilities.dp(40);
                        totalHeight += AndroidUtilities.dp(40);
                    }
                } else {
                    photoImage.setImageBitmap((Drawable) null);
                    calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                }
            } else if (messageObject.type == 16) {
                drawName = false;
                drawForwardedName = false;
                drawPhotoImage = false;
                if (AndroidUtilities.isTablet()) {
                    backgroundWidth = Math.min(AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                } else {
                    backgroundWidth = Math.min(AndroidUtilities.displaySize.x - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                }
                availableTimeWidth = backgroundWidth - AndroidUtilities.dp(31);

                int maxWidth = getMaxNameWidth() - AndroidUtilities.dp(50);
                if (maxWidth < 0) {
                    maxWidth = AndroidUtilities.dp(10);
                }

                String text;
                String time = LocaleController.getInstance().formatterDay.format((long) (messageObject.messageOwner.date) * 1000);
                TLRPC.TL_messageActionPhoneCall call = (TLRPC.TL_messageActionPhoneCall) messageObject.messageOwner.action;
                boolean isMissed = call.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed;
                if (messageObject.isOutOwner()) {
                    if (isMissed) {
                        text = LocaleController.getString("CallMessageOutgoingMissed", R.string.CallMessageOutgoingMissed);
                    } else {
                        text = LocaleController.getString("CallMessageOutgoing", R.string.CallMessageOutgoing);
                    }
                } else {
                    if (isMissed) {
                        text = LocaleController.getString("CallMessageIncomingMissed", R.string.CallMessageIncomingMissed);
                    } else if(call.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
						text = LocaleController.getString("CallMessageIncomingDeclined", R.string.CallMessageIncomingDeclined);
					}  else {
                        text = LocaleController.getString("CallMessageIncoming", R.string.CallMessageIncoming);
                    }
                }
                if (call.duration > 0) {
                    time += ", " + LocaleController.formatCallDuration(call.duration);
                }

                titleLayout = new StaticLayout(TextUtils.ellipsize(text, Theme.chat_audioTitlePaint, maxWidth, TextUtils.TruncateAt.END), Theme.chat_audioTitlePaint, maxWidth + AndroidUtilities.dp(2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                docTitleLayout = new StaticLayout(TextUtils.ellipsize(time, Theme.chat_contactPhonePaint, maxWidth, TextUtils.TruncateAt.END), Theme.chat_contactPhonePaint, maxWidth + AndroidUtilities.dp(2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                setMessageObjectInternal(messageObject);

                totalHeight = AndroidUtilities.dp(65) + namesOffset;
                if (pinnedTop) {
                    namesOffset -= AndroidUtilities.dp(1);
                }
            } else if (messageObject.type == 12) {
                drawName = false;
                drawForwardedName = true;
                drawPhotoImage = true;
                photoImage.setRoundRadius(AndroidUtilities.dp(22));
                if (AndroidUtilities.isTablet()) {
                    backgroundWidth = Math.min(AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                } else {
                    backgroundWidth = Math.min(AndroidUtilities.displaySize.x - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                }
                availableTimeWidth = backgroundWidth - AndroidUtilities.dp(31);

                int uid = messageObject.messageOwner.media.user_id;
                TLRPC.User user = MessagesController.getInstance().getUser(uid);

                int maxWidth = getMaxNameWidth() - AndroidUtilities.dp(110);
                if (maxWidth < 0) {
                    maxWidth = AndroidUtilities.dp(10);
                }

                TLRPC.FileLocation currentPhoto = null;
                if (user != null) {
                    if (user.photo != null) {
                        currentPhoto = user.photo.photo_small;
                    }
                    contactAvatarDrawable.setInfo(user);
                }
                if (uid == 0) {
                    this.contactAvatarDrawable.setColor(Theme.chatContactNameColor);
                }
                photoImage.setImage(currentPhoto, "50_50", user != null ? contactAvatarDrawable : Theme.chat_contactDrawable[messageObject.isOutOwner() ? 1 : 0], null, false);

                String phone = messageObject.messageOwner.media.phone_number;
                if (phone != null && phone.length() != 0) {
                    phone = PhoneFormat.getInstance().format(phone);
                } else {
                    phone = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                }

                CharSequence currentNameString = ContactsController.formatName(messageObject.messageOwner.media.first_name, messageObject.messageOwner.media.last_name).replace('\n', ' ');
                if (currentNameString.length() == 0) {
                    currentNameString = phone;
                }
                titleLayout = new StaticLayout(TextUtils.ellipsize(currentNameString, Theme.chat_contactNamePaint, maxWidth, TextUtils.TruncateAt.END), Theme.chat_contactNamePaint, maxWidth + AndroidUtilities.dp(2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                docTitleLayout = new StaticLayout(TextUtils.ellipsize(phone.replace('\n', ' '), Theme.chat_contactPhonePaint, maxWidth, TextUtils.TruncateAt.END), Theme.chat_contactPhonePaint, maxWidth + AndroidUtilities.dp(2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                setMessageObjectInternal(messageObject);

                if (drawForwardedName && messageObject.isForwarded()) {
                    namesOffset += AndroidUtilities.dp(5);
                } else if (drawNameLayout && messageObject.messageOwner.reply_to_msg_id == 0) {
                    namesOffset += AndroidUtilities.dp(7);
                }

                totalHeight = AndroidUtilities.dp(70) + namesOffset;
                if (pinnedTop) {
                    namesOffset -= AndroidUtilities.dp(1);
                }
                if (docTitleLayout.getLineCount() > 0) {
                    int timeLeft = backgroundWidth - AndroidUtilities.dp(40 + 18 + 44 + 8) - (int) Math.ceil(docTitleLayout.getLineWidth(0));
                    if (timeLeft < timeWidth) {
                        totalHeight += AndroidUtilities.dp(8);
                    }
                }
            } else if (messageObject.type == 2) {
                drawForwardedName = true;
                if (AndroidUtilities.isTablet()) {
                    backgroundWidth = Math.min(AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                } else {
                    backgroundWidth = Math.min(AndroidUtilities.displaySize.x - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                }
                createDocumentLayout(backgroundWidth, messageObject);

                setMessageObjectInternal(messageObject);

                totalHeight = AndroidUtilities.dp(70) + namesOffset;
                if (pinnedTop) {
                    namesOffset -= AndroidUtilities.dp(1);
                }
            } else if (messageObject.type == 14) {
                if (AndroidUtilities.isTablet()) {
                    backgroundWidth = Math.min(AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                } else {
                    backgroundWidth = Math.min(AndroidUtilities.displaySize.x - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                }

                createDocumentLayout(backgroundWidth, messageObject);

                setMessageObjectInternal(messageObject);

                totalHeight = AndroidUtilities.dp(100.0f) + this.namesOffset;
                if (pinnedTop) {
                    namesOffset -= AndroidUtilities.dp(1);
                }
            } else {
                drawForwardedName = messageObject.messageOwner.fwd_from != null && messageObject.type != 13;
                mediaBackground = messageObject.type != 9;
                drawImageButton = true;
                drawPhotoImage = true;

                int photoWidth = 0;
                int photoHeight = 0;
                int additionHeight = 0;

                if (messageObject.audioProgress != 2 && !MediaController.getInstance().canAutoplayGifs() && messageObject.type == 8) {
                    messageObject.audioProgress = 1;
                }

                photoImage.setAllowStartAnimation(messageObject.audioProgress == 0);

                photoImage.setForcePreview(messageObject.isSecretPhoto());
                if (messageObject.type == 9) {
                    radialProgress.setSizeAndType((long) messageObject.messageOwner.media.document.size, messageObject.type);
                    if (AndroidUtilities.isTablet()) {
                        backgroundWidth = Math.min(AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                    } else {
                        backgroundWidth = Math.min(AndroidUtilities.displaySize.x - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                    }
                    if (checkNeedDrawShareButton(messageObject) && messageObject.messageOwner.to_id.channel_id <= 0) {                        backgroundWidth -= AndroidUtilities.dp(20);
                    }
                    int maxWidth = backgroundWidth - AndroidUtilities.dp(86 + 52);
                    createDocumentLayout(maxWidth, messageObject);
                    if (!TextUtils.isEmpty(messageObject.caption)) {
                        maxWidth += AndroidUtilities.dp(86);
                    }
                    if (drawPhotoImage) {
                        photoWidth = AndroidUtilities.dp(86);
                        photoHeight = AndroidUtilities.dp(86);
                    } else {
                        photoWidth = AndroidUtilities.dp(56);
                        photoHeight = AndroidUtilities.dp(56);
                        maxWidth += AndroidUtilities.dp(TextUtils.isEmpty(messageObject.caption) ? 51 : 21);
                    }
                    availableTimeWidth = maxWidth;
                    if (!drawPhotoImage) {
                        if (TextUtils.isEmpty(messageObject.caption) && infoLayout.getLineCount() > 0) {
                            measureTime(messageObject);
                            int timeLeft = backgroundWidth - AndroidUtilities.dp(40 + 18 + 56 + 8) - (int) Math.ceil(infoLayout.getLineWidth(0));
                            if (timeLeft < timeWidth) {
                                photoHeight += AndroidUtilities.dp(8);
                            }
                        }
                    }
                } else if (messageObject.type == 4) { //geo
                    double lat = messageObject.messageOwner.media.geo.lat;
                    double lon = messageObject.messageOwner.media.geo._long;

                    if (messageObject.messageOwner.media.title != null && messageObject.messageOwner.media.title.length() > 0) {
                        if (AndroidUtilities.isTablet()) {
                            backgroundWidth = Math.min(AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                        } else {
                            backgroundWidth = Math.min(AndroidUtilities.displaySize.x - AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 102 : 50), AndroidUtilities.dp(270));
                        }
                        if (checkNeedDrawShareButton(messageObject)) {
                            backgroundWidth -= AndroidUtilities.dp(20);
                        }
                        int maxWidth = backgroundWidth - AndroidUtilities.dp(86 + 37);

                        docTitleLayout = StaticLayoutEx.createStaticLayout(messageObject.messageOwner.media.title, Theme.chat_locationTitlePaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, maxWidth, 2);
                        int lineCount = docTitleLayout.getLineCount();
                        if (messageObject.messageOwner.media.address != null && messageObject.messageOwner.media.address.length() > 0) {
                            infoLayout = StaticLayoutEx.createStaticLayout(messageObject.messageOwner.media.address, Theme.chat_locationAddressPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, maxWidth, Math.min(3, 3 - lineCount));
                        } else {
                            infoLayout = null;
                        }

                        mediaBackground = false;
                        availableTimeWidth = maxWidth;
                        photoWidth = AndroidUtilities.dp(86);
                        photoHeight = AndroidUtilities.dp(86);
                        currentUrl = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", lat, lon, Math.min(2, (int) Math.ceil(AndroidUtilities.density)), lat, lon);
                    } else {
                        availableTimeWidth = AndroidUtilities.dp(200 - 14);
                        photoWidth = AndroidUtilities.dp(200);
                        photoHeight = AndroidUtilities.dp(100);
                        backgroundWidth = photoWidth + AndroidUtilities.dp(12);
                        currentUrl = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=200x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", lat, lon, Math.min(2, (int) Math.ceil(AndroidUtilities.density)), lat, lon);
                    }
                    photoImage.setImage(currentUrl, null, Theme.chat_locationDrawable[messageObject.isOutOwner() ? 1 : 0], null, 0);
                } else if (messageObject.type == 13) { //webp
                    drawBackground = false;
                    for (int a = 0; a < messageObject.messageOwner.media.document.attributes.size(); a++) {
                        TLRPC.DocumentAttribute attribute = messageObject.messageOwner.media.document.attributes.get(a);
                        if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                            photoWidth = attribute.w;
                            photoHeight = attribute.h;
                            break;
                        }
                    }
                    float maxHeight;
                    float maxWidth;
                    if (AndroidUtilities.isTablet()) {
                        maxHeight = maxWidth = AndroidUtilities.getMinTabletSide() * 0.4f;
                    } else {
                        maxHeight = maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f;
                    }
                    if (photoWidth == 0) {
                        photoHeight = (int) maxHeight;
                        photoWidth = photoHeight + AndroidUtilities.dp(100);
                    }
                    photoHeight *= maxWidth / photoWidth;
                    photoWidth = (int) maxWidth;
                    if (photoHeight > maxHeight) {
                        photoWidth *= maxHeight / photoHeight;
                        photoHeight = (int) maxHeight;
                    }
                    documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                    availableTimeWidth = photoWidth - AndroidUtilities.dp(14);
                    backgroundWidth = photoWidth + AndroidUtilities.dp(12);
                    currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                    if (messageObject.attachPathExists) {
                        photoImage.setImage(null, messageObject.messageOwner.attachPath,
                                String.format(Locale.US, "%d_%d", photoWidth, photoHeight),
                                null,
                                currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null,
                                "b1",
                                messageObject.messageOwner.media.document.size, "webp", true);
                    } else if (messageObject.messageOwner.media.document.id != 0) {
                        photoImage.setImage(messageObject.messageOwner.media.document, null,
                                String.format(Locale.US, "%d_%d", photoWidth, photoHeight),
                                null,
                                currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null,
                                "b1",
                                messageObject.messageOwner.media.document.size, "webp", true);
                    }
                    radialProgress.setSizeAndType((long) messageObject.messageOwner.media.document.size, messageObject.type);
                } else {
                    int maxPhotoWidth;
                    if (AndroidUtilities.isTablet()) {
                        maxPhotoWidth = photoWidth = (int) (AndroidUtilities.getMinTabletSide() * 0.7f);
                    } else {
                        maxPhotoWidth = photoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.68f);
                    }
                    photoHeight = photoWidth + AndroidUtilities.dp(100);
                    if (checkNeedDrawShareButton(messageObject)) {
                        maxPhotoWidth -= AndroidUtilities.dp(20);
                        photoWidth -= AndroidUtilities.dp(20);
                    }

                    if (photoWidth > AndroidUtilities.getPhotoSize()) {
                        photoWidth = AndroidUtilities.getPhotoSize();
                    }
                    if (photoHeight > AndroidUtilities.getPhotoSize()) {
                        photoHeight = AndroidUtilities.getPhotoSize();
                    }

                    if (messageObject.type == 1) { //photo
                        updateSecretTimeText(messageObject);
                        currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                    } else if (messageObject.type == 3) { //video
                        createDocumentLayout(0, messageObject);
                        radialProgress.setSizeAndType((long) messageObject.messageOwner.media.document.size, messageObject.type);
                        infoLayout2 = null;
                        photoImage.setNeedsQualityThumb(true);
                        photoImage.setShouldGenerateQualityThumb(true);
                        photoImage.setParentMessageObject(messageObject);
                    } else if (messageObject.type == 8) { //gif
                        String str = AndroidUtilities.formatFileSize(messageObject.messageOwner.media.document.size);
                        this.infoLayout2 = null;
                        this.radialProgress.setSizeAndType((long) messageObject.messageOwner.media.document.size, messageObject.type);
                        this.documentAttach = messageObject.messageOwner.media.document;
//                        duration = 0;
//                        for (a = 0; a < this.documentAttach.attributes.size(); a++) {
//                            attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
//                            if (attribute instanceof TL_documentAttributeVideo) {
//                                duration = attribute.duration;
//                                break;
//                            }
//                        }
//                        if (duration > 0) {
//                            seconds = duration - ((duration / 60) * 60);
//                            str = String.format("%d:%02d, %s", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.documentAttach.size)});
//                        } //TODO Multi
                        infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(str));
                        infoLayout = new StaticLayout(str, Theme.chat_infoPaint, infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                        photoImage.setNeedsQualityThumb(true);
                        photoImage.setShouldGenerateQualityThumb(true);
                        photoImage.setParentMessageObject(messageObject);
                    }

                    if (messageObject.caption != null) {
                        mediaBackground = false;
                    }

                    currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());

                    int w = 0;
                    int h = 0;

                    if (currentPhotoObject != null && currentPhotoObject == currentPhotoObjectThumb) {
                        currentPhotoObjectThumb = null;
                    }

                    if (currentPhotoObject != null) {
                        float scale = (float) currentPhotoObject.w / (float) photoWidth;
                        w = (int) (currentPhotoObject.w / scale);
                        h = (int) (currentPhotoObject.h / scale);
                        if (w == 0) {
                            w = AndroidUtilities.dp(150);
                        }
                        if (h == 0) {
                            h = AndroidUtilities.dp(150);
                        }
                        if (h > photoHeight) {
                            float scale2 = h;
                            h = photoHeight;
                            scale2 /= h;
                            w = (int) (w / scale2);
                        } else if (h < AndroidUtilities.dp(120)) {
                            h = AndroidUtilities.dp(120);
                            float hScale = (float) currentPhotoObject.h / h;
                            if (currentPhotoObject.w / hScale < photoWidth) {
                                w = (int) (currentPhotoObject.w / hScale);
                            }
                        }
                    }

                    if ((w == 0 || h == 0) && messageObject.type == 8) {
                        for (int a = 0; a < messageObject.messageOwner.media.document.attributes.size(); a++) {
                            TLRPC.DocumentAttribute attribute = messageObject.messageOwner.media.document.attributes.get(a);
                            if (attribute instanceof TLRPC.TL_documentAttributeImageSize || attribute instanceof TLRPC.TL_documentAttributeVideo) {
                                float scale = (float) attribute.w / (float) photoWidth;
                                w = (int) (attribute.w / scale);
                                h = (int) (attribute.h / scale);
                                if (h > photoHeight) {
                                    float scale2 = h;
                                    h = photoHeight;
                                    scale2 /= h;
                                    w = (int) (w / scale2);
                                } else if (h < AndroidUtilities.dp(120)) {
                                    h = AndroidUtilities.dp(120);
                                    float hScale = (float) attribute.h / h;
                                    if (attribute.w / hScale < photoWidth) {
                                        w = (int) (attribute.w / hScale);
                                    }
                                }
                                break;
                            }
                        }
                    }


                    if (w == 0 || h == 0) {
                        w = h = AndroidUtilities.dp(150);
                    }
                    if (messageObject.type == 3) {
                        if (w < infoWidth + AndroidUtilities.dp(16 + 24)) {
                            w = infoWidth + AndroidUtilities.dp(16 + 24);
                        }
                    }

                    availableTimeWidth = maxPhotoWidth - AndroidUtilities.dp(14);
                    measureTime(messageObject);
                    int timeWidthTotal = timeWidth + AndroidUtilities.dp(14 + (messageObject.isOutOwner() ? 20 : 0));
                    if (w < timeWidthTotal) {
                        w = timeWidthTotal;
                    }

                    if (messageObject.isSecretPhoto()) {
                        if (AndroidUtilities.isTablet()) {
                            w = h = (int) (AndroidUtilities.getMinTabletSide() * 0.5f);
                        } else {
                            w = h = (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f);
                        }
                    }
                    if (messageObject.isVideoVoice()) {
                        w = h = Math.min(w, h);
                        drawBackground = false;
                        photoImage.setRoundRadius(w / 2);
                    }

                    photoWidth = w;
                    photoHeight = h;
                    backgroundWidth = w + AndroidUtilities.dp(12);
                    if (!mediaBackground) {
                        backgroundWidth += AndroidUtilities.dp(9);
                    }
                    if (messageObject.caption != null) {
                        try {
                            captionLayout = new StaticLayout(messageObject.caption, Theme.chat_msgTextPaint, photoWidth - AndroidUtilities.dp(10), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            if (captionLayout.getLineCount() > 0) {
                                captionHeight = captionLayout.getHeight();
                                additionHeight += captionHeight + AndroidUtilities.dp(9);
                                float lastLineWidth = captionLayout.getLineWidth(captionLayout.getLineCount() - 1) + captionLayout.getLineLeft(captionLayout.getLineCount() - 1);
                                if (photoWidth - AndroidUtilities.dp(8) - lastLineWidth < timeWidthTotal) {
                                    additionHeight += AndroidUtilities.dp(14);
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }

                    currentPhotoFilter = String.format(Locale.US, "%d_%d", (int) (w / AndroidUtilities.density), (int) (h / AndroidUtilities.density));
                    if (messageObject.photoThumbs != null && messageObject.photoThumbs.size() > 1 || messageObject.type == 3 || messageObject.type == 8) {
                        if (messageObject.isSecretPhoto()) {
                            currentPhotoFilter += "_b2";
                        } else {
                            currentPhotoFilter += "_b";
                        }
                    }

                    boolean noSize = false;
                    if (messageObject.type == 3 || messageObject.type == 8) {
                        noSize = true;
                    }
                    if (currentPhotoObject != null && !noSize && currentPhotoObject.size == 0) {
                        currentPhotoObject.size = -1;
                    }

                    if (messageObject.type == 1) {
                        if (messageObject.useCustomPhoto) {
                            photoImage.setImageBitmap(getResources().getDrawable(R.drawable.theme_preview_image));
                        } else {
                            if (currentPhotoObject != null) {
                                boolean photoExist = true;
                                String fileName = FileLoader.getAttachFileName(currentPhotoObject);
                                if (messageObject.mediaExists) {
                                    MediaController.getInstance().removeLoadingFileObserver(this);
                                } else {
                                    photoExist = false;
                                }
                                if (photoExist || MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_PHOTO) || FileLoader.getInstance().isLoadingFile(fileName)) {
                                    photoImage.setImage(currentPhotoObject.location, currentPhotoFilter, currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null, currentPhotoFilter, noSize ? 0 : currentPhotoObject.size, null, false);
                                } else {
                                    photoNotSet = true;
                                    if (currentPhotoObjectThumb != null) {
                                        photoImage.setImage(null, null, currentPhotoObjectThumb.location, currentPhotoFilter, 0, null, false);
                                    } else {
                                        photoImage.setImageBitmap((Drawable) null);
                                    }
                                }
                            } else {
                                photoImage.setImageBitmap((BitmapDrawable) null);
                            }
                        }
                    } else if (messageObject.type == 8) {
                        String fileName = FileLoader.getAttachFileName(messageObject.messageOwner.media.document);
                        int localFile = 0;
                        if (messageObject.attachPathExists) {
                            MediaController.getInstance().removeLoadingFileObserver(this);
                            localFile = 1;
                        } else if (messageObject.mediaExists) {
                            localFile = 2;
                        }
                        if (!messageObject.isSending() && (localFile != 0 || MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_GIF) && MessageObject.isNewGifDocument(messageObject.messageOwner.media.document) || FileLoader.getInstance().isLoadingFile(fileName))) {
                            if (localFile == 1) {
                                photoImage.setImage(null, messageObject.isSendError() ? null : messageObject.messageOwner.attachPath, null, null, currentPhotoObject != null ? currentPhotoObject.location : null, currentPhotoFilter, 0, null, false);
                            } else {
                                photoImage.setImage(messageObject.messageOwner.media.document, null, currentPhotoObject != null ? currentPhotoObject.location : null, currentPhotoFilter, messageObject.messageOwner.media.document.size, null, false);
                            }
                        } else {
                            photoNotSet = true;
                            photoImage.setImage(null, null, currentPhotoObject != null ? currentPhotoObject.location : null, currentPhotoFilter, 0, null, false);
                        }
                    } else {
                        photoImage.setImage(null, null, currentPhotoObject != null ? currentPhotoObject.location : null, currentPhotoFilter, 0, null, false);
                    }
                }
                setMessageObjectInternal(messageObject);

                if (drawForwardedName) {
                    namesOffset += AndroidUtilities.dp(5);
                } else if (drawNameLayout && messageObject.messageOwner.reply_to_msg_id == 0) {
                    namesOffset += AndroidUtilities.dp(7);
                }
                totalHeight = photoHeight + AndroidUtilities.dp(14) + namesOffset + additionHeight;
                if (pinnedTop) {
                    namesOffset -= AndroidUtilities.dp(1);
                }

                photoImage.setImageCoords(0, AndroidUtilities.dp(7) + namesOffset, photoWidth, photoHeight);
                invalidate();
                if (messageObject.type == 1 && !this.drawPhotoImage) {
                    totalHeight += AndroidUtilities.dp(10.0f);
                }
            }
            if (captionLayout == null && messageObject.caption != null && messageObject.type != 13) {
                try {
                    int width = backgroundWidth - AndroidUtilities.dp(31);
                    captionLayout = new StaticLayout(messageObject.caption, Theme.chat_msgTextPaint, width - AndroidUtilities.dp(10), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (captionLayout.getLineCount() > 0) {
                        int timeWidthTotal = timeWidth + (messageObject.isOutOwner() ? AndroidUtilities.dp(20) : 0);
                        captionHeight = captionLayout.getHeight();
                        totalHeight += captionHeight + AndroidUtilities.dp(9);
                        float lastLineWidth = captionLayout.getLineWidth(captionLayout.getLineCount() - 1) + captionLayout.getLineLeft(captionLayout.getLineCount() - 1);
                        if (width - AndroidUtilities.dp(8) - lastLineWidth < timeWidthTotal) {
                            totalHeight += AndroidUtilities.dp(14);
                            captionHeight += AndroidUtilities.dp(14);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            botButtons.clear();
            if (messageIdChanged) {
                botButtonsByData.clear();
                botButtonsByPosition.clear();
                botButtonsLayout = null;
            }
            if (messageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) {
                int rows = messageObject.messageOwner.reply_markup.rows.size();
                substractBackgroundHeight = keyboardHeight = AndroidUtilities.dp(44 + 4) * rows + AndroidUtilities.dp(1);

                widthForButtons = backgroundWidth;
                boolean fullWidth = false;
                if (messageObject.wantedBotKeyboardWidth > widthForButtons) {
                    int maxButtonWidth = -AndroidUtilities.dp(isChat && messageObject.isFromUser() && !messageObject.isOutOwner() ? 62 : 10);
                    if (AndroidUtilities.isTablet()) {
                        maxButtonWidth += AndroidUtilities.getMinTabletSide();
                    } else {
                        maxButtonWidth += Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                    }
                    widthForButtons = Math.max(backgroundWidth, Math.min(messageObject.wantedBotKeyboardWidth, maxButtonWidth));
                    fullWidth = true;
                }

                int maxButtonsWidth = 0;
                HashMap<String, BotButton> oldByData = new HashMap<>(botButtonsByData);
                HashMap<String, BotButton> oldByPosition;
                if (messageObject.botButtonsLayout != null && botButtonsLayout != null && botButtonsLayout.equals(messageObject.botButtonsLayout.toString())) {
                    oldByPosition = new HashMap<>(botButtonsByPosition);
                } else {
                    if (messageObject.botButtonsLayout != null) {
                        botButtonsLayout = messageObject.botButtonsLayout.toString();
                    }
                    oldByPosition = null;
                }
                botButtonsByData.clear();
                for (int a = 0; a < rows; a++) {
                    TLRPC.TL_keyboardButtonRow row = messageObject.messageOwner.reply_markup.rows.get(a);
                    int buttonsCount = row.buttons.size();
                    if (buttonsCount == 0) {
                        continue;
                    }
                    int buttonWidth = (widthForButtons - (AndroidUtilities.dp(5) * (buttonsCount - 1)) - AndroidUtilities.dp(!fullWidth && mediaBackground ? 0 : 9) - AndroidUtilities.dp(2)) / buttonsCount;
                    for (int b = 0; b < row.buttons.size(); b++) {
                        BotButton botButton = new BotButton();
                        botButton.button = row.buttons.get(b);
                        String key = Utilities.bytesToHex(botButton.button.data);
                        String position = a + "" + b;
                        BotButton oldButton;
                        if (oldByPosition != null) {
                            oldButton = oldByPosition.get(position);
                        } else {
                            oldButton = oldByData.get(key);
                        }
                        if (oldButton != null) {
                            botButton.progressAlpha = oldButton.progressAlpha;
                            botButton.angle = oldButton.angle;
                            botButton.lastUpdateTime = oldButton.lastUpdateTime;
                        } else {
                            botButton.lastUpdateTime = System.currentTimeMillis();
                        }
                        botButtonsByData.put(key, botButton);
                        botButtonsByPosition.put(position, botButton);
                        botButton.x = b * (buttonWidth + AndroidUtilities.dp(5));
                        botButton.y = a * AndroidUtilities.dp(44 + 4) + AndroidUtilities.dp(5);
                        botButton.width = buttonWidth;
                        botButton.height = AndroidUtilities.dp(44);
                        CharSequence buttonText;
                        if (botButton.button instanceof TLRPC.TL_keyboardButtonBuy && (messageObject.messageOwner.media.flags & 4) != 0) {
                            buttonText = LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt);
                        } else {
                            buttonText = Emoji.replaceEmoji(botButton.button.text, Theme.chat_botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15), false);
                            buttonText = TextUtils.ellipsize(buttonText, Theme.chat_botButtonPaint, buttonWidth - AndroidUtilities.dp(10), TextUtils.TruncateAt.END);
                        }
                        botButton.title = new StaticLayout(buttonText, Theme.chat_botButtonPaint, buttonWidth - AndroidUtilities.dp(10), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        botButtons.add(botButton);
                        if (b == row.buttons.size() - 1) {
                            maxButtonsWidth = Math.max(maxButtonsWidth, botButton.x + botButton.width);
                        }
                    }
                }
                widthForButtons = maxButtonsWidth;
            } else {
                substractBackgroundHeight = 0;
                keyboardHeight = 0;
            }
            if (pinnedBottom && pinnedTop) {
                totalHeight -= AndroidUtilities.dp(2);
            } else if (pinnedBottom) {
                totalHeight -= AndroidUtilities.dp(1);
            }
            totalHeight = this.avatarSize + AndroidUtilities.dp(10.0f);
            if ((!(!this.showAvatar || this.isChat || messageObject.isOutOwner()) || (((this.showMyAvatar && !this.isChat) || (this.showMyAvatarGroup && this.isChat)) && messageObject.isOutOwner())) && this.totalHeight < this.avatarSize) {
                this.totalHeight = this.avatarSize + AndroidUtilities.dp(10.0f);
            }
        }
        updateWaveform();
        updateButtonState(dataChanged);
    }

    @Override
    public void requestLayout() {
        if (inLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (currentMessageObject != null && currentMessageObject.checkLayout()) {
            inLayout = true;
            MessageObject messageObject = currentMessageObject;
            currentMessageObject = null;
            setMessageObject(messageObject, pinnedBottom, pinnedTop);
            inLayout = false;
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), totalHeight + keyboardHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.currentMessageObject == null) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        int dp;
        int dp2;
        if (changed || !this.wasLayout) {
            this.layoutWidth = getMeasuredWidth();
            this.layoutHeight = getMeasuredHeight() - this.substractBackgroundHeight;
            if (this.timeTextWidth < 0) {
                this.timeTextWidth = AndroidUtilities.dp(10.0f);
            }
            this.timeLayout = new StaticLayout(this.currentTimeString, Theme.chat_timePaint, this.timeTextWidth + AndroidUtilities.dp(6.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.mediaBackground) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(42.0f);
                    if ((this.showMyAvatar && !this.isChat) || (this.showMyAvatarGroup && this.isChat)) {
                        this.timeX = ((this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(42.0f)) - AndroidUtilities.dp((float) this.leftBound);
                    }
                    this.checkX = this.timeX + this.timeWidth;
                } else {
                    dp = (this.backgroundWidth - AndroidUtilities.dp(4.0f)) - this.timeWidth;
                    dp2 = ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) ? AndroidUtilities.dp((float) this.leftBound) : 0;
                    this.timeX = dp2 + dp;
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(38.5f);
                if ((this.showMyAvatar && !this.isChat) || (this.showMyAvatarGroup && this.isChat)) {
                    this.timeX = ((this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(38.5f)) - AndroidUtilities.dp((float) this.leftBound);
                }
                this.checkX = this.timeX + this.timeWidth;
            } else {
                dp = (this.backgroundWidth - AndroidUtilities.dp(9.0f)) - this.timeWidth;
                if ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) {
                    dp2 = AndroidUtilities.dp((float) this.leftBound);
                } else {
                    dp2 = 0;
                }
                this.timeX = dp2 + dp;
            }
            if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                this.viewsLayout = new StaticLayout(this.currentViewsString, Theme.chat_timePaint, this.viewsTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } else {
                this.viewsLayout = null;
            }
            if (this.isAvatarVisible) {
                if (((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) || !this.currentMessageObject.isOutOwner()) {
                    this.avatarImage.setImageCoords(AndroidUtilities.dp((float) Theme.chatAvatarMarginLeft), Theme.chatAvatarAlignTop ? AndroidUtilities.dp(3.0f) : (this.layoutHeight - AndroidUtilities.dp(3.0f)) - this.avatarSize, this.avatarSize, this.avatarSize);
                } else {
                    this.avatarImage.setImageCoords((this.layoutWidth - this.avatarSize) - AndroidUtilities.dp((float) Theme.chatAvatarMarginLeft), Theme.chatOwnAvatarAlignTop ? AndroidUtilities.dp(3.0f) : (this.layoutHeight - AndroidUtilities.dp(3.0f)) - this.avatarSize, this.avatarSize, this.avatarSize);
                    this.drawStatus = false;
                }
            }
            this.wasLayout = true;
        }
        if (this.currentMessageObject.type == 0) {
            this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
        }
        if (this.documentAttachType == 3) {
            if (this.currentMessageObject.isOutOwner()) {
                dp = AndroidUtilities.dp(57.0f) + (this.layoutWidth - this.backgroundWidth);
                dp2 = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
                this.seekBarX = dp - dp2;
                dp = AndroidUtilities.dp(14.0f) + (this.layoutWidth - this.backgroundWidth);
                dp2 = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
                this.buttonX = dp - dp2;
                dp = AndroidUtilities.dp(67.0f) + (this.layoutWidth - this.backgroundWidth);
                if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                    dp2 = 0;
                } else {
                    dp2 = AndroidUtilities.dp((float) this.leftBound);
                }
                this.timeAudioX = dp - dp2;
            } else if ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) {
                this.seekBarX = AndroidUtilities.dp((float) (this.leftBound + 66));
                this.buttonX = AndroidUtilities.dp((float) (this.leftBound + 23));
                this.timeAudioX = AndroidUtilities.dp((float) (this.leftBound + 76));
            } else {
                this.seekBarX = AndroidUtilities.dp(66.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            this.seekBarWaveform.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : 0) + 92)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : 0) + 72)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBarY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            updateAudioProgress();
        } else if (this.documentAttachType == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                dp = AndroidUtilities.dp(56.0f) + (this.layoutWidth - this.backgroundWidth);
                dp2 = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
                this.seekBarX = dp - dp2;
                dp = AndroidUtilities.dp(14.0f) + (this.layoutWidth - this.backgroundWidth);
                dp2 = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
                this.buttonX = dp - dp2;
                dp = AndroidUtilities.dp(67.0f) + (this.layoutWidth - this.backgroundWidth);
                if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                    dp2 = 0;
                } else {
                    dp2 = AndroidUtilities.dp((float) this.leftBound);
                }
                this.timeAudioX = dp - dp2;
            } else if ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) {
                this.seekBarX = AndroidUtilities.dp((float) (this.leftBound + 65));
                this.buttonX = AndroidUtilities.dp((float) (this.leftBound + 23));
                this.timeAudioX = AndroidUtilities.dp((float) (this.leftBound + 76));
            } else {
                this.seekBarX = AndroidUtilities.dp(65.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : 0) + 65)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBarY = (AndroidUtilities.dp(29.0f) + this.namesOffset) + this.mediaOffsetY;
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            updateAudioProgress();
        } else if (this.documentAttachType == 1 && !this.drawPhotoImage) {
            if (this.currentMessageObject.isOutOwner()) {
                dp = AndroidUtilities.dp(14.0f) + (this.layoutWidth - this.backgroundWidth);
                dp2 = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
                this.buttonX = dp - dp2;
            } else if ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) {
                this.buttonX = AndroidUtilities.dp((float) (this.leftBound + 23));
            } else {
                this.buttonX = AndroidUtilities.dp(23.0f);
            }
            if (this.hasLinkPreview) {
                this.buttonX += AndroidUtilities.dp(10.0f);
            }
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0f), this.buttonY - AndroidUtilities.dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        } else if (this.currentMessageObject.type == 12) {
            int x;
            if (this.currentMessageObject.isOutOwner()) {
                dp = AndroidUtilities.dp(14.0f) + (this.layoutWidth - this.backgroundWidth);
                dp2 = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
                x = dp - dp2;
            } else if ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) {
                x = AndroidUtilities.dp((float) (this.leftBound + 23));
            } else {
                x = AndroidUtilities.dp(23.0f);
            }
            this.photoImage.setImageCoords(x, AndroidUtilities.dp(13.0f) + this.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
        } else {
            int x;
            if (this.currentMessageObject.isOutOwner()) {
                if (this.mediaBackground) {
                    dp = (this.layoutWidth - this.backgroundWidth) - AndroidUtilities.dp(3.0f);
                    if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                        dp2 = 0;
                    } else {
                        dp2 = AndroidUtilities.dp((float) this.leftBound);
                    }
                    x = dp - dp2;
                } else {
                    dp = AndroidUtilities.dp(6.0f) + (this.layoutWidth - this.backgroundWidth);
                    dp2 = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
                    x = dp - dp2;
                }
            } else if ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) {
                x = AndroidUtilities.dp((float) (this.leftBound + 15));
            } else {
                x = AndroidUtilities.dp(15.0f);
            }
            this.photoImage.setImageCoords(x, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            this.buttonX = (int) (((float) x) + (((float) (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f))) / 2.0f));
            this.buttonY = ((int) (((float) AndroidUtilities.dp(7.0f)) + (((float) (this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f))) / 2.0f))) + this.namesOffset;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0f), this.buttonY + AndroidUtilities.dp(48.0f));
            this.deleteProgressRect.set((float) (this.buttonX + AndroidUtilities.dp(3.0f)), (float) (this.buttonY + AndroidUtilities.dp(3.0f)), (float) (this.buttonX + AndroidUtilities.dp(45.0f)), (float) (this.buttonY + AndroidUtilities.dp(45.0f)));
        }
    }
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        if (currentMessageObject == null) {
//            super.onLayout(changed, left, top, right, bottom);
//            return;
//        }
//
//        if (changed || !wasLayout) {
//            layoutWidth = getMeasuredWidth();
//            layoutHeight = getMeasuredHeight() - substractBackgroundHeight;
//            if (timeTextWidth < 0) {
//                timeTextWidth = AndroidUtilities.dp(10);
//            }
//            timeLayout = new StaticLayout(currentTimeString, Theme.chat_timePaint, timeTextWidth + AndroidUtilities.dp(6), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//            if (!mediaBackground) {
//                if (!currentMessageObject.isOutOwner()) {
//                    timeX = backgroundWidth - AndroidUtilities.dp(9) - timeWidth + (isChat && currentMessageObject.isFromUser() ? AndroidUtilities.dp(48) : 0);
//                } else {
//                    timeX = layoutWidth - timeWidth - AndroidUtilities.dp(38.5f);
//                }
//            } else {
//                if (!currentMessageObject.isOutOwner()) {
//                    timeX = backgroundWidth - AndroidUtilities.dp(4) - timeWidth + (isChat && currentMessageObject.isFromUser() ? AndroidUtilities.dp(48) : 0);
//                    if ((this.showMyAvatar && !this.isChat) || (this.showMyAvatarGroup && this.isChat)) {
//                        this.timeX = ((this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(42.0f)) - AndroidUtilities.dp((float) this.leftBound);
//                    }
//                    this.checkX = this.timeX + this.timeWidth;
//                } else {
//                    timeX = layoutWidth - timeWidth - AndroidUtilities.dp(42.0f);
//                    timeX = ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) ? AndroidUtilities.dp((float) this.leftBound) : 0;
//                }
//            }
//
//            if ((currentMessageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
//                viewsLayout = new StaticLayout(currentViewsString, Theme.chat_timePaint, viewsTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//            } else {
//                viewsLayout = null;
//            }
//
//            if (isAvatarVisible) {
//                avatarImage.setImageCoords(AndroidUtilities.dp(6), avatarImage.getImageY(), AndroidUtilities.dp(42), AndroidUtilities.dp(42));
//            }
//
//            wasLayout = true;
//        }
//
//        if (currentMessageObject.type == 0) {
//            textY = AndroidUtilities.dp(10) + namesOffset;
//        }
//        if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
//            if (currentMessageObject.isOutOwner()) {
//                seekBarX = layoutWidth - backgroundWidth + AndroidUtilities.dp(57);
//                buttonX = layoutWidth - backgroundWidth + AndroidUtilities.dp(14);
//                timeAudioX = layoutWidth - backgroundWidth + AndroidUtilities.dp(67);
//            } else {
//                if (isChat && currentMessageObject.isFromUser()) {
//                    seekBarX = AndroidUtilities.dp(114);
//                    buttonX = AndroidUtilities.dp(71);
//                    timeAudioX = AndroidUtilities.dp(124);
//                } else {
//                    seekBarX = AndroidUtilities.dp(66);
//                    buttonX = AndroidUtilities.dp(23);
//                    timeAudioX = AndroidUtilities.dp(76);
//                }
//            }
//            if (hasLinkPreview) {
//                seekBarX += AndroidUtilities.dp(10);
//                buttonX += AndroidUtilities.dp(10);
//                timeAudioX += AndroidUtilities.dp(10);
//            }
//            seekBarWaveform.setSize(backgroundWidth - AndroidUtilities.dp(92 + (hasLinkPreview ? 10 : 0)), AndroidUtilities.dp(30));
//            seekBar.setSize(backgroundWidth - AndroidUtilities.dp(72 + (hasLinkPreview ? 10 : 0)), AndroidUtilities.dp(30));
//            seekBarY = AndroidUtilities.dp(13) + namesOffset + mediaOffsetY;
//            buttonY = AndroidUtilities.dp(13) + namesOffset + mediaOffsetY;
//            radialProgress.setProgressRect(buttonX, buttonY, buttonX + AndroidUtilities.dp(44), buttonY + AndroidUtilities.dp(44));
//
//            updateAudioProgress();
//        } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
//            if (currentMessageObject.isOutOwner()) {
//                seekBarX = layoutWidth - backgroundWidth + AndroidUtilities.dp(56);
//                buttonX = layoutWidth - backgroundWidth + AndroidUtilities.dp(14);
//                timeAudioX = layoutWidth - backgroundWidth + AndroidUtilities.dp(67);
//            } else {
//                if (isChat && currentMessageObject.isFromUser()) {
//                    seekBarX = AndroidUtilities.dp(113);
//                    buttonX = AndroidUtilities.dp(71);
//                    timeAudioX = AndroidUtilities.dp(124);
//                } else {
//                    seekBarX = AndroidUtilities.dp(65);
//                    buttonX = AndroidUtilities.dp(23);
//                    timeAudioX = AndroidUtilities.dp(76);
//                }
//            }
//            if (hasLinkPreview) {
//                seekBarX += AndroidUtilities.dp(10);
//                buttonX += AndroidUtilities.dp(10);
//                timeAudioX += AndroidUtilities.dp(10);
//            }
//            seekBar.setSize(backgroundWidth - AndroidUtilities.dp(65 + (hasLinkPreview ? 10 : 0)), AndroidUtilities.dp(30));
//            seekBarY = AndroidUtilities.dp(29) + namesOffset + mediaOffsetY;
//            buttonY = AndroidUtilities.dp(13) + namesOffset + mediaOffsetY;
//            radialProgress.setProgressRect(buttonX, buttonY, buttonX + AndroidUtilities.dp(44), buttonY + AndroidUtilities.dp(44));
//
//            updateAudioProgress();
//        } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT && !drawPhotoImage) {
//            if (currentMessageObject.isOutOwner()) {
//                buttonX = layoutWidth - backgroundWidth + AndroidUtilities.dp(14);
//            } else {
//                if (isChat && currentMessageObject.isFromUser()) {
//                    buttonX = AndroidUtilities.dp(71);
//                } else {
//                    buttonX = AndroidUtilities.dp(23);
//                }
//            }
//            if (hasLinkPreview) {
//                buttonX += AndroidUtilities.dp(10);
//            }
//            buttonY = AndroidUtilities.dp(13) + namesOffset + mediaOffsetY;
//            radialProgress.setProgressRect(buttonX, buttonY, buttonX + AndroidUtilities.dp(44), buttonY + AndroidUtilities.dp(44));
//            photoImage.setImageCoords(buttonX - AndroidUtilities.dp(10), buttonY - AndroidUtilities.dp(10), photoImage.getImageWidth(), photoImage.getImageHeight());
//        } else if (currentMessageObject.type == 12) {
//            int x;
//
//            if (currentMessageObject.isOutOwner()) {
//                x = layoutWidth - backgroundWidth + AndroidUtilities.dp(14);
//            } else {
//                if (isChat && currentMessageObject.isFromUser()) {
//                    x = AndroidUtilities.dp(72);
//                } else {
//                    x = AndroidUtilities.dp(23);
//                }
//            }
//            photoImage.setImageCoords(x, AndroidUtilities.dp(13) + namesOffset, AndroidUtilities.dp(44), AndroidUtilities.dp(44));
//        } else {
//            int x;
//            if (currentMessageObject.isOutOwner()) {
//                if (mediaBackground) {
//                    x = layoutWidth - backgroundWidth - AndroidUtilities.dp(3);
//                } else {
//                    x = layoutWidth - backgroundWidth + AndroidUtilities.dp(6);
//                }
//            } else {
//                if (isChat && currentMessageObject.isFromUser()) {
//                    x = AndroidUtilities.dp(63);
//                } else {
//                    x = AndroidUtilities.dp(15);
//                }
//            }
//            photoImage.setImageCoords(x, photoImage.getImageY(), photoImage.getImageWidth(), photoImage.getImageHeight());
//            buttonX = (int) (x + (photoImage.getImageWidth() - AndroidUtilities.dp(48)) / 2.0f);
//            buttonY = (int) (AndroidUtilities.dp(7) + (photoImage.getImageHeight() - AndroidUtilities.dp(48)) / 2.0f) + namesOffset;
//            radialProgress.setProgressRect(buttonX, buttonY, buttonX + AndroidUtilities.dp(48), buttonY + AndroidUtilities.dp(48));
//            deleteProgressRect.set(buttonX + AndroidUtilities.dp(3), buttonY + AndroidUtilities.dp(3), buttonX + AndroidUtilities.dp(45), buttonY + AndroidUtilities.dp(45));
//        }
//    }

    private void drawContent(Canvas canvas) { //TODO Multi
//private void drawContent(Canvas canvas) {
        int a;
        int b;
        int i;
        int i2;
        int x;
        int y;
        Drawable menuDrawable;
        int color;
    RadialProgress radialProgress;
    String str;
    if (this.needNewVisiblePart && this.currentMessageObject.type == 0) {
        getLocalVisibleRect(this.scrollRect);
        setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
        this.needNewVisiblePart = false;
    }
    this.forceNotDrawTime = false;
    this.photoImage.setPressed(isDrawSelectedBackground());
    this.photoImage.setVisible(!PhotoViewer.getInstance().isShowingImage(this.currentMessageObject), false);
    this.radialProgress.setHideCurrentDrawable(false);
    this.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
    SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
    boolean imageDrawn = false;
    if (this.currentMessageObject.type == 0) {
        if (this.currentMessageObject.isOutOwner()) {
            this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
        } else {
            i2 = this.currentBackgroundDrawable.getBounds().left;
            float f = (this.mediaBackground || !this.pinnedBottom) ? 17.0f : 11.0f;
            this.textX = AndroidUtilities.dp(f) + i2;
        }
        if (this.hasGamePreview) {
            this.textX += AndroidUtilities.dp(11.0f);
            this.textY = AndroidUtilities.dp(14.0f) + this.namesOffset;
            if (this.siteNameLayout != null) {
                this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
            }
        } else if (this.hasInvoicePreview) {
            this.textY = AndroidUtilities.dp(14.0f) + this.namesOffset;
            if (this.siteNameLayout != null) {
                this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
            }
        } else {
            this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
        }
        if (!(this.currentMessageObject.textLayoutBlocks == null || this.currentMessageObject.textLayoutBlocks.isEmpty())) {
            if (this.fullyDraw) {
                this.firstVisibleBlockNum = 0;
                this.lastVisibleBlockNum = this.currentMessageObject.textLayoutBlocks.size();
            }
            if (this.firstVisibleBlockNum >= 0) {
                a = this.firstVisibleBlockNum;
                while (a <= this.lastVisibleBlockNum && a < this.currentMessageObject.textLayoutBlocks.size()) {
                    MessageObject.TextLayoutBlock block = (MessageObject.TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
                    canvas.save();
                    canvas.translate((float) (this.textX - (block.isRtl() ? (int) Math.ceil((double) this.currentMessageObject.textXOffset) : 0)), ((float) this.textY) + block.textYOffset);
                    if (this.pressedLink != null && a == this.linkBlockNum) {
                        for (b = 0; b < this.urlPath.size(); b++) {
                            canvas.drawPath((Path) this.urlPath.get(b), Theme.chat_urlPaint);
                        }
                    }
                    if (a == this.linkSelectionBlockNum && !this.urlPathSelection.isEmpty()) {
                        for (b = 0; b < this.urlPathSelection.size(); b++) {
                            canvas.drawPath((Path) this.urlPathSelection.get(b), Theme.chat_textSearchSelectionPaint);
                        }
                    }
                    try {
                        block.textLayout.draw(canvas);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    canvas.restore();
                    a++;
                }
            }
        }
        if (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview) {
            int startY;
            int linkX;
            TextPaint textPaint;
            int size;
            if (this.hasGamePreview) {
                startY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                linkX = this.textX - AndroidUtilities.dp(10.0f);
            } else if (this.hasInvoicePreview) {
                startY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                linkX = this.textX + AndroidUtilities.dp(1.0f);
            } else {
                startY = (this.textY + this.currentMessageObject.textHeight) + AndroidUtilities.dp(8.0f);
                linkX = this.textX + AndroidUtilities.dp(1.0f);
            }
            int linkPreviewY = startY;
            int smallImageStartY = 0;
            if (!this.hasInvoicePreview) {
                Paint paint = Theme.chat_replyLinePaint;
                if (Theme.usePlusTheme) {
                    i = this.currentMessageObject.isOutOwner() ? Theme.chatRLinkColor : Theme.chatLLinkColor;
                } else {
                    i = Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine);
                }
                paint.setColor(i);
                canvas.drawRect((float) linkX, (float) (linkPreviewY - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + linkX), (float) ((this.linkPreviewHeight + linkPreviewY) + AndroidUtilities.dp(3.0f)), Theme.chat_replyLinePaint);
            }
            if (this.siteNameLayout != null) {
                textPaint = Theme.chat_replyNamePaint;
                if (Theme.usePlusTheme) {
                    i = this.currentMessageObject.isOutOwner() ? Theme.chatRLinkColor : Theme.chatLLinkColor;
                } else {
                    i = Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText);
                }
                textPaint.setColor(i);
                canvas.save();
                if (this.hasInvoicePreview) {
                    i = 0;
                } else {
                    i = AndroidUtilities.dp(10.0f);
                }
                canvas.translate((float) (i + linkX), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                this.siteNameLayout.draw(canvas);
                canvas.restore();
                linkPreviewY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
            }
            if ((this.hasGamePreview || this.hasInvoicePreview) && this.currentMessageObject.textHeight != 0) {
                startY += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
                linkPreviewY += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
            }
            if (this.drawPhotoImage && this.drawInstantView) {
                if (linkPreviewY != startY) {
                    linkPreviewY += AndroidUtilities.dp(2.0f);
                }
                this.photoImage.setImageCoords(AndroidUtilities.dp(10.0f) + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                if (this.drawImageButton) {
                    size = AndroidUtilities.dp(48.0f);
                    this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - size)) / 2.0f));
                    this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - size)) / 2.0f));
                    this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                }
                imageDrawn = this.photoImage.draw(canvas);
                linkPreviewY += this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0f);
            }
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_replyNamePaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_messageTextOut));
                Theme.chat_replyTextPaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_messageTextOut));
            } else {
                Theme.chat_replyNamePaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_messageTextIn));
                textPaint = Theme.chat_replyTextPaint;
                if (Theme.usePlusTheme) {
                    i = Theme.chatLTextColor;
                } else {
                    i = Theme.getColor(Theme.key_chat_messageTextIn);
                }
                textPaint.setColor(i);
            }
            if (this.titleLayout != null) {
                if (linkPreviewY != startY) {
                    linkPreviewY += AndroidUtilities.dp(2.0f);
                }
                smallImageStartY = linkPreviewY - AndroidUtilities.dp(1.0f);
                canvas.save();
                canvas.translate((float) ((AndroidUtilities.dp(10.0f) + linkX) + this.titleX), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                this.titleLayout.draw(canvas);
                canvas.restore();
                linkPreviewY += this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
            }
            if (this.authorLayout != null) {
                if (linkPreviewY != startY) {
                    linkPreviewY += AndroidUtilities.dp(2.0f);
                }
                if (smallImageStartY == 0) {
                    smallImageStartY = linkPreviewY - AndroidUtilities.dp(1.0f);
                }
                canvas.save();
                canvas.translate((float) ((AndroidUtilities.dp(10.0f) + linkX) + this.authorX), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                this.authorLayout.draw(canvas);
                canvas.restore();
                linkPreviewY += this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
            }
            if (this.descriptionLayout != null) {
                if (linkPreviewY != startY) {
                    linkPreviewY += AndroidUtilities.dp(2.0f);
                }
                if (smallImageStartY == 0) {
                    smallImageStartY = linkPreviewY - AndroidUtilities.dp(1.0f);
                }
                this.descriptionY = linkPreviewY - AndroidUtilities.dp(3.0f);
                canvas.save();
                canvas.translate((float) (((this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f)) + linkX) + this.descriptionX), (float) this.descriptionY);
                if (this.pressedLink != null && this.linkBlockNum == -10) {
                    for (b = 0; b < this.urlPath.size(); b++) {
                        canvas.drawPath((Path) this.urlPath.get(b), Theme.chat_urlPaint);
                    }
                }
                this.descriptionLayout.draw(canvas);
                canvas.restore();
                linkPreviewY += this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
            }
            if (this.drawPhotoImage && !this.drawInstantView) {
                if (linkPreviewY != startY) {
                    linkPreviewY += AndroidUtilities.dp(2.0f);
                }
                if (this.isSmallImage) {
                    this.photoImage.setImageCoords((this.backgroundWidth + linkX) - AndroidUtilities.dp(81.0f), smallImageStartY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                } else {
                    ImageReceiver imageReceiver = this.photoImage;
                    if (this.hasInvoicePreview) {
                        i = -AndroidUtilities.dp(6.3f);
                    } else {
                        i = AndroidUtilities.dp(10.0f);
                    }
                    imageReceiver.setImageCoords(i + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    if (this.drawImageButton) {
                        size = AndroidUtilities.dp(48.0f);
                        this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - size)) / 2.0f));
                        this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - size)) / 2.0f));
                        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                    }
                }
                imageDrawn = this.photoImage.draw(canvas);
            }
            if (this.videoInfoLayout != null && (!this.drawPhotoImage || this.photoImage.getVisible())) {
                if (!this.hasGamePreview && !this.hasInvoicePreview) {
                    x = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.durationWidth;
                    y = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                    Theme.chat_timeBackgroundDrawable.setBounds(x - AndroidUtilities.dp(4.0f), y - AndroidUtilities.dp(1.5f), (this.durationWidth + x) + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(14.5f) + y);
                    Theme.chat_timeBackgroundDrawable.draw(canvas);
                } else if (this.drawPhotoImage) {
                    x = this.photoImage.getImageX() + AndroidUtilities.dp(8.5f);
                    y = this.photoImage.getImageY() + AndroidUtilities.dp(6.0f);
                    Theme.chat_timeBackgroundDrawable.setBounds(x - AndroidUtilities.dp(4.0f), y - AndroidUtilities.dp(1.5f), (this.durationWidth + x) + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(16.5f) + y);
                    Theme.chat_timeBackgroundDrawable.draw(canvas);
                } else {
                    x = linkX;
                    y = linkPreviewY;
                }
                canvas.save();
                canvas.translate((float) x, (float) y);
                if (this.hasInvoicePreview) {
                    if (this.drawPhotoImage) {
                        Theme.chat_shipmentPaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_previewGameText));
                    } else if (this.currentMessageObject.isOutOwner()) {
                        Theme.chat_shipmentPaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_messageTextOut));
                    } else {
                        Theme.chat_shipmentPaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_messageTextIn));
                    }
                }
                this.videoInfoLayout.draw(canvas);
                canvas.restore();
            }
            if (this.drawInstantView) {
                Drawable instantDrawable;
                int instantX = linkX + AndroidUtilities.dp(10.0f);
                int instantY = linkPreviewY + AndroidUtilities.dp(4.0f);
                Paint backPaint = Theme.chat_instantViewRectPaint;
                if (this.currentMessageObject.isOutOwner()) {
                    instantDrawable = this.instantPressed ? Theme.chat_msgOutInstantSelectedDrawable : Theme.chat_msgOutInstantDrawable;
                    if (Theme.usePlusTheme) {
                        instantDrawable.setColorFilter(new PorterDuffColorFilter(Theme.chatRLinkColor, PorterDuff.Mode.MULTIPLY));
                    }
                    textPaint = Theme.chat_instantViewPaint;
                    if (Theme.usePlusTheme) {
                        i = Theme.chatRLinkColor;
                    } else {
                        i = Theme.getColor(this.instantPressed ? Theme.key_chat_outPreviewInstantSelectedText : Theme.key_chat_outPreviewInstantText);
                    }
                    textPaint.setColor(i);
                    if (Theme.usePlusTheme) {
                        i = Theme.chatRLinkColor;
                    } else {
                        i = Theme.getColor(this.instantPressed ? Theme.key_chat_outPreviewInstantSelectedText : Theme.key_chat_outPreviewInstantText);
                    }
                    backPaint.setColor(i);
                } else {
                    instantDrawable = this.instantPressed ? Theme.chat_msgInInstantSelectedDrawable : Theme.chat_msgInInstantDrawable;
                    if (Theme.usePlusTheme) {
                        instantDrawable.setColorFilter(new PorterDuffColorFilter(Theme.chatLLinkColor, PorterDuff.Mode.MULTIPLY));
                    }
                    textPaint = Theme.chat_instantViewPaint;
                    if (Theme.usePlusTheme) {
                        i = Theme.chatLLinkColor;
                    } else {
                        i = Theme.getColor(this.instantPressed ? Theme.key_chat_inPreviewInstantSelectedText : Theme.key_chat_inPreviewInstantText);
                    }
                    textPaint.setColor(i);
                    if (Theme.usePlusTheme) {
                        i = Theme.chatLLinkColor;
                    } else {
                        i = Theme.getColor(this.instantPressed ? Theme.key_chat_inPreviewInstantSelectedText : Theme.key_chat_inPreviewInstantText);
                    }
                    backPaint.setColor(i);
                }
                this.rect.set((float) instantX, (float) instantY, (float) (this.instantWidth + instantX), (float) (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) + instantY));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), backPaint);
                setDrawableBounds(instantDrawable, instantX + AndroidUtilities.dp(9.0f), instantY + AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(13.0f));
                instantDrawable.draw(canvas);
                if (this.instantViewLayout != null) {
                    canvas.save();
                    canvas.translate((float) ((this.instantTextX + instantX) + AndroidUtilities.dp(24.0f)), (float) (AndroidUtilities.dp(8.0f) + instantY));
                    this.instantViewLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
        this.drawTime = true;
    } else if (this.drawPhotoImage) {
        imageDrawn = this.photoImage.draw(canvas);
        this.drawTime = this.photoImage.getVisible();
    }
    if (this.buttonState == -1 && this.currentMessageObject.isSecretPhoto()) {
        int drawable = 4;
        if (this.currentMessageObject.messageOwner.destroyTime != 0) {
            if (this.currentMessageObject.isOutOwner()) {
                drawable = 6;
            } else {
                drawable = 5;
            }
        }
        setDrawableBounds(Theme.chat_photoStatesDrawables[drawable][this.buttonPressed], this.buttonX, this.buttonY);
        Theme.chat_photoStatesDrawables[drawable][this.buttonPressed].setAlpha((int) (255.0f * (1.0f - this.radialProgress.getAlpha())));
        Theme.chat_photoStatesDrawables[drawable][this.buttonPressed].draw(canvas);
        if (!(this.currentMessageObject.isOutOwner() || this.currentMessageObject.messageOwner.destroyTime == 0)) {
            float progress = ((float) Math.max(0, (((long) this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance().getTimeDifference() * 1000))))) / (((float) this.currentMessageObject.messageOwner.ttl) * 1000.0f);
            canvas.drawArc(this.deleteProgressRect, -90.0f, -360.0f * progress, true, Theme.chat_deleteProgressPaint);
            if (progress != 0.0f) {
                int offset = AndroidUtilities.dp(2.0f);
                invalidate(((int) this.deleteProgressRect.left) - offset, ((int) this.deleteProgressRect.top) - offset, ((int) this.deleteProgressRect.right) + (offset * 2), ((int) this.deleteProgressRect.bottom) + (offset * 2));
            }
            updateSecretTimeText(this.currentMessageObject);
        }
    }
    if (Theme.chatMemberColorCheck) {
        senderPaint.setColor(Theme.chatMemberColor);
    } else if (this.currentMessageObject != null && this.currentMessageObject.isFromUser()) {
        senderPaint.setColor(AvatarDrawable.getNameColorForId(MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id)).id));
    }
    if ((this.documentAttachType == 2 || this.currentMessageObject.type == 8) && !this.currentMessageObject.isVideoVoice()) {
        if (this.photoImage.getVisible() && !this.hasGamePreview) {
            Drawable drawable2 = Theme.chat_msgMediaMenuDrawable;
            i2 = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
            this.otherX = i2;
            int imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
            this.otherY = imageY;
            setDrawableBounds(drawable2, i2, imageY);
            Theme.chat_msgMediaMenuDrawable.draw(canvas);
        }
    } else if (this.documentAttachType == 5) {
        color = themePrefs.getInt(Theme.pkey_chatRTextColor, -14606047);
        if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_outAudioTitleText));
            Theme.chat_audioPerformerPaint.setColor(Theme.getColor(Theme.key_chat_outAudioPerfomerText));
            Theme.chat_audioTimePaint.setColor(Theme.getColor(Theme.key_chat_outAudioDurationText));
            if (Theme.usePlusTheme) {
                Theme.chat_audioTitlePaint.setColor(color);
                Theme.chat_audioPerformerPaint.setColor(color);
                Theme.chat_audioTimePaint.setColor(Theme.chatRTimeColor);
            }
            radialProgress = this.radialProgress;
            if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                str = Theme.key_chat_outAudioSelectedProgress;
            } else {
                str = Theme.key_chat_outAudioProgress;
            }
            radialProgress.setProgressColor(Theme.getColor(str));
        } else {
            Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_inAudioTitleText));
            Theme.chat_audioPerformerPaint.setColor(Theme.getColor(Theme.key_chat_inAudioPerfomerText));
            Theme.chat_audioTimePaint.setColor(Theme.getColor(Theme.key_chat_inAudioDurationText));
            if (Theme.usePlusTheme) {
                color = themePrefs.getInt(Theme.pkey_chatLTextColor, -14606047);
                Theme.chat_audioTitlePaint.setColor(color);
                Theme.chat_audioPerformerPaint.setColor(color);
                Theme.chat_audioTimePaint.setColor(Theme.chatLTimeColor);
            }
            radialProgress = this.radialProgress;
            str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress;
            radialProgress.setProgressColor(Theme.getColor(str));
        }
        this.radialProgress.draw(canvas);
        canvas.save();
        canvas.translate((float) (this.timeAudioX + this.songX), (float) ((AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY));
        this.songLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
            canvas.translate((float) this.seekBarX, (float) this.seekBarY);
            this.seekBar.draw(canvas);
        } else {
            canvas.translate((float) (this.timeAudioX + this.performerX), (float) ((AndroidUtilities.dp(35.0f) + this.namesOffset) + this.mediaOffsetY));
            this.performerLayout.draw(canvas);
        }
        canvas.restore();
        canvas.save();
        canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.dp(57.0f) + this.namesOffset) + this.mediaOffsetY));
        this.durationLayout.draw(canvas);
        canvas.restore();
        menuDrawable = this.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
        i = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
        this.otherX = i;
        i2 = this.buttonY - AndroidUtilities.dp(5.0f);
        this.otherY = i2;
        setDrawableBounds(menuDrawable, i, i2);
        menuDrawable.draw(canvas);
    } else if (this.documentAttachType == 3) {
        if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outAudioDurationSelectedText : Theme.key_chat_outAudioDurationText));
            radialProgress = this.radialProgress;
            str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_outAudioSelectedProgress : Theme.key_chat_outAudioProgress;
            radialProgress.setProgressColor(Theme.getColor(str));
        } else {
            Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inAudioDurationSelectedText : Theme.key_chat_inAudioDurationText));
            radialProgress = this.radialProgress;
            str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress;
            radialProgress.setProgressColor(Theme.getColor(str));
        }
        this.radialProgress.draw(canvas);
        canvas.save();
        if (this.useSeekBarWaweform) {
            canvas.translate((float) (this.seekBarX + AndroidUtilities.dp(13.0f)), (float) this.seekBarY);
            this.seekBarWaveform.draw(canvas);
        } else {
            canvas.translate((float) this.seekBarX, (float) this.seekBarY);
            this.seekBar.draw(canvas);
        }
        canvas.restore();
        canvas.save();
        canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.dp(44.0f) + this.namesOffset) + this.mediaOffsetY));
        this.durationLayout.draw(canvas);
        canvas.restore();
        if (this.currentMessageObject.type != 0 && this.currentMessageObject.messageOwner.to_id.channel_id == 0 && this.currentMessageObject.isContentUnread()) {
            Theme.chat_docBackPaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outVoiceSeekbarFill : Theme.key_chat_inVoiceSeekbarFill));
            canvas.drawCircle((float) ((this.timeAudioX + this.timeWidthAudio) + AndroidUtilities.dp(6.0f)), (float) ((AndroidUtilities.dp(51.0f) + this.namesOffset) + this.mediaOffsetY), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
        }
    }
    if (this.currentMessageObject.type == 1 || this.documentAttachType == 4 || this.currentMessageObject.type == 8) {
        if (this.photoImage.getVisible()) {
            if (this.documentAttachType == 4) {
                Drawable drawable2 = Theme.chat_msgMediaMenuDrawable;
                i2 = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                this.otherX = i2;
                int imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                this.otherY = imageY;
                setDrawableBounds(drawable2, i2, imageY);
                Theme.chat_msgMediaMenuDrawable.draw(canvas);
            }
            boolean showSenderName = this.currentMessageObject.type == 1;
            if (showSenderName) {
                String senderName = getCurrentNameString(this.currentMessageObject).replaceAll("\\p{C}", "").trim();
                try {
                    if (this.isChat && this.currentMessageObject.isFromUser()) {
                        this.infoWidth = (int) Math.min(Math.ceil((double) senderPaint.measureText(senderName)), (double) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f));
                        this.infoLayout = new StaticLayout(TextUtils.ellipsize(senderName, senderPaint, (float) this.infoWidth, TextUtils.TruncateAt.END), senderPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
            if (this.infoLayout != null && (this.buttonState == 1 || this.buttonState == 0 || this.buttonState == 3 || this.currentMessageObject.isSecretPhoto() || showSenderName || this.currentMessageObject.type == 8)) {
                Theme.chat_infoPaint.setColor(Theme.getColor(Theme.key_chat_mediaInfoText));
                setDrawableBounds(Theme.chat_timeBackgroundDrawable, this.photoImage.getImageX() + AndroidUtilities.dp(4.0f), this.photoImage.getImageY() + AndroidUtilities.dp(4.0f), this.infoWidth + AndroidUtilities.dp(8.0f), AndroidUtilities.dp(showSenderName ? 20.0f : 16.5f));
                Theme.chat_timeBackgroundDrawable.draw(canvas);
                canvas.save();
                canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(5.5f)));
                this.infoLayout.draw(canvas);
                canvas.restore();
            }
        }
    } else if (this.currentMessageObject.type == 4) {
        if (this.docTitleLayout != null) {
            color = themePrefs.getInt(Theme.pkey_chatRTextColor, -14606047);
            if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_outVenueNameText));
                Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outVenueInfoSelectedText : Theme.key_chat_outVenueInfoText));
                if (Theme.usePlusTheme) {
                    Theme.chat_locationTitlePaint.setColor(color);
                    Theme.chat_locationAddressPaint.setColor(Theme.chatRTimeColor);
                }
            } else {
                Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_inVenueNameText));
                Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inVenueInfoSelectedText : Theme.key_chat_inVenueInfoText));
                if (Theme.usePlusTheme) {
                    Theme.chat_locationTitlePaint.setColor(themePrefs.getInt(Theme.pkey_chatLTextColor, -14606047));
                    Theme.chat_locationAddressPaint.setColor(Theme.chatLTimeColor);
                }
            }
            canvas.save();
            canvas.translate((float) (((this.docTitleOffsetX + this.photoImage.getImageX()) + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f)));
            this.docTitleLayout.draw(canvas);
            canvas.restore();
            if (this.infoLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) ((this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f)));
                this.infoLayout.draw(canvas);
                canvas.restore();
            }
        }
    } else if (this.currentMessageObject.type == 16) {
        Drawable icon;
        Drawable phone;
        if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
            Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
        } else {
            Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
            Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
        }
        this.forceNotDrawTime = true;
        if (this.currentMessageObject.isOutOwner()) {
            i2 = AndroidUtilities.dp(16.0f) + (this.layoutWidth - this.backgroundWidth);
            i = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
            x = i2 - i;
        } else if ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) {
            x = AndroidUtilities.dp((float) (this.leftBound + 26));
        } else {
            x = AndroidUtilities.dp(25.0f);
        }
        this.otherX = x;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) x, (float) (AndroidUtilities.dp(12.0f) + this.namesOffset));
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.docTitleLayout != null) {
            canvas.save();
            canvas.translate((float) (AndroidUtilities.dp(19.0f) + x), (float) (AndroidUtilities.dp(37.0f) + this.namesOffset));
            this.docTitleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.currentMessageObject.isOutOwner()) {
            icon = Theme.chat_msgCallUpGreenDrawable;
            phone = (isDrawSelectedBackground() || this.otherPressed) ? Theme.chat_msgOutCallSelectedDrawable : Theme.chat_msgOutCallDrawable;
        } else {
            TLRPC.PhoneCallDiscardReason reason = this.currentMessageObject.messageOwner.action.reason;
            if ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy)) {
                icon = Theme.chat_msgCallDownRedDrawable;
            } else {
                icon = Theme.chat_msgCallDownGreenDrawable;
            }
            phone = (isDrawSelectedBackground() || this.otherPressed) ? Theme.chat_msgInCallSelectedDrawable : Theme.chat_msgInCallDrawable;
        }
        setDrawableBounds(icon, x - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(36.0f) + this.namesOffset);
        icon.draw(canvas);
        i = AndroidUtilities.dp(205.0f) + x;
        i2 = AndroidUtilities.dp(22.0f);
        this.otherY = i2;
        setDrawableBounds(phone, i, i2);
        phone.draw(canvas);
    } else if (this.currentMessageObject.type == 12) {
        Theme.chat_contactNamePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactNameText : Theme.key_chat_inContactNameText));
        Theme.chat_contactPhonePaint.setColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outContactPhoneText : Theme.key_chat_inContactPhoneText));
        if (Theme.usePlusTheme) {
            if (this.currentMessageObject.messageOwner.media.user_id == 0 || Theme.chatContactNameColor != Theme.defColor) {
                Theme.chat_contactNamePaint.setColor(Theme.chatContactNameColor);
            }
            color = themePrefs.getInt(Theme.pkey_chatLTextColor, Theme.getColor(Theme.key_chat_inContactNameText));
            if (this.currentMessageObject.isOutOwner()) {
                color = themePrefs.getInt(Theme.pkey_chatRTextColor, Theme.getColor(Theme.key_chat_outContactPhoneText));
            }
            Theme.chat_contactPhonePaint.setColor(color);
        }
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(16.0f) + this.namesOffset));
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.docTitleLayout != null) {
            canvas.save();
            canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(39.0f) + this.namesOffset));
            this.docTitleLayout.draw(canvas);
            canvas.restore();
        }
        menuDrawable = this.currentMessageObject.isOutOwner() ? isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable : isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
        i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(48.0f);
        this.otherX = i;
        i2 = this.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
        this.otherY = i2;
        setDrawableBounds(menuDrawable, i, i2);
        menuDrawable.draw(canvas);
    }
    if (this.captionLayout != null) {
        canvas.save();
        if (this.currentMessageObject.type == 1 || this.documentAttachType == 4 || this.currentMessageObject.type == 8) {
            i = this.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
            this.captionX = i;
            float f = (float) i;
            i2 = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) + AndroidUtilities.dp(6.0f);
            this.captionY = i2;
            canvas.translate(f, (float) i2);
        } else {
            i = AndroidUtilities.dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f) + this.backgroundDrawableLeft;
            this.captionX = i;
            float f2 = (float) i;
            i = (this.totalHeight - this.captionHeight) - AndroidUtilities.dp(this.pinnedTop ? 9.0f : 10.0f);
            this.captionY = i;
            canvas.translate(f2, (float) i);
        }
        if (this.pressedLink != null) {
            for (b = 0; b < this.urlPath.size(); b++) {
                canvas.drawPath((Path) this.urlPath.get(b), Theme.chat_urlPaint);
            }
        }
        try {
            this.captionLayout.draw(canvas);
        } catch (Throwable e22) {
            FileLog.e(e22);
        }
        canvas.restore();
    }
    if (this.documentAttachType == 1) {
        int titleY;
        int subtitleY;
        if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
            Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
            Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
            menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
            if (Theme.usePlusTheme) {
                Theme.chat_docNamePaint.setColor(Theme.chatRTextColor);
                Theme.chat_infoPaint.setColor(Theme.chatRTextColor);
                Theme.chat_docBackPaint.setColor(Theme.chatRTextColor);
                menuDrawable.setColorFilter(Theme.chatRTimeColor, PorterDuff.Mode.SRC_IN);
            }
        } else {
            Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
            Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
            Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
            menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
            if (Theme.usePlusTheme) {
                Theme.chat_docNamePaint.setColor(Theme.chatLTextColor);
                Theme.chat_infoPaint.setColor(Theme.chatLTextColor);
                Theme.chat_docBackPaint.setColor(Theme.chatLTextColor);
                menuDrawable.setColorFilter(Theme.chatLTimeColor, PorterDuff.Mode.SRC_IN);
            }
        }
        if (this.drawPhotoImage) {
            if (this.currentMessageObject.type == 0) {
                i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(56.0f);
                this.otherX = i;
                i2 = this.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                this.otherY = i2;
                setDrawableBounds(menuDrawable, i, i2);
            } else {
                i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(40.0f);
                this.otherX = i;
                i2 = this.photoImage.getImageY() + AndroidUtilities.dp(1.0f);
                this.otherY = i2;
                setDrawableBounds(menuDrawable, i, i2);
            }
            x = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
            titleY = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
            subtitleY = (this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
            if (this.buttonState >= 0 && this.buttonState < 4) {
                if (imageDrawn) {
                    this.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed]);
                } else {
                    int image = this.buttonState;
                    if (this.buttonState == 0) {
                        image = this.currentMessageObject.isOutOwner() ? 7 : 10;
                    } else if (this.buttonState == 1) {
                        image = this.currentMessageObject.isOutOwner() ? 8 : 11;
                    }
                    radialProgress = this.radialProgress;
                    Drawable[] drawableArr = Theme.chat_photoStatesDrawables[image];
                    i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? 1 : 0;
                    radialProgress.swapBackground(drawableArr[i]);
                }
            }
            if (imageDrawn) {
                if (this.buttonState == -1) {
                    this.radialProgress.setHideCurrentDrawable(true);
                }
                this.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress) - 1);
            } else {
                this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) (this.photoImage.getImageX() + this.photoImage.getImageWidth()), (float) (this.photoImage.getImageY() + this.photoImage.getImageHeight()));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), Theme.chat_docBackPaint);
                if (this.currentMessageObject.isOutOwner()) {
                    radialProgress = this.radialProgress;
                    if (isDrawSelectedBackground()) {
                        str = Theme.key_chat_outFileProgressSelected;
                    } else {
                        str = Theme.key_chat_outFileProgress;
                    }
                    radialProgress.setProgressColor(Theme.getColor(str));
                } else {
                    this.radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
                }
            }
        } else {
            i = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
            this.otherX = i;
            i2 = this.buttonY - AndroidUtilities.dp(5.0f);
            this.otherY = i2;
            setDrawableBounds(menuDrawable, i, i2);
            x = this.buttonX + AndroidUtilities.dp(53.0f);
            titleY = this.buttonY + AndroidUtilities.dp(4.0f);
            subtitleY = this.buttonY + AndroidUtilities.dp(27.0f);
            if (this.currentMessageObject.isOutOwner()) {
                radialProgress = this.radialProgress;
                if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                    str = Theme.key_chat_outAudioSelectedProgress;
                } else {
                    str = Theme.key_chat_outAudioProgress;
                }
                radialProgress.setProgressColor(Theme.getColor(str));
            } else {
                radialProgress = this.radialProgress;
                str = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress;
                radialProgress.setProgressColor(Theme.getColor(str));
            }
        }
        menuDrawable.draw(canvas);
        try {
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas.translate((float) (this.docTitleOffsetX + x), (float) titleY);
                this.docTitleLayout.draw(canvas);
                canvas.restore();
            }
        } catch (Throwable e222) {
            FileLog.e(e222);
        }
        try {
            if (this.infoLayout != null) {
                canvas.save();
                canvas.translate((float) x, (float) subtitleY);
                this.infoLayout.draw(canvas);
                canvas.restore();
            }
        } catch (Throwable e2222) {
            FileLog.e(e2222);
        }
    }
    if (this.drawImageButton && this.photoImage.getVisible()) {
        this.radialProgress.draw(canvas);
    }
    if (!this.botButtons.isEmpty()) {
        int addX;
        if (this.currentMessageObject.isOutOwner()) {
            addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
        } else {
            addX = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
        }
        a = 0;
        while (a < this.botButtons.size()) {
            BotButton button = (BotButton) this.botButtons.get(a);
            y = (button.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
            Theme.chat_systemDrawable.setColorFilter(a == this.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
            Theme.chat_systemDrawable.setBounds(button.x + addX, y, (button.x + addX) + button.width, button.height + y);
            Theme.chat_systemDrawable.draw(canvas);
            canvas.save();
            canvas.translate((float) ((button.x + addX) + AndroidUtilities.dp(5.0f)), (float) (((AndroidUtilities.dp(44.0f) - button.title.getLineBottom(button.title.getLineCount() - 1)) / 2) + y));
            button.title.draw(canvas);
            canvas.restore();
            if (button.button instanceof TLRPC.TL_keyboardButtonUrl) {
                setDrawableBounds(Theme.chat_botLinkDrawalbe, (((button.x + button.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                Theme.chat_botLinkDrawalbe.draw(canvas);
            } else if (button.button instanceof TLRPC.TL_keyboardButtonSwitchInline) {
                setDrawableBounds(Theme.chat_botInlineDrawable, (((button.x + button.width) - AndroidUtilities.dp(3.0f)) - Theme.chat_botInlineDrawable.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                Theme.chat_botInlineDrawable.draw(canvas);
            } else if ((button.button instanceof TLRPC.TL_keyboardButtonCallback) || (button.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) || (button.button instanceof TLRPC.TL_keyboardButtonGame) || (button.button instanceof TLRPC.TL_keyboardButtonBuy)) {
                boolean drawProgress = (((button.button instanceof TLRPC.TL_keyboardButtonCallback) || (button.button instanceof TLRPC.TL_keyboardButtonGame) || (button.button instanceof TLRPC.TL_keyboardButtonBuy)) && SendMessagesHelper.getInstance().isSendingCallback(this.currentMessageObject, button.button)) || ((button.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance().isSendingCurrentLocation(this.currentMessageObject, button.button));
                if (drawProgress || !(drawProgress || button.progressAlpha == 0.0f)) {
                    Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (button.progressAlpha * 255.0f)));
                    x = ((button.x + button.width) - AndroidUtilities.dp(12.0f)) + addX;
                    this.rect.set((float) x, (float) (AndroidUtilities.dp(4.0f) + y), (float) (AndroidUtilities.dp(8.0f) + x), (float) (AndroidUtilities.dp(12.0f) + y));
                    canvas.drawArc(this.rect, (float) button.angle, 220.0f, false, Theme.chat_botProgressPaint);
                    invalidate(((int) this.rect.left) - AndroidUtilities.dp(2.0f), ((int) this.rect.top) - AndroidUtilities.dp(2.0f), ((int) this.rect.right) + AndroidUtilities.dp(2.0f), ((int) this.rect.bottom) + AndroidUtilities.dp(2.0f));
                    long newTime = System.currentTimeMillis();
                    if (Math.abs(button.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                        long delta = newTime - button.lastUpdateTime;
                        button.angle = (int) (((float) button.angle) + (((float) (360 * delta)) / 2000.0f));
                        button.angle = button.angle - ((button.angle / 360) * 360);
                        if (drawProgress) {
                            if (button.progressAlpha < 1.0f) {
                                button.progressAlpha = button.progressAlpha + (((float) delta) / 200.0f);
                                if (button.progressAlpha > 1.0f) {
                                    button.progressAlpha = 1.0f;
                                }
                            }
                        } else if (button.progressAlpha > 0.0f) {
                            button.progressAlpha = button.progressAlpha - (((float) delta) / 200.0f);
                            if (button.progressAlpha < 0.0f) {
                                button.progressAlpha = 0.0f;
                            }
                        }
                    }
                    button.lastUpdateTime = newTime;
                }
            }
            a++;
        }
    }
}
//        int color;
//        int i;
//        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
//        if (needNewVisiblePart && currentMessageObject.type == 0) {
//            getLocalVisibleRect(scrollRect);
//            setVisiblePart(scrollRect.top, scrollRect.bottom - scrollRect.top);
//            needNewVisiblePart = false;
//        }
//
//        forceNotDrawTime = false;
//        photoImage.setPressed(isDrawSelectedBackground());
//        photoImage.setVisible(!PhotoViewer.getInstance().isShowingImage(currentMessageObject), false);
//        radialProgress.setHideCurrentDrawable(false);
//        radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress));
//
//        boolean imageDrawn = false;
//        if (currentMessageObject.type == 0) {
//            if (currentMessageObject.isOutOwner()) {
//                textX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11);
//            } else {
//                textX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(!mediaBackground && pinnedBottom ? 11 : 17);
//            }
//            if (hasGamePreview) {
//                textX += AndroidUtilities.dp(11);
//                textY = AndroidUtilities.dp(14) + namesOffset;
//                if (siteNameLayout != null) {
//                    textY += siteNameLayout.getLineBottom(siteNameLayout.getLineCount() - 1);
//                }
//            } else if (hasInvoicePreview) {
//                textY = AndroidUtilities.dp(14) + namesOffset;
//                if (siteNameLayout != null) {
//                    textY += siteNameLayout.getLineBottom(siteNameLayout.getLineCount() - 1);
//                }
//            } else {
//                textY = AndroidUtilities.dp(10) + namesOffset;
//            }
//
//            if (currentMessageObject.textLayoutBlocks != null && !currentMessageObject.textLayoutBlocks.isEmpty()) {
//                if (fullyDraw) {
//                    firstVisibleBlockNum = 0;
//                    lastVisibleBlockNum = currentMessageObject.textLayoutBlocks.size();
//                }
//                if (firstVisibleBlockNum >= 0) {
//                    for (int a = firstVisibleBlockNum; a <= lastVisibleBlockNum; a++) {
//                        if (a >= currentMessageObject.textLayoutBlocks.size()) {
//                            break;
//                        }
//                        MessageObject.TextLayoutBlock block = currentMessageObject.textLayoutBlocks.get(a);
//                        canvas.save();
//                        canvas.translate(textX - (block.isRtl() ? (int) Math.ceil(currentMessageObject.textXOffset) : 0), textY + block.textYOffset);
//                        if (pressedLink != null && a == linkBlockNum) {
//                            for (int b = 0; b < urlPath.size(); b++) {
//                                canvas.drawPath(urlPath.get(b), Theme.chat_urlPaint);
//                            }
//                        }
//                        if (a == linkSelectionBlockNum && !urlPathSelection.isEmpty()) {
//                            for (int b = 0; b < urlPathSelection.size(); b++) {
//                                canvas.drawPath(urlPathSelection.get(b), Theme.chat_textSearchSelectionPaint);
//                            }
//                        }
//                        try {
//                            block.textLayout.draw(canvas);
//                        } catch (Exception e) {
//                            FileLog.e(e);
//                        }
//                        canvas.restore();
//                    }
//                }
//            }
//
//            if (hasLinkPreview || hasGamePreview || hasInvoicePreview) {
//                TextPaint textPaint;
//                int startY;
//                int linkX;
//                if (hasGamePreview) {
//                    startY = AndroidUtilities.dp(14) + namesOffset;
//                    linkX = textX - AndroidUtilities.dp(10);
//                } else if (hasInvoicePreview) {
//                    startY = AndroidUtilities.dp(14) + namesOffset;
//                    linkX = textX + AndroidUtilities.dp(1);
//                } else {
//                    startY = textY + currentMessageObject.textHeight + AndroidUtilities.dp(8);
//                    linkX = textX + AndroidUtilities.dp(1);
//                }
//                int linkPreviewY = startY;
//                int smallImageStartY = 0;
//
//                if (!hasInvoicePreview) {
////                    int i;
////                    Theme.chat_replyLinePaint.setColor(Theme.getColor(currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine));
//                    Paint paint = Theme.chat_replyLinePaint;
//                    if (Theme.usePlusTheme) {
//                        i = this.currentMessageObject.isOutOwner() ? Theme.chatRLinkColor : Theme.chatLLinkColor;
//                    } else {
//                        i = Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outPreviewLine : Theme.key_chat_inPreviewLine);
//                    }
//                    paint.setColor(i);
//                    canvas.drawRect(linkX, linkPreviewY - AndroidUtilities.dp(3), linkX + AndroidUtilities.dp(2), linkPreviewY + linkPreviewHeight + AndroidUtilities.dp(3), Theme.chat_replyLinePaint);
//                }
//
//                if (siteNameLayout != null) {
////                    Theme.chat_replyNamePaint.setColor(Theme.getColor(currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText));
////                    int i;
//                    textPaint = Theme.chat_replyNamePaint;
//                    if (Theme.usePlusTheme) {
//                        i = this.currentMessageObject.isOutOwner() ? Theme.chatRLinkColor : Theme.chatLLinkColor;
//                    } else {
//                        i = Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outSiteNameText : Theme.key_chat_inSiteNameText);
//                    }
//                    textPaint.setColor(i);
//                    canvas.save();
//                    canvas.translate(linkX + (hasInvoicePreview ? 0 : AndroidUtilities.dp(10)), linkPreviewY - AndroidUtilities.dp(3));
//                    siteNameLayout.draw(canvas);
//                    canvas.restore();
//                    linkPreviewY += siteNameLayout.getLineBottom(siteNameLayout.getLineCount() - 1);
//                }
//                if ((hasGamePreview || hasInvoicePreview) && currentMessageObject.textHeight != 0) {
//                    startY += currentMessageObject.textHeight + AndroidUtilities.dp(4);
//                    linkPreviewY += currentMessageObject.textHeight + AndroidUtilities.dp(4);
//                }
//
//                if (drawPhotoImage && drawInstantView) {
//                    if (linkPreviewY != startY) {
//                        linkPreviewY += AndroidUtilities.dp(2);
//                    }
//                    photoImage.setImageCoords(linkX + AndroidUtilities.dp(10), linkPreviewY, photoImage.getImageWidth(), photoImage.getImageHeight());
//                    if (drawImageButton) {
//                        int size = AndroidUtilities.dp(48);
//                        buttonX = (int) (photoImage.getImageX() + (photoImage.getImageWidth() - size) / 2.0f);
//                        buttonY = (int) (photoImage.getImageY() + (photoImage.getImageHeight() - size) / 2.0f);
//                        radialProgress.setProgressRect(buttonX, buttonY, buttonX + size, buttonY + size);
//                    }
//                    imageDrawn = photoImage.draw(canvas);
//                    linkPreviewY += photoImage.getImageHeight() + AndroidUtilities.dp(6);
//                }
//
//                if (currentMessageObject.isOutOwner()) {
////                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
////                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
//                    Theme.chat_replyNamePaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_messageTextOut));
//                    Theme.chat_replyTextPaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_messageTextOut));
//
//                } else {
////                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
////                    Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
////                    int i;
//                    Theme.chat_replyNamePaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_messageTextIn));
//                    textPaint = Theme.chat_replyTextPaint;
//                    if (Theme.usePlusTheme) {
//                        i = Theme.chatLTextColor;
//                    } else {
//                        i = Theme.getColor(Theme.key_chat_messageTextIn);
//                    }
//                    textPaint.setColor(i);
//                }
//                if (titleLayout != null) {
//                    if (linkPreviewY != startY) {
//                        linkPreviewY += AndroidUtilities.dp(2);
//                    }
//                    smallImageStartY = linkPreviewY - AndroidUtilities.dp(1);
//                    canvas.save();
//                    canvas.translate(linkX + AndroidUtilities.dp(10) + titleX, linkPreviewY - AndroidUtilities.dp(3));
//                    titleLayout.draw(canvas);
//                    canvas.restore();
//                    linkPreviewY += titleLayout.getLineBottom(titleLayout.getLineCount() - 1);
//                }
//
//                if (authorLayout != null) {
//                    if (linkPreviewY != startY) {
//                        linkPreviewY += AndroidUtilities.dp(2);
//                    }
//                    if (smallImageStartY == 0) {
//                        smallImageStartY = linkPreviewY - AndroidUtilities.dp(1);
//                    }
//                    canvas.save();
//                    canvas.translate(linkX + AndroidUtilities.dp(10) + authorX, linkPreviewY - AndroidUtilities.dp(3));
//                    authorLayout.draw(canvas);
//                    canvas.restore();
//                    linkPreviewY += authorLayout.getLineBottom(authorLayout.getLineCount() - 1);
//                }
//
//                if (descriptionLayout != null) {
//                    if (linkPreviewY != startY) {
//                        linkPreviewY += AndroidUtilities.dp(2);
//                    }
//                    if (smallImageStartY == 0) {
//                        smallImageStartY = linkPreviewY - AndroidUtilities.dp(1);
//                    }
//                    descriptionY = linkPreviewY - AndroidUtilities.dp(3);
//                    canvas.save();
//                    canvas.translate(linkX + (hasInvoicePreview ? 0 : AndroidUtilities.dp(10)) + descriptionX, descriptionY);
//                    if (pressedLink != null && linkBlockNum == -10) {
//                        for (int b = 0; b < urlPath.size(); b++) {
//                            canvas.drawPath(urlPath.get(b), Theme.chat_urlPaint);
//                        }
//                    }
//                    descriptionLayout.draw(canvas);
//                    canvas.restore();
//                    linkPreviewY += descriptionLayout.getLineBottom(descriptionLayout.getLineCount() - 1);
//                }
//
//                if (drawPhotoImage && !drawInstantView) {
//                    if (linkPreviewY != startY) {
//                        linkPreviewY += AndroidUtilities.dp(2);
//                    }
//
//                    if (isSmallImage) {
//                        photoImage.setImageCoords(linkX + backgroundWidth - AndroidUtilities.dp(81), smallImageStartY, photoImage.getImageWidth(), photoImage.getImageHeight());
//                    } else {
//                        photoImage.setImageCoords(linkX + (hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10)), linkPreviewY, photoImage.getImageWidth(), photoImage.getImageHeight());
//                        if (drawImageButton) {
//                            int size = AndroidUtilities.dp(48);
//                            buttonX = (int) (photoImage.getImageX() + (photoImage.getImageWidth() - size) / 2.0f);
//                            buttonY = (int) (photoImage.getImageY() + (photoImage.getImageHeight() - size) / 2.0f);
//                            radialProgress.setProgressRect(buttonX, buttonY, buttonX + size, buttonY + size);
//                        }
//                    }
//                    imageDrawn = photoImage.draw(canvas);
//                }
//                if (videoInfoLayout != null && (!drawPhotoImage || photoImage.getVisible())) {
//                    int x;
//                    int y;
//                    if (hasGamePreview || hasInvoicePreview) {
//                        if (drawPhotoImage) {
//                            x = photoImage.getImageX() + AndroidUtilities.dp(8.5f);
//                            y = photoImage.getImageY() + AndroidUtilities.dp(6);
//                            Theme.chat_timeBackgroundDrawable.setBounds(x - AndroidUtilities.dp(4), y - AndroidUtilities.dp(1.5f), x + durationWidth + AndroidUtilities.dp(4), y + AndroidUtilities.dp(16.5f));
//                            Theme.chat_timeBackgroundDrawable.draw(canvas);
//                        } else {
//                            x = linkX;
//                            y = linkPreviewY;
//                        }
//                    } else {
//                        x = photoImage.getImageX() + photoImage.getImageWidth() - AndroidUtilities.dp(8) - durationWidth;
//                        y = photoImage.getImageY() + photoImage.getImageHeight() - AndroidUtilities.dp(19);
//                        Theme.chat_timeBackgroundDrawable.setBounds(x - AndroidUtilities.dp(4), y - AndroidUtilities.dp(1.5f), x + durationWidth + AndroidUtilities.dp(4), y + AndroidUtilities.dp(14.5f));
//                        Theme.chat_timeBackgroundDrawable.draw(canvas);
//                    }
//
//                    canvas.save();
//                    canvas.translate(x, y);
//                    if (this.hasInvoicePreview) {
//                        if (this.drawPhotoImage) {
//                            Theme.chat_shipmentPaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_previewGameText));
//                        } else if (this.currentMessageObject.isOutOwner()) {
//                            Theme.chat_shipmentPaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_messageTextOut));
//                        } else {
//                            Theme.chat_shipmentPaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_messageTextIn));
//                        }
//                    }
//                    videoInfoLayout.draw(canvas);
//                    canvas.restore();
//                }
//
//                if (drawInstantView) {
//                    Drawable instantDrawable;
//                    int instantX = linkX + AndroidUtilities.dp(10);
//                    int instantY = linkPreviewY + AndroidUtilities.dp(4);
//                    Paint backPaint = Theme.chat_instantViewRectPaint;
//                    if (currentMessageObject.isOutOwner()) {
//                        instantDrawable = this.instantPressed ? Theme.chat_msgOutInstantSelectedDrawable : Theme.chat_msgOutInstantDrawable;
//                        if (Theme.usePlusTheme) {
//                            instantDrawable.setColorFilter(new PorterDuffColorFilter(Theme.chatRLinkColor, PorterDuff.Mode.MULTIPLY));
//                        }
//                        textPaint = Theme.chat_instantViewPaint;
//                        if (Theme.usePlusTheme) {
//                            i = Theme.chatRLinkColor;
//                        } else {
//                            i = Theme.getColor(this.instantPressed ? Theme.key_chat_outPreviewInstantSelectedText : Theme.key_chat_outPreviewInstantText);
//                        }
//                        textPaint.setColor(i);
//                        if (Theme.usePlusTheme) {
//                            i = Theme.chatRLinkColor;
//                        } else {
//                            i = Theme.getColor(this.instantPressed ? Theme.key_chat_outPreviewInstantSelectedText : Theme.key_chat_outPreviewInstantText);
//                        }
//                        backPaint.setColor(i);
//                    } else {
//                        instantDrawable = this.instantPressed ? Theme.chat_msgInInstantSelectedDrawable : Theme.chat_msgInInstantDrawable;
//                        if (Theme.usePlusTheme) {
//                            instantDrawable.setColorFilter(new PorterDuffColorFilter(Theme.chatLLinkColor, PorterDuff.Mode.MULTIPLY));
//                        }
//                        textPaint = Theme.chat_instantViewPaint;
//                        if (Theme.usePlusTheme) {
//                            i = Theme.chatLLinkColor;
//                        } else {
//                            i = Theme.getColor(this.instantPressed ? Theme.key_chat_inPreviewInstantSelectedText : Theme.key_chat_inPreviewInstantText);
//                        }
//                        textPaint.setColor(i);
//                        if (Theme.usePlusTheme) {
//                            i = Theme.chatLLinkColor;
//                        } else {
//                            i = Theme.getColor(this.instantPressed ? Theme.key_chat_inPreviewInstantSelectedText : Theme.key_chat_inPreviewInstantText);
//                        }
//                        backPaint.setColor(i);
//                    }
//
//                    rect.set(instantX, instantY, instantX + instantWidth, instantY + AndroidUtilities.dp(30));
//                    canvas.drawRoundRect(rect, AndroidUtilities.dp(3), AndroidUtilities.dp(3), backPaint);
//                    setDrawableBounds(instantDrawable, instantX + AndroidUtilities.dp(9), instantY + AndroidUtilities.dp(9), AndroidUtilities.dp(9), AndroidUtilities.dp(13));
//                    instantDrawable.draw(canvas);
//                    if (instantViewLayout != null) {
//                        canvas.save();
//                        canvas.translate(instantX + instantTextX + AndroidUtilities.dp(24), instantY + AndroidUtilities.dp(8));
//                        instantViewLayout.draw(canvas);
//                        canvas.restore();
//                    }
//                }
//            }
//            drawTime = true;
//        } else if (drawPhotoImage) {
//            imageDrawn = photoImage.draw(canvas);
//            drawTime = photoImage.getVisible();
//        }
//
//        if (buttonState == -1 && currentMessageObject.isSecretPhoto()) {
//            int drawable = 4;
//            if (currentMessageObject.messageOwner.destroyTime != 0) {
//                if (currentMessageObject.isOutOwner()) {
//                    drawable = 6;
//                } else {
//                    drawable = 5;
//                }
//            }
//            setDrawableBounds(Theme.chat_photoStatesDrawables[drawable][buttonPressed], buttonX, buttonY);
//            Theme.chat_photoStatesDrawables[drawable][buttonPressed].setAlpha((int) (255 * (1.0f - radialProgress.getAlpha())));
//            Theme.chat_photoStatesDrawables[drawable][buttonPressed].draw(canvas);
//            if (!currentMessageObject.isOutOwner() && currentMessageObject.messageOwner.destroyTime != 0) {
//                long msTime = System.currentTimeMillis() + ConnectionsManager.getInstance().getTimeDifference() * 1000;
//                float progress = Math.max(0, (long) currentMessageObject.messageOwner.destroyTime * 1000 - msTime) / (currentMessageObject.messageOwner.ttl * 1000.0f);
//                canvas.drawArc(deleteProgressRect, -90, -360 * progress, true, Theme.chat_deleteProgressPaint);
//                if (progress != 0) {
//                    int offset = AndroidUtilities.dp(2);
//                    invalidate((int) deleteProgressRect.left - offset, (int) deleteProgressRect.top - offset, (int) deleteProgressRect.right + offset * 2, (int) deleteProgressRect.bottom + offset * 2);
//                }
//                updateSecretTimeText(currentMessageObject);
//            }
//        }
//
//        if (Theme.chatMemberColorCheck) {
//            senderPaint.setColor(Theme.chatMemberColor);
//        } else if (this.currentMessageObject != null && this.currentMessageObject.isFromUser()) {
//            senderPaint.setColor(AvatarDrawable.getNameColorForId(MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id)).id));
//        }
//
//        if ((documentAttachType == DOCUMENT_ATTACH_TYPE_GIF || currentMessageObject.type == 8) && !currentMessageObject.isVideoVoice()) { //TODO
//            if (photoImage.getVisible() && !hasGamePreview) {
//                setDrawableBounds(Theme.chat_msgMediaMenuDrawable, otherX = photoImage.getImageX() + photoImage.getImageWidth() - AndroidUtilities.dp(14), otherY = photoImage.getImageY() + AndroidUtilities.dp(8.1f));
//                Theme.chat_msgMediaMenuDrawable.draw(canvas);
//            }
//        } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
//            color = themePrefs.getInt(Theme.pkey_chatRTextColor, -14606047);
//            if (currentMessageObject.isOutOwner()) {
//                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_outAudioTitleText));
//                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(Theme.key_chat_outAudioPerfomerText));
//                Theme.chat_audioTimePaint.setColor(Theme.getColor(Theme.key_chat_outAudioDurationText));
//                if (Theme.usePlusTheme) {
//                    Theme.chat_audioTitlePaint.setColor(color);
//                    Theme.chat_audioPerformerPaint.setColor(color);
//                    Theme.chat_audioTimePaint.setColor(Theme.chatRTimeColor);
//                }
//                radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() || buttonPressed != 0 ? Theme.key_chat_outAudioSelectedProgress : Theme.key_chat_outAudioProgress));
//            } else {
//                Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_inAudioTitleText));
//                Theme.chat_audioPerformerPaint.setColor(Theme.getColor(Theme.key_chat_inAudioPerfomerText));
//                Theme.chat_audioTimePaint.setColor(Theme.getColor(Theme.key_chat_inAudioDurationText));
//                if (Theme.usePlusTheme) {
//                    color = themePrefs.getInt(Theme.pkey_chatLTextColor, -14606047);
//                    Theme.chat_audioTitlePaint.setColor(color);
//                    Theme.chat_audioPerformerPaint.setColor(color);
//                    Theme.chat_audioTimePaint.setColor(Theme.chatLTimeColor);
//                }
//                radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() || buttonPressed != 0 ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
//            }
//            radialProgress.draw(canvas);
//
//            canvas.save();
//            canvas.translate(timeAudioX + songX, AndroidUtilities.dp(13) + namesOffset + mediaOffsetY);
//            songLayout.draw(canvas);
//            canvas.restore();
//
//            canvas.save();
//            if (MediaController.getInstance().isPlayingAudio(currentMessageObject)) {
//                canvas.translate(seekBarX, seekBarY);
//                seekBar.draw(canvas);
//            } else {
//                canvas.translate(timeAudioX + performerX, AndroidUtilities.dp(35) + namesOffset + mediaOffsetY);
//                performerLayout.draw(canvas);
//            }
//            canvas.restore();
//
//            canvas.save();
//            canvas.translate(timeAudioX, AndroidUtilities.dp(57) + namesOffset + mediaOffsetY);
//            durationLayout.draw(canvas);
//            canvas.restore();
//
//            Drawable menuDrawable;
//            if (currentMessageObject.isOutOwner()) {
//                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
//            } else {
//                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
//            }
//            setDrawableBounds(menuDrawable, otherX = buttonX + backgroundWidth - AndroidUtilities.dp(currentMessageObject.type == 0 ? 58 : 48), otherY = buttonY - AndroidUtilities.dp(5));
//            menuDrawable.draw(canvas);
//        } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
//            if (currentMessageObject.isOutOwner()) {
//                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outAudioDurationSelectedText : Theme.key_chat_outAudioDurationText));
//                radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() || buttonPressed != 0 ? Theme.key_chat_outAudioSelectedProgress : Theme.key_chat_outAudioProgress));
//            } else {
//                Theme.chat_audioTimePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inAudioDurationSelectedText : Theme.key_chat_inAudioDurationText));
//                radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() || buttonPressed != 0 ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
//            }
//            radialProgress.draw(canvas);
//
//            canvas.save();
//            if (useSeekBarWaweform) {
//                canvas.translate(seekBarX + AndroidUtilities.dp(13), seekBarY);
//                seekBarWaveform.draw(canvas);
//            } else {
//                canvas.translate(seekBarX, seekBarY);
//                seekBar.draw(canvas);
//            }
//            canvas.restore();
//
//            canvas.save();
//            canvas.translate(timeAudioX, AndroidUtilities.dp(44) + namesOffset + mediaOffsetY);
//            durationLayout.draw(canvas);
//            canvas.restore();
//
//            if (currentMessageObject.type != 0 && currentMessageObject.messageOwner.to_id.channel_id == 0 && currentMessageObject.isContentUnread()) {
//                Theme.chat_docBackPaint.setColor(Theme.getColor(currentMessageObject.isOutOwner() ? Theme.key_chat_outVoiceSeekbarFill : Theme.key_chat_inVoiceSeekbarFill));
//                canvas.drawCircle(timeAudioX + timeWidthAudio + AndroidUtilities.dp(6), AndroidUtilities.dp(51) + namesOffset + mediaOffsetY, AndroidUtilities.dp(3), Theme.chat_docBackPaint);
//            }
//        }
//
//        if (this.currentMessageObject.type == 1 || this.documentAttachType == 4 || this.currentMessageObject.type == 8) {            if (photoImage.getVisible()) {
//                if (documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
//                    setDrawableBounds(Theme.chat_msgMediaMenuDrawable, otherX = photoImage.getImageX() + photoImage.getImageWidth() - AndroidUtilities.dp(14), otherY = photoImage.getImageY() + AndroidUtilities.dp(8.1f));
//                    Theme.chat_msgMediaMenuDrawable.draw(canvas);
//                }
//            boolean showSenderName = this.currentMessageObject.type == 1;
//            if (showSenderName) {
//                String senderName = getCurrentNameString(this.currentMessageObject).replaceAll("\\p{C}", "").trim();
//                try {
//                    if (this.isChat && this.currentMessageObject.isFromUser()) {
//                        this.infoWidth = (int) Math.min(Math.ceil((double) senderPaint.measureText(senderName)), (double) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f));
//                        this.infoLayout = new StaticLayout(TextUtils.ellipsize(senderName, senderPaint, (float) this.infoWidth, TextUtils.TruncateAt.END), senderPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//                    }
//                } catch (Throwable e2) {
//                    FileLog.e(e2);
//                }
//            }
//            if (this.infoLayout != null && (this.buttonState == 1 || this.buttonState == 0 || this.buttonState == 3 || this.currentMessageObject.isSecretPhoto() || showSenderName || this.currentMessageObject.type == 8)) {                    Theme.chat_infoPaint.setColor(Theme.getColor(Theme.key_chat_mediaInfoText));
////                    setDrawableBounds(Theme.chat_timeBackgroundDrawable, photoImage.getImageX() + AndroidUtilities.dp(4), photoImage.getImageY() + AndroidUtilities.dp(4), infoWidth + AndroidUtilities.dp(8), AndroidUtilities.dp(16.5f));
//                    setDrawableBounds(Theme.chat_timeBackgroundDrawable, this.photoImage.getImageX() + AndroidUtilities.dp(4.0f), this.photoImage.getImageY() + AndroidUtilities.dp(4.0f), this.infoWidth + AndroidUtilities.dp(8.0f), AndroidUtilities.dp(showSenderName ? 20.0f : 16.5f));
//                    Theme.chat_timeBackgroundDrawable.draw(canvas);
//                    canvas.save();
//                    canvas.translate(photoImage.getImageX() + AndroidUtilities.dp(8), photoImage.getImageY() + AndroidUtilities.dp(5.5f));
//                    infoLayout.draw(canvas);
//                    canvas.restore();
//                }
//            }
//        } else {
//            if (currentMessageObject.type == 4) {
//                if (docTitleLayout != null) {
//                    color = themePrefs.getInt(Theme.pkey_chatRTextColor, -14606047);
//                    if (currentMessageObject.isOutOwner()) {
//                        Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_outVenueNameText));
//                        Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outVenueInfoSelectedText : Theme.key_chat_outVenueInfoText));
//                        if (Theme.usePlusTheme) {
//                            Theme.chat_locationTitlePaint.setColor(color);
//                            Theme.chat_locationAddressPaint.setColor(Theme.chatRTimeColor);
//                        }
//                    } else {
//                        Theme.chat_locationTitlePaint.setColor(Theme.getColor(Theme.key_chat_inVenueNameText));
//                        Theme.chat_locationAddressPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inVenueInfoSelectedText : Theme.key_chat_inVenueInfoText));
//                        if (Theme.usePlusTheme) {
//                            Theme.chat_locationTitlePaint.setColor(themePrefs.getInt(Theme.pkey_chatLTextColor, -14606047));
//                            Theme.chat_locationAddressPaint.setColor(Theme.chatLTimeColor);
//                        }
//                    }
//
//                    canvas.save();
//                    canvas.translate(docTitleOffsetX + photoImage.getImageX() + photoImage.getImageWidth() + AndroidUtilities.dp(10), photoImage.getImageY() + AndroidUtilities.dp(8));
//                    docTitleLayout.draw(canvas);
//                    canvas.restore();
//
//                    if (infoLayout != null) {
//                        canvas.save();
//                        canvas.translate(photoImage.getImageX() + photoImage.getImageWidth() + AndroidUtilities.dp(10), photoImage.getImageY() + docTitleLayout.getLineBottom(docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13));
//                        infoLayout.draw(canvas);
//                        canvas.restore();
//                    }
//                }
//            } else if (currentMessageObject.type == 16) {
//                if (currentMessageObject.isOutOwner()) {
//                    Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
//                    Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
//                } else {
//                    Theme.chat_audioTitlePaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
//                    Theme.chat_contactPhonePaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
//                }
//                forceNotDrawTime = true;
//                int x;
//                if (currentMessageObject.isOutOwner()) {
//
////                    i2 = AndroidUtilities.dp(16.0f) + (this.layoutWidth - this.backgroundWidth);
//                    i = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
////                    x = i2 - i;
//
//                    x = layoutWidth - backgroundWidth + AndroidUtilities.dp(16) - i;
//                } else {
//                    if ((isChat || this.showAvatar) && currentMessageObject.isFromUser()) {
//                        x = AndroidUtilities.dp((float) (this.leftBound + 26));
////                        x = AndroidUtilities.dp(74);
//                    } else {
//                        x = AndroidUtilities.dp(25);
//                    }
//                }
//                otherX = x;
//                if (titleLayout != null) {
//                    canvas.save();
//                    canvas.translate(x, AndroidUtilities.dp(12) + namesOffset);
//                    titleLayout.draw(canvas);
//                    canvas.restore();
//                }
//                if (docTitleLayout != null) {
//                    canvas.save();
//                    canvas.translate(x + AndroidUtilities.dp(19), AndroidUtilities.dp(37) + namesOffset);
//                    docTitleLayout.draw(canvas);
//                    canvas.restore();
//                }
//                Drawable icon;
//                Drawable phone;
//                if (currentMessageObject.isOutOwner()) {
//                    icon = Theme.chat_msgCallUpGreenDrawable;
//                    phone = isDrawSelectedBackground() || otherPressed ? Theme.chat_msgOutCallSelectedDrawable : Theme.chat_msgOutCallDrawable;
//                } else {
//                    TLRPC.PhoneCallDiscardReason reason = currentMessageObject.messageOwner.action.reason;
//                    if (reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed || reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
//                        icon = Theme.chat_msgCallDownRedDrawable;
//                    } else {
//                        icon = Theme.chat_msgCallDownGreenDrawable;
//                    }
//                    phone = isDrawSelectedBackground() || otherPressed ? Theme.chat_msgInCallSelectedDrawable : Theme.chat_msgInCallDrawable;
//                }
//                setDrawableBounds(icon, x - AndroidUtilities.dp(3), AndroidUtilities.dp(36) + namesOffset);
//                icon.draw(canvas);
//
//                setDrawableBounds(phone, x + AndroidUtilities.dp(205), otherY = AndroidUtilities.dp(22));
//                phone.draw(canvas);
//            } else if (currentMessageObject.type == 12) {
//                Theme.chat_contactNamePaint.setColor(Theme.getColor(currentMessageObject.isOutOwner() ?  Theme.key_chat_outContactNameText : Theme.key_chat_inContactNameText));
//                Theme.chat_contactPhonePaint.setColor(Theme.getColor(currentMessageObject.isOutOwner() ? Theme.key_chat_outContactPhoneText : Theme.key_chat_inContactPhoneText));
//                if (Theme.usePlusTheme) {
//                    if (this.currentMessageObject.messageOwner.media.user_id == 0 || Theme.chatContactNameColor != Theme.defColor) {
//                        Theme.chat_contactNamePaint.setColor(Theme.chatContactNameColor);
//                    }
//                    color = themePrefs.getInt(Theme.pkey_chatLTextColor, Theme.getColor(Theme.key_chat_inContactNameText));
//                    if (this.currentMessageObject.isOutOwner()) {
//                        color = themePrefs.getInt(Theme.pkey_chatRTextColor, Theme.getColor(Theme.key_chat_outContactPhoneText));
//                    }
//                    Theme.chat_contactPhonePaint.setColor(color);
//                }
//                if (titleLayout != null) {
//                    canvas.save();
//                    canvas.translate(photoImage.getImageX() + photoImage.getImageWidth() + AndroidUtilities.dp(9), AndroidUtilities.dp(16) + namesOffset);
//                    titleLayout.draw(canvas);
//                    canvas.restore();
//                }
//                if (docTitleLayout != null) {
//                    canvas.save();
//                    canvas.translate(photoImage.getImageX() + photoImage.getImageWidth() + AndroidUtilities.dp(9), AndroidUtilities.dp(39) + namesOffset);
//                    docTitleLayout.draw(canvas);
//                    canvas.restore();
//                }
//
//                Drawable menuDrawable;
//                if (currentMessageObject.isOutOwner()) {
//                    menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
//                } else {
//                    menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
//                }
//                setDrawableBounds(menuDrawable, otherX = photoImage.getImageX() + backgroundWidth - AndroidUtilities.dp(48), otherY = photoImage.getImageY() - AndroidUtilities.dp(5));
//                menuDrawable.draw(canvas);
//            }
//        }
//
//        if (captionLayout != null) {
//            canvas.save();
//            if (currentMessageObject.type == 1 || documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || currentMessageObject.type == 8) {
//                canvas.translate(captionX = photoImage.getImageX() + AndroidUtilities.dp(5), captionY = photoImage.getImageY() + photoImage.getImageHeight() + AndroidUtilities.dp(6));
//            } else {
//                canvas.translate(captionX = backgroundDrawableLeft + AndroidUtilities.dp(currentMessageObject.isOutOwner() ? 11 : 17), captionY = totalHeight - captionHeight - AndroidUtilities.dp(pinnedTop ? 9 : 10));
//            }
//            if (pressedLink != null) {
//                for (int b = 0; b < urlPath.size(); b++) {
//                    canvas.drawPath(urlPath.get(b), Theme.chat_urlPaint);
//                }
//            }
//            try {
//                captionLayout.draw(canvas);
//            } catch (Exception e) {
//                FileLog.e(e);
//            }
//            canvas.restore();
//        }
//
//        if (documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
//            Drawable menuDrawable;
//            if (currentMessageObject.isOutOwner()) {
//                Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_outFileNameText));
//                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileInfoSelectedText : Theme.key_chat_outFileInfoText));
//                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileBackgroundSelected : Theme.key_chat_outFileBackground));
////                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
//                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutMenuSelectedDrawable : Theme.chat_msgOutMenuDrawable;
//                if (Theme.usePlusTheme) {
//                    Theme.chat_docNamePaint.setColor(Theme.chatRTextColor);
//                    Theme.chat_infoPaint.setColor(Theme.chatRTextColor);
//                    Theme.chat_docBackPaint.setColor(Theme.chatRTextColor);
//                    menuDrawable.setColorFilter(Theme.chatRTimeColor, PorterDuff.Mode.SRC_IN);
//                }
//            } else {
//                Theme.chat_docNamePaint.setColor(Theme.getColor(Theme.key_chat_inFileNameText));
//                Theme.chat_infoPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileInfoSelectedText : Theme.key_chat_inFileInfoText));
//                Theme.chat_docBackPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileBackgroundSelected : Theme.key_chat_inFileBackground));
//                menuDrawable = isDrawSelectedBackground() ? Theme.chat_msgInMenuSelectedDrawable : Theme.chat_msgInMenuDrawable;
//                if (Theme.usePlusTheme) {
//                    Theme.chat_docNamePaint.setColor(Theme.chatLTextColor);
//                    Theme.chat_infoPaint.setColor(Theme.chatLTextColor);
//                    Theme.chat_docBackPaint.setColor(Theme.chatLTextColor);
//                    menuDrawable.setColorFilter(Theme.chatLTimeColor, PorterDuff.Mode.SRC_IN);
//                }
//
//
//                int x;
//                int titleY;
//                int subtitleY;
//                if (drawPhotoImage) {
//                    if (currentMessageObject.type == 0) {
//                        setDrawableBounds(menuDrawable, otherX = photoImage.getImageX() + backgroundWidth - AndroidUtilities.dp(56), otherY = photoImage.getImageY() + AndroidUtilities.dp(1));
//                    } else {
//                        setDrawableBounds(menuDrawable, otherX = photoImage.getImageX() + backgroundWidth - AndroidUtilities.dp(40), otherY = photoImage.getImageY() + AndroidUtilities.dp(1));
//                    }
//
//                    x = photoImage.getImageX() + photoImage.getImageWidth() + AndroidUtilities.dp(10);
//                    titleY = photoImage.getImageY() + AndroidUtilities.dp(8);
//                    subtitleY = photoImage.getImageY() + docTitleLayout.getLineBottom(docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13);
//                    if (buttonState >= 0 && buttonState < 4) {
//                        if (!imageDrawn) {
//                            int image = buttonState;
//                            if (buttonState == 0) {
//                                image = currentMessageObject.isOutOwner() ? 7 : 10;
//                            } else if (buttonState == 1) {
//                                image = currentMessageObject.isOutOwner() ? 8 : 11;
//                            }
//                            radialProgress.swapBackground(Theme.chat_photoStatesDrawables[image][isDrawSelectedBackground() || buttonPressed != 0 ? 1 : 0]);
//                        } else {
//                            radialProgress.swapBackground(Theme.chat_photoStatesDrawables[buttonState][buttonPressed]);
//                        }
//                    }
//
//                    if (!imageDrawn) {
//                        rect.set(photoImage.getImageX(), photoImage.getImageY(), photoImage.getImageX() + photoImage.getImageWidth(), photoImage.getImageY() + photoImage.getImageHeight());
//                        canvas.drawRoundRect(rect, AndroidUtilities.dp(3), AndroidUtilities.dp(3), Theme.chat_docBackPaint);
//                        if (currentMessageObject.isOutOwner()) {
//                            radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outFileProgressSelected : Theme.key_chat_outFileProgress));
//                        } else {
//                            radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inFileProgressSelected : Theme.key_chat_inFileProgress));
//                        }
//                    } else {
//                        if (buttonState == -1) {
//                            radialProgress.setHideCurrentDrawable(true);
//                        }
//                        radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_mediaProgress) - 1);
//                    }
//                } else {
//                    setDrawableBounds(menuDrawable, otherX = buttonX + backgroundWidth - AndroidUtilities.dp(currentMessageObject.type == 0 ? 58 : 48), otherY = buttonY - AndroidUtilities.dp(5));
//                    x = buttonX + AndroidUtilities.dp(53);
//                    titleY = buttonY + AndroidUtilities.dp(4);
//                    subtitleY = buttonY + AndroidUtilities.dp(27);
//                    if (currentMessageObject.isOutOwner()) {
//                        radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() || buttonPressed != 0 ? Theme.key_chat_outAudioSelectedProgress : Theme.key_chat_outAudioProgress));
//                    } else {
//                        radialProgress.setProgressColor(Theme.getColor(isDrawSelectedBackground() || buttonPressed != 0 ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
//                    }
//                }
//                menuDrawable.draw(canvas);
//
//                try {
//                    if (docTitleLayout != null) {
//                        canvas.save();
//                        canvas.translate(x + docTitleOffsetX, titleY);
//                        docTitleLayout.draw(canvas);
//                        canvas.restore();
//                    }
//                } catch (Exception e) {
//                    FileLog.e(e);
//                }
//
//                try {
//                    if (infoLayout != null) {
//                        canvas.save();
//                        canvas.translate(x, subtitleY);
//                        infoLayout.draw(canvas);
//                        canvas.restore();
//                    }
//                } catch (Exception e) {
//                    FileLog.e(e);
//                }
//            }
//            if (drawImageButton && photoImage.getVisible()) {
//                radialProgress.draw(canvas);
//            }
//
//            if (!botButtons.isEmpty()) {
//                int addX;
//                if (currentMessageObject.isOutOwner()) {
//                    addX = getMeasuredWidth() - widthForButtons - AndroidUtilities.dp(10);
//                } else {
//                    addX = backgroundDrawableLeft + AndroidUtilities.dp(mediaBackground ? 1 : 7);
//                }
//                for (int a = 0; a < botButtons.size(); a++) {
//                    BotButton button = botButtons.get(a);
//                    int y = button.y + layoutHeight - AndroidUtilities.dp(2);
//                    Theme.chat_systemDrawable.setColorFilter(a == pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
//                    Theme.chat_systemDrawable.setBounds(button.x + addX, y, button.x + addX + button.width, y + button.height);
//                    Theme.chat_systemDrawable.draw(canvas);
//                    canvas.save();
//                    canvas.translate(button.x + addX + AndroidUtilities.dp(5), y + (AndroidUtilities.dp(44) - button.title.getLineBottom(button.title.getLineCount() - 1)) / 2);
//                    button.title.draw(canvas);
//                    canvas.restore();
//                    if (button.button instanceof TLRPC.TL_keyboardButtonUrl) {
//                        int x = button.x + button.width - AndroidUtilities.dp(3) - Theme.chat_botLinkDrawalbe.getIntrinsicWidth() + addX;
//                        setDrawableBounds(Theme.chat_botLinkDrawalbe, x, y + AndroidUtilities.dp(3));
//                        Theme.chat_botLinkDrawalbe.draw(canvas);
//                    } else if (button.button instanceof TLRPC.TL_keyboardButtonSwitchInline) {
//                        int x = button.x + button.width - AndroidUtilities.dp(3) - Theme.chat_botInlineDrawable.getIntrinsicWidth() + addX;
//                        setDrawableBounds(Theme.chat_botInlineDrawable, x, y + AndroidUtilities.dp(3));
//                        Theme.chat_botInlineDrawable.draw(canvas);
//                    } else if (button.button instanceof TLRPC.TL_keyboardButtonCallback || button.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation || button.button instanceof TLRPC.TL_keyboardButtonGame || button.button instanceof TLRPC.TL_keyboardButtonBuy) {
//                        boolean drawProgress = (button.button instanceof TLRPC.TL_keyboardButtonCallback || button.button instanceof TLRPC.TL_keyboardButtonGame || button.button instanceof TLRPC.TL_keyboardButtonBuy) && SendMessagesHelper.getInstance().isSendingCallback(currentMessageObject, button.button) ||
//                                button.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation && SendMessagesHelper.getInstance().isSendingCurrentLocation(currentMessageObject, button.button);
//                        if (drawProgress || !drawProgress && button.progressAlpha != 0) {
//                            Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (button.progressAlpha * 255)));
//                            int x = button.x + button.width - AndroidUtilities.dp(9 + 3) + addX;
//                            rect.set(x, y + AndroidUtilities.dp(4), x + AndroidUtilities.dp(8), y + AndroidUtilities.dp(8 + 4));
//                            canvas.drawArc(rect, button.angle, 220, false, Theme.chat_botProgressPaint);
//                            invalidate((int) rect.left - AndroidUtilities.dp(2), (int) rect.top - AndroidUtilities.dp(2), (int) rect.right + AndroidUtilities.dp(2), (int) rect.bottom + AndroidUtilities.dp(2));
//                            long newTime = System.currentTimeMillis();
//                            if (Math.abs(button.lastUpdateTime - System.currentTimeMillis()) < 1000) {
//                                long delta = (newTime - button.lastUpdateTime);
//                                float dt = 360 * delta / 2000.0f;
//                                button.angle += dt;
//                                button.angle -= 360 * (button.angle / 360);
//                                if (drawProgress) {
//                                    if (button.progressAlpha < 1.0f) {
//                                        button.progressAlpha += delta / 200.0f;
//                                        if (button.progressAlpha > 1.0f) {
//                                            button.progressAlpha = 1.0f;
//                                        }
//                                    }
//                                } else {
//                                    if (button.progressAlpha > 0.0f) {
//                                        button.progressAlpha -= delta / 200.0f;
//                                        if (button.progressAlpha < 0.0f) {
//                                            button.progressAlpha = 0.0f;
//                                        }
//                                    }
//                                }
//                            }
//                            button.lastUpdateTime = newTime;
//                        }
//                    }
//                }
//            }
//        }
//
//
//
    private Drawable getDrawableForCurrentState() {
        if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (buttonState == -1) {
                return null;
            }
            radialProgress.setAlphaForPrevious(false);
            return Theme.chat_fileStatesDrawable[currentMessageObject.isOutOwner() ? buttonState : buttonState + 5][isDrawSelectedBackground() || buttonPressed != 0 ? 1 : 0];
        } else {
            if (documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT && !drawPhotoImage) {
                radialProgress.setAlphaForPrevious(false);
                if (buttonState == -1) {
                    return Theme.chat_fileStatesDrawable[currentMessageObject.isOutOwner() ? 3 : 8][isDrawSelectedBackground() ? 1 : 0];
                } else if (buttonState == 0) {
                    return Theme.chat_fileStatesDrawable[currentMessageObject.isOutOwner() ? 2 : 7][isDrawSelectedBackground() ? 1 : 0];
                } else if (buttonState == 1) {
                    return Theme.chat_fileStatesDrawable[currentMessageObject.isOutOwner() ? 4 : 9][isDrawSelectedBackground() ? 1 : 0];
                }
            } else {
                radialProgress.setAlphaForPrevious(true);
                if (buttonState >= 0 && buttonState < 4) {
                    if (documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        int image = buttonState;
                        if (buttonState == 0) {
                            image = currentMessageObject.isOutOwner() ? 7 : 10;
                        } else if (buttonState == 1) {
                            image = currentMessageObject.isOutOwner() ? 8 : 11;
                        }
                        return Theme.chat_photoStatesDrawables[image][isDrawSelectedBackground() || buttonPressed != 0 ? 1 : 0];
                    } else {
                        return Theme.chat_photoStatesDrawables[buttonState][buttonPressed];
                    }
                } else if (buttonState == -1 && documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    return Theme.chat_photoStatesDrawables[currentMessageObject.isOutOwner() ? 9 : 12][isDrawSelectedBackground() ? 1 : 0];
                }
            }
        }
        return null;
    }

    private int getMaxNameWidth() {
        if (documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
            int maxWidth;
            if (AndroidUtilities.isTablet()) {
                if (isChat && !currentMessageObject.isOutOwner() && currentMessageObject.isFromUser()) {
                    maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42);
                } else {
                    maxWidth = AndroidUtilities.getMinTabletSide();
                }
            } else {
                if (isChat && !currentMessageObject.isOutOwner() && currentMessageObject.isFromUser()) {
                    maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42);
                } else {
                    maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                }
            }
            return maxWidth - backgroundWidth - AndroidUtilities.dp(57);
        }
        return backgroundWidth - AndroidUtilities.dp(mediaBackground ? 22 : 31);
    }

    public void updateButtonState(boolean animated) {
        String fileName = null;
        boolean fileExists = false;
        if (currentMessageObject.type == 1) {
            if (currentPhotoObject == null) {
                return;
            }
            fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
//            fileName = FileLoader.getAttachFileName(currentPhotoObject); //TODO Multi keep original filename
            fileExists = currentMessageObject.mediaExists;
        } else if (currentMessageObject.type == 8 || documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || currentMessageObject.type == 9 || documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (currentMessageObject.useCustomPhoto) {
                buttonState = 1;
                radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                return;
            }
            if (currentMessageObject.attachPathExists) {
                fileName = currentMessageObject.messageOwner.attachPath;
                fileExists = true;
            } else if (!currentMessageObject.isSendError() || documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                fileName = currentMessageObject.getFileName();
                fileExists = currentMessageObject.mediaExists;
            }
        } else if (documentAttachType != DOCUMENT_ATTACH_TYPE_NONE) {
            fileName = FileLoader.getAttachFileName(documentAttach);
            fileExists = currentMessageObject.mediaExists;
        } else if (currentPhotoObject != null) {
            fileName = FileLoader.getAttachFileName(currentPhotoObject);
            fileExists = currentMessageObject.mediaExists;
        }
        if (TextUtils.isEmpty(fileName)) {
            radialProgress.setBackground(null, false, false);
            return;
        }
        boolean fromBot = currentMessageObject.messageOwner.params != null && currentMessageObject.messageOwner.params.containsKey("query_id");

        if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (currentMessageObject.isOut() && currentMessageObject.isSending() || currentMessageObject.isSendError() && fromBot) {
                MediaController.getInstance().addLoadingFileObserver(currentMessageObject.messageOwner.attachPath, currentMessageObject, this);
                buttonState = 4;
                radialProgress.setBackground(getDrawableForCurrentState(), !fromBot, animated);
                if (!fromBot) {
                    Float progress = ImageLoader.getInstance().getFileProgress(currentMessageObject.messageOwner.attachPath);
                    if (progress == null && SendMessagesHelper.getInstance().isSendingMessage(currentMessageObject.getId())) {
                        progress = 1.0f;
                    }
                    radialProgress.setProgress(progress != null ? progress : 0, false);
                } else {
                    radialProgress.setProgress(0, false);
                }
            } else {
                if (fileExists) {
                    MediaController.getInstance().removeLoadingFileObserver(this);
                    boolean playing = MediaController.getInstance().isPlayingAudio(currentMessageObject);
                    if (!playing || playing && MediaController.getInstance().isAudioPaused()) {
                        buttonState = 0;
                    } else {
                        buttonState = 1;
                    }
                    radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                } else {
                    MediaController.getInstance().addLoadingFileObserver(fileName, currentMessageObject, this);
                    if (!FileLoader.getInstance().isLoadingFile(fileName)) {
                        buttonState = 2;
                        radialProgress.setProgress(0, animated);
                        radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                    } else {
                        buttonState = 4;
                        Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                        if (progress != null) {
                            radialProgress.setProgress(progress, animated);
                        } else {
                            radialProgress.setProgress(0, animated);
                        }
                        radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                    }
                }
            }
            updateAudioProgress();
        } else if (currentMessageObject.type == 0 && documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT && documentAttachType != DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (currentPhotoObject == null || !drawImageButton) {
                return;
            }
            if (!fileExists) {
                MediaController.getInstance().addLoadingFileObserver(fileName, currentMessageObject, this);
                float setProgress = 0;
                boolean progressVisible = false;
                if (!FileLoader.getInstance().isLoadingFile(fileName)) {
                    if (!cancelLoading &&
                            (documentAttachType == 0 && MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_PHOTO) ||
                                    documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_GIF))) {
                        progressVisible = true;
                        buttonState = 1;
                    } else {
                        buttonState = 0;
                    }
                } else {
                    progressVisible = true;
                    buttonState = 1;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress : 0;
                }
                radialProgress.setProgress(setProgress, false);
                radialProgress.setBackground(getDrawableForCurrentState(), progressVisible, animated);
                invalidate();
            } else {
                MediaController.getInstance().removeLoadingFileObserver(this);
                if (documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && !photoImage.isAllowStartAnimation()) {
                    buttonState = 2;
                } else {
                    buttonState = -1;
                }
                radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
            }
        } else {
            if (currentMessageObject.isOut() && currentMessageObject.isSending()) {
                if (currentMessageObject.messageOwner.attachPath != null && currentMessageObject.messageOwner.attachPath.length() > 0) {
                    MediaController.getInstance().addLoadingFileObserver(currentMessageObject.messageOwner.attachPath, currentMessageObject, this);
                    boolean needProgress = currentMessageObject.messageOwner.attachPath == null || !currentMessageObject.messageOwner.attachPath.startsWith("http");
                    HashMap<String, String> params = currentMessageObject.messageOwner.params;
                    if (currentMessageObject.messageOwner.message != null && params != null && (params.containsKey("url") || params.containsKey("bot"))) {
                        needProgress = false;
                        buttonState = -1;
                    } else {
                        buttonState = 1;
                    }
                    radialProgress.setBackground(getDrawableForCurrentState(), needProgress, animated);
                    if (needProgress) {
                        Float progress = ImageLoader.getInstance().getFileProgress(currentMessageObject.messageOwner.attachPath);
                        if (progress == null && SendMessagesHelper.getInstance().isSendingMessage(currentMessageObject.getId())) {
                            progress = 1.0f;
                        }
                        radialProgress.setProgress(progress != null ? progress : 0, false);
                    } else {
                        radialProgress.setProgress(0, false);
                    }
                    invalidate();
                }
            } else {
                if (currentMessageObject.messageOwner.attachPath != null && currentMessageObject.messageOwner.attachPath.length() != 0) {
                    MediaController.getInstance().removeLoadingFileObserver(this);
                }
                if (!fileExists) {
                    MediaController.getInstance().addLoadingFileObserver(fileName, currentMessageObject, this);
                    float setProgress = 0;
                    boolean progressVisible = false;
                    if (!FileLoader.getInstance().isLoadingFile(fileName)) {
                        if (!cancelLoading &&
                                (currentMessageObject.type == 1 && MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_PHOTO) ||
                                        currentMessageObject.type == 8 && MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_GIF) && MessageObject.isNewGifDocument(currentMessageObject.messageOwner.media.document)) ) {
                            progressVisible = true;
                            buttonState = 1;
                        } else {
                            buttonState = 0;
                        }
                    } else {
                        progressVisible = true;
                        buttonState = 1;
                        Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                        setProgress = progress != null ? progress : 0;
                    }
                    radialProgress.setBackground(getDrawableForCurrentState(), progressVisible, animated);
                    radialProgress.setProgress(setProgress, false);
                    invalidate();
                } else {
                    MediaController.getInstance().removeLoadingFileObserver(this);
                    if (currentMessageObject.type == 8 && !photoImage.isAllowStartAnimation()) {
                        buttonState = 2;
                    } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                        buttonState = 3;
                    } else {
                        buttonState = -1;
                    }
                    radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                    if (photoNotSet) {
                        setMessageObject(currentMessageObject, pinnedBottom, pinnedTop);
                    }
                    invalidate();
                }
            }
        }
    }

    private void didPressedButton(boolean animated) {
        if (buttonState == 0) {
            if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                if (delegate.needPlayAudio(currentMessageObject)) {
                    buttonState = 1;
                    radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else {
                cancelLoading = false;
                radialProgress.setProgress(0, false);
                if (currentMessageObject.type == 1) {
//                    photoImage.setImage(currentPhotoObject.location, currentPhotoFilter, currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null, currentPhotoFilter, currentPhotoObject.size, null, false);
                    this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, this.currentPhotoFilter, this.currentPhotoObject.size, null, false);
                    this.radialProgress.setSizeAndType((long) this.currentPhotoObject.size, this.currentMessageObject.type);
                } else if (currentMessageObject.type == 8) {
                    currentMessageObject.audioProgress = 2;
                    photoImage.setImage(currentMessageObject.messageOwner.media.document, null, currentPhotoObject != null ? currentPhotoObject.location : null, currentPhotoFilter, currentMessageObject.messageOwner.media.document.size, null, false);
                } else if (currentMessageObject.type == 9) {
                    FileLoader.getInstance().loadFile(currentMessageObject.messageOwner.media.document, false, false);
                } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                    FileLoader.getInstance().loadFile(documentAttach, true, false);
                } else if (currentMessageObject.type == 0 && documentAttachType != DOCUMENT_ATTACH_TYPE_NONE) {
                    if (documentAttachType == DOCUMENT_ATTACH_TYPE_GIF) {
                        photoImage.setImage(currentMessageObject.messageOwner.media.webpage.document, null, currentPhotoObject.location, currentPhotoFilter, currentMessageObject.messageOwner.media.webpage.document.size, null, false);
                        currentMessageObject.audioProgress = 2;
                    } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        FileLoader.getInstance().loadFile(currentMessageObject.messageOwner.media.webpage.document, false, false);
                    }
                } else {
                    photoImage.setImage(currentPhotoObject.location, currentPhotoFilter, currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null, currentPhotoFilterThumb, 0, null, false);
                }
                buttonState = 1;
                radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                invalidate();
            }
        } else if (buttonState == 1) {
            if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                boolean result = MediaController.getInstance().pauseAudio(currentMessageObject);
                if (result) {
                    buttonState = 0;
                    radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else {
                if (currentMessageObject.isOut() && currentMessageObject.isSending()) {
                    delegate.didPressedCancelSendButton(this);
                } else {
                    cancelLoading = true;
                    if (documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        FileLoader.getInstance().cancelLoadFile(documentAttach);
                    } else if (currentMessageObject.type == 0 || currentMessageObject.type == 1 || currentMessageObject.type == 8) {
                        photoImage.cancelLoadImage();
                    } else if (currentMessageObject.type == 9) {
                        FileLoader.getInstance().cancelLoadFile(currentMessageObject.messageOwner.media.document);
                    }
                    buttonState = 0;
                    radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                    invalidate();
                }
            }
        } else if (buttonState == 2) {
            if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                radialProgress.setProgress(0, false);
                FileLoader.getInstance().loadFile(documentAttach, true, false);
                buttonState = 4;
                radialProgress.setBackground(getDrawableForCurrentState(), true, false);
                invalidate();
            } else {
                photoImage.setAllowStartAnimation(true);
                photoImage.startAnimation();
                currentMessageObject.audioProgress = 0;
                buttonState = -1;
                radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
            }
        } else if (buttonState == 3) {
            delegate.didPressedImage(this);
        } else if (buttonState == 4) {
            if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                if (currentMessageObject.isOut() && currentMessageObject.isSending() || currentMessageObject.isSendError()) {
                    if (delegate != null) {
                        delegate.didPressedCancelSendButton(this);
                    }
                } else {
                    FileLoader.getInstance().cancelLoadFile(documentAttach);
                    buttonState = 2;
                    radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            }
        }
    }

    @Override
    public void onFailedDownload(String fileName) {
        updateButtonState(documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC);
    }

    @Override
    public void onSuccessDownload(String fileName) {
        if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            updateButtonState(true);
            updateWaveform();
        } else {
            radialProgress.setProgress(1, true);
            if (currentMessageObject.type == 0) {
                if (documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && currentMessageObject.audioProgress != 1) {
                    buttonState = 2;
                    didPressedButton(true);
                } else if (!photoNotSet) {
                    updateButtonState(true);
                } else {
                    setMessageObject(currentMessageObject, pinnedBottom, pinnedTop);
                }
            } else {
                if (!photoNotSet || currentMessageObject.type == 8 && currentMessageObject.audioProgress != 1) {
                    if (currentMessageObject.type == 8 && currentMessageObject.audioProgress != 1) {
                        photoNotSet = false;
                        buttonState = 2;
                        didPressedButton(true);
                    } else {
                        updateButtonState(true);
                    }
                }
                if (photoNotSet) {
                    setMessageObject(currentMessageObject, pinnedBottom, pinnedTop);
                }
            }
        }
    }

    @Override
    public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        if (currentMessageObject != null && set && !thumb && !currentMessageObject.mediaExists && !currentMessageObject.attachPathExists) {
            currentMessageObject.mediaExists = true;
            updateButtonState(true);
        }
    }

    @Override
    public void onProgressDownload(String fileName, float progress) {
        radialProgress.setProgress(progress, true);
        if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (buttonState != 4) {
                updateButtonState(false);
            }
        } else {
            if (buttonState != 1) {
                updateButtonState(false);
            }
        }
    }

    @Override
    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        radialProgress.setProgress(progress, true);
    }

    @Override
    public void onProvideStructure(ViewStructure structure) {
        super.onProvideStructure(structure);
        if (allowAssistant && Build.VERSION.SDK_INT >= 23) {
            if (currentMessageObject.messageText != null && currentMessageObject.messageText.length() > 0) {
                structure.setText(currentMessageObject.messageText);
            } else if (currentMessageObject.caption != null && currentMessageObject.caption.length() > 0) {
                structure.setText(currentMessageObject.caption);
            }
        }
    }

    public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
        delegate = chatMessageCellDelegate;
    }

    public void setAllowAssistant(boolean value) {
        allowAssistant = value;
    }

    private void measureTime(MessageObject messageObject) {
        boolean hasSign = !messageObject.isOutOwner() && messageObject.messageOwner.from_id > 0 && messageObject.messageOwner.post;
        TLRPC.User signUser = MessagesController.getInstance().getUser(messageObject.messageOwner.from_id);
        if (hasSign && signUser == null) {
            hasSign = false;
        }
        String timeString;
        TLRPC.User author = null;
        if (currentMessageObject.isFromUser()) {
            author = MessagesController.getInstance().getUser(messageObject.messageOwner.from_id);
        }
        if (messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.via_bot_name == null && (author == null || !author.bot) && (messageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_EDITED) != 0) {
            timeString = LocaleController.getString("EditedMessage", R.string.EditedMessage) + " " + LocaleController.getInstance().formatterDay.format((long) (messageObject.messageOwner.date) * 1000);
        } else {
            timeString = LocaleController.getInstance().formatterDay.format((long) (messageObject.messageOwner.date) * 1000);
        }
        if (hasSign) {
            currentTimeString = ", " + timeString;
        } else {
            currentTimeString = timeString;
        }
        timeTextWidth = timeWidth = (int) Math.ceil(Theme.chat_timePaint.measureText(currentTimeString));
        if ((messageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
            currentViewsString = String.format("%s", LocaleController.formatShortNumber(Math.max(1, messageObject.messageOwner.views), null));
            viewsTextWidth = (int) Math.ceil(Theme.chat_timePaint.measureText(currentViewsString));
            timeWidth += viewsTextWidth + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(10);
        }
        if (hasSign) {
            if (availableTimeWidth == 0) {
                availableTimeWidth = AndroidUtilities.dp(1000);
            }
            CharSequence name = ContactsController.formatName(signUser.first_name, signUser.last_name).replace('\n', ' ');
            int widthForSign = availableTimeWidth - timeWidth;
            int width = (int) Math.ceil(Theme.chat_timePaint.measureText(name, 0, name.length()));
            if (width > widthForSign) {
                name = TextUtils.ellipsize(name, Theme.chat_timePaint, widthForSign, TextUtils.TruncateAt.END);
                width = widthForSign;
            }
            currentTimeString = name + currentTimeString;
            timeTextWidth += width;
            timeWidth += width;
        }
    }

    private boolean isDrawSelectedBackground() {
        return isPressed() && isCheckPressed || !isCheckPressed && isPressed || isHighlighted;
    }

//    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
//        if (messageObject.type == 13) {
//            return false;
//        } else if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.channel_id != 0 && !messageObject.isOut()) {
//            return Theme.plusShowDSBtnChannels;
//        } else if (messageObject.isFromUser()) {
//            if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty || messageObject.messageOwner.media == null || messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage && !(messageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) {
//                return false;
//            }
//            TLRPC.User user = MessagesController.getInstance().getUser(messageObject.messageOwner.from_id);
//            if (user != null && user.bot) {
//                return true;
//            }
//            if (!messageObject.isOut()) {
//                if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame || messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
//                    return true;
//                }
//                if (messageObject.isMegagroup()) {
//                    TLRPC.Chat chat = MessagesController.getInstance().getChat(messageObject.messageOwner.to_id.channel_id);
//                    return chat != null && chat.username != null && chat.username.length() > 0 && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact) && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo);
//                }
//            }
//        } else if (messageObject.messageOwner.from_id < 0 || messageObject.messageOwner.post) {
//            if (messageObject.messageOwner.to_id.channel_id != 0 && (messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.reply_to_msg_id == 0 || messageObject.type != 13)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        if (messageObject.type == 13) {
            return false;
        }
        if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.channel_id != 0 && !messageObject.isOut()) {
            return Theme.plusShowDSBtnChannels;
        }
        if (messageObject.isFromUser()) {
            TLRPC.User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
            if (!(user == null || messageObject.isOut() || messageObject.isMegagroup())) {
                if (!this.isChat && !user.bot && Theme.plusShowDSBtnUsers) {
                    return true;
                }
                if (messageObject.messageOwner.to_id.chat_id != 0 && Theme.plusShowDSBtnGroups) {
                    return true;
                }
            }
            if (user != null && user.bot) {
                return Theme.plusShowDSBtnBots;
            }
            if (!messageObject.isOut()) {
                if ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame) || (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
                    return true;
                }
                if (messageObject.isMegagroup()) {
                    if (Theme.plusShowDSBtnSGroups) {
                        return true;
                    }
                    TLRPC.Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(messageObject.messageOwner.to_id.channel_id));
                    if (chat == null || chat.username == null || chat.username.length() <= 0 || (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact) || (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) || !Theme.plusShowDSBtnSGroups) {
                        return false;
                    }
                    return true;
                }
            }
        } else if ((messageObject.messageOwner.from_id < 0 || messageObject.messageOwner.post) && messageObject.messageOwner.to_id.channel_id != 0 && ((messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.reply_to_msg_id == 0) || messageObject.type != 13)) {
            return Theme.plusShowDSBtnChannels;
        }
        return false;
    }

    private void setMessageObjectInternal(MessageObject messageObject) {
        if ((messageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
            if (currentMessageObject.isContentUnread() && !currentMessageObject.isOut()) {
                MessagesController.getInstance().addToViewsQueue(currentMessageObject.messageOwner, false);
                currentMessageObject.setContentIsRead();
            } else if (!currentMessageObject.viewsReloaded) {
                MessagesController.getInstance().addToViewsQueue(currentMessageObject.messageOwner, true);
                currentMessageObject.viewsReloaded = true;
            }
        }

        if (currentMessageObject.isFromUser()) {
            currentUser = MessagesController.getInstance().getUser(currentMessageObject.messageOwner.from_id);
            if (!(Theme.chatHideStatusIndicator || UserObject.isUserSelf(this.currentUser))) {
                setStatusColor(this.currentUser);
            }
        } else if (currentMessageObject.messageOwner.from_id < 0) {
            currentChat = MessagesController.getInstance().getChat(-currentMessageObject.messageOwner.from_id);
        } else if (currentMessageObject.messageOwner.post) {
            currentChat = MessagesController.getInstance().getChat(currentMessageObject.messageOwner.to_id.channel_id);
        }

        if (((this.isChat || this.showAvatar) && !messageObject.isOutOwner() && messageObject.isFromUser()) || (((this.showMyAvatar && !this.isChat) || (this.showMyAvatarGroup && this.isChat)) && messageObject.isOutOwner())) {
            isAvatarVisible = true;
            if (currentUser != null) {
                if (currentUser.photo != null) {
                    currentPhoto = currentUser.photo.photo_small;
                } else {
                    currentPhoto = null;
                }
                avatarDrawable.setInfo(currentUser);
            } else if (currentChat != null) {
                if (currentChat.photo != null) {
                    currentPhoto = currentChat.photo.photo_small;
                } else {
                    currentPhoto = null;
                }
                avatarDrawable.setInfo(currentChat);
                if (!this.currentUser.bot) {
                    this.drawStatus = true;
                }
            } else {
                currentPhoto = null;
                avatarDrawable.setInfo(messageObject.messageOwner.from_id, null, null, false);
            }
            avatarImage.setImage(currentPhoto, "50_50", avatarDrawable, null, false);
        }


        measureTime(messageObject);

        namesOffset = 0;

        String viaUsername = null;
        CharSequence viaString = null;
        if (messageObject.messageOwner.via_bot_id != 0) {
            TLRPC.User botUser = MessagesController.getInstance().getUser(messageObject.messageOwner.via_bot_id);
            if (botUser != null && botUser.username != null && botUser.username.length() > 0) {
                viaUsername = "@" + botUser.username;
                viaString = AndroidUtilities.replaceTags(String.format(" via <b>%s</b>", viaUsername));
                viaWidth = (int) Math.ceil(Theme.chat_replyNamePaint.measureText(viaString, 0, viaString.length()));
                currentViaBotUser = botUser;
            }
        } else if (messageObject.messageOwner.via_bot_name != null && messageObject.messageOwner.via_bot_name.length() > 0) {
            viaUsername = "@" + messageObject.messageOwner.via_bot_name;
            viaString = AndroidUtilities.replaceTags(String.format(" via <b>%s</b>", viaUsername));
            viaWidth = (int) Math.ceil(Theme.chat_replyNamePaint.measureText(viaString, 0, viaString.length()));
        }

        boolean authorName = drawName && isChat && !currentMessageObject.isOutOwner();
        boolean viaBot = (messageObject.messageOwner.fwd_from == null || messageObject.type == 14) && viaUsername != null;
        if (authorName || viaBot) {
            drawNameLayout = true;
            nameWidth = getMaxNameWidth();
            if (nameWidth < 0) {
                nameWidth = AndroidUtilities.dp(100);
            }

            if (authorName) {
                if (currentUser != null) {
                    currentNameString = UserObject.getUserName(currentUser);
                    String currentUsernameString = this.currentUser.username;
                    if (currentUsernameString != null && Theme.chatShowUsernameCheck) {
                        this.currentNameString = this.currentNameString.replaceAll("\\p{C}", " ");
                        this.currentNameString = this.currentNameString.trim().replaceAll(" +", " ") + " [@" + currentUsernameString + "]";
                    }
                } else if (currentChat != null) {
                    currentNameString = currentChat.title;
                } else {
                    currentNameString = "DELETED";
                }
            } else {
                currentNameString = "";
            }
            CharSequence nameStringFinal = TextUtils.ellipsize(currentNameString.replace('\n', ' '), Theme.chat_namePaint, nameWidth - (viaBot ? viaWidth : 0), TextUtils.TruncateAt.END);
            if (viaBot) {
                viaNameWidth = (int) Math.ceil(Theme.chat_namePaint.measureText(nameStringFinal, 0, nameStringFinal.length()));
                if (viaNameWidth != 0) {
                    viaNameWidth += AndroidUtilities.dp(4);
                }
                int color;
                if (currentMessageObject.type == 13) {
                    color = Theme.getColor(Theme.key_chat_stickerViaBotNameText);
                } else {
                    color = Theme.getColor(currentMessageObject.isOutOwner() ? Theme.key_chat_outViaBotNameText : Theme.key_chat_inViaBotNameText);
                }
                if (Theme.usePlusTheme) {
                    if (this.currentMessageObject.isOutOwner()) {
                        color = Theme.chatForwardRColor;
                    } else {
                        color = Theme.chatForwardLColor;
                    }
                }
                if (currentNameString.length() > 0) {
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String.format("%s via %s", nameStringFinal, viaUsername));
                    stringBuilder.setSpan(new TypefaceSpan(Typeface.DEFAULT, 0, color), nameStringFinal.length() + 1, nameStringFinal.length() + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, color), nameStringFinal.length() + 5, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    nameStringFinal = stringBuilder;
                } else {
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String.format("via %s", viaUsername));
                    stringBuilder.setSpan(new TypefaceSpan(Typeface.DEFAULT, 0, color), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, color), 4, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    nameStringFinal = stringBuilder;
                }
                nameStringFinal = TextUtils.ellipsize(nameStringFinal, Theme.chat_namePaint, nameWidth, TextUtils.TruncateAt.END);
            }
            try {
                nameLayout = new StaticLayout(nameStringFinal, Theme.chat_namePaint, nameWidth + AndroidUtilities.dp(2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (nameLayout != null && nameLayout.getLineCount() > 0) {
                    nameWidth = (int) Math.ceil(nameLayout.getLineWidth(0));
                    if (messageObject.type != 13) {
                        namesOffset += AndroidUtilities.dp(19);
                    }
                    nameOffsetX = nameLayout.getLineLeft(0);
                } else {
                    nameWidth = 0;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (currentNameString.length() == 0) {
                currentNameString = null;
            }
        } else {
            currentNameString = null;
            nameLayout = null;
            nameWidth = 0;
        }

        currentForwardUser = null;
        currentForwardNameString = null;
        currentForwardChannel = null;
        forwardedNameLayout[0] = null;
        forwardedNameLayout[1] = null;
        forwardedNameWidth = 0;
        if (drawForwardedName && messageObject.isForwarded()) {
            if (messageObject.messageOwner.fwd_from.channel_id != 0) {
                currentForwardChannel = MessagesController.getInstance().getChat(messageObject.messageOwner.fwd_from.channel_id);
            }
            if (messageObject.messageOwner.fwd_from.from_id != 0) {
                currentForwardUser = MessagesController.getInstance().getUser(messageObject.messageOwner.fwd_from.from_id);
            }

            if (currentForwardUser != null || currentForwardChannel != null) {
                if (currentForwardChannel != null) {
                    if (currentForwardUser != null) {
                        currentForwardNameString = String.format("%s (%s)", currentForwardChannel.title, UserObject.getUserName(currentForwardUser));
                    } else {
                        currentForwardNameString = currentForwardChannel.title;
                    }
                } else if (currentForwardUser != null) {
                    currentForwardNameString = UserObject.getUserName(currentForwardUser);
                }

                forwardedNameWidth = getMaxNameWidth();
                if (this.currentMessageObject.isOutOwner()) {
                    Theme.chat_forwardNamePaint.setColor(Theme.chatForwardRColor);
                } else {
                    Theme.chat_forwardNamePaint.setColor(Theme.chatForwardLColor);
                }
                String fromString = LocaleController.getString("From", R.string.From);
                int fromWidth = (int) Math.ceil(Theme.chat_forwardNamePaint.measureText(fromString + " "));
                CharSequence name = TextUtils.ellipsize(currentForwardNameString.replace('\n', ' '), Theme.chat_replyNamePaint, forwardedNameWidth - fromWidth - viaWidth, TextUtils.TruncateAt.END);
                CharSequence lastLine;
                SpannableStringBuilder stringBuilder;
                if (viaString != null) {
                    stringBuilder = new SpannableStringBuilder(String.format("%s %s via %s", fromString, name, viaUsername));
                    viaNameWidth = (int) Math.ceil(Theme.chat_forwardNamePaint.measureText(fromString + " " + name));
                    stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), stringBuilder.length() - viaUsername.length() - 1, stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    stringBuilder = new SpannableStringBuilder(String.format("%s %s", fromString, name));
                }
                stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), fromString.length() + 1, fromString.length() + 1 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                lastLine = stringBuilder;
                lastLine = TextUtils.ellipsize(lastLine, Theme.chat_forwardNamePaint, forwardedNameWidth, TextUtils.TruncateAt.END);
                try {
                    forwardedNameLayout[1] = new StaticLayout(lastLine, Theme.chat_forwardNamePaint, forwardedNameWidth + AndroidUtilities.dp(2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    lastLine = TextUtils.ellipsize(AndroidUtilities.replaceTags(LocaleController.getString("ForwardedMessage", R.string.ForwardedMessage)), Theme.chat_forwardNamePaint, forwardedNameWidth, TextUtils.TruncateAt.END);
                    forwardedNameLayout[0] = new StaticLayout(lastLine, Theme.chat_forwardNamePaint, forwardedNameWidth + AndroidUtilities.dp(2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    forwardedNameWidth = Math.max((int) Math.ceil(forwardedNameLayout[0].getLineWidth(0)), (int) Math.ceil(forwardedNameLayout[1].getLineWidth(0)));
                    forwardNameOffsetX[0] = forwardedNameLayout[0].getLineLeft(0);
                    forwardNameOffsetX[1] = forwardedNameLayout[1].getLineLeft(0);
                    namesOffset += AndroidUtilities.dp(36);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }

        if (messageObject.isReply()) {
            namesOffset += AndroidUtilities.dp(42);
            if (messageObject.type != 0) {
                if (messageObject.type == 13) {
                    namesOffset -= AndroidUtilities.dp(42);
                } else {
                    namesOffset += AndroidUtilities.dp(5);
                }
            }

            int maxWidth = getMaxNameWidth();
            if (messageObject.type != 13) {
                maxWidth -= AndroidUtilities.dp(10);
            }

            CharSequence stringFinalName = null;
            CharSequence stringFinalText = null;
            if (messageObject.replyMessageObject != null) {
                TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs2, 80);
                if (photoSize == null) {
                    photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs, 80);
                }
                if (photoSize == null || messageObject.replyMessageObject.type == 13 || messageObject.type == 13 && !AndroidUtilities.isTablet() || messageObject.replyMessageObject.isSecretMedia()) {
                    replyImageReceiver.setImageBitmap((Drawable) null);
                    needReplyImage = false;
                } else {
                    currentReplyPhoto = photoSize.location;
                    replyImageReceiver.setImage(photoSize.location, "50_50", null, null, true);
                    needReplyImage = true;
                    maxWidth -= AndroidUtilities.dp(44);
                }

                String name = null;
                if (messageObject.customReplyName != null) {
                    name = messageObject.customReplyName;
                } else {
                    if (messageObject.replyMessageObject.isFromUser()) {
                        TLRPC.User user = MessagesController.getInstance().getUser(messageObject.replyMessageObject.messageOwner.from_id);
                        if (user != null) {
                            name = UserObject.getUserName(user);
                        }
                    } else if (messageObject.replyMessageObject.messageOwner.from_id < 0) {
                        TLRPC.Chat chat = MessagesController.getInstance().getChat(-messageObject.replyMessageObject.messageOwner.from_id);
                        if (chat != null) {
                            name = chat.title;
                        }
                    } else {
                        TLRPC.Chat chat = MessagesController.getInstance().getChat(messageObject.replyMessageObject.messageOwner.to_id.channel_id);
                        if (chat != null) {
                            name = chat.title;
                        }
                    }
                }

                if (name != null) {
                    stringFinalName = TextUtils.ellipsize(name.replace('\n', ' '), Theme.chat_replyNamePaint, maxWidth, TextUtils.TruncateAt.END);
                }
                if (messageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                    stringFinalText = Emoji.replaceEmoji(messageObject.replyMessageObject.messageOwner.media.game.title, Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14), false);
                    stringFinalText = TextUtils.ellipsize(stringFinalText, Theme.chat_replyTextPaint, maxWidth, TextUtils.TruncateAt.END);
                } else if (messageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                    stringFinalText = Emoji.replaceEmoji(messageObject.replyMessageObject.messageOwner.media.title, Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14), false);
                    stringFinalText = TextUtils.ellipsize(stringFinalText, Theme.chat_replyTextPaint, maxWidth, TextUtils.TruncateAt.END);
                } else if (messageObject.replyMessageObject.messageText != null && messageObject.replyMessageObject.messageText.length() > 0) {
                    String mess = messageObject.replyMessageObject.messageText.toString();
                    if (mess.length() > 150) {
                        mess = mess.substring(0, 150);
                    }
                    mess = mess.replace('\n', ' ');
                    stringFinalText = Emoji.replaceEmoji(mess, Theme.chat_replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14), false);
                    stringFinalText = TextUtils.ellipsize(stringFinalText, Theme.chat_replyTextPaint, maxWidth, TextUtils.TruncateAt.END);
                }
            }
            if (stringFinalName == null) {
                stringFinalName = LocaleController.getString("Loading", R.string.Loading);
            }
            try {
                replyNameLayout = new StaticLayout(stringFinalName, Theme.chat_replyNamePaint, maxWidth + AndroidUtilities.dp(6), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (replyNameLayout.getLineCount() > 0) {
                    replyNameWidth = (int)Math.ceil(replyNameLayout.getLineWidth(0)) + AndroidUtilities.dp(12 + (needReplyImage ? 44 : 0));
                    replyNameOffset = replyNameLayout.getLineLeft(0);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                if (stringFinalText != null) {
                    replyTextLayout = new StaticLayout(stringFinalText, Theme.chat_replyTextPaint, maxWidth + AndroidUtilities.dp(6), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (replyTextLayout.getLineCount() > 0) {
                        replyTextWidth = (int) Math.ceil(replyTextLayout.getLineWidth(0)) + AndroidUtilities.dp(12 + (needReplyImage ? 44 : 0));
                        replyTextOffset = replyTextLayout.getLineLeft(0);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        requestLayout();
    }

    public ImageReceiver getAvatarImage() {
        return isAvatarVisible ? avatarImage : null;
    }

    public GradientDrawable getStatusBG() {
        return (this.isAvatarVisible && this.drawStatus && !Theme.chatHideStatusIndicator) ? this.statusBG : null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentMessageObject == null) {
            return;
        }

        if (!wasLayout) {
            requestLayout();
            return;
        }
        int dp1,dp2,dp;
        int def;
        int tColor;
        TextPaint textPaint;
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
        if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_msgTextPaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_messageTextOut));
            Theme.chat_msgTextPaint.linkColor = Theme.usePlusTheme ? Theme.chatRLinkColor : Theme.getColor(Theme.key_chat_messageLinkOut);
            Theme.chat_msgGameTextPaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_messageTextOut));
            Theme.chat_msgGameTextPaint.linkColor = Theme.usePlusTheme ? Theme.chatRLinkColor : Theme.getColor(Theme.key_chat_messageLinkOut);
            Theme.chat_replyTextPaint.linkColor = Theme.usePlusTheme ? Theme.chatRLinkColor : Theme.getColor(Theme.key_chat_messageLinkOut);
        } else {
            Theme.chat_msgTextPaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_messageTextIn));
            Theme.chat_msgTextPaint.linkColor = Theme.usePlusTheme ? Theme.chatLLinkColor : Theme.getColor(Theme.key_chat_messageLinkIn);
            Theme.chat_msgGameTextPaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_messageTextIn));
            Theme.chat_msgGameTextPaint.linkColor = Theme.usePlusTheme ? Theme.chatLLinkColor : Theme.getColor(Theme.key_chat_messageLinkIn);
            Theme.chat_replyTextPaint.linkColor = Theme.usePlusTheme ? Theme.chatLLinkColor : Theme.getColor(Theme.key_chat_messageLinkIn);
        }

        if (documentAttach != null) {
            if (documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
                if (currentMessageObject.isOutOwner()) {
                    seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_outVoiceSeekbar), Theme.getColor(Theme.key_chat_outVoiceSeekbarFill), Theme.getColor(Theme.key_chat_outVoiceSeekbarSelected));
                    seekBar.setColors(Theme.getColor(Theme.key_chat_outAudioSeekbar), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarSelected));
                } else {
                    seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_inVoiceSeekbar), Theme.getColor(Theme.key_chat_inVoiceSeekbarFill), Theme.getColor(Theme.key_chat_inVoiceSeekbarSelected));
                    seekBar.setColors(Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarSelected));
                }
            } else if (documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                documentAttachType = DOCUMENT_ATTACH_TYPE_MUSIC;
                if (currentMessageObject.isOutOwner()) {
                    seekBar.setColors(Theme.getColor(Theme.key_chat_outAudioSeekbar), Theme.getColor(Theme.key_chat_outAudioSeekbarFill), Theme.getColor(Theme.key_chat_outAudioSeekbarSelected));
                } else {
                    seekBar.setColors(Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarSelected));
                }
            }
        }
        if (this.currentMessageObject.isOutOwner()) {
            def = Theme.darkColor;
            tColor = Theme.chatRTimeColor;
        } else {
            def = Theme.getColor(Theme.key_chat_inTimeText);
            tColor = themePrefs.getInt(Theme.pkey_chatLTimeColor, def);
        }
        if (mediaBackground) {
            TextPaint textPaint2;
            if (this.currentMessageObject.type == 13) {
                textPaint2 = Theme.chat_timePaint;
                if (!Theme.usePlusTheme) {
                    tColor = Theme.getColor(Theme.key_chat_serviceText);
                } else if (tColor == def) {
                    tColor = Theme.getColor(Theme.key_chat_serviceText);
                }
                textPaint2.setColor(tColor);
            } else {
                textPaint2 = Theme.chat_timePaint;
                if (!Theme.usePlusTheme) {
                    tColor = Theme.getColor(Theme.key_chat_mediaTimeText);
                } else if (tColor == def) {
                    tColor = Theme.getColor(Theme.key_chat_mediaTimeText);
                }
                textPaint2.setColor(tColor);
            }
        } else {
            if (currentMessageObject.isOutOwner()) {
                textPaint = Theme.chat_timePaint;
                if (!Theme.usePlusTheme) {
                    tColor = Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText);
                }
                textPaint.setColor(tColor);
            } else {
                textPaint = Theme.chat_timePaint;
                if (!Theme.usePlusTheme) {
                    tColor = Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText);
                }
                textPaint.setColor(tColor);
            }
        }

        Drawable currentBackgroundShadowDrawable;
        if (currentMessageObject.isOutOwner()) {
            if (isDrawSelectedBackground()) {
                if (!mediaBackground && !pinnedBottom) {
                    currentBackgroundDrawable = Theme.chat_msgOutSelectedDrawable;
                    currentBackgroundShadowDrawable = Theme.chat_msgOutShadowDrawable;
                } else {
                    currentBackgroundDrawable = Theme.chat_msgOutMediaSelectedDrawable;
                    currentBackgroundShadowDrawable = Theme.chat_msgOutMediaShadowDrawable;
                }
            } else {
                if (!mediaBackground && !pinnedBottom) {
                    currentBackgroundDrawable = Theme.chat_msgOutDrawable;
                    currentBackgroundShadowDrawable = Theme.chat_msgOutShadowDrawable;
                } else {
                    currentBackgroundDrawable = Theme.chat_msgOutMediaDrawable;
                    currentBackgroundShadowDrawable = Theme.chat_msgOutMediaShadowDrawable;
                }
            }
            dp = (this.layoutWidth - this.backgroundWidth) - (!this.mediaBackground ? 0 : AndroidUtilities.dp(9.0f));
            dp2 = ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) ? 0 : AndroidUtilities.dp((float) this.leftBound);
            this.backgroundDrawableLeft = dp - dp2;
//            backgroundDrawableLeft = layoutWidth - backgroundWidth - (!mediaBackground ? 0 : AndroidUtilities.dp(9));
            int backgroundRight = backgroundWidth - (mediaBackground ? 0 : AndroidUtilities.dp(3));
            int backgroundLeft = backgroundDrawableLeft;
            if (!mediaBackground && pinnedBottom) {
                backgroundRight -= AndroidUtilities.dp(6);
            }
            int offsetBottom;
            if (pinnedBottom && pinnedTop) {
                offsetBottom = 0;
            } else if (pinnedBottom) {
                offsetBottom = AndroidUtilities.dp(1);
            } else {
                offsetBottom = AndroidUtilities.dp(2);
            }
            setDrawableBounds(currentBackgroundDrawable, backgroundLeft, pinnedTop || pinnedTop && pinnedBottom ? 0 : AndroidUtilities.dp(1), backgroundRight, layoutHeight - offsetBottom);
            setDrawableBounds(currentBackgroundShadowDrawable, backgroundLeft, pinnedTop || pinnedTop && pinnedBottom ? 0 : AndroidUtilities.dp(1), backgroundRight, layoutHeight - offsetBottom);
        } else {
            if (isDrawSelectedBackground()) {
                if (!mediaBackground && !pinnedBottom) {
                    currentBackgroundDrawable = Theme.chat_msgInSelectedDrawable;
                    currentBackgroundShadowDrawable = Theme.chat_msgInShadowDrawable;
                } else {
                    currentBackgroundDrawable = Theme.chat_msgInMediaSelectedDrawable;
                    currentBackgroundShadowDrawable = Theme.chat_msgInMediaShadowDrawable;
                }
            } else {
                if (!mediaBackground && !pinnedBottom) {
                    currentBackgroundDrawable = Theme.chat_msgInDrawable;
                    currentBackgroundShadowDrawable = Theme.chat_msgInShadowDrawable;
                } else {
                    currentBackgroundDrawable = Theme.chat_msgInMediaDrawable;
                    currentBackgroundShadowDrawable = Theme.chat_msgInMediaShadowDrawable;
                }
            }
//            backgroundDrawableLeft = AndroidUtilities.dp((isChat && currentMessageObject.isFromUser() ? 48 : 0) + (!mediaBackground ? 3 : 9));
            dp2 = ((this.isChat || this.showAvatar) && this.currentMessageObject.isFromUser()) ? this.leftBound : 0;
            this.backgroundDrawableLeft = AndroidUtilities.dp((float) (dp2 + (!this.mediaBackground ? 3 : 9)));

            int backgroundRight = backgroundWidth - (mediaBackground ? 0 : AndroidUtilities.dp(3));
            int backgroundLeft = backgroundDrawableLeft;
            if (!mediaBackground && pinnedBottom) {
                backgroundRight -= AndroidUtilities.dp(6);
                backgroundLeft += AndroidUtilities.dp(6);
            }
            int offsetBottom;
            if (pinnedBottom && pinnedTop) {
                offsetBottom = 0;
            } else if (pinnedBottom) {
                offsetBottom = AndroidUtilities.dp(1);
            } else {
                offsetBottom = AndroidUtilities.dp(2);
            }
            setDrawableBounds(currentBackgroundDrawable, backgroundLeft, pinnedTop || pinnedTop && pinnedBottom ? 0 : AndroidUtilities.dp(1), backgroundRight, layoutHeight - offsetBottom);
            setDrawableBounds(currentBackgroundShadowDrawable, backgroundLeft, pinnedTop || pinnedTop && pinnedBottom ? 0 : AndroidUtilities.dp(1), backgroundRight, layoutHeight - offsetBottom);
        }
        if (drawBackground && currentBackgroundDrawable != null) {
            currentBackgroundDrawable.draw(canvas);
            currentBackgroundShadowDrawable.draw(canvas);
        }

        drawContent(canvas);

        if (drawShareButton) {
            Theme.chat_shareDrawable.setColorFilter(sharePressed ? Theme.colorPressedFilter : Theme.colorFilter);
            if (currentMessageObject.isOutOwner()) {
                shareStartX = currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(2) - Theme.chat_shareDrawable.getIntrinsicWidth();
            } else {
                shareStartX = currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(2);
            }
            setDrawableBounds(Theme.chat_shareDrawable, shareStartX, shareStartY = layoutHeight - AndroidUtilities.dp(41));
            Theme.chat_shareDrawable.draw(canvas);
            setDrawableBounds(Theme.chat_shareIconDrawable, shareStartX + AndroidUtilities.dp(9), shareStartY + AndroidUtilities.dp(9));
            Theme.chat_shareIconDrawable.draw(canvas);
        }

        if (drawNameLayout && nameLayout != null) {
            canvas.save();

            if (currentMessageObject.type == 13) {
                Theme.chat_namePaint.setColor(Theme.getColor(Theme.key_chat_stickerNameText));
                int backWidth;
                if (currentMessageObject.isOutOwner()) {
                    nameX = AndroidUtilities.dp(28);
                } else {
                    nameX = currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(22);
                }
                nameY = layoutHeight - AndroidUtilities.dp(38);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds((int) nameX - AndroidUtilities.dp(12), (int) nameY - AndroidUtilities.dp(5), (int) nameX + AndroidUtilities.dp(12) + nameWidth, (int) nameY + AndroidUtilities.dp(22));
                Theme.chat_systemDrawable.draw(canvas);
            } else {
                if (mediaBackground || currentMessageObject.isOutOwner()) {
                    nameX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11) - nameOffsetX;
                } else {
                    nameX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(!mediaBackground && pinnedBottom ? 11 : 17) - nameOffsetX;
                }
                if (currentUser != null) {
                    textPaint = Theme.chat_namePaint;
                    dp2 = (Theme.usePlusTheme && Theme.chatMemberColorCheck) ? Theme.chatMemberColor : AvatarDrawable.getNameColorForId(this.currentUser.id);
                    textPaint.setColor(dp2);
//                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(currentUser.id));
                } else if (currentChat != null) {
                    textPaint = Theme.chat_namePaint;
                    dp2 = (Theme.usePlusTheme && Theme.chatMemberColorCheck) ? Theme.chatMemberColor : AvatarDrawable.getNameColorForId(this.currentChat.id);
                    textPaint.setColor(dp2);
//                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(currentChat.id));
                } else {
                    textPaint = Theme.chat_namePaint;
                    dp2 = (Theme.usePlusTheme && Theme.chatMemberColorCheck) ? Theme.chatMemberColor : AvatarDrawable.getNameColorForId(0);
                    textPaint.setColor(dp2);
//                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
                }
                nameY = AndroidUtilities.dp(pinnedTop ? 9 : 10);
            }
            canvas.translate(nameX, nameY);
            nameLayout.draw(canvas);
            canvas.restore();
        }

        if (drawForwardedName && forwardedNameLayout[0] != null && forwardedNameLayout[1] != null) {
            forwardNameY = AndroidUtilities.dp(10 + (drawNameLayout ? 19 : 0));
            if (currentMessageObject.isOutOwner()) {
//                Theme.chat_forwardNamePaint.setColor(Theme.getColor(Theme.key_chat_outForwardedNameText));
                Theme.chat_forwardNamePaint.setColor(Theme.usePlusTheme ? Theme.chatForwardRColor : Theme.getColor(Theme.key_chat_outForwardedNameText));
                forwardNameX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11);
            } else {
                Theme.chat_forwardNamePaint.setColor(Theme.usePlusTheme ? Theme.chatForwardLColor : Theme.getColor(Theme.key_chat_inForwardedNameText));                if (mediaBackground) {
                    forwardNameX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11);
                } else {
                    forwardNameX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(!mediaBackground && pinnedBottom ? 11 : 17);
                }
                if (!this.isChat && this.showAvatar) {
                    this.forwardNameY = AndroidUtilities.dp(11.0f);
                }
            }
            for (int a = 0; a < 2; a++) {
                canvas.save();
                canvas.translate(forwardNameX - forwardNameOffsetX[a], forwardNameY + AndroidUtilities.dp(16) * a);
                forwardedNameLayout[a].draw(canvas);
                canvas.restore();
            }
        }

        if (currentMessageObject.isReply()) {
            if (currentMessageObject.type == 13) {
                Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyLine));
                Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyNameText));
                Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_stickerReplyMessageText));
                if (currentMessageObject.isOutOwner()) {
                    if (Theme.usePlusTheme) {
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_outReplyLine));
                        Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_outReplyNameText));
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_outReplyMessageText));
                    }
                    replyStartX = AndroidUtilities.dp(23);
                } else {
                    if (Theme.usePlusTheme) {
                        Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_inReplyLine));
                        Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_inReplyNameText));
                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_inReplyMessageText));
                    }
                    replyStartX = currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(17);
                }
                replyStartY = layoutHeight - AndroidUtilities.dp(58);
                if (nameLayout != null) {
                    replyStartY -= AndroidUtilities.dp(25 + 6);
                }
                int backWidth = Math.max(replyNameWidth, replyTextWidth) + AndroidUtilities.dp(14 + (needReplyImage ? 44 : 0));
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(replyStartX - AndroidUtilities.dp(7), replyStartY - AndroidUtilities.dp(6), replyStartX - AndroidUtilities.dp(7) + backWidth, replyStartY + AndroidUtilities.dp(41));
                Theme.chat_systemDrawable.draw(canvas);
            } else {
                if (currentMessageObject.isOutOwner()) {
//                    Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_outReplyLine));
//                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_outReplyNameText));
                    Theme.chat_replyLinePaint.setColor(Theme.usePlusTheme ? Theme.chatForwardRColor : Theme.getColor(Theme.key_chat_outReplyLine));
                    Theme.chat_replyNamePaint.setColor(Theme.usePlusTheme ? Theme.chatForwardRColor : Theme.getColor(Theme.key_chat_outReplyNameText));

                    if (currentMessageObject.replyMessageObject != null && currentMessageObject.replyMessageObject.type == 0 && !(currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame || currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
//                        Theme.chat_replyTextPaint.setColor(Theme.getColor(Theme.key_chat_outReplyMessageText));
                        textPaint = Theme.chat_replyTextPaint;
                        if (Theme.usePlusTheme) {
                            dp2 = Theme.chatForwardRColor;
                        } else {
                            dp2 = Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outReplyMediaMessageSelectedText : Theme.key_chat_outReplyMediaMessageText);
                        }
                        textPaint.setColor(dp2);
                    } else {
//                        Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_outReplyMediaMessageSelectedText : Theme.key_chat_outReplyMediaMessageText));
                        Theme.chat_replyTextPaint.setColor(Theme.usePlusTheme ? Theme.chatRTextColor : Theme.getColor(Theme.key_chat_outReplyMessageText));
                    }
                    replyStartX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12);
                } else {
//                    Theme.chat_replyLinePaint.setColor(Theme.getColor(Theme.key_chat_inReplyLine));
//                    Theme.chat_replyNamePaint.setColor(Theme.getColor(Theme.key_chat_inReplyNameText));
                    Theme.chat_replyLinePaint.setColor(Theme.usePlusTheme ? Theme.chatForwardLColor : Theme.getColor(Theme.key_chat_inReplyLine));
                    Theme.chat_replyNamePaint.setColor(Theme.usePlusTheme ? Theme.chatForwardLColor : Theme.getColor(Theme.key_chat_inReplyNameText));

                    if (currentMessageObject.replyMessageObject != null && currentMessageObject.replyMessageObject.type == 0 && !(currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame || currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
                        textPaint = Theme.chat_replyTextPaint;
                        if (Theme.usePlusTheme) {
                            dp2 = Theme.chatForwardLColor;
                        } else {
                            dp2 = Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inReplyMediaMessageSelectedText : Theme.key_chat_inReplyMediaMessageText);
                        }
                        textPaint.setColor(dp2);
                    } else {
//                        Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectedBackground() ? Theme.key_chat_inReplyMediaMessageSelectedText : Theme.key_chat_inReplyMediaMessageText));
                        Theme.chat_replyTextPaint.setColor(Theme.usePlusTheme ? Theme.chatLTextColor : Theme.getColor(Theme.key_chat_inReplyMessageText));
                    }
                    if (mediaBackground) {
                        replyStartX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12);
                    } else {
                        replyStartX = currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(!mediaBackground && pinnedBottom ? 12 : 18);
                    }
                }
                replyStartY = AndroidUtilities.dp(12 + (drawForwardedName && forwardedNameLayout[0] != null ? 36 : 0) + (drawNameLayout && nameLayout != null ? 20 : 0));
            }
            canvas.drawRect(replyStartX, replyStartY, replyStartX + AndroidUtilities.dp(2), replyStartY + AndroidUtilities.dp(35), Theme.chat_replyLinePaint);
            if (needReplyImage) {
                replyImageReceiver.setImageCoords(replyStartX + AndroidUtilities.dp(10), replyStartY, AndroidUtilities.dp(35), AndroidUtilities.dp(35));
                replyImageReceiver.draw(canvas);
            }

            if (replyNameLayout != null) {
                canvas.save();
                canvas.translate(replyStartX - replyNameOffset + AndroidUtilities.dp(10 + (needReplyImage ? 44 : 0)), replyStartY);
                replyNameLayout.draw(canvas);
                canvas.restore();
            }
            if (replyTextLayout != null) {
                canvas.save();
                canvas.translate(replyStartX - replyTextOffset + AndroidUtilities.dp(10 + (needReplyImage ? 44 : 0)), replyStartY + AndroidUtilities.dp(19));
                replyTextLayout.draw(canvas);
                canvas.restore();
            }
        }

        if ((drawTime || !mediaBackground) && !forceNotDrawTime) {
            if (pinnedBottom) {
                canvas.translate(0, AndroidUtilities.dp(2));
            }
            if (mediaBackground) {
                Drawable drawable;
                if (currentMessageObject.type == 13) {
                    drawable = Theme.chat_timeStickerBackgroundDrawable;
                } else {
                    drawable = Theme.chat_timeBackgroundDrawable;
                }
                setDrawableBounds(drawable, timeX - AndroidUtilities.dp(4), layoutHeight - AndroidUtilities.dp(27), timeWidth + AndroidUtilities.dp(8 + (currentMessageObject.isOutOwner() ? 20 : 0)), AndroidUtilities.dp(17));
                drawable.draw(canvas);

                int additionalX = 0;
                if ((currentMessageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                    additionalX = (int) (timeWidth - timeLayout.getLineWidth(0));

                    if (currentMessageObject.isSending()) {
                        if (!currentMessageObject.isOutOwner()) {
                            setDrawableBounds(Theme.chat_msgMediaClockDrawable, timeX + AndroidUtilities.dp(11), layoutHeight - AndroidUtilities.dp(13.0f) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                            Theme.chat_msgMediaClockDrawable.draw(canvas);
                        }
                    } else if (currentMessageObject.isSendError()) {
                        if (!currentMessageObject.isOutOwner()) {
                            int x = timeX + AndroidUtilities.dp(11);
                            int y = layoutHeight - AndroidUtilities.dp(26.5f);
                            rect.set(x, y, x + AndroidUtilities.dp(14), y + AndroidUtilities.dp(14));
                            canvas.drawRoundRect(rect, AndroidUtilities.dp(1), AndroidUtilities.dp(1), Theme.chat_msgErrorPaint);
                            setDrawableBounds(Theme.chat_msgErrorDrawable, x + AndroidUtilities.dp(6), y + AndroidUtilities.dp(2));
                            Theme.chat_msgErrorDrawable.draw(canvas);
                        }
                    } else {
                        Drawable viewsDrawable;
                        if (currentMessageObject.type == 13) {
                            viewsDrawable = Theme.chat_msgStickerViewsDrawable;
                        } else {
                            viewsDrawable = Theme.chat_msgMediaViewsDrawable;
                        }
                        setDrawableBounds(viewsDrawable, timeX, layoutHeight - AndroidUtilities.dp(9.5f) - timeLayout.getHeight());
                        viewsDrawable.draw(canvas);

                        if (viewsLayout != null) {
                            canvas.save();
                            canvas.translate(timeX + viewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3), layoutHeight - AndroidUtilities.dp(11.3f) - timeLayout.getHeight());
                            viewsLayout.draw(canvas);
                            canvas.restore();
                        }
                    }
                }

                canvas.save();
                canvas.translate(timeX + additionalX, layoutHeight - AndroidUtilities.dp(11.3f) - timeLayout.getHeight());
                timeLayout.draw(canvas);
                canvas.restore();
            } else {
                int additionalX = 0;
                if ((currentMessageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                    additionalX = (int) (timeWidth - timeLayout.getLineWidth(0));

                    if (currentMessageObject.isSending()) {
                        if (!currentMessageObject.isOutOwner()) {
                            Drawable clockDrawable = isDrawSelectedBackground() ? Theme.chat_msgInSelectedClockDrawable : Theme.chat_msgInClockDrawable;
                            setDrawableBounds(clockDrawable, timeX + AndroidUtilities.dp(11), layoutHeight - AndroidUtilities.dp(8.5f) - clockDrawable.getIntrinsicHeight());
                            clockDrawable.draw(canvas);
                        }
                    } else if (currentMessageObject.isSendError()) {
                        if (!currentMessageObject.isOutOwner()) {
                            int x = timeX + AndroidUtilities.dp(11);
                            int y = layoutHeight - AndroidUtilities.dp(20.5f);
                            rect.set(x, y, x + AndroidUtilities.dp(14), y + AndroidUtilities.dp(14));
                            canvas.drawRoundRect(rect, AndroidUtilities.dp(1), AndroidUtilities.dp(1), Theme.chat_msgErrorPaint);
                            setDrawableBounds(Theme.chat_msgErrorDrawable, x + AndroidUtilities.dp(6), y + AndroidUtilities.dp(2));
                            Theme.chat_msgErrorDrawable.draw(canvas);
                        }
                    } else {
                        if (!currentMessageObject.isOutOwner()) {
                            Drawable viewsDrawable = isDrawSelectedBackground() ? Theme.chat_msgInViewsSelectedDrawable : Theme.chat_msgInViewsDrawable;
                            setDrawableBounds(viewsDrawable, timeX, layoutHeight - AndroidUtilities.dp(4.5f) - timeLayout.getHeight());
                            viewsDrawable.draw(canvas);
                        } else {
                            Drawable viewsDrawable = isDrawSelectedBackground() ? Theme.chat_msgOutViewsSelectedDrawable : Theme.chat_msgOutViewsDrawable;
                            setDrawableBounds(viewsDrawable, timeX, layoutHeight - AndroidUtilities.dp(4.5f) - timeLayout.getHeight());
                            viewsDrawable.draw(canvas);
                        }

                        if (viewsLayout != null) {
                            canvas.save();
                            canvas.translate(timeX + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3), layoutHeight - AndroidUtilities.dp(6.5f) - timeLayout.getHeight());
                            viewsLayout.draw(canvas);
                            canvas.restore();
                        }
                    }
                }

                canvas.save();
                canvas.translate(timeX + additionalX, layoutHeight - AndroidUtilities.dp(6.5f) - timeLayout.getHeight());
                timeLayout.draw(canvas);
                canvas.restore();
            }

            if (currentMessageObject.isOutOwner()) {
                boolean drawCheck1 = false;
                boolean drawCheck2 = false;
                boolean drawClock = false;
                boolean drawError = false;
                boolean isBroadcast = (int)(currentMessageObject.getDialogId() >> 32) == 1;

                if (currentMessageObject.isSending()) {
                    drawCheck1 = false;
                    drawCheck2 = false;
                    drawClock = true;
                    drawError = false;
                } else if (currentMessageObject.isSendError()) {
                    drawCheck1 = false;
                    drawCheck2 = false;
                    drawClock = false;
                    drawError = true;
                } else if (currentMessageObject.isSent()) {
                    if (!currentMessageObject.isUnread()) {
                        drawCheck1 = true;
                        drawCheck2 = true;
                    } else {
                        drawCheck1 = false;
                        drawCheck2 = true;
                    }
                    drawClock = false;
                    drawError = false;
                }

                if (drawClock) {
                    if (!this.mediaBackground) {
                        if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                            setDrawableBounds(Theme.chat_msgOutClockDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicHeight());
                        } else {
                            setDrawableBounds(Theme.chat_msgOutClockDrawable, (this.checkX - AndroidUtilities.dp(3.5f)) + Theme.chat_msgOutClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - Theme.chat_msgOutClockDrawable.getIntrinsicHeight());
                        }
                        Theme.chat_msgOutClockDrawable.draw(canvas);
                    } else if (this.currentMessageObject.type == 13) {
                        if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                            setDrawableBounds(Theme.chat_msgStickerClockDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
                        } else {
                            setDrawableBounds(Theme.chat_msgStickerClockDrawable, (this.checkX - AndroidUtilities.dp(7.0f)) + Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
                        }
                        Theme.chat_msgStickerClockDrawable.draw(canvas);
                    } else {
                        if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                            setDrawableBounds(Theme.chat_msgMediaClockDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                        } else {
                            setDrawableBounds(Theme.chat_msgMediaClockDrawable, (this.checkX - AndroidUtilities.dp(7.0f)) + Theme.chat_msgMediaClockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                        }
                        Theme.chat_msgMediaClockDrawable.draw(canvas);
                    }
                }

                if (!isBroadcast) {
                    if (drawCheck2) {
                        if (!this.mediaBackground) {
                            Drawable drawable3 = isDrawSelectedBackground() ? Theme.chat_msgOutCheckSelectedDrawable : Theme.chat_msgOutCheckDrawable;
                            if (drawCheck1) {
                                if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                    setDrawableBounds(drawable3, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - drawable3.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable3.getIntrinsicHeight());
                                } else {
                                    setDrawableBounds(drawable3, (this.checkX - AndroidUtilities.dp(7.5f)) + drawable3.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable3.getIntrinsicHeight());
                                }
                            } else if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                setDrawableBounds(drawable3, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - drawable3.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable3.getIntrinsicHeight());
                            } else {
                                setDrawableBounds(drawable3, (this.checkX - AndroidUtilities.dp(3.5f)) + drawable3.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable3.getIntrinsicHeight());
                            }
                            drawable3.draw(canvas);
                        } else if (this.currentMessageObject.type == 13) {
                            if (drawCheck1) {
                                if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                    setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                                } else {
                                    setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.checkX - AndroidUtilities.dp(8.0f)) + Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                                }
                            } else if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            } else {
                                setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.checkX - AndroidUtilities.dp(7.0f)) + Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgStickerCheckDrawable.draw(canvas);
                        } else {
                            if (drawCheck1) {
                                if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                    setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                                } else {
                                    setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.checkX - AndroidUtilities.dp(8.0f)) + Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                                }
                            } else if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            } else {
                                setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.checkX - AndroidUtilities.dp(7.0f)) + Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgMediaCheckDrawable.draw(canvas);
                        }
                    }
                    if (drawCheck1) {
                        if (!this.mediaBackground) {
                            Drawable drawable3 = isDrawSelectedBackground() ? Theme.chat_msgOutHalfCheckSelectedDrawable : Theme.chat_msgOutHalfCheckDrawable;
                            if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                setDrawableBounds(drawable3, (this.layoutWidth - AndroidUtilities.dp(18.0f)) - drawable3.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable3.getIntrinsicHeight());
                            } else {
                                setDrawableBounds(drawable3, (this.checkX - AndroidUtilities.dp(3.0f)) + drawable3.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable3.getIntrinsicHeight());
                            }
                            drawable3.draw(canvas);
                        } else if (this.currentMessageObject.type == 13) {
                            if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                setDrawableBounds(Theme.chat_msgStickerHalfCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicHeight());
                            } else {
                                setDrawableBounds(Theme.chat_msgStickerHalfCheckDrawable, (this.checkX - AndroidUtilities.dp(3.0f)) + Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgStickerHalfCheckDrawable.draw(canvas);
                        } else {
                            if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                                setDrawableBounds(Theme.chat_msgMediaHalfCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicHeight());
                            } else {
                                setDrawableBounds(Theme.chat_msgMediaHalfCheckDrawable, (this.checkX - AndroidUtilities.dp(3.0f)) + Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgMediaHalfCheckDrawable.draw(canvas);
                        }
                    }
                } else if (drawCheck1 || drawCheck2) {
                    if (this.mediaBackground) {
                        if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                            setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(24.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
                        } else {
                            setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, (this.checkX - AndroidUtilities.dp(9.0f)) + Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
                        }
                        Theme.chat_msgBroadcastMediaDrawable.draw(canvas);
                    } else {
                        if ((!this.showMyAvatar || this.isChat) && !(this.showMyAvatarGroup && this.isChat)) {
                            setDrawableBounds(Theme.chat_msgBroadcastDrawable, (this.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
                        } else {
                            setDrawableBounds(Theme.chat_msgBroadcastDrawable, (this.checkX - AndroidUtilities.dp(5.5f)) + Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
                        }
                        Theme.chat_msgBroadcastDrawable.draw(canvas);
                    }
                }
                if (drawError) {
                    int x;
                    int y;
                    if (!mediaBackground) {
                        x = layoutWidth - AndroidUtilities.dp(32);
                        y = layoutHeight - AndroidUtilities.dp(21);
                    } else {
                        x = layoutWidth - AndroidUtilities.dp(34.5f);
                        y = layoutHeight - AndroidUtilities.dp(25.5f);
                    }
                    rect.set(x, y, x + AndroidUtilities.dp(14), y + AndroidUtilities.dp(14));
                    canvas.drawRoundRect(rect, AndroidUtilities.dp(1), AndroidUtilities.dp(1), Theme.chat_msgErrorPaint);
                    setDrawableBounds(Theme.chat_msgErrorDrawable, x + AndroidUtilities.dp(6), y + AndroidUtilities.dp(2));
                    Theme.chat_msgErrorDrawable.draw(canvas);
                }
            }
        }
    }

    @Override
    public int getObserverTag() {
        return TAG;
    }

    public MessageObject getMessageObject() {
        return currentMessageObject;
    }

    public boolean isPinnedBottom() {
        return pinnedBottom;
    }

    public boolean isPinnedTop() {
        return pinnedTop;
    }

    public int getLayoutHeight() {
        return layoutHeight;
    }
}
