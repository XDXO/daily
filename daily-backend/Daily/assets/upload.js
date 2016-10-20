var fs = require('fs');
var multer = require('multer');
var path = require('path');
var uuid = require('uuid');

var dir = path.join(__dirname, '..', 'uploads');

if (!fs.existsSync(dir))
    fs.mkdirSync(dir);

var storage = multer.diskStorage({

    destination: function(req, file, callback) {

        callback(null, dir);
    },

    filename: function(req, file, callback) {

        var extension = file.originalname.split('.').pop();
        var filename = uuid.v4().toString() + '-' + Date.now() + "." + extension;

        callback(null, filename);
    }
});

module.exports = multer({ storage : storage });
