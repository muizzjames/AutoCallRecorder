package morocco.mycom.myrecorder.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import morocco.mycom.myrecorder.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class GetOption {
//	static DisplayImageOptions options;
	static ImageLoaderConfiguration config;


	public static DisplayImageOptions getProfileOption(String strName) {
		TextDrawable.IBuilder mDrawableBuilder;
		ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
		DisplayImageOptions options = null;
		if (options == null) {
			mDrawableBuilder = TextDrawable.builder()
					.round();
			TextDrawable drawable = mDrawableBuilder.build(String.valueOf(strName.charAt(0)), mColorGenerator.getColor(strName));
			options = new DisplayImageOptions.Builder().cacheOnDisk(true)
					.cacheInMemory(true).considerExifParams(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.showImageOnLoading(drawable)
					.showImageOnFail(drawable)
					.displayer(new RoundedBitmapDisplayer(90))
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
		}
		return options;
	}
	
	
	public static boolean checkNotNull(String url) {
		if (url.equals(""))
			return false;
		else
			return true;
	}

	public static ImageLoaderConfiguration getConfig(Context context) {
		if (config == null) {
			config = new ImageLoaderConfiguration.Builder(context).memoryCache(
					new WeakMemoryCache()).build();
		}
		return config;
	}

}
