package com.lk.userapp.directionsLib;

import com.lk.userapp.Pojo.MapTimeObj;
import com.lk.userapp.Pojo.mapDistanceObj;

/**
 * Created by Vishal on 10/20/2018.
 * Updated by Pasan on 12/02/2021.
 */

public interface TaskLoadedCallback {
    void onTaskDone(Object... values);
    void onDistanceTaskDone(mapDistanceObj distance);
    void onTimeTaskDone(MapTimeObj time);
}
