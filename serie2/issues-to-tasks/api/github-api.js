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
                    url: 'https://github.com/login/oauth/access_token',
                    headers: {
                        accept: 'application/json'
                    },
                    form: {
                        code: req.query.code,
                        client_id: GIT_CLIENT_ID,
                        client_secret: GIT_CLIENT_SECRET,
                        redirect_uri: 'http://localhost:3000/githubcallback/' + email,
                        state: usersData.getState(),
                    }
                };
                request.post(options)
                    .then(body => {
                        const payload = JSON.parse(body);
                        const accessToken = payload.access_token;
                        usersData.addAccessTokenToUser(email, accessToken);
                        resp.redirect('/getrepos/' + email);
                    })
                    .catch(err => {
                        resp.statusCode = 400;
                        resp.send({ err });
                    });
            }
        },
        'getissues': function (req, resp) {
            if (!cookievalidator.ValidateCookie(req)) {
                res.render('Forbidden');
            } else {
                let username = req.params.username;
                let user = usersMap.getUser(username);
                let accessToken = user.git_access_token;
                let issues = [];
                const options = {
                    url: 'https://api.github.com/issues',
                    headers: {
                        "Authorization": "token " + accessToken,
                        accept: 'application/json',
                        'user-agent': 'si-phase2-project',
                    }
                };
                return request.get(options)
                    .then(body => {
                        const bodyParsed = JSON.parse(body);

                        bodyParsed.forEach((issue) => issues.push({
                            username: username,
                            repoName: issue.url,
                            issueTitle: issue.title,
                            issueBody: issue.body
                        }));
                        return issues;
                    });
            }
        }
    }
    return theApi;
}