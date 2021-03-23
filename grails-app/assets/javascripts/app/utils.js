var Util = {
    /**
     * Parses a plain query string String into and object.
     *
     * @param str
     * @return {*}
     */
    getQueryParameters : function(str) {
        return (str || document.location.search)
            .replace(/(^\?)/,'')
            .split("&")
            .map(
                function(n){
                    return n = n.split("="),this[n[0]] = n[1],this
                }
                .bind({})
            )[0];
    },

    /**
     * Copy a text into client clipboard.
     *
     * @param text
     */
    copyToClipboard : function(text){
        var input = document.createElement('textarea');

        document.getElementById("secret-exchange-area").appendChild(input);
        input.value = text;
        input.focus();
        input.select();
        document.execCommand('Copy');
        input.remove();
    },

    /**
     * Formats a json for being displayed into a front end.
     *
     * @param json
     * @returns {string|XML}
     */
    formatPrettyJson : function(json) {
        if (typeof json != 'string') {
            json = JSON.stringify(json, undefined, 2);
        }
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
    },

    plainJsonObject : function(json, concatenatorChar, eofChar){
        concatenatorChar = validator.getValueOrDefault(concatenatorChar, ", ");
        eofChar = validator.getValueOrDefault(concatenatorChar, "");

        var finalString = "";

        $.each(json, function(k, v){
            finalString += k + concatenatorChar + v + eofChar;
        });

        return finalString;
    },

    scapeBacklashes: function (str) {
        return str.replace(/\\/g, "\\\\")
    },

    escapeXml: function(s) {
        return s.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
    }
};

/**
 * Add Replace all to String class.
 *
 * @param search
 * @param replacement
 * @returns {string}
 */
String.prototype.replaceAll = function(search, replacement) {
    var str = this;
    str = str.replace(search, replacement);
    if(str.indexOf(search) >= 0){
        str = str.replaceAll(search, replacement)
    }

    return str;
};

function customReplace(text, search, replace){
    text = text.replace(search, replace);
    if(text.indexOf(search) >= 0){
        text = customReplace(text, search, replace)
    }

    return text;
};


