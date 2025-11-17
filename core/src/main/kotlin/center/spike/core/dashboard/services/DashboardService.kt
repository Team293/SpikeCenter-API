package center.spike.core.dashboard.services

import center.spike.common.ErrorCode
import center.spike.common.ResponseStatus
import center.spike.common.SafeRunner
import center.spike.common.ServiceError
import center.spike.common.ServiceResponse
import center.spike.common.dashboard.CreateDashboardChipRequest
import center.spike.common.dashboard.UpdateDashboardChipRequest
import center.spike.common.toServiceResponse
import center.spike.core.dashboard.persistence.DashboardChip
import center.spike.core.dashboard.persistence.DashboardChipRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class DashboardService {
    @Inject
    internal lateinit var dashboardChipRepository: DashboardChipRepository

    @Transactional
    fun createChip(request: CreateDashboardChipRequest): ServiceResponse<DashboardChip> {
        val chip = DashboardChip(
            code = request.code,
            filters = request.filters,
            renderType = request.renderType,
            formInputType = request.formInputType,
            title = request.title
        )

        return SafeRunner.runOutcome(
            errorCode = { ErrorCode.Database }
        ) {
            dashboardChipRepository.persist(chip)
        }.toServiceResponse(
            onSuccess = { ServiceResponse(ResponseStatus.SUCCESS, chip) },
            onFailure = { err -> ServiceResponse(ResponseStatus.ERROR, "Failed to create dashboard chip", error = err )}
        )
    }

    @Transactional
    fun updateChip(request: UpdateDashboardChipRequest): ServiceResponse<DashboardChip> {
        val chip = dashboardChipRepository.findById(request.id)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Dashboard chip not found",
                error = ServiceError(ErrorCode.NotFound, "Dashboard chip with id ${request.id} not found")
            )

        request.title?.let { chip.title = it }
        request.filters?.let { chip.filters = it }
        request.renderType?.let { chip.renderType = it }
        request.formInputType?.let { chip.formInputType = it }
        request.code?.let { chip.code = it }

        return SafeRunner.runOutcome(
            errorCode = { ErrorCode.Database }
        ) {
            dashboardChipRepository.persist(chip)
        }.toServiceResponse(
            onSuccess = { ServiceResponse(ResponseStatus.SUCCESS, chip) },
            onFailure = { err -> ServiceResponse(ResponseStatus.ERROR, "Failed to create dashboard chip", error = err )}
        )
    }

    @Transactional
    fun getChip(id: Long): ServiceResponse<DashboardChip> {
        val chip = dashboardChipRepository.findById(id)
            ?: return ServiceResponse(
                status = ResponseStatus.NOT_FOUND,
                message = "Dashboard chip with id $id not found",
                error = ServiceError(ErrorCode.NotFound, "Dashboard chip with id $id not found")
            )

        return ServiceResponse(
            status = ResponseStatus.SUCCESS,
            message = "Chip found",
            data = chip
        )
    }
}