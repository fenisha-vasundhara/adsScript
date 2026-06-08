package com.demo.adsmanage.viewpagerCustom;

import androidx.annotation.Nullable;

import com.demo.adsmanage.viewpagerCustom.animation.AnimationManager;
import com.demo.adsmanage.viewpagerCustom.animation.controller.ValueController;
import com.demo.adsmanage.viewpagerCustom.animation.data.Value;
import com.demo.adsmanage.viewpagerCustom.draw.DrawManager;
import com.demo.adsmanage.viewpagerCustom.draw.data.Indicator;


public class IndicatorManager implements ValueController.UpdateListener {

    private DrawManager drawManager;
    private AnimationManager animationManager;
    private Listener listener;

    interface Listener {
        void onIndicatorUpdated();
    }

    IndicatorManager(@Nullable Listener listener) {
        this.listener = listener;
        this.drawManager = new DrawManager();
        this.animationManager = new AnimationManager(drawManager.indicator(), this);
    }

    public AnimationManager animate() {
        return animationManager;
    }

    public Indicator indicator() {
        return drawManager.indicator();
    }

    public DrawManager drawer() {
        return drawManager;
    }

    @Override
    public void onValueUpdated(@Nullable Value value) {
        drawManager.updateValue(value);
        if (listener != null) {
            listener.onIndicatorUpdated();
        }
    }
}
