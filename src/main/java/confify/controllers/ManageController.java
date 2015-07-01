package confify.controllers;

import confify.exception.ErrorMessage;
import confify.exception.ResponseMessage;
import confify.models.*;
import confify.repositories.*;
import confify.service.TokenDecoder;
import confify.util.DataTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Dennis on 4/24/2015.
 */
@RestController
@RequestMapping(value = "/api")
public class ManageController {
    @Autowired private ConferenceRepository conferenceRepository;
    @Autowired private AttendRepository attendRepository;
    @Autowired private ManageRepository manageRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private GiveSpeechRepository giveSpeechRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DataTransfer dataTransfer;

    @RequestMapping(value = "admins/{adminId}/conferences", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> createConference(@PathVariable("adminId") int adminId, @RequestBody Conference conference){
        Admin admin = adminRepository.getAdminById(adminId);
        if (admin == null) return new ResponseEntity(new ErrorMessage("Admin Not Found"), HttpStatus.NOT_FOUND);
        Conference newConfer = conferenceRepository.save(conference);
        Manage manage = new Manage();
        manage.setAdmin(admin);
        manage.setConference(newConfer);
        manageRepository.save(manage);
        return new ResponseEntity(newConfer, HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> updateConference(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId, @RequestBody Conference input){
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.NOT_FOUND);
        Conference conference = manage.getConference();
        dataTransfer.conferenceDataTransfer(input, conference);
        conferenceRepository.save(conference);
        return new ResponseEntity(conference, HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}", method = RequestMethod.GET)
    public ResponseEntity<?> getManagedConference(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId){
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.NOT_FOUND);
        return new ResponseEntity(manage.getConference(), HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteConference(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId){
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.NOT_FOUND);
        manageRepository.deleteManageByConferenceId(conferId);
        conferenceRepository.delete(manage.getConference());
        return new ResponseEntity(manage.getConference(), HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}/unmanage", method = RequestMethod.PUT)
    public ResponseEntity<?> unmanageConference(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId){
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.NOT_FOUND);
        manageRepository.delete(manage);
        return new ResponseEntity(manage.getConference(), HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}/addManager", method = RequestMethod.POST)
    public ResponseEntity<?> addConferenceManager(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId, @RequestBody Admin input){
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.UNAUTHORIZED);
        Admin admin = adminRepository.getAdminById(input.getId());
        if (admin == null) return new ResponseEntity(new ErrorMessage("Admin Not Found"), HttpStatus.NOT_FOUND);
        Manage newManage = new Manage();
        newManage.setConference(manage.getConference());
        newManage.setAdmin(admin);
        manageRepository.save(newManage);
        return new ResponseEntity(manage.getConference(), HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}/invite", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> inviteUser2Conference(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId, @RequestBody List<Long> userIds){
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.NOT_FOUND);
        for (long userId : userIds ) {
            attendRepository.insert(conferId, userId, AttendStatus.INVITED);
        }
        return new ResponseEntity(manage.getConference(), HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{admins}/manages", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getManages(@PathVariable("userId") long userId) {
        Admin admin = adminRepository.getAdminById(userId);
        if (admin == null) return new ResponseEntity(new ErrorMessage("Admin Not Found"), HttpStatus.BAD_REQUEST);
        List<Manage> manages = admin.getManages();
        return new ResponseEntity(manages, HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}/attends", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getConferenceAttends(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId) {
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.UNAUTHORIZED);
        List<Attend> attends = attendRepository.getAttendsByConferId(conferId);
        return new ResponseEntity(attends, HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}/checkin", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> getConferenceAttends(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId, @RequestBody User input) {
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.UNAUTHORIZED);
        Attend attend = attendRepository.getAttendByUserIdAndConferId(conferId, input.getId());
        if (attend == null) return new ResponseEntity(new ErrorMessage("User is not invited"), HttpStatus.NOT_FOUND);
        if (attend.getStatus() == AttendStatus.INVITED) return new ResponseEntity(new ErrorMessage("User have not accepted invitation"), HttpStatus.NOT_FOUND);
        attend.setStatus(AttendStatus.CHECKED_IN);
        return new ResponseEntity(attend, HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}/giveSpeeches", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getConferenceSpeaker(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId) {
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.UNAUTHORIZED);
        List<GiveSpeech> giveSpeeches = giveSpeechRepository.getGiveSpeechByConferId(conferId);
        return new ResponseEntity(giveSpeeches, HttpStatus.OK);
    }

    @RequestMapping(value = "admins/{adminId}/conferences/{conferId}/giveSpeeches", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> addConferenceSpeaker(@PathVariable("adminId") long adminId, @PathVariable("conferId") long conferId, @RequestBody User input) {
        Manage manage = manageRepository.getManageByIdAndAdminId(conferId, adminId);
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.UNAUTHORIZED);
        User user = userRepository.getUserById(input.getId());
        if (user == null) return new ResponseEntity(new ErrorMessage("Speaker Not Found"), HttpStatus.NOT_FOUND);
        GiveSpeech giveSpeech = giveSpeechRepository.getGiveSpeechByConferIdAndUserId(conferId, user.getId());
        if (giveSpeech != null) return new ResponseEntity(new ErrorMessage("Speaker Already Invited"), HttpStatus.OK);
        giveSpeech = new GiveSpeech();
        giveSpeech.setConference(manage.getConference());
        giveSpeech.setSpeaker(user);
        giveSpeechRepository.save(giveSpeech);
        return new ResponseEntity(giveSpeech, HttpStatus.OK);
    }

}
