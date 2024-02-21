package com.inbank.credit.engine.domain.repository;

import com.inbank.credit.engine.domain.document.LoanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LoanRepository extends MongoRepository<LoanDocument, String> {

    Optional<LoanDocument> findByPersonalCode(String personalCode);
}
