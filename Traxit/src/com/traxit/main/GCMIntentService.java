package com.traxit.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.traxit.common.Config;
import com.traxit.library.NotificationService;
import com.traxit.widget.PushNotificationInfo;


public class GCMIntentService extends GCMBaseIntentService  {

	private static final String TAG = "GCMIntentService";
	
	private static Controller aController = null;

    public GCMIntentService() {
    	// Call extended class Constructor GCMBaseIntentService
        super(Config.GOOGLE_SENDER_ID);

    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
    	
    	//Get Global Controller Class object (see application tag in AndroidManifest.xml)
    	if(aController == null)
           aController = (Controller) getApplicationContext();
    	
        Log.i(TAG, "Device registered: regId = " + registrationId);
        aController.displayMessageOnScreen(context, "Your device registred with GCM");
        //aController.register(context, "wozheguo", registrationId);
    }

    /**
     * Method called on device unregistred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
    	if(aController == null)
            aController = (Controller) getApplicationContext();
        Log.i(TAG, "Device unregistered");
        aController.displayMessageOnScreen(context, getString(R.string.gcm_unregistered));
        //aController.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message from GCM server
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
    	
    	if(aController == null)
            aController = (Controller) getApplicationContext();
   	
        Log.i(TAG, "Received message");
        String type = intent.getExtras().getString("type");
        
        PushNotificationInfo newPushInfo = new PushNotificationInfo();
        if (type.equals(Config.kPushMessageType)) {
           
            /*
             $body["type"]="message";
             $body["sendFrom"] = $email;
             $body["message"] = $message;
             $body["sendTime"] = $currentTime;
             */
            String sendFrom =intent.getExtras().getString("sendFrom");
            String message =intent.getExtras().getString("message");
            //long timeInterval =intent.getExtras().getLong("sendTime");
            long timeInterval =Long.parseLong(intent.getExtras().getString("sendTime"));
            
            String fullName =intent.getExtras().getString("fullName");
           
            newPushInfo.pushNotificationMessage(sendFrom, fullName, message, timeInterval);
            if(aController.isDisplayNotificaton(context, newPushInfo.getSender())){
            	generateNotification(context,  newPushInfo);	
            }
         }else if (type.equals(Config.kPushInviteType)){
            //================send push notification  =============
            //$body["type"]="invite";
            //$body["sendFrom"] = $sender;
        	String sendFrom =intent.getExtras().getString("sendFrom");
        	String fullName =intent.getExtras().getString("fullName");
        	newPushInfo.pushNotificationInvite(sendFrom, fullName);
        	generateNotification(context,  newPushInfo);
        }else if (type.equals(Config.kPushDeclineType)){
            //================send push notification  =============
            //$body["type"]="invite";
            //$body["sendFrom"] = $sender;
        	 String sendFrom =intent.getExtras().getString("sendFrom");
        	 String fullName =intent.getExtras().getString("fullName");
        	 newPushInfo.pushNotificationDecline(sendFrom, fullName);
        	 generateNotification(context,  newPushInfo);

        }else if (type.equals(Config.kPushAcceptType)){
            /*$body["type"]="accept";
            $body["sendFrom"] = $email;
            $body["fullName"] = $res->firstName." ".$res->lastName;
            $body["latitude"] = $res->latitude;
            $body["longitude"] = $res->longitude;*/
        	String sendFrom =intent.getExtras().getString("sendFrom");
            String fullName =intent.getExtras().getString("fullName");
            //double latitude = intent.getExtras().getDouble("latitude");
        	double latitude =Double.parseDouble(intent.getExtras().getString("latitude"));
        	double longitude =Double.parseDouble(intent.getExtras().getString("longitude"));
           // double latitude =Double.parseDouble(intent.getExtras().getString("latitude"));
            //double longitude =intent.getExtras().getDouble("longitude");
            newPushInfo.pushNotificationAccept(sendFrom, fullName, latitude, longitude);
            generateNotification(context,  newPushInfo);
        }else if(type.equals(Config.kPushLocationType)){
        	/*
        	 *     $body["type"]="updateLocation";
			    $body["sendFrom"] = $email;
			    $body["latitude"] = $latitude;
			    $body["longitude"] = $longitude;*/
        	
        	String sendFrom =intent.getExtras().getString("sendFrom");
        	Log.e("moving:", sendFrom);
        	
        	double latitude =Double.parseDouble(intent.getExtras().getString("latitude"));
        	double longitude =Double.parseDouble(intent.getExtras().getString("longitude"));
            //double latitude = intent.getExtras().getDouble("latitude");
            //double longitude =intent.getExtras().getDouble("longitude");
            newPushInfo.pushNotificationLocaiton(sendFrom, latitude, longitude);
        }else{
        	return;
        }
        
        //aController.displayMessageOnScreen(context, notificationMesssage);
        NotificationService.getInstance().postNotification(Config.Notification_Message, newPushInfo);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
    	
    	if(aController == null)
            aController = (Controller) getApplicationContext();
    	
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        aController.displayMessageOnScreen(context, message);
        // notifies user
        //aController.generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
    	
    	if(aController == null)
            aController = (Controller) getApplicationContext();
    	
        Log.i(TAG, "Received error: " + errorId);
        aController.displayMessageOnScreen(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
    	
    	if(aController == null)
            aController = (Controller) getApplicationContext();
    	
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        aController.displayMessageOnScreen(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    @SuppressWarnings("deprecation")
	private static void generateNotification(Context context, PushNotificationInfo pushInfo) {
		String type = pushInfo.getType();
		String message="";
		Intent notificationIntent = new Intent(context, MainActivity.class);
		if(type.equals(Config.kPushMessageType)){
			String content = pushInfo.getMessage();
			if(content.length() > 15){
				content = content.substring(0, 14) + "...";
			}
			message = "From:" + pushInfo.getName()+"\n"+ content;
			notificationIntent = new Intent(context, MessageActivity.class);
			notificationIntent.putExtra(Config.FriendEmail, pushInfo.getSender());
			notificationIntent.putExtra(Config.FriendName, pushInfo.getName());

		}else if(type.equals(Config.kPushInviteType)){
			message = "Invite Request\nFrom:" + pushInfo.getName();
			notificationIntent = new Intent(context, MainActivity.class);
		}else if(type.equals(Config.kPushAcceptType)){
			message = "From:" + pushInfo.getName()+"  \nYou are added as friend!";
			notificationIntent = new Intent(context, MainActivity.class);
		}else if(type.equals(Config.kPushDeclineType)){
			message = "Decline Request\nFrom:" + pushInfo.getName();
			notificationIntent = new Intent(context, MainActivity.class);
		}else if(type.equals(Config.kPushLocationType)){
			return;
		}
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        //Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }


}
