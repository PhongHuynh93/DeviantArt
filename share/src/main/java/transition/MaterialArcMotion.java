package transition;

import android.graphics.Path;
import android.graphics.PointF;
import android.transition.PathMotion;

import androidx.annotation.NonNull;

/**
 * A Material {@link PathMotion} that results in a more dramatic curve than {@link
 * android.transition.ArcMotion}.
 *
 * <p>Use MaterialArcMotion in conjunction with {@link LayoutTransform} via {@link
 * LayoutTransform#setPathMotion(PathMotion)} to have the container move along a curved
 * path from its start position to its end position.
 */
@androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.LOLLIPOP)
public final class MaterialArcMotion extends PathMotion {

  @NonNull
  @Override
  public Path getPath(float startX, float startY, float endX, float endY) {
    Path path = new Path();
    path.moveTo(startX, startY);

    PointF controlPoint = getControlPoint(startX, startY, endX, endY);
    path.quadTo(controlPoint.x, controlPoint.y, endX, endY);
    return path;
  }

  private static PointF getControlPoint(float startX, float startY, float endX, float endY) {
    if (startY > endY) {
      return new PointF(endX, startY);
    } else {
      return new PointF(startX, endY);
    }
  }
}
