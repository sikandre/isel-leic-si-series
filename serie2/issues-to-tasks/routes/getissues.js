var express = require('express');
var router = express.Router();
const usersMap = require("../public/UsersData");
const request = require("request");
const validateCookie = require( "../public/ValidateCookie");

router.get('/:username', function(req, res, next) {
    if (!validateCookie.ValidateCookie(req)){
        res.render('Forbidden');
    } else {
        let username = req.params.username;
        let user = usersMap.getUser(username);
        let accessToken = user.git_access_token;
        let issues = [];
        request.get({
                url: 'https://api.github.com/issues',
                headers: {
                    "Authorization": "token " + accessToken,
                    accept: 'application/json',
                    'user-agent': 'si-phase2-project',
                }
            },
            function (error, resp, body) {
                // body is the decompressed response body
                var bodyParsed = JSON.parse(body);
                //console.log("body", bodyParsed);

                bodyParsed.forEach((issue) => issues.push({
                    username: username,
                    repoName: issue.url,
                    issueTitle: issue.title,
                    issueBody: issue.body
                }));
                res.status(200);
                res.render('listrepos', {issues: issues, username: username});
            }
        );
    }
});

module.exports = router;