package com.inbank.credit.engine.domain;

import com.inbank.credit.engine.domain.document.LoanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoanRepository extends MongoRepository<LoanDocument, String> {

    LoanDocument findByPersonalCode(String personalCode);
}
