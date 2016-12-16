package morocco.mycom.myrecorder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import morocco.mycom.myrecorder.MainActivity;
import morocco.mycom.myrecorder.helper.SecurePreferences;

public class BootReciever extends BroadcastReceiver
{

	private static final int RECORDING_NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
//		Intent myIntent = new Intent(context, MainActivity.class);
//		myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(myIntent);

		if (SecurePreferences.getBooleanPreference(context,
				Constants.PREF_RECORD_CALLS)) {

			updateNotification(context, true);
		} else {

			updateNotification(context, false);
		}
	}
	private void updateNotification(Context context, Boolean status) {


		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

		if (status) {
			int icon = R.drawable.ic_notification_true;
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
					.setContentTitle("Enable")
					.setAutoCancel(false)
					.setContentIntent(pendingIntent)
					.setSmallIcon(icon);
			mNotificationManager
					.notify(RECORDING_NOTIFICATION_ID, notification.build());


		} else {
			int icon = R.drawable.ic_notification_false;
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
					.setContentTitle("Disable")
					.setAutoCancel(false)
					.setContentIntent(pendingIntent)
					.setSmallIcon(icon);
			mNotificationManager
					.notify(RECORDING_NOTIFICATION_ID, notification.build());
		}
	}

}