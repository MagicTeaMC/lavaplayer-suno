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
import java.util.regex.Pattern;

public class SunoAudioSourceManager implements AudioSourceManager {
    private final HttpInterface httpInterface;
    private static final String BASE_URL = "https://studio-api.suno.ai/api/clip/";
    private static final Pattern URL_PATTERN = Pattern.compile("^https?:\\/\\/suno\\.com\\/song\\/(?<id>[a-z0-9-]+)\\/?$");

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
        var matcher = URL_PATTERN.matcher(reference.identifier);
        if (matcher.find()) {
            String id = matcher.group("id");

            try (var response = httpInterface.execute(new HttpGet(BASE_URL + id))) {
                var responseJson = JsonBrowser.parse(response.getEntity().getContent());

                if (responseJson.get("status").text().equals("complete")) {
                    return loadAudioTrack(responseJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public HttpInterface getHttpInterface() {
        return httpInterface;
    }

    private AudioTrack loadAudioTrack(JsonBrowser trackData) {
        long duration = (long) (trackData.get("duration").as(Number.class).doubleValue() * 1000);
        String id = trackData.get("id").text();
        return new SunoAudioTrack(new AudioTrackInfo(
                trackData.get("title").text(),
                trackData.get("display_name").text(),
                duration,
                id,
                false,
                trackData.get("audio_url").text()
        ), id, this);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
        SunoAudioTrack sunoTrack = (SunoAudioTrack) track;
        DataFormatTools.writeNullableText(output, sunoTrack.getId());
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
        return new SunoAudioTrack(trackInfo, DataFormatTools.readNullableText(input), this);
    }

    @Override
    public void shutdown() {
        //
    }
}
