package com.example.korum

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KorumApplication

fun main(args: Array<String>) {
	runApplication<KorumApplication>(*args)
}
