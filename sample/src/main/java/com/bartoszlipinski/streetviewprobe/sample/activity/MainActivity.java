/**
 * Copyright 2015 Bartosz Lipinski
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bartoszlipinski.streetviewprobe.sample.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.streetviewprobe.StreetViewProbe;
import com.bartoszlipinski.streetviewprobe.sample.R;
import com.bartoszlipinski.streetviewprobe.sample.utils.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {
//    public static final double LAT = 40.720032;
//    public static final double LAT = 73.67868;
//    public static final double LON = -73.988354;
    public static final double LAT = 41.085741;
    public static final double LON = -115.192323;

    private TextView mTextLat;
    private TextView mTextLon;
    private TextView mTextAvailability;
    private ImageView mStreetViewImage;
    private ImageView mDotImage;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();

        mStreetViewImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int width = mStreetViewImage.getWidth();
                int height = mStreetViewImage.getHeight();
                if (width > 0 && height > 0) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        mStreetViewImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mStreetViewImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    testLibrary(width);
                }
            }
        });
    }

    private void testLibrary(int width) {
        int corner = getResources().getDimensionPixelSize(R.dimen.background_radius);
        Picasso.with(this)
                .load(getString(R.string.probe_path, width, LAT, LON))
                .transform(new RoundedCornersTransformation(corner, 0))
                .into(mStreetViewImage);

        StreetViewProbe.with(this).probe(LAT, LON, new StreetViewProbe.OnStreetViewStatusListener() {
            @Override
            public void onStreetViewStatus(final int status) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (status) {
                            case StreetViewProbe.Status.AVAILABLE:
                                mTextAvailability.setVisibility(View.VISIBLE);
                                mDotImage.setVisibility(View.VISIBLE);
                                mTextAvailability.setText(R.string.available);
                                mTextAvailability.setSelected(true);
                                mDotImage.setSelected(true);
                                break;
                            case StreetViewProbe.Status.UNAVAILABLE:
                                mTextAvailability.setVisibility(View.VISIBLE);
                                mDotImage.setVisibility(View.VISIBLE);
                                mTextAvailability.setText(R.string.unavailable);
                                mTextAvailability.setSelected(false);
                                mDotImage.setSelected(false);
                                break;
                            case StreetViewProbe.Status.UNKONWN:
                                break;
                        }
                    }
                });
            }
        });
    }

    private void setupViews() {
        mTextLat = (TextView) findViewById(R.id.text_lat);
        mTextLon = (TextView) findViewById(R.id.text_lon);
        mTextAvailability = (TextView) findViewById(R.id.text_avaiability);
        mStreetViewImage = (ImageView) findViewById(R.id.image_street_view);
        mDotImage = (ImageView) findViewById(R.id.image_dot);

        mTextLat.setText(getString(R.string.lat, LAT));
        mTextLon.setText(getString(R.string.lon, LON));
        mTextAvailability.setVisibility(View.INVISIBLE);
        mDotImage.setVisibility(View.INVISIBLE);
    }
}
