  <script>
  var testing = true;

  function getContextPath() {
    return "";
  }

  var FIELDS = {
    "sampledata": "sampleData",
    "phase": "Phase",
    "indicators.dungfoul_habitats": "Dung/Foul Habitats",
    "location": "Location",
    "sampledata.sample_name": "Sample Name",
    "site": "Site",
    "period_code": "Period Code",
    "source": "Source",
    "nisp": "NISP",
    "islandregion": "Island/Region",
    "terrestrial_mammal": "Terrestrial Mammal",
    "data.p4": "p4",
    "indicators.mould_beetles": "Mould Beetles",
    "indicators.indicators_running_water": "Indicators: Running Water",
    "indicators.stored_grain_pest": "Stored Grain Pest",
    "sampledata.sample_group_id": "Sample Group Id",
    "indicators.wood_and_trees": "Wood and Trees",
    "pig": "Pig",
    "sheep": "Sheep",
    "avian": "Avian",
    "site_easting": "Site Easting",
    "end": "End",
    "cow": "Cow",
    "sheepgoat_to_cow": "Sheep/Goat to Cow",
    "site_northing": "Site Northing",
    "indicators.carrion": "Carrion",
    "indicators.heathland_moorland": "Heathland & Moorland",
    "indicators.open_wet_habitats": "Open Wet Habitats",
    "indicators.wetlandsmarshes": "Wetlands/Marshes",
    "indicators": "Indicators",
    "indicators.dry_dead_wood": "Dry Dead Wood",
    "indicators.indicators_dung": "Indicators: Dung",
    "indicators.aquatics": "Aquatics",
    "indicators.meadowland": "Meadowland",
    "sampledata.end": "End",
    "indicators.halotolerant": "Halotolerant",
    "sampledata.start": "Start",
    "indicators.general_synanthropic": "General Synanthropic",
    "sampledata.age_abbreviation": "Age Abbreviation",
    "sampledata.site_name": "Site Name",
    "id": "Id",
    "indicators.indicators_standing_water": "Indicators: Standing Water",
    "indicators.sandydry_disturbedarable": "Sandy/Dry Disturbed/Arable",
    "indicators.indicators_deciduous": "Indicators: Deciduous",
    "sampledata.age_name": "Age Name",
    "sampledata.dating_type": "Dating Type",
    "indicators.ectoparasite": "Ectoparasite",
    "source": "Source",
    "indicators.pasturedung": "Pasture/Dung",
    "indicators.indicators_coniferous": "Indicators: Coniferous",
    "country": "Country",
    "marine_mammal": "Marine Mammal",
    "start": "Start",
    "pig_to_bovids": "Pig to Bovids",
    "sampledata.site_id": "Site Id",
    "indicators.disturbedarable": "Disturbed/Arable",
    "data.au_group": "Analytic Unit Group",
    "data.col_count": "Col Count",
    "data.dl": "DL",
    "data.autonumber": "Auto Number",
    "source": "source",
    "jardabok_full_text": "Jardabok Full Text",
    "title": "Title",
    "data.occupationperiod": "Occupation Period",
    "period_text": "Period (text)",
    "data.context": "Context",
    "data.m3_m1": "m3 m1",
    "farm_number": "Farm Number",
    "start_date": "Start Date",
    "1861_adjusted_value": "1861 Adjusted Value",
    "valuation": "Valuation",
    "sheepgoat_total_mandibles": "Sheepgoat Total Mandibles",
    "end_date": "End Date",
    "data.id_row_tdar": "Id Row tDAR",
    "data.m3_p2": "m3 p2",
    "reference": "Reference",
    "sheepgoat_mortality_12m": "Sheepgoat Mortality <12m",
    "parish_name": "Parish Name",
    "isleif_farms_id": "Isleif Farms id",
    "shire_name": "Shire Name",
    "source": "Source",
    "data.col_path": "Col Path",
    "data.gb": "Gb",
    "data.burning": "Burning",
    "data.m1": "m1",
    "data.dp4": "Dp4",
    "data.area": "Area",
    "data.btrim": "B Trim",
    "data.phase": "Phase",
    "data.m3": "m3",
    "data": "Data",
    "data.su": "Stratigraphic Unit",
    "data.fusion_state": "Fusion State",
    "data.sd": "SD",
    "data.other_context_1": "Other Context 1",
    "data.context_notes": "Context Notes",
    "data.l": "Data",
    "data.dates": "Data Dates",
    "data.col_date": "Col Date",
    "data.ref_": "Ref",
    "data.gl": "GL",
    "data.other_context_2": "Other Context 2",
    "data.gll": "Gll",
    "data.site": "Site",
    "data.m3_p4": "m3 p4",
    "data.m3b": "m3b",
    "data.m2": "m2",
    "data.fish_b_measurement": "Fish b Measurement",
    "data.m3l": "m3l",
    "data.bd": "Bd",
    "data.bp": "Bp",
    "data.b": "B",
    "data.bfd": "Bfd",
    "data.dm": "Dm",
    "data.fish_a_measurement": "Fish a Measurement",
    "data.bfp": "Bfp",
    "data.side": "Side",
    "data.ref__2": "Ref 2",
    "data.taxon": "Taxon",
    "data.bt": "BT",
    "data.glm": "GLm",
    "data.col_size": "Col Size",
    "end": "End",
    "start": "Start",
    "data": "Data",
    "end": "End",
    "data.l": "L",
    "data.species_taxon": "Species (taxon)",
    "ab_sheepgoat_notes": "ab Sheepgoat Notes",
    "period": "Period",
    "sheepgoat_mortality_1_4yrs": "Sheepgoat Mortality 1-4yrs",
    "sheepgoat_mortality_4yrs": "Sheepgoat Mortality  >4yrs",
    "data.ref_num_2": "Ref Num 2",
    "start": "Start",
    "data.end": "End",
    "data.other_context_1": "Other Context 1",
    "data.end_date": "End Date",
    "data.gnaw_orig": "Gnaw (orig)",
    "data.sex_orig": "Sex (orig)",
    "data.age_orig": "Age (orig)",
    "data.bfd": "Bfd",
    "data.gnaw": "Gnaw",
    "data.burn_orig": "Burn (orig)",
    "data.gl": "GL",
    "data.y": "Y",
    "data.butchery_orig": "Butchery (orig)",
    "data.bone_orig": "Bone (orig)",
    "data.fusion": "Fusion",
    "data.bone": "Bone",
    "data.sd": "SD",
    "data.unit": "Unit",
    "data.au": "Analytic Unit",
    "data.fusion_orig": "Fusion (orig)",
    "data.dp": "Dp",
    "data.species_comm": "Species (comm)",
    "data.context_notes": "Context Notes",
    "data.count": "Count",
    "data.burn": "Burn",
    "data.species_orig": "Species (orig)",
    "data.frag": "Frag",
    "data.p4": "P4",
    "data.date": "Date",
    "data.p3": "P3",
    "data.sex": "Sex",
    "data.m1": "M1",
    "data.postcanine": "Postcanine",
    "data.bt": "BT",
    "data.p2": "P2",
    "data.ref": "Ref#",
    "data.bfp": "BFp",
    "data.row_id": "Row Id",
    "data.start_date": "Start Date",
    "data.end_orig": "End (orig)",
    "data.su": "Stratigraphic Unit",
    "data.m3": "M3",
    "data.m2": "M2",
    "data.site": "Site",
    "data.in_": "In ?",
    "data.x": "X",
    "data.c": "C",
    "data.comments": "Comments",
    "data.region": "Region",
    "data.p1": "P1",
    "data.age": "Age",
    "data.frag_orig": "Frag (orig)",
    "data.bd": "Bd",
    "data.phase": "Phase",
    "data.gb": "GB",
    "data.b": "B",
    "data.bp": "Bp",
    "data.dp4": "dp4",
    "data.butchery": "Butchery",
    "source": "Source",
    "size": "Size",
    "link": "Link",
    "what": "What",
    "end": "End",
    "start": "Start",
    "title": "Title",
    "tags": "Tags",
    "function_of_site": "Function of Site",
    "theme": "Theme",
    "oldest_extant_manuscript_date": "Oldest Extant Manuscript Date",
    "type_of_site": "Type of Site",
    "source": "Source",
    "country": "Country",
    "oldest_extant_manuscript_shelf_number": "Oldest Extant Manuscript Shelf Number",
    "placename": "Placename"
  };
  var SCHEMA = { "nabone": "Nabone", "nabone_svk": "NABOne (Sveigakot)", "orkneyfauna": "Orkney Faunal Database", "iceland_farms": "Iceland Farm History Database", "sagas": "Icelandic Sagas Database", "sead": "SEAD" };

  var geoJsonInputs = [
    { id: "1", name: "iceland.json-1505394469296.json", title: "untitled", url: "/geojson/1" }
  ];
  </script>