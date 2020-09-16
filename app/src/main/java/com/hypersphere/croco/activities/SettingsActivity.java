package com.hypersphere.croco.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.R;
import com.hypersphere.croco.helpers.IOHelper;
import com.hypersphere.croco.helpers.SettingsHelper;
import com.hypersphere.croco.helpers.VibrationHelper;

public class SettingsActivity extends AppCompatActivity {

	private ImageView mSoundButton;
	private ImageView mVibroButton;
	private Button mResetUsedWords;

	private boolean soundPref = SettingsHelper.getSoundPref();
	private boolean vibroPref = SettingsHelper.getVibroPref();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		mSoundButton = findViewById(R.id.settings_sound_image);
		mVibroButton = findViewById(R.id.settings_vibration_button);
		mResetUsedWords = findViewById(R.id.settings_reset_used_words);
		MaterialButton backButton = findViewById(R.id.settings_back_button);

		backButton.setOnClickListener(v -> {
			finish();
		});

		updateButtons();

		mSoundButton.setOnClickListener(v -> {
			soundPref=!soundPref;
			updateButtons();
			SettingsHelper.setSoundPref(soundPref);
		});
		mVibroButton.setOnClickListener(v -> {
			vibroPref=!vibroPref;
			if(vibroPref) VibrationHelper.vibrate(VibrationHelper.TYPE_LIGHT);
			updateButtons();
			SettingsHelper.setVibroPref(vibroPref);
		});

		mResetUsedWords.setOnClickListener(v -> {
			IOHelper.clearUsedWords();
		});
	}

	private void updateButtons(){
		if(soundPref) {
			mSoundButton.setImageResource(R.drawable.ic_round_volume_up_24);
		}else{
			mSoundButton.setImageResource(R.drawable.ic_round_volume_off_24);
		}

		if(vibroPref) {
			mVibroButton.setImageResource(R.drawable.ic_round_vibration_24);
		}else{
			mVibroButton.setImageResource(R.drawable.ic_round_vibration_off_24);
		}
	}

}