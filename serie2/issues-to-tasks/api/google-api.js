'use strict'

const JWT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNbvsoGMhmC5vq0NTNxHGgNqxNb";
const CLIENT_ID = "747819205262-uteoim61ntfqe29s4bs8huqjebn6tqr6.apps.googleusercontent.com";
const CLIENT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNb";
// Change to google oauth
module.exports = (request, cookieValidator, usersData, jwt) => {

    const theApi = {
        'login': function (req, resp) {
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
                + 'redirect_uri=http://localhost:3000/googlecallback');
        },
        'callback': function (req, resp) {

            if (req.query.state !== usersData.getState()) {
                resp.statusCode = 403;
                resp.render('Forbidden');
            } else {
                const options = {
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                };
                const form = xwwwfurlenc({
                    code: req.query.code,
                    client_id: CLIENT_ID,
                    client_secret: CLIENT_SECRET,
                    redirect_uri: 'http://localhost:3000/googlecallback',
                    grant_type: 'authorization_code'
                });
                request.post('https://www.googleapis.com/oauth2/v3/token', form, options)
                    .then(response => {
                        let jwt_payload = jwt.decode(response.data.id_token);
                        let email = jwt_payload.email;
                        let sub = jwt_payload.sub;

                        resp.statusCode = 200;

                        const token = jwt.sign({ email, sub }, JWT_SECRET);
                        resp.cookie('token', token, {
                            expires: new Date(Date.now() + 36000),
                            secure: false,
                            httpOnly: true,
                        });

                        //save data in memory
                        usersData.addUser(email, {
                            id_token: response.data.id_token,
                            google_access_token: response.data.access_token
                        });
                        resp.redirect('/gitindex/' + email);
                    })
                    .catch(err => {
                        resp.statusCode = 400;
                        resp.render('error', { errorCode: resp.statusCode, reason: err });
                    });
            }
        },
        'posttask': async function (req, resp) {
            if (!cookieValidator.ValidateCookie(req)) {
                res.render('Forbidden');
            } else {
                let issue = JSON.parse(req.params[0]);
                let user = usersData.getUser(issue.username);
                let googleAccessToken = user.google_access_token;
                let url = 'https://www.googleapis.com/tasks/v1/users/@me/lists?' + 'key=' + CLIENT_ID;

                //get if or create a new tasklist ang return new id
                let id = await promiseTaskListId(googleAccessToken);
                let task = await insertTask(googleAccessToken, id, issue);

                resp.render('task', { task: task });
            }
        }
    }


    const promiseTaskListId = function (access_token) {
        return request.get(
            'https://www.googleapis.com/tasks/v1/users/@me/lists?' + 'key=' + CLIENT_ID, {
            headers: {
                "Authorization": "Bearer " + access_token,
                accept: 'application/json'
            }
        }).then((response) => {
            console.log(response.data)
            if (response.data.items.length == 0) {
                return createNewTaskList(access_token);
            }
            return response.data.items[0]["id"];
        });
    };
    function createNewTaskList(access_token) {
        //TODO
    }

    const insertTask = async (accessToken, taskListId, issue) => {
        try {
            //console.log(issue);
            const response = await request.post(
                'https://www.googleapis.com/tasks/v1/lists/tasklist/tasks?tasklist=' + taskListId + '&Client_id=' + CLIENT_ID,
                {
                    title: issue.issueTitle,
                    notes: issue.issueBody
                },
                {
                    headers: {
                        Authorization: "Bearer " + accessToken
                    }
                });
            return response.data;
        } catch (e) {
            console.log('error insertin task on google api');
            console.log(e)
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