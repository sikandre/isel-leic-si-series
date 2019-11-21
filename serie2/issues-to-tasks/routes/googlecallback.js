var express = require('express');
const request = require("request");
var router = express.Router();
var jwt = require('jsonwebtoken');
const usersData = require("../public/UsersData");

const JWT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNbvsoGMhmC5vq0NTNxHGgNqxNb";
const CLIENT_ID = "747819205262-uteoim61ntfqe29s4bs8huqjebn6tqr6.apps.googleusercontent.com";
const CLIENT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNb";


router.get('/', (req, resp) => {
    if (req.query.state !== usersData.getState()) {
        resp.status(403);
        resp.render('Forbidden');
    } else {
        //get token
    request.post(
            {
                url: 'https://www.googleapis.com/oauth2/v3/token',
                // body parameters
                form: {
                    code: req.query.code,
                    client_id: CLIENT_ID,
                    client_secret: CLIENT_SECRET,
                    redirect_uri: 'http://localhost:3000/googlecallback',
                    grant_type: 'authorization_code'
                }
            },
            function (err, httpResponse, body) {

                if (err) {
                    resp.status(400).send({err});
                }
                let json_response = JSON.parse(body);
                let jwt_payload = jwt.decode(json_response.id_token);
                let email = jwt_payload.email;
                let sub = jwt_payload.sub;

                resp.statusCode = 200;

                const token = jwt.sign({email, sub}, JWT_SECRET);
                resp.cookie('token', token, {
                    expires: new Date(Date.now() + 36000),
                    secure: false, // set to true if your using https
                    httpOnly: true,
                });

                //save data in memory
                usersData.addUser(email, {
                    id_token: json_response.id_token,
                    google_access_token: json_response.access_token
                });
                resp.redirect('/gitindex/'+email);
            }
        );

    }
});

module.exports = router;