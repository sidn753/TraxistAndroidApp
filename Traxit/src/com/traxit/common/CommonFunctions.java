package com.traxit.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class CommonFunctions {
	
	public static boolean isUsableGCM(Context context){
		boolean result = checkPlayServices(context);
		if(result){
			result = deviceHasGoogleAccount(context);
			if(result){
				return true;
			}else{
  				String message = "You must sync google account from device setting for using Traxit!";
  				showWarningDialog(context, message);
  				return false;
			}
		}else{
			return false;
		}
	
	}
 	public static boolean checkPlayServices(Context context) {
 	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
 	    if (resultCode != ConnectionResult.SUCCESS) {
 	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
 	            GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context,
 	                    9000).show();
 	        } else {
 	           Toast.makeText(context, "This device is not supported.", Toast.LENGTH_LONG).show();
 	        }
 	        return false;
 	    }
 	    return true;
 	}
 	public static boolean deviceHasGoogleAccount(Context context){
 		AccountManager accMan = AccountManager.get(context);
 		Account[] accArray = accMan.getAccountsByType("com.google");
 		return accArray.length >=1 ? true:false;
 	}
 	
	public static void showWarningDialog(Context context, String message){
		showConfirmDialog(context, "Warning", message);
	}
	@SuppressWarnings("deprecation")
	public static void showConfirmDialog(Context context, String title, String message)
	 {
			AlertDialog alertDialog = new AlertDialog.Builder(
					context).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(message);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.show();
	 }
}
