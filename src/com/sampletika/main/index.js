var webPage = require('webpage');
var fs = require('fs');
var page = webPage.create();
var args = require('system').args;
var inputFileLoc = args[1];
var outputFileLoc = args[2];

page.open(inputFileLoc,{encoding: "utf-8"}, function(status) {
		var document = page.evaluate(function() {
    		return document.body;
 		});
 		
 		fs.write(outputFileLoc, document.textContent,'w');
// 		console.log("\nDOC2======>>>> \n" + JSON.stringify(doc1))
 		phantom.exit();
});