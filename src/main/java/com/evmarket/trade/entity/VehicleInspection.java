package com.evmarket.trade.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicleinspection")
public class VehicleInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_id")
    private Long inspectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector;

    @Column(name = "battery_health_percent")
    private Integer batteryHealthPercent;

    @Column(name = "motor_condition", columnDefinition = "nvarchar(255)")
    private String motorCondition;

    @Column(name = "brake_condition", columnDefinition = "nvarchar(255)")
    private String brakeCondition;

    @Column(name = "tire_condition", columnDefinition = "nvarchar(255)")
    private String tireCondition;

    @Column(name = "overall_condition", columnDefinition = "nvarchar(255)")
    private String overallCondition;

    @Column(name = "certification_label", columnDefinition = "nvarchar(255)")
    private String certificationLabel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getInspectionId() { return inspectionId; }
    public void setInspectionId(Long inspectionId) { this.inspectionId = inspectionId; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public User getInspector() { return inspector; }
    public void setInspector(User inspector) { this.inspector = inspector; }
    public Integer getBatteryHealthPercent() { return batteryHealthPercent; }
    public void setBatteryHealthPercent(Integer batteryHealthPercent) { this.batteryHealthPercent = batteryHealthPercent; }
    public String getMotorCondition() { return motorCondition; }
    public void setMotorCondition(String motorCondition) { this.motorCondition = motorCondition; }
    public String getBrakeCondition() { return brakeCondition; }
    public void setBrakeCondition(String brakeCondition) { this.brakeCondition = brakeCondition; }
    public String getTireCondition() { return tireCondition; }
    public void setTireCondition(String tireCondition) { this.tireCondition = tireCondition; }
    public String getOverallCondition() { return overallCondition; }
    public void setOverallCondition(String overallCondition) { this.overallCondition = overallCondition; }
    public String getCertificationLabel() { return certificationLabel; }
    public void setCertificationLabel(String certificationLabel) { this.certificationLabel = certificationLabel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}