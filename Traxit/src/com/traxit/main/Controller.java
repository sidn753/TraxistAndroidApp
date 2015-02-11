package com.traxit.main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.traxit.common.AppSetting;
import com.traxit.common.Config;



public class Controller extends Application{
    public double mPreLat;
    public double mPreLon;
    public int mGoal;
    
    
    public boolean isDisplayNotificaton(Context context, String senderEmail){
    	if(AppSetting.currentChatFriendEmail == null) return true;
    	if(isAppForeground(context)){
    		//if(getFrontActivity().equals("MessageActivity")){
    			if(AppSetting.currentChatFriendEmail != null && AppSetting.currentChatFriendEmail.equals(senderEmail)){
    				return false;
    			}else{
    				return true;
    			}
    		/*}else{
    			return true;
    		}*/
    	}else{
    		return true;
    	}
    }
    private String getFrontActivity(){
    	  ActivityManager am = (ActivityManager) this .getSystemService(ACTIVITY_SERVICE);
    	  List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
    	  ComponentName componentInfo = taskInfo.get(0).topActivity;
    	  Log.d("s", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName()+"   Package Name :  "+componentInfo.getPackageName());
    	  return taskInfo.get(0).topActivity.getClassName();
    }

    private boolean isAppForeground(Context context){
    	try {
			return new ForegroundCheckTask().execute(context).get();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }
     // Unregister this account/device pair within the server.
     public void unregister(final Context context, final String regId) {
    	 
        Log.i(Config.TAG, "unregistering device (regId = " + regId + ")");
        
        String serverUrl = Config.TraxitServerURL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            displayMessageOnScreen(context, message);
        } catch (IOException e) {
        	
            // At this point the device is unregistered from GCM, but still
            // registered in the our server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
        	
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            displayMessageOnScreen(context, message);
        }
    }

    // Issue a POST request to the server.
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {   	
        
        URL url;
        try {
        	
            url = new URL(endpoint);
            
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        
        String body = bodyBuilder.toString();
        
        Log.v(Config.TAG, "Posting '" + body + "' to " + url);
        
        byte[] bytes = body.getBytes();
        
        HttpURLConnection conn = null;
        try {
        	
        	Log.e("URL", "> " + url);
        	
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            
            // handle the response
            int status = conn.getResponseCode();
            
            // If response is not success
            if (status != 200) {
            	
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
    
    
    
	// Checking for all possible internet providers
    public boolean isConnectingToInternet(){
    	
        ConnectivityManager connectivity = 
        	                 (ConnectivityManager) getSystemService(
        	                  Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
	
   // Notifies UI to display a message.
   public void displayMessageOnScreen(Context context, String message) {
    	 
        Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
        intent.putExtra(Config.EXTRA_MESSAGE, message);
        
        // Send Broadcast to Broadcast receiver with message
        context.sendBroadcast(intent);
        
    }
    
    
   //Function to display simple Alert Dialog
   @SuppressWarnings("deprecation")
public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Set Dialog Title
		alertDialog.setTitle(title);

		// Set Dialog Message
		alertDialog.setMessage(message);

		if(status != null)
			// Set alert dialog icon
			alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Set OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});

		// Show Alert Message
		alertDialog.show();
	}
    
    private PowerManager.WakeLock wakeLock;
    
    @SuppressWarnings("deprecation")
	public  void acquireWakeLock(Context context) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "WakeLock");
        
        wakeLock.acquire();
    }

    public  void releaseWakeLock() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
    
    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

    	  @Override
    	  protected Boolean doInBackground(Context... params) {
    	    final Context context = params[0].getApplicationContext();
    	    return isAppOnForeground(context);
    	  }

    	  private boolean isAppOnForeground(Context context) {
    	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
    	    if (appProcesses == null) {
    	      return false;
    	    }
    	    final String packageName = context.getPackageName();
    	    for (RunningAppProcessInfo appProcess : appProcesses) {
    	      if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
    	        return true;
    	      }
    	    }
    	    return false;
    	  }
    	}

}
