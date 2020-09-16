package com.hypersphere.croco.helpers;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.hypersphere.croco.CrocoApplication;

public class VibrationHelper {

	public static final int TYPE_STRONG = 1;
	public static final int TYPE_LIGHT = 2;

	public static void vibrate(int type){
		if(!SettingsHelper.getVibroPref()) return;

		Vibrator vibrator = (Vibrator) CrocoApplication.getContext().getSystemService(Context.VIBRATOR_SERVICE);
		if(type == TYPE_STRONG){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				vibrator.vibrate(VibrationEffect.createOneShot(500, 200));
			} else {
				vibrator.vibrate(500);
			}
		}else if(type == TYPE_LIGHT){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				vibrator.vibrate(VibrationEffect.createOneShot(200, 100));
			} else {
				vibrator.vibrate(200);
			}
		}
	}
}
