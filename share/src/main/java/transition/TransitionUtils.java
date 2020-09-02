package transition;

import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Phong Huynh on 9/2/2020
 */
class TransitionUtils {
    static View findAncestorById(View view, @IdRes int ancestorId) {
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

    static RectF getLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        return new RectF(left, top, right, bottom);
    }

    static float calculateArea(@NonNull RectF bounds) {
        return bounds.width() * bounds.height();
    }

    static int lerp(
            int startValue,
            int endValue,
            @FloatRange(from = 0.0, to = 1.0) float startFraction,
            @FloatRange(from = 0.0, to = 1.0) float endFraction,
            @FloatRange(from = 0.0, to = 1.0) float fraction) {
        if (fraction < startFraction) {
            return startValue;
        }
        if (fraction > endFraction) {
            return endValue;
        }
        return (int)
                lerp(startValue, endValue, (fraction - startFraction) / (endFraction - startFraction));
    }

    static float lerp(
            float startValue, float endValue, @FloatRange(from = 0.0, to = 1.0) float fraction) {
        return startValue + fraction * (endValue - startValue);
    }

    static float lerp(
            float startValue,
            float endValue,
            @FloatRange(from = 0.0, to = 1.0) float startFraction,
            @FloatRange(from = 0.0, to = 1.0) float endFraction,
            @FloatRange(from = 0.0, to = 1.0) float fraction) {
        if (fraction < startFraction) {
            return startValue;
        }
        if (fraction > endFraction) {
            return endValue;
        }

        return lerp(startValue, endValue, (fraction - startFraction) / (endFraction - startFraction));
    }

    @NonNull
    static <T> T defaultIfNull(@Nullable T value, @NonNull T defaultValue) {
        return value != null ? value : defaultValue;
    }

    static Shader createColorShader(@ColorInt int color) {
        return new LinearGradient(0, 0, 0, 0, color, color, Shader.TileMode.CLAMP);
    }
}
