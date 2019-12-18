package msadaka.models;


import msadaka.enums.PayBillStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;

@Entity
@Table(name = "churches")
public class Church implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;
    private String altName;
    private String shortCode;
    @Column(unique = true)
    private String payBill;
    @Enumerated(EnumType.STRING)
    PayBillStatus status;
    private Instant creationDate;
    private Instant lastRenewed;
    private Instant expiryDate;
    private String userEmail;
    private String mpesaAppkey;
    private String mpesaAppsecret;
    private String mpesaPasskey;
    @Transient
    private String objectID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String getPayBill() {
        return payBill;
    }

    public void setPayBill(String payBill) {
        this.payBill = payBill;
    }

    public PayBillStatus getStatus() {
        return status;
    }

    public void setStatus(PayBillStatus status) {
        this.status = status;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getLastRenewed() {
        return lastRenewed;
    }

    public void setLastRenewed(Instant lastRenewed) {
        this.lastRenewed = lastRenewed;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String useremail) {
        this.userEmail = useremail;
    }

    public String getMpesaAppkey() {
        return mpesaAppkey;
    }

    public void setMpesaAppkey(String mpesaAppkey) {
        this.mpesaAppkey = mpesaAppkey;
    }

    public String getMpesaAppsecret() {
        return mpesaAppsecret;
    }

    public void setMpesaAppsecret(String mpesaAppsecret) {
        this.mpesaAppsecret = mpesaAppsecret;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getMpesaPasskey() {
        return mpesaPasskey;
    }

    public void setMpesaPasskey(String mpesaPasskey) {
        this.mpesaPasskey = mpesaPasskey;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }
}
