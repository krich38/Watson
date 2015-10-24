package org.watson.module.user;

/**
 * @author Kyle Richards
 * @version 1.0
 *          <p>
 *          User Access control of Watson.
 *          ANYONE = Anyone who requires no control
 *          FULL_USER = Ability to control Watson
 */
public enum UserAccess {
    ANYONE, FULL_USER;

    public static UserAccess getByOrdinal(int ordinal) {
        for (UserAccess ua : UserAccess.values()) {
            if (ua.ordinal() == ordinal) {
                return ua;
            }
        }
        return null;
    }
}
