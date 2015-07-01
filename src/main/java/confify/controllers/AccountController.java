package confify.controllers;

import confify.exception.ResponseMessage;
import confify.models.AccountType;
import confify.models.Credential;
import confify.models.output.AdminInfo;
import confify.models.output.UserInfo;
import confify.service.QRCodeService;
import confify.service.StorageService;
import confify.service.TokenDecoder;
import confify.util.DataTransfer;
import confify.exception.ErrorMessage;
import confify.models.Admin;
import confify.models.User;
import confify.repositories.AdminRepository;
import confify.repositories.UserRepository;
import confify.validator.AvatarValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.util.Random;

/**
 * Created by Dennis on 4/20/2015.
 */
@RestController
@RequestMapping(value = "/api")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder pwdEncoder;
    @Autowired private AdminRepository adminRepository;
    @Autowired private DataTransfer dataTransfer;
    @Autowired private QRCodeService qrCodeService;
    @Autowired private ServletContext servletContext;
    @Autowired private StorageService storageService;

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> registerUser(@RequestBody User user){
        User tmpUser = userRepository.getUserByEmail(user.getEmail());
        if (tmpUser != null) {
            ResponseEntity resEn = new ResponseEntity(new ErrorMessage("email already exists"), HttpStatus.CONFLICT);
            return resEn;
        }
        user.setPassword(pwdEncoder.encode(user.getPassword()));
        userRepository.save(user);
        user = userRepository.getUserByEmail(user.getEmail());
        String qrUrl = qrCodeService.generateQRcode(String.valueOf(user.getId()));
        user.setQrUrl(qrUrl);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/admins", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin){
        Admin tmpAdmin = adminRepository.getAdminByEmail(admin.getEmail());
        if (tmpAdmin != null) {
            ResponseEntity resEn = new ResponseEntity(new ErrorMessage("email already exists"), HttpStatus.CONFLICT);
            return resEn;
        }
        admin.setPassword(pwdEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return new ResponseEntity(admin, HttpStatus.OK);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestHeader("AccountType") String accountType, @RequestBody User input){
        if (accountType.toLowerCase().equals(AccountType.ADMIN)) {
            Admin admin = adminRepository.getAdminByEmail(input.getEmail());
            if (admin != null && pwdEncoder.matches(input.getPassword(), admin.getPassword()) ) {
                return new ResponseEntity(admin, HttpStatus.OK);
            }
        }
        else {
            User user = userRepository.getUserByEmail(input.getEmail());
            if (user != null && pwdEncoder.matches(input.getPassword(), user.getPassword()) ) {
                return new ResponseEntity(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity(new ErrorMessage("invalid credential"), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.POST)
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,@PathVariable("userId") long userId, @RequestBody User input){
        User user = userRepository.getUserById(userId);
        if (user != null) {
            dataTransfer.userDataTransfer(input, user);
            userRepository.save(user);
            return new ResponseEntity(user, HttpStatus.OK);
        }
        return new ResponseEntity(new ErrorMessage("User Not Found"), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                     @PathVariable("userId") long userId){
        User user = userRepository.getUserById(userId);
        if (user != null) {
            return new ResponseEntity(user, HttpStatus.OK);
        }
        return new ResponseEntity(new ErrorMessage("User Not Found"), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/users/me", method = RequestMethod.GET)
    public ResponseEntity<?> getMyUserProfile(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType){
        Credential credential = TokenDecoder.decode(token);
        if(accountType.toLowerCase().equals(AccountType.USER)){
            User user = userRepository.getUserByEmail(credential.getUsername());
            if (user != null) {
                UserInfo userInfo = new UserInfo();
                dataTransfer.user2UserInfo(user, userInfo);
                return new ResponseEntity(userInfo, HttpStatus.OK);
            }
            return new ResponseEntity(new ErrorMessage("User Not Found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(new ErrorMessage("AccountType Error"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/admins/me", method = RequestMethod.GET)
    public ResponseEntity<?> getMyAdminProfile(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType){
        Credential credential = TokenDecoder.decode(token);
        if (accountType.toLowerCase().equals(AccountType.ADMIN)) {
            Admin admin = adminRepository.getAdminByEmail(credential.getUsername());
            if (admin != null) {
                AdminInfo adminInfo = new AdminInfo();
                dataTransfer.admin2AdminInfo(admin, adminInfo);
                return new ResponseEntity(adminInfo, HttpStatus.OK);
            }
            return new ResponseEntity(new ErrorMessage("Admin Not Found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(new ErrorMessage("AccountType Error"), HttpStatus.UNAUTHORIZED);
    }


    @RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                        @PathVariable("userId") long userId){
        User user = userRepository.getUserById(userId);
        if (user != null) {
            userRepository.delete(user);
            return new ResponseEntity(user, HttpStatus.OK);
        }
        return new ResponseEntity(new ErrorMessage("User Not Found"), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/admins/{adminId}", method = RequestMethod.GET)
    public ResponseEntity<?> getAdmin(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                     @PathVariable("adminId") long adminId){
        Admin admin = adminRepository.getAdminById(adminId);
        if (admin != null) {
            return new ResponseEntity(admin, HttpStatus.OK);
        }
        return new ResponseEntity(new ErrorMessage("Admin Not Found"), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/admins/{adminId}", method = RequestMethod.POST)
    public ResponseEntity<?> updateAdmin(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                         @PathVariable("adminId") long adminId, @RequestBody Admin input){
        Admin admin = adminRepository.getAdminById(adminId);
        if (admin != null) {
            dataTransfer.adminInput2Admin(input, admin);
            adminRepository.save(admin);
            return new ResponseEntity(admin, HttpStatus.OK);
        }
        return new ResponseEntity(new ErrorMessage("Admin Not Found"), HttpStatus.NOT_FOUND);
    }

    @Autowired private AvatarValidator avatarValidator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(avatarValidator);
    }

    @RequestMapping(value = "/users/{userId}/avatar", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody ResponseEntity<?> uploadUserAvatar(@RequestHeader("Authorization") String token, @RequestHeader("AccountType") String accountType,
                                                            @PathVariable("userId") int userId, @Valid @RequestPart("file") MultipartFile file) {
        User user = userRepository.getUserById(userId);
        if (user == null) return new ResponseEntity(new ResponseMessage("User Not Found"), HttpStatus.UNAUTHORIZED);
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                // Creating the directory to store file
                //String rootPath = System.getProperty("catalina.home");
                //String filepath =  servletContext.getRealPath("/") + "/static/images/avatar/";
                String oriFileName = file.getOriginalFilename();
                String rootpath =  "static/nodejs/public";
                Random random = new Random();
                String filepath = "images/avatar/" + random.nextInt() + oriFileName.substring(oriFileName.lastIndexOf('.'));
                storageService.putFile(rootpath + filepath, bytes);
                user.setAvatarUrl(filepath);
                return new ResponseEntity(new ResponseMessage("FileRecord Upload Succeed"), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity(new ResponseMessage("FileRecord Upload Failed"), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(new ResponseMessage("Empty FileRecord"), HttpStatus.BAD_REQUEST);
        }
    }
}
