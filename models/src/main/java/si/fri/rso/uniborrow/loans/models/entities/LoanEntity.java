package si.fri.rso.uniborrow.loans.models.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "loans")
@NamedQueries(value =
        {
                @NamedQuery(name = "LoanEntity.getAll",
                        query = "SELECT im FROM LoanEntity im")
        })
@Data
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "description")
    private String description;

    @Column(name = "from_id")
    private Integer fromId;

    @Column(name = "to_id")
    private Integer toId;

    @Column(name = "starts")
    private Instant startTime;

    @Column(name = "ends")
    private Instant endTime;

    @Column(name = "proposed_by")
    private Integer proposedById;

    @Column(name = "accepted_state")
    @Enumerated(EnumType.STRING)
    private AcceptedState acceptedState;
}

