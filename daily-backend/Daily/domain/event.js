var sequelize = require('sequelize');
var orm = require('../assets/orm');

var event = orm.define('event', {

    uuid : { type : sequelize.STRING, primaryKey : true, validate : { isUUID: 4 } },

    title : { type : sequelize.STRING, allowNull : false },
    content : { type : sequelize.STRING, allowNull : false, validate : { len : [1, 200] } },

    emotion : { type : sequelize.INTEGER, allowNull : false, defaultValue : 0, validate : { min : 0, max : 5 } },

    location : { type : sequelize.STRING, allowNull : true },
    latitude : { type : sequelize.DOUBLE, allowNull : true, defaultValue : -1 },
    longitude : { type : sequelize.DOUBLE, allowNull : true, defaultValue : -1 },

    path : { type : sequelize.STRING, allowNull : true }
});

event.sync();

module.exports = event;
