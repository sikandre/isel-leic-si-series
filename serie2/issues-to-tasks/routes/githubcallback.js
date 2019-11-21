var express = require('express');
const request = require("request");
var router = express.Router();
const usersData = require("../public/UsersData");

//github credentials
const GIT_CLIENT_ID = "8440aaee2cd8100b1136";
const GIT_CLIENT_SECRET = "e45df0fe9b0c45e85992dee198aff1ab54a94c20";

router.get('/:username', (req, resp, next) => {
    let email = req.params.username;
    if (req.query.state !== usersData.getState()) {
        resp.status(403);
        resp.render('Forbidden');
    } else {
        //get token
        let payload;
        let accessToken;
        request.post(
            {
                url: 'https://github.com/login/oauth/access_token',
                headers: {
                    accept: 'application/json'
                },
                // body parameters
                form: {
                    code: req.query.code,
                    client_id: GIT_CLIENT_ID,
                    client_secret: GIT_CLIENT_SECRET,
                    redirect_uri: 'http://localhost:3000/githubcallback/' + email,
                    state: usersData.getState(),
                }
            },

            function (err, httpResponse, body) {
                if (err) {
                    resp.status(400).send({error});
                }

                payload = JSON.parse(body);
                accessToken = payload.access_token;
                usersData.addAccessTokenToUser(email, accessToken);
                resp.redirect('/getrepos/' + email);
            }
        );
    }
});

module.exports = router;