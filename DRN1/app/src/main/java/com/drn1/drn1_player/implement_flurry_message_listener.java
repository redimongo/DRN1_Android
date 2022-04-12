package com.drn1.drn1_player;

import android.content.Context;
import android.util.Log;

import com.flurry.android.marketing.messaging.FlurryMessaging;
import com.flurry.android.marketing.messaging.FlurryMessagingListener;
import com.flurry.android.marketing.messaging.notification.FlurryMessage;
import com.google.firebase.messaging.RemoteMessage;

class MyFlurryMessagingListener implements FlurryMessagingListener {

    final static String LOG_TAG = MyFlurryMessagingListener.class.getCanonicalName();


    Context context;

    public MyFlurryMessagingListener(Context context) {

        this.context = context;

    }

    @Override

    public boolean onNotificationReceived(FlurryMessage flurryMessage) {

        // determine if you'd like to handle the received notification yourself or not

        boolean handled = false;

        // flurry will not show notification if app is in foreground, so handle it appropriately

        if (FlurryMessaging.isAppInForeground()) {

            // handle the notification using data from FlurryMessage

            // NOTE: since you are handling the notification, be sure to call logNotificationOpened and logNotificationCancelled after this

            handled = true;

        }

        return handled;

    }

    @Override

    public boolean onNotificationClicked(FlurryMessage flurryMessage) {

        // NOTE: THIS WILL ONLY BE CALLED IF FLURRY HANDLED onNotificationReceived callback

        // determine if you'd like to handle the clicked notification yourself or not

        boolean handled = false;

       /* if (youWantToHandleUiNavigation) {

            // handle

            handled = true;

        }*/

        return handled;

    }

    @Override

    public void onNotificationCancelled(FlurryMessage flurryMessage) {

        Log.d(LOG_TAG, "Notification cancelled!");

    }

    @Override

    public void onTokenRefresh(String refreshedToken) {

        Log.d(LOG_TAG, "Token refreshed - " + refreshedToken);

    }

    @Override

    public void onNonFlurryNotificationReceived(Object nonFlurryMessage) {

        // If Flurry receives a non-Flurry message, it will be passed to you here. You can cast the object

        // based on the push provider. For example...

        if (nonFlurryMessage instanceof RemoteMessage) {

            RemoteMessage firebaseMessage = (RemoteMessage) nonFlurryMessage;

            Log.d(LOG_TAG, "A non-flurry message was received from firebase.");

        }

    }

}