'use strict'

const PORT = process.env.port || '3000'

// external dependencies
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const  jwt = require('jsonwebtoken');
const request = require('axios');
const crypto = require("crypto");

const app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

// middleware
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// setting up dependencies for injection !
const usersData = require('./utils/UsersData')(jwt, crypto);

const cookieValidator = require('./utils/ValidateCookie')(usersData);

const googleService = require('./service/google-oauth-service.js')(request, usersData, jwt);
const githubService = require('./service/github-oauth-service.js')(request, usersData);
const service = require('./service/service.js')(googleService, githubService);
const api = require('./api/issues-to-tasks-api.js')(app, service, usersData, cookieValidator);


app.listen(PORT);

console.log(`app is listening on port ${PORT}`);