let fs=require("fs");

let dicrionary = require("./modules/dictionary.js").DICTIONARY;
let lexer = require("./modules/lexer.js").LEXER;

fs.readFile("./test/test.lang", "utf-8", function (error, content){

    if(error === null)
    {
        let lexems = lexer(content, dicrionary);

        console.log(JSON.stringify(lexems, null, 4));
    }
    else
    {
        console.error("ERROR!");
        console.error(error);
    }
});