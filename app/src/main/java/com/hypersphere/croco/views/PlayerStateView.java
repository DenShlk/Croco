package com.hypersphere.croco.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.hypersphere.croco.R;
import com.hypersphere.croco.model.Player;

public class PlayerStateView extends MaterialButton {
	public PlayerStateView(Context context) {
		super(context);
	}

	public PlayerStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayerStateView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setState(Player.State state){
		ColorStateList color = null;
		String text = null;
		switch (state){
			case Moved:
				color = ContextCompat.getColorStateList(getContext(), R.color.colorPlayerStateMoved);
				text = "Сходил";
				break;
			case Moving:
				color = ContextCompat.getColorStateList(getContext(), R.color.colorPlayerStateMoving);
				text = "Ходит";
				break;
			case DidNotMove:
				color = ContextCompat.getColorStateList(getContext(), R.color.colorPlayerStateDidNotMove);
				text = "Не сходил";
				break;
		}
		setStrokeColor(color);
		setTextColor(color);
		setText(text);
	}
}
