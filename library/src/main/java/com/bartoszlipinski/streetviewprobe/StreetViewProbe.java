/*
 * Copyright 2015 Bartosz Lipinski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bartoszlipinski.streetviewprobe;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * Created by Bartosz Lipinski
 * 29.05.15
 * Available at: https://github.com/blipinsk/StreetViewProbe
 */
public class StreetViewProbe {
    private static StreetViewProbe instance = null;
    private final WeakReference<Context> context;
    private final int size;
    private final int threshold;
    private OkHttpClient client;

    private StreetViewProbe(WeakReference<Context> context, OkHttpClient client, int size, int threshold) {
        this.context = context;
        this.client = client;
        this.size = size;
        this.threshold = threshold;
    }

    public synchronized static StreetViewProbe with(Context context) {
        if (instance == null) {
            instance = new Builder(context).build();
        }
        return instance;
    }

    public synchronized static void destroy() {
        //TODO: perform necessary actions (on destroy)
        instance = null;
    }

    public synchronized void probe(final double lat, final double lon, final OnStreetViewStatusListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null.");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (context.get() != null) {
                    String path = context.get().getString(R.string.probe_path, size, lat, lon);
                    client.newCall(
                            new Request.Builder()
                                    .url(path)
                                    .build())
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    listener.onStreetViewStatus(Status.UNKNOWN);
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
//                                    Log.d("BITM", response.body().contentLength() + " ");
                                    listener.onStreetViewStatus(response.body().contentLength() > threshold ? Status.AVAILABLE : Status.UNAVAILABLE);
                                }
                            });
                }
            }
        }).start();
    }

    @IntDef({Status.UNKNOWN, Status.AVAILABLE, Status.UNAVAILABLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int UNKNOWN = 0;
        int AVAILABLE = 1;
        int UNAVAILABLE = 2;
    }

    public interface OnStreetViewStatusListener {
        void onStreetViewStatus(@Status int status);
    }

    public static class Builder {
        private final WeakReference<Context> context;
        private int size;
        private int threshold;
        private OkHttpClient client;


        public Builder(Context context) {
            if (context == null) {
                destroy();
                throw new IllegalArgumentException("Context must not be null.");
            } else {
                this.context = new WeakReference<Context>(context);
            }
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder threshold(int threshold) {
            this.threshold = threshold;
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public StreetViewProbe build() {
            if (size <= 0) {
                size = context.get().getResources().getInteger(R.integer.probe_default_size);
            }
            if (threshold <= 0) {
                threshold = context.get().getResources().getInteger(R.integer.availability_threshold);
            }
            if (client == null) {
                client = new OkHttpClient();
            }
            return new StreetViewProbe(context, client, size, threshold);
        }
    }

}
