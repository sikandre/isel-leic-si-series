var express = require('express');
var router = express.Router();
const usersData = require("../public/UsersData");


/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('user');
});

module.exports = router;
