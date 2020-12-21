package com.hypersphere.croco.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.R;
import com.hypersphere.croco.helpers.AnalyticsHelper;
import com.hypersphere.croco.helpers.IOHelper;
import com.hypersphere.croco.helpers.SettingsHelper;
import com.hypersphere.croco.helpers.VibrationHelper;
import com.hypersphere.croco.model.GameConfig;
import com.hypersphere.croco.model.GameResults;
import com.hypersphere.croco.model.Player;
import com.hypersphere.croco.model.WordsList;
import com.hypersphere.croco.views.PlayerScoresAdapter;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import es.dmoral.toasty.Toasty;

public class GameActivity extends AppCompatActivity {

	private GameConfig mGameConfig;
	private List<String> mWords;
	private int mCurrentPlayerIndex = 0;
	private List<Player> mPlayers = new ArrayList<>();

	// true if round sheet is showed, otherwise ready sheet is showed
	private boolean isInRound = false;

	// true if time is up (but round is still going on)
	private boolean isRoundTimeIsUp = false;
	private boolean isWordVisible = true;

	private PlayerScoresAdapter mScoresAdapter;
	private TextView mCurrentPlayerNameText;
	private CircularProgressBar mTimerBar;
	private TextView mTimerText;
	private View mReadySheet;
	private View mRoundSheet;
	private View mStartRoundButton;
	private View mSkipButton;
	private View mDoneButton;
	private View mEndGameButton;
	private TextView mCurrentWordText;
	private ImageButton mWordVisibilityButton;
	private ImageButton mHelpButton;
	private ImageView mSettingsButton;

	private static final int TIMER_SOUND_RATE_INCREASING_DURATION = 10;
	private static final float TIMER_SOUND_MIN_RATE = 1.0f;
	private static final float TIMER_SOUND_MAX_RATE = 2.0f;

	private Handler mRoundEndHandler = new Handler();
	private SoundPool mSoundPool;
	private ValueAnimator timerAnimator;
	ValueAnimator timerSoundRateAnimator;

	private int mTimerTickSoundId   = 0;
	private int mAnsweredSoundId    = 0;
	private int mSkipSoundId        = 0;
	private int mRoundEndSoundId    = 0;
	private int mTimerTickStreamId  = 0;
	private int mAnsweredStreamId   = 0;
	private int mSkipStreamId       = 0;
	private int mRoundEndStreamId   = 0;
	private float curVolume = SettingsHelper.getSoundPref() ? 1 : 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		setUpAudio();

		if(getIntent().hasExtra("continue")){
			IOHelper.GameData gameData = IOHelper.restoreGame();
			mGameConfig = gameData.getConfig();
			mPlayers = gameData.getPlayers();
			mCurrentPlayerIndex = gameData.getCurrentPlayerIndex();
		}else {
			// TODO: 16.12.2020 refactor idea: create SavingsHelper and move there all saving staff
			mGameConfig = (GameConfig) getIntent().getSerializableExtra("gameConfig");
			for (String name : mGameConfig.playerNames) {
				mPlayers.add(new Player(name));
			}
		}
		// order must be same
		mPlayers = Collections.unmodifiableList(mPlayers);


		mCurrentPlayerNameText = findViewById(R.id.game_current_player_name);
		mReadySheet = findViewById(R.id.game_ready_sheet);
		mRoundSheet = findViewById(R.id.game_round_sheet);
		mTimerBar = findViewById(R.id.game_timer_progressbar);
		mTimerText = findViewById(R.id.game_timer_text);
		mStartRoundButton = findViewById(R.id.game_start_round_button);
		mSkipButton = findViewById(R.id.game_skip_button);
		mDoneButton = findViewById(R.id.game_done_button);
		mEndGameButton = findViewById(R.id.game_end_game_button);
		mCurrentWordText = findViewById(R.id.game_current_word);
		mWordVisibilityButton = findViewById(R.id.game_word_visibility_button);
		mHelpButton = findViewById(R.id.game_help_button);
		mSettingsButton = findViewById(R.id.game_settings_button);

		mSettingsButton.setOnClickListener(v -> {
			startActivity(new Intent(GameActivity.this, SettingsActivity.class));
		});

		mWordVisibilityButton.setOnClickListener(v -> {
			setWordVisibility(!isWordVisible, true);
		});

		adjustGameButtons();

		mTimerBar.setProgressMax(mGameConfig.roundDuration * 1.0f);

		mStartRoundButton.setOnClickListener(v -> startRound());
		
		mEndGameButton.setOnClickListener(v -> showEndGameDialog());

		RecyclerView scoresRecycler = findViewById(R.id.game_scores_recycler);
		scoresRecycler.setHasFixedSize(true);
		scoresRecycler.setLayoutManager(new LinearLayoutManager(GameActivity.this, RecyclerView.VERTICAL, false));
		mScoresAdapter = new PlayerScoresAdapter();
		scoresRecycler.setAdapter(mScoresAdapter);

		calcWords();

		loadReadyScreen();

		Toolbar toolbar = findViewById(R.id.app_bar);
		toolbar.setNavigationOnClickListener(v -> {
			onBackPressed();
		});
	}

	/**
	 * Finishes activity if all data saved (round ended) or asks user for it if not.
	 */
	@Override
	public void onBackPressed() {
		if(isInRound)
			//check that user understand game will not be saved
			new AlertDialog.Builder(GameActivity.this, R.style.AlertDialog_Croco)
					.setTitle("Уверен что хочешь выйти?")
					.setMessage("Промежуточные результаты не сохранятся.")
					.setCancelable(true)
					.setPositiveButton("Остаться", (dialog, which) -> {
						dialog.dismiss();
					})
					.setNegativeButton("Выйти", (dialog, which) -> {
						finish();
					})
					.create()
					.show();
		else {
			finish();
		}
	}

	/**
	 * Sets {@link android.view.View.OnClickListener} for game buttons such as skip, answered and
	 * help buttons.
	 */
	private void adjustGameButtons() {

		mSkipButton.setOnClickListener(v -> {
			VibrationHelper.vibrate(VibrationHelper.TYPE_LIGHT);
			mSkipStreamId = mSoundPool.play(mSkipSoundId, curVolume, curVolume, 0, 0, 1);

			String word = String.valueOf(mCurrentWordText.getText());
			AnalyticsHelper.sendEventWithWord(AnalyticsHelper.ActionId.SkipWord, word);

			Player currentPlayer = mPlayers.get(mCurrentPlayerIndex);
			currentPlayer.addPoints(-mGameConfig.pointFinePerSkip);

			if(isRoundTimeIsUp){
				endRound();
			}else {
				loadNewWord();
			}
		});
		mDoneButton.setOnClickListener(v -> {
			mAnsweredStreamId = mSoundPool.play(mAnsweredSoundId, curVolume, curVolume, 0, 0, 1);

			//remove word only if it's answered
			String word = String.valueOf(mCurrentWordText.getText());
			mWords.remove(word);
			IOHelper.addUsedWord(word);

			AnalyticsHelper.sendEventWithWord(AnalyticsHelper.ActionId.GuessWord, word);

			Player currentPlayer = mPlayers.get(mCurrentPlayerIndex);
			currentPlayer.addPoints(mGameConfig.pointsPerWord);

			if(isRoundTimeIsUp){
				endRound();
			}else {
				loadNewWord();
			}
		});

		mHelpButton.setOnClickListener(v -> {
			String word = String.valueOf(mCurrentWordText.getText());
			AnalyticsHelper.sendEventWithWord(AnalyticsHelper.ActionId.ClickHelpInGame, word);

			if(CrocoApplication.isInternetAvailable()) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String wikiLink = "https://ru.wikipedia.org/wiki/" + word;
				intent.setData(Uri.parse(wikiLink));
				startActivity(intent);
			}else{
				Toasty.error(GameActivity.this, "Необходим доступ в интернет.", Toasty.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Sets {@link AudioAttributes} for {@code mSoundPool} and loads sound clips to it.
	 */
	private void setUpAudio() {
		AudioAttributes attributes = new AudioAttributes.Builder()
				.setUsage(AudioAttributes.USAGE_GAME)
				.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
				.build();
		mSoundPool = new SoundPool.Builder()
				.setAudioAttributes(attributes)
				.setMaxStreams(10)
				.build();
		mTimerTickSoundId = mSoundPool.load(GameActivity.this, R.raw.timer_single_tick_2, 2);
		mAnsweredSoundId = mSoundPool.load(GameActivity.this, R.raw.answered, 0);
		mSkipSoundId = mSoundPool.load(GameActivity.this, R.raw.skip, 0);
		mRoundEndSoundId = mSoundPool.load(GameActivity.this, R.raw.gong, 1);
	}

	/**
	 * Applies sound settings, because it might be called after
	 * returning from {@link SettingsActivity}.
	 */
	@Override
	protected void onStart() {
		super.onStart();

		applySoundSettings();
	}

	private void applySoundSettings() {
		curVolume = SettingsHelper.getSoundPref() ? 1 : 0;

		mSoundPool.setVolume(mTimerTickSoundId, 0, 0);
		mSoundPool.setVolume(mAnsweredSoundId, curVolume, curVolume);
		mSoundPool.setVolume(mSkipSoundId, curVolume, curVolume);
		// feels too loud
		mSoundPool.setVolume(mRoundEndSoundId, curVolume * 0.5f, curVolume * 0.5f);
		mSoundPool.setVolume(mTimerTickStreamId, curVolume, curVolume);
		mSoundPool.setVolume(mAnsweredStreamId, curVolume, curVolume);
		mSoundPool.setVolume(mSkipStreamId, curVolume, curVolume);
		mSoundPool.setVolume(mRoundEndStreamId, curVolume, curVolume);
	}

	private void setWordVisibility(boolean visible, boolean animate){
		if(visible == isWordVisible) return;

		isWordVisible = visible;
		final long animationDuration = 500;
		if(visible){
			mWordVisibilityButton.setImageResource(R.drawable.ic_round_visibility_off_24);
			mCurrentWordText.animate()
					.alpha(1.0f)
					.setDuration(animate ? animationDuration : 1)
					.start();
		}else {
			mWordVisibilityButton.setImageResource(R.drawable.ic_round_visibility_24);
			mCurrentWordText.animate()
					.alpha(0.0f)
					.setDuration(animate ? animationDuration : 1)
					.start();
		}
	}

	private void calcWords() {
		mWords = new ArrayList<>();

		Set<String> used = IOHelper.getUsedWords();
		for (WordsList list : mGameConfig.wordsLists) {
			List<String> words = list.getWords();
			for (String word : words) {
				if (!used.contains(word)) {
					mWords.add(word);
				}
			}
		}
	}

	private void loadNewWord() {
		String prevWord = String.valueOf(mCurrentWordText.getText());
		Random random = new Random();
		String word;
		if(mWords.size() == 0){
			AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.RanOutOfWords);
			showRanOutOfWordsDialog();

			pauseRound();
			return;
		}
		do {
			word = mWords.get(Math.abs(random.nextInt()) % mWords.size());
		} while(word.equals(prevWord));

		mCurrentWordText.setText(word);

		setWordVisibility(true, false);
	}
	
	private void showEndGameDialog(){
		new AlertDialog.Builder(this, R.style.AlertDialog_Croco)
				.setMessage("Вы уверены что хотите закончить?")
				.setPositiveButton("Ну ещё чуть-чуть", (dialog, which) -> {
					AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.CancelFinishGame);

					dialog.dismiss();
				})
				.setNeutralButton("Да", (dialog, which) -> {
					AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.FinishGame);

					Intent intent = new Intent(GameActivity.this, GameResultsActivity.class);
					List<Player> scores = mScoresAdapter.getData();
					GameResults results = new GameResults(scores);
					intent.putExtra("results", results);
					startActivity(intent);
					
					finish();
				})
				.setCancelable(true)
				.create()
				.show();
		
	}

	private void showRanOutOfWordsDialog() {
		new AlertDialog.Builder(this, R.style.AlertDialog_Croco)
				.setMessage("Упс, слова кончились!")
				.setPositiveButton("Вернуться в меню", (dialog, which) -> {
					AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.LeaveGameAfterRanOutOfWords);

					dialog.dismiss();
					finish();
				})
				.setNeutralButton("Обнулить и продолжить", (dialog, which) -> {
					AnalyticsHelper.sendEvent(AnalyticsHelper.ActionId.ResetWordsAfterRanOut);

					dialog.dismiss();
					IOHelper.clearUsedWords();
					calcWords();
					loadNewWord();
					resumeRound();
				})
				.setCancelable(false)
				.create()
				.show();
	}

	long playedTime;

	private void pauseRound() {
		timerAnimator.pause();
		timerSoundRateAnimator.pause();
		mSoundPool.pause(mTimerTickStreamId);
		playedTime = timerAnimator.getCurrentPlayTime();
		mRoundEndHandler.removeCallbacksAndMessages(null);
	}

	private void resumeRound() {
		timerAnimator.resume();
		mSoundPool.resume(mTimerTickStreamId);
		mRoundEndHandler.postDelayed(this::timeUp, mGameConfig.roundDuration * 1000 -  playedTime);
		if(timerSoundRateAnimator.isPaused()) {
			timerSoundRateAnimator.resume();
		}else{
			mRoundEndHandler.postDelayed(timerSoundRateAnimator::start,
					(mGameConfig.roundDuration - TIMER_SOUND_RATE_INCREASING_DURATION) * 1000 - playedTime);
		}
	}

	private void loadReadyScreen() {
		mReadySheet.setVisibility(View.VISIBLE);
		mRoundSheet.setVisibility(View.INVISIBLE);

		Player currentPlayer = mPlayers.get(mCurrentPlayerIndex);

		mCurrentPlayerNameText.setText(currentPlayer.getName());
		currentPlayer.setState(Player.State.Moving);

		List<Player> pointSortedPlayers = new ArrayList<>(mPlayers);
		Collections.sort(pointSortedPlayers, (o1, o2) -> o2.getPoints().compareTo(o1.getPoints()));

		mScoresAdapter.update(pointSortedPlayers);

		// game should be saved every round and on the start (probably rewritten)
		IOHelper.saveGame(mGameConfig, mPlayers, mCurrentPlayerIndex);
	}

	private void startRound() {
		mTimerTickStreamId = mSoundPool.play(mTimerTickSoundId, curVolume, curVolume, 2, -1, TIMER_SOUND_MIN_RATE);

		mReadySheet.setVisibility(View.INVISIBLE);
		mRoundSheet.setVisibility(View.VISIBLE);

		timerAnimator = ValueAnimator.ofFloat(mGameConfig.roundDuration, 0f);

		//see https://stackoverflow.com/questions/62903447/time-in-valueanimator-twice-faster-than-real-android/62903624#62903624
		float animationScale = Settings.Global.getFloat(getContentResolver(),
				Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
		timerAnimator.setDuration((long) (mGameConfig.roundDuration * 1000 / animationScale));

		timerAnimator.setInterpolator(new LinearInterpolator());

		timerAnimator.addUpdateListener(animation -> {
			float timeLeft = (Float) animation.getAnimatedValue();
			mTimerBar.setProgress(timeLeft);
			int fullSecondsLeft = (int) timeLeft;
			mTimerText.setText(String.valueOf(fullSecondsLeft));
		});
		timerAnimator.start();

		timerSoundRateAnimator = ValueAnimator.ofFloat(TIMER_SOUND_MIN_RATE, TIMER_SOUND_MAX_RATE);
		timerSoundRateAnimator.setInterpolator(new LinearInterpolator());
		timerSoundRateAnimator.setDuration((long) (TIMER_SOUND_RATE_INCREASING_DURATION * 1000 / animationScale));
		timerSoundRateAnimator.addUpdateListener(animation -> {
			float rate = (float) animation.getAnimatedValue();
			mSoundPool.setRate(mTimerTickStreamId, rate);
			//float volume = (float) (1 + Math.pow(rate - TIMER_SOUND_MIN_RATE, 2));
			//mSoundPool.setVolume(mTimerTickStreamId, volume, volume);
		});
		mRoundEndHandler.postDelayed(timerSoundRateAnimator::start, (mGameConfig.roundDuration - TIMER_SOUND_RATE_INCREASING_DURATION) * 1000);

		mRoundEndHandler.postDelayed(this::timeUp, mGameConfig.roundDuration * 1000);
		isRoundTimeIsUp = false;
		isInRound = true;
		
		loadNewWord();
	}

	private void timeUp(){
		mSoundPool.stop(mTimerTickStreamId);
		mRoundEndStreamId = mSoundPool.play(mRoundEndSoundId, curVolume, curVolume, 1, 0, 1);

		isRoundTimeIsUp = true;

		VibrationHelper.vibrate(VibrationHelper.TYPE_STRONG);
	}

	private void endRound() {
		mPlayers.get(mCurrentPlayerIndex).setState(Player.State.Moved);

		mCurrentPlayerIndex++;
		if(mCurrentPlayerIndex == mGameConfig.playersCount){
			mCurrentPlayerIndex = 0;
			
			for (Player player : mPlayers) {
				player.setState(Player.State.DidNotMove);
			}

			mEndGameButton.setVisibility(View.VISIBLE);
		}else{
			mEndGameButton.setVisibility(View.GONE);
		}

		isInRound = false;

		loadReadyScreen();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mSoundPool.stop(mTimerTickStreamId);
		mSoundPool.stop(mRoundEndStreamId);
		mSoundPool.stop(mAnsweredStreamId);
		mSoundPool.stop(mSkipStreamId);
		mRoundEndHandler.removeCallbacksAndMessages(null);
	}
}