package com.example.spinusoidsolutions.spinesolutions;

import java.text.Normalizer;
import java.util.Date;

/**
 * Created by Noah on 2018-03-25.
 */

public class CollectedDataSingleton {
    // To call from other files, do CollectedDataSingleton instance = CollectedDataSingleton.getCollectedDataSingleton(formattedDate, differnece)
    //

    public FormattedSpineData data;

    private static CollectedDataSingleton instance = null;

    private CollectedDataSingleton(FormattedSpineData data) {
        this.data = data;
    }

    public static CollectedDataSingleton getCollectedDataSingleton(FormattedSpineData data)
    {
        if (instance == null) {
            instance = new CollectedDataSingleton(data);
        }
        return instance;
    }
    public void setData(FormattedSpineData data)
    {
        this.data = data;
    }


}
