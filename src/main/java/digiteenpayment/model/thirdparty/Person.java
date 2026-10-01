package digiteenpayment.model.thirdparty;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Immutable
public class Person {

    @Id
    private Long id;

    @Embedded
    private PersonInfo personInfo;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "joinTime")
    private LocalDateTime joinTime;

    @Column(name = "trace_id")
    private String traceId;

}
