var event = require('../domain/event');

module.exports = {

    create: function (uuid, title, content, emotion, location, latitude, longitude, file) {

        var e = {};

        e.uuid = uuid;

        e.title = title;
        e.content = content;
        e.emotion = emotion;

        if (location !== undefined && latitude !== -1 && longitude !== -1) {

            e.location = location;

            e.latitude = latitude;
            e.longitude = longitude;
        }

        if (file !== undefined) {

            e.path = file.filename;
        }

        return event.create(e);
    },

    fetch: function (since) {

        since = new Date(since);

        return event.findAll({

            where : { createdAt : { $gte : since } },
            order : 'createdAt DESC'
        });
    },

    photo: function (uuid) {

        return event.findOne({

            where : { uuid : uuid }
        });
    },

    copy: function (obj) {

        var str = JSON.stringify(obj);
        return JSON.parse(str);
    }
};
