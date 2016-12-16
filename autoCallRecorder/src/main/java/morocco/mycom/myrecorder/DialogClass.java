package morocco.mycom.myrecorder;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class DialogClass extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to save this recorded file ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Bundle bundle = getIntent().getExtras();
						String absolutepath = null;
						if (bundle != null) {
							absolutepath = bundle.getString("absolutepath");
							File file = new File(absolutepath);
							file.delete();
						}
						dialog.cancel();
						
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}
}
