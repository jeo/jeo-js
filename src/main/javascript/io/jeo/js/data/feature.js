var MapFeature = Java.type('io.jeo.vector.MapFeature');
var util = require('../util.js');

module.exports = function(obj) {
  return new MapFeature(util.map(obj));
}
