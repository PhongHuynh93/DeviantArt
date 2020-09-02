package transition;

interface FadeModeEvaluator {
  /** Calculate the current start and end view sizes and scales depending on the fit mode. */
  FadeModeResult evaluate(float progress, float fadeStartFraction, float fadeEndFraction);
}
