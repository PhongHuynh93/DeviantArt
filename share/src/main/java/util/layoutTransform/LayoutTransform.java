package util.layoutTransform;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.ViewCompat;
import androidx.transition.PathMotion;

import com.google.android.material.internal.ViewUtils;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.transition.FitModeEvaluator;
import com.google.android.material.transition.MaterialContainerTransform;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.core.util.Preconditions.checkNotNull;

/**
 * Created by Phong Huynh on 9/1/2020
 */
public class LayoutTransform extends Transition {
    public static final String TAG = LayoutTransform.class.getSimpleName();
    private static final String PROP_BOUNDS = "layoutTransform:bounds";
    @IdRes
    private int drawingViewId = android.R.id.content;

    @RestrictTo(LIBRARY_GROUP)
    @IntDef({FADE_MODE_IN, FADE_MODE_OUT, FADE_MODE_CROSS, FADE_MODE_THROUGH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FadeMode {}

    @RestrictTo(LIBRARY_GROUP)
    @IntDef({FIT_MODE_AUTO, FIT_MODE_WIDTH, FIT_MODE_HEIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FitMode {}

    @FadeMode
    private int fadeMode = FADE_MODE_IN;
    @FitMode
    private int fitMode = FIT_MODE_AUTO;

    /**
     * Indicates that this transition should only fade in the incoming content, without changing the
     * opacity of the outgoing content.
     */
    public static final int FADE_MODE_IN = 0;

    /**
     * Indicates that this transition should only fade out the outgoing content, without changing the
     * opacity of the incoming content.
     */
    public static final int FADE_MODE_OUT = 1;

    /** Indicates that this transition should cross fade the outgoing and incoming content. */
    public static final int FADE_MODE_CROSS = 2;

    /**
     * Indicates that this transition should sequentially fade out the outgoing content and fade in
     * the incoming content.
     */
    public static final int FADE_MODE_THROUGH = 3;

    /**
     * Indicates that this transition should automatically choose whether to use {@link
     * #FIT_MODE_WIDTH} or {@link #FIT_MODE_HEIGHT}.
     */
    public static final int FIT_MODE_AUTO = 0;

    /**
     * Indicates that this transition should fit the incoming content to the width of the outgoing
     * content during the scale animation.
     */
    public static final int FIT_MODE_WIDTH = 1;

    /**
     * Indicates that this transition should fit the incoming content to the height of the outgoing
     * content during the scale animation.
     */
    public static final int FIT_MODE_HEIGHT = 2;

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        if (ViewCompat.isLaidOut(view) || view.getWidth() != 0 || view.getHeight() != 0) {
            // Capture location in screen co-ordinates
            RectF bounds = view.getParent() == null ? getRelativeBounds(view) : getLocationOnScreen(view);
            transitionValues.values.put(PROP_BOUNDS, bounds);
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        RectF startBounds = (RectF) startValues.values.get(PROP_BOUNDS);
        if (startBounds == null) {
            Log.w(TAG, "Skipping due to null start bounds. Ensure start view is laid out and measured.");
            return null;
        }
        RectF endBounds = (RectF) endValues.values.get(PROP_BOUNDS);
        if (endBounds == null) {
            Log.w(TAG, "Skipping due to null end bounds. Ensure end view is laid out and measured.");
            return null;
        }
        final View startView = startValues.view;
        final View endView = endValues.view;
        final View drawingView;
        View boundingView;
        View drawingBaseView = endView.getParent() != null ? endView : startView;
        if (drawingViewId == drawingBaseView.getId()) {
            drawingView = (View) drawingBaseView.getParent();
            boundingView = drawingBaseView;
        } else {
            drawingView = findAncestorById(drawingBaseView, drawingViewId);
            boundingView = null;
        }
        // Calculate drawable bounds and offset start/end bounds as needed
        RectF drawingViewBounds = getLocationOnScreen(drawingView);
        float offsetX = -drawingViewBounds.left;
        float offsetY = -drawingViewBounds.top;
        RectF drawableBounds = calculateDrawableBounds(drawingView, boundingView, offsetX, offsetY);
        startBounds.offset(offsetX, offsetY);
        endBounds.offset(offsetX, offsetY);

        boolean entering = isEntering(startBounds, endBounds);
        final TransitionDrawable transitionDrawable =
                new TransitionDrawable(
                        getPathMotion(),
                        startView,
                        startBounds,
                        getElevationOrDefault(startElevation, startView),
                        endView,
                        endBounds,
                        getElevationOrDefault(endElevation, endView),
                        containerColor,
                        startContainerColor,
                        endContainerColor,
                        scrimColor,
                        entering,
                        elevationShadowEnabled,
                        FadeModeEvaluators.get(fadeMode, entering),
                        FitModeEvaluators.get(fitMode, entering, startBounds, endBounds),
                        buildThresholdsGroup(entering),
                        drawDebugEnabled);
        // Set the bounds of the transition drawable to not exceed the bounds of the drawingView.
        transitionDrawable.setBounds(
                Math.round(drawableBounds.left),
                Math.round(drawableBounds.top),
                Math.round(drawableBounds.right),
                Math.round(drawableBounds.bottom));
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        transitionDrawable.setProgress(animation.getAnimatedFraction());
                    }
                });
        addListener(
                new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionStart(@NonNull Transition transition) {
                        // Add the transition drawable to the root ViewOverlay
                        ViewUtils.getOverlay(drawingView).add(transitionDrawable);

                        // Hide the actual views at the beginning of the transition
                        startView.setAlpha(0);
                        endView.setAlpha(0);
                    }

                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {
                        if (holdAtEndEnabled) {
                            // Keep drawable showing and views hidden (useful for Activity return transitions)
                            return;
                        }
                        // Show the actual views at the end of the transition
                        startView.setAlpha(1);
                        endView.setAlpha(1);

                        // Remove the transition drawable from the root ViewOverlay
                        ViewUtils.getOverlay(drawingView).remove(transitionDrawable);
                    }
                });

        return animator;
    }

    private static RectF getRelativeBounds(View view) {
        return new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    private static RectF getLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        return new RectF(left, top, right, bottom);
    }

    private static View findAncestorById(View view, @IdRes int ancestorId) {
        String resourceName = view.getResources().getResourceName(ancestorId);
        while (view != null) {
            if (view.getId() == ancestorId) {
                return view;
            }
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                view = (View) parent;
            } else {
                break;
            }
        }
        throw new IllegalArgumentException(resourceName + " is not a valid ancestor");
    }

    private static RectF calculateDrawableBounds(
            View drawingView, @Nullable View boundingView, float offsetX, float offsetY) {
        if (boundingView != null) {
            RectF drawableBounds = getLocationOnScreen(boundingView);
            drawableBounds.offset(offsetX, offsetY);
            return drawableBounds;
        } else {
            return new RectF(0, 0, drawingView.getWidth(), drawingView.getHeight());
        }
    }

    private boolean isEntering(@NonNull RectF startBounds, @NonNull RectF endBounds) {
        switch (transitionDirection) {
            case TRANSITION_DIRECTION_AUTO:
                return calculateArea(endBounds) > calculateArea(startBounds);
            case TRANSITION_DIRECTION_ENTER:
                return true;
            case TRANSITION_DIRECTION_RETURN:
                return false;
            default:
                throw new IllegalArgumentException("Invalid transition direction: " + transitionDirection);
        }
    }

    private static abstract class TransitionListenerAdapter implements TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {}

        @Override
        public void onTransitionEnd(Transition transition) {}

        @Override
        public void onTransitionCancel(Transition transition) {}

        @Override
        public void onTransitionPause(Transition transition) {}

        @Override
        public void onTransitionResume(Transition transition) {}
    }

    /**
     * A {@link Drawable} that is able to draw a point in a container transformation given a progress
     * between 0.0 and 1.0.
     */
    private static final class TransitionDrawable extends Drawable {
        // Start container
        private final View startView;
        private final RectF startBounds;
        private final float startElevation;
        // End container
        private final View endView;
        private final RectF endBounds;
        private final float endElevation;
        // Drawing
        private final boolean entering;
        private final boolean elevationShadowEnabled;
        private final MaterialShapeDrawable compatShadowDrawable = new MaterialShapeDrawable();
        private final RectF currentStartBounds;
        private final RectF currentStartBoundsMasked;
        private final RectF currentEndBounds;
        private final RectF currentEndBoundsMasked;
        private final MaterialContainerTransform.ProgressThresholdsGroup progressThresholds;
        private final FadeModeEvaluator fadeModeEvaluator;
        private final FitModeEvaluator fitModeEvaluator;
        // Debug drawing
        private final boolean drawDebugEnabled;
        // Paint
        private final Paint containerPaint = new Paint();
        private final Paint startContainerPaint = new Paint();
        private final Paint endContainerPaint = new Paint();
        private final Paint shadowPaint = new Paint();
        private final Paint scrimPaint = new Paint();
        // Drawing
        private final boolean entering;
        private final boolean elevationShadowEnabled;
        private final MaterialShapeDrawable compatShadowDrawable = new MaterialShapeDrawable();
        private final RectF currentStartBounds;
        private final RectF currentStartBoundsMasked;
        private final RectF currentEndBounds;
        private final RectF currentEndBoundsMasked;
        private final MaterialContainerTransform.ProgressThresholdsGroup progressThresholds;
        private final FadeModeEvaluator fadeModeEvaluator;
        private final FitModeEvaluator fitModeEvaluator;

        private TransitionDrawable(
                PathMotion pathMotion,
                View startView,
                RectF startBounds,
                float startElevation,
                View endView,
                RectF endBounds,
                float endElevation,
                @ColorInt int containerColor,
                @ColorInt int startContainerColor,
                @ColorInt int endContainerColor,
                int scrimColor,
                boolean entering,
                boolean elevationShadowEnabled,
                FadeModeEvaluator fadeModeEvaluator,
                FitModeEvaluator fitModeEvaluator,
                ProgressThresholdsGroup progressThresholds,
                boolean drawDebugEnabled) {
            this.startView = startView;
            this.startBounds = startBounds;
            this.startElevation = startElevation;
            this.endView = endView;
            this.endBounds = endBounds;
            this.endElevation = endElevation;
            this.entering = entering;
            this.elevationShadowEnabled = elevationShadowEnabled;
            this.fadeModeEvaluator = fadeModeEvaluator;
            this.fitModeEvaluator = fitModeEvaluator;
            this.progressThresholds = progressThresholds;
            this.drawDebugEnabled = drawDebugEnabled;

            containerPaint.setColor(containerColor);
            startContainerPaint.setColor(startContainerColor);
            endContainerPaint.setColor(endContainerColor);

            // Initializes calculations the drawable
            updateProgress(0);
        }

        private void updateProgress(float progress) {
            this.progress = progress;
            // Fade in/out scrim over non-shared elements
            scrimPaint.setAlpha((int) (entering ? lerp(0, 255, progress) : lerp(255, 0, progress)));

            // Calculate current elevation and set up shadow layer
            currentElevation = lerp(startElevation, endElevation, progress);
            shadowPaint.setShadowLayer(currentElevation, 0, currentElevation, SHADOW_COLOR);

            // Calculate position based on motion path
            motionPathMeasure.getPosTan(motionPathLength * progress, motionPathPosition, null);
            float motionPathX = motionPathPosition[0];
            float motionPathY = motionPathPosition[1];

            // Calculate current start and end bounds
            float scaleStartFraction = checkNotNull(progressThresholds.scale.start);
            float scaleEndFraction = checkNotNull(progressThresholds.scale.end);
            fitModeResult =
                    fitModeEvaluator.evaluate(
                            progress,
                            scaleStartFraction,
                            scaleEndFraction,
                            startBounds.width(),
                            startBounds.height(),
                            endBounds.width(),
                            endBounds.height());
            currentStartBounds.set(
                    motionPathX - fitModeResult.currentStartWidth / 2,
                    motionPathY,
                    motionPathX + fitModeResult.currentStartWidth / 2,
                    motionPathY + fitModeResult.currentStartHeight);
            currentEndBounds.set(
                    motionPathX - fitModeResult.currentEndWidth / 2,
                    motionPathY,
                    motionPathX + fitModeResult.currentEndWidth / 2,
                    motionPathY + fitModeResult.currentEndHeight);

            // Mask start or end bounds based on fit mode, over the duration of the fade
            currentStartBoundsMasked.set(currentStartBounds);
            currentEndBoundsMasked.set(currentEndBounds);
            float maskStartFraction = checkNotNull(progressThresholds.scaleMask.start);
            float maskEndFraction = checkNotNull(progressThresholds.scaleMask.end);
            boolean shouldMaskStartBounds = fitModeEvaluator.shouldMaskStartBounds(fitModeResult);
            RectF maskBounds = shouldMaskStartBounds ? currentStartBoundsMasked : currentEndBoundsMasked;
            float maskProgress = lerp(0f, 1f, maskStartFraction, maskEndFraction, progress);
            float maskMultiplier = shouldMaskStartBounds ? maskProgress : 1 - maskProgress;
            fitModeEvaluator.applyMask(maskBounds, maskMultiplier, fitModeResult);

            // Union start and end mask bounds
            currentMaskBounds =
                    new RectF(
                            Math.min(currentStartBoundsMasked.left, currentEndBoundsMasked.left),
                            Math.min(currentStartBoundsMasked.top, currentEndBoundsMasked.top),
                            Math.max(currentStartBoundsMasked.right, currentEndBoundsMasked.right),
                            Math.max(currentStartBoundsMasked.bottom, currentEndBoundsMasked.bottom));

            maskEvaluator.evaluate(
                    progress,
                    startShapeAppearanceModel,
                    endShapeAppearanceModel,
                    currentStartBounds,
                    currentStartBoundsMasked,
                    currentEndBoundsMasked,
                    progressThresholds.shapeMask);

            // Cross-fade images of the start/end states over range of `progress`
            float fadeStartFraction = checkNotNull(progressThresholds.fade.start);
            float fadeEndFraction = checkNotNull(progressThresholds.fade.end);
            fadeModeResult = fadeModeEvaluator.evaluate(progress, fadeStartFraction, fadeEndFraction);

            // Update the start and end container paints to share the same opacity as their respective
            // view.
            if (startContainerPaint.getColor() != Color.TRANSPARENT) {
                startContainerPaint.setAlpha(fadeModeResult.startAlpha);
            }
            if (endContainerPaint.getColor() != Color.TRANSPARENT) {
                endContainerPaint.setAlpha(fadeModeResult.endAlpha);
            }

            invalidateSelf();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (scrimPaint.getAlpha() > 0) {
                canvas.drawRect(getBounds(), scrimPaint);
            }

            int debugCanvasSave = drawDebugEnabled ? canvas.save() : -1;

            if (elevationShadowEnabled && currentElevation > 0) {
                drawElevationShadow(canvas);
            }

            // Clip the canvas to container's path. Anything drawn to the canvas after this clipping will
            // be masked inside the clipped area.
            maskEvaluator.clip(canvas);

            maybeDrawContainerColor(canvas, containerPaint);

            if (fadeModeResult.endOnTop) {
                drawStartView(canvas);
                drawEndView(canvas);
            } else {
                drawEndView(canvas);
                drawStartView(canvas);
            }

            if (drawDebugEnabled) {
                canvas.restoreToCount(debugCanvasSave);
                drawDebugCumulativePath(canvas, currentStartBounds, debugPath, Color.MAGENTA);
                drawDebugRect(canvas, currentStartBoundsMasked, Color.YELLOW);
                drawDebugRect(canvas, currentStartBounds, Color.GREEN);
                drawDebugRect(canvas, currentEndBoundsMasked, Color.CYAN);
                drawDebugRect(canvas, currentEndBounds, Color.BLUE);
            }
        }

        // Transform the canvas to the current bounds, scale and alpha before drawing the start view.
        private void drawStartView(Canvas canvas) {
            maybeDrawContainerColor(canvas, startContainerPaint);
            transform(
                    canvas,
                    getBounds(),
                    currentStartBounds.left,
                    currentStartBounds.top,
                    fitModeResult.startScale,
                    fadeModeResult.startAlpha,
                    new TransitionUtils.CanvasOperation() {
                        @Override
                        public void run(Canvas canvas) {
                            startView.draw(canvas);
                        }
                    });
        }

        // Transform the canvas to the current bounds, scale and alpha before drawing the end view.
        private void drawEndView(Canvas canvas) {
            maybeDrawContainerColor(canvas, endContainerPaint);
            transform(
                    canvas,
                    getBounds(),
                    currentEndBounds.left,
                    currentEndBounds.top,
                    fitModeResult.endScale,
                    fadeModeResult.endAlpha,
                    new TransitionUtils.CanvasOperation() {
                        @Override
                        public void run(Canvas canvas) {
                            endView.draw(canvas);
                        }
                    });
        }

        private void maybeDrawContainerColor(Canvas canvas, Paint containerPaint) {
            // Fill the container at the current layer with a color. Useful when the start or end view
            // does not have a background or when the container size exceeds the image size which it can
            // in large start/end size changes.
            if (containerPaint.getColor() != Color.TRANSPARENT && containerPaint.getAlpha() > 0) {
                canvas.drawRect(getBounds(), containerPaint);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            throw new UnsupportedOperationException("Setting alpha on is not supported");
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            throw new UnsupportedOperationException("Setting a color filter is not supported");
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        public void setProgress(float progress) {
            if (this.progress != progress) {
                updateProgress(progress);
            }
        }
    }

    /**
     * Helper method to translate, scale and set an alpha layer on a canvas, run any operations on the
     * transformed canvas and finally, restore the Canvas to it's original state.
     */
    static void transform(
            Canvas canvas, Rect bounds, float dx, float dy, float scale, int alpha, CanvasOperation op) {
        // Exit early and avoid drawing if what will be drawn is completely transparent.
        if (alpha <= 0) {
            return;
        }

        int checkpoint = canvas.save();
        canvas.translate(dx, dy);
        canvas.scale(scale, scale);
        if (alpha < 255) {
            saveLayerAlphaCompat(canvas, bounds, alpha);
        }
        op.run(canvas);
        canvas.restoreToCount(checkpoint);
    }

    interface CanvasOperation {
        void run(Canvas canvas);
    }

    private static int saveLayerAlphaCompat(Canvas canvas, Rect bounds, int alpha) {
        transformAlphaRectF.set(bounds);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            return canvas.saveLayerAlpha(transformAlphaRectF, alpha);
        } else {
            return canvas.saveLayerAlpha(
                    transformAlphaRectF.left,
                    transformAlphaRectF.top,
                    transformAlphaRectF.right,
                    transformAlphaRectF.bottom,
                    alpha,
                    Canvas.ALL_SAVE_FLAG);
        }
    }
}
