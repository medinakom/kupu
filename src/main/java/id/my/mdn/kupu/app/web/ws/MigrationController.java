/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.ws;

import id.my.mdn.kupu.app.web.service.MigrationService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author aphasan
 */
@Stateless
@Path("admin/migrate")
public class MigrationController {

    @Inject
    private MigrationService migrationService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response migrate() {
        migrationService.migrateAll();
        return Response.ok("Migration started asynchronously. Check server logs.").build();
    }
}
