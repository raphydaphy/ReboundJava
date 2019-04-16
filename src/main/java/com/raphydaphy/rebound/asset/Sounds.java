package com.raphydaphy.rebound.asset;

import com.raphydaphy.rebound.engine.asset.Sound;
import com.raphydaphy.rebound.util.ResourceName;

public class Sounds {
    public static Sound click;

    public static void init() {
        click = new Sound(new ResourceName("sounds/click_1.ogg"));
    }
}
