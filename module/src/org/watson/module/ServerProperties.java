package org.watson.module;

import org.watson.module.user.UserAccess;

import java.util.Map;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public class ServerProperties {
    public String file;
    public String ip;
    public String nick;
    public String pass;
    public Map<String, UserAccess> users;
    public int port;
    public String altnick;
    public String ident;
    public String realname;
    public String[] channels;
    private boolean doReconnect;

    public String getNickname() {
        return nick;
    }

    public String getIdent() {
        return ident;
    }

    public String getRealname() {
        return realname;
    }

    public String getPassword() {
        return pass;
    }

    public void setDoReconnect(boolean doReconnect) {
        this.doReconnect = doReconnect;
    }

    public String[] getHomeChannels() {
        return channels;
    }

    public boolean isReconnect() {
        return doReconnect;
    }

    public Map<String, UserAccess> getUsers() {
        return users;
    }
}
