package tw.maoyue.lavaplayer.suno;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.http.client.methods.HttpGet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SunoAudioSourceManager implements AudioSourceManager {
    private final HttpInterface httpInterface;
    private static final String BASE_URL = "https://studio-api.suno.ai/api/clip/";

    public SunoAudioSourceManager() {
        var httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();
        httpInterfaceManager.setHttpContextFilter(new SunoHttpContextFilter());
        httpInterface = httpInterfaceManager.getInterface();
    }

    @Override
    public String getSourceName() {
        return "suno";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        String songId = extractSongId(reference.identifier);
        if (songId == null) {
            return null;
        }

        try {
            HttpGet request = new HttpGet(BASE_URL + songId);
            var response = httpInterface.execute(request);
            var responseJson = JsonBrowser.parse(response.getEntity().getContent());

            return loadTrack(responseJson);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private AudioTrack loadTrack(JsonBrowser trackData) {
        String id = trackData.get("id").text();
        String title = trackData.get("title").text();
        String author = trackData.get("display_name").text();
        long duration = (long) (trackData.get("duration").asDouble() * 1000);
        String audioUrl = trackData.get("audio_url").text();

        AudioTrackInfo trackInfo = new AudioTrackInfo(title, author, duration, id, false, audioUrl);
        return new SunoAudioTrack(trackInfo, this);
    }

    private String extractSongId(String url) {
        if (url == null || !url.startsWith("https://suno.com/song/")) {
            return null;
        }
        return url.substring("https://suno.com/song/".length());
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
        DataFormatTools.writeNullableText(output, track.getIdentifier());
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
        String trackId = DataFormatTools.readNullableText(input);
        return new SunoAudioTrack(trackInfo, this);
    }

    @Override
    public void shutdown() {
        // Implement shutdown logic if needed
    }
}
