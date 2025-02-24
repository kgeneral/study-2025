package com.bruce.study.architecture

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class Response(val nanotime:Long, val message: String)

@RestController
class ApplicationController {
	@GetMapping(value = ["/"])
	fun hello() = Response(System.nanoTime(), "Hello World")

	@GetMapping(value = ["/heapmemtest"])
	fun heapMemTest(): List<Response> {
		val list = mutableListOf<Response>()
		for (i in 1..1000000) {
			list.add(Response(System.nanoTime(), "test"))
		}
		return list
	}
}

@SpringBootApplication
class ArchitectureApplication

fun main(args: Array<String>) {
	runApplication<ArchitectureApplication>(*args)
}
