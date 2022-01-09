package si.fri.rso.uniborrow.loans.services.cash;

import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@ToString
public class TransactionEntity {

    private Integer id;

    private Float cash;

    private Instant timestamp;

    private Integer fromId;

    private Integer toId;
}

