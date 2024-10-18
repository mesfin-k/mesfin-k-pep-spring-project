package com.example.service;

import com.example.entity.Message;
import com.example.entity.Account;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    // Fetch all messages
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Fetch a message by ID
    public Optional<Message> getMessageById(Integer id) {
        return messageRepository.findById(id);
    }

    public List<Message> getMessagesByUserId(Integer userId) {
        return messageRepository.findMessagesByPostedBy(userId); 
    }
    

    // Save a new message or update an existing one
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    // Delete a message by ID
    public boolean deleteMessageById(Integer id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
            return true;  // Indicate successful deletion
        } else {
            return false;  // Message not found
        }
    }
}
