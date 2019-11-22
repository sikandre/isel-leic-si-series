'use strict'

//github credentials
const GIT_CLIENT_ID = "8440aaee2cd8100b1136";
const GIT_CLIENT_SECRET = "e45df0fe9b0c45e85992dee198aff1ab54a94c20";

module.exports = (request, cookievalidator, usersData) => {

    const theApi = {
        'gitindex': function (req, resp) {
            if (cookievalidator.ValidateCookie(req)) {
                var email = req.params.username;
                resp.render('gitindex', { email: email });
            } else {
                resp.render('Forbidden');
            }
        },
        'login': function (req, resp) {
            const email = req.params.username;
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
                + 'redirect_uri=http://localhost:3000/githubcallback/' + email);
        },
        'callback': function (req, resp) {
            let email = req.params.username;
            if (req.query.state !== usersData.getState()) {
                resp.status(403);
                resp.render('Forbidden');
            } else {
                const options = {
                    headers: {
                        'accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded' 
                    }
                };
                const form = xwwwfurlenc({
                    code: req.query.code,
                    client_id: GIT_CLIENT_ID,
                    client_secret: GIT_CLIENT_SECRET,
                    redirect_uri: 'http://localhost:3000/githubcallback/' + email,
                    state: usersData.getState(),
                });
                request.post('https://github.com/login/oauth/access_token', form, options)
                    .then(response => {
                        const payload = response.data;
                        const accessToken = payload.access_token;
                        usersData.addAccessTokenToUser(email, accessToken);
                        resp.redirect('/getrepos/' + email);
                    })
                    .catch(err => {
                        resp.statusCode = 400;
                        resp.send({ err });// RENDER ERROR
                    });
            }
        },
        'getissues': function (req, resp) {
            if (!cookievalidator.ValidateCookie(req)) {
                res.render('Forbidden');
            } else {
                let username = req.params.username;
                let user = usersData.getUser(username);
                let accessToken = user.git_access_token;
                let issues = [];
                const options = {
                    headers: {
                        "Authorization": "token " + accessToken,
                        accept: 'application/json',
                        'user-agent': 'si-phase2-project',
                    }
                };
                return request.get('https://api.github.com/issues',options)
                    .then(response => {
                        const body = response.data;

                        body.forEach((issue) => issues.push({
                            username: username,
                            repoName: issue.url,
                            issueTitle: issue.title,
                            issueBody: issue.body
                        }));
                        return {issues : issues, username : username};
                    });
            }
        }
    }

    function xwwwfurlenc(srcjson) {
        if (typeof srcjson !== "object")
            if (typeof console !== "undefined") {
                console.log("\"srcjson\" is not a JSON object");
                return null;
            }
        var urljson = "";
        var keys = Object.keys(srcjson);
        for (var i = 0; i < keys.length; i++) {
            urljson += keys[i] + "=" + srcjson[keys[i]];
            if (i < (keys.length - 1)) urljson += "&";
        }
        return urljson;
    }

    return theApi;
}