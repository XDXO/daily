var express = require('express');
var logger = require('morgan');
var bodyParser = require('body-parser');

var model = require('./model/event');
var route = require('./routers/event');

var app = express();

app.set('etag', false);

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.use('/events', route);

app.use(function(req, res, next) {

    var err = new Error('Not Found');
    err.status = 404;

    next(err);
});

app.use(function(err, req, res, next) {

    res.status(err.status || 500);
    res.json({ });
});

module.exports = app;
