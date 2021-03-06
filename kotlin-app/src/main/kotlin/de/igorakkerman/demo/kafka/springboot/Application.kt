package de.igorakkerman.demo.kafka.springboot

import de.igorakkerman.demo.kafka.application.GameService
import de.igorakkerman.demo.kafka.application.MoveMessageProducer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

fun main(commandLineArguments: Array<String>) {
    runApplication<Application>(*commandLineArguments)
}

@SpringBootApplication(scanBasePackages = ["de.igorakkerman.demo.kafka"])
class Application

@Configuration
class ServiceConfiguration {
    @Bean
    fun gameService(moveMessageProducer: MoveMessageProducer) = GameService(moveMessageProducer)
}

