package ua.APIexplorer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Getter
@Setter
@Table(name = "api")
public class Api {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apiID")
    private Long id;

    @Column(name = "api_NAME")
    private String name;

    @Column(name = "api_VERSION")
    private String version;

    @Lob
    @Column(name = "api_SPEC", columnDefinition = "LONGTEXT")
    private String spec;
}
