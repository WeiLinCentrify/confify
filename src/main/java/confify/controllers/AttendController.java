package confify.controllers;

import confify.exception.ErrorMessage;
import confify.exception.ResponseMessage;
import confify.models.*;
import confify.repositories.AttendRepository;
import confify.repositories.ConferenceRepository;
import confify.repositories.ManageRepository;
import confify.repositories.UserRepository;
import confify.service.TokenDecoder;
import org.hibernate.Hibernate;
import org.json.HTTP;
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
public class AttendController {
    @Autowired
    private ConferenceRepository conferenceRepository;
    @Autowired
    private AttendRepository attendRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ManageRepository manageRepository;

    @RequestMapping(value = "users/{userId}/conferences/{conferId}/accept", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> rsvpConference(@PathVariable("userId") long userId, @PathVariable("conferId") long conferId) {
        Attend attend = attendRepository.getAttendByUserIdAndConferId(conferId, userId);
        if (attend == null) return new ResponseEntity(new ErrorMessage("Conference Not Found"), HttpStatus.NOT_FOUND);
        if (attend.getStatus() == AttendStatus.ACCEPCTED)
            new ResponseEntity(new ErrorMessage("Conference Already Accept"), HttpStatus.OK);
        attend.setStatus(AttendStatus.ACCEPCTED);
        return new ResponseEntity(attend, HttpStatus.OK);
    }

    @RequestMapping(value = "users/{userId}/conferences/{conferId}/unaccept", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> unrsvpConference(@PathVariable("userId") long userId, @PathVariable("conferId") long conferId) {
        Attend attend = attendRepository.getAttendByUserIdAndConferId(conferId, userId);
        if (attend == null) return new ResponseEntity(new ErrorMessage("Conference Not Found"), HttpStatus.NOT_FOUND);
        if (attend.getStatus() == AttendStatus.INVITED)
            new ResponseEntity(new ErrorMessage("Conference Not Accepted"), HttpStatus.OK);
        attend.setStatus(AttendStatus.INVITED);
        return new ResponseEntity(attend, HttpStatus.OK);
    }

    @RequestMapping(value = "users/{userId}/attends", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getAttends(@RequestHeader("Authorization") String token, @PathVariable("userId") long userId) {
        List<Attend> attends = attendRepository.getAttendsByUserId(userId);
        return new ResponseEntity(attends, HttpStatus.OK);
    }

    @RequestMapping(value = "users/{userId}/conferences/{conferId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getConferenceInfo(@PathVariable("userId") long userId, @PathVariable("conferId") long conferId) {
        Attend attend = attendRepository.getAttendByUserIdAndConferId(conferId, userId);
        if (attend == null) return new ResponseEntity(new ErrorMessage("Conference Not Found"), HttpStatus.NOT_FOUND);
        Conference conference = attend.getConference();
        return new ResponseEntity(conference, HttpStatus.OK);
    }

    @RequestMapping(value = "users/{userId}/conferences/{conferId}/attends", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getConferenceAttends(@PathVariable("userId") long userId, @PathVariable("conferId") long conferId) {
        Attend attend = attendRepository.getAttendByUserIdAndConferId(conferId, userId);
        if (attend == null) return new ResponseEntity(new ErrorMessage("Conference Not Found"), HttpStatus.NOT_FOUND);
        List<Attend> attends = attendRepository.getAttendsByConferId(conferId);
        return new ResponseEntity(attends, HttpStatus.OK);
    }

    @RequestMapping(value = "/conferences/{conferId}/attendees/{userId}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<?> getConferenceAttends(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                                  @PathVariable("userId") long userId, @PathVariable("conferId") long conferId) {
        if (accountType.equalsIgnoreCase(accountType)) {
            Credential credential = TokenDecoder.decode(token);
            Manage manage = manageRepository.getManageByConferIdAndAdminEmail(conferId, credential.getUsername());
            if (manage == null) return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
            Attend attend = attendRepository.getAttendByUserIdAndConferId(conferId, userId);
            if (attend == null) return new ResponseEntity(new ErrorMessage("Attendee Not Found"), HttpStatus.NOT_FOUND);
            attendRepository.delete(attend);
            return new ResponseEntity(new ResponseMessage("Attendee is Deleted"), HttpStatus.OK);
        }
        else {
            return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        }
    }
}
