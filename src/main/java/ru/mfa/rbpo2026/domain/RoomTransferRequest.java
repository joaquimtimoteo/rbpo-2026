package ru.mfa.rbpo2026.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Заявка на переселение в другую комнату. */
@Entity @Table(name="room_transfer_requests", indexes={@Index(name="idx_transfer_contract", columnList="contract_id"), @Index(name="idx_transfer_target_room", columnList="target_room_id")})
public class RoomTransferRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="contract_id", nullable=false) private Long contractId;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="contract_id", insertable=false, updatable=false, foreignKey=@ForeignKey(name="fk_transfer_contract")) private ResidenceContract contract;
    @Column(name="target_room_id", nullable=false) private Long targetRoomId;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="target_room_id", insertable=false, updatable=false, foreignKey=@ForeignKey(name="fk_transfer_target_room")) private Room targetRoom;
    @Column(name="submitted_at", nullable=false) private LocalDate submittedAt;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private TransferStatus status;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getContractId(){return contractId;} public void setContractId(Long v){this.contractId=v;}
    public ResidenceContract getContract(){return contract;}
    public Long getTargetRoomId(){return targetRoomId;} public void setTargetRoomId(Long v){this.targetRoomId=v;}
    public Room getTargetRoom(){return targetRoom;}
    public LocalDate getSubmittedAt(){return submittedAt;} public void setSubmittedAt(LocalDate v){this.submittedAt=v;}
    public TransferStatus getStatus(){return status;} public void setStatus(TransferStatus v){this.status=v;}
}
