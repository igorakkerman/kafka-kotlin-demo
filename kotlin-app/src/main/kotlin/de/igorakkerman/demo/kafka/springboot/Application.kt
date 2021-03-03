package de.igorakkerman.demo.kafka.springboot

import de.igorakkerman.demo.kafka.application.GameService
import de.igorakkerman.demo.kafka.application.MoveProducer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication(scanBasePackages = ["de.igorakkerman.demo.kafka"])
class Application

@Configuration
class ServiceConfiguration {
    @Bean
    fun gameService(moveProducer: MoveProducer) = GameService(moveProducer)
}

fun main(commandLineArguments: Array<String>) {
    runApplication<Application>(*commandLineArguments)
}
