//npm i pug
// npm i -D browser-sync


const express = require('express')
var path = require('path')
const app = express()
var request = require('request');
var cookieParser = require('cookie-parser');
var jwt = require('jsonwebtoken');
var HashMap = require('hashmap');



app.set("view engine", "pug");

const port = 3001;

app.set('views', path.join(__dirname, 'views'));
app.use(express.static(path.join(__dirname, 'public')));

app.set('view engine', 'pug');


let publicDir = path.join(__dirname, 'public');


var map = new HashMap();

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

//github credentials
const GIT_CLIENT_ID = "8440aaee2cd8100b1136";
const GIT_CLIENT_SECRET = "e45df0fe9b0c45e85992dee198aff1ab54a94c20";


let state = Math.round(new Date().getTime()).toString();

app.use(cookieParser());

app.get("/", (req, res) => {
    res.render("index", { title: "Home" });
});

/*
app.get('/', function (req, res) {


    res.sendFile(path.join(publicDir, 'indexIsel.html'));
});

 */

const validateToken = (req, resp, next) => {
    let cookie =  req.cookies['token'];
    if (cookie){
        const decrypt = jwt.verify(cookie, JWT_SECRET);
        let json_response = map.get(decrypt.email);
        let jwt_payload = jwt.decode(json_response.id_token);
        let sub = jwt_payload.sub;
        if (decrypt.sub === sub){
            resp.status(200);
            next();
        }
    }
    resp.status(403);
    resp.sendFile(path.join(publicDir, 'Forbidden.html'));


};

app.get('/auth', validateToken, (req, resp) => {
   //teste para fazer auth com o git



    resp.send('done');

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

            function (err, httpResponse, body) {
                if (err) {
                    resp.status(400).send({error});
                }
                let json_response = JSON.parse(body);
                let jwt_payload = jwt.decode(json_response.id_token);
                let email = jwt_payload.email;
                let sub = jwt_payload.sub;

                resp.statusCode = 200;

                const token = jwt.sign({email, sub}, JWT_SECRET);
                resp.cookie('token', token, {
                    expires: new Date(Date.now() + 360000),
                    secure: false, // set to true if your using https
                    httpOnly: true,
                });
                //save data in memory
                if (map.get(email)) {
                    map.delete(email);
                }
                map.set(email, json_response);
                //resp.sendFile(path.join(publicDir, 'GithubLogin.html'));
                resp.redirect('/gitindex');

            }
        );

    }
});

app.get('/gitindex', validateToken, (req, resp, next) => {
    console.log("email",next);
    //resp.sendFile(path.join(publicDir, 'GithubLogin.html'));
    resp.render("gitindex", { title: "Home" });


    //resp.send('done');

});
app.get('/logingit', validateToken, (req, resp, next) => {
    resp.redirect(302,
        // authorization endpoint
        'https://github.com/login/oauth/authorize?'
        // client id
        + 'client_id=' + GIT_CLIENT_ID + '&'
        // scope "openid email"
        + 'scope=openid%20email&'
        // parameter state should bind the user's session to a request/response
        + 'state=' + state + '&'
        // responde_type for "authorization code grant"
        // +'accept=json&'
        // redirect uri used to register RP
        + 'redirect_uri=http://localhost:3001/githubcallback');
});

app.get('/githubcallback', (req, resp, next) => {
    if (req.query.state !== state) {
        resp.status(403);
        resp.sendFile(path.join(publicDir, 'Forbidden.html'));
    } else {
        //get token

        resp.set('Content-Type', 'application/json');
        request.post(
            {
                url: 'https://github.com/login/oauth/access_token',
                // body parameters
                form: {
                    code: req.query.code,
                    client_id: GIT_CLIENT_ID,
                    client_secret: GIT_CLIENT_SECRET,
                    redirect_uri: 'http://localhost:3001/githubcallback',
                    state: state,
                }
            },

            function (err, httpResponse, body) {
                if (err) {
                    resp.status(400).send({error});
                }
                //let json_response = JSON.parse(body);
                console.log("aki",body);
                //console.log("http", httpResponse);
                /*

                let jwt_payload = jwt.decode(json_response.id_token);
                let email = jwt_payload.email;
                let sub = jwt_payload.sub;

                resp.statusCode = 200;

                const token = jwt.sign({email, sub}, JWT_SECRET);
                resp.cookie('token', token, {
                    expires: new Date(Date.now() + 360000),
                    secure: false, // set to true if your using https
                    httpOnly: true,
                });
                //save data in memory
                if (map.get(email)) {
                    map.delete(email);
                }
                map.set(email, json_response);
                //resp.sendFile(path.join(publicDir, 'GithubLogin.html'));
                resp.redirect('/getRepos');
                */
                 resp.send('done');

            }
        );

    }
});


app.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }
    console.log(`server is listening on ${port}`)
});