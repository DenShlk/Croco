package com.hypersphere.croco.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.R;
import com.hypersphere.croco.helpers.SettingsHelper;
import com.hypersphere.croco.helpers.TipsHelper;
import com.hypersphere.croco.model.GameConfig;
import com.hypersphere.croco.views.WordsListCardAdapter;
import com.ramotion.fluidslider.FluidSlider;

import java.util.Arrays;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

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
		LabeledSwitch customNamesSwitch = findViewById(R.id.custom_names_switch);
		View nextButton = findViewById(R.id.create_game_start_button);
		TextView playersCountText = findViewById(R.id.create_game_players_count_text);
		ImageButton playersCountLessButton = findViewById(R.id.create_game_players_count_less);
		ImageButton playersCountMoreButton = findViewById(R.id.create_game_players_count_more);
		TextView roundDurationText = findViewById(R.id.create_game_round_duration_text);
		FluidSlider roundDurationSlider = findViewById(R.id.create_game_round_duration_slider);

		roundDurationSlider.setStartText(String.valueOf(GameConfig.MIN_ROUND_DURATION));
		roundDurationSlider.setEndText(String.valueOf(GameConfig.MAX_ROUND_DURATION));
		roundDurationSlider.setBeginTrackingListener(() -> {
			roundDurationText.animate()
					.alpha(0f)
					.setDuration(250)
					.start();
			return Unit.INSTANCE;
		});
		roundDurationSlider.setEndTrackingListener(() -> {
			new Handler().postDelayed((Runnable) () -> {
				roundDurationText.animate()
						.alpha(1f)
						.setDuration(250)
						.start();
			}, 200);
			return Unit.INSTANCE;
		});
		roundDurationSlider.setPositionListener(new Function1<Float, Unit>() {
			@Override
			public Unit invoke(Float aFloat) {
				mRoundDuration = (int) (aFloat * (GameConfig.MAX_ROUND_DURATION - GameConfig.MIN_ROUND_DURATION)
										+ GameConfig.MIN_ROUND_DURATION);
				mRoundDuration = mRoundDuration / 10 * 10;
				roundDurationSlider.setBubbleText(mRoundDuration + "с");
				return Unit.INSTANCE;
			}
		});
		roundDurationSlider.setPosition(1f * (mRoundDuration - GameConfig.MIN_ROUND_DURATION) /
				(GameConfig.MAX_ROUND_DURATION - GameConfig.MIN_ROUND_DURATION));

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

		PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
		pagerSnapHelper.attachToRecyclerView(wordsListsRecycler);
		mWordsListsAdapter = new WordsListCardAdapter(CrocoApplication.getAvailableWordsLists(), SettingsHelper.getLastChosenLists());
		mWordsListsAdapter.setInterpolator(new AccelerateDecelerateInterpolator());

		/*
		if(orientation == Configuration.ORIENTATION_LANDSCAPE)
			wordsListsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
		else
			wordsListsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		 */
		wordsListsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		wordsListsRecycler.setAdapter(mWordsListsAdapter);

		customNamesSwitch.setOn(false);

		nextButton.setOnClickListener(v -> {
			if(checkInputCorrectness()){
				nextButton.setClickable(false);

				GameConfig config = new GameConfig(mRoundDuration, mPlayersCount, mWordsListsAdapter.getCheckedWordLists());
				SettingsHelper.setLastChosenLists(mWordsListsAdapter.getCheckedWordLists());

				Intent intent;
				if(customNamesSwitch.isOn()){
					intent = new Intent(CreateGameActivity.this, ChoosePlayersNamesActivity.class);
				}else{
					intent = new Intent(CreateGameActivity.this, GameActivity.class);
				}
				intent.putExtra("gameConfig", config);

				startActivity(intent);
				finish();
			}
		});

		showTips();

		Toolbar toolbar = findViewById(R.id.app_bar);
		toolbar.setNavigationOnClickListener(v -> {
			finish();
		});
	}

	private void showTips(){
		List<TipsHelper.Tip> tips = Arrays.asList(
				new TipsHelper.Tip(
						"Выберите слова",
						"Мы создали множество подборок слов по темам, от музыкальных инструментов, до персонажей игры престолов!",
						R.id.create_game_words_recycler),
				new TipsHelper.Tip(
						"Укажите число комманд",
						"Мы придумали базовые названия, но вы можете их изменить.",
						R.id.create_game_players_count_text),
				new TipsHelper.Tip(
						"В игру!",
						"Желаем удачи",
						R.id.create_game_start_button)
				);
		TipsHelper.showTips(this, tips.iterator(), true);
	}

	private boolean checkInputCorrectness(){
		if(mWordsListsAdapter.getCheckedWordLists().size() > 0){
			return true;
		}else{
			Toast.makeText(this, "Выберите хотя бы 1 словарь", Toast.LENGTH_LONG).show();
			return false;
		}
	}
}