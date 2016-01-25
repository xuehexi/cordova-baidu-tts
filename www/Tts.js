
var exec = require('cordova/exec');
var SERVICE_NAME = 'BaiduTts';
var ACTION_SPEAK = 'speak';
var ACTION_INIT  = 'init';
var ACTION_STOP  = 'stop';

var baidutts = module.exports = {};

var idGenerator = 0;


baidutts.init = function(entity) {
    exec(null, null, SERVICE_NAME, ACTION_INIT, [entity]);
};

baidutts.speak = function(success, error, entity) {
    exec(success, error, SERVICE_NAME, ACTION_SPEAK, [entity]);
};

baidutts.stop = function() {
    exec(null, null, SERVICE_NAME, ACTION_STOP, [null]);
};