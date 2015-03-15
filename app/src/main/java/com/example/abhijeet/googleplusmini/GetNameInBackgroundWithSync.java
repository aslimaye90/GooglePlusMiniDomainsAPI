package com.example.abhijeet.googleplusmini;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;

import java.io.IOException;

/**
 * This example shows how to fetch tokens if you are creating a sync adapter.
 */
public class GetNameInBackgroundWithSync extends AbstractGetNameTask {

    public static final String CONTACTS_AUTHORITY = "com.android.contacts";

    public GetNameInBackgroundWithSync(
            HelloActivity activity, String email, String scope) {
        super(activity, email, scope);
    }

    @Override
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getTokenWithNotification(
                    mActivity, mEmail, mScope, null, CONTACTS_AUTHORITY, null);
        } catch (UserRecoverableNotifiedException userRecoverableException) {
            // Unable to authenticate, but the user can fix this.
            // Because we've used getTokenWithNotification(), a Notification is
            // created automatically so the user can recover from the error
            onError("Could not fetch token.", null);
        } catch (GoogleAuthException fatalException) {
            onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
        }
        return null;
    }
}
