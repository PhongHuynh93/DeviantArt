package util.layoutTransform;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Phong Huynh on 9/2/2020
 * A class that configures and is able to provide an {@link Animator} that fades out or in a view.
 *
 * <p>FadeThroughProvider differs from FadeProvider in that it fades out and in views sequentially.
 */
final class FadeThroughProvider implements VisibilityAnimatorProvider {
    static final float PROGRESS_THRESHOLD = 0.35f;

    @Nullable
    @Override
    public Animator createAppear(@NonNull ViewGroup sceneRoot, @NonNull View view) {
        return createFadeThroughAnimator(
                view,
                /* startValue= */ 0f,
                /* endValue= */ 1f,
                /* startFraction= */ PROGRESS_THRESHOLD,
                /* endFraction= */ 1f);
    }

    @Nullable
    @Override
    public Animator createDisappear(@NonNull ViewGroup sceneRoot, @NonNull View view) {
        return createFadeThroughAnimator(
                view,
                /* startValue= */ 1f,
                /* endValue= */ 0f,
                /* startFraction= */ 0f,
                /* endFraction= */ PROGRESS_THRESHOLD);
    }

    private static Animator createFadeThroughAnimator(
            final View view,
            final float startValue,
            final float endValue,
            final @FloatRange(from = 0.0, to = 1.0) float startFraction,
            final @FloatRange(from = 0.0, to = 1.0) float endFraction) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();
                        view.setAlpha(TransitionUtils.lerp(startValue, endValue, startFraction, endFraction, progress));
                    }
                });
        return animator;
    }



}
