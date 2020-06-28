import React, {Component} from 'react';
import './HeaderComponent.css'
import {getPDFActionURL} from "./PrettyPDFToolsApp";

class HeaderComponent extends Component {
    render() {
        return (
            <div>
                <header>
                    <nav className={"navbar navbar-expand-lg navbar-dark bg-dark"}>
                        <a className={"navbar-brand flex"} href={"/"} title="PrettyPDF">
                            <img src="assets/images/brand.png"
                                // className="d-inline-block align-top"
                                 width={"50"}
                                 alt={"PrettyPDF"}/>
                        </a>
                        <button className={"navbar-toggler collapsed"} type={"button"} data-toggle={"collapse"}
                                data-target={"#mainNav"} aria-controls={"mainNav"} aria-expanded="false"
                                aria-label="Toggle">
                            <span className="toggler-icon"></span>
                            <span className="toggler-icon"></span>
                            <span className="toggler-icon"></span>
                        </button>
                        <div className={"navbar-collapse sub-menu-bar collapse"} id={"mainNav"}>
                            <ul className={"navbar-nav mr-auto"}>
                                <li className={"nav-item active"}>
                                    <a className={"nav-link"} href={"/"}>Home</a>
                                </li>
                                <li className={"nav-item"}>
                                    <a className={"nav-link"} href={getPDFActionURL("MERGE")}>Merge</a>
                                </li>
                                <li className={"nav-item"}>
                                    <a className={"nav-link"} href={getPDFActionURL("SPLIT")}>Split</a>
                                </li>
                                <li className={"nav-item"}>
                                    <a className={"nav-link"} href={getPDFActionURL("DELETE")}>Delete</a>
                                </li>
                                <li className={"nav-item"}>
                                    <a className={"nav-link"} href={getPDFActionURL("ENCRYPT")}>Lock</a>
                                </li>
                                <li className={"nav-item"}>
                                    <a className={"nav-link"} href={getPDFActionURL("DECRYPT")}>Unlock</a>
                                </li>
                            </ul>
                        </div>
                    </nav>
                </header>
            </div>
        );
    }
}

export default HeaderComponent;