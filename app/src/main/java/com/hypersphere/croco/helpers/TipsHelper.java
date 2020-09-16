package com.hypersphere.croco.helpers;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.hypersphere.croco.R;

import java.util.Iterator;
import java.util.List;

public class TipsHelper {

	private static ShowcaseView.Builder getBaseBuilder(Activity activity) {
		Button goneButton = new Button(activity);
		goneButton.setVisibility(View.GONE);

		return new ShowcaseView.Builder(activity)
				.withNewStyleShowcase()
				.setStyle(R.style.Widget_Croco_Showcase)
				.replaceEndButton(goneButton)
				.hideOnTouchOutside();
	}

	public static void showTip(Activity activity, Tip tip, boolean single) {
		ShowcaseView.Builder builder = getBaseBuilder(activity);
		builder.setTarget(new ViewTarget(tip.viewId, activity))
				.setContentTitle(tip.title)
				.setContentText(tip.text);

		if (single) builder.singleShot(tip.viewId);

		builder.build();
	}

	public static class Tip {
		String title;
		String text;
		int viewId;

		public Tip(String title, String text, int viewId) {
			this.title = title;
			this.text = text;
			this.viewId = viewId;
		}
	}

	public static void showTips(Activity activity, Iterator<Tip> tipsIterator, boolean single) {
		if (tipsIterator.hasNext()) {
			Tip tip = tipsIterator.next();
			ShowcaseView.Builder builder = getBaseBuilder(activity);
			builder.setTarget(new ViewTarget(tip.viewId, activity))
					.setContentTitle(tip.title)
					.setContentText(tip.text);

			if (single) builder.singleShot(tip.viewId);

			builder.setShowcaseEventListener(new OnShowcaseEventListener() {
				@Override
				public void onShowcaseViewHide(ShowcaseView showcaseView) {
					showTips(activity, tipsIterator, single);
				}

				@Override
				public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
				}

				@Override
				public void onShowcaseViewShow(ShowcaseView showcaseView) {
				}

				@Override
				public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
				}
			});

			builder.build();
		}
	}
}
