package tw.maoyue.lavaplayer.suno;

import com.sedmelluq.discord.lavaplayer.tools.http.HttpContextFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

public class SunoHttpContextFilter implements HttpContextFilter {
    @Override
    public void onContextOpen(HttpClientContext context) {
        // Add custom logic for when the context is opened
    }

    @Override
    public void onContextClose(HttpClientContext context) {
        // Add custom logic for when the context is closed
    }

    @Override
    public void onRequest(HttpClientContext context, HttpUriRequest request, boolean isRepetition) {
        request.setHeader("Referer", "https://suno.com/");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
    }

    @Override
    public boolean onRequestResponse(HttpClientContext context, HttpUriRequest request, HttpResponse response) {
        return false;
    }

    @Override
    public boolean onRequestException(HttpClientContext context, HttpUriRequest request, Throwable error) {
        return false;
    }
}
