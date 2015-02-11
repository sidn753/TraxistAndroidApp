package com.traxit.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class BaseActivity extends Activity{
	static Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}
	
	protected void showWarningDialog(String message){
		showConfirmDialog("Warning", message);
	}
	@SuppressWarnings("deprecation")
	protected void showConfirmDialog(String title, String message)
	 {
			AlertDialog alertDialog = new AlertDialog.Builder(
					mContext).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(message);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.show();
	 }
}
