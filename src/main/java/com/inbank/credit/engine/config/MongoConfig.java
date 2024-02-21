package com.inbank.credit.engine.config;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories({"com.inbank.credit.engine.domain.repository"})
@EnableMongock
public class MongoConfig {
}
