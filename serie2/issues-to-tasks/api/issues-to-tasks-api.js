'use strict'

module.exports = (app, service, usersData, cookievalidator) => {

    const theApi = {

        'index': function (req, resp) {
            resp.render('index', { title: 'Issues to Tasks' });
        },
        'login': function (req, resp) {
            resp.redirect(302, service.getGoogleLoginUri());
        },
        'googlecallback': async function (req, resp) {
            if (req.query.state !== usersData.getState()) {
                resp.statusCode = 403;
                resp.render('Forbidden');
            } else {
                try {
                    const code = req.query.code;
                    const result = await service.googlecallback(code);
                    resp.statusCode = 200;
                    resp.cookie(result.cookie.name, result.cookie.value, result.cookie.options);
                    resp.redirect('/gitindex/' + result.email);
                } catch (err) {
                    statusCode400(resp, err);
                }
            }
        },
        'gitindex': function (req, resp) {
            if (cookievalidator.ValidateCookie(req)) {
                const email = req.params.username;
                resp.render('gitindex', { email: email });
            } else {
                resp.render('Forbidden');
            }
        },
        'gitlogin': function (req, resp) {
            const email = req.params.username;
            resp.redirect(302, service.getGitLoginUri(email));
        },
        'githubcallback': async function (req, resp) {
            if (req.query.state !== usersData.getState()) {
                resp.status(403);
                resp.render('Forbidden');
            } else {
                try {
                    const email = req.params.username;
                    const code = req.query.code;
                    const result = await service.gitcallback(email, code);
                    if (!result) {
                        throw 'post in github token endpoint was not successfull'
                    }
                    resp.redirect('/getissues/' + email);
                } catch (err) {
                    statusCode400(resp, err);
                }
            }
        },
        'getissues': async function (req, resp) {
            if (!cookievalidator.ValidateCookie(req)) {
                res.render('Forbidden');
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
            if (!cookievalidator.ValidateCookie(req)) {
                resp.render('Forbidden');
            } else {
                try {
                    let issue = JSON.parse(req.params[0]);
                    const task = await service.storetask(issue);
                    resp.render('task', { task: task });
                } catch (err) {
                    statusCode400(resp, err);
                }
            }
        },

        'statusCode404': function (req, resp) {
            resp.statusCode = 404;
            resp.render('error', { errorCode: resp.statusCode, reason: `Sorry! ${req.originalUrl} is not a valid path` });
        },
    }

    app.get('/', theApi.index);

    app.get('/login', theApi.login);
    app.get('/googlecallback', theApi.googlecallback);

    app.get('/gitindex/:username', theApi.gitindex);
    app.get('/logingit/:username', theApi.gitlogin);
    app.get('/githubcallback/:username', theApi.githubcallback);
    app.get('/getissues/:username', theApi.getissues)
    app.get('/posttask/*', theApi.storetask);
    app.get('*', theApi.statusCode404);

    function statusCode400(resp, err) {
        resp.statusCode = 400;
        resp.render('error', { errorCode: resp.statusCode, reason: err });
    }

    return theApi;
}

/*
uniformizar a utilização de async await

distinguir melhor o que é api e o que é serviço
apanhar erros de promises

filtrar issues assigned e unassigned de repositorios publicos e privados
*/
