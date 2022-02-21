package com.example.offerdaysongs.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@NamedEntityGraph(
        name = "copyright-entity-graph-with-company-recording",
        attributeNodes = {
                @NamedAttributeNode("company"),
                @NamedAttributeNode(value = "recording", subgraph = "recording-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(name = "recording-subgraph",
                        attributeNodes = @NamedAttributeNode("singer"))
        }
)
public class Copyright {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double royalty;
    private ZonedDateTime startTime;
    private ZonedDateTime expiryTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recording_id")
    @ToString.Exclude
    private Recording recording;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @ToString.Exclude
    private Company company;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Copyright copyright = (Copyright) o;
        return id != null && Objects.equals(id, copyright.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
