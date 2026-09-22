package ee.testiplatvorm.persistence.competencefile;

import ee.testiplatvorm.persistence.competence.Competence;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "competence_file", schema = "testi_platvorm")
public class CompetenceFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    @Size(max = 255)
    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Size(max = 50)
    @NotNull
    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    @NotNull
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;


}