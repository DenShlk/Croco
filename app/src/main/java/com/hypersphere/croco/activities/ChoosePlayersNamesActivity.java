package com.hypersphere.croco.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hypersphere.croco.R;
import com.hypersphere.croco.model.GameConfig;
import com.hypersphere.croco.views.PlayerNamesAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.dmoral.toasty.Toasty;

/**
 * Allows to set custom names to user
 */
// TODO: 16.12.2020 return ok if game started or cancel otherwise
public class ChoosePlayersNamesActivity extends AppCompatActivity {

	private PlayerNamesAdapter mPlayerNamesAdapter;
	private RecyclerView mNamesRecycler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_players_names);

		GameConfig config = (GameConfig) getIntent().getSerializableExtra("gameConfig");

		mNamesRecycler = findViewById(R.id.choose_names_players_recycler);
		mNamesRecycler.setHasFixedSize(true);
		mNamesRecycler.setLayoutManager(new LinearLayoutManager(ChoosePlayersNamesActivity.this, RecyclerView.VERTICAL, false));
		List<String> namesCopy = new ArrayList<>(config.playerNames);
		mPlayerNamesAdapter = new PlayerNamesAdapter(namesCopy);
		mNamesRecycler.setAdapter(mPlayerNamesAdapter);

		adjustItemTouchHelper();

		MaterialButton startButton = findViewById(R.id.choose_names_start_button);
		startButton.setOnClickListener(v -> {
			if(checkIsInputValid()) {
				Intent intent = new Intent(ChoosePlayersNamesActivity.this, GameActivity.class);

				GameConfig newConfig = new GameConfig(config.roundDuration, config.playersCount,
						config.wordsLists, mPlayerNamesAdapter.getPlayerNames());
				intent.putExtra("gameConfig", newConfig);
				startActivity(intent);

				setResult(RESULT_OK);
				finish();
			}else{
				Toasty.warning(this, "Имена не дожны совпадать", Toasty.LENGTH_SHORT).show();
			}
		});

		Toolbar toolbar = findViewById(R.id.app_bar);
		toolbar.setNavigationOnClickListener(v -> {
			finish();
		});
	}

	/**
	 * Creates {@link ItemTouchHelper} from SimpleCallback with
	 * overrided onMove, onChildDraw, onSelectedChanged methods.
	 */
	private void adjustItemTouchHelper() {
		ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				mPlayerNamesAdapter.swipe(viewHolder.getAdapterPosition(), target.getAdapterPosition());
				return true;
			}

			@Override
			public void onChildDraw(@NotNull Canvas canvas, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
				float topY = viewHolder.itemView.getTop() + dY;
				float bottomY = topY + viewHolder.itemView.getHeight();
				if (topY < 0) {
					dY = 0;
				} else if (bottomY > recyclerView.getHeight()) {
					dY = recyclerView.getHeight() - viewHolder.itemView.getHeight() - viewHolder.itemView.getTop();
				}
				super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

			private PlayerNamesAdapter.PlayerNameHolder lastDragged;

			@Override
			public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
				super.onSelectedChanged(viewHolder, actionState);

				if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
					PlayerNamesAdapter.PlayerNameHolder nameHolder = (PlayerNamesAdapter.PlayerNameHolder) viewHolder;
					nameHolder.onDrag();
					lastDragged = nameHolder;
				}
				if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
					lastDragged.onDragEnd();
				}
			}

			@Override
			public boolean isItemViewSwipeEnabled() {
				return false;
			}
		});
		touchHelper.attachToRecyclerView(mNamesRecycler);
		mPlayerNamesAdapter.attachToItemTouchHelper(touchHelper);
	}

	/**
	 * Check that there are no names duplicates.
	 * @return
	 */
	private boolean checkIsInputValid() {
		List<String> playerNames = mPlayerNamesAdapter.getPlayerNames();
		Set<String> namesSet = new HashSet<>();

		for (String name :
				playerNames) {
			if (namesSet.contains(name)){
				return false;
			}
			namesSet.add(name);
		}
		return true;
	}
}