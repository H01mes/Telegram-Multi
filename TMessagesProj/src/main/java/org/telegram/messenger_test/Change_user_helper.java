package org.telegram.messenger_test;

/**
 * Created by oleg.svs on 21.05.2017.
 */

public class Change_user_helper {
    private static String userTag = "_user_0";

    static public void setUserTag(String tag) {
        if(tag != null)
            userTag = tag;
    }

    static public String getUserTag() {
        return userTag;
    }
}
