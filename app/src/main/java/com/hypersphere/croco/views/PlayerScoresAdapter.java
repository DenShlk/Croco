package com.hypersphere.croco.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hypersphere.croco.R;
import com.hypersphere.croco.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows scores in table.
 */
public class PlayerScoresAdapter extends RecyclerView.Adapter<PlayerScoresAdapter.PlayerScoreHolder> {

	private List<Player> mData = new ArrayList<>();

	@NonNull
	@Override
	public PlayerScoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.player_score_item, parent, false);

		return new PlayerScoreHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull PlayerScoreHolder holder, int position) {
		holder.fill(mData.get(position));
		holder.setLast(position == getItemCount() - 1);
	}

	public List<Player> getData(){
		return mData;
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	public void update(List<Player> data){
		mData = data;
		notifyDataSetChanged();
	}

	public static class PlayerScoreHolder extends RecyclerView.ViewHolder {

		private TextView mNameText, mScoreText;
		private View mBottomLine;
		protected PlayerStateView mPlayerStateView;

		public PlayerScoreHolder(@NonNull View itemView) {
			super(itemView);

			mNameText = itemView.findViewById(R.id.player_score_item_name);
			mScoreText = itemView.findViewById(R.id.player_score_item_score);
			mBottomLine = itemView.findViewById(R.id.player_score_item_bottom_line);
			mPlayerStateView = itemView.findViewById(R.id.player_score_item_player_state_view);
		}

		public void fill(Player player){
			mNameText.setText(player.getName());
			mScoreText.setText(String.valueOf(player.getPoints()));
			mPlayerStateView.setState(player.getState());
		}

		public void setLast(boolean isLast){
			mBottomLine.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
		}
	}
}
