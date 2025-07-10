package com.eaglebank.api.service.account;

import com.eaglebank.api.dto.account.BankAccountRequest;
import com.eaglebank.api.dto.account.BankAccountResponse;

public interface BankAccountService {

    /**
     * Creates a new bank account.
     *
     * @param accountRequest the request containing account details
     * @return the created bank account
     */
    BankAccountResponse createBankAccount(BankAccountRequest accountRequest);

    /**
     * Retrieves a bank account by its ID.
     *
     * @param accountId the ID of the bank account
     * @return the bank account details
     */
    BankAccountResponse getBankAccountById(String accountId);

    /**
     * Updates an existing bank account for a specific user.
     *
     * @param accountId the ID of the bank account to update
     * @param accountRequest the request containing updated account details
     * @param userEmail the email of the authenticated user
     * @return the updated bank account
     */
    BankAccountResponse updateBankAccountForUser(String accountId, BankAccountRequest accountRequest, String userEmail);

    /**
     * Retrieves a bank account by its ID for a specific user.
     *
     * @param accountId the ID of the bank account
     * @param userEmail the email of the authenticated user
     * @return the bank account details if it belongs to the user
     */
    BankAccountResponse getBankAccountByIdForUser(String accountId, String userEmail);

    /**
     * Retrieves all bank accounts for a specific user.
     *
     * @param userEmail the email of the authenticated user
     * @return list of bank accounts belonging to the user
     */
    java.util.List<BankAccountResponse> getBankAccountsForUser(String userEmail);
}

