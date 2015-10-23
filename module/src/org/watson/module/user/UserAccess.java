package org.watson.module.user;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public enum UserAccess {
    ANYONE, FULL_USER;

    public static UserAccess getByOrdinal(int ordinal) {
        for(UserAccess ua : UserAccess.values()) {
            if(ua.ordinal() == ordinal) {
                return ua;
            }
        }
        return null;
    }
}
