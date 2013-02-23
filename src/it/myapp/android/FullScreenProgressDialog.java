package it.myapp.android;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class FullScreenProgressDialog extends Dialog {

	public FullScreenProgressDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		setCancelable(false);
		setContentView(R.layout.progress_dialog);
	}

	public static Dialog show(Context context) {
		FullScreenProgressDialog toRet = new FullScreenProgressDialog(context);
		toRet.show();
		return toRet;
	}

}