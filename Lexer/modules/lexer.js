let LEXER = function(content, dictionary)
{
    let text = content.replace(/\s\s+/gm, " ");
    let strings = text.split(";");

    let lexems = [];

    for(let i=0; i<strings.length; i++)
    {
        let currentString=strings[i].trim();

        if(currentString !== "")
        {
            let words = currentString.split(" ");
            let copyString = currentString;
            let stringObject = {};

            let command = words[0];

            if(dictionary["function"].indexOf(command.toLowerCase()) != -1)
            {
                Object.assign(stringObject, { "function" : command});
            }
            else
            {
                Object.assign(stringObject, { "undefined_function" : command});
            }

            let value = copyString.replace(new RegExp(command + " : ", "g"), "");

            if(/\"(.*)\"/gim.test(value))
            {
                Object.assign(stringObject, { 
                    "value" : 
                    {
                        "type": "string",
                        "value": value
                    }
                });
            }
            else
            {
                if(Number(value))
                {
                    if(Number(value) % 1 ===0)
                    {
                        if(Number(value)>-2147483648 && Number(value) < 2147483647)
                        {
                            Object.assign(stringObject, { 
                                "value" : 
                                {
                                    "type": "integer",
                                    "value": Number(value)
                                }
                            });
                        }
                        else 
                        {
                            if(Number(value)> -9223372036854775808 && Number(value) < 9223372036854775807)
                            {
                                Object.assign(stringObject, { 
                                    "value" : 
                                    {
                                        "type": "integer",
                                        "subtipe": "longint",
                                        "value": Number(value)
                                    }
                                });
                            }
                            else
                            {
                                Object.assign(stringObject, { 
                                    "value" : 
                                    {
                                        "type": "integer",
                                        "subtipe": "infinity",
                                        "value": Number(value)
                                    }
                                });
                            }
                        }
                    }
                    else 
                    {
                        Object.assign(stringObject, { 
                            "value" : 
                            {
                                "type": "float",
                                "value": Number(value)
                            }
                        });
                    }
                }
                else
                {
                    Object.assign(stringObject, { 
                        "value" : 
                        {
                            "type": "undefined",
                            "value": value
                        }
                    });
                }
            }
            lexems.push(stringObject);
        }
    }

    return lexems;
}
module.exports.LEXER=LEXER;