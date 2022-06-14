package berthbooking.model;

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

    @Column(name="port_name")
    private String portName;

    private String email;

    @Column(name="nr_of_guest_berths")
    private int numberOfGuestBerths;

    @OneToMany(mappedBy = "port")
    private List<Berth> berths = new ArrayList<>();

    public void addBerth(Berth berth) {
        berths.add(berth);
        berth.setPort(this);
    }

}
