#!/bin/bash
# $Id: //open/util/bin/checkFile#10 $
# Checks that a file is valid.
# Used by perforce submit trigger, via runTrigger.
# The file is deemed to be valid if this command produces no output.
#
# Usage:
#   checkFile [ --depotPath <depotPath> ] <file> 
#
# runTrigger uses first form, with a temporary file, e.g.
#   checkFile --depotPath /tmp/foo.txt //depot/src/foo/Bar.java
#
# The second form is useful for checking files in the client before you
# try to submit them:
#   checkFile src/foo/Bar.java
#

usage() {
    echo "checkFile  [ <options> ] --depotPath <depotPath> <file>"
    echo "    Checks a temporary file. depotPath is the full path of"
    echo "    the file stored in perforce, for error reporting; file"
    echo "    holds the actual file contents."
    echo "checkFile  [ <options> ] <file>..."
    echo "    Checks a list of files."
    echo "checkFile  [ <options> ] --opened"
    echo "    Checks all files that are opened for edit in the current"
    echo "    perforce client."
    echo "checkFile --help"
    echo "    Prints this help."
    echo
    echo "Options:"
    echo "--lenient"
    echo "    Does not apply rules to components which are not known to"
    echo "    be in compliance. The perforce trigger uses this option."
}

doCheck() {
    filePath="$1"
    file="$2"

    # CHECKFILE_IGNORE is an environment variable that contains a callback
    # to decide whether to check this file. The command or function should
    # succeed (that is, return 0) if checkFile is to ignore the file, fail
    # (that is, return 1 or other non-zero value) otherwise.
    if [ "$CHECKFILE_IGNORE" ]; then
        if eval $CHECKFILE_IGNORE "$filePath"; then
            return
        fi
    fi

    # Exceptions for mondrian
    case "$filePath" in
    */mondrian/util/Base64.java| \
    */mondrian/olap/MondrianDef.java| \
    */mondrian/gui/MondrianGuiDef.java| \
    */mondrian/xmla/DataSourcesConfig.java| \
    */mondrian/rolap/aggmatcher/DefaultDef.java| \
    */mondrian/resource/MondrianResource.java| \
    */mondrian/olap/Parser.java| \
    */mondrian/olap/ParserSym.java)
        # mondrian.util.Base64 is checked in as is, so don't check it
        # Other files above are generated.
        return
        ;;

    # Exceptions for olap4j
    */org/olap4j/mdx/parser/impl/*.java| \
    */org/olap4j/mdx/parser/impl/*.cup| \
    */org/olap4j/impl/Base64.java)
        return
        ;;

    # Only validate .java and .cup files at present.
    *.java|*.cup|*.h|*.cpp)
        ;;
    *)
        return
        ;;
    esac

    # Check whether there are tabs, or lines end with spaces
    # todo: check for ' ;'
    # todo: check that every file has copyright/license header
    # todo: check that every class has javadoc
    # todo: check that every top-level class has @author and @version
    # todo: check c++ files
    cat "$file" |
    awk '
function error(fname, linum, msg) {
    printf "%s: %d: %s\n", fname, linum, msg;
    if (0) print; # for debug
}
function matchFile(fname) {
    return fname ~ "/mondrian/" \
       || fname ~ "/org/olap4j/" \
       || fname ~ "/aspen/" \
       || fname ~ "/com/sqlstream/" \
       || !lenient;
}
function isCpp(fname) {
#print  "isCpp(" fname  ") = " (fname ~ /\.(cpp|h)$/);
    return fname ~ /\.(cpp|h)$/;
}
function push(val) {
   switchStack[switchStackLen++] = val;
}
function pop() {
   --switchStackLen
   val = switchStack[switchStackLen];
   delete switchStack[switchStackLen];
   return val;
}
BEGIN {
    # pre-compute regexp for single-quoted strings
    apos = sprintf("%c", 39);
    pattern = apos "(\\" apos "|[^" apos "])*" apos;
}
{
    if (previousLineEndedInBrace > 0) {
        --previousLineEndedInBrace;
    }
    s = $0;
    # replace strings
    gsub(/"(\\"|[^"])*"/, "string", s);
    # replace single-quoted strings
    gsub(pattern, "string", s);
    if (inComment && $0 ~ /\*\//) {
        # end of multiline comment "*/"
        inComment = 0;
        gsub(/^.*\*\//, "/* comment */", s);
    } else if (inComment) {
        s = "/* comment */";
    } else if ($0 ~ /\/\*/ && $0 !~ /\/\*.*\*\//) {
        # beginning of multiline comment "/*"
        inComment = 1;
        gsub(/\/\*.*$/, "/* comment */", s);
    } else {
        # mask out /* */ comments
        gsub(/\/\*.*$/, "/* comment */", s);
    }
    # mask out // comments
    gsub(/\/\/.*$/, "// comment", s);
}
/ $/ {
    error(fname, FNR, "Line ends in space");
}
/[\t]/ {
    error(fname, FNR, "Tab character");
}
s ~ /\<if\>.*;$/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else {
        error(fname, FNR, "if followed by statement on same line");
    }
}
s ~ /\<(if|while|for|switch|catch|do|synchronized)\(/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s !~ /(    )+(if|while|for|switch|synchronized|assert|} catch|do)/) {
        error(fname, FNR, "if/while/for/switch/synchronized/catch/do must be correctly indented");
    } else {
        error(fname, FNR, "if/while/for/switch/synchronized/catch/do must be followed by space");
    }
}
s ~ /\<switch\>/ {
    push(switchCol);
    switchCol = index($0, "switch");
}
s ~ /{/ {
    braceCol = index($0, "{");
    if (braceCol == switchCol) {
        push(switchCol);
    }
}
s ~ /}/ {
    braceCol = index($0, "}");
    if (braceCol == switchCol) {
        switchCol = pop();
    }
}
s ~ /\<(case|default)\>/ {
    caseDefaultCol = match($0, /case|default/);
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (caseDefaultCol != switchCol) {
        error(fname, FNR, "case/default must be aligned with switch");
    }
}
s ~ /\<assert\(/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (isCpp(fname)) {} # rule only applies to java
    else if (s !~ /(    )+(assert)/) {
        error(fname, FNR, "assert must be correctly indented");
    } else {
        error(fname, FNR, "assert must be followed by space");
    }
}
s ~ /\<else\>/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (isCpp(fname) && s ~ /^# *else$/) {} # ignore "#else"
    else if (s !~ /(    )+} else (if |{$|{ *\/\/|{ *\/\*)/) {
        error(fname, FNR, "else must be preceded by } and followed by { or if and correctly indented");
    }
}
s ~ /\<try\>/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s !~ /(    )+try {/) {
        error(fname, FNR, "try must be followed by space {, and correctly indented");
    }
}
s ~ /\<catch\>/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s !~ /(    )+} catch /) {
        error(fname, FNR, "catch must be preceded by }, followed by space, and correctly indented");
    }
}
s ~ /\<finally\>/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s !~ /(    )+} finally {/) {
        error(fname, FNR, "finally must be preceded by }, followed by space {, and correctly indented");
    }
}
s ~ /[]A-Za-z0-9()](+|-|\*|\/|%|=|==|+=|-=|\*=|\/=|>|<|>=|<=|!=|&|&&|\||\|\||^|\?|:) *[A-Za-z0-9(]/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s ~ /<.*>/) {} # ignore templates
    else if (s ~ /\(-/) {} # ignore case "foo(-1)"
    else if (s ~ /[eE][+-][0-9]/) {} # ignore e.g. 1e-5
    else if (s ~ /(case.*|default):$/) {} # ignore e.g. "case 5:"
    else if (isCpp(fname) && s ~ /[^ ][*&]/) {} # ignore e.g. "Foo* p;" in c++ - debatable
    else if (isCpp(fname) && s ~ /\<operator.*\(/) {} # ignore e.g. "operator++()" in c++
    else {
        error(fname, FNR, "operator must be preceded by space");
    }
}
s ~ /[]A-Za-z0-9()] *(+|-|\*|\/|%|=|==|+=|-=|\*=|\/=|>|<|>=|<=|!=|&|&&|\||\|\||^|\?|:)[A-Za-z0-9(]/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s ~ /<.*>/) {} # ignore templates
    else if (s ~ /(\(|return |case |= )-/) {} # ignore prefix -
    else if (s ~ /(case.*|default):$/) {} # ignore e.g. "case 5:"
    else if (s ~ /[eE][+-][0-9]/) {} # ignore e.g. 1e-5
    else if (isCpp(fname) && s ~ /[*&][^ ]/) {} # ignore e.g. "Foo *p;" in c++
    else if (isCpp(fname) && s ~ /\<operator[^ ]+\(/) {} # ignore e.g. "operator++()" in c++
    else {
        error(fname, FNR, "operator must be followed by space");
    }
}
s ~ /[[(] / {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else {
        error(fname, FNR, "( or [ must not be preceded by space");
    }
}
s ~ / [])]/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s ~ /^ *\)/ && previousLineEndedInBrace) {} # ignore "bar(new Foo() { } );"
    else {
        error(fname, FNR, ") or ] must not be followed by space");
    }
}
s ~ /}/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s !~ /}( |;|,|$|\))/) {
        error(fname, FNR, "} must be followed by space");
    } else if (s !~ /(    )*}/) {
        error(fname, FNR, "} must be at start of line and correctly indented");
    }
}
s ~ /{/ {
    if (!matchFile(fname)) {} # todo: enable for farrago
    else if (s ~ /(\]\)?|=) *{/) {} # ignore e.g. "(int[]) {1, 2}" or "int[] x = {1, 2}"
    else if (s ~ /\({/) {} # ignore e.g. @SuppressWarnings({"unchecked"})
    else if (s ~ /{ *(\/\/|\/\*)/) {} # ignore e.g. "do { // a comment"
    else if (fname ~ /\.cup$/) {} # ignore .cup file, which has {:
    else if (s ~ / {}$/) {} # ignore e.g. "Constructor() {}"
    else if (s ~ /{}/) { # e.g. "Constructor(){}"
        error(fname, FNR, "{} must be preceded by space and at end of line");
    } else if (isCpp(fname) && s ~ /{ *\\$/) {
        # ignore - "{" can be followed by "\" in c macro
    } else if (s !~ /{$/) {
        error(fname, FNR, "{ must be at end of line");
    } else if (s !~ /(^| ){/) {
        error(fname, FNR, "{ must be preceded by space or at start of line");
    }
}
/./ {
    lastNonEmptyLine = $0;
}
/}$/ {
    previousLineEndedInBrace = 2;
}
{
    next;
}
END {
    # Compute basename. If fname="/foo/bar/baz.txt" then basename="baz.txt".
    basename = fname;
    gsub(".*/", "", basename);
    terminator = "// End " basename;
    if (lastNonEmptyLine != terminator) {
        error(fname, FNR, sprintf("Last line should be %c%s%c", 39, terminator, 39));
    }
}' fname="$filePath" lenient="$lenient"

}

lenient=
if [ "$1" == --lenient ]; then
    lenient=true
    shift
fi

if [ "$1" == --help ]; then
    usage
    exit 0
fi

depotPath=
if [ "$1" == --depotPath ]; then
    depotPath="$2"
    shift 2
fi

opened=
if [ "$1" == --opened ]; then
    opened=true
    shift
fi

if [ "$opened" ]; then
    p4 opened |
    awk -F'#' '{print $1}' |
    while read line; do
        file=$(p4 where "$line" | awk '{print $3}' | tr \\\\ /)
        doCheck "$file" "$file"
    done
else
    for file in "$@"; do
        filePath="$file"
        if [ "$depotPath" ]; then
            filePath="$depotPath"
        fi
        doCheck "$filePath" "$file"
    done
fi

# End checkFile

