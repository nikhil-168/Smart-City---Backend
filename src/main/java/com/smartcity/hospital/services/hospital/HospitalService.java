package com.smartcity.hospital.services.hospital;

import java.sql.Blob;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.smartcity.hospital.dao.CitizenDao;
import com.smartcity.hospital.dao.HospitalDao;
import com.smartcity.hospital.helper.HospitalResponse;
import com.smartcity.hospital.helper.JwtUtil;
import com.smartcity.hospital.helper.ResponseMessage;
import com.smartcity.hospital.model.Hospital;

@Component
public class HospitalService {
    
    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CitizenDao citizenDao;

    @Autowired
    private HospitalDao hospitalDao;

    @PersistenceContext
    public EntityManager entityManager;


    public ResponseEntity<?> addHospital(String authorization, Hospital hospital) {
        try {
            String token = authorization.substring(7);

            String email = jwtUtil.extractUsername(token); 
            
            if(citizenDao.getCitizenByemail(email)!=null) {
                responseMessage.setMessage("You aren't authorised to add hospitals......");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMessage);
            }

            hospitalDao.save(hospital);

            responseMessage.setMessage("Hospital added successfully......");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
        
    }

//    public ResponseEntity<?> addHospitalImage(String authorization, int id, MultipartFile image) {
//        try {
//            String token = authorization.substring(7);
//
//            String email = jwtUtil.extractUsername(token); 
//            
//            if(citizenDao.getCitizenByemail(email)!=null) {
//                responseMessage.setMessage("You aren't authorised to add hospitals......");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMessage);
//            }
//            Hospital hospital = hospitalDao.getHospitalById(id);
//            if (hospital==null) {
//                responseMessage.setMessage("Hospital does not exist........");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
//            }
//            Session session = entityManager.unwrap(Session.class);
//            Blob hospitalImage = session.getLobHelper().createBlob(image.getInputStream(),image.getSize());
//            hospital.setImg(hospitalImage);
//            hospitalDao.save(hospital);
//            responseMessage.setMessage("Image added successfully....");
//            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//            responseMessage.setMessage(e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
//        }
//    }

    public ResponseEntity<?> getHospitals(String authorization) {
        try {
        	String email = jwtUtil.extractUsername(authorization.substring(7));
        	if (citizenDao.getCitizenByemail(email)==null) {
        		responseMessage.setMessage("Forbidden...");
        		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseMessage);
        	}
            return ResponseEntity.status(HttpStatus.OK).body(hospitalDao.findAll()); 
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    public ResponseEntity<?> getHospital(int id) {
        try {
            Hospital hospital = hospitalDao.getHospitalById(id);
            if (hospital==null) {
                responseMessage.setMessage("Hospital not found....");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
            return ResponseEntity.status(HttpStatus.OK).body(hospital);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    public ResponseEntity<?> deleteHospital(String authorization, int id) {
        try {
            String token = authorization.substring(7);

            String email = jwtUtil.extractUsername(token); 
            
            if(citizenDao.getCitizenByemail(email)!=null) {
                responseMessage.setMessage("You aren't authorised to add hospitals......");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMessage);
            }
            Hospital hospital = hospitalDao.getHospitalById(id);
            if (hospital==null) {
                responseMessage.setMessage("Hospital does not exist........");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
            hospitalDao.delete(hospital);
            responseMessage.setMessage("Hospital deleted successfully......");
            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}