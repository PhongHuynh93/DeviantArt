package transition;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.transition.ArcMotion;
import android.transition.PathMotion;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.ViewCompat;

import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.wind.collagePhotoMaker.share.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.core.util.Preconditions.checkNotNull;
import static com.google.android.material.transition.platform.TransitionUtils.lerp;
import static transition.TransitionUtils.calculateArea;
import static transition.TransitionUtils.createColorShader;
import static transition.TransitionUtils.defaultIfNull;
import static transition.TransitionUtils.findAncestorById;
import static transition.TransitionUtils.getLocationOnScreen;


/**
 * Created by Phong Huynh on 9/2/2020
 */
public class LayoutTransform extends Transition {
    private static final String PROP_BOUNDS = "LayoutTransform:bounds";
    @IdRes
    private int drawingViewId = android.R.id.content;
    /**
     * Indicates that this transition should use automatic detection to determine whether it is an
     * Enter or a Return. If the end container has a larger area than the start container then it is
     * considered an Enter transition, otherwise it is a Return transition.
     */
    public static final int TRANSITION_DIRECTION_AUTO = 0;

    /**
     * Indicates that this is an Enter transition, i.e., when elements are entering the scene.
     */
    public static final int TRANSITION_DIRECTION_ENTER = 1;

    /**
     * Indicates that this is a Return transition, i.e., when elements are exiting the scene.
     */
    public static final int TRANSITION_DIRECTION_RETURN = 2;

    @RestrictTo(LIBRARY_GROUP)
    @IntDef({TRANSITION_DIRECTION_AUTO, TRANSITION_DIRECTION_ENTER, TRANSITION_DIRECTION_RETURN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TransitionDirection {
    }

    @TransitionDirection
    private int transitionDirection = TRANSITION_DIRECTION_AUTO;
    @ColorInt
    private int containerColor = Color.TRANSPARENT;
    @ColorInt
    private int startContainerColor = Color.TRANSPARENT;
    @ColorInt
    private int endContainerColor = Color.TRANSPARENT;
    @ColorInt
    private int scrimColor = 0x52000000;

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

    /**
     * Indicates that this transition should fit the incoming content to the height of the outgoing
     * content during the scale animation.
     */
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

    /**
     * Indicates that this transition should cross fade the outgoing and incoming content.
     */
    public static final int FADE_MODE_CROSS = 2;

    /**
     * Indicates that this transition should sequentially fade out the outgoing content and fade in
     * the incoming content.
     */
    public static final int FADE_MODE_THROUGH = 3;

    @RestrictTo(LIBRARY_GROUP)
    @IntDef({FADE_MODE_IN, FADE_MODE_OUT, FADE_MODE_CROSS, FADE_MODE_THROUGH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FadeMode {
    }

    @RestrictTo(LIBRARY_GROUP)
    @IntDef({FIT_MODE_AUTO, FIT_MODE_WIDTH, FIT_MODE_HEIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FitMode {
    }

    @FadeMode
    private int fadeMode = FADE_MODE_IN;
    @FitMode
    private int fitMode = FIT_MODE_AUTO;


    // Default animation thresholds. Will be used by default when the default linear PathMotion is
    // being used or when no other progress thresholds are appropriate (e.g., the arc thresholds for
    // an arc path).
    private static final ProgressThresholdsGroup DEFAULT_ENTER_THRESHOLDS =
            new ProgressThresholdsGroup(
                    /* fade= */ new ProgressThresholds(0f, 0.25f),
                    /* scale= */ new ProgressThresholds(0f, 1f),
                    /* scaleMask= */ new ProgressThresholds(0f, 1f),
                    /* shapeMask= */ new ProgressThresholds(0f, 0.75f));
    private static final ProgressThresholdsGroup DEFAULT_RETURN_THRESHOLDS =
            new ProgressThresholdsGroup(
                    /* fade= */ new ProgressThresholds(0.60f, 0.90f),
                    /* scale= */ new ProgressThresholds(0f, 1f),
                    /* scaleMask= */ new ProgressThresholds(0f, 0.90f),
                    /* shapeMask= */ new ProgressThresholds(0.30f, 0.90f));

    // Default animation thresholds for an arc path. Will be used by default when the PathMotion is
    // set to ArcMotion or MaterialArcMotion.
    private static final ProgressThresholdsGroup DEFAULT_ENTER_THRESHOLDS_ARC =
            new ProgressThresholdsGroup(
                    /* fade= */ new ProgressThresholds(0.10f, 0.40f),
                    /* scale= */ new ProgressThresholds(0.10f, 1f),
                    /* scaleMask= */ new ProgressThresholds(0.10f, 1f),
                    /* shapeMask= */ new ProgressThresholds(0.10f, 0.90f));
    private static final ProgressThresholdsGroup DEFAULT_RETURN_THRESHOLDS_ARC =
            new ProgressThresholdsGroup(
                    /* fade= */ new ProgressThresholds(0.60f, 0.90f),
                    /* scale= */ new ProgressThresholds(0f, 0.90f),
                    /* scaleMask= */ new ProgressThresholds(0f, 0.90f),
                    /* shapeMask= */ new ProgressThresholds(0.20f, 0.90f));


    private boolean holdAtEndEnabled = false;
    @Nullable private ProgressThresholds fadeProgressThresholds;
    @Nullable private ProgressThresholds scaleProgressThresholds;
    @Nullable private ProgressThresholds scaleMaskProgressThresholds;
    @Nullable private ProgressThresholds shapeMaskProgressThresholds;

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
            RectF bounds = (RectF) view.getTag(R.id.tagBoundLayoutTransform);
            transitionValues.values.put(PROP_BOUNDS, bounds);
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        RectF startBounds = (RectF) startValues.values.get(PROP_BOUNDS);
        RectF endBounds = (RectF) endValues.values.get(PROP_BOUNDS);

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
        final TransitionDrawable transitionDrawable = new TransitionDrawable(
                getPathMotion(),
                startView,
                startBounds,
                endView,
                endBounds,
                containerColor,
                startContainerColor,
                endContainerColor,
                scrimColor,
                entering,
                FadeModeEvaluators.get(fadeMode, entering),
                FitModeEvaluators.get(fitMode, entering, startBounds, endBounds),
                buildThresholdsGroup(entering)
        );
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
                new TransitionListener() {
                    @Override
                    public void onTransitionStart(@NonNull Transition transition) {
                        // Add the transition drawable to the root ViewOverlay
                        drawingView.getOverlay().add(transitionDrawable);

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
                        drawingView.getOverlay().remove(transitionDrawable);
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }
                });

        return animator;
    }


    private ProgressThresholdsGroup buildThresholdsGroup(boolean entering) {
        PathMotion pathMotion = getPathMotion();
        if (pathMotion instanceof ArcMotion || pathMotion instanceof MaterialArcMotion) {
            return getThresholdsOrDefault(
                    entering, DEFAULT_ENTER_THRESHOLDS_ARC, DEFAULT_RETURN_THRESHOLDS_ARC);
        } else {
            return getThresholdsOrDefault(entering, DEFAULT_ENTER_THRESHOLDS, DEFAULT_RETURN_THRESHOLDS);
        }
    }

    private ProgressThresholdsGroup getThresholdsOrDefault(
            boolean entering,
            ProgressThresholdsGroup defaultEnterThresholds,
            ProgressThresholdsGroup defaultReturnThresholds) {
        ProgressThresholdsGroup defaultThresholds =
                entering ? defaultEnterThresholds : defaultReturnThresholds;
        return new ProgressThresholdsGroup(
                defaultIfNull(fadeProgressThresholds, defaultThresholds.fade),
                defaultIfNull(scaleProgressThresholds, defaultThresholds.scale),
                defaultIfNull(scaleMaskProgressThresholds, defaultThresholds.scaleMask),
                defaultIfNull(shapeMaskProgressThresholds, defaultThresholds.shapeMask));
    }

    /**
     * Whether to hold the last frame at the end of the animation.
     *
     * @see #setHoldAtEndEnabled(boolean)
     */
    public boolean isHoldAtEndEnabled() {
        return holdAtEndEnabled;
    }

    /**
     * If true, the last frame of the animation will be held in place, and the original outgoing and
     * incoming views will not be re-shown.
     *
     * <p>Useful for Activity return transitions to make sure the screen doesn't flash at the end.
     */
    public void setHoldAtEndEnabled(boolean holdAtEndEnabled) {
        this.holdAtEndEnabled = holdAtEndEnabled;
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


    private static final class TransitionDrawable extends Drawable {
        // Start container
        private final View startView;
        private final RectF startBounds;

        // End container
        private final View endView;
        private final RectF endBounds;

        // Paint
        private final Paint containerPaint = new Paint();
        private final Paint startContainerPaint = new Paint();
        private final Paint endContainerPaint = new Paint();
        private final Paint scrimPaint = new Paint();

        // Motion path
        private final MaskEvaluator maskEvaluator = new MaskEvaluator();
        private final PathMeasure motionPathMeasure;
        private final float motionPathLength;
        private final float[] motionPathPosition = new float[2];

        // Drawing
        private final boolean entering;
        private final RectF currentStartBounds;
        private final RectF currentStartBoundsMasked;
        private final RectF currentEndBounds;
        private final RectF currentEndBoundsMasked;
        private final ProgressThresholdsGroup progressThresholds;
        private final FadeModeEvaluator fadeModeEvaluator;
        private final FitModeEvaluator fitModeEvaluator;

        // Current progress calculations
        private FadeModeResult fadeModeResult;
        private FitModeResult fitModeResult;
        private RectF currentMaskBounds;
        private float progress;

        public TransitionDrawable(PathMotion pathMotion, View startView, RectF startBounds, View endView, RectF endBounds, int containerColor, int startContainerColor, int endContainerColor, int scrimColor, boolean entering, FadeModeEvaluator fadeModeEvaluator, FitModeEvaluator fitModeEvaluator, ProgressThresholdsGroup progressThresholds) {
            this.startView = startView;
            this.startBounds = startBounds;
            this.endView = endView;
            this.endBounds = endBounds;
            this.entering = entering;
            this.fadeModeEvaluator = fadeModeEvaluator;
            this.fitModeEvaluator = fitModeEvaluator;
            this.progressThresholds = progressThresholds;

            containerPaint.setColor(containerColor);
            startContainerPaint.setColor(startContainerColor);
            endContainerPaint.setColor(endContainerColor);

            currentStartBounds = new RectF(startBounds);
            currentStartBoundsMasked = new RectF(currentStartBounds);
            currentEndBounds = new RectF(currentStartBounds);
            currentEndBoundsMasked = new RectF(currentEndBounds);

            // Calculate motion path
            PointF startPoint = getMotionPathPoint(startBounds);
            PointF endPoint = getMotionPathPoint(endBounds);
            Path motionPath = pathMotion.getPath(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
            motionPathMeasure = new PathMeasure(motionPath, false);
            motionPathLength = motionPathMeasure.getLength();
            // Fill the motion path with default positions in case a zero-length path is specified which
            // causes motionPathMeasure.getPosTan to skip filling this int array.
            // A zero-length path happens when the startBounds are top aligned and horizontally centered
            // on the endBounds.
            motionPathPosition[0] = startBounds.centerX();
            motionPathPosition[1] = startBounds.top;

            scrimPaint.setStyle(Paint.Style.FILL);
            scrimPaint.setShader(createColorShader(scrimColor));

            // Initializes calculations the drawable
            updateProgress(0);
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

        private static PointF getMotionPathPoint(RectF bounds) {
            return new PointF(bounds.centerX(), bounds.top);
        }
    }

    private static class ProgressThresholdsGroup {
        @NonNull
        private final ProgressThresholds fade;
        @NonNull
        private final ProgressThresholds scale;
        @NonNull
        private final ProgressThresholds scaleMask;
        @NonNull
        private final ProgressThresholds shapeMask;

        private ProgressThresholdsGroup(
                @NonNull ProgressThresholds fade,
                @NonNull ProgressThresholds scale,
                @NonNull ProgressThresholds scaleMask,
                @NonNull ProgressThresholds shapeMask) {
            this.fade = fade;
            this.scale = scale;
            this.scaleMask = scaleMask;
            this.shapeMask = shapeMask;
        }
    }

    /**
     * A class which holds a start and end value which represent a range within 0.0 - 1.0.
     *
     * <p>This class is used to define the period, or sub-range, over which a child animation is run
     * inside a parent animation's overall 0.0 - 1.0 progress.
     *
     * <p>For example, setting the fade thresholds to a range of 0.3 - 0.6 would mean that for the
     * first 30% of the animation, the start view would be fully opaque and the end view would be
     * fully transparent. Then, the fade would begin at the 30% point of the animation and complete at
     * the 60% point. For the remainder of the animation after the 60% point, the start view would be
     * fully transparent and the end view would be fully opaque.
     */
    public static class ProgressThresholds {
        @FloatRange(from = 0.0, to = 1.0)
        private final float start;

        @FloatRange(from = 0.0, to = 1.0)
        private final float end;

        public ProgressThresholds(
                @FloatRange(from = 0.0, to = 1.0) float start,
                @FloatRange(from = 0.0, to = 1.0) float end) {
            this.start = start;
            this.end = end;
        }

        @FloatRange(from = 0.0, to = 1.0)
        public float getStart() {
            return start;
        }

        @FloatRange(from = 0.0, to = 1.0)
        public float getEnd() {
            return end;
        }
    }
}
