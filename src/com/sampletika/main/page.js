var webPage = require('webpage');
var fs = require('fs');
var page1 = webPage.create();
var page2 = webPage.create()
var args = require('system').args;
var file1 = args[1];
var file2 = args[2];

page1.open(file1, function(status) {
	page2.open(file2,function(status1){
		var doc1 = page1.evaluate(function() {
    		return document.body;
 		});
 		var doc2 = page2.evaluate(function(){
 			return document.body;
 		})

// 		var element = doc1.outerHTML.getElementsByAttributeValue("style", "display: none;");
// 		fs.write("/Users/tilakk/Projects/TestCases/samplePhantomjs/temp1.html", doc1.,'w');
 		//fs.write("/Users/tilakk/Projects/TestCases/samplePhantomjs/temp1.html", doc1.outerHTML,'w');
 		//fs.write("/Users/tilakk/Projects/TestCases/samplePhantomjs/temp2.html", doc2.outerHTML,'w');

 		fs.write("/Users/tilakk/Projects/TestCases/samplePhantomjs/temp11.html", doc1.textContent,'w');
 		fs.write("/Users/tilakk/Projects/TestCases/samplePhantomjs/temp22.html", doc2.textContent,'w')
 		//console.log("\nDOC2======>>>> \n" + JSON.stringify(doc2))
 		phantom.exit();
	})
});