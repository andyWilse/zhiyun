package com.religion.zhiyun.venues.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "RM_VENUES_INFO")
public  class VenuesEntity implements Serializable {
    @Id
    private String id;

    @Column(name = "VENUES_ID")
    private String venuesId;//主键ID

    @Column(name = "VENUES_ADDRES")
    private String venuesAddres;//场所地址

    @Column(name = "PICTURES_ONE")
    private String picturesOne;//照片1

    @Column(name = "PICTURES_TWO")
    private String picturesTwo;//照片2

    @Column(name = "PICTURES_THREE")
    private String picturesThree;//照片3

    @Column(name = "RESPONSIBLE_PERSON")
    private String responsiblePerson;//负责人

    @Column(name = "NUMBER")
    private String number;//号码

    @Column(name = "USE_STATUS")
    private String useStatus;//使用状态

    @Column(name = "CREATOR")
    private String creator;//创建人

    @Column(name = "CREATE_TIME")
    private String createTime;//创建时间

    @Column(name = "LAST_MODIFIER")
    private String lastModifier;//最后修改人

    @Column(name = "LAST_MODIFY_TIME")
    private String lastModifyTime;//最后修改时间

    public void setVenuesId(String venuesId) {
        this.venuesId = venuesId;
    }

    public String getVenuesId() {
        return venuesId;
    }

    public String getVenuesAddres() {
        return venuesAddres;
    }

    public void setVenuesAddres(String venuesAddres) {
        this.venuesAddres = venuesAddres;
    }

    public String getPicturesOne() {
        return picturesOne;
    }

    public void setPicturesOne(String picturesOne) {
        this.picturesOne = picturesOne;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    public String getPicturesTwo() {
        return picturesTwo;
    }

    public void setPicturesTwo(String picturesTwo) {
        this.picturesTwo = picturesTwo;
    }

    public String getPicturesThree() {
        return picturesThree;
    }

    public void setPicturesThree(String picturesThree) {
        this.picturesThree = picturesThree;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
