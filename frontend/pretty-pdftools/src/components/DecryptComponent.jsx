import React, {Component} from 'react';
import {getActionContentHeader, getActionHeader} from "./ActionUtilities";
import {useEncryptDecryptForm} from "./EncryptComponent";

function DecryptComponent() {
    return (
        <div className="container align-t">
            {getActionHeader("Unlock PDFs")}
            {getActionContentHeader("The easiest way to remove password from your password protected PDF file, " +
                "Remove encryption from PDF document. Choose the file and and hit Unlock button.")}
            <div className="row justify-content-center input-area">
                {/*       <form>
                    <div className={"form-group d-flex"}>
                        <input id={"pageNumbers"} type={"password"} placeholder={"password@123"}></input>
                        <small id={"inputHelp"} className={"form-text text-muted pl-3"}>Enter the password.</small>
                    </div>
                    <div className="form-group d-flex custom-file mb-2">
                        <input type="file" className="form-control-file custom-file-input" id="pdfFile"/>
                        <label className="custom-file-label" htmlFor="pdfFile">Choose file</label>
                    </div>
                    <div/>
                    <button className={"btn btn-success"}>Unlock PDF</button>
                </form>*/}
                {useEncryptDecryptForm('decrypt')}
            </div>
        </div>
    );
}

export default DecryptComponent;