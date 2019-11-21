const usersData = require("../public/UsersData");

module.exports = {
    /**
     * @return {boolean}
     */
    ValidateCookie: function (req) {
        let cookie = req.cookies['token'];
        if (!cookie) {
            return false;
        } else return !!usersData.validateToken(cookie);
    },
};