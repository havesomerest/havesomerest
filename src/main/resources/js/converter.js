function convertToObject(json) {
  var json = JSON.stringify(json);
  return JSON.parse(json);
}

function convertToJson(object) {
  return JSON.stringify(object);
}