package ru.mfa.rbpo2026.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** Комната общежития. */
@Entity @Check(constraints="capacity > 0") @Table(name="rooms", uniqueConstraints=@UniqueConstraint(name="uk_rooms_number", columnNames="number"))
public class Room {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable=false, length=30) private String number;
    @Min(1) @Column(nullable=false) private int capacity;
    @Column(nullable=false) private boolean available=true;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getNumber(){return number;} public void setNumber(String v){this.number=v;}
    public int getCapacity(){return capacity;} public void setCapacity(int v){this.capacity=v;}
    public boolean isAvailable(){return available;} public void setAvailable(boolean v){this.available=v;}
}
