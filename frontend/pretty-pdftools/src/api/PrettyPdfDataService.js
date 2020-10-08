import axios from "axios";

class PrettyPdfDataService {
    config = {
        headers: {
            'Content-Type': 'multipart/form-data',
            'Boundary': 'gc0p4Jq0M2Yt08jU534c0p',
            // "Access-Control-Allow-Origin": "*"
        }
    };
    webserviceUrl = "http://localhost:8080/pretty-pdf/";

    encryptPDF(formData) {
        console.log("Posting file for encryption");
        // console.log(formData)
        // return axios.post(`${this.webserviceUrl}encrypt`, formData, this.config);
        // let promise = this.testAPI();
        let promise = this.testFileAPI(formData);
        console.log(promise);
        return promise;
    }

    // To test the backend service
    testAPI() {
        let formData = new FormData();
        formData.append("password", "amit@123");
        formData.append("user", "amit");
        // let promise = axios({
        //     method: 'post',
        //     url: `${this.webserviceUrl}test`,
        //     data: formData,
        //     headers: {'Content-Type': 'multipart/form-data'}
        // });
        return axios.post(`${this.webserviceUrl}test`, formData);
    }

    testFileAPI(formData) {
        console.log(formData)
        return axios.post(`${this.webserviceUrl}testfile`, formData, this.config);
    }
}

export default new PrettyPdfDataService();