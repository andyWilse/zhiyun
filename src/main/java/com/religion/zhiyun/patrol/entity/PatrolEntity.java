package com.religion.zhiyun.patrol.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rm_patrol_info")
public class PatrolEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "PATROL_ID")
    private int patrolId;//主键ID

    @Column(name = "REL_VENUES_ID")
    private int relVenuesId;

    @Column(name = "SCORE")
    private BigDecimal score;

    @Column(name = "SAFE_PASS")
    private int safePass;
    @Column(name = "SAFE_UNCHECK")
    private int safeUncheck;
    @Column(name = "SAFE_DANGE")
    private int safeDange;

    @Column(name = "ELEC_PASS")
    private int elecPass;
    @Column(name = "ELEC_UNCHECK")
    private int elecUncheck;
    @Column(name = "ELEC_DANGE")
    private int elecDange;

    @Column(name = "DEVICE_PASS")
    private int devicePass;
    @Column(name = "DEVICE_UNCHECK")
    private int deviceUncheck;
    @Column(name = "DEVICE_DANGE")
    private int deviceDange;

    @Column(name = "EVACUATE_PASS")
    private int evacuatePass;
    @Column(name = "EVACUATE_UNCHECK")
    private int evacuateUncheck;
    @Column(name = "EVACUATE_DANGE")
    private int evacuateDange;

    @Column(name = "EDUCATE_PASS")
    private int educatePass;
    @Column(name = "EDUCATE_UNCHECK")
    private int educateUncheck;
    @Column(name = "EDUCATE_DANGE")
    private int educateDange;

    @Column(name = "YEAR_PASS")
    private int yearPass;
    @Column(name = "YEAR_UNCHECK")
    private int yearUncheck;
    @Column(name = "YEAR_DANGE")
    private int yearDange;

    @Column(name = "MONTH_PASS")
    private int monthPass;
    @Column(name = "MONTH_UNCHECK")
    private int monthUncheck;
    @Column(name = "MONTH_DANGE")
    private int monthDange;

    @Column(name = "CURRENT_PASS")
    private int currentPass;
    @Column(name = "CURRENT_UNCHECK")
    private int currentUncheck;
    @Column(name = "CURRENT_DANGE")
    private int currentDange;

    @Column(name = "BUILD")
    private int build;

    private int venuesId;
    private String venuesName;

}
