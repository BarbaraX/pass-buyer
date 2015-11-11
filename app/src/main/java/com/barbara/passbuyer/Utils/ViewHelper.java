package com.barbara.passbuyer.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * Created by barbara on 7/2/15.
 */
public class ViewHelper {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void switchShowedView(Context context, final View fromView, final  View toView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            fromView.setVisibility(View.GONE);
            fromView.animate().setDuration(shortAnimTime).alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fromView.setVisibility(View.GONE);
                }
            });

            toView.setVisibility(View.VISIBLE);
            toView.animate().setDuration(shortAnimTime).alpha(1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    toView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            fromView.setVisibility(View.GONE);
            toView.setVisibility(View.VISIBLE);
        }
    }
}
