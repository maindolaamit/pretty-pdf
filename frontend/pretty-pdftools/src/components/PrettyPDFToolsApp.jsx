import React, {Component} from 'react';
import HeaderComponent from "./HeaderComponent";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import './PrettyPDFToolsApp.css'
import BodyComponent from "./BodyComponent";
import MergeComponent from "./MergeComponent";
import SplitComponent, {DeleteComponent} from "./SplitComponent";
import EncryptComponent from "./EncryptComponent";
import DecryptComponent from "./DecryptComponent";

class PrettyPDFToolsApp extends Component {
    render() {
        return (
            <div className={"PrettyPDFToolsApp"}>
                <Router>
                    <>
                        <HeaderComponent/>
                        <Switch>
                            <Route path={"/"} exact component={BodyComponent}/>
                            <Route path={"/split"} exact component={SplitComponent}/>
                            <Route path={"/delete"} exact component={DeleteComponent}/>
                            <Route path={"/merge"} exact component={MergeComponent}/>
                            <Route path={"/encrypt"} exact component={EncryptComponent}/>
                            <Route path={"/decrypt"} exact component={DecryptComponent}/>
                        </Switch>
                    </>
                </Router>
            </div>
        );
    }
}

/**
 * Function to give the Action url based on action. Created to keep similar logic in header and Body Actions
 * @param action
 * @returns {string}
 */
export function getPDFActionURL(action) {
    let actionUrl;
    if (action.toUpperCase() === "MERGE") {
        actionUrl = "/merge";
    } else if (action.toUpperCase() === "SPLIT") {
        actionUrl = "/split";
    } else if (action.toUpperCase() === "DELETE") {
        actionUrl = "/delete";
    } else if (action.toUpperCase() === "ENCRYPT") {
        actionUrl = "/encrypt";
    } else if (action.toUpperCase() === "DECRYPT") {
        actionUrl = "/decrypt";
    }
    return actionUrl;
}


export default PrettyPDFToolsApp;
