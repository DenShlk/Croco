package com.hypersphere.croco.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hypersphere.croco.R;
import com.hypersphere.croco.model.GameResults;
import com.hypersphere.croco.model.Player;
import com.hypersphere.croco.views.PlayerResultsAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Shows results of finished game to user. Shows name of winner in special TextView and all
 * results in RecyclerView with {@link com.hypersphere.croco.views.PlayerResultsAdapter}.
 */
public class GameResultsActivity extends AppCompatActivity {

	private TextView mWinnerNameText;
	private RecyclerView mResultsRecycler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_results);

		mWinnerNameText = findViewById(R.id.results_winner_name_text);
		mResultsRecycler = findViewById(R.id.results_results_recycler);

		fillResults();

		Toolbar toolbar = findViewById(R.id.app_bar);
		toolbar.setNavigationOnClickListener(v -> {
			finish();
		});
	}

	private void fillResults() {
		GameResults results = (GameResults) getIntent().getSerializableExtra("results");

		List<Player> players = results.getPlayersScores();
		Collections.sort(players, (a,b) -> b.getPoints().compareTo(a.getPoints()));

		mResultsRecycler.setHasFixedSize(true);
		mResultsRecycler.setLayoutManager(new LinearLayoutManager(GameResultsActivity.this, RecyclerView.VERTICAL, false));
		PlayerResultsAdapter adapter = new PlayerResultsAdapter();
		adapter.update(players);
		mResultsRecycler.setAdapter(adapter);

		mWinnerNameText.setText(players.get(0).getName());
	}
}