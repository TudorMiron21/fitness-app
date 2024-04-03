package tudor.work.model;

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
public class CoachSubscribers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //only coaches have subscribers
    @ManyToOne
    @JoinColumn(name = "id_subscriber")
    @JsonBackReference
    private User subscriber;


    private LocalDateTime subscriptionTime;


}
