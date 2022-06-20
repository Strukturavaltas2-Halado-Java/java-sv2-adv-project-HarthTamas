package berthbooking.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="ports")
public class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="town")
    private String town;

    private String email;

    @Column(name="nr_of_guest_berths")
    private int numberOfGuestBerths;

    @OneToMany(mappedBy = "port", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Berth> berths = new ArrayList<>();


    public Port(String town, String email, int numberOfGuestBerths) {
        this.town = town;
        this.email = email;
        this.numberOfGuestBerths = numberOfGuestBerths;
    }

    public void addBerth(Berth berth) {
        berths.add(berth);
        berth.setPort(this);
    }

}
