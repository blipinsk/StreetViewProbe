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
package com.bartoszlipinski.streetviewprobe;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by Bartosz Lipinski.
 * 29.05.15
 */
public class StreetViewProbe {
    private static StreetViewProbe sInstance = null;
    private final WeakReference<Context> mContext;
    private final int mSize;
    private final int mThreshold;
    private OkHttpClient mClient;

    private StreetViewProbe(WeakReference<Context> context, OkHttpClient client, int size, int threshold) {
        mContext = context;
        mClient = client;
        mSize = size;
        mThreshold = threshold;
    }

    public synchronized static StreetViewProbe with(Context context) {
        if (sInstance == null) {
            sInstance = new Builder(context).build();
        }
        return sInstance;
    }

    public synchronized static void destroy() {
        //TODO: perform necessary actions (on destroy)
        sInstance = null;
    }

    public synchronized void probe(final double lat, final double lon, final OnStreetViewStatusListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null.");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mContext.get() != null) {
                    String path = mContext.get().getString(R.string.probe_path, mSize, lat, lon);
                    mClient.newCall(
                            new Request.Builder()
                                    .url(path)
                                    .build())
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    listener.onStreetViewStatus(Status.UNKONWN);
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    Log.d("BITM", response.body().contentLength() + " ");
                                    listener.onStreetViewStatus(response.body().contentLength() > mThreshold ? Status.AVAILABLE : Status.UNAVAILABLE);
                                }
                            });
                }
            }
        }).start();
    }

    public enum Status {
        AVAILABLE, UNAVAILABLE, UNKONWN
    }

    public static interface OnStreetViewStatusListener {
        public void onStreetViewStatus(Status status);
    }

    public static class Builder {
        private final WeakReference<Context> mContext;
        private int mSize;
        private int mThreshold;
        private OkHttpClient mClient;


        public Builder(Context context) {
            if (context == null) {
                destroy();
                throw new IllegalArgumentException("Context must not be null.");
            } else {
                mContext = new WeakReference<Context>(context);
            }
        }

        public Builder size(int size) {
            this.mSize = size;
            return this;
        }

        public Builder threshold(int threshold) {
            this.mThreshold = threshold;
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.mClient = client;
            return this;
        }

        public StreetViewProbe build() {
            if (mSize <= 0) {
                mSize = mContext.get().getResources().getInteger(R.integer.probe_default_size);
            }
            if (mThreshold <= 0) {
                mThreshold = mContext.get().getResources().getInteger(R.integer.availability_threshold);
            }
            if (mClient == null) {
                mClient = new OkHttpClient();
            }
            return new StreetViewProbe(mContext, mClient, mSize, mThreshold);
        }
    }

}
