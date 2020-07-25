package util;

import android.view.View;

/**
 * Created by Phong Huynh on 7/2/2018.
 */
public abstract class SingleClickListener implements View.OnClickListener {
    public abstract void onSingleClick(View v);

    private SingleClickHelper mSingleClickHelper;

    public SingleClickListener() {
        mSingleClickHelper = new SingleClickHelper();
    }

    @Override
    public final void onClick(View v) {
        if (mSingleClickHelper.isTrueClick()) {
            onSingleClick(v);
        }
    }
}
