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

    public Port(String portName, String email, int numberOfGuestBerths) {
        this.portName = portName;
        this.email = email;
        this.numberOfGuestBerths = numberOfGuestBerths;
    }


    @Column(name="port_name")
    private String portName;

    private String email;

    @Column(name="nr_of_guest_berths")
    private int numberOfGuestBerths;

    @OneToMany(mappedBy = "port", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Berth> berths = new ArrayList<>();

    public void addBerth(Berth berth) {
        berths.add(berth);
        berth.setPort(this);
    }

}
