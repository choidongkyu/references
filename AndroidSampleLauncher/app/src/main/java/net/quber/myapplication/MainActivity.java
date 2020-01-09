/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package net.quber.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import net.quber.myapplication.ui.fragment.CustomHeadersFragment;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity implements CustomFrameLayout.OnFocusListener {

    private static final String TAG = "MainActivity";

    private FragmentManager mFragmentManager;
    private boolean mNavigationDrawerOpen = false;
    private static final float NAVIGATION_DRAWER_SCALE_FACTOR = 0.9f;

    @Override
    public void onRequestFocus(View child, View focused) {
        if (child.getId() == R.id.header_container) {
            toggleHeadersFragment(true);
        } else if (child.getId() == R.id.rows_container) {
            toggleHeadersFragment(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "getrequested orientation = " + getRequestedOrientation());
        CustomHeadersFragment headersFragment = new CustomHeadersFragment();
        CustomFrameLayout customFrameLayout = findViewById(R.id.main_activity);
        customFrameLayout.setOnFocusListener(this);
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.header_container, headersFragment, "CustomHeadersFragment");
        transaction.commit();
    }

    public FragmentManager getfragmentManager() {
        return mFragmentManager;
    }

    public synchronized void toggleHeadersFragment(final boolean doOpen) {
        boolean condition = (doOpen ? !mNavigationDrawerOpen : mNavigationDrawerOpen);

        if (condition) {
            final View headersContainer = findViewById(R.id.header_container);
            final View rowsContainer = findViewById(R.id.rows_container);

            final float delta = headersContainer.getWidth() * NAVIGATION_DRAWER_SCALE_FACTOR;

            // get current margin (a previous animation might have been interrupted)
            final int currentHeadersMargin = (((ViewGroup.MarginLayoutParams) headersContainer.getLayoutParams()).leftMargin);
            final int currentRowsMargin = (((ViewGroup.MarginLayoutParams) rowsContainer.getLayoutParams()).leftMargin);
            // calculate destination
            final int headersDestination = (doOpen ? 0 : (int) (0 - delta));
            final int rowsDestination = (doOpen ? (Utils.convertDpToPixel(this, 260)) : (int) (Utils.convertDpToPixel(this, 260) - delta)); // main_cumtom 마진(260)

            // calculate the delta (destination - current)
            final int headersDelta = headersDestination - currentHeadersMargin;
            final int rowsDelta = rowsDestination - currentRowsMargin;

            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ViewGroup.MarginLayoutParams headersParams = (ViewGroup.MarginLayoutParams) headersContainer.getLayoutParams();
                    headersParams.leftMargin = (int) (currentHeadersMargin + headersDelta * interpolatedTime);
                    headersContainer.setLayoutParams(headersParams);

                    ViewGroup.MarginLayoutParams rowsParams = (ViewGroup.MarginLayoutParams) rowsContainer.getLayoutParams();
                    rowsParams.leftMargin = (int) (currentRowsMargin + rowsDelta * interpolatedTime);
                    rowsContainer.setLayoutParams(rowsParams);
                }
            };

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mNavigationDrawerOpen = doOpen;
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            animation.setDuration(200);
            ((View) rowsContainer.getParent()).startAnimation(animation);
        }
    }
}
