package util.layoutTransform;

/**
 * Created by Phong Huynh on 9/1/2020
 */
interface FadeModeEvaluator {
  /** Calculate the current start and end view sizes and scales depending on the fit mode. */
  FadeModeResult evaluate(float progress, float fadeStartFraction, float fadeEndFraction);
}
