package com.hypersphere.croco.helpers;

import android.app.Activity;

import java.util.Iterator;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

public class TipsHelper {


	private static GuideView.Builder getBaseBuilder(Activity activity) {

		return new GuideView.Builder(activity)
				.setGravity(Gravity.auto)
				.setDismissType(DismissType.anywhere);
	}



	/*
	public static void showTip(Activity activity, Tip tip, boolean single) {
		ShowcaseView.Builder builder = getBaseBuilder(activity);
		builder.setTarget(new ViewTarget(tip.viewId, activity))
				.setContentTitle(tip.title)
				.setContentText(tip.text);

		if (single) builder.singleShot(tip.viewId);

		builder.build();
	}

	 */

	public static class Tip {
		String title;
		String text;
		int viewId;

		public Tip(String title, String text, int viewId) {
			this.title = title;
			this.text = text;
			this.viewId = viewId;
		}

		/**
		 * Returns hashcode of string {@code title + "~" + text + X} where title and text are
		 * {@link Tip} fields, X is 'theoretical symbol' with hashcode = viewId. We assume that
		 * title and text do not contain symbol "~".
		 * @return hashCode of {@link Tip}.
		 */
		@Override
		public int hashCode() {
			return (title + "~" + text).hashCode() * 31 + viewId;
		}
	}

	public static void showTips(Activity activity, Iterator<Tip> tipsIterator) {
		if (!tipsIterator.hasNext()){
			SettingsHelper.setTipsHaveShown(true);
			return;
		}
		if(SettingsHelper.getHaveTipsShown())
			return;

		Tip tip = tipsIterator.next();

		GuideView.Builder builder = getBaseBuilder(activity);
		builder.setTitle(tip.title)
				.setContentText(tip.text)
				.setTargetView(activity.findViewById(tip.viewId));
		// TODO: 02.12.2020 show once
		//if (single)

		builder.setGuideListener(view -> showTips(activity, tipsIterator));

		builder
				.build()
				.show();

	}
}
