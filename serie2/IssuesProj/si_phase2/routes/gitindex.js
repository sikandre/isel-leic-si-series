var express = require('express');
var router = express.Router();
const usersData = require("../public/UsersData");


router.get('/:username', function(req, res, next) {
    let cookie =  req.cookies['token'];
    if(!cookie) {
        res.render('Forbidden');
    } else if(usersData.validateToken(cookie)){
        var email = req.params.username;
        res.render('gitindex', { email: email });
    } else{
        res.render('Forbidden');
    }
});


module.exports = router;
