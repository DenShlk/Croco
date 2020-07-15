package com.hypersphere.croco.views;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerCardAdapter extends RecyclerView.Adapter<PlayerCardAdapter.PlayerCardViewHolder> {

	List<String> names;

	@NonNull
	@Override
	public PlayerCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return null;
	}

	@Override
	public void onBindViewHolder(@NonNull PlayerCardViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return 0;
	}

	public class PlayerCardViewHolder extends RecyclerView.ViewHolder {

		public PlayerCardViewHolder(@NonNull View itemView) {
			super(itemView);
		}

		
	}
}
