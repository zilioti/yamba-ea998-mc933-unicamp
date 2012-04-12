package br.unicamp.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service{

	static final String TAG = "UpdaterService"; 
	
	static final int DELAY = 30000; // 1 minuto
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;
	
	@Override
	public IBinder onBind(Intent intent) { 
		return null;
	}
	
	@Override
	public void onCreate() { 
		super.onCreate();
		this.updater = new Updater(); 
		
		this.yamba = (YambaApplication) getApplication();

		Log.d(TAG, "onCreated");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		super.onStartCommand(intent, flags, startId);
		
		this.runFlag = true;
		this.updater.start();
		
		this.yamba.setServiceRunning(true);
		
		Log.d(TAG, "onStarted");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() { 
		super.onDestroy();
		
		this.runFlag = false;
		this.updater.interrupt(); 
		
		this.yamba.setServiceRunning(false);

		Log.d(TAG, "onDestroyed");
	}
	
	
	/**
	* Thread that performs the actual update from the online service
	*/
	private class Updater extends Thread { //
		
		List<Twitter.Status> timeline;

		
		public Updater() {
		super("UpdaterService-Updater");
	}
		
		
		@Override
		public void run() { 
		
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runFlag) { 
				Log.d(TAG, "Updater running");
				try {
					// Some work goes here...
					// Get the timeline from the cloud
					try {
						timeline = yamba.getTwitter().getFriendsTimeline(); //
					} catch (TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}

					// Loop over the timeline and print it out
					for (Twitter.Status status : timeline) { //
						Log.d(TAG, String.format("%s: %s", status.user.name, status.text)); //
					}

					
					Log.d(TAG, "Updater ran");
					Thread.sleep(DELAY); //
				} catch (InterruptedException e) { 
					Log.e(TAG, "Failed to connect to twitter service", e);
					updaterService.runFlag = false;
				}
			}
		}
	}
}