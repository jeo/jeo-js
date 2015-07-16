var Geom = Java.type('io.jeo.geom.Geom');

module.exports = {
  fromWKT: function(str) {
    return Geom.parse(str);
  },

  fromJSON: function(obj) {
    if (typeof obj === 'object') {
      obj = JSON.stringify(obj);
    }

    return Geom.parse(obj);
  }
}