

module.exports = (usersData) => {

    const theValidator = {
        'validateGoogleCookie': function (cookie) {
            if (!cookie) {
                return false;
            } else return !!usersData.validateToken(cookie);
        },
        'validateGithubCookie' : function (cookie, user) {
            if(!cookie){
                return false;
            }
            const udata = usersData.getUser(user);
            if(!udata){
                return false;
            }
            const hash = usersData.sha256(udata.git_access_token);
            return hash == cookie;
        }
    }

    return theValidator;
};