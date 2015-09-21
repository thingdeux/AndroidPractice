package josh.land.android.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by josh on 9/20/15.
 */
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = StartupReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        boolean isOn = PollService.isServiceAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);
        Log.i(TAG, "Received broadcast intent for PhotoGallery: " + intent.getAction() + ", set Alarm to " + isOn);
    }
}
