package com.hypersphere.croco.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hypersphere.croco.R;
import com.hypersphere.croco.model.GameConfig;
import com.hypersphere.croco.model.WordsList;
import com.hypersphere.croco.views.PlayerScoresAdapter;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.hypersphere.croco.helpers.IOHelper;

public class GameActivity extends AppCompatActivity {

	private GameConfig mGameConfig;
	private List<String> mWords;
	private int mCurrentPlayerIndex = 0;
	private List<Pair<String, Integer>> mNamesAndScores = new ArrayList<>();
	private boolean isRoundFinished = false;
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
	private TextView mCurrentWordText;
	private ImageButton mWordVisibilityButton;

	private Handler mRoundEndHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		if(getIntent().hasExtra("continue")){
			Pair<Pair<GameConfig, List<Pair<String, Integer>>>, Integer> data = IOHelper.restoreGame();
			mGameConfig = data.first.first;
			mNamesAndScores = data.first.second;
			mCurrentPlayerIndex = data.second;
		}else {
			mGameConfig = (GameConfig) getIntent().getSerializableExtra("gameConfig");
			for (String name : mGameConfig.playerNames) {
				mNamesAndScores.add(new Pair<>(name, 0));
			}
		}

		mCurrentPlayerNameText = findViewById(R.id.game_current_player_name);
		mReadySheet = findViewById(R.id.game_ready_sheet);
		mRoundSheet = findViewById(R.id.game_round_sheet);
		mTimerBar = findViewById(R.id.game_timer_progressbar);
		mTimerText = findViewById(R.id.game_timer_text);
		mStartRoundButton = findViewById(R.id.game_start_round_button);
		mSkipButton = findViewById(R.id.game_skip_button);
		mDoneButton = findViewById(R.id.game_done_button);
		mCurrentWordText = findViewById(R.id.game_current_word);
		mWordVisibilityButton = findViewById(R.id.game_word_visibility_button);

		mWordVisibilityButton.setOnClickListener(v -> {
			setWordVisibility(!isWordVisible, true);
		});

		mSkipButton.setOnClickListener(v -> {
			Pair<String, Integer> currentPlayerData = mNamesAndScores.get(mCurrentPlayerIndex);
			mNamesAndScores.set(mCurrentPlayerIndex, new Pair<>(currentPlayerData.first, currentPlayerData.second - mGameConfig.pointFinePerSkip));

			if(isRoundFinished){
				endRound();
			}else {
				loadNewWord();
			}
		});
		mDoneButton.setOnClickListener(v -> {
			//remove word only if it's answered
			String word = String.valueOf(mCurrentWordText.getText());
			mWords.remove(word);
			IOHelper.addUsedWord(word);

			Pair<String, Integer> currentPlayerData = mNamesAndScores.get(mCurrentPlayerIndex);
			mNamesAndScores.set(mCurrentPlayerIndex, new Pair<>(currentPlayerData.first, currentPlayerData.second + mGameConfig.pointsPerWord));

			if(isRoundFinished){
				endRound();
			}else {
				loadNewWord();
			}
		});

		mTimerBar.setProgressMax(mGameConfig.roundDuration * 1.0f);

		mStartRoundButton.setOnClickListener(v -> startRound());

		Toolbar toolbar = findViewById(R.id.app_bar);
		toolbar.setNavigationOnClickListener(v -> {
			finish();
		});

		RecyclerView scoresRecycler = findViewById(R.id.game_scores_recycler);
		scoresRecycler.setHasFixedSize(true);
		scoresRecycler.setLayoutManager(new LinearLayoutManager(GameActivity.this, RecyclerView.VERTICAL, false));
		mScoresAdapter = new PlayerScoresAdapter();
		scoresRecycler.setAdapter(mScoresAdapter);

		calcWords();

		loadReadyScreen();
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
			for (String word : IOHelper.getListFromRes(list.resourceId)) {
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
		do{
			word = mWords.get(Math.abs(random.nextInt()) % mWords.size());
		} while(word.equals(prevWord));

		mCurrentWordText.setText(word);

		setWordVisibility(true, false);
	}

	private void loadReadyScreen() {
		mReadySheet.setVisibility(View.VISIBLE);
		mRoundSheet.setVisibility(View.INVISIBLE);

		mCurrentPlayerNameText.setText(mGameConfig.playerNames.get(mCurrentPlayerIndex));

		Collections.sort(mNamesAndScores, (o1, o2) -> o2.second.compareTo(o1.second));

		mScoresAdapter.update(mNamesAndScores);
	}

	private void startRound() {
		mReadySheet.setVisibility(View.INVISIBLE);
		mRoundSheet.setVisibility(View.VISIBLE);

		loadNewWord();

		ValueAnimator timerAnimator = ValueAnimator.ofFloat(mGameConfig.roundDuration, 0f);

		//see https://stackoverflow.com/questions/62903447/time-in-valueanimator-twice-faster-than-real-android/62903624#62903624 for understand
		float animationScale = Settings.Global.getFloat(getContentResolver(),
				Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
		timerAnimator.setDuration((long) (mGameConfig.roundDuration * 1000 / animationScale));

		timerAnimator.setInterpolator(new LinearInterpolator());

		long startTime = System.currentTimeMillis();

		timerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float timeLeft = (Float) animation.getAnimatedValue();
				mTimerBar.setProgress(timeLeft);
				int fullSecondsLeft = (int) timeLeft;
				mTimerText.setText(String.valueOf(fullSecondsLeft));
			}
		});
		timerAnimator.start();

		mRoundEndHandler.postDelayed(this::timeUp, mGameConfig.roundDuration * 1000);
		isRoundFinished = false;
	}

	private void timeUp(){
		isRoundFinished = true;
	}

	private void endRound() {
		mCurrentPlayerIndex = (mCurrentPlayerIndex + 1) % mGameConfig.playersCount;
		loadReadyScreen();
		IOHelper.saveGame(mGameConfig, mNamesAndScores, mCurrentPlayerIndex);
	}
}