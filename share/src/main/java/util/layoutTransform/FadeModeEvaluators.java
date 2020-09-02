package util.layoutTransform;

import static util.layoutTransform.LayoutTransform.FADE_MODE_CROSS;
import static util.layoutTransform.LayoutTransform.FADE_MODE_IN;
import static util.layoutTransform.LayoutTransform.FADE_MODE_OUT;
import static util.layoutTransform.LayoutTransform.FADE_MODE_THROUGH;
import static util.layoutTransform.TransitionUtils.lerp;

/**
 * Created by Phong Huynh on 9/2/2020
 */
class FadeModeEvaluators {
    private static final FadeModeEvaluator IN = new FadeModeEvaluator() {
        @Override
        public FadeModeResult evaluate(float progress, float fadeStartFraction, float fadeEndFraction) {
            int startAlpha = 255;
            int endAlpha = lerp(0, 255, fadeStartFraction, fadeEndFraction, progress);
            return FadeModeResult.endOnTop(startAlpha, endAlpha);
        }
    };


    private static final FadeModeEvaluator OUT =
            new FadeModeEvaluator() {
                @Override
                public FadeModeResult evaluate(
                        float progress, float fadeStartFraction, float fadeEndFraction) {
                    int startAlpha = TransitionUtils.lerp(255, 0, fadeStartFraction, fadeEndFraction, progress);
                    int endAlpha = 255;
                    return FadeModeResult.startOnTop(startAlpha, endAlpha);
                }
            };

    private static final FadeModeEvaluator CROSS =
            new FadeModeEvaluator() {
                @Override
                public FadeModeResult evaluate(
                        float progress, float fadeStartFraction, float fadeEndFraction) {
                    int startAlpha = TransitionUtils.lerp(255, 0, fadeStartFraction, fadeEndFraction, progress);
                    int endAlpha = TransitionUtils.lerp(0, 255, fadeStartFraction, fadeEndFraction, progress);
                    return FadeModeResult.startOnTop(startAlpha, endAlpha);
                }
            };

    private static final FadeModeEvaluator THROUGH =
            new FadeModeEvaluator() {
                @Override
                public FadeModeResult evaluate(
                        float progress, float fadeStartFraction, float fadeEndFraction) {
                    float fadeFractionDiff = fadeEndFraction - fadeStartFraction;
                    float fadeFractionThreshold =
                            fadeStartFraction + fadeFractionDiff * FadeThroughProvider.PROGRESS_THRESHOLD;
                    int startAlpha = TransitionUtils.lerp(255, 0, fadeStartFraction, fadeFractionThreshold, progress);
                    int endAlpha = TransitionUtils.lerp(0, 255, fadeFractionThreshold, fadeEndFraction, progress);
                    return FadeModeResult.startOnTop(startAlpha, endAlpha);
                }
            };

    static FadeModeEvaluator get(@LayoutTransform.FadeMode int fadeMode, boolean entering) {
        switch (fadeMode) {
            case FADE_MODE_IN:
                return entering ? IN : OUT;
            case FADE_MODE_OUT:
                return entering ? OUT : IN;
            case FADE_MODE_CROSS:
                return CROSS;
            case FADE_MODE_THROUGH:
                return THROUGH;
            default:
                throw new IllegalArgumentException("Invalid fade mode: " + fadeMode);
        }
    }

    private FadeModeEvaluators() {}
}
