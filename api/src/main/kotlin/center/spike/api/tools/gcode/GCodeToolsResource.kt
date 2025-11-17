package center.spike.api.tools.gcode

import center.spike.core.tools.gcode.services.GCodeMergerService
import jakarta.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path

@Path("/tools/gcode")
class GCodeToolsResource {

    @Inject
    lateinit var gCodeMergerService: GCodeMergerService

    @POST
    @Path("/merge")
    fun mergeGCodeFiles(gcodeFiles: List<String>): String {
        return gCodeMergerService.mergeGCodeFiles(gcodeFiles)
    }
}