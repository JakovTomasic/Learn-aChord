package com.justchill.android.learnachord;

import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;

public class TextScaling {


    // For MainActivity interval/chord text size
    public static final float SCALED_DENSITY_SMALL = 1.1f;
    public static final float SCALED_DENSITY_NORMAL = 1.5f;
    public static final float SCALED_DENSITY_LARGE = 1.75f;



    // Adjust density if out of borders
    public static float autoAdjustScalingDensity(float density) {
        if(density < SCALED_DENSITY_SMALL) {
            return SCALED_DENSITY_SMALL;
        }
        if(density > SCALED_DENSITY_LARGE) {
            return SCALED_DENSITY_LARGE;
        }
        return density;
    }

    // Check of new value for scaled density is available
    public static void refreshScaledDensity() {
        switch (DatabaseData.chordTextScalingMode) {
            case DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_SMALL:
                DatabaseData.scaledDensity = SCALED_DENSITY_SMALL;
                break;
            case DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_NORMAL:
                DatabaseData.scaledDensity = SCALED_DENSITY_NORMAL;
                break;
            case DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_LARGE:
                DatabaseData.scaledDensity = SCALED_DENSITY_LARGE;
                break;
            default: // CHORD_TEXT_SCALING_MODE_AUTO
                DatabaseData.scaledDensity = autoAdjustScalingDensity(MyApplication.getAppContext().getResources().getDisplayMetrics().scaledDensity);
        }
    }
}
