module.exports = {
  isEmpty: function(val) {
    return typeof val === 'undefined' || val === null || val === '';
  },

  map: function(obj) {
    var m = new java.util.LinkedHashMap();
    for each (var k in Object.keys(obj)) {
      m.put(k, obj[k]);
    }
    return m;
  }
}