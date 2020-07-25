package util;

import android.view.View;

/**
 * Created by Tron Nguyen on 12/2/2019.
 */
public abstract class SingleLongClickListener implements View.OnLongClickListener {
    public abstract boolean onSingleLongClick(View v);

    private SingleClickHelper mSingleClickHelper;

    public SingleLongClickListener() {
        mSingleClickHelper = new SingleClickHelper();
    }

    @Override
    public final boolean onLongClick(View v) {
        if (mSingleClickHelper.isTrueClick()) {
            onSingleLongClick(v);
            return true;
        }
        return false;
    }
}
