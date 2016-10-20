module.exports = {

    str: function (val, def) {

        if (val === undefined || typeof val !== "string") {

            if (arguments.length == 1) {

                var err = new Error();
                err.status = 412;

                throw err;
            }
            else {

                return def;
            }
        }
        else {

            return val;
        }
    },

    num: function (val, def) {

        val = Number(val);

        if (val === undefined || typeof val !== "number") {

            if (arguments.length == 1) {

                var err = new Error();
                err.status = 412;

                throw err;
            }
            else {

                return def;
            }
        }
        else {

            return val;
        }
    }
};
