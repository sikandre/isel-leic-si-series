var express = require('express');
var router = express.Router();
const usersMap = require("../public/UsersData");
const request = require("request");

router.get('/*', function(req, res, next) {
    var username = req.params[0];
    console.log(JSON.parse(username));
    res.send('save');
});

module.exports = router;