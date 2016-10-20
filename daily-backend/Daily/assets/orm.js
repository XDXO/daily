var sequelize = require('sequelize');
var config = require('./config');

var database = config.database;
var orm = new sequelize('daily', database.username, database.password, {

    host : database.url,
    dialect : 'mysql'
});

module.exports = orm;
