var express = require('express');
var multer = require('multer');
var path = require('path');

var model = require('../model/event');
var upload = require('../assets/upload');
var validator = require('../util/validator');

var router = express.Router();

router.get('/', function(req, res, next) {

    try {

        var since = validator.num(req.query.since, 0);

        model.fetch(since)
             .then(function (data) {

                 var result = [];

                 for (var i in data) {

                    var item = model.copy(data[i]);
                    item.synced = true;

                    result.push(item);
                 }

                 res.status(200).json(result);
             })
             .catch(function (e) {

                 res.status(e.status || 500).json({ });
             });
    }
    catch(e) {

        res.status(e.status || 500).json({ });
    }
});

router.get('/:uuid/photo', function(req, res) {

    try {

        var uuid = validator.str(req.params.uuid);

        model.photo(uuid)
             .then(function (data) {

                 res.status(200).sendFile(path.join(__dirname, '..', 'uploads', data.path));
             })
             .catch(function (e) {

                 res.status(e.status || 500).json({ });
             });
    }
    catch(e) {

        res.status(e.status || 500).json({ });
    }
});


router.post('/', upload.single('photo'), function(req, res, next) {

    try {

        console.log(JSON.stringify(req.body));

        var uuid = validator.str(req.body.uuid);

        var title = validator.str(req.body.title);
        var content = validator.str(req.body.content);
        var emotion = validator.num(req.body.emotion, 0);

        var location = validator.str(req.body.location, undefined);
        var latitude = validator.num(req.body.latitude, -1);
        var longitude = validator.num(req.body.longitude, -1);

        var file = req.file;

        model.create(uuid, title, content, emotion, location, latitude, longitude, file)
             .then(function (data) {

                 var item = model.copy(data);

                 item.synced = true;
                 item.path = undefined;

                 res.status(201).json(item);
             })
             .catch(function (e) {

                 res.status(e.status || 500).json({ });
             });
    }
    catch (e) {

        res.status(e.status || 500).json({ });
    }
});

module.exports = router;
