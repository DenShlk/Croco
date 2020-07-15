package com.hypersphere.croco.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.R;
import com.hypersphere.croco.model.GameConfig;
import com.hypersphere.croco.views.WordsListCardAdapter;

/**
 * Creates @link com.hypersphere.croco.model.GameConfig } from user input.
 */
public class CreateGameActivity extends AppCompatActivity {

	WordsListCardAdapter wordsListsAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);

		int orientation = getResources().getConfiguration().orientation;

		RecyclerView wordsListsRecycler = findViewById(R.id.create_game_words_recycler);
		PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
		pagerSnapHelper.attachToRecyclerView(wordsListsRecycler);
		wordsListsAdapter = new WordsListCardAdapter(CrocoApplication.getAvailableWordsLists(), CrocoApplication.getCheckedAtInit());
		wordsListsAdapter.setInterpolator(new AccelerateDecelerateInterpolator());
		if(orientation == Configuration.ORIENTATION_LANDSCAPE)
			wordsListsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
		else
			wordsListsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		wordsListsRecycler.setAdapter(wordsListsAdapter);

		NumberPicker playersNumberPicker = findViewById(R.id.players_number_picker);
		playersNumberPicker.setMinValue(2);
		playersNumberPicker.setMaxValue(10);
		playersNumberPicker.setValue(3);
		playersNumberPicker.setWrapSelectorWheel(false);
		Switch customNamesSwitch = findViewById(R.id.custom_names_switch);
		customNamesSwitch.setChecked(false);

		View nextButton = findViewById(R.id.create_game_next_button);
		nextButton.setOnClickListener(v -> {
			if(checkInputCorrectness()){
				nextButton.setClickable(false);

				GameConfig config = new GameConfig(playersNumberPicker.getValue(), wordsListsAdapter.getCheckedWordLists());

				Intent intent;
				if(customNamesSwitch.isChecked()){
					intent = new Intent(CreateGameActivity.this, ChoosePlayersNamesActivity.class);
				}else{
					intent = new Intent(CreateGameActivity.this, GameActivity.class);
				}
				intent.putExtra("gameConfig", config);

				startActivity(intent);
				finish();
			}
		});

		Toolbar toolbar = findViewById(R.id.app_bar);
		toolbar.setNavigationOnClickListener(v -> {
			finish();
		});
	}

	private boolean checkInputCorrectness(){
		if(wordsListsAdapter.getCheckedWordLists().size() > 0){
			return true;
		}else{
			Toast.makeText(this, "Choose at least 1 word-list", Toast.LENGTH_LONG).show();
			return false;
		}
	}
}