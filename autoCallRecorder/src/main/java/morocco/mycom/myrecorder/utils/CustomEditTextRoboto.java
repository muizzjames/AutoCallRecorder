package morocco.mycom.myrecorder.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CustomEditTextRoboto extends EditText {
	public CustomEditTextRoboto(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomEditTextRoboto(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomEditTextRoboto(Context context) {
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
