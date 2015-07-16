var window = this;

// setup jasmine
load('jasmine/lib/jasmine-2.3.4/jasmine.js');
load('jasmine/jasmine2-html-stub.js');
load('jasmine/lib/jasmine-2.3.4/boot.js');

// load require
load('../../main/javascript/io/jeo/js/jvm-npm.js');

// load all the spec files
java.nio.file.Files.walk(java.nio.file.Paths.get('.')).forEach(function(p) {
  if (p.toFile().name.endsWith('.spec.js')) {
   load(p.toString());
  }
});

// call onload, triggers the jasmine tests to run
onload();

var specs = jsApiReporter.specs();

// report on results
var passed = specs.filter(function(spec) {
  return spec.status == "passed";
});
var failed = specs.filter(function(spec) {
  return spec.status == "failed";
});

print(passed.length + ' test passed.');
print(failed.length + ' test failed.');

failed.forEach(function(spec) {
  print('Failed test: ' + spec.fullName);
  spec.failedExpectations.forEach(function(failure) {
    failure.stack.split('\n').forEach(function(frame) {
      print('\t' + frame);
    });
  });
  print();
});
