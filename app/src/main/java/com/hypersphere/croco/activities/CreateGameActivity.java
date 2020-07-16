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
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.R;
import com.hypersphere.croco.model.GameConfig;
import com.hypersphere.croco.views.WordsListCardAdapter;

/**
 * Creates @link com.hypersphere.croco.model.GameConfig } from user input.
 */
public class CreateGameActivity extends AppCompatActivity {

	private WordsListCardAdapter mWordsListsAdapter;
	private int mPlayersCount = GameConfig.BASE_PLAYERS_COUNT;
	private int mRoundDuration = GameConfig.BASE_ROUND_DURATION;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);

		int orientation = getResources().getConfiguration().orientation;

		RecyclerView wordsListsRecycler = findViewById(R.id.create_game_words_recycler);
		SeekBar roundDurationSeekBar = findViewById(R.id.create_game_round_duration_seekbar);
		TextView roundDurationText = findViewById(R.id.create_game_round_duration_text);
		Switch customNamesSwitch = findViewById(R.id.custom_names_switch);
		View nextButton = findViewById(R.id.create_game_start_button);
		TextView playersCountText = findViewById(R.id.create_game_players_count_text);
		ImageButton playersCountLessButton = findViewById(R.id.create_game_players_count_less);
		ImageButton playersCountMoreButton = findViewById(R.id.create_game_players_count_more);

		playersCountText.setText(String.valueOf(mPlayersCount));
		playersCountLessButton.setOnClickListener(v -> {
			if(mPlayersCount > GameConfig.MIN_PLAYERS_COUNT){
				playersCountText.setText(String.valueOf(--mPlayersCount));
			}
		});
		playersCountMoreButton.setOnClickListener(v -> {
			if(mPlayersCount < GameConfig.MAX_PLAYERS_COUNT){
				playersCountText.setText(String.valueOf(++mPlayersCount));
			}
		});

		roundDurationSeekBar.setMax((GameConfig.MAX_ROUND_DURATION - GameConfig.MIN_ROUND_DURATION) / 10);
		roundDurationSeekBar.setProgress((mRoundDuration - GameConfig.MIN_ROUND_DURATION) / 10);
		roundDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mRoundDuration = progress * 10 + GameConfig.MIN_ROUND_DURATION;
				String durationString = mRoundDuration + "с";
				roundDurationText.setText(durationString);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});

		PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
		pagerSnapHelper.attachToRecyclerView(wordsListsRecycler);
		mWordsListsAdapter = new WordsListCardAdapter(CrocoApplication.getAvailableWordsLists(), CrocoApplication.getCheckedAtInit());
		mWordsListsAdapter.setInterpolator(new AccelerateDecelerateInterpolator());
		if(orientation == Configuration.ORIENTATION_LANDSCAPE)
			wordsListsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
		else
			wordsListsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		wordsListsRecycler.setAdapter(mWordsListsAdapter);

		customNamesSwitch.setChecked(false);

		nextButton.setOnClickListener(v -> {
			if(checkInputCorrectness()){
				nextButton.setClickable(false);

				GameConfig config = new GameConfig(mRoundDuration, mPlayersCount, mWordsListsAdapter.getCheckedWordLists());

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
		if(mWordsListsAdapter.getCheckedWordLists().size() > 0){
			return true;
		}else{
			Toast.makeText(this, "Choose at least 1 word-list", Toast.LENGTH_LONG).show();
			return false;
		}
	}
}