package com.codeopticals.activitiestracking;

import android.widget.Chronometer;

public interface Controller {

    void startCount(Chronometer meter);

    void stopCount(Chronometer meter);
}
