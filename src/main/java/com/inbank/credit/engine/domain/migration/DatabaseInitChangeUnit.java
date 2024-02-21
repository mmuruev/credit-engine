package com.inbank.credit.engine.domain.migration;

import com.inbank.credit.engine.domain.document.LoanDocument;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@ChangeUnit(order = "001", id = "database_init_unit", author = "mmuruev")
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitChangeUnit {
    private final MongoTemplate mongoTemplate;

    /**
     * This is the method with the migration code
     **/
    @Execution
    public void changeSet() {
        List<LoanDocument> loanDocuments = List.of(
                new LoanDocument(null, "49002010965", -1),
                new LoanDocument(null, "49002010976", 100),
                new LoanDocument(null, "49002010987", 300),
                new LoanDocument(null, "49002010998", 1000)
        );

        loanDocuments.forEach(mongoTemplate::save);
        log.info("Init migration has applied {} total records", loanDocuments.size());
    }

    /**
     * This method is mandatory even when transactions are enabled.
     * They are used in the undo operation and any other scenario where transactions are not an option.
     * However, note that when transactions are available and Mongock need to rollback, this method is ignored.
     **/
    @RollbackExecution
    public void rollback() {
        log.info("Init rollback");
        mongoTemplate.dropCollection(LoanDocument.class);
    }

}
