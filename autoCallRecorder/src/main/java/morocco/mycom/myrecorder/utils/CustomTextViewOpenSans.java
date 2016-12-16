package morocco.mycom.myrecorder.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class CustomTextViewOpenSans extends TextView {
	public CustomTextViewOpenSans(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomTextViewOpenSans(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomTextViewOpenSans(Context context) {
		super(context);
		init();
	}

	public void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/Roboto-Regular.ttf");
			setTypeface(tf);
		}
	}
}
