package com.bruce.study.architecture

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class Response(val message: String)

@RestController
class ApplicationController {
	@GetMapping(value = ["/"])
	fun hello() = Response("Hello World")
}

@SpringBootApplication
class ArchitectureApplication

fun main(args: Array<String>) {
	runApplication<ArchitectureApplication>(*args)
}
