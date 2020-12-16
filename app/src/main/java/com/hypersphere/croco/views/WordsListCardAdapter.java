package com.hypersphere.croco.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.hypersphere.croco.R;
import com.hypersphere.croco.model.WordsList;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class WordsListCardAdapter extends DecoratedRecyclerViewAdapter<WordsListCardAdapter.WordsListCardViewHolder> {

	HashSet<String> checkedAtInitNames;
	List<WordsList> dataList;
	List<WordsListCardViewHolder> holders = new ArrayList<>();

	public WordsListCardAdapter(@NotNull List<WordsList> dataList, @NotNull HashSet<String> checkedAtInitNames) {
		this.dataList = dataList;
		this.checkedAtInitNames = checkedAtInitNames;
	}

	@NonNull
	@Override
	public WordsListCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.words_list_card_layout, parent, false);

		// check getItemViewType
		int position = viewType;
		if (holders.size() > position)
			return holders.get(position);
		else {
			WordsListCardViewHolder holder = new WordsListCardViewHolder(itemView);

			if (holders.size() != position)
				throw new InvalidParameterException("Elements must be added one-by-one, from first to last");

			holders.add(holder);

			return holder;
		}
	}

	@Override
	public int getItemViewType(int position) {
		// onCreateViewHolder assumes it
		return position;
	}

	@Override
	public void onBindViewHolder(@NonNull DecoratedViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		WordsList list = dataList.get(position);

		WordsListCardViewHolder wordsListHolder = (WordsListCardViewHolder) holder;

		wordsListHolder.listNameText.setText(list.getName());
		wordsListHolder.listDescriptionText.setText(list.getDescription());
		wordsListHolder.listBackgroundImage.setImageResource(list.getDrawableResourceId());

		wordsListHolder.setChecked(checkedAtInitNames.contains(list.getName()));
	}

	@Override
	public void onViewRecycled(@NonNull DecoratedViewHolder holder) {
		super.onViewRecycled(holder);
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}

	// TODO: 16.12.2020 test

	/**
	 * Iterates through items of recycler view and returns checked elements. If item was not viewed
	 * and binded checks if {@code checkedAtInitNames} contains it.
	 * @return checked elements of this recycler.
	 */
	public List<WordsList> getCheckedWordLists() {
		List<WordsList> list = new ArrayList<>();
		for (int i = 0; i < getItemCount(); i++) {
			if (i >= holders.size()) {
				String name = dataList.get(i).getName();
				if (checkedAtInitNames.contains(name)) {
					list.add(dataList.get(i));
				}
			} else if (holders.get(i).isChecked()) {
					list.add(dataList.get(i));
			}
		}
		return list;
	}

	public class WordsListCardViewHolder extends DecoratedRecyclerViewAdapter<WordsListCardViewHolder>.DecoratedViewHolder {

		private static final int CHECKED_ANIMATION_DURATION = 100;

		private View mView;

		TextView listNameText;
		TextView listDescriptionText;
		ImageView listBackgroundImage;
		ImageView listCheckedImage;

		private boolean checked = false;

		BlurView blurView;

		public WordsListCardViewHolder(@NonNull View itemView) {
			super(itemView);
			mView = itemView;

			listNameText = mView.findViewById(R.id.words_list_card_name_text);
			listDescriptionText = mView.findViewById(R.id.words_list_card_description_text);
			listBackgroundImage = mView.findViewById(R.id.words_list_card_image);
			listCheckedImage = mView.findViewById(R.id.words_list_card_checked_image);

			blurView = mView.findViewById(R.id.words_list_card_blur_view);

			blurView.setupWith((ViewGroup) itemView)
					.setBlurAlgorithm(new RenderScriptBlur(itemView.getContext()))
					.setBlurRadius(3f)
					.setHasFixedTransformationMatrix(false);
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
			if (position == POSITION_LEFT) {
				mView.setPivotX(mView.getMeasuredWidth() * distanceToCenter);
			} else {
				mView.setPivotX(mView.getMeasuredWidth() * (1 - distanceToCenter));
			}
			float minScale = 0.75f, maxScale = 1;
			float scale = maxScale - (maxScale - minScale) * distanceToCenter;
			mView.setScaleX(scale);
			mView.setScaleY(scale);
		}

		public void setChecked(boolean checked) {
			this.checked = checked;


			listDescriptionText.animate()
					.alpha(checked ? 0 : 1)
					.setDuration(CHECKED_ANIMATION_DURATION)
					.start();
			listNameText.animate()
					.alpha(checked ? 0 : 1)
					.setDuration(CHECKED_ANIMATION_DURATION)
					.start();
			blurView.animate()
					.alpha(checked ? 0 : 1)
					.setDuration(CHECKED_ANIMATION_DURATION)
					.setInterpolator(new AccelerateInterpolator())
					.start();

			listCheckedImage.animate()
					.alpha(checked ? 1 : 0)
					.setDuration(CHECKED_ANIMATION_DURATION)
					.start();

		}

		public boolean isChecked() {
			return checked;
		}
	}
}
