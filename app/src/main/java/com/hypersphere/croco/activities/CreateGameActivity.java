package com.hypersphere.croco.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.gson.Gson;
import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.R;
import com.hypersphere.croco.helpers.AnalyticsHelper;
import com.hypersphere.croco.helpers.SettingsHelper;
import com.hypersphere.croco.helpers.TipsHelper;
import com.hypersphere.croco.model.GameConfig;
import com.hypersphere.croco.model.WordsList;
import com.hypersphere.croco.views.WordsListCardAdapter;
import com.ramotion.fluidslider.FluidSlider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;

/**
 * Creates {@link GameConfig} from user input.
 */
public class CreateGameActivity extends AppCompatActivity {

	private static final int CHOOSE_PLAYERS_NAMES_ACTIVITY_REQUEST_CODE = 833;

	private TextView mRoundDurationText;
	private FluidSlider mRoundDurationSlider;
	private View mNextButton;

	private WordsListCardAdapter mWordsListsAdapter;
	private int mPlayersCount;
	private int mRoundDuration;
	private HashSet<String> mLastChosenWordListsNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);

		restoreLastSettings();

		RecyclerView wordsListsRecycler = findViewById(R.id.create_game_words_recycler);
		LabeledSwitch customNamesSwitch = findViewById(R.id.create_game_custom_names_switch);
		mNextButton = findViewById(R.id.create_game_start_button);
		TextView playersCountText = findViewById(R.id.create_game_players_count_text);
		ImageButton playersCountLessButton = findViewById(R.id.create_game_players_count_less);
		ImageButton playersCountMoreButton = findViewById(R.id.create_game_players_count_more);
		mRoundDurationText = findViewById(R.id.create_game_round_duration_text);
		mRoundDurationSlider = findViewById(R.id.create_game_round_duration_slider);

		adjustRoundDurationSlider();

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
		mWordsListsAdapter = new WordsListCardAdapter(CrocoApplication.getAvailableWordsLists(), mLastChosenWordListsNames);
		mWordsListsAdapter.setInterpolator(new AccelerateDecelerateInterpolator());

		wordsListsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
		wordsListsRecycler.setAdapter(mWordsListsAdapter);

		customNamesSwitch.setOn(false);

		mNextButton.setOnClickListener(v -> {
			if(checkInputCorrectness()){
				mNextButton.setClickable(false);

				GameConfig config = new GameConfig(mRoundDuration, mPlayersCount, mWordsListsAdapter.getCheckedWordLists());

				//We don't want to send data we not interested in, like player names,
				// all data about WordsLists
				sendAnalyticsOnGameCreate(config, customNamesSwitch.isOn());

				Intent intent;
				if(customNamesSwitch.isOn()){
					intent = new Intent(CreateGameActivity.this, ChoosePlayersNamesActivity.class);

					intent.putExtra("gameConfig", config);
					startActivityForResult(intent, CHOOSE_PLAYERS_NAMES_ACTIVITY_REQUEST_CODE);
				}else {
					//create game skipping choosing names and finishing this activity just after
					intent = new Intent(CreateGameActivity.this, GameActivity.class);

					intent.putExtra("gameConfig", config);
					startActivity(intent);
					finish();
				}
			}
		});

		showTips();

		Toolbar toolbar = findViewById(R.id.app_bar);
		toolbar.setNavigationOnClickListener(v -> {
			finish();
		});
	}

	/**
	 * Gets result from {@link ChoosePlayersNamesActivity}. If it is {@code RESULT_OK} game started
	 * and this activity should be finished, otherwise user returned from
	 * {@link ChoosePlayersNamesActivity} to change some settings (at least we expect so).
	 * @param requestCode
	 * @param resultCode {@code RESULT_OK} if game started and activity should be finished or
	 *                                    otherwise game not started.
	 * @param data null
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == CHOOSE_PLAYERS_NAMES_ACTIVITY_REQUEST_CODE){
			if(resultCode == RESULT_OK){
				// game started
				finish();
			}else{
				// next button was disabled to prevent multi starting of activities
				mNextButton.setClickable(true);
			}
		}
	}

	private void restoreLastSettings() {
		GameConfig lastConfig = SettingsHelper.getLastGameSettings();
		mRoundDuration = lastConfig.roundDuration;
		mPlayersCount = lastConfig.playersCount;
		mLastChosenWordListsNames = new HashSet<>();
		for (WordsList wordsList : lastConfig.wordsLists) {
			mLastChosenWordListsNames.add(wordsList.getName());
		}
	}

	private void adjustRoundDurationSlider() {

		mRoundDurationSlider.setStartText(String.valueOf(GameConfig.MIN_ROUND_DURATION));
		mRoundDurationSlider.setEndText(String.valueOf(GameConfig.MAX_ROUND_DURATION));
		mRoundDurationSlider.setBeginTrackingListener(() -> {
			mRoundDurationText.clearAnimation();
			mRoundDurationText.animate()
					.alpha(0f)
					.setDuration(250)
					.start();
			return Unit.INSTANCE;
		});
		mRoundDurationSlider.setEndTrackingListener(() -> {
			// delayed animation creates bag when user double-click - text stays visible under sliders' pointer
			//new Handler().postDelayed((Runnable) () -> {
			mRoundDurationText.clearAnimation();
			mRoundDurationText.animate()
					.alpha(1f)
					.setDuration(250)
					.start();
			//}, 200);
			return Unit.INSTANCE;
		});
		mRoundDurationSlider.setPositionListener(aFloat -> {
			mRoundDuration = (int) (aFloat * (GameConfig.MAX_ROUND_DURATION - GameConfig.MIN_ROUND_DURATION)
					+ GameConfig.MIN_ROUND_DURATION);

			mRoundDuration = mRoundDuration / 10 * 10;
			mRoundDurationSlider.setBubbleText(mRoundDuration + "с");

			return Unit.INSTANCE;
		});
		mRoundDurationSlider.setPosition(1f * (mRoundDuration - GameConfig.MIN_ROUND_DURATION) /
				(GameConfig.MAX_ROUND_DURATION - GameConfig.MIN_ROUND_DURATION));
	}

	private void sendAnalyticsOnGameCreate(GameConfig config, boolean customNames) {
		Bundle gameDataBundle = new Bundle();
		gameDataBundle.putInt("RoundDuration", config.roundDuration);
		gameDataBundle.putInt("PlayersCount", config.playersCount);
		gameDataBundle.putBoolean("CustomNames", customNames);

		ArrayList<String> chosenWordsLists = new ArrayList<>();
		for (WordsList list : config.wordsLists) {
			chosenWordsLists.add(list.getName());
		}
		Gson gson = new Gson();
		gameDataBundle.putString("ChosenWordsLists", gson.toJson(chosenWordsLists, chosenWordsLists.getClass()));

		AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.CreateGame, gameDataBundle);
	}

	private void showTips(){
		List<TipsHelper.Tip> tips = Arrays.asList(
				new TipsHelper.Tip(
						"Выберите наборы слов",
						"Мы создали множество подборок слов по темам, от музыкальных инструментов, до персонажей известных фильмов!",
						R.id.create_game_words_recycler),
				new TipsHelper.Tip(
						"Настройте длительность раунда",
						"Меняйте это значении в зависимости от сложности слов или количества человек в команде.",
						R.id.create_game_round_duration_layout
				),
				new TipsHelper.Tip(
						"Укажите число комманд",
						"Настройте игру под свою компанию!",
						R.id.create_game_players_count_layout),
				new TipsHelper.Tip(
						"Можно изменить названия команд",
						"Изначально используются придуманные нами названия, но вы можете изменить их.",
						R.id.create_game_custom_names_layout
				)
		);
		TipsHelper.showTips(this, tips.iterator());
	}

	private boolean checkInputCorrectness(){
		if(mWordsListsAdapter.getCheckedWordLists().size() > 0){
			return true;
		}else{
			Toasty.warning(this, "Выберите хотя бы 1 словарь", Toasty.LENGTH_SHORT).show();
			return false;
		}
	}

	/**
	 * Saves current settings after each closing of activity.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		GameConfig config = new GameConfig(mRoundDuration, mPlayersCount, mWordsListsAdapter.getCheckedWordLists());
		SettingsHelper.setLastGameSettings(config);
	}
}