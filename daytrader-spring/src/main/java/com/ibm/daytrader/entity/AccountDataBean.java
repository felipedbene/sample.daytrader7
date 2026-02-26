package com.ibm.daytrader.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "accountejb")
public class AccountDataBean implements Serializable {

    private static final long serialVersionUID = 8437841265136840545L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNTID", nullable = false)
    private Integer accountID;

    @NotNull
    @Column(name = "LOGINCOUNT", nullable = false)
    private int loginCount;

    @NotNull
    @Column(name = "LOGOUTCOUNT", nullable = false)
    private int logoutCount;

    @Column(name = "LASTLOGIN")
    private LocalDateTime lastLogin;

    @Column(name = "CREATIONDATE")
    private LocalDateTime creationDate;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "OPENBALANCE")
    private BigDecimal openBalance;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<OrderDataBean> orders;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<HoldingDataBean> holdings;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_USERID")
    private AccountProfileDataBean profile;

    @Transient
    private String profileID;

    public AccountDataBean() {
    }

    public AccountDataBean(int loginCount, int logoutCount, LocalDateTime lastLogin,
                           LocalDateTime creationDate, BigDecimal balance, BigDecimal openBalance,
                           String profileID) {
        this.loginCount = loginCount;
        this.logoutCount = logoutCount;
        this.lastLogin = lastLogin;
        this.creationDate = creationDate;
        this.balance = balance;
        this.openBalance = openBalance;
        this.profileID = profileID;
    }

    public void login() {
        this.lastLogin = LocalDateTime.now();
        this.loginCount++;
    }

    public void logout() {
        this.logoutCount++;
    }

    @Override
    public String toString() {
        return "Account Data for account: " + accountID
                + " loginCount:" + loginCount
                + " logoutCount:" + logoutCount
                + " lastLogin:" + lastLogin
                + " creationDate:" + creationDate
                + " balance:" + balance
                + " openBalance:" + openBalance
                + " profileID:" + getProfileID();
    }

    // Getters and Setters

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public int getLogoutCount() {
        return logoutCount;
    }

    public void setLogoutCount(int logoutCount) {
        this.logoutCount = logoutCount;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getOpenBalance() {
        return openBalance;
    }

    public void setOpenBalance(BigDecimal openBalance) {
        this.openBalance = openBalance;
    }

    public String getProfileID() {
        if (profile != null) {
            return profile.getUserID();
        }
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public List<OrderDataBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDataBean> orders) {
        this.orders = orders;
    }

    public List<HoldingDataBean> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<HoldingDataBean> holdings) {
        this.holdings = holdings;
    }

    public AccountProfileDataBean getProfile() {
        return profile;
    }

    public void setProfile(AccountProfileDataBean profile) {
        this.profile = profile;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountID);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AccountDataBean other)) return false;
        return Objects.equals(accountID, other.accountID);
    }
}
