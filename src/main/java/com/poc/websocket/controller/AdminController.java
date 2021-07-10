package com.poc.websocket.controller;

import com.poc.websocket.handler.GameManager;
import com.poc.websocket.util.PropertyReader;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin/tictactoe")
public class AdminController {

    private static final GameManager gameManager = GameManager.getInstance();

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public Response health(@HeaderParam("Admin-Auth") String auth) {
        if(null == auth || !auth.equals(PropertyReader.getAuthKey())){
            return Response.status(500,"Auth Failed").build();
        }
        return Response.ok("TicTacToe game is up and running.").build();
    }

    @GET
    @Path("/rooms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms(@HeaderParam("Admin-Auth") String auth) {
        if(null == auth || !auth.equals(PropertyReader.getAuthKey())){
            return Response.status(500,"Auth Failed").build();
        }
        return Response.ok(gameManager.getAllRooms()).build();
    }

    @GET
    @Path("/game/{roomNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameObject(@HeaderParam("Admin-Auth") String auth, @PathParam("roomNo") final String roomNo) {
        if(null == auth || !auth.equals(PropertyReader.getAuthKey())){
            return Response.status(500,"Auth Failed").build();
        }
        return Response.ok(gameManager.getGameFromRoom(roomNo)).build();
    }

    @GET
    @Path("/session/{sessionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayer(@HeaderParam("Admin-Auth") String auth, @PathParam("sessionId") final String sessionId) {
        if(null == auth || !auth.equals(PropertyReader.getAuthKey())){
            return Response.status(500,"Auth Failed").build();
        }
        return Response.ok(gameManager.getPlayer(sessionId)).build();
    }

    @DELETE
    @Path("/close/room/{roomNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response closeGame(@HeaderParam("Admin-Auth") String auth, @PathParam("roomNo") final String roomNo) {
        if(null == auth || !auth.equals(PropertyReader.getAuthKey())){
            return Response.status(500,"Auth Failed").build();
        }
        gameManager.closeGame(roomNo);
        return Response.accepted().build();
    }
}
