/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package id.my.mdn.kupu.app.web.ws;

import id.my.mdn.kupu.app.web.dao.KitabContentFacade;
import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.dao.VideoSegmentFacade;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.VideoSegment;
import id.my.mdn.kupu.app.web.model.VideoSegmentId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aphasan
 */
@Stateless
@Path("videokajian")
public class VideoKajianServiceController {

    @Inject
    private VideoSegmentFacade segmentFacade;

    @GET
    @Path("ping")
    @Produces({MediaType.TEXT_PLAIN})
    public String ping() {
        return "pong";
    }

    @GET
    @Path("{video}/stream")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getPlaylist(@PathParam("video") String video) {
        VideoSegment segment = segmentFacade.find(new VideoSegmentId(video, "stream.m3u8"));
        if (segment == null) {
            return Response.noContent().build();
        }
        return Response.ok(segment.getContent())
                .header("Content-Type", "application/x-mpegURL")
                .build();
    }

    @GET
    @Path("{video}/{segment}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getSegment(@PathParam("video") String video, @PathParam("segment") String segmentName) {
        VideoSegment segment = segmentFacade.find(new VideoSegmentId(video, segmentName));
        if (segment == null) {
            return Response.noContent().build();
        }
        return Response.ok(segment.getContent())
                .header("Content-Type", "video/MP2T")
                .build();
    }

    @Inject
    private KitabFacade kitabFacade;

    @Inject
    private KitabContentFacade contentFacade;

    @GET
    @Path("kitab/{id}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getKitabContant(@PathParam("id") String kitabId) {
        Kitab kitab = kitabFacade.find(kitabId);
        byte[] content = contentFacade.getContent(kitab.getContent().getId());
        if (content == null) {
            return Response.noContent().build();
        }
        return Response.ok(content)
                .header("Content-Type", "application/pdf")
                .build();
    }

    @GET
    @Path("live/stream")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getLive() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8090/hls/stream.m3u8"))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());

            return Response.ok(response.body())
                    .header("Content-Type", "application/x-mpegURL")
                    .build();
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            Logger.getLogger(VideoKajianServiceController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.noContent().build();
    }

    @GET
    @Path("live/{num}/stream.m3u8")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getLivePlaylist(@PathParam("num") String num) {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8090/hls/" + num + "/stream.m3u8"))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());

            return Response.ok(response.body())
                    .header("Content-Type", "application/x-mpegURL")
                    .build();
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            Logger.getLogger(VideoKajianServiceController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.noContent().build();
    }

    @GET
    @Path("live/{num}/{name}.ts")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getLiveSegment(@PathParam("num") String num, @PathParam("name") String chunkName) {

        if (!chunkName.contains("offline")) {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8090/hls/" + num + "/" + chunkName + ".ts"))
                        .GET()
                        .build();

                HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());

                return Response.ok(response.body())
                        .header("Content-Type", "video/MP2T")
                        .build();
            } catch (URISyntaxException | IOException | InterruptedException ex) {
                Logger.getLogger(VideoKajianServiceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return Response.noContent().build();
    }

}
