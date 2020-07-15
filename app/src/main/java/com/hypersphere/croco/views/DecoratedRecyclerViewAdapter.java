package com.hypersphere.croco.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.chauthai.overscroll.RecyclerViewBouncy;

import java.util.List;

/**
 * Shows center item and part of left and right items. Plays animation on scroll.
 */
// TODO: 12.07.2020 Define vertical orientation behavior!!!
abstract class DecoratedRecyclerViewAdapter<VH extends DecoratedRecyclerViewAdapter<VH>.DecoratedViewHolder> extends RecyclerViewBouncy.Adapter<DecoratedRecyclerViewAdapter<VH>.DecoratedViewHolder> {

	protected static final int POSITION_LEFT = 1;
	protected static final int POSITION_RIGHT = 2;

	// [0..1] part of width of recycler view should has view holder
	public float centerItemPart = 0.7f;

	RecyclerView mRecyclerView;

	private Interpolator interpolator = new LinearInterpolator();

	@Override
	public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);

		mRecyclerView = recyclerView;
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			private int totalScrollX;

			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				totalScrollX += dx;
				float centerX = recyclerView.getMeasuredWidth()/2f;
				float maxDistance = recyclerView.getMeasuredWidth()/2f;
				//margin of first element
				for (int i = 0; i < recyclerView.getChildCount(); i++) {
					View itemView = recyclerView.getChildAt(i);
					DecoratedViewHolder holder;
					try {
						holder = (VH) recyclerView.findContainingViewHolder(itemView);
					} catch (ClassCastException e) {
						continue;
					}

					float itemCenter = holder.initialPosX - totalScrollX;
					float distance = Math.abs(centerX - itemCenter);
					float value = interpolator.getInterpolation(Math.max(Math.min(distance / maxDistance, 1), 0));

					holder.scrollUpdate(value, itemCenter < centerX ? POSITION_LEFT : POSITION_RIGHT);

				}
			}
		});
	}

	@Override
	public void onBindViewHolder(@NonNull DecoratedViewHolder holder, int position) {
		int parentWidth = mRecyclerView.getMeasuredWidth();

		RecyclerView.LayoutParams holderParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
		holderParams.width = (int) (parentWidth * centerItemPart);

		//width of items before + half of self width + start margin of first item
		holder.initialPosX = (int) (holderParams.width * position + holderParams.width * 0.5 + parentWidth * (1 - centerItemPart) * 0.5f);
		//add margin to first element
		holderParams.setMarginStart(0);
		holderParams.setMarginEnd(0);
		if(position == 0){
			holderParams.setMarginStart((int) (parentWidth * (1 - centerItemPart) * 0.5f));
		}
		if(position == getItemCount() - 1){
			holderParams.setMarginEnd((int) (parentWidth * (1 - centerItemPart) * 0.5f));
		}

	}

	public void setInterpolator(Interpolator interpolator) {
		this.interpolator = interpolator;
	}

	public abstract class DecoratedViewHolder extends RecyclerViewBouncy.ViewHolder {

		int initialPosX;

		public DecoratedViewHolder(@NonNull View itemView) {
			super(itemView);
		}

		abstract void scrollUpdate(float distanceToCenter, int position);
	}
}
