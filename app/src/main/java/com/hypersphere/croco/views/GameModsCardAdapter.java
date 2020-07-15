package com.hypersphere.croco.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hypersphere.croco.R;

import java.util.List;

public class GameModsCardAdapter extends DecoratedRecyclerViewAdapter<GameModsCardAdapter.GameModeCardViewHolder> {

	List<Object> dataList;

	public GameModsCardAdapter(List<Object> dataList) {
		this.dataList = dataList;
	}

	@NonNull
	@Override
	public GameModeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.game_mode_card_layout, parent, false);

		return new GameModeCardViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull DecoratedViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		GameModeCardViewHolder gameModeHolder = (GameModeCardViewHolder) holder;
		//gameModeHolder.listNameText.setText(dataList.get(position).name);
		//gameModeHolder.listDescriptionText.setText(dataList.get(position).description);
	}

	public Object getModeAt(int pos){
		return dataList.get(pos);
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}

	public class GameModeCardViewHolder extends DecoratedRecyclerViewAdapter<GameModsCardAdapter.GameModeCardViewHolder>.DecoratedViewHolder {

		private View mView;

		public TextView listNameText;
		public TextView listDescriptionText;

		public GameModeCardViewHolder(@NonNull View itemView) {
			super(itemView);

			mView = itemView;

			listNameText = mView.findViewById(R.id.game_mode_card_name_text);
			listDescriptionText = mView.findViewById(R.id.game_mode_card_description_text);
		}

		@Override
		void scrollUpdate(float distanceToCenter, int position) {
			mView.setPivotY(mView.getMeasuredHeight() * 0.5f);
			if(position == POSITION_LEFT){
				mView.setPivotX(mView.getMeasuredWidth() * distanceToCenter);
			}else{
				mView.setPivotX(mView.getMeasuredWidth() * (1 - distanceToCenter));
			}
			float minScale = 0.75f, maxScale = 1;
			float scale = maxScale - (maxScale - minScale) * distanceToCenter;
			mView.setScaleX(scale);
			mView.setScaleY(scale);
		}
	}
}
