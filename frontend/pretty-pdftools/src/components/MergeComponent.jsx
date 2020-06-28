import React from 'react';
import {getActionContentHeader, getActionHeader, useMyFormHook} from "./ActionUtilities";

function MergeComponent() {
    return (
        <div className="container">
            {getActionHeader("Merge PDF Files")}
            {getActionContentHeader("The easiest way to combine PDF Files, combine different PDF documents" +
                "or other files types like images and merge them into a single PDF. Choose the files and hit Merge button")}
            <div className="row justify-content-center input-area">
                {useMergeForm()}
            </div>
        </div>
    );
}

function useMergeForm() {
    let submitBtnText = 'Merge PDFs';

    const {handleChange, handleSubmit, values, errors} = useMyFormHook({
        pdfFile: ''
    }, submit, validateErrors);

    // Function to submit the form values
    function submit(event) {
        console.log("Submitted the form");
    }

    function validateErrors(values) {
        console.log("validating errors");
        let errors = {};
        const pdfFile = values.pdfFile;

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

export default MergeComponent;