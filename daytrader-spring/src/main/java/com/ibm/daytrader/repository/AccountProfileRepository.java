package com.ibm.daytrader.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ibm.daytrader.entity.AccountProfileDataBean;

public interface AccountProfileRepository extends JpaRepository<AccountProfileDataBean, String> {
}
