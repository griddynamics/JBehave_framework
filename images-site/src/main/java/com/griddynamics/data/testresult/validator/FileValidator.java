package com.griddynamics.data.testresult.validator;

/**
 * @author lzakharova
 */

import com.griddynamics.data.model.UploadedFile;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class FileValidator implements Validator {

    @Override
    public boolean supports(Class arg0) {
        return false;
    }

    @Override
    public void validate(Object uploadedFile, Errors errors) {

        UploadedFile file = (UploadedFile) uploadedFile;

        if (file.getFile().getSize() == 0) {
            errors.rejectValue("file", "uploadForm.salectFile",
                    "Please select a file!");
        }

    }

}

