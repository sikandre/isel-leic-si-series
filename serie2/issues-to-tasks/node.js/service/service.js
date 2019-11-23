'use strict'

module.exports = (googleoauthservice, githuboauthservice) => {

    const theService = {
        'getGoogleLoginUri' : function () {
            return googleoauthservice.loginuri();
        },
        'googlecallback' : async function (code) {
            return googleoauthservice.callback(code);
        },
        'getGitLoginUri' : function (email) {
            return githuboauthservice.loginuri(email);
        },
        'gitcallback' : async function (email, code, state) {
            return githuboauthservice.callback(email, code, state);
        },
        'getissues' : async function (username) {
            return githuboauthservice.getissues(username);
        },
        'storetask' : async function (issue) {
            return googleoauthservice.storetask(issue);
        }
    }
    return theService;
};