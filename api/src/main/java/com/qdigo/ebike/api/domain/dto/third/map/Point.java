/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.api.domain.dto.third.map;

/**
 * Created by niezhao on 2017/8/1.
 */
public class Point {
    private long timestamp;
    private double longitude;
    private double latitude;

    public long getTimestamp() {
        return timestamp;
    }

    public Point setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public Point setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public Point setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }
}
