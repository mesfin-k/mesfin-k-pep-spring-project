package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    // Register a new account
    public Account register(Account account) {
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        return accountRepository.save(account);
    }

    // Login a user
    public Optional<Account> login(String username, String password) {
        return accountRepository.findByUsernameAndPassword(username, password);
    }

    // Fetch an account by ID
    public Optional<Account> getAccountById(Integer id) {
        return accountRepository.findById(id);
    }

    // Fetch all accounts
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Update an existing account
    public Account updateAccount(Integer id, Account accountDetails) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setUsername(accountDetails.getUsername());
            account.setPassword(accountDetails.getPassword());
            return accountRepository.save(account); 
        } else {
            throw new RuntimeException("Account not found for id: " + id);
        }
    }

    // Delete an account by ID
    public void deleteAccountById(Integer id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            accountRepository.deleteById(id);
        } else {
            throw new RuntimeException("Account not found for id: " + id);
        }
    }
}
