var util = require('../util.js');
var Vector = require('./vector.js');

function Workspace(data) {
  this.data = data;
}

Workspace.prototype.list = function() {
  var l = [];
  this.data.list().forEach(function(h) l.push(h.name()));
  return l;
}

Workspace.prototype.get = function(name) Vector.wrap(this.data.get(name));

Workspace.prototype.create = function(name, schema) {
  var Schema = Java.type('io.jeo.vector.Schema');
  var sb = Schema.build(name);

  var spec = [];
  for each (var key in Object.keys(schema)) {
    spec.push(key + ':' + schema[key]);
  }

  var ds = this.data.create(sb.fields(spec.join(', ')).schema());
  return Vector.wrap(ds);
}

Workspace.prototype.destroy = function(name) this.data.destroy('destroy');

Workspace.prototype.dispose = function() this.data.dispose();

Workspace.prototype.toString = function() {
  return this.data.driver().name() + '(' + this.data.driverOptions() + ')';
}

module.exports = Workspace;