var Query = Java.type('io.jeo.vector.VectorQuery');
var VectorDataset = Java.type('io.jeo.vector.VectorDataset');
var Feature = Java.type('io.jeo.vector.Feature');

var feature = require('./feature.js');
var util = require('../util.js');

function Vector(data) {
  this.data = data;
  this.name = data.name();
  this.driver = data.driver();
}

Vector.wrap = function(data) {
  if (data != null && data instanceof VectorDataset) {
    data = new Vector(data);
  }

  return data;
}

Vector.prototype.schema = function() this.data.schema();
Vector.prototype.bounds = function() this.data.bounds();
Vector.prototype.crs = function() this.data.crs();

Vector.prototype.count = function(q) {
  if (util.isEmpty(q)) {
    q = new Query();
  }
  return this.data.count(q);
}

Vector.prototype.read = function(q) {
  if (util.isEmpty(q)) {
    q = new Query();
  }
  return this.data.read(q);
}

Vector.prototype.add = function(features) {
  if (!Array.isArray(features)) {
    features = [features];
  }

  var cursor = this.data.append(new Query());
  for each (var f in features) {
    if (!(f instanceof Feature)) {
      f = feature(f);
    }

    var next = cursor.next();
    f.map().entrySet().stream().forEach(function(e) next.put(e.key, e.value));
    cursor.write();
  }
  cursor.close();
}

Vector.prototype.toString = function() {
  return 'Dataset(' + this.name + ', ' + this.driver.name() + ')';
}

module.exports = Vector;