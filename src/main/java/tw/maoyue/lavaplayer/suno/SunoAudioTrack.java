package tw.maoyue.lavaplayer.suno;

import com.sedmelluq.discord.lavaplayer.container.mpeg.MpegAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

import java.net.URI;

public class SunoAudioTrack extends DelegatedAudioTrack {
    private final String id;
    private final SunoAudioSourceManager sourceManager;

    public SunoAudioTrack(AudioTrackInfo trackInfo, String id, SunoAudioSourceManager sourceManager) {
        super(trackInfo);
        this.id = id;
        this.sourceManager = sourceManager;
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        try (var stream = new PersistentHttpStream(sourceManager.getHttpInterface(), new URI(trackInfo.uri), null)) {
            processDelegate(new MpegAudioTrack(trackInfo, stream), executor);
        }
    }

    @Override
    public AudioTrack makeShallowClone() {
        return new SunoAudioTrack(trackInfo, id, sourceManager);
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return sourceManager;
    }

    public String getId() {
        return id;
    }
}
