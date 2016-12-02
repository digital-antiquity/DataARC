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

function writePieChart(id, pkey, input) {
//    console.log(id);
    var txt1 = "<div id='" + id +"'></div>";
    var data1 = [];
    $.each(input, function(k,v) {
        data1.push([k,v]);
    });

    var chartData1 = {
        bindto : "#" + id,
        data : {
            columns : data1,
            type : 'pie'
        }
    };
    // we need a slight delay here to register the #radioChart div in the DOM
    if (data1.length > 0) {
        txt1 += "<script>c3.generate(" + JSON.stringify(chartData1) + ");</script>";
    }
    return txt1;
}

var counter= 0;
function createNaboneGraph(key, input_) {
    var input;
    $.each(input_, function(k,v) {
        input = JSON.parse(k);
    });
    console.log(input);
    var id = "radioChart" + counter;
    console.log(key, input);
    var pkey = 'perc';
    var txt = writePieChart(id,pkey,input.perc);
    
    var dkey = "domPerc";
    counter++;
    id = "radioChart" + counter;
    txt += writePieChart(id,dkey,input.domPerc);
    counter++;
    return txt;
}


function createSeadGraph(key, input) {
    var txt = "<div id='barChart' style='height:400px'></div>";
    var data = [];
    var sampleLabels = [];
    var grouping = [];
    var categories = ["Aquatics", "Carrion", "Disturbed/arable", "Dung/foul habitats", "Ectoparasite", "General synanthropic", "Halotolerant", "Heathland & moorland", "Indicators: Coniferous", "Indicators: Deciduous", "Indicators: Dung", "Indicators: Standing water", "Meadowland", "Mould beetles", "Open wet habitats", "Pasture/Dung", "Sandy/dry disturbed/arable", "Stored grain pest", "Wetlands/marshes", "Wood and trees"];
    var ignore = [];
    var cats = {};
    for (v in input) {
        if (!input.hasOwnProperty(v)) {
            continue;
        }
        var jd = JSON.parse(v).samples;
        console.log(jd);
        var num =0;
        for (site in jd) {
            if (!jd.hasOwnProperty(site)) {
                continue;
            }
            var samples = jd[site];
            console.log(site);
            console.log(samples);
            for (var i=0; i < samples.length; i++) {
                $.each(samples[i], function(k, vv) {
//                    console.log(k,vv);
                    if (k == 'Sample') {
                        sampleLabels.push(vv);
                    } else {
                        var cat = k;
                        var val = parseFloat(vv);
                        if (cats[cat] == undefined) {
                            cats[cat] = [];
                        }
                        cats[cat].push(val);
                    }
                });
                
            }
            num++;
            for (var c =0 ; c < categories.length; c++) {
                var cat = categories[c];
                var out = [cat];
                out = out.concat(cats[cat]);
                data[c] = out;
            }
        }
    }
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