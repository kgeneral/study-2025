package com.bruce.study.architecture.point

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*

data class PointEventRequest(val userId: Long, val point: Long)
data class PointResponse(val userId: Long, val point: Long, val message: String)

@Entity
@Table(name = "user_points")
class UserPoint(

    @Id
    @Column(name = "user_id", nullable = false)
    open var userId: Long,

    @Column(name = "current_point", nullable = false)
    open var currentPoint: Long,

    @Column(name = "last_modified_at", nullable = false)
    open var lastModifiedAt: LocalDateTime = LocalDateTime.now(),
) {
    @PreUpdate
    fun preUpdate() {
        lastModifiedAt = LocalDateTime.now()
    }
}

@Entity
@Table(name = "point_events")
class PointEvent(

    @Id
    @GeneratedValue
    @Column(name = "tx_id", nullable = false, updatable = false)
    open var txId: UUID? = null,

    // todo is this relationship needed?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var userPoint: UserPoint,

    @Column(name = "point", nullable = false)
    open var point: Long,

    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now(),
)

@Repository
interface UserPointRepository : JpaRepository<UserPoint, Long>

@Repository
interface PointEventRepository : JpaRepository<PointEvent, UUID>


@RestController
class PointController(
    private val userPointRepository: UserPointRepository,
) {

    @GetMapping(value = ["/user/{userId}/point"])
    fun getPoint(@PathVariable userId: Long): PointResponse {
        val userPointEntity = userPointRepository.findByIdOrNull(userId)
        val userIdResult = userPointEntity?.userId ?: userId //throw EntityNotFoundException()
        val currentPoint = userPointEntity?.currentPoint ?: 0
        return PointResponse(userIdResult, currentPoint, "user point info")
    }
}
