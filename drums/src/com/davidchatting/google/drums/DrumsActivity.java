package com.davidchatting.google.drums;

import java.io.File;
import java.util.ArrayList;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.fmod.FMODAudioDevice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DrumsActivity extends ARActivity {
	boolean isPlayerReady = false;
	boolean isPlaying = false;
	
	int downloadCount = 0;
	
	/* NDK */
	static {
		System.loadLibrary("fmodex");
		System.loadLibrary("main");
	}

	public native void cBegin();
	
	public native void cBeginWith(String path0, String path1, String path2, String path3);

	public native void cUpdate();

	public native void cEnd();

	//public native int cGetDSPBufferSize();

	public native void cPlaySound(int id);

	//public native void cPlaySoundFrom(int id, int position);

	//public native void cPauseSound();

	//public native void cStopSound();

	//public native void cSeekSoundTo(int position);

	public native int cGetLength();

	public native int cGetPosition();

	public native boolean cGetPlaying();

	public native int cGetChannelsPlaying();

	private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();
	
	private Handler mUpdateHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{		
			cUpdate();
			
			int position = cGetPosition();
			int length = cGetLength();
			int channels = cGetChannelsPlaying();
			
			//((TextView)findViewById(R.id.txtState)).setText(cGetPlaying() ? "Playing" : "Stopped");	
			//((TextView)findViewById(R.id.txtPos)).setText(String.format("%02d:%02d:%02d / %02d:%02d:%02d", position / 1000 / 60, position / 1000 % 60, position / 10 % 100, length / 1000 / 60, length / 1000 % 60, length / 10 % 100));
			//((TextView)findViewById(R.id.txtChans)).setText(String.format("%d", channels));

			removeMessages(0);
		    sendMessageDelayed(obtainMessage(0), 50);
		    
		    
		}
	};

	private ArrayList<String> trackUrls;
	private static final String trackUrl0 = "https://dl.dropboxusercontent.com/u/46669699/GoogleIO/bass.wav";
	private static final String trackUrl1 = "https://dl.dropboxusercontent.com/u/46669699/GoogleIO/hat.wav";
	private static final String trackUrl2 = "https://dl.dropboxusercontent.com/u/46669699/GoogleIO/snaredrum.wav";
	private static final String trackUrl3 = "https://dl.dropboxusercontent.com/u/46669699/GoogleIO/bosa.wav";
	
	long length;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//setCameraDisplayOrientation(this,0,getCameraInstance());
		
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.main);
		
		trackUrls = new ArrayList<String>();
		trackUrls.add(trackUrl0);
		trackUrls.add(trackUrl1);
		trackUrls.add(trackUrl2);
		trackUrls.add(trackUrl3);

		
		FrameLayout frameLayout=(FrameLayout)this.findViewById(R.id.mainLayout);
		frameLayout.setOnTouchListener(new View.OnTouchListener() {
	         @Override
	         public boolean onTouch(View v, MotionEvent event) {
		            switch(event.getAction()){
		               case MotionEvent.ACTION_DOWN:{
		            	   setDebug(!getDebug());
		            	   
		            	   TextView textView=(TextView)findViewById(R.id.debugText);
		            	   textView.setVisibility(getDebug()?TextView.VISIBLE:TextView.GONE);
		               }
		            }
		            return false;
		         }
	      });
	}
	
    @Override
    public void onStart()
    {
    	super.onStart();   	
    	mFMODAudioDevice.start();
    	String path1 = "/sdcard/bass.wav";
    	String path2 = "/sdcard/hat.wav";
		String path3 = "/sdcard/snaredrum.wav";
		String path4 = "/sdcard/bosa.wav";
				
		this.downloadTrack();
		
    	//cBeginWith(path1, path2, path3, path4);
		//cBegin();
    	//mUpdateHandler.sendMessageDelayed(mUpdateHandler.obtainMessage(0), 0);
    }
    
	@Override
	public void onStop() {
		
		/*
		if(this.isPlayerReady){
			this.endPlayer();	
		}
		*/
		
		/*
		mUpdateHandler.removeMessages(0);
    	cEnd();
    	mFMODAudioDevice.stop();

    	*/
		
		super.onStop();
		
	}
	
	@Override
	public void onDestroy() {
		
		/*
		if(this.isPlayerReady){
			this.endPlayer();	
		}
		*/
		
		/*
		mUpdateHandler.removeMessages(0);
    	cEnd();
    	mFMODAudioDevice.stop();
    	super.onDestroy();
    	*/
		super.onDestroy();
		
	}
	
	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs){
		View result=super.onCreateView(parent,name,context,attrs);
		
		return(result);
	}
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs){
		View result=super.onCreateView(name,context,attrs);
		
		return(result);
	}
	
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
 
	/**
	 * Provide our own SimpleRenderer.
	 */
	@Override
	protected ARRenderer supplyRenderer() {
		return new DrumsRenderer(this.getBaseContext());
	}
	
	/**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout)this.findViewById(R.id.mainLayout);    	
	}
	
	@SuppressWarnings("deprecation")
	public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = Surface.ROTATION_90;	//activity.getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
	
	boolean getDebug(){
		return(((DrumsApplication)(DrumsApplication.getInstance())).getDebug());
	}
	
	void setDebug(boolean d){
		((DrumsApplication)(DrumsApplication.getInstance())).setDebug(d);
	}
	
	
	/* FMOD Player */
	public void startPlayer() {
		isPlayerReady = true;
		mFMODAudioDevice.start();
		
		String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + this.getPackageName() + "/track";
		
		directoryPath = directoryPath.replace("/0/", "/legacy/");
		
		String path1 = directoryPath + "/bass.wav";
		String path2 = directoryPath + "/hat.wav";
		String path3 = directoryPath + "/snaredrum.wav";
		String path4 = directoryPath + "/bosa.wav";
		
		//path1 = path1.substring(path1.indexOf("/sdcard"));
		//path2 = path2.substring(path2.indexOf("/sdcard"));
		//path3 = path3.substring(path3.indexOf("/sdcard"));
		//path4 = path4.substring(path4.indexOf("/sdcard"));
		
		Log.d(TAG, path1);
		Log.d(TAG, path2);
		Log.d(TAG, path3);
		Log.d(TAG, path4);
		
		//path1 = "/sdcard/bosa.wav";
		//path2 = "/sdcard/bass.wav";
		//path3 = "/sdcard/hat.wav";
		//path4 = "/sdcard/snaredrum.wav";
				
		
		//cBeginWith(path1, path2, path3, path4);
		cBegin();
		
		mUpdateHandler.sendMessageDelayed(mUpdateHandler.obtainMessage(0), 0);
		
		/*		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        // Do something after 5s = 5000ms
		    	//downloadTrack();
		    	
		    	cUpdate();
				DrumsActivity.this.length = cGetLength();
				//int channels = cGetChannelsPlaying();

				
				//DrumsActivity.this.inspectBufferSize();
				
				DrumsActivity.this.playSound1(null);
				DrumsActivity.this.playSound2(null);
				DrumsActivity.this.playSound3(null);
				DrumsActivity.this.playSound4(null);
		    }
		}, 5000);
		*/
		
		
	}

	public void endPlayer() {
		isPlayerReady = false;
		cEnd();
		mFMODAudioDevice.stop();
	}

	public void playSound1(View view) {
		cPlaySound(0);
	}

	public void playSound2(View view) {
		cPlaySound(1);
	}

	public void playSound3(View view) {
		cPlaySound(2);
	}

	public void playSound4(View view) {
		cPlaySound(3);
	}

	public void playSound(int trackIndex) {

		cPlaySound(trackIndex);
		this.isPlaying = true;
	}

	/*
	public void playSoundFrom(int trackIndex, int position) {
		Log.d(TAG, "PlaySoundFrom " + position);
		cPlaySoundFrom(trackIndex, position);
		this.isPlaying = true;
	}
	*/

	public void seekSoundTo(int position) {
		//cSeekSoundTo(position);
	}

	/*
	public void pauseSound() {
		cPauseSound();
		this.isPlaying = false;
	}
	*/

	/*
	public void stopSound(int trackIndex) {
		cStopSound();
		this.isPlaying = false;
	}
	*/

	/*
	public void inspectBufferSize() {
		int size = cGetDSPBufferSize();
		Log.d(TAG, "DSP Buffer Size is " + size);
	}
	*/
	
	public void downloadTrack() {
		this.showLoadingPanel();
		
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			String directoryPath = Environment.getExternalStorageDirectory()
					.getPath()
					+ "/Android/data/"
					+ this.getPackageName()
					+ "/track";

			File directory = new File(directoryPath);

			if (!directory.exists()) {
				directory.mkdirs();
				if (!directory.exists()) {
					Log.e(TAG, "Failed to create directory:" + directoryPath);
				}
			}
			
			boolean needToDownload = false;
			for (int i = 0; i < this.trackUrls.size(); i++) {
				String trackUrl = this.trackUrls.get(i);
				String fileName = trackUrl
						.substring(trackUrl.lastIndexOf("/") + 1);
				
				
				File file = new File(directory, fileName);
				if(!file.exists()){
					needToDownload = true;
				}
				
			}
			
			if(needToDownload == false){
				this.hideLoadingPanel();
				this.startPlayer();
				//startPlaySync();	
			}else{
				for (int i = 0; i < this.trackUrls.size(); i++) {
					String trackUrl = this.trackUrls.get(i);
					String fileName = trackUrl
							.substring(trackUrl.lastIndexOf("/") + 1);
					File file = new File(directory, fileName);

					DownloadFileAsyncTask task = new DownloadFileAsyncTask() {
						@Override
						protected void onPostExecute(Boolean result) {
							if (result == true) {
								
								downloadCount ++;
								
								if(downloadCount == 4){
									DrumsActivity.this.hideLoadingPanel();
									// Log.i(TAG, "Finished downloading moview");
									Toast.makeText(DrumsActivity.this, "Music Loaded", Toast.LENGTH_SHORT)
											.show();
									startPlayer();
									//startPlaySync();
								}							
								
							} else {
								DrumsActivity.this.hideLoadingPanel();
								new AlertDialog.Builder(DrumsActivity.this)
										.setMessage("Failed to load music")
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {

													}
												}).show();
							}
						}
					};
					String filePath = file.getPath();
					task.execute(trackUrl, file.getPath());
					// Log.i(TAG, "Start downloading movie of " + this.movieUrl);

				}
			}

		}

	}
	
	
	public void showLoadingPanel(){
		/*
		View view = (View) this.findViewById(R.id.loading_panel);
		view.setVisibility(View.VISIBLE);
		
		ImageView loadingMark = (ImageView) this.findViewById(R.id.loading_mark);
		Animation animationSlideIn = AnimationUtils.loadAnimation(this, R.anim.spinning);
		loadingMark.startAnimation(animationSlideIn);
		*/		
	}
	
	public void hideLoadingPanel(){
		/*
		View view = (View) this.findViewById(R.id.loading_panel);
		view.setVisibility(View.GONE);
		
		ImageView loadingMark = (ImageView) this.findViewById(R.id.loading_mark);
		loadingMark.clearAnimation();
		*/
	}
	
}