using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

namespace packt.PhotoEra.Mapping {

public static class MathG
{
    public static float Distance(MapLocation mp1, MapLocation mp2) {
        double R = 6371;    // 지구의 평균 반경 km 값
        double lat1 = mp1.Latitude;
        double lat2 = mp2.Latitude;
        double lon1 = mp1.Longitude;
        double lon2 = mp2.Longitude;

        // 좌표를 라디안으로 변환
        lat1 = deg2rad(lat1);
        lon1 = deg2rad(lon1);
        lat2 = deg2rad(lat2);
        lon2 = deg2rad(lon2);

        // 좌표들의 차이를 계산
        var dlat = (lat2 - lat1);
        var dlon = (lon2 - lon1);

        // 하버사인 공식
        var a = Math.Pow(Math.Sin(dlat / 2), 2) + Math.Cos(lat1) * Math.Cos(lat2) * Math.Pow(Math.Sin(dlon / 2), 2);
        var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1-a));
        var d = c * R;

        //float로 변환 후 km를 m로 반환
        return (float)d * 1000;

    }

    public static double deg2rad(double deg) {
        var rad = deg * Math.PI / 180;
        // 라디안 = 각도 * 파이/180
        return rad;
    }

    public static float Distance(float x1, float y1, float x2, float y2)
        {
            return Distance(new MapLocation(x1, y1), new MapLocation(x2, y2));
        }
}
}
