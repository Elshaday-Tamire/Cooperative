package com.dxvalley.project.services;

import java.util.List;

import com.dxvalley.project.models.AccountBalance;

public interface AccountBalanceService {
    AccountBalance addAccountBalance(AccountBalance accountBalance);
    AccountBalance editAccountBalance (AccountBalance accountBalance);
    List<AccountBalance> getAccountBalances();
    AccountBalance getAccountBalanceById(Long accountBalanceId);
    AccountBalance getAccountBalanceByDateGenerated(String dateGenerated);
    void deleteAccountBalance( Long accountBalanceId);
}
