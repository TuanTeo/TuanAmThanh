package com.dev.tuanteo.tuanamthanh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dev.tuanteo.tuanamthanh.units.Constant;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent customIntent = new Intent(Constant.NOTIFICATION_ACTION);
        customIntent.putExtra(Constant.NOTIFICATION_ACTION_NAME, intent.getAction());
        context.sendBroadcast(customIntent);
    }
}
