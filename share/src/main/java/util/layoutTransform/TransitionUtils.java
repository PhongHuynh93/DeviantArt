package util.layoutTransform;

import androidx.annotation.FloatRange;

/**
 * Created by Phong Huynh on 9/2/2020
 */
class TransitionUtils {
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
