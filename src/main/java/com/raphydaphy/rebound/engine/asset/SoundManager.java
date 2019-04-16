package com.raphydaphy.rebound.engine.asset;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;

import java.util.ArrayList;
import java.util.List;

public class SoundManager {
    private List<Sound> sounds = new ArrayList<>();
    private long context;
    private long device;

    public SoundManager() {
        device = ALC10.alcOpenDevice(ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER));
        context = ALC10.alcCreateContext(device, new int[]{0});
        ALC10.alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));

        AL10.alListener3f(AL10.AL_POSITION, 0, 0, 0);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }

    public void register(Sound sound) {
        sounds.add(sound);
    }

    public void delete() {
        for (Sound sound : sounds) {
            sound.delete();
        }
        if (context != 0) ALC10.alcDestroyContext(context);
        if (device != 0) ALC10.alcCloseDevice(device);
    }
}
