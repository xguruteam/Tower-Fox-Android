package com.foxridge.towerfox.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;


import com.foxridge.towerfox.ui.LoginActivity;

import java.io.File;
import java.util.ArrayList;

public class Helper {

	private static Helper helperInstance;

	private Helper() {

	}

	public static Helper getHelperInstance() {

		if (null == helperInstance) {
			helperInstance = new Helper();
		}
		return helperInstance;

	}

	public static final String RECORDING_PATH = Environment
			.getExternalStorageDirectory() + "/Recordings";
	public static final String LOAD_RECORDINGS = "Load Records";


	public ArrayList<String> getAllFileInDirectory(File directory) {

		final File[] files = directory.listFiles();
		ArrayList<String> listOfRecordings = new ArrayList<String>();

		if (files != null) {
			for (File file : files) {
				if (file != null) {
					if (file.isDirectory()) { // it is a folder...
						getAllFileInDirectory(file);
					} else { // it is a file...

						listOfRecordings.add(file.getAbsolutePath());
					}
				}
			}
		}
		return listOfRecordings;
	}

	public ArrayList<String> getAllRecordings() {
		return getAllFileInDirectory(new File(RECORDING_PATH));

	}

	public boolean createRecordingFolder() {

		if (!new File(RECORDING_PATH).exists()) {

			return new File(RECORDING_PATH).mkdir();
		} else {
			return true;
		}

	}

	public static String getErrorText(Context context, int code) {

		return "Error";
	}

	public static void showLoginAlert(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("Warning!").setMessage("You should sign in first.").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				activity.startActivity(new Intent(activity, LoginActivity.class));
				activity.finish();
			}
		});
		builder.show();
	}

	public static void showErrorDialog(final Activity activity, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("Warning!").setMessage(message).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}


}
