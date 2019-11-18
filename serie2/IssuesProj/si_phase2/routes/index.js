var express = require('express');
var router = express.Router();
const usersMap = require("../public/UsersData");


/* GET home page. */
router.get('/', function(req, res, next) {
  usersMap.setState();
  res.render('index', { title: 'Express' });

});

module.exports = router;
