package confify.controllers;

import confify.exception.ErrorMessage;
import confify.exception.ResponseMessage;
import confify.models.*;
import confify.models.FileRecord;
import confify.models.input.AttendForm;
import confify.models.output.ConferenceOutput;
import confify.models.output.ConferenceWithSpeakerAndAttendee;
import confify.repositories.*;
import confify.service.*;
import confify.util.DataTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dennis on 4/23/2015.
 */
@RestController
@RequestMapping(value = "/api")
public class ConferenceController {
    private static final Logger logger = LoggerFactory.getLogger(ConferenceController.class);
    @Autowired private ConferenceRepository conferenceRepository;
    @Autowired private AttendRepository attendRepository;
    @Autowired private ManageRepository manageRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private GiveSpeechRepository giveSpeechRepository;
    @Autowired private DataTransfer dataTransfer;
    @Autowired private FileRecordRepository fileRecordRepository;
    @Autowired private StorageService storageService;
    @Autowired private EmailService emailservice;
    @Autowired private PasswordEncoder pwdEncoder;
    @Autowired private PasswordService passwordService;
    @Autowired private QRCodeService qrCodeService;

    @RequestMapping(value = "/conferences", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> createConferences(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                               @RequestBody Conference conference) {
        Credential credential = TokenDecoder.decode(token);
        if (accountType.toLowerCase().equals(AccountType.ADMIN)) {
            Admin admin = adminRepository.getAdminByEmail(credential.getUsername());
            conferenceRepository.save(conference);
            Manage manage = new Manage();
            manage.setConference(conference);
            manage.setAdmin(admin);
            manageRepository.save(manage);
            conference = manage.getConference();
            conference.setOrganization(admin.getOrganization());
            return new ResponseEntity(conference, HttpStatus.OK);
        }
        else {
            return new ResponseEntity(new ErrorMessage("AccountType Error"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/conferences", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getConferences(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType) {
        Credential credential = TokenDecoder.decode(token);
        if (accountType.toLowerCase().equals(AccountType.ADMIN)) {
            List<Conference> conferenceList = manageRepository.getConferenceByEmail(credential.getUsername());
            return new ResponseEntity(conferenceList, HttpStatus.OK);
        }
        else if (accountType.toLowerCase().equals(AccountType.USER)) {
            List<Attend> attendList = attendRepository.getNonrejectedAttendByEmail(credential.getUsername());
            List<ConferenceOutput> conferenceOutputList = new ArrayList();
            dataTransfer.attendList2ConferenceOutputList(attendList, conferenceOutputList);
            return new ResponseEntity(conferenceOutputList, HttpStatus.OK);
        }
        else {
            return new ResponseEntity(new ErrorMessage("AccountType Error"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/conferences/{conferId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<?> getConferenceInfoWithAttendeeAndSpeaker(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                               @PathVariable("conferId") long conferId) {
        Credential credential = TokenDecoder.decode(token);
        if (accountType.toLowerCase().equals(AccountType.ADMIN)) {
            Conference conference = manageRepository.getConferenceByConferIdAndAdminEmail(conferId, credential.getUsername());
            if (conference == null) return new ResponseEntity(new ErrorMessage("Conference Not Found"), HttpStatus.NOT_FOUND);
            ConferenceWithSpeakerAndAttendee conferSA = new ConferenceWithSpeakerAndAttendee();
            dataTransfer.conference2ConferenceInfoWithSpeakerAndAttendee(conference, conferSA);
            return new ResponseEntity(conferSA, HttpStatus.OK);
        }
        else if (accountType.toLowerCase().equals(AccountType.USER)) {
            Conference conference = attendRepository.getConferenceByConferIdAndAttendEmail(conferId, credential.getUsername());
            if (conference == null) return new ResponseEntity(new ErrorMessage("Conference Not Found"), HttpStatus.NOT_FOUND);
            ConferenceWithSpeakerAndAttendee conferSA = new ConferenceWithSpeakerAndAttendee();
            dataTransfer.conference2ConferenceInfoWithSpeakerAndAttendee(conference, conferSA);
            return new ResponseEntity(conferSA, HttpStatus.OK);
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/conferences/{conferId}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> updateConferenceInfo(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                               @PathVariable("conferId") long conferId, @RequestBody Conference input) {
        Credential credential = TokenDecoder.decode(token);
        if (accountType.toLowerCase().equals(AccountType.ADMIN)) {
            Conference conference = manageRepository.getConferenceByConferIdAndAdminEmail(conferId, credential.getUsername());
            if (conference == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.OK);
            dataTransfer.conferenceDataTransfer(input, conference);
            return new ResponseEntity(conference, HttpStatus.OK);
        }
        return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.OK);
    }

    @RequestMapping(value = "/conferences/{conferId}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<?> deleteConferenceInfo(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                                  @PathVariable("conferId") long conferId) {
        Credential credential = TokenDecoder.decode(token);
        if (accountType.toLowerCase().equals(AccountType.ADMIN)) {
            Conference conference = manageRepository.getConferenceByConferIdAndAdminEmail(conferId, credential.getUsername());
            if (conference == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.OK);
            manageRepository.deleteManageByConferenceId(conferId);
            conferenceRepository.delete(conference);
            return new ResponseEntity(conference, HttpStatus.OK);
        }
        return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/conferences/{conferId}/invitations", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> inviteUser2Conference(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                                   @PathVariable("conferId") long conferId, @RequestBody List<String> emails){
        if (!accountType.toLowerCase().equals(AccountType.ADMIN))
            return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        Credential credential = TokenDecoder.decode(token);
        Manage manage = manageRepository.getManageByConferIdAndAdminEmail(conferId, credential.getUsername());
        if (manage == null) return new ResponseEntity(new ErrorMessage("No such conference under your management"), HttpStatus.NOT_FOUND);
        List<User> userList = new ArrayList();
        Conference conference = manage.getConference();
        Address venue= conference.getVenue();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd yyyy  hh:mm a");
        String mailSubject = "Confify invites you to attend conference " + conference.getName();
        String mailBody = manage.getAdmin().getFirstName() + " is inviting you to event " + conference.getName() +
                " which will be held from " + conference.getStartTime().toLocalDateTime().format(df) +
                " to " + conference.getEndTime().toLocalDateTime().format(df) + " at " + venue.getStreet() + ", " +
                venue.getCity() + ", " + venue.getState() + " " + venue.getZip();
        List<String> emailInvited = new ArrayList();
        for (String email : emails) {
            String newMailBody = mailBody;
            Attend attend = attendRepository.getAttendByConferIdAndUserEmail(conferId, email);
            if (attend != null) {
                emailInvited.add(email);
                continue;
            }
            User user = userRepository.getUserByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                String generatedPwd = passwordService.generatePassword();
                user.setPassword(pwdEncoder.encode(generatedPwd));
                userRepository.save(user);
                user = userRepository.getUserByEmail(user.getEmail());
                String qrUrl = qrCodeService.generateQRcode(String.valueOf(user.getId()));
                user.setQrUrl(qrUrl);
                newMailBody += "\nPlease login to view more details at http://localhost:8080/#/login with " +
                        "username:" + email + " and password:" + generatedPwd;
            }
            attend = new Attend();
            attend.setAttendee(user);
            attend.setConference(conference);
            attend.setStatus(AttendStatus.INVITED);
            attendRepository.save(attend);
            emailservice.sendEmail("no-reply@confify.com", new String[]{email}, mailSubject, newMailBody);
        }
        if (emailInvited.size() > 0) return new ResponseEntity(emailInvited, HttpStatus.CONFLICT);
        return new ResponseEntity(new ResponseMessage("Invite Success"), HttpStatus.OK);
    }

    @RequestMapping(value = "/conferences/{conferId}/attendance", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<?> changeInvitationStatus(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                                   @PathVariable("conferId") long conferId, @RequestBody AttendForm attendForm){
        Credential credential = TokenDecoder.decode(token);
        if (accountType.toLowerCase().equals(AccountType.ADMIN)) {
            Conference conference = manageRepository.getConferenceByConferIdAndAdminEmail(conferId, credential.getUsername());
            if (conference == null) return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
            Attend attend = attendRepository.getAttendByUserIdAndConferId(conferId, attendForm.getUserId());
            if (attend == null) return new ResponseEntity(new ErrorMessage("User Not Invited"), HttpStatus.NOT_FOUND);
            attend.setStatus(attendForm.getStatus());
            return new ResponseEntity(new ResponseMessage("Status Changed"), HttpStatus.OK);
        }
        else {
            Attend attend = attendRepository.getAttendByConferIdAndUserEmail(conferId, credential.getUsername());
            if (attend == null) return new ResponseEntity(new ErrorMessage("User Not Invited"), HttpStatus.UNAUTHORIZED);
            attend.setStatus(attendForm.getStatus());
            return new ResponseEntity(new ResponseMessage("Status Changed"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/conferences/{conferId}/speakers", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> addConferenceSpeaker(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                                    @PathVariable("conferId") long conferId, @RequestBody List<String> emails){
        if (!accountType.toLowerCase().equals(AccountType.ADMIN)) {
            return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        Credential credential = TokenDecoder.decode(token);
        Manage manage = manageRepository.getManageByConferIdAndAdminEmail(conferId, credential.getUsername());
        if (manage == null) return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        Conference conference = manage.getConference();
        Address venue= conference.getVenue();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd yyyy  hh:mm a");
        String mailSubject = "Confify invites you to attend conference " + conference.getName();
        String mailBody = manage.getAdmin().getFirstName() + " is inviting you as speaker for event " + conference.getName() +
                " which will be held from " + conference.getStartTime().toLocalDateTime().format(df) +
                " to " + conference.getEndTime().toLocalDateTime().format(df) + " at " + venue.getStreet() + ", " +
                venue.getCity() + ", " + venue.getState() + " " + venue.getZip();
        List<String> emailInvited = new ArrayList();
        for (String email : emails) {
            String newMailBody = mailBody;
            GiveSpeech giveSpeech = giveSpeechRepository.getGiveSpeechByConferIdAndEmail(conferId, email);
            if (giveSpeech != null) {
                emailInvited.add(email);
                continue;
            }
            User user = userRepository.getUserByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                String generatedPwd = passwordService.generatePassword();
                user.setPassword(pwdEncoder.encode(generatedPwd));
                userRepository.save(user);
                user = userRepository.getUserByEmail(user.getEmail());
                String qrUrl = qrCodeService.generateQRcode(String.valueOf(user.getId()));
                user.setQrUrl(qrUrl);
                newMailBody += "\nPlease login to view more details at http://localhost:8080/#/login with " +
                        "username:" + email + " and password:" + generatedPwd;
            }
            giveSpeech = new GiveSpeech();
            giveSpeech.setConference(conference);
            giveSpeech.setSpeaker(user);
            giveSpeechRepository.save(giveSpeech);
            emailservice.sendEmail("no-reply@confify.com", new String[]{email}, mailSubject, newMailBody);
        }
        if (emailInvited.size() > 0) return new ResponseEntity(emailInvited, HttpStatus.CONFLICT);
        return new ResponseEntity(new ResponseMessage("All speakers Added"), HttpStatus.OK);
    }

    @RequestMapping(value = "/conferences/{conferId}/speakers/{speakerId}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<?> addConferenceSpeaker(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                                  @PathVariable("conferId") long conferId, @PathVariable("speakerId") long speakerId){
        if (!accountType.equals(AccountType.ADMIN)) {
            return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        Credential credential = TokenDecoder.decode(token);
        Conference conference = manageRepository.getConferenceByConferIdAndAdminEmail(conferId, credential.getUsername());
        if (conference == null) return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        GiveSpeech giveSpeech = giveSpeechRepository.getGiveSpeechByConferIdAndUserId(conferId, speakerId);
        if (giveSpeech == null) return new ResponseEntity(new ErrorMessage("Speaker Not Found"), HttpStatus.NOT_FOUND);
        giveSpeechRepository.delete(giveSpeech);
        return new ResponseEntity(new ResponseMessage("Speaker Removed"), HttpStatus.OK);
    }

    @RequestMapping(value = "/conferences/{conferId}/files", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> uploadSingleFile(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                              @PathVariable("conferId") long conferId, @RequestParam("file") MultipartFile file,
                                              @RequestParam("description") String description){
        Credential credential = TokenDecoder.decode(token);
        GiveSpeech giveSpeech = giveSpeechRepository.getGiveSpeechByConferIdAndEmail(conferId, credential.getUsername());
        if (giveSpeech == null) return new ResponseEntity(new ErrorMessage("Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String rootpath =  "static/nodejs/public/";
                String filepath = "files/" + giveSpeech.getConference().getId() + "/"
                        + giveSpeech.getSpeaker().getId() + "/" + file.getOriginalFilename();
                storageService.putFile(rootpath + filepath, bytes);
                FileRecord fileRecord = new FileRecord();
                fileRecord.setConference(giveSpeech.getConference());
                fileRecord.setOwner(giveSpeech.getSpeaker());
                fileRecord.setFilepath(filepath);
                fileRecord.setDescription(description);
                fileRecordRepository.save(fileRecord);
                return new ResponseEntity(new ResponseMessage("FileRecord Upload Succeed"), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity(new ResponseMessage("FileRecord Upload Failed"), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(new ResponseMessage("Empty FileRecord"), HttpStatus.BAD_REQUEST);
        }
    }
}

