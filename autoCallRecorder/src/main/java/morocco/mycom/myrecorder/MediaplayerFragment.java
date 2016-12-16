package morocco.mycom.myrecorder;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import morocco.mycom.myrecorder.utils.GetOption;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MediaplayerFragment extends Fragment {
	MediaPlayer mp;
	Handler mHandler = new Handler();
	SeekBar bar;
	int position;
	int datalistsize;
	boolean isSearch = false;
	boolean isReset = false;
	TextView tvName;
	TextView tvCurrenttime;
	TextView tvTotaltime;
	TextView tvTime;
	ImageView btnNext, btnPrevious, btnPlay, callerimage, imgBack;
	ImageLoader imageLoader = ImageLoader.getInstance();

	public MediaplayerFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(
				R.layout.media_screen, container, false);

		AdView adView = (AdView) v.findViewById(R.id.adView_mediascreen);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		Bundle bundle = new Bundle();
		bundle = getArguments();
		if (bundle != null) {
			position = bundle.getInt("position");
			isSearch = bundle.getBoolean("isSearch");
		}

		datalistsize = MainActivity.dataContainer.size();

		bar = (SeekBar) v.findViewById(R.id.seekBar);
		tvName = (TextView) v.findViewById(R.id.tvName);
		tvTotaltime = (TextView) v.findViewById(R.id.tvTotaltime);
		tvCurrenttime = (TextView) v.findViewById(R.id.tvCurrenttime);
		tvTime = (TextView) v.findViewById(R.id.tvTime);
		btnNext = (ImageView) v.findViewById(R.id.btnNext);
		btnPrevious = (ImageView) v.findViewById(R.id.btnPrevious);
		btnPlay = (ImageView) v.findViewById(R.id.btnPlay);
		callerimage = (ImageView) v.findViewById(R.id.callerimage);
		imgBack = (ImageView) v.findViewById(R.id.imgBack);

		imageLoader.init(GetOption.getConfig(getActivity()));
		if (isSearch) {
			tvName.setText(MainActivity.tempContainer.get(position).name);
			tvTime.setText(MainActivity.tempContainer.get(position).time);

			imageLoader.displayImage(MainActivity.tempContainer
					.get(position).imagePath.toString(), callerimage);
		}

		else {
			tvName.setText(MainActivity.dataContainer.get(position).name);
			tvTime.setText(MainActivity.dataContainer.get(position).time);

			imageLoader.displayImage(MainActivity.dataContainer
					.get(position).imagePath.toString(), callerimage);
		}

		playerMedia();

		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mp.isPlaying()) {
					mp.pause();
					btnPlay.setImageResource(R.drawable.btnplay);
				} else {
					mp.start();
					btnPlay.setImageResource(R.drawable.btn_pause);
				}
			}
		});

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (datalistsize != position + 1) {

					try {

						mp.stop();

						if (isReset == false) {
							position = position + 1;
						}

						if (isSearch) {
							tvName.setText(MainActivity.tempContainer
									.get(position).name);
							tvTime.setText(MainActivity.tempContainer
									.get(position).time);

							imageLoader.displayImage(
									MainActivity.tempContainer
											.get(position).imagePath.toString(),
									callerimage);
							mp = new MediaPlayer();
							mp.reset();
							mp.setDataSource(MainActivity.tempContainer
									.get(position).audio.getAbsolutePath());
						} else {
							tvName.setText(MainActivity.dataContainer
									.get(position).name);
							tvTime.setText(MainActivity.dataContainer
									.get(position).time);

							imageLoader.displayImage(
									MainActivity.dataContainer
											.get(position).imagePath.toString(),
									callerimage);
							mp = new MediaPlayer();
							mp.reset();
							mp.setDataSource(MainActivity.dataContainer
									.get(position).audio.getAbsolutePath());
						}
						mp.prepare();
						mp.start();

						mp.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								btnPlay.setImageResource(R.drawable.btnplay);
							}
						});

						btnPlay.setImageResource(R.drawable.btn_pause);

						mp.setOnPreparedListener(new OnPreparedListener() {
							@Override
							public void onPrepared(MediaPlayer mp) {
								updateProgressBar();
							}
						});

						bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {

								mHandler.removeCallbacks(mUpdateTimeTask);
								int totalDuration = mp.getDuration();
								int currentPosition = progressToTimer(
										seekBar.getProgress(), totalDuration);

								// forward or backward to certain seconds
								mp.seekTo(currentPosition);

								// update timer progress again
								updateProgressBar();
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub
								mHandler.removeCallbacks(mUpdateTimeTask);
							}

							@Override
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								// TODO Auto-generated method stub

							}
						});

					} catch (Exception e) {
					}
					isReset = false;
				} else {
					position = 0;
					isReset = true;
					btnNext.performClick();
				}

			}
		});

		btnPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					mp.stop();
					position = position - 1;

					if (isSearch) {
						tvName.setText(MainActivity.tempContainer
								.get(position).name);

						tvTime.setText(MainActivity.tempContainer
								.get(position).time);

						imageLoader.displayImage(
								MainActivity.tempContainer.get(position).imagePath
										.toString(), callerimage);
						mp = new MediaPlayer();
						mp.reset();
						mp.setDataSource(MainActivity.tempContainer
								.get(position).audio.getAbsolutePath());
					} else {
						tvName.setText(MainActivity.dataContainer
								.get(position).name);

						tvTime.setText(MainActivity.dataContainer
								.get(position).time);

						imageLoader.displayImage(
								MainActivity.dataContainer.get(position).imagePath
										.toString(), callerimage);
						mp = new MediaPlayer();
						mp.reset();
						mp.setDataSource(MainActivity.dataContainer
								.get(position).audio.getAbsolutePath());
					}

					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							btnPlay.setImageResource(R.drawable.btnplay);
						}
					});
					
					btnPlay.setImageResource(R.drawable.btn_pause);
					mp.setOnPreparedListener(new OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mp) {
							updateProgressBar();
						}
					});

					bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {

							mHandler.removeCallbacks(mUpdateTimeTask);
							int totalDuration = mp.getDuration();
							int currentPosition = progressToTimer(
									seekBar.getProgress(), totalDuration);

							// forward or backward to certain seconds
							mp.seekTo(currentPosition);

							// update timer progress again
							updateProgressBar();
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							mHandler.removeCallbacks(mUpdateTimeTask);
						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub

						}
					});

				} catch (Exception e) {
				}
			}
		});

		imgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mp.stop();
				getActivity().onBackPressed();
			}
		});
		return v;
	}

	private void playerMedia() {

		try {

			if (isSearch) {

				mp = new MediaPlayer();
				mp.reset();
				mp.setDataSource(MainActivity.tempContainer.get(position).audio
						.getAbsolutePath());

			} else {

				mp = new MediaPlayer();
				mp.reset();
				mp.setDataSource(MainActivity.dataContainer.get(position).audio
						.getAbsolutePath());

			}
			mp.prepare();
			mp.start();
			
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					btnPlay.setImageResource(R.drawable.btnplay);
				}
			});
			
			mp.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					updateProgressBar();
				}
			});

			bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

					mHandler.removeCallbacks(mUpdateTimeTask);
					int totalDuration = mp.getDuration();
					int currentPosition = progressToTimer(
							seekBar.getProgress(), totalDuration);

					// forward or backward to certain seconds
					mp.seekTo(currentPosition);

					// update timer progress again
					updateProgressBar();
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					mHandler.removeCallbacks(mUpdateTimeTask);
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
				}
			});

		} catch (Exception e) {
		}
	}

	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();

			int progress = (int) (getProgressPercentage(currentDuration,
					totalDuration));
			bar.setProgress(progress);

			tvCurrenttime.setText(milliSecondsToTimer(currentDuration));
			tvTotaltime.setText(milliSecondsToTimer(totalDuration));

			mHandler.postDelayed(this, 100);
		}
	};

	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double) progress) / 100) * totalDuration);

		// return current duration in milliseconds
		return currentDuration * 1000;
	}

	public int getProgressPercentage(long currentDuration, long totalDuration) {
		Double percentage = (double) 0;

		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);

		// calculating percentage
		percentage = (((double) currentSeconds) / totalSeconds) * 100;

		// return percentage
		return percentage.intValue();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {

					mp.stop();

					FragmentTransaction fragmentTransaction = getFragmentManager()
							.beginTransaction();
					fragmentTransaction.setCustomAnimations(
							R.anim.back_slide_in, R.anim.back_slide_out);
					MediaplayerFragment fragment = (MediaplayerFragment) getFragmentManager()
							.findFragmentByTag("MediaplayerFragment");
					fragmentTransaction.remove(fragment);
					fragmentTransaction.commit();
					getFragmentManager().popBackStack();

					return true;

				}

				return false;
			}
		});
	}

	public String milliSecondsToTimer(long milliseconds) {
		String finalTimerString = "";
		String secondsString = "";

		// Convert total duration into time
		int hours = (int) (milliseconds / (1000 * 60 * 60));
		int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
		// Add hours if there
		if (hours > 0) {
			finalTimerString = hours + ":";
		}

		// Prepending 0 to seconds if it is one digit
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = "" + seconds;
		}

		finalTimerString = finalTimerString + minutes + ":" + secondsString;

		// return timer string
		return finalTimerString;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
