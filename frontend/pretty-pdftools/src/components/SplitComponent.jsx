import React from 'react';
import {getActionContentHeader, getActionHeader, useMyFormHook} from "./ActionUtilities";

// React Component {
const SplitComponent = () => {
    return (
        <div className="container body-container">
            {getActionHeader("Split PDF File")}
            {getActionContentHeader("The easiest way to split a PDF files, split a PDF document into multiple" +
                " PDF based on page number. Give page numbers separated by comma to split the file. For e.g. if " +
                "you have a PDF file having 13 pages and you enter 3,5,9 then 3 PDF files will be created or other " +
                "files types like images and merge them into one PDF.")}
            <div className="row justify-content-center input-area">
                {useSplitDeleteForm("split")}
            </div>
        </div>
    );
}

// React Component
function DeleteComponent() {
    return (
        <div className="container body-container">
            {getActionHeader("Delete PDF pages")}
            {getActionContentHeader("The easiest way to delete unwanted pages from a PDF file, enter the page" +
                " numbers to be deleted in the box in comma separated form. For e.g. if you have a PDF file having " +
                "13 pages and you enter 3,5,9 then page number 3, 5 and 9 will be deleted from the PDF Document.")}
            <div className="row justify-content-center input-area">
                {useSplitDeleteForm("delete")}
            </div>
        </div>
    );
}

function useSplitDeleteForm(actionType) {
    let message, submitBtnText;
    if (actionType.toUpperCase() === "SPLIT") {
        message = "Enter page numbers to split file e.g. 3,5,9";
        submitBtnText = "Split PDF";
    } else if (actionType.toUpperCase() === "DELETE") {
        message = "Enter page numbers to delete file e.g. 3,5,9";
        submitBtnText = "Delete pages";
    }

    const {handleChange, handleSubmit, values, errors} = useMyFormHook({
        pageNumbers: '',
        pdfFile: ''
    }, submit, validateErrors);

    // Function to submit the form values
    function submit(event) {
        console.log("Submitted the form");
    }

    function validateErrors(values) {
        console.log("validating errors");
        let errors = {};
        const pageNumber = values.pageNumbers
        const pdfFile = values.pdfFile;

        // Check page numbers
        if (pageNumber.trim().length === 0) {
            errors.pageNumbers = "Field can not be empty."
        } else if (pageNumber.length > 1) {
            if (!pageNumber.includes(',')) {
                errors.pageNumbers = "Page numbers should be comma separated.";
            } else {
                for (const item of pageNumber.split(",")) {
                    if (parseInt(item) === "NaN") {
                        errors.pageNumbers = `Invalid page number ${item} should be integer`
                    }
                }
            }

        }
        console.log(`pdfFile : ${pdfFile}`)
        console.log(`${pdfFile === '' ? 'true' : 'false'}`);
        // Check PDF File
        if (pdfFile === '') {
            console.log('pdffile is empty');
            errors.pdfFile = "Field can not be empty.";
        } else if (!pdfFile.endsWith(".pdf")) {
            errors.pdfFile = 'Invalid PDF File';
        }

        return errors;
    }

    // Actual Form content
    return (<form onSubmit={handleSubmit}>
        <div className={"form-group"}>{message}
            {/* register your input into the hook by invoking the "register" function */}
            <small name={"inputHelp"} className={"form-text text-muted pl-3 text-info"}></small>
            <input className={`form-control ${errors.pageNumbers && "is-invalid"}`} name={"pageNumbers"} type={"input"}
                   placeholder={"3,5,9"}
                   value={values.pageNumbers} onChange={handleChange}/>
            <div className={"invalid-feedback"}>{errors.pageNumbers}</div>
        </div>
        <div className="form-group custom-file mb-2">
            <input type="file" id="pdfFile" name={"pdfFile"}
                   className={`form-control-file custom-file-input ${errors.pdfFile && "is-invalid"}`}
                   value={values.pdfFile} onChange={handleChange}/>
            <label className="custom-file-label" htmlFor="pdfFile">Choose file</label>
            <div className={"invalid-feedback"}>{errors.pdfFile}</div>
        </div>
        <button type={"submit"} className={"btn btn-info rounded-pill btn-lg waves-effect action-btn"}>{submitBtnText}
        </button>
    </form>);
}

export {DeleteComponent, useSplitDeleteForm};
export default SplitComponent;
