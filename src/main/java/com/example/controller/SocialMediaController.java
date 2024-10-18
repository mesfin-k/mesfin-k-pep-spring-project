package com.example.controller;

import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.entity.Account;
import com.example.service.MessageService;
import com.example.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class SocialMediaController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    // User Registration
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Account account) {
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username already taken", HttpStatus.CONFLICT);  // 409 Conflict
        }
        accountRepository.save(account);
        return new ResponseEntity<>(account, HttpStatus.OK);  // 200 OK
    }

    // User Login
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Optional<Account> loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
        return loggedInAccount.map(acc -> new ResponseEntity<>(acc, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    // POST /messages - Create a new message
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        Optional<Account> account = accountService.getAccountById(message.getPostedBy());

        if (account.isEmpty()) {
            return new ResponseEntity<>("Account not found", HttpStatus.BAD_REQUEST);  // 400 Bad Request
        }

        if (message.getMessageText().isBlank()) {
            return new ResponseEntity<>("Message cannot be blank", HttpStatus.BAD_REQUEST);
        }

        if (message.getMessageText().length() > 255) {
            return new ResponseEntity<>("Invalid message text", HttpStatus.BAD_REQUEST);  // 400 Bad Request
        }

        Message savedMessage = messageService.saveMessage(message);
        return new ResponseEntity<>(savedMessage, HttpStatus.OK);  // 200 OK
    }

    // GET /messages/{id} - get a message by ID
    @GetMapping("/messages/{id}")
    public ResponseEntity<?> getMessageById(@PathVariable Integer id) {
        Optional<Message> messageOptional = messageService.getMessageById(id);
        if (messageOptional.isEmpty()) {
            return new ResponseEntity<>("", HttpStatus.OK);  // Return 200 OK with an empty body if the message is not found
        }
        return new ResponseEntity<>(messageOptional.get(), HttpStatus.OK);  // Return the message if found
    }


    @GetMapping("/accounts/{id}/messages")
    public ResponseEntity<?> getAllMessagesByUserId(@PathVariable Integer id) {
        Optional<Account> accountOptional = accountService.getAccountById(id);
        
        if (accountOptional.isEmpty()) {
            return new ResponseEntity<>("Account not found", HttpStatus.BAD_REQUEST);  // 400 if account does not exist
        }

        // Fetch messages for the user instead of fetching by message ID
        List<Message> messages = messageService.getMessagesByUserId(id);

        if (messages.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);  // Return an empty list if no messages found
        }

        return new ResponseEntity<>(messages, HttpStatus.OK);  // Return the list of messages
    }







    // GET /messages - Retrieve all messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        if (messages.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // PUT /messages/{id} - Update a message by ID
    @PatchMapping("/messages/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Integer id, @RequestBody Message messageDetails) {
        Optional<Message> messageOptional = messageService.getMessageById(id);
        if (messageOptional.isEmpty()) {
            return new ResponseEntity<>("Message not found", HttpStatus.BAD_REQUEST);  // 400 if not found
        }

        if (messageDetails.getMessageText().isBlank()) {
            return new ResponseEntity<>("Message text cannot be empty", HttpStatus.BAD_REQUEST);  // 400 Bad Request
        }

        if (messageDetails.getMessageText().length() > 255) {
            return new ResponseEntity<>("Message text is too long", HttpStatus.BAD_REQUEST);  // 400 Bad Request
        }

        Message existingMessage = messageOptional.get();
        existingMessage.setMessageText(messageDetails.getMessageText());
        messageService.saveMessage(existingMessage);
        return new ResponseEntity<>(1, HttpStatus.OK);  // 200 OK on success
    }

    // DELETE /messages/{id} - Delete a message by ID
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer id) {
        boolean isDeleted = messageService.deleteMessageById(id);
        if (isDeleted) {
            return new ResponseEntity<>(1, HttpStatus.OK);  // Return 200 OK with the number of rows deleted
        } else {
            return new ResponseEntity<>("", HttpStatus.OK);  // Return 200 OK with empty response
        }
    }
}
