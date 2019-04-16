package com.raphydaphy.rebound.engine.asset;

import com.google.common.io.ByteStreams;
import com.raphydaphy.rebound.Rebound;
import com.raphydaphy.rebound.util.ResourceName;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;

public class Sound {
    private int buffer;

    public Sound(ResourceName file) {
        buffer = AL10.alGenBuffers();

        STBVorbisInfo info = STBVorbisInfo.malloc();
        byte[] bytes;
        try (InputStream stream = file.getInputStream()) {
            if (stream == null) {
                Rebound.getLogger().warning("Could not find sound " + file + "!");
                return;
            }
            bytes = ByteStreams.toByteArray(stream);
        }  catch (IOException e) {
            Rebound.getLogger().log(Level.WARNING, "Failed to load sound " + file + "!", e);
            return;
        }

        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();
        IntBuffer errorCode = BufferUtils.createIntBuffer(1);
        long samples = STBVorbis.stb_vorbis_open_memory(buffer, errorCode, null);
        if (samples == 0) {
            Rebound.getLogger().warning("Failed to load sound " + file + " with error code " + errorCode.get(0));
            return;
        }

        STBVorbis.stb_vorbis_get_info(samples, info);
        ShortBuffer soundBuffer = BufferUtils.createShortBuffer(STBVorbis.stb_vorbis_stream_length_in_samples(samples));
        int channels = info.channels();
        soundBuffer.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(samples, channels, soundBuffer) * channels);
        STBVorbis.stb_vorbis_close(samples);
        AL10.alBufferData(this.buffer, channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, soundBuffer, info.sample_rate());
        info.free();

        Rebound.getInstance().getSoundManager().register(this);
    }

    public int getBuffer() {
        return buffer;
    }

    public void delete() {
        AL10.alDeleteBuffers(buffer);
    }

    public static class Source {
        private int id;

        public Source() {
            id = AL10.alGenSources();
            AL10.alSourcef(id, AL10.AL_GAIN, 1);
            AL10.alSourcef(id, AL10.AL_PITCH, 1);
            AL10.alSource3f(id, AL10.AL_POSITION, 0, 0, 0);
        }

        public void play(Sound sound) {
            AL10.alSourcei(id, AL10.AL_BUFFER, sound.getBuffer());
            AL10.alSourcePlay(id);
        }

        public void delete() {
            AL10.alDeleteSources(id);
        }
    }
}
