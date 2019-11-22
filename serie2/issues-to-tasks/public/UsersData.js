var HashMap = require('hashmap');
var jwt = require('jsonwebtoken');
const JWT_SECRET = "vsoGMhmC5vq0NTNxHGgNqxNbvsoGMhmC5vq0NTNxHGgNqxNb";

var map = new HashMap();
var state = '';

module.exports = {
    getUser: function(user) {
        return map.get(user)
    },

    addUser: function(user, data) {
       map.set(user, data);
       //console.log("userfrom data",map.get(user));
    },
    addAccessTokenToUser: function(user, access_token){
        let oldUser = map.get(user);
        //todo validate if exists
        let data = {
            id_token: oldUser.id_token,
            google_access_token: oldUser.google_access_token,
            git_access_token: access_token
        };
        map.set(user, data);
        //console.log("userfrom data",map.get(user))
    },

    setState: function () {
        state = Math.round(new Date().getTime()).toString();
    },

    getState: function () {
        if (state === ''){
            state = Math.round(new Date().getTime()).toString();
        }
        return state;
    },

    validateToken: (cookie) => {
        const decrypt = jwt.verify(cookie, JWT_SECRET);
        let data = map.get(decrypt.email);
        let jwt_payload = jwt.decode(data.id_token);
        let sub = jwt_payload.sub;
        if (decrypt.sub === sub){
            return true;
        }
        return false;
    }
};
