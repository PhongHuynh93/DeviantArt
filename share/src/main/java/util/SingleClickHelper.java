package util;

/**
 * Created by phuong on 4/11/19.
 */
public class SingleClickHelper {
    private static final long MIN_CLICK_INTERVAL = 500;

    private long mMinClickInterval;
    private long mLastClickTime = 0;

    public SingleClickHelper() {
        mMinClickInterval = MIN_CLICK_INTERVAL;
    }

    public SingleClickHelper(long minInterval) {
        mMinClickInterval = minInterval;
    }

    /**
     * must call every time onClick(View v) is called
     * @return false if spam click, true for true click
     */
    public boolean isTrueClick() {
        final long currentClickTime = System.currentTimeMillis();
        final long elapsedTime = currentClickTime - mLastClickTime;
        if (elapsedTime >= 0 && elapsedTime <= mMinClickInterval) {
            return false;
        }
        mLastClickTime = currentClickTime;
        return true;
    }

    public boolean isFalseClick() {
        return !isTrueClick();
    }
}
