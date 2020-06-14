package com.bompotis.netcheck.data.entities;

import javax.persistence.*;

/**
 * Created by Kyriakos Bompotis on 14/6/20.
 */
@Entity
@Table(name = "https_check")
public class HttpsCheckEntity extends HttpCheckEntity{

    @ManyToOne(targetEntity = DomainCertificateEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "issuerCertificate_id")
    private DomainCertificateEntity issuerCertificate;


    public DomainCertificateEntity getIssuerCertificate() {
        return issuerCertificate;
    }

    public void setIssuerCertificate(DomainCertificateEntity issuerCertificate) {
        this.issuerCertificate = issuerCertificate;
    }
}
