import React, {Component} from 'react';
import {getPDFActionURL} from "./PrettyPDFToolsApp";
import Nav from "react-bootstrap/Nav";
import {Navbar} from "react-bootstrap";

class HeaderComponent extends Component {
    render() {
        return (
            <div>
                <header>
                    <Navbar bg={"dark"} variant={"dark"} expand={"lg"} sticky={"top"}>
                        <Navbar.Brand href={"/"}>
                            <img src="assets/images/brand.png"
                                 width={"50px"}
                                 alt={"PrettyPDF"} className="d-inline-block align-center"/>{' '}
                            PrettyPDF
                        </Navbar.Brand>
                        <Navbar.Toggle aria-controls="main-nav"/>
                        <Navbar.Collapse id="main-nav">
                            <Nav className={"mr-auto"}>
                                <Nav.Link href="/">Home</Nav.Link>
                                <Nav.Link href={getPDFActionURL("MERGE")}>Merge</Nav.Link>
                                <Nav.Link href={getPDFActionURL("SPLIT")}>Split</Nav.Link>
                                <Nav.Link href={getPDFActionURL("DELETE")}>Delete</Nav.Link>
                                <Nav.Link href={getPDFActionURL("ENCRYPT")}>Lock</Nav.Link>
                                <Nav.Link href={getPDFActionURL("DECRYPT")}>Unlock</Nav.Link>
                            </Nav>
                        </Navbar.Collapse>
                    </Navbar>
                    {/*<nav className={"navbar navbar-expand-lg navbar-dark bg-dark"}>
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
                    </nav>*/}
                </header>
            </div>
        );
    }
}

export default HeaderComponent;