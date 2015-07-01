package confify.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Created by Dennis on 5/3/2015.
 */
@Service
public class AvatarValidator implements Validator {
    public boolean supports(Class<?> paramClass) {
        return MultipartFile.class.equals(paramClass);
    }
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "avatar", "avatar.empty");
        MultipartFile file = (MultipartFile) obj;
        if (!file.getContentType().equals("image/jpeg")) {
            errors.reject("Only JPG/JPEG/PNG are accepted");
        }
    }
}
