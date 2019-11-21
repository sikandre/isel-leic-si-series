'use strict'

module.exports = (app, googleApi, githubApi, usersMap) => {

    const theApi = {
        'index' : function (req, resp) {
            usersMap.setState();
            resp.render('index', { title: 'Express' });
        },
        'posttask' : async function (req, resp) {
            let task = await service.task();
            resp.render('task', {task: task});
        },
        'getissues' : async function (req, resp) {
            const issues = await githubApi.getissues();
            res.status(200);
            res.render('listrepos', {issues: issues, username: username});
        },
        'statusCode404' : function (req, resp) {
            resp.statusCode = 404;
            resp.render('error', {errorCode: resp.statusCode, reason : `Sorry! ${req.originalUrl} is not a valid path`});
        },
    }

    app.get('/', theApi.index);
    
    app.get('/login', googleApi.login);
    app.get('/googlecallback', googleApi.callback);

    app.get('/gitindex/:username', githubApi.gitindex);
    app.get('/logingit/:username', githubApi.login);
    app.get('/githubcallback/:username', githubApi.callback);
    //app.get('/getrepos/:username', theApi.getissues)
    //app.get('/posttask', theApi.posttask);

    app.get('*', theApi.statusCode404);

    return theApi;
}

/*
uniformizar a utilização de async await

distinguir melhor o que é api e o que é serviço
apanhar erros de promises

filtrar issues assigned e unassigned de repositorios publicos e privados
*/
