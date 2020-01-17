'use strict'

module.exports = (app, service, usersData, cookievalidator) => {

    const theApi = {

        'index': function (req, resp) {
            const googleCookie = req.cookies['google-token'];
            const googleValidated = cookievalidator.validateGoogleCookie(googleCookie);
            if (googleValidated) {
                const email = usersData.getEmailFromCookie(googleCookie);
                resp.redirect(302, '/gitindex/' + email);
            } else {
                resp.render('index', { title: 'Issues to Tasks' });
            }
        },
        'login': function (req, resp) {
            resp.redirect(302, service.getGoogleLoginUri());
        },
        'googlecallback': async function (req, resp) {
            const state = req.query.state;
            if (!usersData.isValidState(state)) {
                resp.statusCode = 403;
                resp.render('Forbidden');
            } else {
                try {
                    const code = req.query.code;
                    const state = req.query.state;
                    const result = await service.googlecallback(code);
                    usersData.consumeState(state);
                    resp.statusCode = 200;
                    resp.cookie(result.cookie.name, result.cookie.value, result.cookie.options);
                    resp.redirect('/gitindex/' + result.email);
                } catch (err) {
                    statusCode400(resp, err);
                }
            }
        },
        'gitindex': function (req, resp) {
            const googleCookie = req.cookies['google-token'];
            const googleValidated = cookievalidator.validateGoogleCookie(googleCookie);
            const githubValidated = cookievalidator.validateGithubCookie(req.cookies['github-token'], req.params.username);
            if (googleValidated && githubValidated) {
                const email = usersData.getEmailFromCookie(googleCookie);
                resp.redirect(302, '/getissues/' + email);
            } else if (!googleValidated && githubValidated) {
                resp.render('Forbidden');
            } else if (!googleValidated && !githubValidated) {
                resp.redirect(302, '/');
            } else {
                const email = req.params.username;
                const emailFromCookie = usersData.getEmailFromCookie(googleCookie);
                if (emailFromCookie != email) {
                    resp.render('Forbidden');
                }
                resp.render('gitindex', { email: email });
            }
        },
        'gitlogin': function (req, resp) {
            const email = req.params.username;
            resp.redirect(302, service.getGitLoginUri(email));
        },
        'githubcallback': async function (req, resp) {
            const state = req.query.state;
            if (!usersData.isValidState(state)) {
                resp.status(403);
                resp.render('Forbidden');
            } else {
                try {
                    const email = req.params.username;
                    const code = req.query.code;
                    const result = await service.gitcallback(email, code, state);
                    usersData.consumeState(state);
                    resp.statusCode = 200;
                    resp.cookie(result.cookie.name, result.cookie.value, result.cookie.options);
                    resp.redirect('/getissues/' + email);
                } catch (err) {
                    statusCode400(resp, err);
                }
            }
        },
        'getissues': async function (req, resp) {

            const googleValidated = cookievalidator.validateGoogleCookie(req.cookies['google-token']);
            const githubValidated = cookievalidator.validateGithubCookie(req.cookies['github-token'], req.params.username);
            if (!googleValidated || !githubValidated) {
                resp.render('Forbidden');
            } else {
                const username = req.params.username;
                try {
                    const result = await service.getissues(username);
                    resp.status(200);
                    resp.render('listrepos', { issues: result.issues, username: result.username });
                } catch (err) {
                    statusCode400(resp, err);
                }
            }
        },

        'storetask': async function (req, resp) {
            // TODO: make this route a post with issue on body
            try {
                let issue = JSON.parse(req.params[0]);
                const task = await service.storetask(issue);
                resp.render('task', { task: task });
            } catch (err) {
                statusCode400(resp, err);
            }
        },

        'statusCode404': function (req, resp) {
            resp.statusCode = 404;
            resp.render('error', { errorCode: resp.statusCode, reason: `Sorry! ${req.originalUrl} is not a valid path` });
        },
        'logout': function (req, resp) {
            resp.clearCookie('google-token');
            resp.clearCookie('github-token');
            resp.redirect(302, '/');
        }
    }

    app.get('/', theApi.index);

    app.get('/login', theApi.login);
    app.get('/googlecallback', theApi.googlecallback);

    app.get('/gitindex/:username', theApi.gitindex);
    app.get('/logingit/:username', theApi.gitlogin);
    app.get('/githubcallback/:username', theApi.githubcallback);
    app.get('/getissues/:username', theApi.getissues)
    app.get('/posttask/*', theApi.storetask);

    app.get('/logout', theApi.logout);
    app.get('*', theApi.statusCode404);

    function statusCode400(resp, err) {
        resp.statusCode = 400;
        resp.render('error', { errorCode: resp.statusCode, reason: err });
    }

    return theApi;
}
