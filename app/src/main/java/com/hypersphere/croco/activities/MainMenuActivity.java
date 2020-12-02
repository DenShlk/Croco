package com.hypersphere.croco.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hypersphere.croco.R;
import com.hypersphere.croco.helpers.AnalyticsHelper;
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
		View menuSettingsButton = findViewById(R.id.main_menu_settings_button);

		menuNewGameButton.setOnClickListener(v -> {
			AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.ClickNewGame);
			
			Intent intent = new Intent(MainMenuActivity.this, CreateGameActivity.class);
			startActivity(intent);
		});
		menuContinueButton.setOnClickListener(v -> {
			AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.ClickContinue);

			Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
			intent.putExtra("continue", 0);
			startActivity(intent);
		});
		menuRulesButton.setOnClickListener(v -> {
			AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.ClickRules);

			Intent intent = new Intent(MainMenuActivity.this, RulesActivity.class);
			startActivity(intent);
		});
		menuSettingsButton.setOnClickListener(v -> {
			AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.ClickSettings);

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