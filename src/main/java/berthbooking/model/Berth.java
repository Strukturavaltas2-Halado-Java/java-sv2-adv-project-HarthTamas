package berthbooking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name="berths")
public class Berth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private int length;

    private int width;

    @Enumerated(EnumType.STRING)
    @Column(name="berth_type")
    private BerthType berthType;

    @ElementCollection
    private List<Booking> bookings = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "port_id")
    @JsonBackReference
    private Port port;


    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public Berth(String code, int length, int width, BerthType berthType) {
        this.code = code;
        this.length = length;
        this.width = width;
        this.berthType = berthType;
    }
}
