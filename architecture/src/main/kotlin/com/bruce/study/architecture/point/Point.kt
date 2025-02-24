package com.bruce.study.architecture.point

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

data class PointEventRequest(val userId: String, val point: Long)
data class PointResponse(val userId: String, val point: Long, val message: String)



@RestController
class PointController {
    @GetMapping(value = ["/user/{userId}/point"])
    fun getPoint(@PathVariable userId: String): PointResponse {
        return PointResponse(userId, 0L, "user point info")
    }
}
