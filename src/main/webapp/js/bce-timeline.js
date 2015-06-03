function createTimeline() {
    var eventSource = new Timeline.DefaultEventSource(0);

    var theme = Timeline.ClassicTheme.create();
    theme.event.bubble.width = 350;
    theme.event.bubble.height = 300;
    var d = Timeline.DateTime.parseGregorianDateTime("1000")
    var bandInfos = [ Timeline.createBandInfo({
        width : "85%",
        intervalUnit : Timeline.DateTime.CENTURY,
        intervalPixels : 200,
        eventSource : eventSource,
        date : d,
        theme : theme,
        layout : 'original' // original, overview, detailed
    }), Timeline.createBandInfo({
        width : "15%",
        intervalUnit : Timeline.DateTime.CENTURY,
        intervalPixels : 70,
        eventSource : eventSource,
        date : d,
        overview : true,
        theme : theme
    }) ];
    bandInfos[1].syncWith = 0;
    bandInfos[1].highlight = true;

    tl = Timeline.create(document.getElementById("tl"), bandInfos, Timeline.HORIZONTAL);
    tl.loadJSON("https://spreadsheets.google.com/feeds/list/1gLTbM6ihwOo3aUkbQ7sA7Qo2ZaufZH9TYFCQ76wR1UI/1/public/values?alt=json", function(data, url) {
        var events = [];
        data.feed.entry.forEach(function(row) {
            if (getGoogleVal("gsx$start", row)) {

                var event = {
                    "start" : getGoogleVal("gsx$start", row),
                    "description" : getGoogleVal("gsx$description", row),
                    "title" : getGoogleVal("gsx$title", row),
                    "link" : ""
                };

                var cat = getGoogleVal("gsx$what", row);
                if (cat == 'Environmental') {
                    event.color = 'green';
                    // event.trackNum = 1;
                }
                if (getGoogleVal("gsx$end", row)) {
                    event.end = getGoogleVal("gsx$end", row);
                }
                events.push(event);
                // console.log("start:", event.start, "end:", event.end, " title:", event.title);
            }
        });

        eventSource.loadJSON({
            'dateTimeFormat' : 'Gregorian',
            'events' : events
        }, "");

    });
    // stop browser caching of data during testing...

}

var resizeTimerID = null;
function onResizeTimeline() {
    if (resizeTimerID == null) {
        resizeTimerID = window.setTimeout(function() {
            resizeTimerID = null;
            tl.layout();
        }, 500);
    }
}

function getGoogleVal(col, row) {
    if (row[col]) {
        var val = row[col]["$t"];
        if (val) {
            if (parseInt(val)) {
                return "" + val;
            }
            return val;
        }
    }
    return "";
}

$(function() {
    createTimeline();
});
