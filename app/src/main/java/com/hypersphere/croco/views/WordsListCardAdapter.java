package com.hypersphere.croco.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.hypersphere.croco.R;
import com.hypersphere.croco.model.WordsList;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class WordsListCardAdapter extends DecoratedRecyclerViewAdapter<WordsListCardAdapter.WordsListCardViewHolder> {

	List<Boolean> isListCheckedAtInit;
	List<WordsList> dataList;
	List<WordsListCardViewHolder> holders = new ArrayList<>();

	public WordsListCardAdapter(List<WordsList> dataList, List<Boolean> isListCheckedAtInit) {
		this.dataList = dataList;
		this.isListCheckedAtInit = isListCheckedAtInit;
		//sizes of given lists must be equal
	}

	@NonNull
	@Override
	public WordsListCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.words_list_card_layout, parent, false);

		WordsListCardViewHolder holder = new WordsListCardViewHolder(itemView);
		holders.add(holder);

		return holder;
	}

	@Override
	public void onBindViewHolder(@NonNull DecoratedViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		WordsListCardViewHolder wordsListHolder = (WordsListCardViewHolder) holder;
		wordsListHolder.listNameText.setText(dataList.get(position).name);
		wordsListHolder.listDescriptionText.setText(dataList.get(position).description);
		wordsListHolder.setChecked(isListCheckedAtInit.get(position));
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}

	public List<WordsList> getCheckedWordLists(){
		List<WordsList> list = new ArrayList<>();
		for (int i = 0; i < getItemCount(); i++) {
			if(holders.get(i).isChecked()) {
				list.add(dataList.get(i));
			}
		}
		return list;
	}

	public class WordsListCardViewHolder extends DecoratedRecyclerViewAdapter<WordsListCardViewHolder>.DecoratedViewHolder {

		private View mView;

		public TextView listNameText;
		public TextView listDescriptionText;

		private boolean checked = false;

		BlurView blurView;

		public WordsListCardViewHolder(@NonNull View itemView) {
			super(itemView);
			mView = itemView;

			listNameText = mView.findViewById(R.id.words_list_card_name_text);
			listDescriptionText = mView.findViewById(R.id.words_list_card_description_text);

			blurView = mView.findViewById(R.id.words_list_card_blur_view);

			blurView.setupWith((ViewGroup) itemView)
					.setBlurAlgorithm(new RenderScriptBlur(itemView.getContext()))
					.setBlurRadius(1f)
					.setHasFixedTransformationMatrix(true);
			blurView.setVisibility(View.VISIBLE);
			blurView.setAlpha(0);

			MaterialCardView cardView = mView.findViewById(R.id.words_list_cardview);
			cardView.setOnClickListener(v -> {
				setChecked(!checked);
			});
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

		public void setChecked(boolean checked) {
			this.checked = checked;

			//visibility animation
			blurView.animate()
					.alpha(checked ? 1 : 0)
					.setDuration(100)
					.setInterpolator(new AccelerateInterpolator())
					.start();
		}

		public boolean isChecked() {
			return checked;
		}
	}
}
