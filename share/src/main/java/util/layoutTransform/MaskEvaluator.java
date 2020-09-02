package util.layoutTransform;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;

/**
 * Created by Phong Huynh on 9/2/2020
 *
 * A class which is responsible calculating the path which represents a container transform's
 * transforming container based on a progress between 0 and 1 as well as clipping a canvas to that
 * given path.
 */
class MaskEvaluator {
    private final Path path = new Path();
    private final Path startPath = new Path();
    private final Path endPath = new Path();
    private final ShapeAppearancePathProvider pathProvider = new ShapeAppearancePathProvider();

    /** Update the mask used by this evaluator based on a given progress. */
    void evaluate(
            float progress,
            RectF currentStartBounds,
            RectF currentStartBoundsMasked,
            RectF currentEndBoundsMasked,
            LayoutTransform.ProgressThresholds shapeMaskThresholds) {

        // Animate shape appearance corner changes over range of `progress` & use this when
        // drawing the container background & images
        float shapeStartFraction = shapeMaskThresholds.getStart();
        float shapeEndFraction = shapeMaskThresholds.getEnd();

        pathProvider.calculatePath(currentShapeAppearanceModel, 1, currentStartBoundsMasked, startPath);
        pathProvider.calculatePath(currentShapeAppearanceModel, 1, currentEndBoundsMasked, endPath);

        // Union the two paths on API 23 and above. API 21 and 22 have problems with this
        // call and instead use the start and end paths to clip.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            path.op(startPath, endPath, Path.Op.UNION);
        }
    }

    /** Clip the given Canvas to the mask held by this evaluator. */
    void clip(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            canvas.clipPath(path);
        } else {
            canvas.clipPath(startPath);
            canvas.clipPath(endPath, Region.Op.UNION);
        }
    }

    Path getPath() {
        return path;
    }

    ShapeAppearanceModel getCurrentShapeAppearanceModel() {
        return currentShapeAppearanceModel;
    }
}
