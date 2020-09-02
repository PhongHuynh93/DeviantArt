package transition;

import android.graphics.RectF;

interface FitModeEvaluator {

  /** Calculate the current start and end view sizes and scales depending on the fit mode. */
  FitModeResult evaluate(
          float progress,
          float scaleStartFraction,
          float scaleEndFraction,
          float startWidth,
          float startHeight,
          float endWidth,
          float endHeight);

  /**
   * Determine whether the start or end view should be masked. For example, if fitting to width and
   * the end view is proportionally taller than the start view, then this method should return false
   * so that the end view is masked to create the reveal effect.
   */
  boolean shouldMaskStartBounds(FitModeResult fitModeResult);

  /** Update the mask bounds to create the reveal effect. */
  void applyMask(RectF maskBounds, float maskMultiplier, FitModeResult fitModeResult);
}
