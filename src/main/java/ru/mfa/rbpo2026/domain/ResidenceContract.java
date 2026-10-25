package ru.mfa.rbpo2026.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import java.time.LocalDate;

/** Договор проживания: заявление, комната и сроки заселения. */
@Entity @Check(constraints="move_out_date IS NULL OR move_out_date > move_in_date") @Table(name="residence_contracts", indexes={@Index(name="idx_contract_application", columnList="application_id"), @Index(name="idx_contract_room_status", columnList="room_id,status")})
public class ResidenceContract {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="application_id", nullable=false) private Long applicationId;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="application_id", insertable=false, updatable=false, foreignKey=@ForeignKey(name="fk_contract_application")) private HousingApplication application;
    @Column(name="room_id", nullable=false) private Long roomId;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="room_id", insertable=false, updatable=false, foreignKey=@ForeignKey(name="fk_contract_room")) private Room room;
    @Column(name="move_in_date", nullable=false) private LocalDate moveInDate;
    @Column(name="move_out_date") private LocalDate moveOutDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private ContractStatus status;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getApplicationId(){return applicationId;} public void setApplicationId(Long v){this.applicationId=v;}
    public HousingApplication getApplication(){return application;}
    public Long getRoomId(){return roomId;} public void setRoomId(Long v){this.roomId=v;}
    public Room getRoom(){return room;}
    public LocalDate getMoveInDate(){return moveInDate;} public void setMoveInDate(LocalDate v){this.moveInDate=v;}
    public LocalDate getMoveOutDate(){return moveOutDate;} public void setMoveOutDate(LocalDate v){this.moveOutDate=v;}
    public ContractStatus getStatus(){return status;} public void setStatus(ContractStatus v){this.status=v;}
}
