package com.hypersphere.croco.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hypersphere.croco.R;

/**
 * Simplified {@link PlayerScoresAdapter}, player state is disabled
 */
public class PlayerResultsAdapter extends PlayerScoresAdapter {

	@NonNull
	@Override
	public PlayerResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.player_score_item, parent, false);

		return new PlayerResultsHolder(itemView);
	}

	public static class PlayerResultsHolder extends PlayerScoresAdapter.PlayerScoreHolder {

		public PlayerResultsHolder(@NonNull View itemView) {
			super(itemView);

			mPlayerStateView.setVisibility(View.GONE);
		}
	}
}
