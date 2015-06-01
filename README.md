StreetViewProbe
===============

[![License](https://img.shields.io/github/license/blipinsk/FlippableStackView.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

A simple (although a bit hacky) way to check if StreetView is available for a specific location.

It's just a small piece of code (inspired by [this post](http://stackoverflow.com/a/25059956/1993204)), that I needed for one of my projects.
It's based on downloading StreetView screen (with OkHttp) and deciding based on its size whether it contains a placeholder or actual StreetView image.
Until I find reliable values of screen dimensions, and size availability threshold, this library will be in pre-release state.

![ ](/StreetViewProbe.png)

Important note
==============

Do not expect this library to give you 100% dependable results. There might be cases in which it won't work properly.

I'm planning to perform a small research toward finding a proper parameter values (to download as little data as possible and still get the desired results).

Until then, use it carefully.

Usage
=====
*For a working implementation of this library see the `sample/` folder.*

  1. Add [Square's](https://github.com/square) [`Okio`](https://github.com/square/okio) dependency to your build.gradle:

        compile 'com.squareup.okio:okio:1.4.0'

  2. Well... `probe` with `StreetViewProbe`:

        StreetViewProbe.with(context).probe(LAT, LON, new StreetViewProbe.OnStreetViewStatusListener() {
            @Override
            public void onStreetViewStatus(StreetViewProbe.Status status) {
                //street view status is available
            }
        });


Including In Your Project
-------------------------
For now the library is not available via Maven Central.
I will upload it to it as soon as I find reasonable parameter values for the library to work correctly.

Developed by
==========
 * Bartosz Lipiński

License
======

    Copyright 2015 Bartosz Lipiński
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
