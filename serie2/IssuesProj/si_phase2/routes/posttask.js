var express = require('express');
const request = require("request");
var router = express.Router();
const usersData = require("../public/UsersData");

router.get('/:taskname', function(req, res, next) {
    res.send('user');
});

module.exports = router;