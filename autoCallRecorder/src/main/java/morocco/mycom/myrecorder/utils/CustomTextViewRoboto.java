package morocco.mycom.myrecorder.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class CustomTextViewRoboto extends TextView {
	public CustomTextViewRoboto(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomTextViewRoboto(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomTextViewRoboto(Context context) {
		super(context);
		init();
	}

	public void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/OPENSANS-REGULAR.TTF");
			setTypeface(tf);
		}
	}
}
