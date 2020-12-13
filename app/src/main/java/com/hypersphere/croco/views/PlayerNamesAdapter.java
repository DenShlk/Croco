package com.hypersphere.croco.views;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.R;
import com.hypersphere.croco.helpers.IOHelper;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.Collections;
import java.util.List;

/**
 * Shows name items in table. User can change and drag them.
 */
public class PlayerNamesAdapter extends RecyclerView.Adapter<PlayerNamesAdapter.PlayerNameHolder> {

	private List<String> mData;
	private ItemTouchHelper mItemTouchHelper;
	private PlayerNameHolder holderInEditing;

	public PlayerNamesAdapter(List<String> data) {
		mData = data;
	}

	@NonNull
	@Override
	public PlayerNameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.player_name_item, parent, false);

		return new PlayerNameHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull PlayerNameHolder holder, int position) {
		holder.fill(mData.get(position), position);
		//holder.setLast(position == getItemCount() - 1);
	}

	public void swipe(int first, int second){
		Collections.swap(mData, first, second);
		notifyItemMoved(first, second);
	}

	public void attachToItemTouchHelper(ItemTouchHelper helper){
		mItemTouchHelper = helper;
	}

	public List<String> getPlayerNames(){
		if(holderInEditing != null)
			holderInEditing.endEditing();

		return mData;
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	public class PlayerNameHolder extends RecyclerView.ViewHolder {

		private TextView mNameText;
		private View mBottomLine;
		private EditText mNameEdit;
		private View mBackground;

		private int mPosition;

		public PlayerNameHolder(@NonNull View itemView) {
			super(itemView);

			mNameText = itemView.findViewById(R.id.player_name_item_name_text);
			mBottomLine = itemView.findViewById(R.id.player_name_item_bottom_line);
			mNameEdit = itemView.findViewById(R.id.player_name_item_name_edit);
			mBackground = itemView.findViewById(R.id.player_name_item_background);

			ImageButton mDragButton = itemView.findViewById(R.id.player_name_item_drag_button);
			mDragButton.setOnTouchListener((v, event) -> {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					mItemTouchHelper.startDrag(PlayerNameHolder.this);
				}
				return false;
			});

			ImageButton editButton = itemView.findViewById(R.id.player_name_item_edit_button);
			editButton.setOnClickListener(v -> {
				mNameText.setVisibility(View.INVISIBLE);
				mNameEdit.setVisibility(View.VISIBLE);
				mNameEdit.findFocus();
				mNameEdit.requestFocus();
				mNameEdit.setSelection(mNameEdit.getText().length());
				InputMethodManager imm = (InputMethodManager) CrocoApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mNameEdit, InputMethodManager.SHOW_IMPLICIT);

				holderInEditing = PlayerNameHolder.this;
			});

			ImageButton randomButton = itemView.findViewById(R.id.player_name_item_random_button);
			randomButton.setOnClickListener(v -> {
				String newName;
				do {
					newName = IOHelper.getRandomName();
				}while (mData.contains(newName));
				update(newName);
			});

			mNameEdit.setOnEditorActionListener((v, actionId, event) -> {
				if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
					endEditing();
				}
				return false;
			});
			mNameEdit.setOnFocusChangeListener((v, hasFocus) -> {
				if(!hasFocus){
					endEditing();
				}
			});
			KeyboardVisibilityEvent.setEventListener(getActivity(mNameEdit), new KeyboardVisibilityEventListener() {
				@Override
				public void onVisibilityChanged(boolean isOpen) {
					if(!isOpen){
						endEditing();
					}
				}
			});
		}

		private void endEditing(){
			if(holderInEditing == PlayerNameHolder.this){
				holderInEditing = null;
			}
			String newName = String.valueOf(mNameEdit.getText());

			update(newName);
			mNameEdit.clearFocus();
			mNameText.setVisibility(View.VISIBLE);
			mNameEdit.setVisibility(View.INVISIBLE);
			InputMethodManager imm = (InputMethodManager) CrocoApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mNameEdit.getWindowToken(), 0);

		}

		private Activity getActivity(View v) {
			Context context = v.getContext();
			while (context instanceof ContextWrapper) {
				if (context instanceof Activity) {
					return (Activity)context;
				}
				context = ((ContextWrapper)context).getBaseContext();
			}
			return null;
		}
		
		public void onDrag(){
			ValueAnimator animator = ValueAnimator.ofArgb(
					CrocoApplication.getContext().getColor(R.color.colorBackground),
					CrocoApplication.getContext().getColor(R.color.colorSelectedBackground));
			animator.setDuration(300);
			animator.addUpdateListener(animation -> {
				mBackground.setBackground(new ColorDrawable((Integer) animation.getAnimatedValue()));
			});
			animator.start();
		}

		public void onDragEnd(){
			ValueAnimator animator = ValueAnimator.ofArgb(
					CrocoApplication.getContext().getColor(R.color.colorSelectedBackground),
					CrocoApplication.getContext().getColor(R.color.colorBackground));
			animator.setDuration(300);
			animator.addUpdateListener(animation -> {
				mBackground.setBackground(new ColorDrawable((Integer) animation.getAnimatedValue()));
			});
			animator.start();
		}
		
		private void update(String newName){
			mData.set(mPosition, newName);
			mNameText.setText(newName);
			mNameEdit.setText(newName);
		}

		void fill(String name, int position){
			mNameText.setText(name);
			mNameEdit.setText(name);
			mPosition = position;
		}

		void setLast(boolean isLast){
			mBottomLine.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
		}
	}
}
