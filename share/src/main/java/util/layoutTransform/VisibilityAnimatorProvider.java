package util.layoutTransform;

/**
 * Created by Phong Huynh on 9/2/2020
 */

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * An interface which is able to provide an Animator to be supplied to a {@link
 * androidx.transition.Visibility} transition when a target view is appearing or disappearing.
 */
interface VisibilityAnimatorProvider {

    /**
     * Should return an Animator that animates in the appearing target {@code view}.
     *
     * @param sceneRoot The root of the transition hierarchy, which can be useful for checking
     *     configurations such as RTL
     * @param view The view that is appearing
     */
    @Nullable
    Animator createAppear(@NonNull ViewGroup sceneRoot, @NonNull View view);

    /**
     * Should return an Animator that animates out the disappearing target {@code view}.
     *
     * @param sceneRoot The root of the transition hierarchy, which can be useful for checking
     *     configurations such as RTL
     * @param view The view that is disappearing
     */
    @Nullable
    Animator createDisappear(@NonNull ViewGroup sceneRoot, @NonNull View view);
}