package com.hypersphere.croco.views;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hypersphere.croco.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Shows scores in table.
 */
public class PlayerScoresAdapter extends RecyclerView.Adapter<PlayerScoresAdapter.PlayerScoreHolder> {

	private List<Pair<String, Integer>> mData = new ArrayList<>();

	@NonNull
	@Override
	public PlayerScoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.player_score_item, parent, false);

		return new PlayerScoreHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull PlayerScoreHolder holder, int position) {
		holder.fill(mData.get(position).first, mData.get(position).second);
		holder.setLast(position == getItemCount() - 1);
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	public void update(List<Pair<String, Integer>> data){
		mData = data;
		notifyDataSetChanged();
	}

	public class PlayerScoreHolder extends RecyclerView.ViewHolder {

		private TextView mNameText, mScoreText;
		private View mBottomLine;

		public PlayerScoreHolder(@NonNull View itemView) {
			super(itemView);

			mNameText = itemView.findViewById(R.id.player_score_item_name);
			mScoreText = itemView.findViewById(R.id.player_score_item_score);
			mBottomLine = itemView.findViewById(R.id.player_score_item_bottom_line);
		}

		public void fill(String name, Integer score){
			mNameText.setText(name);
			mScoreText.setText(String.valueOf(score));
		}

		public void setLast(boolean isLast){
			mBottomLine.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
		}
	}
}
