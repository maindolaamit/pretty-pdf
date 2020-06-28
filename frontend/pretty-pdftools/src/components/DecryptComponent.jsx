import React from 'react';
import {getActionContentHeader, getActionHeader} from "./ActionUtilities";
import {useEncryptDecryptForm} from "./EncryptComponent";

function DecryptComponent() {
    return (
        <div className="container body-container">
            {getActionHeader("Unlock PDFs")}
            {getActionContentHeader("The easiest way to remove password from your password protected PDF file, " +
                "Remove encryption from PDF document. Choose the file and and hit Unlock button.")}
            <div className="row justify-content-center input-area">
                {useEncryptDecryptForm('decrypt')}
            </div>
        </div>
    );
}

export default DecryptComponent;