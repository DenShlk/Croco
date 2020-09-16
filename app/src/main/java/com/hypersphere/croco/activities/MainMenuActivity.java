package com.hypersphere.croco.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hypersphere.croco.R;

import com.hypersphere.croco.helpers.IOHelper;

/**
 * Activity with menu for start game, check rules, edit words and open settings.
 */
public class MainMenuActivity extends AppCompatActivity {

	private View menuContinueButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		View menuNewGameButton = findViewById(R.id.main_menu_new_game_button);
		menuContinueButton = findViewById(R.id.main_menu_continue_button);
		View menuRulesButton = findViewById(R.id.main_menu_rules_button);
		View menuWordsButton = findViewById(R.id.main_menu_settings_button);
		View menuSettingsButton = findViewById(R.id.main_menu_settings_button);

		menuNewGameButton.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, CreateGameActivity.class);
			startActivity(intent);
		});
		menuContinueButton.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
			intent.putExtra("continue", 0);
			startActivity(intent);
		});
		menuRulesButton.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, RulesActivity.class);
			startActivity(intent);
		});
		menuWordsButton.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, WordsRedactorActivity.class);
			startActivity(intent);
		});
		menuSettingsButton.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
			startActivity(intent);
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		if(IOHelper.isGameSaved()){
			menuContinueButton.setVisibility(View.VISIBLE);
		}else{
			menuContinueButton.setVisibility(View.GONE);
		}
	}
}