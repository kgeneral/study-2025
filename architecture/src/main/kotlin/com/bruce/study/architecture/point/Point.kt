package com.bruce.study.architecture.point

import jakarta.persistence.*
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

data class PointEventRequest(val point: Long)
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
    @Column(name = "tx_id", nullable = false, updatable = false)
    open var txId: UUID? = UUID.randomUUID(),

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
interface UserPointRepository : JpaRepository<UserPoint, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserPoint u WHERE u.userId = :userId")
    fun findByIdWithLock(userId: Long): UserPoint?
}


@Repository
interface PointEventRepository : JpaRepository<PointEvent, UUID>


@RestController
class PointController(
    private val userPointRepository: UserPointRepository,
    private val pointEventRepository: PointEventRepository
) {

    @PostMapping(value = ["/user/{userId}/point"])
    @Transactional
    fun addPoint(
        @PathVariable userId: Long,
        @RequestBody request: PointEventRequest
    ): ResponseEntity<PointResponse> {
        if (request.point == 0L) {
            return ResponseEntity.badRequest().body(
                PointResponse(userId, 0, "Point value must not be zero")
            )
        }

        // Fetch or create user point
        val userPoint = userPointRepository.findByIdWithLock(userId)
            ?: UserPoint(
                userId = userId,
                currentPoint = 0
            )


        // Calculate updated points
        val updatedPoint = userPoint.currentPoint + request.point
        if (updatedPoint < 0) {
            return ResponseEntity.badRequest().body(
                PointResponse(userId, userPoint.currentPoint, "Insufficient points")
            )
        }

        // Update user's points
        userPoint.currentPoint = updatedPoint
        userPointRepository.save(userPoint)

        // Log the point change event
        val pointEvent = PointEvent(
            userPoint = userPoint,
            point = request.point
        )
        pointEventRepository.save(pointEvent)

        return ResponseEntity.ok(
            PointResponse(userId, userPoint.currentPoint, "User points updated successfully")
        )
    }


    @GetMapping(value = ["/user/{userId}/point"])
    fun getPoint(@PathVariable userId: Long): PointResponse {
        val userPointEntity = userPointRepository.findByIdOrNull(userId)
        val userIdResult = userPointEntity?.userId ?: userId //throw EntityNotFoundException()
        val currentPoint = userPointEntity?.currentPoint ?: 0
        return PointResponse(userIdResult, currentPoint, "user point info")
    }
}
