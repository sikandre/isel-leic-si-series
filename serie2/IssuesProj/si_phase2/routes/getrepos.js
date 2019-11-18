var express = require('express');
var router = express.Router();
const usersMap = require("../public/UsersData");
const request = require("request");

router.get('/:username', function(req, res, next) {
    //res.send('repos');
    var username = req.params.username;
    var user = usersMap.getUser(username);
    var accessToken = user.git_access_token;
    console.log(accessToken);
    request.get({
            url: 'https://api.github.com/issues',
            headers: {
                "Authorization": "token "+accessToken,
                accept: 'application/json',
                'user-agent': 'si-phase2-project',
            }
        },
        function (error, resp, body) {
            // body is the decompressed response body
            console.log("body", body);
        }
    );
    res.render('listrepos');


});

module.exports = router;