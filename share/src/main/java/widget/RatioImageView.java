package widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.imageview.ShapeableImageView;
import com.wind.collagePhotoMaker.share.R;

public class RatioImageView extends ShapeableImageView {
    // height/width
    protected float mRatio = 0f;

    public RatioImageView(Context context) {
        this(context, null, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RatioImageView, 0, defStyleAttr);
        try {
            String ratio = typedArray.getString(R.styleable.RatioImageView_riv_dimensionRatio);
            if (!TextUtils.isEmpty(ratio)) {
                String[] wh = ratio.split(":");
                if (wh.length == 2) {
                    mRatio = Float.parseFloat(wh[0]) / Float.parseFloat(wh[1]);
                }
            }
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == View.MeasureSpec.EXACTLY && heightMode != View.MeasureSpec.EXACTLY && mRatio != 0) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * mRatio);
            if (heightMode == View.MeasureSpec.AT_MOST) {
                height = Math.min(height, View.MeasureSpec.getSize(heightMeasureSpec));
            }
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * @param ratio: height/width
     */
    public void setRatio(float ratio) {
        mRatio = ratio;
        requestLayout();
    }
}