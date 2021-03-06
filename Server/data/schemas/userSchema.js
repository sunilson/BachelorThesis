var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var eventVariables = require('../../variables/EventVariables');
var userVariables = require('../../variables/UserVariables');
var passwordHash = require("password-hash");
var validate = require('mongoose-validator');
var Event = require('../schemas/eventSchema');

//User event validators
var usernameValidator = [
    validate({
        validator: 'isAlphanumeric',
        message: 'Username contains invalid characters!'
    }),
    validate({
        validator: 'isLength',
        arguments: [userVariables.userName.minLength, userVariables.userName.maxLength],
        message: "Username is too long or too short!"
    })
];

var passwordValidator = [
    validate({
        validator: 'isLength',
        arguments: [userVariables.password.minLength, userVariables.password.maxLength],
        passIfEmpty: true,
        message: "Password is too long or too short!"
    })
];

var emailValidator = [
    validate({
        validator: 'isEmail',
        message: 'Email is not valid!'
    })
];

//This schema defines a single user in the database
var userSchema = new Schema({
    username: {
        type: String,
        required: true,
        validate: usernameValidator,
        index: {
            unique: true
        }
    },
    email: {
        type: String,
        required: true,
        validate: emailValidator,
        index: {
            unique: true
        }
    },
    password: {
        type: String,
        validate: passwordValidator
    },
    activated: {
        type: Boolean
    },
    //The url that is used to import ical events. Can be transformed to array later on for multiple import support
    icalurl: {
        type: String
    },
    //Social or standard login
    type: {
        type: String,
        required: true
    },
    created_at: Date
}, {
    collection: "users"
});

//Executed before data is written to database, but after validation
userSchema.pre('save', function (next) {
    //Set creation date
    if (!this.created_at) {
        this.created_at = new Date();
    }

    //Set activation status
    if (!this.activated) {
        if (this.type !== userVariables.type.standard) {
            this.activated = true;
        } else {
            this.activated = false;
        }
    }

    //Transform username
    this.username = this.username.toLowerCase();

    if (!this.isModified('password')) return next();

    //Hash password
    if (this.password) {
        this.password = passwordHash.generate(this.password);
    }

    next();
});


module.exports = userSchema;