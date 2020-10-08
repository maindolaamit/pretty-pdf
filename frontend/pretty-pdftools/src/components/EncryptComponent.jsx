import React from 'react';
import {getActionContentHeader, getActionHeader, useMyFormHook} from "./ActionUtilities";
import PrettyPdfDataService from "../api/PrettyPdfDataService";

function EncryptComponent() {
    return (
        <div className="container body-container">
            {getActionHeader("Lock PDF")}
            {getActionContentHeader("The easiest way to password protect your PDF files, " +
                "encrypt single or multiple PDF documents using password of your choice. Enter the password " +
                "for document(s) and hit Protect button.")}
            <div className="row justify-content-center input-area">
                {useEncryptDecryptForm("encrypt")}
            </div>
        </div>
    );
}

function useEncryptDecryptForm(actionType) {
    let message, submitBtnText;
    if (actionType.toUpperCase() === "ENCRYPT") {
        message = "Enter the password to encrypt the PDF file with.";
        submitBtnText = "Lock PDF";
    } else if (actionType.toUpperCase() === "DECRYPT") {
        message = "Enter the current password set on the protected PDF.";
        submitBtnText = "Decrypt PDF";
    }

    const {handleChange, handleSubmit, values, errors, formData} = useMyFormHook({
        password: '', pdfFile: ''
    }, submitForm, validateErrors);

    // Function to submit the form values
    function submitForm(values) {
        console.log("Submitted the form");
        if (actionType.toUpperCase() === "ENCRYPT") {
            PrettyPdfDataService.encryptPDF(formData).then(response => {
                console.log(response.data);
                if (200 === response.status) {
                    alert("File uploaded successfully.")
                }
            }).catch(errors => {
                console.log(errors)
            });
        }

    }

    function validateErrors(values) {
        console.log("validating errors");
        let errors = {};
        const password = values.password
        const pdfFile = values.pdfFile
        // const validPassword = RegExp('^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}[]:;<>,.?/~_+-=|\\]).{8,32}$');
        // Check password

        if (actionType.toUpperCase() === 'ENCRYPT') {
            if (password.trim().length === 0) {
                errors.password = "Field can not be empty."
            } else if (password.length < 8) {
                errors.password = 'Password should be at least 8 characters long'
            } else if (password.length > 32) {
                errors.password = 'Password should not be more than 32 characters long'
            } /*else if (!validPassword.test(password)) {
                 errors.password = 'Password should have at least one digit, one lowercase letter, one uppercase letter and one special character'
             }*/
        } else if (actionType.toUpperCase() === 'DECRYPT') {
            if (password.trim().length === 0) {
                errors.password = `Field can not be empty.`;
            }
        }

        // Check PDF File
        if (pdfFile === '') {
            errors.pdfFile = "Field can not be empty.";
        } else if (!pdfFile.endsWith(".pdf")) {
            errors.pdfFile = `${pdfFile} : Invalid PDF File`;
        }

        return errors;
    }

    // Actual Form content
    return (<form onSubmit={handleSubmit} encType="multipart/form-data">
        <div className={"form-group"}>{message}
            <small name={"inputHelp"} className={"form-text text-muted pl-3 text-info"}></small>
            <input className={`form-control ${errors.password && "is-invalid"}`} name={"password"} type={"password"}
                   placeholder={"Password@123"}
                   value={values.password} onChange={handleChange}/>
            <div className={"invalid-feedback"}>{errors.password}</div>
        </div>
        <div className="form-group custom-file mb-5">
            <input type="file" id="pdfFile" name={"pdfFile"}
                   className={`form-control-file custom-file-input ${errors.pdfFile && "is-invalid"}`}
                   value={values.pdfFile} onChange={handleChange}/>
            <label className="custom-file-label" htmlFor="pdfFile">Upload PDF File</label>
            <div className={"invalid-feedback"}>{errors.pdfFile}</div>
        </div>
        <button type={"submit"}
                className={"btn btn-info rounded-pill btn-lg waves-effect action-btn mb-3"}>{submitBtnText}
        </button>
    </form>);
}

export {useEncryptDecryptForm};
export default EncryptComponent;