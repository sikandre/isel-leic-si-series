'use strict'

const JWT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNbvsoGMhmC5vq0NTNxHGgNqxNb";
const CLIENT_ID = "747819205262-uteoim61ntfqe29s4bs8huqjebn6tqr6.apps.googleusercontent.com";
const CLIENT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNb";

const REDIRECT_URI = 'http://localhost:3000/googlecallback';
const TOKEN_ENDPOINT = 'https://www.googleapis.com/oauth2/v3/token';

// Change to google oauth
module.exports = (request, cookieValidator, usersData, jwt) => {

    const theApi = {
        'loginuri': 'https://accounts.google.com/o/oauth2/v2/auth?'
            // client id
            + 'client_id=' + CLIENT_ID + '&'
            // scope "openid email"
            + 'scope=openid email https://www.googleapis.com/auth/tasks&'
            // parameter state should bind the user's session to a request/response
            + 'state=' + usersData.getState() + '&'
            // responde_type for "authorization code grant"
            + 'response_type=code&'
            // redirect uri used to register RP
            + 'redirect_uri=http://localhost:3000/googlecallback',

        'callback': async function (code) {

            const options = {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            };
            const form = xwwwfurlenc({
                code: code,
                client_id: CLIENT_ID,
                client_secret: CLIENT_SECRET,
                redirect_uri: REDIRECT_URI,
                grant_type: 'authorization_code'
            });
            const response = await request.post(TOKEN_ENDPOINT, form, options);

            let jwt_payload = jwt.decode(response.data.id_token);
            let email = jwt_payload.email;
            let sub = jwt_payload.sub;

            const token = jwt.sign({ email, sub }, JWT_SECRET);

            //save data in memory
            usersData.addUser(email, {
                id_token: response.data.id_token,
                google_access_token: response.data.access_token
            });

            return {
                email: email,
                cookie: {
                    name: 'token',
                    value: token,
                    options: {
                        expires: new Date(Date.now() + 36000),
                        secure: false,
                        httpOnly: true,
                    }
                }
            };
        },
        'storetask': async function (issue) {
            let user = usersData.getUser(issue.username);
            let googleAccessToken = user.google_access_token;
            //let url = 'https://www.googleapis.com/tasks/v1/users/@me/lists?' + 'key=' + CLIENT_ID;

            //get if or create a new tasklist ang return new id
            let id = await promiseTaskListId(googleAccessToken);
            let task = await insertTask(googleAccessToken, id, issue);
            return task;
        }
    }


    const promiseTaskListId = async function (access_token) {
        const body = await request.get(
            'https://www.googleapis.com/tasks/v1/users/@me/lists?' + 'key=' + CLIENT_ID, {
            headers: {
                "Authorization": "Bearer " + access_token,
                accept: 'application/json'
            }
        });
        if (body.data.items.length == 0) {
            return createNewTaskList(access_token);
        }
        return body.data.items[0]["id"];
    }
    function createNewTaskList(access_token) {
        //TODO
    }

    const insertTask = async (accessToken, taskListId, issue) => {
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