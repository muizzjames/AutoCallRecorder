package morocco.mycom.myrecorder.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButtonHelvetica extends Button {
	public CustomButtonHelvetica(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomButtonHelvetica(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomButtonHelvetica(Context context) {
		super(context);
		init();
	}

	public void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/HelveticaNeueLTCom-Th.ttf");
			setTypeface(tf);
		}
	}
}
