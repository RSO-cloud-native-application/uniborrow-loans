package si.fri.rso.uniborrow.loans.models.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "loans")
@NamedQueries(value =
        {
                @NamedQuery(name = "LoanEntity.getAll",
                        query = "SELECT im FROM LoanEntity im"),
                @NamedQuery(name = "LoanEntity.getByItemId",
                        query = "SELECT im FROM LoanEntity im WHERE im.itemId = :itemId")
        })
@Data
@ToString
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

    @Column(name = "price")
    private Float price;
}

