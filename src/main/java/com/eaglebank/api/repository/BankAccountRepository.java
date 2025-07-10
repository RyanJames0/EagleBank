package com.eaglebank.api.repository;

import com.eaglebank.api.model.account.BankAccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

}
