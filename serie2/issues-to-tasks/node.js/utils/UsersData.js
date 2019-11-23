var HashMap = require('hashmap');
const JWT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNbvsoGMhmC5vq0NTNxHGgNqxNb";

var map = new HashMap();
const validStates = [];

module.exports = (jwt, crypto) => {

    const theudata = {
        getUser: function (user) {
            return map.get(user)
        },

        addUser: function (user, data) {
            map.set(user, data);
        },
        addAccessTokenToUser: function (user, access_token) {
            let oldUser = map.get(user);
            //todo validate if exists
            let data = {
                id_token: oldUser.id_token,
                google_access_token: oldUser.google_access_token,
                git_access_token: access_token
            };
            map.set(user, data);
        },

        getNewState: function () {
            const newState = Math.round(new Date().getTime()).toString();
            validStates.push(newState);
            return newState;
        },
        isValidState : function (state) {
            return validStates.includes(state);
        },

        consumeState : function (state) {
            const index = validStates.indexOf(state);
            if (index > -1) {
                validStates.splice(index, 1);
            }
        },

        validateToken: (cookie) => {
            const decrypt = jwt.verify(cookie, JWT_SECRET);
            const data = map.get(decrypt.email);
            if(data === undefined) {
                return false;
            }
            const jwt_payload = jwt.decode(data.id_token);
            const sub = jwt_payload.sub;
            if (decrypt.sub === sub) {
                return true;
            }
            return false;
        },
        getEmailFromCookie : (cookie) => {
            const decrypt = jwt.verify(cookie, JWT_SECRET);
            const data = map.get(decrypt.email);
            if(data === undefined) {
                throw 'cookie malformed'
            }
            const email = jwt.decode(data.id_token).email;
            return email;
        },
        'sha256' : function (data) {
            return crypto.createHash("sha256").update(data, "binary").digest("base64");
        }
    }
    return theudata;
};
