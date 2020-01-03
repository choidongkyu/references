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

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import net.quber.myapplication.ui.fragment.CustomHeadersFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG ="MainActivity";

    private CustomHeadersFragment mHeadersFragment;
    private FragmentManager mFragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"getrequested orientation = "+getRequestedOrientation());
        mHeadersFragment = new CustomHeadersFragment();


        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.header_container, mHeadersFragment,"CustomHeadersFragment");
        transaction.commit();
    }

    public FragmentManager getfragmentManager (){
        return mFragmentManager;
    }

}
