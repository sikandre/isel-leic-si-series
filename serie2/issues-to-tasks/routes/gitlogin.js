var express = require('express');
var router = express.Router();
const usersData = require("../public/UsersData");

//github credentials
const GIT_CLIENT_ID = "8440aaee2cd8100b1136";

router.get('/:username', /*validateToken,*/ (req, resp, next) => {
    var email = req.params.username;
    resp.redirect(302,
        // authorization endpoint
        'https://github.com/login/oauth/authorize?'
        // client id
        + 'client_id=' + GIT_CLIENT_ID + '&'
        // scope "openid email"
        + 'scope=repo&'
        // parameter state should bind the user's session to a request/response
        + 'state=' + usersData.getState() + '&'
        // redirect uri used to register RP
        + 'redirect_uri=http://localhost:3000/githubcallback/'+email);
});

module.exports = router;