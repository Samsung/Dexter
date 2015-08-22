/*
 * Copyright (c) 2012 Miles Shang <mail@mshang.ca>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * Usage:
 * Set your settings:
 *   base64.settings.char62 = "-";
 *   base64.settings.char63 = "_";
 *   etc.
 *
 * Then:
 * base64.encode(str) takes a string and returns the base64 encoding of it.
 * base64.decode(str) does the reverse.
 */

/* TODO:
 * Add a "padding_mandatory" flag to check for bad padding in the decoder.
 * Add test cases that throw errors.
 */

var base64 = new Object();

base64.settings = { // defaults
    "char62"        : "+",
    "char63"        : "/",
    "pad"           : "=",
    "ascii"         : false
};

/*
 * Settings:
 * If "pad" is not null or undefined, then it will be used for encoding.
 *
 * If "ascii" is set to true, then the encoder
 * will assume that plaintext is in 8-bit chars (the standard).
 * In this case, for every 3 chars in plaintext, you get 4 chars of base64.
 * Any non-8-bit chars will cause an error.
 * Otherwise, assume that all plaintext can be in the full range
 * of Javascript chars, i.e. 16 bits. Get 8 chars of base64 for 3 chars
 * of plaintext. Any possible JS string can be encoded.
 */

exports.encode = function(str){
    "use strict";
    return base64.encode(str);
};
exports.decode = function(str){
    "use strict";
    return base64.decode(str);

}

base64.encode = function (str) {
    this.char_set =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        + this.settings.char62 + this.settings.char63;

    var output = ""; // final output
    var buf = ""; // binary buffer
    for (var i = 0; i < str.length; ++i) {
        var c_num = str.charCodeAt(i);
        if (this.settings.ascii)
            if (c_num >= 256)
                throw "Not an 8-bit char.";
        var c_bin = c_num.toString(2);
        while (c_bin.length < (this.settings.ascii ? 8 : 16))
            c_bin = "0" + c_bin;
        buf += c_bin;

        while (buf.length >= 6) {
            var sextet = buf.slice(0, 6);
            buf = buf.slice(6);
            output += this.char_set.charAt(parseInt(sextet, 2));
        }
    }

    if (buf) { // not empty
        while (buf.length < 6) buf += "0";
        output += this.char_set.charAt(parseInt(buf, 2));
    }

    if (this.settings.pad)
        while (output.length % (this.settings.ascii ? 4 : 8) != 0)
            output += this.settings.pad;

    return output;
}

base64.decode = function (str) {
    this.char_set =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        + this.settings.char62 + this.settings.char63;

    var output = ""; // final output
    var buf = ""; // binary buffer
    var bits = (this.settings.ascii ? 8 : 16);
    for (var i = 0; i < str.length; ++i) {
        if (str[i] == this.settings.pad) break;
        var c_num = this.char_set.indexOf(str.charAt(i));
        if (c_num == -1) throw "Not base64.";
        var c_bin = c_num.toString(2);
        while (c_bin.length < 6) c_bin = "0" + c_bin;
        buf += c_bin;

        while (buf.length >= bits) {
            var octet = buf.slice(0, bits);
            buf = buf.slice(bits);
            output += String.fromCharCode(parseInt(octet, 2));
        }
    }
    return output;
}