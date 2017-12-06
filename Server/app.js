var express = require('express');
var path = require('path');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var events = require('./routes/event-route');
var auth = require('./routes/auth-route');
var cors = require("cors");
var mongoose = require("mongoose");
var cfg = require("./config.js");

var app = express();

//Db setup
mongoose.connect(cfg.mongoDBURL, {
  useMongoClient: true
});

//Set promise library
mongoose.Promise = global.Promise;

//Allow cross origin 
app.use(cors());

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: false
}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/events', events);
app.use('/auth', auth);

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // console.log(err);

  // render the error page
  console.log(err);
  res.status(err.status || 500).send(err.message);
});

module.exports = app;