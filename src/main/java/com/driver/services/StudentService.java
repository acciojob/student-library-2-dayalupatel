package com.driver.services;

import com.driver.repositories.StudentRepository;
import com.driver.models.Card;
import com.driver.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    CardService cardService4;

    @Autowired
    StudentRepository studentRepository4;

    public Student getDetailsByEmail(String email){
        return studentRepository4.findByEmailId(email);
    }

    public Student getDetailsById(int id){
        return studentRepository4.findById(id).get();
    }

    public void createStudent(Student student){
        Card newCard = cardService4.createAndReturn(student);
    }

    public void updateStudent(Student student){
        studentRepository4.updateStudentDetails(student);
    }

    public void deleteStudent(int id){
        cardService4.deactivateCard(id);
        studentRepository4.deleteCustom(id);
    }
}
