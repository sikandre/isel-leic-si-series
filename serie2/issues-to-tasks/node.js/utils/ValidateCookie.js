

module.exports = (usersData) => {

    const theValidator = {
        'ValidateCookie': function (req) {
            let cookie = req.cookies['google-token'];
            if (!cookie) {
                return false;
            } else return !!usersData.validateToken(cookie);
        },
    }
    return theValidator;
};