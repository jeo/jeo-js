var Drivers = Java.type('io.jeo.data.Drivers');
var util = require('./util.js');
var Vector = require('./data/vector.js');
var Workspace = require('./data/workspace.js');

var geom = require('./geom.js');
var feature = require('./data/feature.js');

function uri(str) {
  return new java.net.URI(str);
}

function wrap(obj) {
  if (!util.isEmpty(obj)) {
    if (obj instanceof Java.type('io.jeo.vector.VectorDataset')) {
      obj = new Vector(obj);
    }
    if (obj instanceof Java.type('io.jeo.data.Workspace')) {
      obj = new Workspace(obj);
    }
  }

  return obj;
}

module.exports = {
  drivers: function() {
    return Drivers.list()
  },

  open: function(opts, driver) {
    var result = null;
    if (!util.isEmpty(driver)) {
      var drv = Drivers.find(driver);
      if (drv == null) {
        throw new Error('Unable to find driver named: ' + driver);
      }

      if (!drv.isEnabled(null)) {
        throw new Error('Driver ' + driver + ' is not enabled');
      }

      var map = opts;
      if (typeof opts === 'string') {
        map = Drivers.parseURI(uri(opts), drv);
      }
      result = drv.open(map);
    }
    else {
      result =
        Drivers.open(typeof opts === 'string' ? uri(opts) : util.map(opts));
    }

    return wrap(result);
  },

  geom: geom,

  feature: feature
};