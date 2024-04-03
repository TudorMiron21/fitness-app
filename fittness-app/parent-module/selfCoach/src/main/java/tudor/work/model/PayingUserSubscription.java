package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PayingUserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //Only paying users can subscribe to a coach
    @ManyToOne
    @JoinColumn(name = "id_coach")
    @JsonBackReference
    private User subscribeTo;

    private LocalDateTime subscriptionTime;
}
