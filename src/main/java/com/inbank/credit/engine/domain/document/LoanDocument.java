package com.inbank.credit.engine.domain.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "loan_document")
@Getter
@Setter
@NoArgsConstructor
public class LoanDocument {
    @Id
    private String id;

    @Field("personal_code")
    @Indexed(unique = true)
    private String personalCode;

    @Field("modifier")
    private Integer modifier;
}
