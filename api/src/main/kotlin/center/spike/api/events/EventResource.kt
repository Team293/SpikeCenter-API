package center.spike.api.events

import center.spike.common.events.GenerateMatchScheduleRequest
import center.spike.common.events.GeneratePitScheduleRequest
import center.spike.common.toResponse
import center.spike.core.events.services.EventService
import center.spike.core.schedule.services.ScheduleService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response

@Path("/events")
class EventResource {

    @Inject
    internal lateinit var eventService: EventService

    @Inject
    internal lateinit var scheduleService: ScheduleService

    @Path("/get/{eventCode}")
    @GET
    fun getEvent(@PathParam("eventCode") eventCode: String): Response {
        return eventService.getOrInitializeEvent(eventCode).toResponse()
    }

    @Path("/generate/pit")
    @POST
    fun generatePitSchedule(request: GeneratePitScheduleRequest): Response {
        return scheduleService.createPitSchedule(request.eventCode, request.scouters).toResponse()
    }

    @Path("/generate/match")
    @POST
    fun generateMatchSchedule(request: GenerateMatchScheduleRequest): Response {
        return scheduleService.createMatchSchedule(
            request.eventCode,
            request.groupAScouters,
            request.groupBScouters,
            request.groupALeader,
            request.groupBLeader
        ).toResponse()
    }

    @Path("/schedule/pit/{eventCode}")
    @GET
    fun getPitSchedule(@PathParam("eventCode") eventCode: String): Response {
        return scheduleService.getPitSchedule(eventCode).toResponse()
    }

    @Path("/schedule/match/{eventCode}")
    @GET
    fun getMatchSchedule(@PathParam("eventCode") eventCode: String): Response {
        return scheduleService.getMatchSchedule(eventCode).toResponse()
    }

}