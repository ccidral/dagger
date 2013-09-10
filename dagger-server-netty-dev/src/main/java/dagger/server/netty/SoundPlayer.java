package dagger.server.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundPlayer {

    private static final Logger logger = LoggerFactory.getLogger(SoundPlayer.class);

    public static void playSound(String name) {
        try {
            Clip clip = AudioSystem.getClip();
            InputStream soundFile = SoundPlayer.class.getResourceAsStream("/" + name + ".wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(soundFile));
            clip.open(ais);
            clip.start();
        } catch(Throwable e) {
            logger.warn("Failed to play sound", e.getMessage());
        }
    }

}
