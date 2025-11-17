package center.spike.core.integration.tba

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import org.eclipse.microprofile.config.ConfigProvider
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(baseUri = "https://www.thebluealliance.com/api/v3")
@Path("/event/{event_key}")
@RegisterClientHeaders(TbaClientHeadersFactory::class)
interface TBAEventClient {
    @GET
    @Path("/matches")
    @Produces(MediaType.APPLICATION_JSON)
    fun getMatchesForEvent(@PathParam("event_key") eventKey: String): List<TBAMatch>

    @GET
    @Path("/simple")
    @Produces(MediaType.APPLICATION_JSON)
    fun getEventInfo(@PathParam("event_key") eventKey: String): TBAEvent

    @GET
    @Path("/teams/simple")
    @Produces(MediaType.APPLICATION_JSON)
    fun getTeamsForEvent(@PathParam("event_key") eventKey: String): List<TBATeam>
}

@RegisterRestClient(baseUri = "https://www.thebluealliance.com/api/v3")
@Path("/team/{team_key}")
@RegisterClientHeaders(TbaClientHeadersFactory::class)
interface TBATeamClient {
    @GET
    @Path("/simple")
    @Produces(MediaType.APPLICATION_JSON)
    fun getTeamInfo(@PathParam("team_key") teamKey: String): TBATeam
}


data class TBATeam(
    val team_number: Long? = null,
    val nickname: String? = null
)

data class TBAEvent(
    val name: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
)

data class AllianceSide(
    val team_keys: List<String> = emptyList(),
)

data class Alliances(
    val blue: AllianceSide? = null,
    val red: AllianceSide? = null
)

data class TBAMatch(
    val predicted_time: Long? = null,
    val alliances: Alliances? = null,
    val key: String? = null,
)

class TbaClientHeadersFactory : ClientHeadersFactory {
    override fun update(incomingHeaders: MultivaluedMap<String, String>?, outgoingHeaders: MultivaluedMap<String, String>): MultivaluedMap<String, String> {
        val tbaKey = try {
            ConfigProvider.getConfig().getValue("tba.api.key", String::class.java)
        } catch (_: Exception) {
            ""
        }
        if (tbaKey.isNotBlank()) {
            outgoingHeaders.add("X-TBA-Auth-Key", tbaKey)
        }
        return outgoingHeaders
    }
}
