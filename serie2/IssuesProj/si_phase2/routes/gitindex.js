var express = require('express');
var router = express.Router();
const validateCookie = require( "../public/ValidateCookie");

router.get('/:username', function(req, res, next) {
    if (validateCookie.ValidateCookie(req)){
        var email = req.params.username;
        res.render('gitindex', { email: email });
    } else{
        res.render('Forbidden');
    }
});

module.exports = router;
