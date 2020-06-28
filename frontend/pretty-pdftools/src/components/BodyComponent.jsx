import React, {Component} from 'react';
import {getPDFActionURL} from "./PrettyPDFToolsApp";

class BodyComponent extends Component {
    getPDFActionImgTag(action) {
        let imageURL;
        if (action.toUpperCase() === "MERGE") {
            imageURL = <img className="shape" src="assets/images/pdf-merge.png" width={100} alt="Shape"/>;
        } else if (action.toUpperCase() === "SPLIT") {
            imageURL = <img className="shape" src="assets/images/pdf-split.png" width={100} alt="Shape"/>;
        } else if (action.toUpperCase() === "DELETE") {
            imageURL = <img className="shape" src="assets/images/pdf-delete.png" width={40} alt="Shape"/>;
        } else if (action.toUpperCase() === "ENCRYPT") {
            imageURL = <img className="shape" src="assets/images/pdf-encrypt.png" width={100} alt="Shape"/>;
        } else if (action.toUpperCase() === "DECRYPT") {
            imageURL = <img className="shape" src="assets/images/pdf-decrypt.png" width={100} alt="Shape"/>;
        }
        return imageURL;
    }

    getToolsPlaceHolder(action, title, content) {
        return (<div className="col-lg-4 col-md-7 col-sm-9">
            <div className="single-features mt-40 bg-light border-info">
                <div className="features-title-icon justify-content-between">
                    <div className="features-icon">
                        <i className="lni lni-layout"></i>
                        {this.getPDFActionImgTag(action)}
                    </div>
                    <h4 className="features-title"><a href={getPDFActionURL(action)}>{title}</a></h4>
                </div>
                <div className="features-content">
                    <p className="text">{content}</p>
                </div>
            </div>
        </div>)
    }

    render() {
        let style = {backgroundImage: "public/assets/images/body-bg.jpg"}
        return (
            <>
                <div className={"container body-container"}>
                    <div className={"row home-title"} style={style}>
                        <h1 className="title">Every tool you need to work with PDFs in one place</h1>
                        <h2 className="subtitle mb-1">PrettyPDF tools are 100% FREE and easy
                            to use! Merge, Split, Delete pages, Encrypt and Unlock PDFs with just a few
                            clicks.</h2>
                    </div>
                    {/*<div className={"container"}>*/}
                        <div className="row justify-content-center">
                            {this.getToolsPlaceHolder("Merge", "Merge PDF", "Merge two or more PDF Files into a single PDF file.")}
                            {this.getToolsPlaceHolder("Split", "Split PDF", "Split a PDF File into two PDF files.")}
                            {this.getToolsPlaceHolder("Delete", "Delete Pages", "Delete one or more pages from an existing PDF File")}
                            {this.getToolsPlaceHolder("Encrypt", "Lock PDF", "Encrypt/Lock your PDF file with Password.")}
                            {this.getToolsPlaceHolder("Decrypt", "Unlock PDF", "Deccrypt/Unlock - remove password from your PDF file.")}
                        </div>
                    {/*</div>*/}
                </div>
            </>
        );
    }
}

export default BodyComponent;