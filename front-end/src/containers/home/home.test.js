import { expect } from "chai";
import nock from 'nock';
import axios from 'axios';
import settings from '../../config/config';

axios.defaults.adapter = require('axios/lib/adapters/http')
it('renders without crashing', done => {
    const response = "200";
    nock(`http://${settings.API_URL}`)
        .post('/userAuth/a/a')
        .reply(200, response);

    const url = `http://${settings.API_URL}/userAuth/a/a`
    axios.post(url)
        .then((resp) => {
            expect(resp.status).to.be.eq(200);
        }).then(done,done);
});