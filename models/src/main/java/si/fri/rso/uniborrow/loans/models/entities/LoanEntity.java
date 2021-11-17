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

    @Column(name = "fromId")
    private Integer fromId;

    @Column(name = "toId")
    private Integer toId;

    @Column(name = "starts")
    private Instant startTime;

    @Column(name = "ends")
    private Instant endTime;

}