function createCustomGraphs(key, input) {
    var txt = "";

    if (key.indexOf("SEAD") == 0) {
        txt = createSeadGraph(key, input);
    }

    // for NABONE, create the pie-chart based on the values
    if (key.indexOf("NABONE") == 0) {
        txt = createNaboneGraph(key, input);
    }
    
    if (txt == "") {
        return "";
    } else {
        txt = "<tr><td></td><td colspan=10>" + txt + "</td></tr>";
    }

    return txt;
}

function createNaboneGraph(key, input) {
    var txt1 = "<div id='radioChart1'></div>";
    var data1 = [];
    for (v in input) {
        if (!input.hasOwnProperty(v)) {
            continue;
        }
        var jd = JSON.parse(v);
        var percLabels = ["Dom %","Whale %","Seal %","Walrus %","Caribou %" , "Other mam %" ,"Bird %" , "Fish %", "Mol Arth Gast %"];
        for (var i=0; i < percLabels.length; i++) {
            var val = parseFloat(jd['perc'][i]);
            if (val > 0) {
                data1.push([percLabels[i],val]);
            }
        }
    }

    var chartData1 = {
        bindto : "#radioChart1",
        data : {
            columns : data1,
            type : 'pie'
        }
    };
    // we need a slight delay here to register the #radioChart div in the DOM
    if (data1.length > 0) {
        txt1 += "<script>c3.generate(" + JSON.stringify(chartData1) + ");</script>";
    }

    
    var txt = "<div id='radioChart'></div>";
    var data = [];
    for (v in input) {
        if (!input.hasOwnProperty(v)) {
            continue;
        }
        var jd = JSON.parse(v);
        var percLabels = ["Bos dom %", "Canis dom %", "Sus dom %",  "Equ dom %",  " Cap dom %",  "Ovi dom %", "Ovca dom %", "Felis dom %"];
        for (var i=0; i < percLabels.length; i++) {
            var val = parseFloat(jd['perc'][i]);
            if (val > 0) {
                data.push([percLabels[i],val]);
            }
        }
    }

    var chartData = {
        bindto : "#radioChart",
        data : {
            columns : data,
            type : 'pie'
        }
    };
    // we need a slight delay here to register the #radioChart div in the DOM
    if (data.length > 0) {
        txt += "<script>c3.generate(" + JSON.stringify(chartData) + ");</script>";
    }
    return txt + txt1;
}


function createSeadGraph(key, input) {
    var txt = "<div id='barChart' style='height:400px'></div>";
    var data = [];
    var sampleLabels = [];
    var grouping = [];
    var categories = ["Aquatics", "Carrion", "Disturbed/arable", "Dung/foul habitats", "Ectoparasite", "General synanthropic", "Halotolerant", "Heathland & moorland", "Indicators: Coniferous", "Indicators: Deciduous", "Indicators: Dung", "Indicators: Standing water", "Meadowland", "Mould beetles", "Open wet habitats", "Pasture/Dung", "Sandy/dry disturbed/arable", "Stored grain pest", "Wetlands/marshes", "Wood and trees"];
    var ignore = [];
    for (v in input) {
        if (!input.hasOwnProperty(v)) {
            continue;
        }
        var jd = JSON.parse(v);
        for (site in jd) {
            if (!jd.hasOwnProperty(site)) {
                continue;
            }
            var samples = jd[site];
            
            for (var s=0;s<samples.length;s++) {
                sampleLabels.push(samples[s][0]);
            }
            for (var c =0 ; c < categories.length; c++) {
                data[c] = [];
                var cat = categories[c];
                // we need to add one to make space for the label at the beginning
                var catSampleOffset = c+1;
                var total = 0.0;
                // test each category to see what actually needs to be displayed
                for (var s =0; s < samples.length; s++) {
                    total += parseFloat(samples[s][catSampleOffset]);
                }
                console.log(cat +" total:" + total);
                // skip category entries that have no values
                if (!(total > 0.0)) {
                    ignore.push(cat);
                    continue;
                }
                // buiild the actual output array per category
                for (var s =0; s < samples.length; s++) {
                    if (s == 0) {
                        data[c][0] = cat;
                    } 
                    data[c][s+1] = samples[s][catSampleOffset];
                }
                
            }
            
        }
    }
    categories = $(categories).not(ignore).get();
    console.log("filtered:", categories);
    var chartData = {
        bindto : "#barChart",
        data : {
            columns : data,
            type : 'bar',
            groups: [categories]
        },
        axis: {
            rotated: true,
            x: {
                type: 'category',
                categories: sampleLabels
            }
        }
    };
    // we need a slight delay here to register the #radioChart div in the DOM
    if (data.length > 0) {
        txt += "<script>c3.generate(" + JSON.stringify(chartData) + ");</script>";
    }
    return txt;
}