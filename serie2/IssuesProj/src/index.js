const express = require('express')
var path = require('path')
const app = express()
var request = require('request');
var cookieParser = require('cookie-parser');
const verifyToken = require('./verifyToken');
var jwt = require('jsonwebtoken');
const port = 3001;
app.use(express.static('public'));

// more info at:
// https://github.com/auth0/node-jsonwebtoken
// https://jwt.io/#libraries

// system variables where RP credentials are stored
//process.env.CLIENT_ID
const CLIENT_ID = "747819205262-uteoim61ntfqe29s4bs8huqjebn6tqr6.apps.googleusercontent.com";
//process.env.CLIENT_SECRET
const CLIENT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNb";
//process.env.JWT_SECRET
const JWT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNbvsoGMhmC5vq0NTNxHGgNqxNb";

let email = "";

let publicDir = path.join(__dirname, 'public');
let state = Math.round(new Date().getTime()).toString();

app.use(cookieParser());

app.get('/', function (req, res) {
    res.sendFile(path.join(publicDir, 'index.html'));
});

const validateToken = (req, resp, next) => {
    let token =  req.cookies['token'];
    if (token){
        const decrypt = jwt.verify(token, JWT_SECRET);
        if (decrypt.email === email && decrypt.CLIENT_ID === CLIENT_ID){
            next();
        }
    }
    resp.status(403);
    resp.sendFile(path.join(publicDir, 'Forbidden.html'));
};

app.get('/auth', validateToken, (req, resp) => {
   //teste para fazer auth com o git


    resp.send(req.cookies['token'])

});


app.get('/login', (req, resp) => {
    resp.redirect(302,
        // authorization endpoint
        'https://accounts.google.com/o/oauth2/v2/auth?'
        // client id
        + 'client_id=' + CLIENT_ID + '&'
        // scope "openid email"
        + 'scope=openid%20email&'
        // parameter state should bind the user's session to a request/response
        + 'state=' + state + '&'
        // responde_type for "authorization code grant"
        + 'response_type=code&'
        // redirect uri used to register RP
        + 'redirect_uri=http://localhost:3001/googlecallback')
});

app.get('/googlecallback', (req, resp) => {
    if (req.query.state !== state) {
        resp.status(403);
        resp.sendFile(path.join(publicDir, 'Forbidden.html'));
    } else {
        //get token
        request.post(
            {
                url: 'https://www.googleapis.com/oauth2/v3/token',
                // body parameters
                form: {
                    code: req.query.code,
                    client_id: CLIENT_ID,
                    client_secret: CLIENT_SECRET,
                    redirect_uri: 'http://localhost:3001/googlecallback',
                    grant_type: 'authorization_code'
                }
            },

            //TODO remover esta area para iniciar leitura dos issues do git
            function (err, httpResponse, body) {
                if (err) {
                    resp.status(400).send({error});
                }
                let json_response = JSON.parse(body);
                let jwt_payload = jwt.decode(json_response.id_token);
                email = jwt_payload.email;

                console.log(jwt_payload);
                resp.statusCode = 200;
                /*
                // compute hmac
                var h = hmac.digest(jwt_payload.at_hash)
                // convert to base64
                var hBase64 = Buffer.from(h).toString('base64');
                //resp.setHeader('jwt', ['jwt_payload', 'T='+hBase64], );*/

                const token = jwt.sign({email, CLIENT_ID}, JWT_SECRET);
                console.log("token created", token)
                resp.cookie('token', token, {
                    expires: new Date(Date.now() + 360000),
                    secure: false, // set to true if your using https
                    httpOnly: true,
                });

                resp.send('<a href="/auth">auth</a>')

            }
        );

    }
});




app.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }
    console.log(`server is listening on ${port}`)
})