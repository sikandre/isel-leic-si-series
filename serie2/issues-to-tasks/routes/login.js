var express = require('express');
var router = express.Router();
const usersData = require("../public/UsersData");

const CLIENT_ID = "747819205262-uteoim61ntfqe29s4bs8huqjebn6tqr6.apps.googleusercontent.com";

router.get('/', (req, resp) => {
    resp.redirect(302,
        // authorization endpoint
        'https://accounts.google.com/o/oauth2/v2/auth?'
        // client id
        + 'client_id=' + CLIENT_ID + '&'
        // scope "openid email"
        + 'scope=openid email https://www.googleapis.com/auth/tasks&'
        // parameter state should bind the user's session to a request/response
        + 'state=' + usersData.getState() + '&'
        // responde_type for "authorization code grant"
        + 'response_type=code&'
        // redirect uri used to register RP
        + 'redirect_uri=http://localhost:3000/googlecallback')
});

module.exports = router;