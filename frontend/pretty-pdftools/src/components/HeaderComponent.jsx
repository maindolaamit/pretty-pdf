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
                            <Nav className={"mr-auto"} onSelect={this.handleChange()}>
                                <div id="marker"></div>
                                <Nav.Link href="/">Home</Nav.Link>
                                <Nav.Link href={getPDFActionURL("MERGE")}>Merge</Nav.Link>
                                <Nav.Link href={getPDFActionURL("SPLIT")}>Split</Nav.Link>
                                <Nav.Link href={getPDFActionURL("DELETE")}>Delete</Nav.Link>
                                <Nav.Link href={getPDFActionURL("ENCRYPT")}>Lock</Nav.Link>
                                <Nav.Link href={getPDFActionURL("DECRYPT")}>Unlock</Nav.Link>
                            </Nav>
                        </Navbar.Collapse>
                    </Navbar>
                </header>
            </div>
        );
    }

    handleChange() {
        console.log("handling change")
        let marker = document.querySelector("#marker");
        let item = document.querySelectorAll("nav a");

        function indicator(e) {
            marker.style.left = e.offsetLeft + "px";
            marker.style.width = e.offsetWidth + "px";
        }

        item.forEach(link => {
            console.log(link);
            link.addEventListener('click', (e) => {
                indicator(e.target);
            });
        });
    }
}

export default HeaderComponent;