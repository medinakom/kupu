/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package id.my.mdn.kupu.app.web.ws;

import id.my.mdn.kupu.app.web.dao.AudioSegmentFacade;
import id.my.mdn.kupu.app.web.model.AudioSegment;
import id.my.mdn.kupu.app.web.model.AudioSegmentId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author aphasan
 */
@Stateless
@Path("audiokajian")
public class AudioKajianServiceController {

    @Inject
    private AudioSegmentFacade segmentFacade;

    @GET
    @Path("ping")
    @Produces({MediaType.TEXT_PLAIN})
    public String ping() {
        return "pong";
    }

    public final static int MAX_AGE = 42 * 60 * 60;

    @GET
    @Path("{video}/stream")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getPlaylist(@PathParam("video") String video) {
        AudioSegment segment = segmentFacade.find(new AudioSegmentId(video, "stream.m3u8"));
        if(segment == null) return Response.noContent().build();
        return Response.ok(segment.getContent())
                .header("Content-Type", "application/x-mpegURL")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin,accept,content-type")
                .header("Access-Control-Expose-Headers", "location,info")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", MAX_AGE)
                .header("x-responded-by", "cors-response-filter")
                .build();
    }

    @GET
    @Path("{video}/{segment}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getSegment(@PathParam("video") String video, @PathParam("segment") String segmentName) {
        AudioSegment segment = segmentFacade.find(new AudioSegmentId(video, segmentName));
        if(segment == null) return Response.noContent().build();
        return Response.ok(segment.getContent())
                .header("Content-Type", "video/MP2T")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin,accept,content-type")
                .header("Access-Control-Expose-Headers", "location,info")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", MAX_AGE)
                .header("x-responded-by", "cors-response-filter")
                .build();
    }

}
