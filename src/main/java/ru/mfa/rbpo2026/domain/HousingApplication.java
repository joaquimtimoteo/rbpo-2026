package ru.mfa.rbpo2026.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Заявление на проживание в общежитии. */
@Entity @Table(name="housing_applications", indexes=@Index(name="idx_application_applicant", columnList="applicant_id"))
public class HousingApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="applicant_id", nullable=false) private Long applicantId;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="applicant_id", insertable=false, updatable=false, foreignKey=@ForeignKey(name="fk_application_applicant")) private User applicant;
    @Column(name="submitted_at", nullable=false) private LocalDate submittedAt;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private ApplicationStatus status;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getApplicantId(){return applicantId;} public void setApplicantId(Long v){this.applicantId=v;}
    public User getApplicant(){return applicant;}
    public LocalDate getSubmittedAt(){return submittedAt;} public void setSubmittedAt(LocalDate v){this.submittedAt=v;}
    public ApplicationStatus getStatus(){return status;} public void setStatus(ApplicationStatus v){this.status=v;}
}
