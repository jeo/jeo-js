describe('geom', function() {

  var geom;

  beforeEach(function() {
    geom = require('jeo').geom;
  });

  it('should parse WKT', function() {
    var p = geom.fromWKT('POINT(1 2)');
    expect(p.x).toBe(1);
    expect(p.y).toBe(2);
  });

  it('should parse GeoJSON', function() {
    var p = geom.fromJSON(JSON.stringify({
      type: 'Point',
      coordinates: [1,2]
    }));
    expect(p.x).toBe(1);
    expect(p.y).toBe(2);
  })
});
