/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 15.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab51;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

/**
 * Widget class
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String INTENT_SWITCH_WIFI = "com.ruswizards.rwlab51.wifi.switch";

    /**
     * Calls {@link #onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[])}
     * if received intent about wifi connection changed. Switches wifi on/off if received intent to
     * do this.
     * @param context context
     * @param intent intent
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION) ||
                intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION) ||
                intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widgetComponentName = new ComponentName(context, this.getClass());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponentName);
            this.onUpdate(context, appWidgetManager, appWidgetIds);
        } else if (intent.getAction().equals(INTENT_SWITCH_WIFI)){
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews remoteViewsWidget = new RemoteViews(
                context.getPackageName(), R.layout.widget_layout);

        // Set up pending intent for sending it when wifi icon in widget clicked
        Intent intentActionSwitch = new Intent(context, WidgetProvider.class);
        intentActionSwitch.setAction(INTENT_SWITCH_WIFI);
        PendingIntent pendingActionSwitch = PendingIntent.getBroadcast(
                context, 0, intentActionSwitch, 0);
        remoteViewsWidget.setOnClickPendingIntent(R.id.image_view_wifi_status, pendingActionSwitch);

        // Change image resource and update widget instances
        changeImageView(remoteViewsWidget, context);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViewsWidget);
    }

    /**
     * Checks wi-fi state and changes image resource in a widget (do not update widget instances)
     * @param remoteViewsWidget remoteViewsWidget
     * @param context context
     */
    private void changeImageView(RemoteViews remoteViewsWidget, Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()){
            remoteViewsWidget.setImageViewResource(R.id.image_view_wifi_status, R.drawable.wifi_off);
        } else if (wifiManager.getConnectionInfo().getSupplicantState().equals(
                SupplicantState.COMPLETED)){
            remoteViewsWidget.setImageViewResource(R.id.image_view_wifi_status, R.drawable.wifi_connected);
        } else {
            remoteViewsWidget.setImageViewResource(R.id.image_view_wifi_status, R.drawable.wifi_connecting);
        }
    }
}
