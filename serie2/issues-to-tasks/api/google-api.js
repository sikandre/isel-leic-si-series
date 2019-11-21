'use strict'

const JWT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNbvsoGMhmC5vq0NTNxHGgNqxNb";
const CLIENT_ID = "747819205262-uteoim61ntfqe29s4bs8huqjebn6tqr6.apps.googleusercontent.com";
const CLIENT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNb";
// Change to google oauth
module.exports = (usersData, request, jwt) => {

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
                + 'redirect_uri=http://localhost:3000/googlecallback')
        },
        'callback': function (req, resp) {
            if (req.query.state !== usersData.getState()) {
                resp.statusCode = 403;
                resp.render('Forbidden');
            } else {
                const options = {
                    url: 'https://www.googleapis.com/oauth2/v3/token',
                    form: {
                        code: req.query.code,
                        client_id: CLIENT_ID,
                        client_secret: CLIENT_SECRET,
                        redirect_uri: 'http://localhost:3000/googlecallback',
                        grant_type: 'authorization_code'
                    }
                };
                request.post(options).then(body => {
                    let json_response = JSON.parse(body);
                    let jwt_payload = jwt.decode(json_response.id_token);
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
                        id_token: json_response.id_token,
                        google_access_token: json_response.access_token
                    });
                    resp.redirect('/gitindex/' + email);
                })
                    .catch(err => {
                        resp.statusCode = 400;
                        resp.render('error', { errorCode: resp.statusCode, reason: err });
                    });
            }
        },
    }

    return theApi;

}