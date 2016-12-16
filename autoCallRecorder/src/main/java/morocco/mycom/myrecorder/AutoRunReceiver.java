package morocco.mycom.myrecorder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

public class AutoRunReceiver extends BroadcastReceiver {
	String ACTION_SHOW_ALERT_DIALOG = "morocco.mycom.myrecorder.SHOW_ALERT_DIALOG";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == intent)
			return;
		Intent i = new Intent(CallRecorderService.ACTION);
		if (intent.getAction().equals(ACTION_SHOW_ALERT_DIALOG)) {
			Intent intentDialog = new Intent(context, DialogClass.class);
			String absolutepath = intent.getStringExtra("absolutepath");
			intentDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentDialog.putExtra("absolutepath", absolutepath);
			context.startActivity(intentDialog);
		}
		if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
			i.putExtra(CallRecorderService.STATE, CallRecorderService.OUTGOING);
			i.putExtra(Intent.EXTRA_PHONE_NUMBER,
					intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
		} else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent
				.getAction())) {
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
				i.putExtra(CallRecorderService.STATE,
						CallRecorderService.INCOMING);
				i.putExtra(Intent.EXTRA_PHONE_NUMBER, intent
						.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
			} else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
				i.putExtra(CallRecorderService.STATE, CallRecorderService.BEGIN);
			} else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
				i.putExtra(CallRecorderService.STATE, CallRecorderService.END);
			}
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			i.putExtra(CallRecorderService.STATE, CallRecorderService.START);
		} else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
				|| intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)) {
			i.putExtra(CallRecorderService.STATE, CallRecorderService.STORAGE);
		} else {
			return;
		}
//		i.setPackage(this.ACTION_SHOW_ALERT_DIALOG);

//		context.startService(i);

		Intent explicitIntent = convertImplicitIntentToExplicitIntent(i, context);
		explicitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(explicitIntent != null){
			context.startService(explicitIntent);
		}

		Log.d(CallRecorderService.TAG,
				"AutoRunReceiver startService "
						+ i.getStringExtra(CallRecorderService.STATE) + ":"
						+ i.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
	}
	public static Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);

		if (resolveInfoList == null || resolveInfoList.size() != 1) {
			return null;
		}
		ResolveInfo serviceInfo = resolveInfoList.get(0);
		ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
		Intent explicitIntent = new Intent(implicitIntent);
		explicitIntent.setComponent(component);
		return explicitIntent;
	}
}
