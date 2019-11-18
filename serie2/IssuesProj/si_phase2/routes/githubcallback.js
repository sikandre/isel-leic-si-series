var express = require('express');
const request = require("request");
var router = express.Router();
var jwt = require('jsonwebtoken');
const usersData = require("../public/UsersData");

//github credentials
const GIT_CLIENT_ID = "8440aaee2cd8100b1136";
const GIT_CLIENT_SECRET = "e45df0fe9b0c45e85992dee198aff1ab54a94c20";

router.get('/:username', (req, resp, next) => {
    var email = req.params.username;
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


/*console.log("aki",body);
//var access_token = body.split('=')[1].split('&')[0];
console.log(JSON.parse(body));
//save access_token from github*/


//console.log("http", httpResponse);
/*

let jwt_payload = jwt.decode(json_response.id_token);
let email = jwt_payload.email;
let sub = jwt_payload.sub;

resp.statusCode = 200;

const token = jwt.sign({email, sub}, JWT_SECRET);
resp.cookie('token', token, {
    expires: new Date(Date.now() + 360000),
    secure: false, // set to true if your using https
    httpOnly: true,
});
//save data in memory
if (map.get(email)) {
    map.delete(email);
}
map.set(email, json_response);
//resp.sendFile(path.join(publicDir, 'GithubLogin.html'));
resp.redirect('/getRepos');
*/


/*    }
);

}*/

module.exports = router;