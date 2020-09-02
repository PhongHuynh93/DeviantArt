package util.layoutTransform;

import android.graphics.RectF;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Phong Huynh on 9/2/2020
 */
class TransitionUtils {

    @NonNull
    static <T> T defaultIfNull(@Nullable T value, @NonNull T defaultValue) {
        return value != null ? value : defaultValue;
    }

    static float calculateArea(@NonNull RectF bounds) {
        return bounds.width() * bounds.height();
    }

    static float lerp(
            float startValue, float endValue, @FloatRange(from = 0.0, to = 1.0) float fraction) {
        return startValue + fraction * (endValue - startValue);
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
}
