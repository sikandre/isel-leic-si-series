'use strict'

//github credentials
const GIT_CLIENT_ID = "8440aaee2cd8100b1136";
const GIT_CLIENT_SECRET = "e45df0fe9b0c45e85992dee198aff1ab54a94c20";

module.exports = (request, cookievalidator, usersData) => {

    const theService = {

        'loginuri': function (email) {
            // authorization endpoint
            return 'https://github.com/login/oauth/authorize?'
                // client id
                + 'client_id=' + GIT_CLIENT_ID + '&'
                // scope "openid email"
                + 'scope=repo&'
                // parameter state should bind the user's session to a request/response
                + 'state=' + usersData.getState() + '&'
                // redirect uri used to register RP
                + 'redirect_uri=http://localhost:3000/githubcallback/' + email;
        },
        'callback': async function (email, code) {
            const options = {
                headers: {
                    'accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            };
            const form = xwwwfurlenc({
                code: code,
                client_id: GIT_CLIENT_ID,
                client_secret: GIT_CLIENT_SECRET,
                redirect_uri: 'http://localhost:3000/githubcallback/' + email,
                state: usersData.getState(),
            });
            let response = await request.post('https://github.com/login/oauth/access_token', form, options)

            const payload = response.data;
            const accessToken = payload.access_token;
            usersData.addAccessTokenToUser(email, accessToken);

            return true;
        },
        'getissues': async function (username) {

            const user = usersData.getUser(username);
            const accessToken = user.git_access_token;
            let issues = [];
            const options = {
                headers: {
                    "Authorization": "token " + accessToken,
                    accept: 'application/json',
                    'user-agent': 'si-phase2-project',
                }
            };
            const response = await request.get('https://api.github.com/issues', options);

            const body = response.data;
            body.forEach((issue) => issues.push({
                username: username,
                repoName: issue.url,
                issueTitle: issue.title,
                issueBody: issue.body
            }));
            return { issues: issues, username: username };
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

    return theService;
}