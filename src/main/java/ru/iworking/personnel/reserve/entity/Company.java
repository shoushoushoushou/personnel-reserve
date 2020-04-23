package ru.iworking.personnel.reserve.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "Company")
@Table(name = "company")
public class Company {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "datecreate")
    private LocalDateTime datecreate = LocalDateTime.now();

    @Column(name = "company_type_id")
    private Long companyTypeId;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="number_phone_id")
    private NumberPhone numberPhone;

    @Column(name = "email")
    private String email;

    @Column(name = "web_page")
    private String webPage;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="address_id")
    private Address address;

    @Column(name = "creater_id")
    private Long createrId;

    @Column(name = "logo_id")
    private Long logoId;

    public Company() { }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDatecreate() {
        return datecreate;
    }
    public void setDatecreate(LocalDateTime datecreate) {
        this.datecreate = datecreate;
    }

    public Long getCompanyTypeId() {
        return companyTypeId;
    }
    public void setCompanyTypeId(Long companyTypeId) {
        this.companyTypeId = companyTypeId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public NumberPhone getNumberPhone() {
        return numberPhone;
    }
    public void setNumberPhone(NumberPhone numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebPage() {
        return webPage;
    }
    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getCreaterId() {
        return createrId;
    }
    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }

    public Long getLogoId() {
        return logoId;
    }
    public void setLogoId(Long logoId) {
        this.logoId = logoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(datecreate, company.datecreate) &&
                Objects.equals(companyTypeId, company.companyTypeId) &&
                Objects.equals(name, company.name) &&
                Objects.equals(numberPhone, company.numberPhone) &&
                Objects.equals(email, company.email) &&
                Objects.equals(webPage, company.webPage) &&
                Objects.equals(address, company.address) &&
                Objects.equals(createrId, company.createrId) &&
                Objects.equals(logoId, company.logoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datecreate, companyTypeId, name, numberPhone, email, webPage, address, createrId, logoId);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", datecreate=" + datecreate +
                ", companyTypeId=" + companyTypeId +
                ", name='" + name + '\'' +
                ", numberPhone=" + numberPhone +
                ", email='" + email + '\'' +
                ", webPage='" + webPage + '\'' +
                ", address=" + address +
                ", createrId=" + createrId +
                ", logoId=" + logoId +
                '}';
    }
}
